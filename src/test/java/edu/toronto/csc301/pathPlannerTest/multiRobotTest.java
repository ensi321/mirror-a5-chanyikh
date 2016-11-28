package edu.toronto.csc301.pathPlannerTest;

import static edu.toronto.csc301.util.TestUtil.createPathPlanner;

import static edu.toronto.csc301.util.TestUtil.createWarehouse;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;

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
import edu.toronto.csc301.warehouse.RobotMover;
import edu.toronto.csc301.warehouse.WaitForOthersException;
import edu.toronto.csc301.warehouse.nextStepNotFoundException;

public class multiRobotTest{
	
	@Rule
    public Timeout globalTimeout = Timeout.seconds(20);
	
	
	
	// ------------------------------------------------------------------------
	
	
	
	private IPathPlanner pathPlanner;
	private IWarehouse warehouse;
	
	@Before
	public void setup() throws Exception{
		warehouse = createWarehouse(
				SimpleGridImpl.emptyRactanlge(10, 10, GridCell.at(0, 0)));
		pathPlanner = createPathPlanner(warehouse, null);
	}
	
	@After
	public void tearDown(){
		pathPlanner = null;
		warehouse = null;
	}
	
	
	// ------------------------------------------------------------------------
	@Test(expected=IllegalArgumentException.class)
	public void twoRobotsWithSameDest() throws Exception{
		IGridRobot robot1 = warehouse.addRobot(GridCell.at(3, 3));
		IGridRobot robot2 = warehouse.addRobot(GridCell.at(8, 6));
		Map<IGridRobot, GridCell> robot2dest = new HashMap<IGridRobot,GridCell>();
		robot2dest.put(robot1, GridCell.at(1, 1));
		robot2dest.put(robot2, GridCell.at(1, 1));		
		
		singleThreadedPathPlannerTest(robot2dest);
	}
	@Test(expected=IllegalArgumentException.class)
	public void robotsWithDestOutOfBound() throws Exception{
		IGridRobot robot1 = warehouse.addRobot(GridCell.at(3, 3));
		IGridRobot robot2 = warehouse.addRobot(GridCell.at(8, 6));
		Map<IGridRobot, GridCell> robot2dest = new HashMap<IGridRobot,GridCell>();
		robot2dest.put(robot1, GridCell.at(10, 20));
		robot2dest.put(robot2, GridCell.at(49, 13));		
		
		singleThreadedPathPlannerTest(robot2dest);
	}
	
	// This test looks like this:
	//	+--+--+--+--+--+--+--+--+--+--+
	//	|  |  |  |  |  |  |  |  |d2|  |
	//	+--+--+--+--+--+--+--+--+--+--+
	//	|  |  |  |  |  |  |  |  |  |  |
	//	+--+--+--+--+--+--+--+--+--+--+
	//	|  |  |  |  |  |  |  |  |  |  |
	//	+--+--+--+--+--+--+--+--+--+--+
	//	|  |  |  |  |  |  |  |  |R2|  |
	//	+--+--+--+--+--+--+--+--+--+--+
	//	|  |  |  |  |  |  |  |  |  |  |
	//	+--+--+--+--+--+--+--+--+--+--+
	//	|  |  |  |  |  |  |  |  |  |  |
	//	+--+--+--+--+--+--+--+--+--+--+
	//	|  |  |  |R1|  |  |  |  |  |  |
	//	+--+--+--+--+--+--+--+--+--+--+
	//	|  |  |  |  |  |  |  |  |  |  |
	//	+--+--+--+--+--+--+--+--+--+--+
	//	|  |d1|  |  |  |  |  |  |  |  |
	//	+--+--+--+--+--+--+--+--+--+--+
	//	|  |  |  |  |  |  |  |  |  |  |
	//	+--+--+--+--+--+--+--+--+--+--+

	@Test
	public void twoGoalstwoRobots() throws Exception{
		IGridRobot robot1 = warehouse.addRobot(GridCell.at(3, 3));
		IGridRobot robot2 = warehouse.addRobot(GridCell.at(8, 6));
		
		Map<IGridRobot, GridCell> robot2dest = new HashMap<IGridRobot,GridCell>();
		robot2dest.put(robot1, GridCell.at(1, 1));
		robot2dest.put(robot2, GridCell.at(8, 9));		
		
		singleThreadedPathPlannerTest(robot2dest);
	}
	// Same as twoGoalstwoRobots, but multi-threaded
	@Test
	public void twoGoalstwoRobotsParallel() throws Exception{
		IGridRobot robot1 = warehouse.addRobot(GridCell.at(3, 3));
		IGridRobot robot2 = warehouse.addRobot(GridCell.at(8, 6));
		Map<IGridRobot, GridCell> robot2dest = new HashMap<IGridRobot,GridCell>();
		robot2dest.put(robot1, GridCell.at(1, 1));
		robot2dest.put(robot2, GridCell.at(8, 9));
		
		multiThreadedPathPlannerTest(robot2dest);
	}
	// This case is interesting
	// Under one-threaded pathplanner, this is not a difficult case
	// But under multi-threaded, this case requires extra effort
	//	+--+--+--+--+--+--+--+--+--+--+
	//	|d2|  |  |  |  |  |  |  |  |d1|
	//	+--+--+--+--+--+--+--+--+--+--+
	//	|  |  |  |  |  |  |  |  |  |  |
	//	+--+--+--+--+--+--+--+--+--+--+
	//	|  |  |  |  |  |  |  |  |  |  |
	//	+--+--+--+--+--+--+--+--+--+--+
	//	|  |  |  |  |  |  |  |  |  |  |
	//	+--+--+--+--+--+--+--+--+--+--+
	//	|  |  |  |  |  |  |  |  |  |  |
	//	+--+--+--+--+--+--+--+--+--+--+
	//	|  |  |  |  |  |  |  |  |  |  |
	//	+--+--+--+--+--+--+--+--+--+--+
	//	|  |  |  |  |  |  |  |  |  |  |
	//	+--+--+--+--+--+--+--+--+--+--+
	//	|  |  |  |  |  |  |  |  |  |  |
	//	+--+--+--+--+--+--+--+--+--+--+
	//	|  |  |  |  |  |  |  |  |  |  |
	//	+--+--+--+--+--+--+--+--+--+--+
	//	|R1|  |  |  |  |  |  |  |  |R2|
	//	+--+--+--+--+--+--+--+--+--+--+
	@Test
	public void twoRobotsInterfereEachOther() throws Exception{
		IGridRobot robot1 = warehouse.addRobot(GridCell.at(0, 0));
		IGridRobot robot2 = warehouse.addRobot(GridCell.at(9, 0));
		Map<IGridRobot, GridCell> robot2dest = new HashMap<IGridRobot,GridCell>();
		robot2dest.put(robot1, GridCell.at(9, 9));
		robot2dest.put(robot2, GridCell.at(0, 9));
		
		singleThreadedPathPlannerTest(robot2dest);

	}
	
	@Test
	public void twoRobotsInterfereEachOtherParallel() throws Exception{
		IGridRobot robot1 = warehouse.addRobot(GridCell.at(0, 0));
		IGridRobot robot2 = warehouse.addRobot(GridCell.at(9, 0));
		Map<IGridRobot, GridCell> robot2dest = new HashMap<IGridRobot,GridCell>();
		robot2dest.put(robot1, GridCell.at(9, 9));
		robot2dest.put(robot2, GridCell.at(0, 9));	
		
		multiThreadedPathPlannerTest(robot2dest);

	}
	//	+-------+--+--+--+--+--+--+--+--+--+
	//	|       |  |  |  |  |  |  |  |  |  |
	//	+-------+--+--+--+--+--+--+--+--+--+
	//	|       |  |  |  |  |  |  |  |  |  |
	//	+-------+--+--+--+--+--+--+--+--+--+
	//	|       |  |  |  |  |  |  |  |  |  |
	//	+-------+--+--+--+--+--+--+--+--+--+
	//	|       |  |  |  |  |  |  |  |  |  |
	//	+-------+--+--+--+--+--+--+--+--+--+
	//	|       |  |  |  |  |  |  |  |  |  |
	//	+-------+--+--+--+--+--+--+--+--+--+
	//	|       |  |  |  |  |  |  |  |  |  |
	//	+-------+--+--+--+--+--+--+--+--+--+
	//	|       |  |  |  |  |  |  |  |  |  |
	//	+-------+--+--+--+--+--+--+--+--+--+
	//	|       |  |  |  |  |  |  |  |  |  |
	//	+-------+--+--+--+--+--+--+--+--+--+
	//	| R2/d1 |  |  |  |  |  |  |  |  |  |
	//	+-------+--+--+--+--+--+--+--+--+--+
	//	| R1/d2 |  |  |  |  |  |  |  |  |  |
	//	+-------+--+--+--+--+--+--+--+--+--+
	@Test
	public void twoRobotsInterfereEachOther2() throws Exception{
		IGridRobot robot1 = warehouse.addRobot(GridCell.at(0, 0));
		IGridRobot robot2 = warehouse.addRobot(GridCell.at(0, 1));
		Map<IGridRobot, GridCell> robot2dest = new HashMap<IGridRobot,GridCell>();
		robot2dest.put(robot1, GridCell.at(0, 1));
		robot2dest.put(robot2, GridCell.at(0, 0));
		
		singleThreadedPathPlannerTest(robot2dest);

	}
	
	@Test
	public void twoRobotsInterfereEachOtherParallel2() throws Exception{
		IGridRobot robot1 = warehouse.addRobot(GridCell.at(0, 0));
		IGridRobot robot2 = warehouse.addRobot(GridCell.at(0, 1));
		Map<IGridRobot, GridCell> robot2dest = new HashMap<IGridRobot,GridCell>();
		robot2dest.put(robot1, GridCell.at(0, 1));
		robot2dest.put(robot2, GridCell.at(0, 0));	
		
		multiThreadedPathPlannerTest(robot2dest);

	}
	//	+-------+-------+
	//	|  d3   | R3/d2 |
	//	+-------+-------+
	//	|  R1   | R2/d1 |
	//	+-------+-------+
	// This should be fail because the algorithm will only put a robot to idle for one round only
	@Test
	public void threeRobotsCycle() throws Exception{
		warehouse = createWarehouse(
				SimpleGridImpl.emptyRactanlge(2, 2, GridCell.at(0, 0)));
		pathPlanner = createPathPlanner(warehouse, null);
		
		IGridRobot robot1 = warehouse.addRobot(GridCell.at(0, 0));
		IGridRobot robot2 = warehouse.addRobot(GridCell.at(1, 0));
		IGridRobot robot3 = warehouse.addRobot(GridCell.at(1, 1));
		Map<IGridRobot, GridCell> robot2dest = new HashMap<IGridRobot,GridCell>();
		robot2dest.put(robot1, GridCell.at(1, 0));
		robot2dest.put(robot2, GridCell.at(1, 1));
		robot2dest.put(robot3, GridCell.at(0, 1));
		singleThreadedPathPlannerTest(robot2dest);
	}
	@Test
	public void threeRobotsCycleParallel() throws Exception{
		warehouse = createWarehouse(
				SimpleGridImpl.emptyRactanlge(2, 2, GridCell.at(0, 0)));
		pathPlanner = createPathPlanner(warehouse, null);
		
		IGridRobot robot1 = warehouse.addRobot(GridCell.at(0, 0));
		IGridRobot robot2 = warehouse.addRobot(GridCell.at(1, 0));
		IGridRobot robot3 = warehouse.addRobot(GridCell.at(1, 1));
		Map<IGridRobot, GridCell> robot2dest = new HashMap<IGridRobot,GridCell>();
		robot2dest.put(robot1, GridCell.at(1, 0));
		robot2dest.put(robot2, GridCell.at(1, 1));
		robot2dest.put(robot3, GridCell.at(0, 1));
		multiThreadedPathPlannerTest(robot2dest);
	}
	
	
	// ===========================
	// Helper function
	public void singleThreadedPathPlannerTest(Map<IGridRobot, GridCell> robot2dest) throws Exception{
		Entry<IGridRobot, Direction> nextStep = null;
		
		while (true){
			try {
				nextStep = pathPlanner.nextStep(warehouse, robot2dest);
				if (nextStep == null){
					break;
				}

				IGridRobot r = nextStep.getKey();
				System.out.println("==========================");
				System.out.println(r.toString());
				System.out.println(nextStep.getValue());
				r.step(nextStep.getValue());
			}catch (WaitForOthersException e){
				continue;
			}

		}
		Iterator<IGridRobot> r_iter = warehouse.getRobots();
		IGridRobot r;
		// Verify that every robot in warehouse has arrived it's destination
		while (r_iter.hasNext()){
			r = r_iter.next();
			if (r.getLocation().equals(robot2dest.get(r))){
				continue;
			}
			fail();
		}
		return;
	}
	
	public void multiThreadedPathPlannerTest(Map<IGridRobot, GridCell> robot2dest) throws Exception{
		List<Thread> threads = new ArrayList<Thread>();		
		Entry<IGridRobot, Direction> nextStep = null;

		while (true){
			try{
				nextStep = pathPlanner.nextStep(warehouse, robot2dest);
				if (nextStep == null){
					break;
				}
			}catch(WaitForOthersException e){
				continue;
			}
			IGridRobot r = nextStep.getKey();
			System.out.println("==========================");
			System.out.println(r.toString());
			System.out.println(nextStep.getValue());
			RobotMover R = new RobotMover(nextStep, pathPlanner);
			Thread t = new Thread(R);
			pathPlanner.lock(nextStep.getKey(), nextStep.getValue());
			t.start();
			threads.add(t);

		}
		int runningThreads = 0;
		while (true){
			runningThreads = 0;
			for (int i = 0; i < threads.size(); i++){
				Thread t = threads.get(i);
				if(t.isAlive()){
					runningThreads ++;
				}
			}
			if (runningThreads == 0){
				break;
			}
		}
	
		Iterator<IGridRobot> r_iter = warehouse.getRobots();
		IGridRobot r;
		// Verify that every robot in warehouse has arrived it's destination
		while (r_iter.hasNext()){
			r = r_iter.next();
			if (r.getLocation().equals(robot2dest.get(r))){
				continue;
			}
			else {
				System.out.println("Suppose to be: "+ robot2dest.get(r) + " but got "+ r.getLocation());
			}
			fail();
		}
		return;
	}
}