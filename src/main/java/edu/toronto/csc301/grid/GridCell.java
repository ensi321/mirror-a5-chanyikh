package edu.toronto.csc301.grid;


/**
 * A little helper class.
 * A pair of X/Y coordinates that represents a location on a grid.
 */
public class GridCell {	
	
	
	/**
	 * Static factory method.
	 * @return A grid-cell instance with the given XY-coordinates.
	 */
	public static GridCell at(int x, int y){
		return new GridCell(x, y);
	}
	
	

	public final int x;
	public final int y;
	
	private GridCell(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public boolean equals(Object other) {
		return other instanceof GridCell && 
			this.x == ((GridCell) other).x &&
			this.y == ((GridCell) other).y;
	}
	
	
	@Override
	public int hashCode() {
		return (int) (Math.pow(2, x) * Math.pow(3, y));
	}
	
	@Override
	public String toString() {
		return "[" + x + "," + y + "]";
	}

}
