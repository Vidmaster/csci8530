package edu.unomaha.pimusic;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class LedGrid {
	private static volatile LedGrid instance;

	GpioController gpio;

	GpioPinDigitalOutput r1, r2, r3, r4, r5, r6, r7, r8;
	GpioPinDigitalOutput c1, c2, c3, c4, c5, c6, c7, c8;
	GpioPinDigitalOutput[] rows;
	GpioPinDigitalOutput[] columns;

	// 1000 ms / 30 Hz / 8 columns = 4.16 ms/column
	static final int DEFAULT_PULSE_DURATION = 4;

	public static synchronized LedGrid getInstance() {
		if (instance == null) {
			instance = new LedGrid();
		}

		return instance;
	}

	private LedGrid() {
		this.gpio = GpioFactory.getInstance();
		provisionGridWPI();
	}

	// This method creates the LED grid using the BCM pin numbers.
	@SuppressWarnings("unused")
	private void provisionGridBCM() {
		this.r1 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_21, "R1", PinState.LOW);
		this.r2 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_16, "R2", PinState.LOW);
		this.r3 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_06, "R3", PinState.LOW);
		this.r4 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_20, "R4", PinState.LOW);
		this.r5 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_17, "R5", PinState.LOW);
		this.r6 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_05, "R6", PinState.LOW);
		this.r7 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_27, "R7", PinState.LOW);
		this.r8 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_22, "R8", PinState.LOW);
		this.rows = buildRowArray();

		this.c1 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_24, "C1", PinState.HIGH);
		this.c2 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_19, "C2", PinState.HIGH);
		this.c3 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_13, "C3", PinState.HIGH);
		this.c4 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_12, "C4", PinState.HIGH);
		this.c5 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_26, "C5", PinState.HIGH);
		this.c6 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_25, "C6", PinState.HIGH);
		this.c7 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_23, "C7", PinState.HIGH);
		this.c8 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_18, "C8", PinState.HIGH);
		this.columns = buildColumnArray();
	}

	private void provisionGridWPI() {
		this.r1 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_29, "R1", PinState.LOW);
		this.r2 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_27, "R2", PinState.LOW);
		this.r3 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_22, "R3", PinState.LOW);
		this.r4 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_28, "R4", PinState.LOW);
		this.r5 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00, "R5", PinState.LOW);
		this.r6 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_21, "R6", PinState.LOW);
		this.r7 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02, "R7", PinState.LOW);
		this.r8 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03, "R8", PinState.LOW);
		this.rows = buildRowArray();

		this.c1 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_05, "C1", PinState.HIGH);
		this.c2 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_24, "C2", PinState.HIGH);
		this.c3 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_23, "C3", PinState.HIGH);
		this.c4 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_26, "C4", PinState.HIGH);
		this.c5 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_25, "C5", PinState.HIGH);
		this.c6 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_06, "C6", PinState.HIGH);
		this.c7 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04, "C7", PinState.HIGH);
		this.c8 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, "C8", PinState.HIGH);
		this.columns = buildColumnArray();
	}

	private GpioPinDigitalOutput[] buildRowArray() {
		GpioPinDigitalOutput[] array = { r1, r2, r3, r4, r5, r6, r7, r8 };

		return array;
	}

	private GpioPinDigitalOutput[] buildColumnArray() {
		GpioPinDigitalOutput[] array = { c1, c2, c3, c4, c5, c6, c7, c8 };

		return array;
	}

	public void activateGrid(int[] levels) throws InterruptedException {
		if (levels.length != 8) {
			System.out.println("Received " + levels.toString() + " in activateGrid");
			throw new RuntimeException("Bad levels received in activateGrid");
		}

		for (int c = 0; c < 8; c++) {
			activateColumn(c, levels[c]);
			Thread.sleep(DEFAULT_PULSE_DURATION);
		}
	}

	public void activateColumn(int c, int level) {
		activateColumn(c, level, DEFAULT_PULSE_DURATION);
	}

	// TODO: Clean this up a bit. c = [0-7], l=[0-8] but 0 should be off.
	// Confusing right now.
	public void activateColumn(int c, int level, long duration) {
		if (level > 8) {
			level = 8;
		}
		if (c > 8) {
			return;
		}

		for (int i = 0; i < level; i++) {
			try {
				rows[i].pulse(duration);
			} catch (Exception ex) {
				System.out.println("error activating pin " + i);
			}
		}
		try {
			columns[c].pulse(duration, PinState.LOW);
		} catch (Exception ex) {
			System.out.println("error activating column " + c);
		}
	}

}
