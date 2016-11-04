package edu.toronto.csc301.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import edu.toronto.csc301.grid.GridCell;
import edu.toronto.csc301.grid.IGrid;


/**
 * An extremely simple implementation of an IGrid<T>, used for testing.
 */
public class SimpleGridImpl<T> implements IGrid<T> {

	
	// Convenience factory method
	public static <T> SimpleGridImpl<T> emptyRactanlge(
			int width, int height, GridCell sw){
		
		Map<GridCell, T> cell2item = new HashMap<GridCell,T>();
		for (int x = sw.x; x < sw.x + width; x++) {
			for (int y = sw.y; y < sw.y + height; y++) {
				cell2item.put(GridCell.at(x, y), null);
			}
		}
		
		return new SimpleGridImpl<T>(cell2item);
	}
	

	
	
	
	private Map<GridCell, T> cell2item = new HashMap<GridCell, T>();
	
	public SimpleGridImpl(Map<GridCell, T> cell2item) {
		this.cell2item.putAll(cell2item);
	}
	
	
	
	@Override
	public T getItem(GridCell cell) {
		return cell2item.get(cell);
	}

	@Override
	public Iterator<GridCell> getGridCells() {
		return cell2item.keySet().iterator();
	}

	@Override
	public boolean hasCell(GridCell cell) {
		return cell2item.containsKey(cell);
	}

}
