package application.service;

import application.dataType.Cell;
import application.dataType.Direction;
import application.dataType.Result;

import java.util.Stack;

import static application.applet.MainApplet.*;


public class MazeExplore extends Thread {

    Stack<Cell> depthCellStack = new Stack<>();

    final int FRAMERATE = 1000 / 60;

    public MazeExplore() {
        this.start();
    }

    @Override
    public void run() {
        while (true) {
            loop();

            try {
                Thread.sleep(FRAMERATE);
            } catch (InterruptedException ignore) {
            }
        }
    }

    void loop() {

        try {
            if (/*currentCell.isSurveyComplete() &&*/ receiveData(currentCell)) {
                Direction nextMovement = nextMoveNeighborsByPriority(currentCell);

                if (nextMovement == null) {
                    nextMovement = getExitOfMaze(currentCell);

                    if (nextMovement == null) {
                        Cell nextCell = depthCellStack.pop();
                        nextMovement = getMovementFormTargetCell(currentCell, nextCell);
                    }
                } else {
                    pushCurrentStack(currentCell);
                }

                serialEventBus.send("direction", nextMovement.toString());
                currentCell.visit();

                moveCellFormDirection(currentCell, nextMovement);
            }

        } catch (Exception ignore) {
            System.out.println("Yeah Hoo !!!!!!!!!!!!!!!!!!");
        }
    }

    boolean receiveData(Cell cell) {
        String n = serialEventBus.readNonContain("wallN");
        if (n.equals("1")) {
            cell.setN(Result.ROAD);
        } else if (n.equals("0")) {
            cell.setN(Result.WALL);
        }

        String e = serialEventBus.readNonContain("wallE");
        if (e.equals("1")) {
            cell.setE(Result.ROAD);
        } else if (e.equals("0")) {
            cell.setE(Result.WALL);
        }

        String s = serialEventBus.readNonContain("wallS");
        if (s.equals("1")) {
            cell.setS(Result.ROAD);
        } else if (s.equals("0")) {
            cell.setS(Result.WALL);
        }

        String w = serialEventBus.readNonContain("wallW");
        if (w.equals("1")) {
            cell.setW(Result.ROAD);
        } else if (w.equals("0")) {
            cell.setW(Result.WALL);
        }

        if (w != "") {
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

    void moveCellFormDirection(Cell cell, Direction direction) {
        int column = cell.columnIndex;
        int row = cell.rowIndex;

        if (direction == Direction.N) {
            currentCell = mazeData.get(column, row - 1);
        } else if (direction == Direction.E) {
            currentCell = mazeData.get(column + 1, row);
        } else if (direction == Direction.S) {
            currentCell = mazeData.get(column, row + 1);
        } else if (direction == Direction.W) {
            currentCell = mazeData.get(column - 1, row);
        }
    }

    Direction getExitOfMaze(Cell cell) {
        if (cell.columnIndex == 0 && cell.W.equals(Result.ROAD)) {
            return Direction.W;
        } else if (cell.columnIndex == mazeSizeIndex - 1 && cell.E.equals(Result.ROAD)) {
            return Direction.E;
        } else if (cell.rowIndex == 0 && cell.N.equals(Result.ROAD)) {
            return Direction.N;
        } else if (cell.rowIndex == mazeSizeIndex - 1 && cell.S.equals(Result.ROAD)) {
            return Direction.S;
        }

        return null;
    }

    void pushCurrentStack(Cell cell) {
        int column = cell.columnIndex;
        int row = cell.rowIndex;
        depthCellStack.push(mazeData.get(column, row));
    }

    Direction getMovementFormTargetCell(Cell currentCell, Cell targetCell) {
        if (targetCell.columnIndex == currentCell.columnIndex && targetCell.rowIndex == currentCell.rowIndex - 1) {
            System.out.println("LOOP2 N");
            return Direction.N;
        } else if (targetCell.columnIndex == currentCell.columnIndex + 1 && targetCell.rowIndex == currentCell.rowIndex) {
            System.out.println("LOOP2 E");
            return Direction.E;
        } else if (targetCell.columnIndex == currentCell.columnIndex && targetCell.rowIndex == currentCell.rowIndex + 1) {
            System.out.println("LOOP2 S");
            return Direction.S;
        } else if (targetCell.columnIndex == currentCell.columnIndex - 1 && targetCell.rowIndex == currentCell.rowIndex) {
            System.out.println("LOOP2 W");
            return Direction.W;
        }
        System.out.println("LOOP2 NULL");

        return null;
    }
}
