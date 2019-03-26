package edu.unomaha.pimusic;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.trigger.GpioCallbackTrigger;

public class Application {

	public static void main(String[] args) throws InterruptedException {

		final GpioController gpio = GpioFactory.getInstance();

		GpioPinDigitalInput myButton = gpio.provisionDigitalInputPin(RaspiPin.GPIO_14, "MyButton",
				PinPullResistance.PULL_DOWN);
		myButton.setShutdownOptions(true);
		GpioPinDigitalInput myOtherButton = gpio.provisionDigitalInputPin(RaspiPin.GPIO_15, "MyOtherButton",
				PinPullResistance.PULL_DOWN);
		myButton.setShutdownOptions(true);

		myButton.addListener(new TestButtonListener());
		myOtherButton.addTrigger(new GpioCallbackTrigger(new TestCallback()));
		myOtherButton.addListener(new TestButtonListener());
		myOtherButton.addTrigger(new GpioCallbackTrigger(new TestCallback()));

		LedGrid g = new LedGrid();

		easyTest(g, 2, 2);

		while (true) {
			Thread.sleep(500);
		}

	}

	private static void easyTest(LedGrid grid, int row, int col) {
		grid.activateColumn(col, row, 200);
	}

	private static void triangleTest(LedGrid grid) throws InterruptedException {
		// |\ triangle
		for (int i = 0; i < 30; i++) {
			int[] test1 = { 8, 7, 6, 5, 4, 3, 2, 1 };
			grid.activateGrid(test1);
		}

		// /| triangle
		for (int i = 0; i < 30; i++) {
			int[] test1 = { 1, 2, 3, 4, 5, 6, 7, 8 };
			grid.activateGrid(test1);
		}

		// /\ triangle
		for (int i = 0; i < 30; i++) {
			int[] test1 = { 2, 4, 6, 8, 8, 6, 4, 2 };
			grid.activateGrid(test1);
		}
	}

}
