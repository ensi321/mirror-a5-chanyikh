package edu.toronto.csc301.warehouse;

import java.util.AbstractMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Map.Entry;
import java.util.function.Consumer;

import edu.toronto.csc301.grid.GridCell;
import edu.toronto.csc301.robot.IGridRobot;
import edu.toronto.csc301.robot.IGridRobot.Direction;
import edu.toronto.csc301.warehouse.PathPlanner.Node;
import edu.toronto.csc301.warehouse.util.*;

public class PathPlanner implements IPathPlanner, Consumer<IWarehouse> {

		
	private IWarehouse house;
	private List<GridCell> goals;
	private List<IGridRobot>robots = new ArrayList<IGridRobot>();
	
	// Current BFS stack. Function: Add new node to the stack, pop the first node in stack and expand etc.
	private Queue<Node> BFS_stack = new LinkedList<Node>();
	// Keep track of all gridcells that the robot has been visited, to prevent cycle
	private List<GridCell> node_visited = new ArrayList<GridCell>();
	public PathPlanner(IWarehouse house, List<GridCell> goals){
		this.house = house;
		if (goals != null){
			this.goals = goals;
		}
		else {
			this.goals = new ArrayList<GridCell>();
		}
		Iterator<IGridRobot> robot_iterator = house.getRobots();
		while (robot_iterator.hasNext()){
			this.robots.add(robot_iterator.next());
		}
		// Subscribe to warehouse
		this.house.subscribe(this);
	}
	
	// This is node of the tree
	public class Node{
		
		Node parent;
		GridCell cell;
		List<Node> children = new ArrayList<Node>();
		public Node(GridCell location, Node p){
			this.parent = p;
			this.cell = location;
		}
		public Node getParent(){
			return this.parent;
		}
		public GridCell getLocation(){
			return this.cell;
		}
		public void addChild(Node c){
			children.add(c);
			return;
		}
		public List<Node> getChildren(){
			return this.children;
		}
		
	}
	

	public Direction BFS(Node root, GridCell dest){
		Node current_node = new Node(null, null);
		while (BFS_stack.size() != 0){
			// Pop the first node in the stack
			current_node = BFS_stack.poll();
			// Check if this node is our dest
			if (dest.equals(current_node.getLocation())){
				break;
			}
			
			// Expand it and insert all its children into the stack
			List<Node> children = findChildren(current_node);
			// Add all children into the stack
			for (Node child : children){
				BFS_stack.add(child);
			}

			// Add current_node into node_visited
			node_visited.add(current_node.getLocation());
		}
		
		// Return error if we cant find anything
		if (current_node.getParent() == null){
			throw new IllegalArgumentException();
		}
		
		// Start doing a backtrace
		// Trace back until a node's parent is root
		while (current_node.getParent() != root){
			current_node = current_node.getParent();

		}
		// Now we have current_node which its parent is root
		// Find the direction
		GridCell root_location = root.getLocation();
		GridCell next_step_location = current_node.getLocation();
		int root_x = root_location.x;
		int root_y = root_location.y;
		int next_x = next_step_location.x;
		int next_y = next_step_location.y;
		Direction result = null;
		if (next_x == root_x + 1){
			result = Direction.EAST;
		}
		else if (next_x == root_x - 1){
			result = Direction.WEST;
		}
		else if (next_y == root_y + 1){
			result = Direction.NORTH;
		}
		else if (next_y == root_y - 1){
			result = Direction.SOUTH;
		}
		
		return result;
		
	}

	// Check if cell goes out of grid, or if cell has another robot on it
	// Return true if cell is illegal
	public boolean checkIllegalCell(GridCell cell){
		// If floor plan does not have this cell
		if (!this.house.getFloorPlan().hasCell(cell)){
			return true;
		}
		// Check if robot is on that cell
		Iterator<IGridRobot> robots = this.house.getRobots();
		while (robots.hasNext()){
			IGridRobot current_robot = robots.next();
			if	(cell.equals(current_robot.getLocation())){
				return true;
			}
		}
		// Check if we are trap in cycle
		if (node_visited.contains(cell)){
			return true;
		}
		
		return false;
	}
	
	
	// Return a list of children nodes given a node
	public List<Node> findChildren(Node current){
		List<Node> result = new ArrayList<Node>();
		List<GridCell> possibleLocation = new ArrayList<GridCell>();
		GridCell node_location = current.getLocation();
		int x = node_location.x;
		int y = node_location.y;
		// In this order: Up left right down
		possibleLocation.add(GridCell.at(x, y + 1));
		possibleLocation.add(GridCell.at(x - 1, y));
		possibleLocation.add(GridCell.at(x + 1, y));		
		possibleLocation.add(GridCell.at(x, y - 1));

		for (int i = 0; i < possibleLocation.size(); i++){
			GridCell location = possibleLocation.get(i);
			if (checkIllegalCell(location)){
				possibleLocation.set(i, null);
			}
		}
		

		for (GridCell location : possibleLocation){
			if (location != null) {
				Node new_node = new Node(location, current);
				result.add(new_node);
			}
		}
		
		return result;
	}
	
	// Debug function, print out stuff inside node
	public void printNode(Node n){
		String s = "Location: " + n.getLocation().toString(); 
		System.out.println(s);
	}
	
	public void printStack(){
		String s = "BFS_stack: ";
		for (Node n : BFS_stack){
			s = s + n.getLocation() + " ";
		}
		System.out.println(s);
	}

	@Override
	public Entry<IGridRobot, Direction> nextStep() {
		// This is assignment of robot to dest 
		Map<IGridRobot, GridCell> robot2dest = assignGoal();
		// Find the robot:dest pair that has the lowest cost but not already at goal
		Iterator<Entry<IGridRobot, GridCell>> iter = robot2dest.entrySet().iterator();
		Entry<IGridRobot, GridCell> entry = null;
		double min_cost = Double.POSITIVE_INFINITY;
		while (iter.hasNext()){
			Entry<IGridRobot, GridCell> current_entry = iter.next();
			double current_cost = hamiltonDistance(current_entry.getKey(), current_entry.getValue());
			if (current_cost < min_cost && current_cost > 0){
				entry = current_entry;
				min_cost = current_cost;
			}
		}
		// If cost of all robot:dest pair are 0, this means we are done
		if (entry == null){
			return null;
		}
		IGridRobot robot = entry.getKey();
		GridCell dest = entry.getValue();
		// Add root to the stack
		Node root = new Node(robot.getLocation(), null);
		BFS_stack.add(root);
		Direction direction = BFS(root, dest);
		Entry<IGridRobot, Direction> result = new AbstractMap.SimpleEntry<IGridRobot, Direction>(robot, direction);
		
		BFS_stack = new LinkedList<Node>();
		node_visited = new ArrayList<GridCell>();
		
		return result;

	}

	@Override
	public void addGoal(GridCell goal) {
		this.goals.add(goal);
	}

	@Override
	public void removeGoal(GridCell goal) {
		this.goals.remove(goal);
		
	}

	// Assign each robot to a goal
	@Override
	public Map<IGridRobot, GridCell> assignGoal() {
		// Construct a cost matrix for hungarian algorithm
		double[][] cost = new double[robots.size()][goals.size()];
		for (int i = 0; i < robots.size(); i++){
			IGridRobot current_robot = robots.get(i);
			for (int j = 0; j < goals.size(); j++){
				GridCell current_goal = goals.get(j);
				cost[i][j] = hamiltonDistance(current_robot, current_goal);
			}
		}
		HungarianAlgorithm h = new HungarianAlgorithm(cost);
		int[] assignment = h.execute();
		Map<IGridRobot, GridCell> robot2dest = new HashMap<IGridRobot, GridCell>();
		for (int i = 0; i < assignment.length; i++){
			robot2dest.put(robots.get(i), goals.get(assignment[i]));
		}
		return robot2dest;
	}

	@Override
	public void updateRobot(IWarehouse warehouse) {
		this.robots = new ArrayList<IGridRobot>();
		Iterator<IGridRobot> robot_iterator = warehouse.getRobots();
		while (robot_iterator.hasNext()){
			this.robots.add(robot_iterator.next());
		}
		
	}

	// When there is any update to the house, update the robots
	@Override
	public void accept(IWarehouse t) {
		updateRobot(t);
		
	}
	// Given robot at (x1, y1) and cell at (x2, y2)
	// Return |x1 - x2| + |y1 - y2|
	public double hamiltonDistance(IGridRobot robot, GridCell cell){
		return Math.abs(robot.getLocation().x - cell.x) + Math.abs(robot.getLocation().y - cell.y);
	}
	
	public static void main(String [] args){
		  double [][] a = {{7, 10, 5}, {4, 8, 3}};
		  HungarianAlgorithm h = new HungarianAlgorithm(a);
		  int[] result = h.execute();
		  System.out.println(Arrays.toString(result));
	}
}
