package edu.toronto.csc301.warehouse;



public class Rack {
	
	private int capacity;

	public Rack(int capacity) {
		this.capacity = capacity;
	}
	
	public int getCapacity() {
		return capacity;
	}
	
	@Override
	public boolean equals(Object o) {
		return o instanceof Rack && ((Rack) o).capacity == this.capacity;
	}
	
	@Override
	public int hashCode() {
		return this.capacity;
	}
}
