package application.applet;

import application.controller.MazeSolveController;
import application.dataType.Cell;
import application.dataType.Result;
import application.controller.MazeExploreController;
import application.service.SerialEventBus;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import controlP5.ControlEvent;
import controlP5.ControlP5;
import processing.core.PApplet;

public class MainApplet extends PApplet {

    public PApplet processing;

    /*-------------------------------------------------------------------------------------------*/

    ControlP5 cp5;

    public static SerialEventBus serialEventBus;
    public static Table<Integer, Integer, Cell> mazeData = HashBasedTable.create();
    public static Cell currentCell;

    /*-------------------------------------------------------------------------------------------*/

    public static int mazeSizeIndex = 10;
    int columnInit = 9;
    int rowInit = 9;

    int boxSize = 80;
    int mazeSize = boxSize * mazeSizeIndex;

    /*-------------------------------------------------------------------------------------------*/

    @Override
    public void settings() {
        size(mazeSize + 250, mazeSize);
    }

    @Override
    public void setup() {
        processing = this;

        background(0);
        frameRate(60);
        strokeCap(PROJECT);

        cp5 = new ControlP5(this);

        cp5.addButton("explore")
                .setPosition(mazeSize + 25, 60)
                .setSize(200, 30);

        cp5.addButton("solve")
                .setPosition(mazeSize + 25, 120)
                .setSize(200, 30);

        for (int i = 0; i < mazeSizeIndex; i++) {
            for (int j = 0; j < mazeSizeIndex; j++) {
                mazeData.put(i, j, new Cell(i, j));
            }
        }

        currentCell = mazeData.get(columnInit, rowInit);
        currentCell.setS(Result.START);
    }

    @Override
    public void draw() {
        background(0, 0, 0);

        for (int column = 0; column < mazeSizeIndex; column++) {
            for (int row = 0; row < mazeSizeIndex; row++) {
                pushMatrix();

                Cell cell = mazeData.get(column, row);

                if (cell == currentCell) {
                    fill(0, 0, 200);
                } else {
                    assert cell != null;
                    if (cell.isSolutionPath) {
                        fill(200, 0, 0);
                    } else if (cell.isVisit) {
                        fill(0, 200, 0);
                    } else {
                        fill(240);
                    }
                }
                noStroke();
                translate(column * boxSize, row * boxSize);
                rect(column, row, boxSize, boxSize);


                strokeWeight(10);
                assert cell != null;
                if (cell.N == Result.WALL) {
                    stroke(0);
                    line(column, row, column + boxSize, row);
                }
                if (cell.S == Result.WALL) {
                    stroke(0);
                    line(column, row + boxSize, column + boxSize, row + boxSize);
                }
                if (cell.E == Result.WALL) {
                    stroke(0);
                    line(column + boxSize, row, column + boxSize, row + boxSize);
                }
                if (cell.W == Result.WALL) {
                    stroke(0);
                    line(column, row, column, row + boxSize);
                }

                popMatrix();
            }
        }
    }

    public void controlEvent(ControlEvent theEvent) {
        println(theEvent.getController().getName());
    }

    public void explore(int theValue) {
        serialEventBus = new SerialEventBus("COM8", 115200);
        MazeExploreController mazeExploreController = new MazeExploreController();
    }

    public void solve(int theValue) {
        MazeSolveController mazeSolveController = new MazeSolveController();
    }
}
