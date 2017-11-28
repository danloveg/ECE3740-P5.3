/* 
 * File:   I2C.h
 * Author: Daniel Lovegrove
 *
 * Version: November 7, 2017
 */

#ifndef STACK_USE_I2C_BUS
#define STACK_USE_I2C_BUS

#include "HWP PIC32_ETH_SK_ETH795.h"

// Clock Constant
#define I2C_CLOCK_FREQ              5000

// EEPROM Constants
#define TMP2_I2C_BUS	            I2C1
#define TMP2_ADDRESS                0x4B        // 0b1010000 TMP2 address
#define CONFIG_REG					0x03		// Configuration register address
#define TEMP_REG_MSB				0x00		// MSB temperature register address
#define TMP2_DEV_ID_REG_ADDR		0x0B		// device ID (Register Address 0x0B)
#define TMP2_DEV_ID					0xCB
#define TMP2_ADDR_WRITE				0b10010110
#define TMP2_ADDR_READ 				0b10010111
#define	USING_START_METHOD			FALSE
#define	USING_RESTART_METHOD 		TRUE
#define ACK							TRUE
#define NACK						FALSE
#define TMP2_RESOLUTION				0.0078

// Initialize I2C bus
void I2CInit(void);

// Get temp from sensor
float GetTemp();

#endif
