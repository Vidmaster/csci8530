package edu.unomaha.pimusic;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.RaspiPin;

public class Application {

	public static void main(String[] args) throws InterruptedException {

		final GpioController gpio = GpioFactory.getInstance();

		GpioPinDigitalInput myButton = gpio.provisionDigitalInputPin(RaspiPin.GPIO_15, "MyButton",
				PinPullResistance.PULL_DOWN);
		GpioPinDigitalInput myOtherButton = gpio.provisionDigitalInputPin(RaspiPin.GPIO_16, "MyButton",
				PinPullResistance.PULL_DOWN);

		myButton.addListener(new TestButtonListener());
		myOtherButton.addListener(new TestButtonListener());

		while (true) {
			Thread.sleep(500);
		}

	}

}
