# UNOmaha CSCI 8530
## Advanced Operating Systems Term Project - Henry McNeil
### Music Visualization on an 8x8 LED grid with Raspberry Pi

NOTE: Audio output may not work without following this guide: https://nealvs.wordpress.com/2017/08/11/java-sound-on-a-raspberry-pi-with-openjdk/

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
|---|---|---|
|Play/Pause|14|15|
|Next|15|16|

## Lessons learned along the way
1. Resistors are important. Messed up a whole row of the LED grid this way.
2. Be careful of the wiring. My buttons weren't working because I had them going to ground with the pull down resistor, so obviously nothing was happening.
3. Pin numbering is different between WiringPi and the Pi's pinout diagram.
4. When pulsing a lot of pins at once things get weird. This seems to be related to some of the pi4j internals, which are doing a lot of asynchronous stuff behind the scenes. The current solution is to make judicious use of try/catch blocks.
5. Obvious in hindsight, but we can't just read the bytes of a file and turn them into frequencies. For a 24-bit audio file, we need to read 3 bytes per channel, or 6 bytes per frame. Now the concept of frames and why they're 6 bytes makes a lot more sense!
6. TarsosDSP is awesome for signal processing and not great for actually playing audio with how I'm using it

## General progress updates
March 23-31: Busy due to work and other coursework early in the month. Completed LED grid tests and hardware setup. Wrote some code to do more interesting hardware tests. Initial research on spectrum analysis, FFTs, signal processing, and other related concepts.

April 1-2: Implemented test code to play an audio file and spit out a small spectrum. Neat! Also began work on report and presentation.

April 8: Bugfixes and misguided fixes that just made things worse oh no. This was a lifesaver: https://nealvs.wordpress.com/2017/08/11/java-sound-on-a-raspberry-pi-with-openjdk/. 


## Helpful and/or Cool Resources
* https://0110.be/releases/TarsosDSP/TarsosDSP-latest/TarsosDSP-latest-Documentation/
* https://github.com/JorenSix/TarsosDSP
* http://wiringpi.com/pins/
* https://www.instructables.com/id/RGB-LED-STRIP-COLOR-ORGAN-WITHOUT-MICROCONTROLLER/
* https://pi4j.com
* http://commons.apache.org/proper/commons-math/javadocs/api-3.4/org/apache/commons/math3/transform/FastFourierTransformer.html
* https://stackoverflow.com/questions/6740545/understanding-fft-output
* https://en.wikipedia.org/wiki/Window_function
* https://www.developer.com/java/other/article.php/2191351/Java-Sound-Using-Audio-Line-Events.htm
* https://stackoverflow.com/questions/19414453/how-to-get-resources-directory-path-programmatically
* https://stackoverflow.com/questions/11012819/how-can-i-get-a-resource-folder-from-inside-my-jar-file
* http://www.kingbrightusa.com/images/catalog/spec/tc15-11srwa.pdf
* https://nealvs.wordpress.com/2017/08/11/java-sound-on-a-raspberry-pi-with-openjdk/

