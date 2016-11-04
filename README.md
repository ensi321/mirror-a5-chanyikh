# Individual Final Demo

 * [Introduction](#introduction)
 * [Scope / Requirements](#scope--requirements)
 * [How to approach this assignment?](#how-to-approach-this-assignment)
 * [Logistics](#logistics)
 * [Evaluation](#evaluation)


<br /><br /><br />

## Introduction


In your previous individual assignments, you were given precise specifications (i.e. interfaces and unit tests) and were asked to write the code. 

This assignment is different in a number of ways:

 1. You are given English specifications, which are not precise and leave some room for interpretation.
 2. You will need to make these specification precise (i.e. define interfaces and create testing code).
 3. You will write the code according to your precise specifications.
 4. In addition to that, this assignment is NOT auto-marked. Instead, you will present your work to TA in person. 

The purpose of the in-person presentation is to give you a chance to demonstrate:

 * Solid understanding of object-oriented concepts,
 * Proper use of software engineering tools,
 * Your ability to translate English specifications to code,
 * And your ability to tackle a non-trivial problem.


The starter code for this assignment is very similar to the starter code you had for A4. 
Only this time, you are asked to go beyond what you did for A4 and handle two additional challenges.


### Challenge 1: Path-planning with multiple goals

In A4, we only tested your path planner with a single goal. That is, we only tried to get one robot to a destination. Although our A4 test coverage was limited, the [`IPathPlanner`](/src/main/java/edu/toronto/csc301/warehouse/IPathPlanner.java) interface was designed with multiple goals in mind. 

For this assignment, we would like your path planner implementation to support multiple goals.

### Challenge 2: Parallelizing path execution

In a realistic scenario, physical robots take time to move in space. 
Therefore, we have provided you with a [GridRobot](/src/main/java/edu/toronto/csc301/robot/GridRobot.java) implementation that 
simulates this behaviour (by sleeping for a little while every time someone calls the `step` method).

Your implementation should try to minimize the time it takes for all robots to reach their destination, by moving them in parallel. In other words, robots should not stand idle, if they can move towards their destination.

For this challenge, you will need to create a new component that (uses your path planner and) moves robots to their destinations.
  * In this context, *component* means: An interface, an implementation of that interface and any helper classes you might need.
  * The design of the API (names of interfaces/classes and method signatures) is up to you.


<br /><br /><br />

## Scope / Requirements


Your testing code should verify the correctness and efficiency of your code:

 * Correctness: 
   * All robots get to their destination, whenever it is possible.
   * Robots don’t crash into walls or into one another.
 * Efficiency:
   * Robots do not take unnecessarily long paths
   * Robots do not stand idle when they can move and get closer to their destination
   * _Note:_ We are not looking for optimality, we just want you to verify that your code is doing something reasonable.


Your testing code should cover (at least) the following cases:

 * Basic cases that:
   1. Show how your code is used.
   2. Prove that your implementation works (i.e. handles challenges 1 and/or 2) for simple cases.
 * Advanced cases that increase confidence in your system.
   * Cases where multiple robots move in the same area of the warehouse and possibly interfere with one another.
   * Cases that require complex path planning.
 * Tests that specify the behaviour in a few edge cases.
   * Invalid input (e.g. null checks, adding/moving robots to an invalid location, etc.)
   * Unachievable goals (i.e. it is impossible to get all robots to their destination)


Your implementation is expected to handle the basic cases, as well as (at least) two complex cases that are fundamentally different from one another.


 > If your test coverage is good, you might not be able to pass all (or even most) of your tests.        
   That’s totally OK. In fact, thinking of more complex cases and coding them makes your presentation much stronger. 
 >
 > The rationale is that having clear specifications with automated tests makes it easier to implement advanced behaviour in the future.


<br /><br /><br />

## How to approach this assignment?


Start early! And spread your work throughout the three weeks.


Before you start coding, decide what you want to demo:

 * Break the demo into 3 - 4 parts
   * 1 - 2 basic/simple scenarios, 2 advanced/complex scenarios.
   * Each part corresponds to a test case (or a set of test cases).
 * Remember that you are limited to 10-15 minutes
   * Choose the most important/interesting test cases.
   * Choose the most interesting part(s) of your code/design that you want to discuss.
   * Use visual aids (e.g. simple hand-drawn pictures) to describe test cases clearly and quickly.
   * Planning the demo is also a good way to narrow down the scope of your project - "If it’s not used in the demo, there is no point building it".


Be disciplined when going from English to code:

 * Describe your test cases in English, **before** you start coding.
 * __Write down__ the English description of your test cases.
 * Make sure you can explain your testing strategy in high-level.


Prepare for your presentation:

 * Write a script and pay attention to the timing.
 * Practice your presentation (with a friend, or by recording yourself).
 * Speak slowly to give the TA a chance to understand you code as well as what you are saying.
 * Stop frequently to verify that the TA understood the last thing you said.
 * If you are using you laptop, make sure your battery is charged and that everything is loaded and working, **before** you start the presentation.



<br /><br /><br />

## Logistics


 * Demos will take place during the week of Monday, Nov 28.
 * Each of you will register for a 15 minute time slot.      
   * You should plan a 10 minute (max') demo.
   * We are alocating 15 minutes in order to give the TA time to ask questions and/or in case you have technical difficulties.
   * I will post more details about the registration next week.
 * _Code freeze_ is on **Monday, Nov 28, at 10 am**.          
   * That is, you are only allowed to demo code that was committed to your repo by that time.
   * Notice that, unlike the previous assignments, you have write permission to your A5 repo.
 * You will be able to use your laptop or the TA’s.           
   If you are using the TA’s laptop, I will post more details over the next couple of weeks.



<br /><br /><br />

## Evaluation


Your TA will evaluate your work by filling in the following table:


| Component | Weight | 0 | 1 | 2 | 3 | 4 |
| --------- | ------ | ---- | ---- | ---- | ---- | ---- |
| Challenge 1 - Supporting multiple goals | 20% | | | | | |
| Challenge 2 - Parallelizing execution   | 30% | | | | | |
| Presentation                            | 30% | | | | | |
| Software development process            | 20% | | | | | |


<br />

As usual, the 0 - 4 scale means:

|   |  Out of 100 | Description |
| ---- | ---- | ---- |
| 4 | 100% | Outstanding, exceeds expectations |
| 3 | 85%  | Very good, meeting all expectations    |
| 2 | 70%  | OK, but some expectations were not fully met |
| 1 | 50%  | Below expectations |
| 0 | 0%   | Missing (or extremely low value) work |

<br />

The expectations for each component are specified below:

<br />

### Coding (applies to both challenges)


 * Testing code that meets the [scope/requirements](#scope--requirements) of the project.
 * Implementation code that meets the [scope/requirements](#scope--requirements) of the project.
 * Code is of high quality
   * Easy to read and understand.
   * Follows best practices. For example: Meaningful names, proper use of helper functions, no commented out code, no compiler warnings, no repeated or redundant code, etc.


### Presentation

 * Clearly explain your testing strategy, and the various scenarios/cases you are testing.
 * Clearly explain your implementation and interesting design choices you made
 * Answer questions to demonstrate a solid understanding of object-oriented concepts and the design of your software.

**Tip:** Consider using diagrams or other visual aids when explaining your strategy/design.


### Software development process

 * Granular commits with coherent and useful messages.
 * Continuous improvement on a regular basis. At the very least, we expect you to commit work on three different days a week.
 * Planning artifacts - GitHub issues containing the English description of your test cases.


<br />

**Note:** The work you present must be yours. Therefore, we expect you to be able to explain any part of your code to the TA.



