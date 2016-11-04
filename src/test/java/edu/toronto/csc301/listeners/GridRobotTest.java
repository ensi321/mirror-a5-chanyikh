package edu.toronto.csc301.listeners;

import static edu.toronto.csc301.util.TestUtil.createGridRobot;
import static edu.toronto.csc301.util.TestUtil.randomCell;
import static edu.toronto.csc301.util.TestUtil.randomDirection;
import static edu.toronto.csc301.robot.GridRobot.oneCellOver;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import edu.toronto.csc301.grid.GridCell;
import edu.toronto.csc301.robot.IGridRobot;
import edu.toronto.csc301.robot.IGridRobot.Direction;


public class GridRobotTest {


	// ======================== Helper Classes ==================================


	/**
	 * Helper class that listens to an IGridRobot, and keeps a log of 
	 * every time it was called.
	 */
	public static class StepEventLogger implements IGridRobot.StepListener {

		List<StepEvent> log = new ArrayList<StepEvent>();

		public List<StepEvent> getEventLog() {
			return Collections.unmodifiableList(log);
		}

		@Override
		public void onStepStart(IGridRobot robot, Direction direction) {
			log.add(new StepEvent(StepEvent.EventType.STEP_START, robot, direction));
		}

		@Override
		public void onStepEnd(IGridRobot robot, Direction direction) {
			log.add(new StepEvent(StepEvent.EventType.STEP_END, robot, direction));
		}
	}


	private static class StepEvent {

		public static enum EventType { STEP_START, STEP_END };

		public final EventType type;
		public final IGridRobot robot;
		public final GridCell robotLocation;
		public final Direction direction;

		public StepEvent(EventType type, IGridRobot robot, Direction direction) {
			this.type = type;
			this.robot = robot;
			this.robotLocation = robot.getLocation();
			this.direction = direction;
		}
	}


	// ========================================================================


	@Test
	public void listenerGetsCalledOnStepStartAndStepEnd() throws Exception {
		// Create a robot
		GridCell initialLocation = randomCell();
		IGridRobot robot = createGridRobot(initialLocation);

		// Attach a listener
		StepEventLogger eventLogger = new StepEventLogger();
		robot.startListening(eventLogger);

		// Make a step
		Direction stepDirection = randomDirection();
		robot.step(stepDirection);

		// Verify that the listener was called with the expected arguments ...
		List<StepEvent> eventLog = eventLogger.getEventLog();

		assertEquals(2, eventLog.size());

		StepEvent e = eventLog.get(0);
		assertEquals(StepEvent.EventType.STEP_START, e.type);
		assertEquals(robot, e.robot);
		assertEquals(initialLocation, e.robotLocation);
		assertEquals(stepDirection, e.direction);

		e = eventLog.get(1);
		assertEquals(StepEvent.EventType.STEP_END, e.type);
		assertEquals(robot, e.robot);
		assertEquals(oneCellOver(initialLocation, stepDirection), e.robotLocation);
		assertEquals(stepDirection, e.direction);
	}



	@Test
	public void testMultipleStepListeners() throws Exception {
		GridCell initialLocation = randomCell();
		IGridRobot robot = createGridRobot(initialLocation);

		// Attach multiple listeners ...
		Collection<StepEventLogger> listeners = new ArrayList<StepEventLogger>();
		int n = 5; // arbitrary
		for (int i = 0; i < n; i++) {
			StepEventLogger eventLogger = new StepEventLogger();
			robot.startListening(eventLogger);
			listeners.add(eventLogger);
		}

		// Make a step
		Direction stepDirection = randomDirection();
		robot.step(stepDirection);

		// Verify that all listeners were called with the expected arguments ...
		for(StepEventLogger listener : listeners){
			List<StepEvent> eventLog = listener.getEventLog();
			
			assertEquals(2, eventLog.size());

			StepEvent e = eventLog.get(0);
			assertEquals(StepEvent.EventType.STEP_START, e.type);
			assertEquals(robot, e.robot);
			assertEquals(initialLocation, e.robotLocation);
			assertEquals(stepDirection, e.direction);

			e = eventLog.get(1);
			assertEquals(StepEvent.EventType.STEP_END, e.type);
			assertEquals(robot, e.robot);
			assertEquals(oneCellOver(initialLocation, stepDirection), e.robotLocation);
			assertEquals(stepDirection, e.direction);
		}
				
	}




	@Test
	public void testStepListenerWithMultipleSteps() throws Exception {
		GridCell initialLocation = randomCell();
		IGridRobot robot = createGridRobot(initialLocation);

		StepEventLogger eventLogger = new StepEventLogger();
		robot.startListening(eventLogger);

		// Take a couple of steps ...
		Direction direction1 = randomDirection();
		robot.step(direction1);
		Direction direction2 = randomDirection();
		robot.step(direction2);

		// Check the event log to make sure the listener was called properly ...
		List<StepEvent> eventLog = eventLogger.getEventLog();

		assertEquals(4, eventLog.size());

		StepEvent e = eventLog.get(0);
		assertEquals(StepEvent.EventType.STEP_START, e.type);
		assertEquals(robot, e.robot);
		assertEquals(initialLocation, e.robotLocation);
		assertEquals(direction1, e.direction);

		e = eventLog.get(1);
		assertEquals(StepEvent.EventType.STEP_END, e.type);
		assertEquals(robot, e.robot);
		assertEquals(oneCellOver(initialLocation, direction1), e.robotLocation);
		assertEquals(direction1, e.direction);

		e = eventLog.get(2);
		assertEquals(StepEvent.EventType.STEP_START, e.type);
		assertEquals(robot, e.robot);
		assertEquals(oneCellOver(initialLocation, direction1), e.robotLocation);
		assertEquals(direction2, e.direction);

		e = eventLog.get(3);
		GridCell expectedLocation = oneCellOver(
				oneCellOver(initialLocation, direction1), 
				direction2);
		assertEquals(StepEvent.EventType.STEP_END, e.type);
		assertEquals(robot, e.robot);
		assertEquals(expectedLocation, e.robotLocation);
		assertEquals(direction2, e.direction);
	}


	

	@Test
	public void testStopListening() throws Exception {
		GridCell initialLocation = randomCell();
		IGridRobot robot = createGridRobot(initialLocation);
		StepEventLogger eventLogger = new StepEventLogger();
		
		// Start listening
		robot.startListening(eventLogger);

		// Take a step
		Direction stepDirection = randomDirection();
		robot.step(stepDirection);

		// Stop listening, then take a few more steps 
		robot.stopListening(eventLogger);
		int n = 10; // arbitrary
		for (int i = 0; i < n; i++) {
			robot.step(randomDirection());
		}
		
		// Verify that only the first step triggered the listener ...
		List<StepEvent> eventLog = eventLogger.getEventLog();

		assertEquals(2, eventLog.size());

		StepEvent e = eventLog.get(0);
		assertEquals(StepEvent.EventType.STEP_START, e.type);
		assertEquals(robot, e.robot);
		assertEquals(initialLocation, e.robotLocation);
		assertEquals(stepDirection, e.direction);

		e = eventLog.get(1);
		assertEquals(StepEvent.EventType.STEP_END, e.type);
		assertEquals(robot, e.robot);
		assertEquals(oneCellOver(initialLocation, stepDirection), e.robotLocation);
		assertEquals(stepDirection, e.direction);
	}
}
