package edu.unomaha.pimusic;

import java.util.concurrent.Callable;

public class TestCallback implements Callable<Void> {

	public Void call() throws Exception {
		// TODO Auto-generated method stub
		System.out.println("pushed the button");
		return null;
	}

}
