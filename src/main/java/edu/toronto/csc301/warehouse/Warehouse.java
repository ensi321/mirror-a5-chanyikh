package edu.toronto.csc301.warehouse;

import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;

import edu.toronto.csc301.grid.GridCell;
import edu.toronto.csc301.grid.IGrid;
import edu.toronto.csc301.robot.IGridRobot;
import edu.toronto.csc301.robot.IGridRobot.Direction;

public class Warehouse implements IWarehouse, IGridRobot.StepListener {
	
	/**
	 * TODO: Complete the implementation of this class.
	 * (you can probably use your implementation from A4) 
	 */

	
	
	public Warehouse(IGrid<Rack> floorPlan) {
		// TODO Auto-generated constructor stub
	}
	
	

	@Override
	public void onStepStart(IGridRobot robot, Direction direction) {
		// TODO Auto-generated method stub		
	}

	@Override
	public void onStepEnd(IGridRobot robot, Direction direction) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IGrid<Rack> getFloorPlan() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IGridRobot addRobot(GridCell initialLocation) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<IGridRobot> getRobots() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<IGridRobot, Direction> getRobotsInMotion() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void subscribe(Consumer<IWarehouse> observer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unsubscribe(Consumer<IWarehouse> observer) {
		// TODO Auto-generated method stub
		
	}

	
}
