/* 
 * File:   TCPGPIOServer.h
 * Author: Daniel Lovegrove
 *
 * Version: October 29, 2017
 */

#ifndef TCPGPIOSERVER_H
#define	TCPGPIOSERVER_H

// Defines which port the server will listen on
#define TCP_GPIO_SERVER_PORT    7777
#define TERMINATING_BYTE 0xFF
#define TERMINATING_BYTE_RECEIVED 0xEF
#define MAX_RESPONSE_LENGTH 20
#define MAX_CMD_LENGTH 18

typedef enum _myState {
    SM_OPEN_SERVER_SOCKET = 0,
    SM_LISTEN_FOR_CLIENT_CONNECTION,
    SM_DISPLAY_MENU,
    SM_FIND_COMMAND,
    SM_PROCESS_COMMAND,
    SM_DISCONNECT_CLIENT
} state;

typedef enum _parsedCommand {
    INVALID = 0,
    NULL_COMMAND,
    LED1,
    LED2,
    LED3,
    LED4,
    NOT_LED1,
    NOT_LED2,
    NOT_LED3,
    NOT_LED4,
    BTN1,
    BTN2,
    BTN3,
    TEMP
} parsedCommand;

parsedCommand findCommand (BYTE* u);
BOOL executeCommand(TCP_SOCKET socket, parsedCommand cmd);
void tcpSendMessageWithProtocol(TCP_SOCKET s, char* msg);
void tcpSendDisconnectAcknowledge(TCP_SOCKET s);
void getTemperatureReading(char* str, UINT32 size);
BOOL tcpReadCommandWithProtocol(TCP_SOCKET s, BYTE* command, UINT32 numBytes,
        UINT32 start);

#endif	/* TCPGPIOSERVER_H */

