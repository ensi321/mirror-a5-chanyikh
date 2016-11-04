package edu.toronto.csc301.listeners;

import static edu.toronto.csc301.util.TestUtil.createWarehouse;
import static edu.toronto.csc301.util.TestUtil.randomDirection;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.toronto.csc301.grid.GridCell;
import edu.toronto.csc301.grid.IGrid;
import edu.toronto.csc301.robot.IGridRobot;
import edu.toronto.csc301.robot.IGridRobot.Direction;
import edu.toronto.csc301.util.SimpleGridImpl;
import edu.toronto.csc301.util.Counter;
import edu.toronto.csc301.warehouse.IWarehouse;
import edu.toronto.csc301.warehouse.Rack;

public class WarehouseTest {
	

	//============================ Helper Classes =============================
	
	
	/**
	 * Helper class used in some of the tests above.
	 * A warehouse listener that logs the robots in-motion, every time it 
	 * is called.
	 */
	private static class RobotsInMotionLogger implements Consumer<IWarehouse>{

		List<Map<IGridRobot, Direction>> log = 
				new ArrayList<Map<IGridRobot, Direction>>();
		
		
		public List<Map<IGridRobot, Direction>> getLog() {
			return log;
		}
		
		
		@Override
		public void accept(IWarehouse warehouse) {
			log.add(new HashMap<IGridRobot, Direction>(
									warehouse.getRobotsInMotion()));
		}

	}

	
	
	
	/**
	 * A helper class that checks that getRobotsInMotion() does not
	 * expose any internal data structure.
	 */
	private static class RobotInMotionImmutabilityTester implements Consumer<IWarehouse>{
		
		@Override
		public void accept(IWarehouse warehouse) {
			Map<IGridRobot, Direction> robotsInMotion = warehouse.getRobotsInMotion();
			
			// If there are no robots in motion, there is nothing for us to test
			if(robotsInMotion.isEmpty()){
				return;
			}
			
			// Let's make sure we cannot mess up the internal data structure of the warehouse ...
			
			try{
				// Clear the map
				robotsInMotion.clear();
				
				// Then ask the warehouse for the robotsInMotion again.
				// We expect to get a fresh copy of the warehouse data ...
				assertFalse(warehouse.getRobotsInMotion().isEmpty());
				
			} catch (Exception e) {
				// If your code is preventing us from clearing the map by
				// throwing an exception, that's also an acceptable behaviour.
			}
		}

	}
	
	
	
	
	//=========================================================================
	
	
	
	// Create a new empty 23x17 warehouse, before each test ... 
	
	private IWarehouse warehouse;
	
	@Before
	public void setup() throws Exception{
		IGrid<Rack> floorPlan = SimpleGridImpl.emptyRactanlge(23, 17, GridCell.at(0, 0));
		warehouse = createWarehouse(floorPlan);
	}
	
	@After
	public void tearDown(){
		warehouse = null;
	}

	
	
	// ------------------------------------------------------------------------
	
	
	
	@Test
	public void verifyThatAddRobotTriggersListener() throws Exception {
		// Attach a listener
		Counter<IWarehouse> listener = new Counter<IWarehouse>();
		warehouse.subscribe(listener);
		
		// Make sure the listener was called
		warehouse.addRobot(GridCell.at(0, 0));
		assertEquals(1, listener.getCount());
	}
	
	
	@Test
	public void verifyThatAddRobotTriggersListenerEveryTime() throws Exception {
		// Attach a listener
		Counter<IWarehouse> listener = new Counter<IWarehouse>();
		warehouse.subscribe(listener);
		
		// Add a few robots ...
		warehouse.addRobot(GridCell.at(0, 0));
		warehouse.addRobot(GridCell.at(1, 0));
		warehouse.addRobot(GridCell.at(2, 0));
		
		assertEquals(3, listener.getCount());
	}
	
	
	@Test
	public void verifyThatAddRobotTriggersAllListeners() throws Exception {
		Collection<Counter<IWarehouse>> listeners = new ArrayList<Counter<IWarehouse>>();
		int n = 7; // Arbitrary
		for (int i = 0; i < n; i++) {
			Counter<IWarehouse> listener = new Counter<IWarehouse>();
			listeners.add(listener);
			warehouse.subscribe(listener);
		}
		
		// Trigger the listeners ...
		warehouse.addRobot(GridCell.at(0, 0));
		
		// Then verify the counts ...
		for(Counter<IWarehouse> listener : listeners){
			assertEquals(1, listener.getCount());
		}
	}
	
	
	@Test
	public void verifyThatTheWarehousePassesItselfToTheListener() throws Exception {
		warehouse.subscribe(wh -> assertSame(warehouse, wh));
		// Trigger the listener by adding a robot
		warehouse.addRobot(GridCell.at(0, 0));
	}
	
	

	@Test
	public void unsubscribeAndVerifyThatAddRobotDoesNotTriggerTheListener() throws Exception {
		Counter<IWarehouse> listener = new Counter<IWarehouse>();
		warehouse.subscribe(listener);
		
		warehouse.addRobot(GridCell.at(0, 0));
		assertEquals(1, listener.getCount());
		
		// Unsubscribe
		warehouse.unsubscribe(listener);
		warehouse.addRobot(GridCell.at(0, 1));
		// Make sure the listener wasn't called
		assertEquals(1, listener.getCount());
	}
	

	
	// ------------------------------------------------------------------------
	
	
	
	@Test
	public void verifyThatRobotStepTriggersListener_1() throws Exception {
		IGridRobot robot = warehouse.addRobot(GridCell.at(10, 10));
		
		Counter<IWarehouse> listener = new Counter<IWarehouse>();
		warehouse.subscribe(listener);
		
		// Each robot step should trigger the listener twice.
		// Once when the step starts, and another time when it ends.
		robot.step(randomDirection());
		assertEquals(2, listener.getCount());
	}
	
	
	@Test
	public void verifyThatRobotStepTriggersListener_2() throws Exception {
		IGridRobot robot = warehouse.addRobot(GridCell.at(10, 10));
		
		Counter<IWarehouse> listener = new Counter<IWarehouse>();
		warehouse.subscribe(listener);
		
		// Test multiple steps ...
		int n = 5; // Arbitrary
		for (int i = 1; i < n; i++) {
			robot.step(randomDirection());
			assertEquals(i * 2, listener.getCount());
		}
	}
	
	
	
	// ------------------------------------------------------------------------
	
	
	@Test
	public void verifyThatRobotMotionIsTracked() throws Exception {
		IGridRobot robot = warehouse.addRobot(GridCell.at(10, 10));
		RobotsInMotionLogger listener = new RobotsInMotionLogger();
		warehouse.subscribe(listener);
		
		Direction direction = randomDirection(); 
		robot.step(direction);
		
		// Now, let's check the log ...
		List<Map<IGridRobot, Direction>> log = listener.getLog();
		
		// We expect 2 entries (once when the robot's step starts, and one when it ends)
		assertEquals(2, log.size());
		
		// When the robot's step starts ...
		Map<IGridRobot, Direction> robot2direction = log.get(0);
		assertEquals(1, robot2direction.size());
		assertTrue(robot2direction.containsKey(robot));
		assertEquals(direction, robot2direction.get(robot));
		
		// And, when it ends ...
		robot2direction = log.get(1);
		assertEquals(0, robot2direction.size());
	}
	
	
	
	@Test
	public void verifyThatRobotsInMotionCannotBeChangedFromOutsideOfTheWarehouse() throws Exception {
		IGridRobot robot = warehouse.addRobot(GridCell.at(10, 10));
		RobotInMotionImmutabilityTester listener = new RobotInMotionImmutabilityTester();
		warehouse.subscribe(listener);
		robot.step(randomDirection());
	}
	
	

}
