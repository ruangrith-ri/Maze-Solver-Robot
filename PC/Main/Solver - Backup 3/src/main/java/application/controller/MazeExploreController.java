package application.controller;

import application.dataType.Cell;
import application.dataType.Direction;
import application.dataType.Result;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.NoSuchElementException;

import static application.applet.MainApplet.*;


public class MazeExploreController extends Thread {

    Deque<Cell> depthCellStack = new ArrayDeque<>();

    private static final int FRAMERATE = 1000 / 60;

    public MazeExploreController() {
        this.start();
    }

    private boolean runnerFlag = true;

    public void stopThread() {
        runnerFlag = false;
    }

    @Override
    public void run() {
        while (runnerFlag) {
            loop();
            try {
                Thread.sleep(FRAMERATE);
            } catch (InterruptedException ignore) {
            }
        }
    }

    boolean firstRun = true;

    void loop() {
        //System.out.println("Loop");
        try {
            if (! depthCellStack.isEmpty() || firstRun) {
                if (receiveData(currentCell)) {
                    Direction nextMovement = nextMoveNeighborsByPriority(currentCell);

                    if (nextMovement == null) {
                        Cell nextCell = depthCellStack.pop();
                        nextMovement = getMovementFormTargetCell(currentCell, nextCell);

                        if (nextMovement == null) {

                            System.out.println("EXIT NULL)");
                        }
                    } else {
                        pushCurrentStack(currentCell);
                    }

                    System.out.println("Exe : " + nextMovement.toString());
                    serialEventBus.send("direction", nextMovement.toString());
                    currentCell.visit();

                    //TODO : defineExitOfMaze(currentCell);
                    defineExitOfMaze(currentCell);
                    currentCell = getCellFromDirection(currentCell, nextMovement);
                }
            } else {
                // System.out.println("Exit (i,j): (" + finalCell.columnIndex + ", " + finalCell.rowIndex + ")");
            }

        } catch (NoSuchElementException e) {
            // System.out.println("Exit (i,j): (" + finalCell.columnIndex + ", " + finalCell.rowIndex + ")" + e);
        } /*catch (NullPointerException exception) {
            System.out.println("Exception" + exception);
        }*/
    }

    boolean receiveData(Cell cell) {
        String n = serialEventBus.read("wallN");
        String e = serialEventBus.read("wallE");
        String s = serialEventBus.read("wallS");
        String w = serialEventBus.read("wallW");

        if (! n.equals("") && ! e.equals("") && ! s.equals("") && ! w.equals("")) {

            n = serialEventBus.readNonContain("wallN");
            if (n.equals("1")) {
                cell.setN(Result.ROAD);
            } else if (n.equals("0")) {
                cell.setN(Result.WALL);
            }

            e = serialEventBus.readNonContain("wallE");
            if (e.equals("1")) {
                cell.setE(Result.ROAD);
            } else if (e.equals("0")) {
                cell.setE(Result.WALL);
            }

            s = serialEventBus.readNonContain("wallS");
            if (s.equals("1")) {
                cell.setS(Result.ROAD);
            } else if (s.equals("0")) {
                cell.setS(Result.WALL);
            }

            w = serialEventBus.readNonContain("wallW");
            if (w.equals("1")) {
                cell.setW(Result.ROAD);
            } else if (w.equals("0")) {
                cell.setW(Result.WALL);
            }
        }

        if (! w.equals("")) {
            return true;
        }
        return false;
    }

    Direction nextMoveNeighborsByPriority(Cell cell) {
        /* N > E > S > W */
        System.out.println("nextMoveNeighborsByPriority");

        int column = cell.columnIndex;
        int row = cell.rowIndex;

        Cell north = mazeData.get(column, row - 1);
        Cell east = mazeData.get(column + 1, row);
        Cell south = mazeData.get(column, row + 1);
        Cell west = mazeData.get(column - 1, row);

        if (north != null) {
            if ((! north.isVisit) && cell.N == Result.ROAD) {
                return Direction.N;
            }
        }

        if (east != null) {
            if ((! east.isVisit) && cell.E == Result.ROAD) {
                return Direction.E;
            }
        }

        if (south != null) {
            if ((! south.isVisit) && cell.S == Result.ROAD) {
                return Direction.S;
            }
        }

        if (west != null) {
            if ((! west.isVisit) && cell.W == Result.ROAD) {
                return Direction.W;
            }
        }
        return null;
    }

    public static Cell getCellFromDirection(Cell cell, Direction direction) throws NullPointerException {
        int column = cell.columnIndex;
        int row = cell.rowIndex;

        if (direction == Direction.N) {
            return mazeData.get(column, row - 1);
        } else if (direction == Direction.E) {
            return mazeData.get(column + 1, row);
        } else if (direction == Direction.S) {
            return mazeData.get(column, row + 1);
        } else if (direction == Direction.W) {
            return mazeData.get(column - 1, row);
        }
        return null;
    }

    static void defineExitOfMaze(Cell cell) {
        if (cell.rowIndex == 0 && cell.N.equals(Result.ROAD)) {
            finalCell = cell;
            //finalCell.exitDirection = Direction.N;
            cell.N = Result.EXIT;
            System.out.println("Exit is : N");
        } else if (cell.columnIndex == mazeSizeIndex - 1 && cell.E.equals(Result.ROAD)) {
            finalCell = cell;
            //finalCell.exitDirection = Direction.E;
            cell.E = Result.EXIT;
            System.out.println("Exit is : E");
        } else if (cell.rowIndex == mazeSizeIndex - 1 && cell.S.equals(Result.ROAD)) {
            finalCell = cell;
            //finalCell.exitDirection = Direction.S;
            cell.S = Result.EXIT;
            System.out.println("Exit is : S");
        } else if (cell.columnIndex == 0 && cell.W.equals(Result.ROAD)) {
            finalCell = cell;
            // finalCell.exitDirection = Direction.W;
            cell.W = Result.EXIT;
            System.out.println("Exit is : W");
        }

    }

    void pushCurrentStack(Cell cell) {
        int column = cell.columnIndex;
        int row = cell.rowIndex;
        depthCellStack.push(mazeData.get(column, row));
    }

    public static Direction getMovementFormTargetCell(Cell currentCell, Cell targetCell) {

        System.out.println("LOOP 2: STACK REMOVAL");

        if (targetCell.columnIndex == currentCell.columnIndex && targetCell.rowIndex == currentCell.rowIndex - 1) {
            return Direction.N;
        } else if (targetCell.columnIndex == currentCell.columnIndex + 1 && targetCell.rowIndex == currentCell.rowIndex) {
            return Direction.E;
        } else if (targetCell.columnIndex == currentCell.columnIndex && targetCell.rowIndex == currentCell.rowIndex + 1) {
            return Direction.S;
        } else if (targetCell.columnIndex == currentCell.columnIndex - 1 && targetCell.rowIndex == currentCell.rowIndex) {
            return Direction.W;
        }
        System.out.println("LOOP2 NULL");

        return null;
    }


/*
    boolean mazeGenComplete(){
        return depthCellStack.isEmpty() && (finalCell.columnIndex != 0 && finalCell.columnIndex != 0);
    }
    */
}
