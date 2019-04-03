package edu.unomaha.pimusic;

public class VisualizerThread implements Runnable {
	private static final int msPerUpdate = 1000 / 30;
	AudioController ac;
	LedGrid grid;

	public VisualizerThread() {
		ac = AudioController.getInstance();
		grid = LedGrid.getInstance();
	}

	public void run() {
		int[] levels;
		while (true) {
			try {
				levels = ac.getGridLevels();
				grid.activateGrid(levels);
				Thread.sleep(msPerUpdate);
			} catch (InterruptedException e) {
				e.printStackTrace();
				return;
			}
		}

	}

}
