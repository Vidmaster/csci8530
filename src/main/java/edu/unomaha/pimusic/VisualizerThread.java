package edu.unomaha.pimusic;

public class VisualizerThread implements Runnable {
	private static final int msPerUpdate = 1000 / 30;
	VisualizationController visualizationController;
	LedGrid grid;

	public VisualizerThread() {
		visualizationController = VisualizationController.getInstance();
		grid = LedGrid.getInstance();
	}

	public void run() {
		int[] levels;
		while (true) {
			try {
				levels = visualizationController.getGridLevels();
				grid.activateGrid(levels);
				Thread.sleep(msPerUpdate);
			} catch (InterruptedException e) {
				e.printStackTrace();
				return;
			}
		}

	}

}
