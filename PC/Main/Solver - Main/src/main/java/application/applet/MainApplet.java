package application.applet;

import application.controller.MazeSolveController;
import application.data.Cell;
import application.data.Direction;
import application.data.Result;
import application.controller.MazeExploreController;
import application.service.SerialEventBus;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import controlP5.ControlEvent;
import controlP5.ControlP5;
import processing.core.PApplet;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;

public class MainApplet extends PApplet {

    public PApplet processing;

    /*-------------------------------------------------------------------------------------------*/

    ControlP5 controlP5;

    public static final int ANIMATION_DELAY = 300;

    public static final Table<Integer, Integer, Cell> MAZE_DATA = HashBasedTable.create();

    public static Cell currentCell;
    public static Cell finalCell;

    public static Queue<Cell> queueVisualizer = new LinkedList<>();
    public static Deque<Cell> stackVisualizer = new ArrayDeque<>();

    /*-------------------------------------------------------------------------------------------*/

    private static final int MAZE_SIZE = 10;
    private static final int COLUMN_INIT =  /*mazeSizeIndex - 1*/0;
    private static final int ROW_INIT = /*mazeSizeIndex - 1*/0;

    private static final int CELL_SIZE = 80;
    private static final int MAZE_DRAW_SIZE = CELL_SIZE * MAZE_SIZE;

    public static int getMazeSize() {
        return MAZE_SIZE;
    }

    /*-------------------------------------------------------------------------------------------*/

    @Override
    public void settings() {
        size(MAZE_DRAW_SIZE + 500, MAZE_DRAW_SIZE + 50);
    }

    @Override
    public void setup() {
        processing = this;

        background(0);
        frameRate(60);

        initGraphicUserInterface();

        for (int i = 0; i < MAZE_SIZE; i++) {
            for (int j = 0; j < MAZE_SIZE; j++) {
                MAZE_DATA.put(i, j, new Cell(i, j));
            }
        }

        currentCell = MAZE_DATA.get(COLUMN_INIT, ROW_INIT);
    }

    private void initGraphicUserInterface() {
        controlP5 = new ControlP5(this);

        controlP5.addButton("explore")
                .setPosition(MAZE_DRAW_SIZE + 350f, 25)
                .setSize(100, 50);

        controlP5.addButton("solve")
                .setPosition(MAZE_DRAW_SIZE + 350f, 100)
                .setSize(100, 50);
    }

    @Override
    public void draw() {
        background(64);

        pushMatrix();
        translate(25, 25);

        drawMazeCell();
        drawMazeGridline();

        stackVisualization();
        queueVisualization();

        popMatrix();
    }

    private void stackVisualization() {
        pushMatrix();
        pushStyle();

        translate(MAZE_DRAW_SIZE + 50f, MAZE_DRAW_SIZE);
        fill(0);
        text("STACK", 30, 15);

        translate(0, - 25 * stackVisualizer.size());

        for (Cell cell : stackVisualizer) {

            fill(255, 0, 0);
            strokeWeight(2);
            stroke(0);
            rect(0, 0, 100, 25);

            noStroke();
            fill(0);
            text("Col: " + cell.columnIndex, 5, 16);
            text("Row: " + cell.rowIndex, 50, 16);

            translate(0, 25);
        }

        popStyle();
        popMatrix();
    }

    private void queueVisualization() {
        pushMatrix();
        pushStyle();

        translate(MAZE_DRAW_SIZE + 175f, MAZE_DRAW_SIZE);
        fill(0);
        text("QUEUE", 30, 15);

        translate(0, -25);

        for (Cell cell : queueVisualizer) {

            fill(0, 255, 0);
            strokeWeight(2);
            stroke(0);
            rect(0, 0, 100, 25);

            noStroke();
            fill(0);
            text("Col: " + cell.columnIndex, 5, 16);
            text("Row: " + cell.rowIndex, 50, 16);

            translate(0, - 25);
        }
        popStyle();
        popMatrix();
    }

    private void drawMazeGridline() {
        for (int column = 0; column < MAZE_SIZE; column++) {
            for (int row = 0; row < MAZE_SIZE; row++) {
                pushMatrix();

                Cell cell = MAZE_DATA.get(column, row);
                translate(column * CELL_SIZE, row * CELL_SIZE);

                strokeWeight(10);
                stroke(0);

                assert cell != null;
                for (Direction direction : Direction.values()) {
                    if (cell.getResultExplore(direction) == Result.WALL) {
                        switch (direction) {
                            case NORTH:
                                line(column, row, column + (CELL_SIZE), row);
                                break;
                            case EAST:
                                line(column + CELL_SIZE, row, column + CELL_SIZE, row + CELL_SIZE);
                                break;
                            case SOUTH:
                                line(column, row + CELL_SIZE, column + CELL_SIZE, row + CELL_SIZE);
                                break;
                            case WAST:
                                line(column, row, column, row + CELL_SIZE);
                                break;
                        }
                    }
                }
                popMatrix();
            }
        }
    }

    private void drawMazeCell() {
        for (int column = 0; column < MAZE_SIZE; column++) {
            for (int row = 0; row < MAZE_SIZE; row++) {
                pushMatrix();

                Cell cell = MAZE_DATA.get(column, row);

                if (cell == currentCell) {
                    fill(0, 0, 200);
                } else {
                    assert cell != null;
                    if (cell.isSolutionPath) {
                        fill(200, 0, 0);
                    } else if (cell.isVisit) {
                        fill(0, 200, 0);
                    } else if (cell.isExplor) {
                        fill(240);
                    } else {
                        fill(128);
                    }
                }
                noStroke();
                translate(column * CELL_SIZE, row * CELL_SIZE);
                rect(column, row, CELL_SIZE, CELL_SIZE);

                popMatrix();
            }
        }
    }

    public void controlEvent(ControlEvent theEvent) {
        println(theEvent.getController().getName());
    }

    public static SerialEventBus serialEventBus;

    public static MazeExploreController mazeExploreController;
    public static MazeSolveController mazeSolveController;

    public void explore(int theValue) {
        serialEventBus = new SerialEventBus("COM8", 115200);
        mazeExploreController = new MazeExploreController(MAZE_DATA.get(COLUMN_INIT, ROW_INIT));
    }

    public void solve(int theValue) {
        mazeSolveController = new MazeSolveController(MAZE_DATA.get(COLUMN_INIT, ROW_INIT));
    }
}
