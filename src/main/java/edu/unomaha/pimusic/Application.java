package edu.unomaha.pimusic;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.RaspiPin;

public class Application {

	final LedGrid grid = LedGrid.getInstance();
	final GpioController gpio = GpioFactory.getInstance();

	Player player;

	public static void main(String[] args) throws InterruptedException {
		Application a = new Application();
		a.init();
		a.run();
	}

	public void init() {
		player = new Player();
		configureButtons();
	}

	public void run() {
		Thread visualizer = new Thread(new VisualizerThread());
		visualizer.start();
		player.play();
	}

	private void configureButtons() {
		GpioPinDigitalInput playPauseButton = gpio.provisionDigitalInputPin(RaspiPin.GPIO_15, "PlayPause",
				PinPullResistance.PULL_UP);
		playPauseButton.setShutdownOptions(true);
		playPauseButton.addListener(new PlayPauseButtonListener(player));

		GpioPinDigitalInput nextButton = gpio.provisionDigitalInputPin(RaspiPin.GPIO_16, "MyOtherButton",
				PinPullResistance.PULL_UP);
		nextButton.setShutdownOptions(true);
		nextButton.addListener(new NextTrackListener(player));
	}

}
