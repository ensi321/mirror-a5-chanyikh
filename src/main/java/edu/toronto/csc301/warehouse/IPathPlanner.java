package edu.toronto.csc301.warehouse;

import java.util.Map;
import java.util.Map.Entry;

import edu.toronto.csc301.grid.GridCell;
import edu.toronto.csc301.robot.IGridRobot;
import edu.toronto.csc301.robot.IGridRobot.Direction;

public interface IPathPlanner {

	/**
	 * A helper function used for planning the paths of robots through a grid.
	 * 
	 * This function is given some goals (robot2dest) and constraints (the floor plan
	 * of the warehouse and the location of other robots on the floor), and returns
	 * the next step (i.e. which robot should step in which direction) that 
	 * someone should take in order to satisfy the goals.
	 * 
	 * @param warehouse The full state of a warehouse, containing information about
	 *   the floor plan, the robots on the floor and their state (e.g. are they in 
	 *   motion? If so, in which direction?)
	 * @param robot2dest Specifies our goals - Which robots should get to which locations. 
	 */
	Entry<IGridRobot, Direction> nextStep(IWarehouse warehouse, 
			Map<IGridRobot,GridCell> robot2dest);
	
}
