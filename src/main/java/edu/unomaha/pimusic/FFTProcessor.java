package edu.unomaha.pimusic;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.util.fft.BlackmanWindow;
import be.tarsos.dsp.util.fft.FFT;

public class FFTProcessor implements AudioProcessor {
	AudioController ac;

	public FFTProcessor() {
		ac = AudioController.getInstance();
	}

	public boolean process(AudioEvent audioEvent) {
		float[] fftBuffer = audioEvent.getFloatBuffer().clone();
		float[] amplitudes = new float[fftBuffer.length / 2];

		FFT fft = new FFT(fftBuffer.length, new BlackmanWindow());

		fft.forwardTransform(fftBuffer);
		fft.modulus(fftBuffer, amplitudes);
		ac.generateGridLevels(amplitudes);

		return true;
	}

	public void processingFinished() {
		// TODO Auto-generated method stub

	}

}
