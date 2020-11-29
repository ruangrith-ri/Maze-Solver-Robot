package application.data;

import java.util.*;

import static application.util.CellOperation.getCellFromDirection;

public class Cell {
    public int columnIndex, rowIndex;

    public boolean isExplor = false;
    public boolean isVisit = false;
    public boolean isSolutionPath = false;

    public Direction passMovementFormLastCell;

    Map<Direction,Result> exploreResult = new EnumMap<>(Direction.class);

    public Cell(int columnIndex, int rowIndex) {
        this.columnIndex = columnIndex;
        this.rowIndex = rowIndex;

        for (Direction direction : Direction.values()) {
            exploreResult.put(direction,null);
        }
    }

    public Cell visit() {
        isVisit = true;
        return this;
    }

    public Cell explore() {
        isExplor = true;
        return this;
    }

    public Cell solutionMark() {
        isSolutionPath = true;
        return this;
    }

    public Cell setResultExplore(Direction direction,Result result){
        exploreResult.put(direction,result);
        return this;
    }

    public Cell setResultExplore(Direction direction,Result result ,Cell cell){
        exploreResult.put(direction,result);

        try {
            Objects.requireNonNull(getCellFromDirection(cell, direction)).setResultExplore(direction.flip(),result);
        }catch (Exception ignore){
            System.out.println("Is Border Cell");
        }

        return this;
    }

    public Result getResultExplore(Direction direction){
        return exploreResult.get(direction);
    }
}


