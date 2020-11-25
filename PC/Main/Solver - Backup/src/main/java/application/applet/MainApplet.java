package application.applet;

import application.dataType.Cell;
import application.dataType.Result;
import application.service.MazeExplore;
import application.service.SerialEventBus;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import controlP5.ControlEvent;
import controlP5.ControlP5;
import processing.core.PApplet;

public class MainApplet extends PApplet {

    public PApplet processing;

    ControlP5 cp5;

    MazeExplore me;

    public static SerialEventBus serialEventBus;
    public static Table<Integer, Integer, Cell> mazeData = HashBasedTable.create();
    public static Cell currentCell;

    /*-------------------------------------------------------------------------------------------*/

    int boxSize = 80;

    public static int mazeSizeIndex = 10;
    int columnInit = 0;
    int rowInit = 0;
    int mazeSize = boxSize * mazeSizeIndex;

    /*-------------------------------------------------------------------------------------------*/

    @Override
    public void settings() {
        size(1220, 800);
    }

    @Override
    public void setup() {
        //Cell.setCanvas(super.g);
        processing = this;

        background(0);
        frameRate(60);
        strokeCap(PROJECT);

        cp5 = new ControlP5(this);

        cp5.addButton("explore")
                .setPosition(950, 60)
                .setSize(200, 30);

        cp5.addButton("solve")
                .setPosition(950, 100)
                .setSize(200, 30);

        for (int i = 0; i < mazeSizeIndex; i++) {
            for (int j = 0; j < mazeSizeIndex; j++) {
                mazeData.put(i, j, new Cell(i, j));
            }
        }
/*
        Cell cell = mazeData.get(0, 0);
        cell.setE(Result.WALL);

        cell = mazeData.get(4, 3);
        cell.isVisit = true;
        cell.isSolutionPath = true;
*/
        currentCell = mazeData.get(columnInit, rowInit);
        currentCell.setS(Result.START);
    }

    @Override
    public void draw() {
        background(32, 0, 0);

        for (int i = 0; i < mazeSizeIndex; i++) {
            for (int j = 0; j < mazeSizeIndex; j++) {
                pushMatrix();

                Cell cell = mazeData.get(i, j);

                if (cell == currentCell) {
                    fill(0, 0, 200);
                } else if (cell.isSolutionPath) {
                    fill(200, 0, 0);
                } else if (cell.isVisit) {
                    fill(0, 200, 0);
                } else {
                    fill(255);
                }
                noStroke();
                translate(i * boxSize, j * boxSize);
                rect(i, j, boxSize, boxSize);


                strokeWeight(10);
                if (cell.N == Result.WALL) {
                    stroke(0);
                    line(i, j, i + boxSize, j);
                }
                if (cell.S == Result.WALL) {
                    stroke(0);
                    line(i, j + boxSize, i + boxSize, j + boxSize);
                }
                if (cell.E == Result.WALL) {
                    stroke(0);
                    line(i + boxSize, j, i + boxSize, j + boxSize);
                }
                if (cell.W == Result.WALL) {
                    stroke(0);
                    line(i, j, i, j + boxSize);
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
        me = new MazeExplore();
    }
}
