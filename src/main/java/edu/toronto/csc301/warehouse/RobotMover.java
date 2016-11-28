package edu.toronto.csc301.warehouse;

import java.util.Arrays;
import java.util.Map.Entry;

import edu.toronto.csc301.robot.IGridRobot;
import edu.toronto.csc301.robot.IGridRobot.Direction;

public class RobotMover implements Runnable {
	   private IGridRobot robot;
	   private Direction direction;
	   private IPathPlanner pathPlanner;
	   
	   public RobotMover(Entry<IGridRobot, Direction> nextStep, IPathPlanner pathPlanner) {
		   this.direction = nextStep.getValue();
		   this.robot = nextStep.getKey();
		   this.pathPlanner = pathPlanner;
	   }
	   
	   public void run() {
		  System.out.println(robot.toString() + " is stepping " + direction);
		  robot.step(direction);
	      System.out.println(robot.toString() + " is done stepping " + direction);
	      pathPlanner.unlock(robot);
	   }
	   
}
