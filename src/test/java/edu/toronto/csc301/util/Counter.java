package edu.toronto.csc301.util;

import java.util.function.Consumer;


/**
 * A helper class. A Consumer<T> that keeps a count of how many times
 * it was called.
 */
public class Counter<T> implements Consumer<T> {

	private int count = 0;
	
	
	@Override
	public void accept(T t) {
		count++;
	}
	
	public int getCount() {
		return count;
	}

}
