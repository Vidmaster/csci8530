package edu.unomaha.pimusic;

import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

public class PlayPauseButtonListener implements GpioPinListenerDigital {
	Player player;

	public PlayPauseButtonListener(Player p) {
		this.player = p;
	}

	public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
		System.out.println("Play pause button pressed");
		System.out.println(event);
		if (event.getState() == PinState.HIGH) {
			return;
		}
		player.playPause();
	}

}
