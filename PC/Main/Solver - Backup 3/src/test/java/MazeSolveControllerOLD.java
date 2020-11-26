package application.controller;

import application.dataType.Cell;
import application.dataType.Direction;
import application.dataType.Result;

import java.util.*;

import static application.applet.MainApplet.*;

public class MazeSolveControllerOLD extends Thread {

    public static Queue<String> solution = new LinkedList<>();
    private static ArrayList<Cell> cellQueue = new ArrayList<>();

    private static int moveI[] = {0, 1, 0, - 1};
    private static int moveJ[] = {- 1, 0, 1, 0};
    private static int count = 0;

    private static final int FRAMERATE = 1000 / 60;

    public MazeSolveControllerOLD() {
        this.start();
    }

    @Override
    public void run() {
        setup();
        while (true) {
            loop();

            try {
                Thread.sleep(FRAMERATE);
            } catch (InterruptedException ignore) {
            }
        }
    }

    void setup() {
        mazeSolve();

        System.out.println("Solution" + solution.toString());
    }

    void loop() {
        if (count < solution.size() - 2) {
            if (receiveData()) {
                //TODO : Send Command
//                serialEventBus.send("direction", solution.get(count + 1));
                count++;
            }
        } else {
            System.out.println("Exit reached.");
        }
    }

    boolean receiveData() {
        String n = serialEventBus.readNonContain("wallN");
        if (n != "") {
            return true;
        }
        return false;
    }

    boolean flag;

    void mazeSolve() {
        initSolve();
        routingToExit();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException ignore) {
            System.out.println(ignore);
        }

        System.out.println("Before Backward cellQue " + cellQueue.get(0).columnIndex + " ROW" + cellQueue.get(0).rowIndex);

        //get target cell form movement
        while (cellQueue.get(0).directionFrom != null) {
            String directionFrom = cellQueue.get(0).directionFrom;
            solution.add(directionFrom);
            cellQueue.get(0).solutionMark();
            currentCell = getTargetCellFormMovement(cellQueue.get(0), directionFrom);

            cellQueue.remove(0);
            cellQueue.add(currentCell);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignore) {
                System.out.println(ignore);
            }
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignore) {
            System.out.println(ignore);
        }
        String directionFrom = cellQueue.get(0).directionFrom;
        solution.add(directionFrom);
        cellQueue.get(0).solutionMark();




/*
        while (solution.get(0).equals("START")) {
            solution.add(0, currentCell.directionFrom);
            currentCell.isSolutionPath = true;

            if (currentCell.directionFrom.equals("S")) {
                currentCell = mazeData.get(currentCell.columnIndex, currentCell.rowIndex - 1);
            }
            if (currentCell.directionFrom.equals("W")) {
                currentCell = mazeData.get(currentCell.columnIndex + 1, currentCell.rowIndex);
            }
            if (currentCell.directionFrom.equals("N")) {
                currentCell = mazeData.get(currentCell.columnIndex, currentCell.rowIndex + 1);
            }
            if (currentCell.directionFrom.equals("E")) {
                currentCell = mazeData.get(currentCell.columnIndex - 1, currentCell.rowIndex);
            }
        }
*/
    }

    Cell getTargetCellFormMovement(Cell cell, String direction) {
        int column = cell.columnIndex;
        int row = cell.rowIndex;

        switch (direction) {
            case "N":
                return mazeData.get(column, row + 1);
            case "E":
                return mazeData.get(column - 1, row);
            case "S":
                return mazeData.get(column, row + 1);
            case "W":
                return mazeData.get(column + 1, row);
            default:
                return null;
        }
    }

    void initSolve() {
        for (int i = 0; i < mazeSizeIndex; i++) {
            for (int j = 0; j < mazeSizeIndex; j++) {
                mazeData.get(i, j).isSolutionPath = false;
                mazeData.get(i, j).isVisit = false;
            }
        }

        currentCell = mazeData.get(columnInit, rowInit);

        if (currentCell != null) {
            currentCell.isVisit = true;
            currentCell.directionFrom = "START";

            //TODO : Add Solution

            // solution.add("END");
            cellQueue.add(currentCell);

            System.out.println("NOT NULL " + currentCell.isVisit + "Que " + cellQueue.size());
        }

        System.out.println("into ROUTING");
    }

    public static boolean stepRun = true;

    void routingToExit() {
        while (! cellQueue.isEmpty()) {
            System.out.println("");
            for (Cell c : cellQueue
            ) {
                System.out.println("Column " + c.columnIndex + "   Row " + c.rowIndex);
            }
            System.out.println("");
            System.out.println("");

            try {
                Thread.sleep(1500);
            } catch (InterruptedException ignore) {
                System.out.println(ignore);
            }

            System.out.println("QueSize " + " COL " + cellQueue.get(0).columnIndex + "  ROW " + cellQueue.get(0).rowIndex);
            currentCell = cellQueue.get(0);

            System.out.println("CURRENT CELL" + " COL " + currentCell.columnIndex + "  ROW " + currentCell.rowIndex);
            currentCell.visit();

            if (currentCell.columnIndex == finalCell.columnIndex && currentCell.rowIndex == finalCell.rowIndex) {

                System.out.println("Final Cell" + " COL " + finalCell.columnIndex + "  ROW " + finalCell.rowIndex);
                currentCell.visit();

                //Finish Cell
                break;
            }

            for (int i = 0; i < 4; i++) {
                int nextI = currentCell.columnIndex + moveI[i];
                int nextJ = currentCell.rowIndex + moveJ[i];

                if (isValid(mazeData.get(nextI, nextJ)) && canMove(currentCell, i) && ! mazeData.get(nextI, nextJ).isVisit) {
                    switch (i) {
                        case 0:
                            mazeData.get(nextI, nextJ).directionFrom = "N";
                            System.out.println("Que N");
                            break;
                        case 1:
                            mazeData.get(nextI, nextJ).directionFrom = "E";
                            System.out.println("Que E");
                            break;
                        case 2:
                            mazeData.get(nextI, nextJ).directionFrom = "S";
                            System.out.println("Que S");
                            break;
                        case 3:
                            mazeData.get(nextI, nextJ).directionFrom = "W";
                            System.out.println("Que W");
                            break;
                        default:
                            System.out.println("non Que");
                            break;
                    }

                    Cell adjCell = mazeData.get(nextI, nextJ);
                    cellQueue.add(adjCell);

                    //System.out.println("RUN QUE");
                } else {
                    System.out.println("Else");
                    //cellQueue.remove(0);
                }
            }
        }
    }

    boolean canMove(Cell cell, int dir) {
        boolean cellMovement = false;
        switch (dir) {
            case 0:
                if (mazeData.get(cell.columnIndex, cell.rowIndex).N == Result.ROAD) {
                    cellMovement = true;
                    break;
                }
            case 1:
                if (mazeData.get(cell.columnIndex, cell.rowIndex).E == Result.ROAD) {
                    cellMovement = true;
                    break;
                }
            case 2:
                if (mazeData.get(cell.columnIndex, cell.rowIndex).S == Result.ROAD) {
                    cellMovement = true;
                    break;
                }
            case 3:
                if (mazeData.get(cell.columnIndex, cell.rowIndex).W == Result.ROAD) {
                    cellMovement = true;
                    break;
                }
        }
        return cellMovement;
    }

    boolean isValid(Cell cell) {
        if (cell != null) {
            boolean rowValid = (cell.rowIndex >= 0) && (cell.rowIndex < mazeSizeIndex);
            boolean columnValid = (cell.columnIndex >= 0) && (cell.columnIndex < mazeSizeIndex);

            return rowValid && columnValid;
        }
        return false;
    }
}
