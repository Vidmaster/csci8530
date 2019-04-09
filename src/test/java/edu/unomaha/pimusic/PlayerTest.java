package edu.unomaha.pimusic;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PlayerTest {

	@Test
	public void testGetFileList() {
		Player player = new Player("src/main/resources/music");

		assertTrue(player.getFileList().size() > 0);
	}

}
