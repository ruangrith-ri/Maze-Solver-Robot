package application.util;

import application.data.Cell;
import application.data.Direction;
import application.data.Result;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import static application.applet.MainApplet.*;

public class CellOperation {

    private static final Logger logger = Logger.getLogger("CellOperation");

    private CellOperation() {
        throw new IllegalStateException("Utility class");
    }

    public static Direction getMoveDirectionByPriority(Cell cell) {

        for (Direction direction : Direction.values()) {
            Cell checkCell = getCellFromDirection(cell, direction);

            if (checkCell != null && (! checkCell.isVisit) && cell.getResultExplore(direction) == Result.ROAD) {
                return direction;
            }
        }

        return null;
    }

    public static void checkAndMarkExitOfMaze(Cell cell) {
        for (Direction direction : Direction.values()) {
            try {
                if (cell.getResultExplore(direction).equals(Result.ROAD)) {
                    int borderIndexOfDirection;
                    int checkerAxisIndex;

                    switch (direction) {
                        case NORTH:
                        case SOUTH:
                            borderIndexOfDirection = direction == Direction.NORTH ? 0 : getMazeSize() - 1;
                            checkerAxisIndex = cell.rowIndex;
                            break;

                        case WAST:
                        case EAST:
                            borderIndexOfDirection = direction == Direction.WAST ? 0 : getMazeSize() - 1;
                            checkerAxisIndex = cell.columnIndex;
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + direction);
                    }

                    if (checkerAxisIndex == borderIndexOfDirection) {
                        finalCell = cell;
                        cell.setResultExplore(direction, Result.EXIT);

                        logger.log(Level.INFO, MessageFormat.format("Exit in cell is : {0}", direction.toStringCommand()));
                    }
                }
            }catch (NullPointerException e){
                System.out.println("Null " + direction);
            }
        }
    }

    public static Cell getCellFromDirection(Cell cell, Direction direction) throws NullPointerException {
        int column = cell.columnIndex;
        int row = cell.rowIndex;

        switch (direction){
            case NORTH:
                return MAZE_DATA.get(column, row - 1);
            case EAST:
                return MAZE_DATA.get(column + 1, row);
            case SOUTH:
                return MAZE_DATA.get(column, row + 1);
            case WAST:
                return MAZE_DATA.get(column - 1, row);
            default:
                return null;
        }
    }

    public static Direction getMovementFormTargetCell(Cell currentCell, Cell targetCell) {

        logger.log(Level.INFO, "STACK REMOVAL");

        if (targetCell.columnIndex == currentCell.columnIndex && targetCell.rowIndex == currentCell.rowIndex - 1) {
            return Direction.NORTH;
        } else if (targetCell.columnIndex == currentCell.columnIndex + 1 && targetCell.rowIndex == currentCell.rowIndex) {
            return Direction.EAST;
        } else if (targetCell.columnIndex == currentCell.columnIndex && targetCell.rowIndex == currentCell.rowIndex + 1) {
            return Direction.SOUTH;
        } else if (targetCell.columnIndex == currentCell.columnIndex - 1 && targetCell.rowIndex == currentCell.rowIndex) {
            return Direction.WAST;
        }
        return null;
    }

    public static boolean isSurveyComplete(Cell cell) {

        for (Direction direction : Direction.values()) {
            if (cell.getResultExplore(direction) == null) {
                return false;
            }
        }

        return true;
    }
}
