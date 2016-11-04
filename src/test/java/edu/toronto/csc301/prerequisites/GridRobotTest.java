package edu.toronto.csc301.prerequisites;

import static edu.toronto.csc301.util.TestUtil.createGridRobot;
import static edu.toronto.csc301.util.TestUtil.randomCell;
import static org.junit.Assert.assertEquals;

import java.util.function.Function;

import org.junit.Test;

import edu.toronto.csc301.grid.GridCell;
import edu.toronto.csc301.robot.IGridRobot;
import edu.toronto.csc301.robot.IGridRobot.Direction;

public class GridRobotTest {

	
	
	@Test(expected=NullPointerException.class)
	public void mustSpecifyInitialLocation() throws Exception {
		createGridRobot(null);
	}
	
	
	@Test
	public void createAndCheckLocation() throws Exception {
		GridCell location = randomCell();
		IGridRobot robot = createGridRobot(location);
		assertEquals(location, robot.getLocation());
	}
	
	
	
	
	@Test
	public void testStepNorth() throws Exception{
		testStep(Direction.NORTH, cell -> GridCell.at(cell.x, cell.y + 1));
	}
	
	@Test
	public void testStepEast() throws Exception{
		testStep(Direction.EAST, cell -> GridCell.at(cell.x + 1, cell.y));
	}
	
	@Test
	public void testStepSouth() throws Exception{
		testStep(Direction.SOUTH, cell -> GridCell.at(cell.x, cell.y - 1));
	}
	
	@Test
	public void testStepWest() throws Exception{
		testStep(Direction.WEST, cell -> GridCell.at(cell.x - 1, cell.y));
	}
	
	
	private void testStep(Direction direction, 
			Function<GridCell, GridCell> expectedLocation) throws Exception{
		GridCell location = randomCell();
		IGridRobot robot = createGridRobot(location);
		robot.step(direction);
		assertEquals(expectedLocation.apply(location), robot.getLocation());
	}
	

}
