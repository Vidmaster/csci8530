package edu.unomaha.pimusic;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Mixer.Info;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.junit.Test;

import be.tarsos.dsp.util.fft.FFT;
import be.tarsos.dsp.util.fft.HannWindow;
import edu.unomaha.pimusic.Player.PlayerState;

public class TestAudio {
	@Test
	public void stuff() {
		Info[] info = AudioSystem.getMixerInfo();
		for (Info i : info) {
			System.out.println(i.toString());
		}
	}

	@Test
	public void testJVMAudioStream() throws Exception {
		File f = new File("/Users/Vidmaster/csci8530/src/test/resources/Trance2_Lead.wav");
		AudioInputStream in = AudioSystem.getAudioInputStream(f);
		Clip c = AudioSystem.getClip();
		c.open(in);
		c.setFramePosition(25000);
		c.start();
		System.out.println(c.getFormat());
		System.out.println(c.getFormat().getSampleRate());
		System.out.println(c.getFrameLength());

		Thread.sleep(3000);
		c.stop();
		c.close();
	}

	@Test
	public void testFFT() throws Exception {
		File f = new File("/Users/Vidmaster/csci8530/src/test/resources/Trance2_Lead.wav");
		AudioInputStream in = AudioSystem.getAudioInputStream(f);
		byte[] b = new byte[1024];
		System.out.println("Read " + in.read(b, 0, 1024));
		double[] d = new double[1024];
		for (int i = 0; i < 1024; i++) {
			d[i] = b[i];
		}
		FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);
		Complex[] c = fft.transform(d, TransformType.FORWARD);
		System.out.println(c.length);
		System.out.println(Arrays.toString(c));

		/*
		 * Frame length / sample rate = audio file length Sample rate / 30 = #
		 * of frames to grab Frame size * frames to grab = bytes 44100 / 30 * 6
		 * = 8820 bytes every 1/30th of a second Should be able to FFT on any
		 * power of 2 <= 8192 in this case Depending on how long the pi takes to
		 * do a FFT The frequency to fft-bin equation is (bin_id * freq/2) /
		 * (N/2) where freq is your sample-frequency (aka 32 Hz, and N is the
		 * size of your FFT) 44100 hz/2) / (1024/2)
		 * 
		 */
	}

	/*
	 * This is cool, but incorrect. We need to convert bytes to floats 3 at a
	 * time and take endianness into consideration when doing so. Also floats
	 * are 4 bytes in Java so we need a block of 0x00 at the start (or end).
	 * Tarsos seems to have some functionality for handling this, which should
	 * make things easier.
	 */
	@Test
	public void testTarsosFFT() throws IOException, UnsupportedAudioFileException {
		int size = 4096;

		File f = new File("/Users/Vidmaster/csci8530/src/test/resources/Trance2_Lead.wav");
		AudioInputStream in = AudioSystem.getAudioInputStream(f);
		byte[] b = new byte[size];
		System.out.println("Read " + in.read(b, 0, size));
		float[] d = new float[size];
		for (int i = 0; i < size; i++) {
			d[i] = b[i];
		}

		FFT fft = new FFT(size, new HannWindow());
		fft.forwardTransform(d);
		System.out.println(Arrays.toString(d));
		float[] amplitudes = new float[size / 2];
		fft.modulus(d, amplitudes);
		System.out.println(Arrays.toString(amplitudes));
		System.out.println(findMax(amplitudes));

		// Convert to output array of 8/8 for the grid
		int[] output = new int[8];
		int chunk = amplitudes.length / 8;
		for (int i = 0; i < 8; i++) {
			output[i] = (int) (averageRange(amplitudes, i * chunk, i * chunk + chunk) / 1000);
		}
		System.out.println(Arrays.toString(output));
		visualizeOutput(output);
	}

	@Test
	public void playerTest() throws Exception {
		String filePath = Application.class.getResource("/music/Trance2_Lead.wav").getFile();

		Player player = new Player(new FFTProcessor(), null);
		player.load(filePath);
		player.play();

		while (player.getState() == PlayerState.PLAYING) {
			Thread.sleep(50);
		}
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
		System.out.println("Averaging from " + start + " to " + end);
		float avg = 0;
		for (int i = start; i < end; i++) {
			avg += data[i];
		}
		avg = avg / (end - start);
		System.out.println(avg);
		return avg;
	}

	float findMax(float[] x) {
		float max = 0;
		for (int i = 0; i < x.length; i++) {
			if (x[i] > max)
				max = x[i];
		}
		return max;
	}
}
