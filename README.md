# csci8530
UNOmaha CSCI 8530 - Advanced Operating Systems
Music Visualization on an 8x8 LED grid with Raspberry Pi

The grid has 16 pins, which correspond to the 8 rows and 8 columns of the grid.
The numbering for pins : row/col : Pi GPIO is as follows:
| Pin | Row/Column | GPIO |
|---|---|---|
| 9 | R1 | 21 |
| 14 | R2 | 16 |
| 8 | R3 | 6 |
| 12 | R4 | 20 |
| 1 | R5 | 17 |
| 7 | R6 | 5 |
| 2 | R7 | 27 |
| 5 | R8 | 22 |
| 13 | C1 | 24 |
| 3 | C2 | 19 |
| 4 | C3 | 13 |
| 10 | C4 | 12 |
| 6 | C5 | 26 |
| 11 | C6 | 25 |
| 15 | C7 | 23 |
| 16 | C8 | 18 |

Additionally, two buttons are wired to pins 14 and 15, and their intended functions are as follows:
|Button|Pin|
|---|---|
|Play/Pause|14|
|Next|15|