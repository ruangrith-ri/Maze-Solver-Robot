package application.controller;

import application.applet.MainApplet;
import application.data.Cell;
import application.data.Direction;
import application.data.Result;
import application.util.CellOperation;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.logging.Level;
import java.util.logging.Logger;

import static application.applet.MainApplet.*;


public class MazeExploreController extends Thread {

    Logger logger = Logger.getLogger("MazeExploreController");

    private final Deque<Cell> reverseCellExploreComputeStack = new ArrayDeque<>();
    private Cell startCell;

    private static final int FRAMERATE = 1000 / 60;

    public MazeExploreController(Cell startCell) {
        currentCell = startCell;
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
            try {
                Thread.sleep(FRAMERATE);
            } catch (InterruptedException ignore) {
            }
        }
    }

    void setup(){
        stackVisualizer = reverseCellExploreComputeStack;
    }

    boolean firstRun = true;

    void loop() {
        try {
            if (receiveAndSetData(currentCell)) {
                if (firstRun) {
                    System.out.println("First");
                    currentCell.setResultExplore(Direction.SOUTH, null, currentCell);
                }

                Direction nextMovement = CellOperation.getMoveDirectionByPriority(currentCell);

                if (nextMovement == null) {
                    Cell nextCell = reverseCellExploreComputeStack.pop();
                    nextMovement = CellOperation.getMovementFormTargetCell(currentCell, nextCell);
                } else {
                    reverseCellExploreComputeStack.push(currentCell);
                }

                if (nextMovement != null) {
                    if (firstRun) {
                        firstRun = false;
                        System.out.println("First");
                    }
                    serialEventBus.send("direction", nextMovement.toStringCommand());
                    currentCell.visit().explore();

                    CellOperation.checkAndMarkExitOfMaze(currentCell);
                    currentCell = CellOperation.getCellFromDirection(currentCell, nextMovement);

                    logger.log(Level.INFO, " Direction : {0}", nextMovement.toStringCommand());
                }
            }
        } catch (Exception e) {
            stopThread();
            logger.log(Level.INFO, "Finish " + e);

            mazeSolveController = new MazeSolveController(startCell);
        }
    }

    boolean receiveAndSetData(Cell cell) {
        int dataInCount = 0;

        for (Direction direction : Direction.values()) {
            if (! serialEventBus.read("wall" + direction.toStringCommand()).equals("")) {
                dataInCount++;
            }
        }

        if (dataInCount == 4) {
            for (Direction direction : Direction.values()) {
                String buffer = serialEventBus.readNonContain("wall" + direction.toStringCommand());
                cell.setResultExplore(direction, buffer.equals("ROAD") ? Result.ROAD : Result.WALL, currentCell);
            }
        }

        return (dataInCount >= 3);
    }
}
