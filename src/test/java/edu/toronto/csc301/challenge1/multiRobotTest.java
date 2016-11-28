package edu.toronto.csc301.challenge1;

import static edu.toronto.csc301.util.TestUtil.createPathPlanner;

import static edu.toronto.csc301.util.TestUtil.createWarehouse;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
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
	public void twoGoalstwoRobotsParallel() throws Exception{
		IGridRobot robot1 = warehouse.addRobot(GridCell.at(2, 3));
		IGridRobot robot2 = warehouse.addRobot(GridCell.at(10, 11));
		Map<IGridRobot, GridCell> robot2dest = new HashMap<IGridRobot,GridCell>();
		robot2dest.put(robot1, GridCell.at(1, 1));
		robot2dest.put(robot2, GridCell.at(19, 15));
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
	
		
		assertEquals(1, 1);
	}
	@Test
	public void twoGoalstwoRobots() throws Exception{
		IGridRobot robot1 = warehouse.addRobot(GridCell.at(2, 3));
		IGridRobot robot2 = warehouse.addRobot(GridCell.at(10, 11));
		Map<IGridRobot, GridCell> robot2dest = new HashMap<IGridRobot,GridCell>();
		robot2dest.put(robot1, GridCell.at(1, 1));
		robot2dest.put(robot2, GridCell.at(19, 15));		
		Entry<IGridRobot, Direction> nextStep = null;

		while (true){
			nextStep = pathPlanner.nextStep(warehouse, robot2dest);
			if (nextStep == null){
				break;
			}

			IGridRobot r = nextStep.getKey();
			System.out.println("==========================");
			System.out.println(r.toString());
			System.out.println(nextStep.getValue());
			r.step(nextStep.getValue());

		}
		
		assertEquals(1, 1);
	}
	@Test
	public void twoRobotsInterveneEachOther() throws Exception{
		IGridRobot robot1 = warehouse.addRobot(GridCell.at(0, 0));
		IGridRobot robot2 = warehouse.addRobot(GridCell.at(19, 0));
		Map<IGridRobot, GridCell> robot2dest = new HashMap<IGridRobot,GridCell>();
		robot2dest.put(robot1, GridCell.at(19, 19));
		robot2dest.put(robot2, GridCell.at(0, 19));	
		Entry<IGridRobot, Direction> nextStep = null;
		
		while (true){
			nextStep = pathPlanner.nextStep(warehouse, robot2dest);
			if (nextStep == null){
				break;
			}

			IGridRobot r = nextStep.getKey();
			System.out.println("==========================");
			System.out.println(r.toString());
			System.out.println(nextStep.getValue());
			r.step(nextStep.getValue());

		}
		
		assertEquals(1, 1);
	}
	
	@Test
	public void twoRobotsInterveneEachOtherParallel() throws Exception{
		IGridRobot robot1 = warehouse.addRobot(GridCell.at(0, 0));
		IGridRobot robot2 = warehouse.addRobot(GridCell.at(19, 0));
		Map<IGridRobot, GridCell> robot2dest = new HashMap<IGridRobot,GridCell>();
		robot2dest.put(robot1, GridCell.at(19, 19));
		robot2dest.put(robot2, GridCell.at(0, 19));	
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
	
		
		assertEquals(1, 1);
	}
}