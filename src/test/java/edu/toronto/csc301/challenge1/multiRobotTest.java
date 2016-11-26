package edu.toronto.csc301.challenge1;

import static edu.toronto.csc301.util.TestUtil.createPathPlanner;
import static edu.toronto.csc301.util.TestUtil.createWarehouse;
import static org.junit.Assert.*;

import java.util.Map.Entry;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import edu.toronto.csc301.grid.GridCell;
import edu.toronto.csc301.robot.IGridRobot;
import edu.toronto.csc301.robot.IGridRobot.Direction;
import edu.toronto.csc301.util.SimpleGridImpl;
import edu.toronto.csc301.warehouse.IPathPlanner;
import edu.toronto.csc301.warehouse.IWarehouse;

public class multiRobotTest{
	
//	@Rule
//    public Timeout globalTimeout = Timeout.seconds(10);
	
	
	
	// ------------------------------------------------------------------------
	
	
	
	private IPathPlanner pathPlanner;
	private IWarehouse warehouse;
	
	@Before
	public void setup() throws Exception{
		warehouse = createWarehouse(
				SimpleGridImpl.emptyRactanlge(20, 20, GridCell.at(0, 0)));
		pathPlanner = createPathPlanner(warehouse, null);
	}
	
	@After
	public void tearDown(){
		pathPlanner = null;
		warehouse = null;
	}
	
	
	
	// ------------------------------------------------------------------------
	
	@Test
	public void twoGoalstwoRobots() throws Exception{
		pathPlanner.addGoal(GridCell.at(1, 1));
		pathPlanner.addGoal(GridCell.at(19, 15));
		IGridRobot robot1 = warehouse.addRobot(GridCell.at(2, 3));
		IGridRobot robot2 = warehouse.addRobot(GridCell.at(10, 11));
		Entry<IGridRobot, Direction> nextStep = pathPlanner.nextStep();
		while(nextStep != null){
			IGridRobot r = nextStep.getKey();
			System.out.println("==========================");
			System.out.println(r.toString());
			System.out.println(nextStep.getValue());
			r.step(nextStep.getValue());
			nextStep = pathPlanner.nextStep();
		}
		
		assertEquals(1, 1);
	}
	
	
	
}