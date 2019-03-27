package edu.unomaha.pimusic;

import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

public class TestButtonListener implements GpioPinListenerDigital {
	public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
		// display pin state on console
		if (event.getState() == PinState.HIGH) {
			System.out.println(" --> GPIO PIN STATE CHANGE: " + event.getPin() + " = " + event.getState());
		}
	}

}
