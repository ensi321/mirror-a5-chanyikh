package edu.toronto.csc301.warehouse;

import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;

import edu.toronto.csc301.grid.GridCell;
import edu.toronto.csc301.grid.IGrid;
import edu.toronto.csc301.robot.IGridRobot;
import edu.toronto.csc301.robot.IGridRobot.Direction;


/**
 * The full state of a warehouse:
 * - Floor plan (i.e. Grid of racks), and the grid's cell size.
 * - All robots on the warehouse floor
 * - Which robots are currently moving, and in which direction.
 */
public interface IWarehouse {

	public IGrid<Rack> getFloorPlan();
	
	public IGridRobot addRobot(GridCell initialLocation);
	public Iterator<IGridRobot> getRobots();
	
	public Map<IGridRobot,Direction> getRobotsInMotion();
	
	
	// ------------------------------------------------------------------------
	// An IWarehouse is observable. 
	
	void subscribe(Consumer<IWarehouse> observer);
	void unsubscribe(Consumer<IWarehouse> observer);
}
