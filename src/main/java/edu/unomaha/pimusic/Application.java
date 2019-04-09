package edu.unomaha.pimusic;

import java.nio.file.Paths;

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
		if (args.length < 1 || !Paths.get(args[0]).toFile().isDirectory()) {
			System.out.println("Please call with path to music directory");
			return;
		}
		Application a = new Application();
		a.init(args[0]);
		a.run();
	}

	public void init(String musicDirectory) {
		player = new Player(musicDirectory);
		configureButtons();
	}

	public void run() {
		Thread visualizer = new Thread(new VisualizerThread());
		visualizer.start();
		player.play();
	}

	// TODO: Fix the buttons. Need to handle player state more gracefully and
	// loop better
	// TODO: Send some 0s to the visualizer when we pause?
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
