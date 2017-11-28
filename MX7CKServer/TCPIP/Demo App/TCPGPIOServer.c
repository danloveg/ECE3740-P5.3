/*
 * File:   TCPGPIOServer.c
 * Author: Daniel Lovegrove
 *
 * Summary:
 *      Implements a TCP/IP server that will turn the onboard LEDs on/off of the
 *      MX7CK board, or give the state of the push buttons depending on what the
 *      user requests.
 *
 * Version: November 3, 2017
 */

#define _SUPPRESS_PLIB_WARNING

#include "TCPIPConfig.h"

#if defined(STACK_USE_TCP_GPIO_SERVER)

#include "TCPIP Stack/TCPIP.h"
#include <ctype.h>
#include <string.h>
#include <assert.h>
#include <stdio.h>
#include "PortConfig.h"
#include "TCPGPIOServer.h"
#include "I2C.h"

// State variables
state myState = SM_OPEN_SERVER_SOCKET;
parsedCommand parsedCmd = INVALID;
UINT32 startingIndex = 0;


/*****************************************************************************
  Function:
        void TCPGPIOServer(void)

  Summary:
        Implements a simple TCP Server, which accepts a set of commands from the
        user to turn LEDs on the board on/off, and monitor push buttons.
  
  Description:
        Implements a simple TCP Server, which accepts a set of commands from the
        user to turn LEDs on the board on/off, and monitor push buttons.

  Precondition:
        TCP is initialized.

  Parameters:
        None

  Returns:
        None
 ***************************************************************************/
void TCPGPIOServer(void) {
    static TCP_SOCKET mySocket;
    WORD numBytes = 0;
    BYTE userCmd[MAX_CMD_LENGTH];

    switch (myState) {
        // -----------------------------------------------------------------
        // STATE: Open Server Socket
        // -----------------------------------------------------------------
        case SM_OPEN_SERVER_SOCKET:
            mySocket = TCPOpen(0, TCP_OPEN_SERVER, TCP_GPIO_SERVER_PORT,
                    TCP_PURPOSE_TCP_GPIO_SERVER);
            if (mySocket == INVALID_SOCKET) {
                return;
            }
            myState = SM_LISTEN_FOR_CLIENT_CONNECTION;
            break;

        // -----------------------------------------------------------------
        // STATE: Listen for client connections
        // -----------------------------------------------------------------
        case SM_LISTEN_FOR_CLIENT_CONNECTION:
            if (TCPIsConnected(mySocket) == TRUE) {
                myState = SM_DISPLAY_MENU;
            }
            break;

        // -----------------------------------------------------------------
        // STATE: Display client menu
        // -----------------------------------------------------------------
        case SM_DISPLAY_MENU:
            // If the user has disconnected, somehow, close the connection
            if (TCPIsConnected(mySocket) == FALSE) {
                myState = SM_DISCONNECT_CLIENT;
                return;
            }

            // Send connected message
            tcpSendMessageWithProtocol(mySocket, "Hello.");
            myState = SM_FIND_COMMAND;
            break;

        // -----------------------------------------------------------------
        // STATE: Find the correct command that the user has entered
        // -----------------------------------------------------------------
        case SM_FIND_COMMAND:
            // If the user has disconnected, somehow, close the connection
            if (TCPIsConnected(mySocket) == FALSE) {
                myState = SM_DISCONNECT_CLIENT;
                return;
            }

            // Get the number of bytes in the 'GET' queue
            numBytes = TCPIsGetReady(mySocket);

            // Otherwise, get the user's command
            if (numBytes > 0) {
                // Read from the socket
                BOOL commandComplete = tcpReadCommandWithProtocol(
                        mySocket, userCmd, numBytes - 2, startingIndex);

                if (commandComplete == FALSE) {
                    // If command is incomplete, continue building it starting
                    // at the ending index next time.
                    startingIndex = numBytes - 2;
                } else {
                    // If command is complete, process it
                    if (numBytes == 4 && (userCmd[0] == 'q' || userCmd[0] == 'd')) {
                        // User wants to disconnect or quit...
                        myState = SM_DISCONNECT_CLIENT;
                    } else {
                        // User entered a command, find it to process
                        parsedCmd = findCommand((BYTE*)&userCmd);
                        myState = SM_PROCESS_COMMAND;
                    }
                    // Reset starting index
                    startingIndex = 0;
                }
            }
            break;

        // -----------------------------------------------------------------
        // STATE: Execute the user's command
        // -----------------------------------------------------------------
        case SM_PROCESS_COMMAND:
            {
                // If the user has disconnected, somehow, close the connection
                if (TCPIsConnected(mySocket) == FALSE) {
                    myState = SM_DISCONNECT_CLIENT;
                    return;
                }

                // Determine if command executed
                BOOL commandExecuted = executeCommand(mySocket, parsedCmd);

                if (commandExecuted == TRUE) {
                    myState = SM_FIND_COMMAND;
                }
            }
            break;

        // -----------------------------------------------------------------
        // STATE: Disconnect the client
        // -----------------------------------------------------------------
        case SM_DISCONNECT_CLIENT:
            // Disconnect acknowledge
            tcpSendDisconnectAcknowledge(mySocket);
            // Discard socket
            TCPDiscard(mySocket);
            // Disconnect socket
            TCPDisconnect(mySocket);
            // And start listening for other connections
            myState = SM_LISTEN_FOR_CLIENT_CONNECTION;
            break;
    }
}


/**
 * Find the correct command from the user's string.
 * @param unparsedCommand - The raw string from the user
 * @return The parsed command of type parsedCommand
 */
parsedCommand findCommand(BYTE* unparsedCommand) {
    char *usersCommand = (char*) unparsedCommand;
    parsedCommand cmd;
     
    if (strcmp(usersCommand, "L1on") == 0) { cmd = LED1; }
    else if (strcmp(usersCommand, "L2on") == 0) { cmd = LED2; }
    else if (strcmp(usersCommand, "L3on") == 0) { cmd = LED3; }
    else if (strcmp(usersCommand, "L4on") == 0) { cmd = LED4; }
    else if (strcmp(usersCommand, "L1off") == 0) { cmd = NOT_LED1; }
    else if (strcmp(usersCommand, "L2off") == 0) { cmd = NOT_LED2; }
    else if (strcmp(usersCommand, "L3off") == 0) { cmd = NOT_LED3; }
    else if (strcmp(usersCommand, "L4off") == 0) { cmd = NOT_LED4; }
    else if (strcmp(usersCommand, "gpb1") == 0) { cmd = BTN1; }
    else if (strcmp(usersCommand, "gpb2") == 0) { cmd = BTN2; }
    else if (strcmp(usersCommand, "gpb3") == 0) { cmd = BTN3; }
    else if (strcmp(usersCommand, "temp") == 0) { cmd = TEMP; }
    else { cmd = INVALID; }
    
    return cmd;
}


/**
 * Execute a parsed command.
 * @param socket - The socket the client is connected to
 * @param cmd - The command that the client sent
 * @return TRUE if command was executed, FALSE if not.
 */
BOOL executeCommand(TCP_SOCKET socket, parsedCommand cmd) {
    BOOL executed = FALSE;

    // If the socket is not ready to put, return
    if (TCPIsPutReady(socket) >= MAX_RESPONSE_LENGTH) {
        switch (cmd) {
            case LED1:
                ledOn(1, TRUE);
                tcpSendMessageWithProtocol(socket, "LED1 is ON");
                break;
            case NOT_LED1:
                ledOn(1, FALSE);
                tcpSendMessageWithProtocol(socket, "LED1 is OFF");
                break;
            case LED2:
                ledOn(2, TRUE);
                tcpSendMessageWithProtocol(socket, "LED2 is ON");
                break;
            case NOT_LED2:
                ledOn(2, FALSE);
                tcpSendMessageWithProtocol(socket, "LED2 is OFF");
                break;
            case LED3:
                ledOn(3, TRUE);
                tcpSendMessageWithProtocol(socket, "LED3 is ON");
                break;
            case NOT_LED3:
                ledOn(3, FALSE);
                tcpSendMessageWithProtocol(socket, "LED3 is OFF");
                break;
            case LED4:
                ledOn(4, TRUE);
                tcpSendMessageWithProtocol(socket, "LED4 is ON");
                break;
            case NOT_LED4:
                ledOn(4, FALSE);
                tcpSendMessageWithProtocol(socket, "LED4 is OFF");
                break;
            case BTN1:
                if (buttonPressed(1) == TRUE) {
                    tcpSendMessageWithProtocol(socket, "BTN1 is pressed");
                } else {
                    tcpSendMessageWithProtocol(socket, "BTN1 is NOT pressed");
                }
                break;
            case BTN2:
                if (buttonPressed(2) == TRUE) {
                    tcpSendMessageWithProtocol(socket, "BTN2 is pressed");
                } else {
                    tcpSendMessageWithProtocol(socket, "BTN2 is NOT pressed");
                }
                break;
            case BTN3:
                if (buttonPressed(3) == TRUE) {
                    tcpSendMessageWithProtocol(socket, "BTN3 is pressed");
                } else {
                    tcpSendMessageWithProtocol(socket, "BTN3 is NOT pressed");
                }
                break;
            case TEMP:
                {
                    // Create an empty string and put the temp reading into it
                    char a[20];
                    char* tempReading = &a[0];
                    getTemperatureReading(tempReading, 20);

                    // Send the temperature to the client
                    tcpSendMessageWithProtocol(socket, tempReading);
                }
                break;
            default:
                tcpSendMessageWithProtocol(socket, "Invalid Command");
                break;
        }

        executed = TRUE;
    }

    return executed;
}


/**
 * Send a message to the client using the communications protocol. The protocol
 * is to send a single terminating byte.
 * @param socket - The socket over which to send the message
 * @param message - The message to send to the client
 */
void tcpSendMessageWithProtocol(TCP_SOCKET socket, char* message) {
    TCPPutArray(socket, (BYTE*) message, strlen(message));
    TCPPut(socket, (BYTE) TERMINATING_BYTE);
    TCPFlush(socket);   
}


/**
 * Get a temperature reading from the sensor in degrees Celsius.
 * @param string The string that will hold the message from the temp
 */
void getTemperatureReading(char* string, UINT32 size) {
    float temp = GetTemp();
    snprintf(string, size, "Temp: %3.2f", temp);
}


/**
 * Send a disconnect acknowledge message to the client.
 * @param socket - The socket that the client is disconnecting from
 */
void tcpSendDisconnectAcknowledge(TCP_SOCKET socket) {
    tcpSendMessageWithProtocol(socket, "Disconnect acknowledged.");
}


/**
 * Read an array of bytes from the socket, and terminate it to make it a string.
 * @param socket - The socket to be read from
 * @param command - The command variable where the bytes will be placed
 * @param numBytes - The number of bytes to read. The rest of the bytes in the
 *     FIFO will be discarded.
 * @return TRUE if terminating byte was found, FALSE if not
 */
BOOL tcpReadCommandWithProtocol(TCP_SOCKET socket, BYTE* command,
        UINT32 numBytes, UINT32 startPos) {

    BOOL commandComplete = FALSE;

    // The starting index plus the number of bytes must be no greater than the
    // size of the command buffer.
    if ((startPos + numBytes) <= MAX_CMD_LENGTH) {

        BYTE a = '\0';
        BYTE *currentChar = &a;
        int i;

        // Get numBytes from the socket, and append them to the command
        for (i = 0; i < numBytes; i++) {
            TCPGet(socket, currentChar);

            if (currentChar[0] != TERMINATING_BYTE_RECEIVED) {
                // If the byte isn't the termination byte, append it to the command
                command[startPos + i] = currentChar[0];
            } else {
                // If the byte is the termination byte, add a null terminator to
                // make the command a string.
                command[startPos + i] = '\0';
                commandComplete = TRUE;
            }
        }

        TCPDiscard(socket);
    }

    return commandComplete;
}

#endif //#if defined(STACK_USE_TCP_TO_UPPER_SERVER)
