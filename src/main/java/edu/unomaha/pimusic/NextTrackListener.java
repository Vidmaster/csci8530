package edu.unomaha.pimusic;

import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

public class NextTrackListener implements GpioPinListenerDigital {
	Player player;

	public NextTrackListener(Player p) {
		this.player = p;
	}

	public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
		System.out.println("Next Track button pressed");
		System.out.println(event);
		if (event.getState() == PinState.HIGH) {
			return;
		}
		player.next();
		System.out.println("Now Playing: " + player.nowPlaying());
	}

}
