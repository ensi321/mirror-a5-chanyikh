package edu.toronto.csc301.warehouse;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import edu.toronto.csc301.grid.GridCell;
import edu.toronto.csc301.grid.IGrid;
import edu.toronto.csc301.robot.GridRobot;
import edu.toronto.csc301.robot.IGridRobot;
import edu.toronto.csc301.robot.IGridRobot.Direction;

public class Warehouse implements IWarehouse, IGridRobot.StepListener {
	
	IGrid<Rack> floorPlan;
	// List of robots in warehouse
	List<IGridRobot> robots = new ArrayList<IGridRobot>();
	
	// Map of robots to its direction of last step
	Map<IGridRobot, IGridRobot.Direction> robot_directions = new HashMap<IGridRobot, IGridRobot.Direction>();
	
	// List of listeners of this warehouse
	private List<Consumer<IWarehouse>> warehouse_listeners = new ArrayList<Consumer<IWarehouse>>();

	
	public Warehouse(IGrid<Rack> grid){
		if (grid == null){
			throw new NullPointerException();
		}
		this.floorPlan = grid;
	}
	@Override
	public IGrid<Rack> getFloorPlan() {
		return this.floorPlan;
	}

	@Override
	public IGridRobot addRobot(GridCell initialLocation) {		
		// Check if this cell exists
		if (!this.floorPlan.hasCell(initialLocation)){
			throw new IllegalArgumentException();
		}
		// Check if robot is in this cell already
		for (IGridRobot current_robot : this.robots){
			if (current_robot.getLocation() == initialLocation){
				throw new IllegalArgumentException();
			}
		}

		IGridRobot new_robot = (IGridRobot) new GridRobot(initialLocation);
		this.robots.add(new_robot);
		// Trigger the warehouse_listeners
		for (Consumer<IWarehouse> l : warehouse_listeners){
			l.accept(this);
		}
		// Listen to this robot
		new_robot.startListening(this);
		return new_robot;
	}

	@Override
	public Iterator<IGridRobot> getRobots() {
		Iterator<IGridRobot> result = this.robots.iterator();
		
		return result;
	}

	@Override
	public Map<IGridRobot, Direction> getRobotsInMotion() {
		Map<IGridRobot, Direction> result = new HashMap<IGridRobot, Direction>(robot_directions);
		
		return result;
	}

	@Override
	public void subscribe(Consumer<IWarehouse> observer) {
		warehouse_listeners.add(observer);
	}

	@Override
	public void unsubscribe(Consumer<IWarehouse> observer) {
		warehouse_listeners.remove(observer);
	}
	@Override
	public void onStepStart(IGridRobot robot, Direction direction) {
		robot_directions.put(robot, direction);
		for (Consumer<IWarehouse> l : warehouse_listeners){
			l.accept(Warehouse.this);
		}
	}
	@Override
	public void onStepEnd(IGridRobot robot, Direction direction) {
		robot_directions.remove(robot);
		
		for (Consumer<IWarehouse> l : warehouse_listeners){
			l.accept(Warehouse.this);
		}
	}

	
}
