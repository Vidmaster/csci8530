package edu.unomaha.pimusic;

import java.util.Arrays;

import be.tarsos.dsp.util.fft.HannWindow;
import be.tarsos.dsp.util.fft.WindowFunction;

public class VisualizationController {
	private static final double SCALE_FACTOR = 0.09;

	private static volatile VisualizationController instance;
	private volatile int[] amplitudes;

	private VisualizationController() {
		amplitudes = new int[8];
		Arrays.fill(amplitudes, 0);
	}

	public static synchronized VisualizationController getInstance() {
		if (instance == null) {
			instance = new VisualizationController();
		}

		return instance;
	}

	public int[] getGridLevels() {
		return amplitudes;
	}

	public void generateGridLevels(float[] fftAmplitudes) {
		// Convert the FFT amplitudes to an output array of 8 items for the LED
		// grid
		int chunk = fftAmplitudes.length / 16;
		WindowFunction w = new HannWindow();
		w.apply(fftAmplitudes);
		for (int i = 0; i < 8; i++) {
			amplitudes[i] = (int) (averageRange(fftAmplitudes, i * chunk, i * chunk + chunk) / SCALE_FACTOR);
		}
		System.out.println(Arrays.toString(amplitudes));
		visualizeOutput(amplitudes);
	}

	void visualizeOutput(int[] output) {
		for (int i = 0; i < output.length; i++) {
			char[] o = new char[output[i]];
			Arrays.fill(o, '*');
			System.out.println(o);
		}
		System.out.println();
	}

	float averageRange(float[] data, int start, int end) {
		float avg = 0;

		for (int i = start; i < end; i++) {
			avg += data[i];
		}

		avg = avg / (end - start);
		System.out.println(avg);
		return avg;
	}

}
