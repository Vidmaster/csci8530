/*
 * Modified from TarsosDSP example Player code by Henry McNeil for CSCI 8530
 * at the University of Nebraska, Omaha
 * 
 * TODO: Tarsos audio output is choppy right now. Test on the Pi and then
 * consider modifying this to just do the FFT and running audio through the java library
 */

/*
*      _______                       _____   _____ _____  
*     |__   __|                     |  __ \ / ____|  __ \ 
*        | | __ _ _ __ ___  ___  ___| |  | | (___ | |__) |
*        | |/ _` | '__/ __|/ _ \/ __| |  | |\___ \|  ___/ 
*        | | (_| | |  \__ \ (_) \__ \ |__| |____) | |     
*        |_|\__,_|_|  |___/\___/|___/_____/|_____/|_|     
*                                                         
* -------------------------------------------------------------
*
* TarsosDSP is developed by Joren Six at IPEM, University Ghent
*  
* -------------------------------------------------------------
*
*  Info: http://0110.be/tag/TarsosDSP
*  Github: https://github.com/JorenSix/TarsosDSP
*  Releases: http://0110.be/releases/TarsosDSP/
*  
*  TarsosDSP includes modified source code by various authors,
*  for credits and info, see README.
* 
*/

package edu.unomaha.pimusic;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.GainProcessor;
import be.tarsos.dsp.WaveformSimilarityBasedOverlapAdd;
import be.tarsos.dsp.WaveformSimilarityBasedOverlapAdd.Parameters;
import be.tarsos.dsp.io.jvm.AudioDispatcherFactory;
import be.tarsos.dsp.io.jvm.AudioPlayer;

// TODO: Fix audio playing.
/*
 * Need to add a java Clip and play it in parallel with the player
 * Should be able to synchronize between time played and frames
 */
public class Player implements AudioProcessor {

	private PropertyChangeSupport support = new PropertyChangeSupport(this);

	private PlayerState state;
	private File loadedFile;
	private GainProcessor gainProcessor;
	private AudioPlayer audioPlayer;
	private WaveformSimilarityBasedOverlapAdd wsola;
	private AudioDispatcher dispatcher;

	private double durationInSeconds;
	private double currentTime;
	private double pauzedAt;

	private final AudioProcessor beforeWSOLAProcessor;
	private final AudioProcessor afterWSOLAProcessor;

	private double gain;
	private double tempo;

	private List<String> files;
	private int currentFileIndex = 0;
	private String musicDirectory;

	private Clip clip;

	public Player(AudioProcessor beforeWSOLAProcessor, AudioProcessor afterWSOLAProcessor) {
		state = PlayerState.NO_FILE_LOADED;
		gain = 0.0; // Set gain to 0 so only the JVM Clip outputs sound
		tempo = 1.0;
		this.beforeWSOLAProcessor = beforeWSOLAProcessor;
		this.afterWSOLAProcessor = afterWSOLAProcessor;
		this.musicDirectory = "music";
		try {
			this.clip = AudioSystem.getClip();
		} catch (LineUnavailableException e) {
			System.out.println("Unable to get system audio output");
		}
	}

	/*
	 * HCM Added - Initialize a new player with the FFT processor and load the
	 * first file from the resources directory
	 */
	public Player(String musicDirectory) {
		this(null, new FFTProcessor());
		this.musicDirectory = musicDirectory;
		setFileList();
		load(getFileList().get(0));
	}

	/*
	 * HCM Added - Overloaded for convenience
	 */
	public void load(String filePath) {
		File file = new File(filePath);
		load(file);
	}

	public void load(File file) {
		if (state != PlayerState.NO_FILE_LOADED) {
			eject();
		}
		loadedFile = file;
		AudioFileFormat fileFormat;
		try {
			fileFormat = AudioSystem.getAudioFileFormat(loadedFile);
		} catch (UnsupportedAudioFileException e) {
			throw new Error(e);
		} catch (IOException e) {
			throw new Error(e);
		}
		AudioFormat format = fileFormat.getFormat();
		durationInSeconds = fileFormat.getFrameLength() / format.getFrameRate();
		pauzedAt = 0;
		currentTime = 0;
		setState(PlayerState.FILE_LOADED);
	}

	public void eject() {
		loadedFile = null;
		try {
			stop();
		} catch (Exception ex) {
			// Left intentionally blank
		}
		setState(PlayerState.NO_FILE_LOADED);
	}

	/*
	 * HCM Added - Added for convenience
	 */
	public void playPause() {
		if (state == PlayerState.PLAYING) {
			pauze();
		} else {
			if (state != PlayerState.NO_FILE_LOADED) {
				load(new File(files.get(0)));
			}
			play();
		}
	}

	public void play() {
		if (state == PlayerState.NO_FILE_LOADED) {
			throw new IllegalStateException("Can not play when no file is loaded");
		} else if (state == PlayerState.PAUZED) {
			play(pauzedAt);
		} else {
			play(0);
		}
	}

	public void play(double startTime) {
		if (state == PlayerState.NO_FILE_LOADED) {
			throw new IllegalStateException("Can not play when no file is loaded");
		} else {
			try {
				AudioFileFormat fileFormat = AudioSystem.getAudioFileFormat(loadedFile);
				AudioInputStream in = AudioSystem.getAudioInputStream(loadedFile);
				clip.open(in);
				AudioFormat format = fileFormat.getFormat();
				clip.setMicrosecondPosition((long) (startTime * 1000000.0));

				gainProcessor = new GainProcessor(gain);
				audioPlayer = new AudioPlayer(format);
				wsola = new WaveformSimilarityBasedOverlapAdd(
						Parameters.slowdownDefaults(tempo, format.getSampleRate()));

				dispatcher = AudioDispatcherFactory.fromFile(loadedFile, wsola.getInputBufferSize(),
						wsola.getOverlap());

				wsola.setDispatcher(dispatcher);
				dispatcher.skip(startTime);

				dispatcher.addAudioProcessor(this);
				if (beforeWSOLAProcessor != null) {
					dispatcher.addAudioProcessor(beforeWSOLAProcessor);
				}

				dispatcher.addAudioProcessor(wsola);

				if (afterWSOLAProcessor != null) {
					dispatcher.addAudioProcessor(afterWSOLAProcessor);
				}
				dispatcher.addAudioProcessor(gainProcessor);

				dispatcher.addAudioProcessor(audioPlayer);

				Thread t = new Thread(dispatcher, "Audio Player Thread");
				clip.start();
				t.start();
				setState(PlayerState.PLAYING);
			} catch (UnsupportedAudioFileException e) {
				throw new Error(e);
			} catch (IOException e) {
				throw new Error(e);
			} catch (LineUnavailableException e) {
				throw new Error(e);
			}
		}
	}

	public void pauze() {
		pauze(currentTime);
	}

	public void pauze(double pauzeAt) {
		if (state == PlayerState.PLAYING || state == PlayerState.PAUZED) {
			setState(PlayerState.PAUZED);
			dispatcher.stop();
			clip.stop();
			pauzedAt = pauzeAt;
		} else {
			throw new IllegalStateException("Can not pauze when nothing is playing");
		}
	}

	public void stop() {
		if (state == PlayerState.PLAYING || state == PlayerState.PAUZED) {
			setState(PlayerState.STOPPED);
			dispatcher.stop();
			clip.stop();
		} else if (state != PlayerState.STOPPED) {
			throw new IllegalStateException("Can not stop when nothing is playing");
		}

	}

	public void setGain(double newGain) {
		gain = newGain;
		if (state == PlayerState.PLAYING) {
			gainProcessor.setGain(gain);
		}
	}

	public void setTempo(double newTempo) {
		tempo = newTempo;
		if (state == PlayerState.PLAYING) {
			wsola.setParameters(Parameters.slowdownDefaults(tempo, dispatcher.getFormat().getSampleRate()));
		}
	}

	public double getDurationInSeconds() {
		if (state == PlayerState.NO_FILE_LOADED) {
			throw new IllegalStateException("No file loaded, unable to determine the duration in seconds");
		}
		return durationInSeconds;
	}

	private void setState(PlayerState newState) {
		PlayerState oldState = state;
		state = newState;
		support.firePropertyChange("state", oldState, newState);
	}

	public PlayerState getState() {
		return state;
	}

	public void addPropertyChangeListener(PropertyChangeListener l) {
		support.addPropertyChangeListener(l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		support.removePropertyChangeListener(l);
	}

	public boolean process(AudioEvent audioEvent) {
		currentTime = audioEvent.getTimeStamp();
		return true;
	}

	public void processingFinished() {
		if (state == PlayerState.PLAYING) {
			setState(PlayerState.STOPPED);
		}
	}

	/**
	 * Defines the state of the audio player.
	 * 
	 * @author Joren Six
	 */
	public static enum PlayerState {
		/**
		 * No file is loaded.
		 */
		NO_FILE_LOADED, /**
						 * A file is loaded and ready to be played.
						 */
		FILE_LOADED, /**
						 * The file is playing
						 */
		PLAYING, /**
					 * Audio play back is paused.
					 */
		PAUZED, /**
				 * Audio play back is stopped.
				 */
		STOPPED
	}

	/*
	 * HCM Added - Load and play the next file from the player's list
	 */
	public void next() {
		currentFileIndex++;
		if (currentFileIndex >= files.size()) {
			currentFileIndex = 0;
		}
		try {
			eject();
		} catch (IllegalStateException e) {
			System.out.println("Error ejecting: " + e.getMessage());
		}
		load(new File(files.get(currentFileIndex)));
		play();
	}

	/*
	 * HCM Added - Build a list of files from the resources/music directory
	 */
	public void setFileList() {
		Path p = Paths.get(musicDirectory);
		System.out.println("Audio files found in " + p.toAbsolutePath() + ":");

		try (Stream<Path> walk = Files.list(p)) {
			files = walk.map(x -> x.toString()).filter(f -> f.endsWith(".wav")).collect(Collectors.toList());
			files.forEach(System.out::println);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public List<String> getFileList() {
		return files;
	}

	public String getCurrentFile() {
		return this.files.get(currentFileIndex);
	}

	public int getCurrent() {
		return this.currentFileIndex;
	}

	public String nowPlaying() {
		if (state == PlayerState.PLAYING) {
			String[] f = getCurrentFile().split("/");
			return f[f.length - 1];
		}
		return state.name();
	}
}
