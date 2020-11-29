package application.controller;

import application.data.Cell;
import application.data.Direction;
import application.data.Result;

import java.util.*;

import static application.applet.MainApplet.*;
import static application.util.CellOperation.getCellFromDirection;
import static application.util.CellOperation.getMovementFormTargetCell;

public class MazeSolveController extends Thread {

    Queue<Cell> pendingComputeCell = new LinkedList<>();
    Deque<Cell> robotCommandCellStack = new ArrayDeque<>();

    private static final int FRAMERATE = 1000 / 60;
    private Cell startCell;

    public MazeSolveController(Cell startCell) {
        this.startCell = startCell;
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
            delay(FRAMERATE);
        }
        serialEventBus.send("fin", "1");
    }

    void setup() {
        clearOldVisit();
        pendingComputeCell.add(startCell);

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
            invertState();
        } else if (incomingData() && robotCommandCellStack.isEmpty() && (! isFinish)) {
            isFinish = true;

            Direction nextMovement = getExitDirection(currentCell);
            System.out.println("EXECUTE : " + nextMovement.toStringCommand());
            serialEventBus.send("direction", nextMovement.toStringCommand());

            System.out.println("LAST MOVE");
            invertState();

            stopThread();
        }
    }

    Direction getExitDirection(Cell cell) {
        for (Direction direction : Direction.values()) {
            if (cell.getResultExplore(direction).equals(Result.EXIT)) {
                return direction;
            }
        }
        return null;
    }

    void moveRobot(Cell nextCell) {
        Direction nextMovement = getMovementFormTargetCell(currentCell, nextCell);

        assert nextMovement != null;
        System.out.println("Exe : " + nextMovement.toStringCommand());
        serialEventBus.send("direction", nextMovement.toStringCommand());

        currentCell = nextCell;
    }

    boolean incomingData() {
        String n = serialEventBus.read("wallN");

        return  (! n.equals("")) ;
    }

    void invertState() {
        serialEventBus.readNonContain("wallN");
    }

    void reverseMovement() {
        do {
            Direction directionToRoot = currentCell.passMovementFormLastCell;
            currentCell.solutionMark();
            robotCommandCellStack.push(currentCell);
            currentCell = getCellFromDirection(currentCell, directionToRoot);

            delay(ANIMATION_DELAY);
        } while (currentCell != startCell);

        currentCell.solutionMark();
        delay(ANIMATION_DELAY);
    }

    void computeAddPendingQueue() {
        while (pendingComputeCell.peek() != finalCell) {
            moveNext();
        }
        moveNext();
    }

    void moveNext() {
        currentCell = pendingComputeCell.poll();

        assert currentCell != null;
        currentCell.visit();

        for (Direction direction : Direction.values()) {
            if (currentCell.getResultExplore(direction).equals(Result.ROAD)) {
                Cell cell = getCellFromDirection(currentCell, direction);

                assert cell != null;
                if (! cell.isVisit) {
                    cell.passMovementFormLastCell = direction.flip();
                    pendingComputeCell.add(cell);
                }
            }
        }
        delay(ANIMATION_DELAY);
    }

    void clearOldVisit() {
        for (int i = 0; i < getMazeSize(); i++) {
            for (int j = 0; j < getMazeSize(); j++) {
                Objects.requireNonNull(MAZE_DATA.get(i, j)).isVisit = false;
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
