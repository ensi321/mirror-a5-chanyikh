package edu.toronto.csc301.prerequisites;

import static edu.toronto.csc301.util.TestUtil.createWarehouse;
import static edu.toronto.csc301.util.TestUtil.randomInt;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.junit.Test;

import edu.toronto.csc301.grid.GridCell;
import edu.toronto.csc301.grid.IGrid;
import edu.toronto.csc301.robot.IGridRobot;
import edu.toronto.csc301.util.SimpleGridImpl;
import edu.toronto.csc301.warehouse.IWarehouse;
import edu.toronto.csc301.warehouse.Rack;

public class WarehouseTest {

	
	@Test(expected=NullPointerException.class)
	public void floorPlanCannotBeNull() throws Exception {
		createWarehouse(null);
	}
	
	
	@Test
	public void createWarehouseThenGetFloorPlan() throws Exception {
		IGrid<Rack> floorPlan = new SimpleGridImpl<Rack>(Collections.emptyMap());
		IWarehouse warehouse  = createWarehouse(floorPlan);
		assertSame(floorPlan, warehouse.getFloorPlan());
	}
	
	
	@Test
	public void warehouseStartsWithNoRobots() throws Exception {
		IGrid<Rack> floorPlan = new SimpleGridImpl<Rack>(Collections.emptyMap());
		IWarehouse warehouse  = createWarehouse(floorPlan);
		assertFalse(warehouse.getRobots().hasNext());
	}
	
	
	@Test
	public void warehouseStartsWithNoRobotsInMotion() throws Exception {
		IGrid<Rack> floorPlan = new SimpleGridImpl<Rack>(Collections.emptyMap());
		IWarehouse warehouse  = createWarehouse(floorPlan);
		assertTrue(warehouse.getRobotsInMotion().isEmpty());
	}


	// ------------------------------------------------------------------------
	
	
	
	@Test
	public void addRobotThenTestGetRobots() throws Exception {	
		IGrid<Rack> floorPlan = SimpleGridImpl.emptyRactanlge(
											20, 10, GridCell.at(0, 0));
		IWarehouse warehouse  = createWarehouse(floorPlan);
		
		// Choose a random location on the grid
		GridCell location = GridCell.at(randomInt(0, 20), randomInt(0, 10));
		IGridRobot robot  = warehouse.addRobot(location);
		
		Iterator<IGridRobot> robots = warehouse.getRobots();
		assertTrue(robots.hasNext());
		assertEquals(robot, robots.next());
		assertFalse(robots.hasNext());
	}
	
	
	@Test(expected=IllegalArgumentException.class)
	public void cannotAddRobotOutsideOfTheGrid() throws Exception {	
		IGrid<Rack> floorPlan = SimpleGridImpl.emptyRactanlge(
											20, 10, GridCell.at(0, 0));
		IWarehouse warehouse  = createWarehouse(floorPlan);
		
		// Try to add a robot at a location that is not on the grid
		warehouse.addRobot(GridCell.at(-3, 112));
	}
	
	
	@Test(expected=IllegalArgumentException.class)
	public void cannotHaveTwoRobotsInTheSameLocation() throws Exception {	
		IGrid<Rack> floorPlan = SimpleGridImpl.emptyRactanlge(
											20, 10, GridCell.at(0, 0));
		IWarehouse warehouse  = createWarehouse(floorPlan);
		
		// Choose a random location on the grid
		GridCell location = GridCell.at(randomInt(0, 20), randomInt(0, 10));
		warehouse.addRobot(location);
		// Try to add another robot at the same location ...
		warehouse.addRobot(location);
	}
	
	
	
	@Test
	public void addMultipleRobotsThenTestGetRobots() throws Exception {	
		IGrid<Rack> floorPlan = SimpleGridImpl.emptyRactanlge(
											23, 17, GridCell.at(0, 0));
		IWarehouse warehouse  = createWarehouse(floorPlan);
		
		
		// Add a few robots at a few arbitrary locations on the grid
		Set<IGridRobot> expectedRobots = new HashSet<IGridRobot>();
		int n = 5;
		for (int i = 0; i < n; i += 10) {
			GridCell location = GridCell.at(i % 23, i % 17);
			expectedRobots.add(warehouse.addRobot(location));
		}
		
		Set<IGridRobot> actualRobots = new HashSet<IGridRobot>();
		Iterator<IGridRobot> robots = warehouse.getRobots();
		while (robots.hasNext()) {
			actualRobots.add(robots.next());
			
		}
		
		assertEquals(expectedRobots, actualRobots);
	}
	
	
	
	
	
	
	
	
}
