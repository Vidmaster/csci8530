package edu.unomaha.pimusic;

import org.junit.Before;
import org.junit.Test;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

public class TestHardware {
	static LedGrid g;

	@Before
	public void setup() {
		g = LedGrid.getInstance();
	}

	@Test
	public void testButtons() throws InterruptedException {

		final GpioController gpio = GpioFactory.getInstance();

		GpioPinDigitalInput myButton = gpio.provisionDigitalInputPin(RaspiPin.GPIO_15, "MyButton",
				PinPullResistance.PULL_UP);
		myButton.setShutdownOptions(true);
		GpioPinDigitalInput myOtherButton = gpio.provisionDigitalInputPin(RaspiPin.GPIO_16, "MyOtherButton",
				PinPullResistance.PULL_UP);
		myButton.setShutdownOptions(true);
		myOtherButton.setShutdownOptions(true);

		myButton.addListener(new TestButtonListener());
		myOtherButton.addListener(new GpioPinListenerDigital() {

			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
				// TODO Auto-generated method stub
				try {
					triangleTest();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});

		System.out.println("Push some buttons now!");
		Thread.sleep(10000);
		System.out.println("Test over, stop!");
	}

	@Test
	private void simpleGridTest() {
		g.activateColumn(4, 4, 1000);
	}

	@Test
	private void triangleTest() throws InterruptedException {
		// |\ triangle
		for (int i = 0; i < 30; i++) {
			int[] test1 = { 8, 7, 6, 5, 4, 3, 2, 1 };
			g.activateGrid(test1);
		}

		Thread.sleep(500);

		// /| triangle
		for (int i = 0; i < 30; i++) {
			int[] test1 = { 1, 2, 3, 4, 5, 6, 7, 8 };
			g.activateGrid(test1);
		}

		Thread.sleep(500);

		// /\ triangle
		for (int i = 0; i < 30; i++) {
			int[] test1 = { 2, 4, 6, 8, 8, 6, 4, 2 };
			g.activateGrid(test1);
		}
	}
}
