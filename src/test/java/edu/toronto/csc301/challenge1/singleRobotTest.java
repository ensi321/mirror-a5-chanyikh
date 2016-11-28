package edu.toronto.csc301.challenge1;

import static edu.toronto.csc301.util.TestUtil.createPathPlanner;

import static edu.toronto.csc301.util.TestUtil.createWarehouse;
import static edu.toronto.csc301.util.TestUtil.randomInt;
import static edu.toronto.csc301.util.TestUtil.randomDirection;
import static edu.toronto.csc301.util.TestUtil.oneCellOver;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;
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
import edu.toronto.csc301.util.Counter;
import edu.toronto.csc301.warehouse.IPathPlanner;
import edu.toronto.csc301.warehouse.IWarehouse;
import edu.toronto.csc301.warehouse.nextStepNotFoundException;

public class singleRobotTest {
	
	
//	@Rule
//    public Timeout globalTimeout = Timeout.seconds(20);
	
	
	
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
	public void returnNullIfAllRobotIsAlreadyAtDestination() throws Exception {
		// Add a robot at (0,0)
		IGridRobot robot = warehouse.addRobot(GridCell.at(0, 0));
		
		// Set the robot's final destination to be (0,0) 
		Map<IGridRobot, GridCell> robot2dest = new HashMap<IGridRobot,GridCell>();
		robot2dest.put(robot, GridCell.at(0, 0));
		
		// Since the robot is already at its destination, there is no next step
		assertNull(pathPlanner.nextStep(warehouse, robot2dest));
	}
	
	
	@Test
	public void returnTheCorrectResultWhenDestinationIsOneCellAway() throws Exception {	
		// Start somewhere in the room (but not near the walls)
		GridCell initialLocation = GridCell.at(randomInt(5, 15), randomInt(5, 15));
		Direction stepDirection = randomDirection();
		
		// Add the robot
		IGridRobot robot = warehouse.addRobot(initialLocation);
		
		// Set the robot's final destination to be one cell over  
		Map<IGridRobot, GridCell> robot2dest = new HashMap<IGridRobot,GridCell>();
		robot2dest.put(robot, oneCellOver(initialLocation, stepDirection));
		
		// The planner should return the correct next step 
		Entry<IGridRobot, Direction> result = pathPlanner.nextStep(warehouse, robot2dest);
		assertEquals(robot, result.getKey());
		assertEquals(stepDirection, result.getValue());
	}
	
	
	@Test
	public void returnTheCorrectResultWhenDestinationIsAFewCellsAway() throws Exception {
		// The starting point is more or less in the middle of the room
		GridCell initialLocation = GridCell.at(randomInt(8, 13), randomInt(8, 13));
		
		// The destination will be a few (not too many) cells away
		int deltaX = randomInt(3, 6);
		int deltaY = randomInt(3, 6);
		GridCell destination = GridCell.at(
				initialLocation.x + deltaX, initialLocation.y - deltaY);
		
		// Add the robot
		IGridRobot robot = warehouse.addRobot(initialLocation);
		
		// Set the robot's final destination  
		Map<IGridRobot, GridCell> robot2dest = new HashMap<IGridRobot,GridCell>();
		robot2dest.put(robot, destination);
		
		// The planner should get the robot to the destination (and do it efficiently)
		int stepLimit = deltaX + deltaY;
		int stepCount = 0;
		while(stepCount < stepLimit){
			Entry<IGridRobot, Direction> nextStep = pathPlanner.nextStep(warehouse, robot2dest);
			IGridRobot r = nextStep.getKey();
			
			// Take the step ...
			r.step(nextStep.getValue());
			stepCount++;
			
			// Now, make sure we're one step closer ...
			int distance = Math.abs(r.getLocation().x - destination.x) + 
					Math.abs(r.getLocation().y - destination.y);    
			assertEquals(stepLimit - stepCount, distance);
		}
	}
	@Test(expected=nextStepNotFoundException.class)
	public void returnNextStepNotFoundExceptionWhenDestIsUnreasonable() throws Exception{
		GridCell destination = GridCell.at(25, 25);
		// Add the robot
		IGridRobot robot = warehouse.addRobot(GridCell.at(10, 10));
		
		// Set the robot's final destination  
		Map<IGridRobot, GridCell> robot2dest = new HashMap<IGridRobot,GridCell>();
		robot2dest.put(robot, destination);
		Entry<IGridRobot, Direction> nextStep = pathPlanner.nextStep(warehouse, robot2dest);
		while (nextStep != null){
			IGridRobot r = nextStep.getKey();
			System.out.println("==========================");
			System.out.println(r.toString());
			System.out.println(nextStep.getValue());
			// Take the step ...
			r.step(nextStep.getValue());
			nextStep = pathPlanner.nextStep(warehouse, robot2dest);
		}
		
	}
	
	
	// ------------------------------------------------------------------------
	
	

	/**
	 * In this test we want to verify that the path-planner is only
	 * doing planning.
	 * That is, it does not move any robots, it only suggests which robot
	 * should move in which direction. 
	 */
	@Test
	public void thePlannerMustNotTriggerAnyAction() throws Exception {
		IGridRobot robot = warehouse.addRobot(GridCell.at(4, 4));
		  
		Map<IGridRobot, GridCell> robot2dest = new HashMap<IGridRobot,GridCell>();
		robot2dest.put(robot, GridCell.at(6, 5));
		
		// Attach a listener to the warehouse 
		Counter<IWarehouse> listener = new Counter<IWarehouse>();
		warehouse.subscribe(listener);
		
		// Use the planner
		pathPlanner.nextStep(warehouse, robot2dest);
		
		// Make sure that the planner didn't trigger the listener
		assertEquals(0, listener.getCount());
		
	}

}
