package edu.toronto.csc301.grid;

import java.util.Iterator;

public interface IGrid<T>{
	
	public T getItem(GridCell cell);
	public Iterator<GridCell> getGridCells();
	public boolean hasCell(GridCell cell);
	
}
