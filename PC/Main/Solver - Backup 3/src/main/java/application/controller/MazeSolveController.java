package application.controller;

import application.dataType.Cell;
import application.dataType.Direction;
import application.dataType.Result;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;

import static application.applet.MainApplet.*;
import static application.controller.MazeExploreController.getCellFromDirection;
import static application.controller.MazeExploreController.getMovementFormTargetCell;

public class MazeSolveController extends Thread {

    Queue<Cell> pendingComputeCell = new LinkedList<>();
    Deque<Cell> robotCommandCellStack = new ArrayDeque<>();

    private static final int FRAMERATE = 1000 / 60;

    public MazeSolveController() {
        this.start();
    }

    private boolean runnerFlag = true;

    public void stopThread() {
        runnerFlag = false;
    }

    @Override
    public void run() {
        setup();
        while (runnerFlag) {
            loop();
            try {
                Thread.sleep(FRAMERATE);
            } catch (InterruptedException ignore) {
            }
        }
    }

    void setup() {
        clearOldVisit();
        pendingComputeCell.add(mazeData.get(columnInit, rowInit));

        computeAddPendingQueue();
        reverseMovement();
    }

    boolean firstRun = true;
    boolean isFinish = false;

    void loop() {
        if (firstRun && ! robotCommandCellStack.isEmpty()) {
            firstRun = false;
            moveRobot(robotCommandCellStack.pop());
        } else if (incomingData() && ! robotCommandCellStack.isEmpty()) {
            moveRobot(robotCommandCellStack.pop());
            inverstState();
        } else if (incomingData() && robotCommandCellStack.isEmpty() && (! isFinish)) {
            isFinish = true;

            Direction nextMovement = getExitDirection(currentCell);
            System.out.println("Exe : " + nextMovement.toString());
            serialEventBus.send("direction", nextMovement.toString());

            System.out.println("LAST MOVE");
            inverstState();
        }

        System.out.println("FLAG :" + incomingData() + "  " + robotCommandCellStack.isEmpty() + " " + (! isFinish));
    }

    Direction getExitDirection(Cell cell) {
        if (cell.N.equals(Result.EXIT)) {
            return Direction.N;
        } else if (cell.E.equals(Result.EXIT)) {
            return Direction.E;
        } else if (cell.S.equals(Result.EXIT)) {
            return Direction.S;
        } else if (cell.W.equals(Result.EXIT)) {
            return Direction.W;
        }
        return null;
    }

    void moveRobot(Cell nextCell) {
        Direction nextMovement = getMovementFormTargetCell(currentCell, nextCell);

        System.out.println("Exe : " + nextMovement.toString());
        serialEventBus.send("direction", nextMovement.toString());

        currentCell = nextCell;
    }

    boolean incomingData() {
        String n = serialEventBus.read("wallN");

        return  (! n.equals("")) ;
    }

    void inverstState() {
        String n = serialEventBus.readNonContain("wallN");
    }

    void reverseMovement() {
        Cell startCell = mazeData.get(columnInit, rowInit);

        do {
            Direction directionToRoot = currentCell.passMovementForm;
            currentCell.solutionMark();
            robotCommandCellStack.push(currentCell);
            currentCell = getCellFromDirection(currentCell, directionToRoot);

            delay(animationDelay);
        } while (currentCell != startCell);

        currentCell.solutionMark();
        delay(animationDelay);
    }

    void computeAddPendingQueue() {
        while (pendingComputeCell.peek() != finalCell) {
            moveNext();
        }
        moveNext();
    }

    void moveNext() {
        currentCell = pendingComputeCell.poll();
        currentCell.visit();

        if (currentCell.N.equals(Result.ROAD)) {
            Cell cell = getCellFromDirection(currentCell, Direction.N);

            if (! cell.isVisit) {
                cell.passMovementForm = Direction.S;
                pendingComputeCell.add(cell);
            }
        }
        if (currentCell.E.equals(Result.ROAD)) {
            Cell cell = getCellFromDirection(currentCell, Direction.E);

            if (! cell.isVisit) {
                cell.passMovementForm = Direction.W;
                pendingComputeCell.add(cell);
            }
        }
        if (currentCell.S.equals(Result.ROAD)) {
            Cell cell = getCellFromDirection(currentCell, Direction.S);

            if (! cell.isVisit) {
                cell.passMovementForm = Direction.N;
                pendingComputeCell.add(cell);
            }
        }
        if (currentCell.W.equals(Result.ROAD)) {
            Cell cell = getCellFromDirection(currentCell, Direction.W);

            if (! cell.isVisit) {
                cell.passMovementForm = Direction.E;
                pendingComputeCell.add(cell);
            }
        }

        delay(animationDelay);
    }

    void clearOldVisit() {
        for (int i = 0; i < mazeSizeIndex; i++) {
            for (int j = 0; j < mazeSizeIndex; j++) {
                mazeData.get(i, j).isVisit = false;
            }
        }
    }

    void delay(int delayTimeMilli) {
        try {
            Thread.sleep(delayTimeMilli);
        } catch (InterruptedException ignore) {
        }
    }
}
