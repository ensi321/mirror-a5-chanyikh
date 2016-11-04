package edu.toronto.csc301.robot;

import edu.toronto.csc301.grid.GridCell;


public interface IGridRobot {
	
	public static enum Direction {NORTH, EAST, SOUTH, WEST};

	// ------------------------------------------------------------------------
	
	public GridCell getLocation();
	public void step(Direction direction);

	// ------------------------------------------------------------------------
	
	interface StepListener {
		void onStepStart(IGridRobot robot, Direction direction);
		void onStepEnd(IGridRobot robot, Direction direction);
	}

	public void startListening(IGridRobot.StepListener listener);
	public void stopListening(IGridRobot.StepListener listener);
	
}
