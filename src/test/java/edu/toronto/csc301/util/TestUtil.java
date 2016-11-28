package edu.toronto.csc301.util;

import java.util.List;
import java.util.Random;

import edu.toronto.csc301.grid.GridCell;
import edu.toronto.csc301.grid.IGrid;
import edu.toronto.csc301.robot.GridRobot;
import edu.toronto.csc301.robot.IGridRobot;
import edu.toronto.csc301.robot.IGridRobot.Direction;
import edu.toronto.csc301.warehouse.IPathPlanner;
import edu.toronto.csc301.warehouse.IWarehouse;
import edu.toronto.csc301.warehouse.PathPlanner;
import edu.toronto.csc301.warehouse.Rack;
import edu.toronto.csc301.warehouse.Warehouse;

/**
 * Helper functions used by other test classes.
 */
public class TestUtil {
	
	
	private static Random random = new Random();
	
	

	public static IGridRobot createGridRobot(GridCell initialLocation) throws Exception {
		return new GridRobot(initialLocation);
	}

	
	public static IWarehouse createWarehouse(IGrid<Rack> floorPlan) throws Exception {
		return new Warehouse(floorPlan);
	}
	
	public static IPathPlanner createPathPlanner(IWarehouse house, List<GridCell> goals) throws Exception {
		return new PathPlanner();
	}
	
	public static GridCell oneCellOver(GridCell location, Direction direction){
		switch (direction) {
		case NORTH:
			return GridCell.at(location.x, location.y + 1);
		case EAST:
			return GridCell.at(location.x + 1, location.y);
		case SOUTH:
			return GridCell.at(location.x, location.y - 1);
		case WEST:
			return GridCell.at(location.x - 1, location.y);
		default:
			return null;
		}
	}
	
	
	public static Direction randomDirection(){
		Direction[] directions = Direction.values();
		return directions[random.nextInt(directions.length)];
	}
	
	public static GridCell randomCell(){
		return GridCell.at(randomInt(-10000, 10000), randomInt(-10000, 10000));
	}
	
	/**
	 * Return a random integer in the range [a, b).
	 * That is, including a and excluding b.
	 */
	public static int randomInt(int a, int b){
		return a + random.nextInt(b - a);
	}

}
