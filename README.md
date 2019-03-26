# UNOmaha CSCI 8530
## Advanced Operating Systems Term Project - Henry McNeil
### Music Visualization on an 8x8 LED grid with Raspberry Pi

The grid has 16 pins, which correspond to the 8 rows and 8 columns of the grid.


The numbering for pins : row/col : BCM : wiringPi is as follows:

| Pin | Row/Column | BCM | wPi |
|---|---|---|---|
| 9 | R1 | 21 | 29 |
| 14 | R2 | 16 | 27 |
| 8 | R3 | 6 | 22 |
| 12 | R4 | 20 | 28 |
| 1 | R5 | 17 | 0 |
| 7 | R6 | 5 | 21 |
| 2 | R7 | 27 | 2 |
| 5 | R8 | 22 | 3 |
| 13 | C1 | 24 | 5 |
| 3 | C2 | 19 | 24 |
| 4 | C3 | 13 | 23 |
| 10 | C4 | 12 | 26 |
| 6 | C5 | 26 | 25 |
| 11 | C6 | 25 | 6 |
| 15 | C7 | 23 | 4 |
| 16 | C8 | 18 | 1 |

Note that the GPIO pin numbers on the RPI documentation and the pin numbers used in WiringPi are different. Why? Because nothing is ever easy.

Additionally, two buttons are wired to pins 14 and 15, and their intended functions are as follows:

|Button|Pin|wPi|
|---|---|
|Play/Pause|14|15|
|Next|15|16|
