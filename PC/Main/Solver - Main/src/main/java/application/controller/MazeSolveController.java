package application.controller;

import application.dataType.Cell;

import java.util.PriorityQueue;
import java.util.Queue;

public class MazeSolveController extends Thread {

    Queue<Cell> cellQueue = new PriorityQueue<Cell>();

    private static final int FRAMERATE = 1000 / 60;

    public MazeSolveController() {
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

    void setup(){

    }

    void loop(){

    }
}
