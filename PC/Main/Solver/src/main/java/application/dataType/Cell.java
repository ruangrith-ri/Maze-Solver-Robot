package application.dataType;

//Data structure used for DFS maze generation//

import application.applet.MainApplet;
import processing.core.PApplet;
import processing.core.PGraphics;

import java.util.ArrayList;

import java.util.LinkedList;
import java.util.Queue;
import  java.util.Random;

import static application.applet.MainApplet.*;

public class Cell {
    public int i, j;
    public boolean walls[] = {true, true, true, true};
    boolean wallData[] = {false, false, false, false};
    public boolean visited = false;
    boolean visitedSolve = false;
    boolean solutionPath = false;
    String dir;

    static PGraphics canvas;

    public static void setCanvas(PGraphics canvas){
        Cell.canvas = canvas;
    }

    //Take i and k indice and put them into an object in class Cell
    public Cell(int ipos, int jpos) {
        i = ipos;
        j = jpos;
    }

    Cell(int ipos, int jpos, String dirpos) {
        i = ipos;
        j = jpos;
        dir = dirpos;
    }
    static ArrayList<Cell> neighbors;

    //Neighbor checking method & randomization of next cell to go//
    public Cell checkNeighbors(){
        neighbors = new ArrayList<Cell>();

        Cell north = grid.get(findIndex(i, j-1));
        Cell east  = grid.get(findIndex(i+1, j));
        Cell south = grid.get(findIndex(i, j+1));
        Cell west  = grid.get(findIndex(i-1, j));

        if(!north.visited) neighbors.add(north);
        if(!east.visited)  neighbors.add(east);
        if(!south.visited) neighbors.add(south);
        if(!west.visited)  neighbors.add(west);

        if (neighbors.size() > 0){
            int r = new Random().nextInt(neighbors.size());
            return neighbors.get(r);
        }
        else {
            return null;
        }
    }

    public Cell checkNeighborsRobot(){
        ArrayList<Cell> neighbors = new ArrayList<Cell>();

        Cell north = map.get(findIndex(i, j-1));
        Cell east  = map.get(findIndex(i+1, j));
        Cell south = map.get(findIndex(i, j+1));
        Cell west  = map.get(findIndex(i-1, j));

        if(!north.visited && canMove(current.i, current.j, 0, map)) neighbors.add(north);
        if(!east.visited  && canMove(current.i, current.j, 1, map)) neighbors.add(east);
        if(!south.visited && canMove(current.i, current.j, 2, map)) neighbors.add(south);
        if(!west.visited  && canMove(current.i, current.j, 3, map)) neighbors.add(west);

        println("Select" + neighbors.toString());

        if (!neighbors.isEmpty()){
            int r = new Random().nextInt(neighbors.size());
            return neighbors.get(r);
        }
        else {
            return null;
        }
    }

    //Show maze//
    public void show(int stroke,int w) {
        int x = (i*w) + stroke/4;
        int y = (j*w) + stroke/4;

        if(visitedSolve || visited){
            canvas.noStroke();
            canvas.fill(0,0,255,100);
            canvas.rect(x,y,w,w);
        }

        if(solutionPath){
            canvas.noStroke();
            canvas.fill(255,0,0,100);
            canvas.rect(x,y,w,w);
        }

        canvas.stroke(0);
        canvas.strokeWeight(stroke);

        if (walls[0]) {canvas.line(x  , y  , x+w, y  );} //North
        if (walls[1]) {canvas.line(x+w, y  , x+w, y+w);} //East
        if (walls[2]) {canvas.line(x  , y+w, x+w, y+w);} //South
        if (walls[3]) {canvas.line(x  , y  , x  , y+w);} //West
    }

    //Highlight(Blue)//
    public void highlight(int stroke,int w){
        int x = (i*w) + stroke*3/4;
        int y = (j*w) + stroke*3/4;
        canvas.noStroke();
        canvas.fill(0, 0, 255, 100);
        canvas.rect(x, y, w-stroke, w-stroke);
    }

    //Highlight(Blue)//
    public void highlightGreen(int stroke,int w){
        int x = (i*w) + stroke*3/4;
        int y = (j*w) + stroke*3/4;
        canvas.noStroke();
        canvas.fill(0, 255, 0, 100);
        canvas.rect(x, y, w-stroke, w-stroke);
    }

    static int count = 0;

    //Function to check if robot can move from (i,j) to the direction of dir (0,1,2,3 -> N,E,S,W)//
    static boolean canMove(int i, int j, int dir, ArrayList<Cell> cellArray){
        boolean canMove = false;
        if(cellArray.get(findIndex(i, j)).walls[dir] == false) {canMove = true;}
        return canMove;
    }

    //Function to check if cell at (i,j) is out of bounds//
    static boolean isValid(int i, int j) {
        return (i >= 0) && (i < rows) && (j >= 0) && (j < cols);
    }

    //Movement array for N,E,S,W//
    static int moveI[] = {  0,  1,  0, -1 };
    static int moveJ[] = { -1,  0,  1,  0 };

    //BFS maze solving//
    public static void mazeSolve(ArrayList<Cell> cellArray) {

        solution.clear();

        for (int j = 0; j< rows; j++) {
            for (int i = 0; i< cols; i++) {
                cellArray.get(findIndex(i,j)).visitedSolve = false;
                cellArray.get(findIndex(i,j)).solutionPath = false;
            }
        }

        //Initialize solution list//
        cellArray.get(findIndex(start.i,start.j)).visited = true; //Starting cell as visited
        cellArray.get(findIndex(start.i,start.j)).dir = "START"; //Set starting point as I for solution tracking
        solution.add("END");

        Queue<queueNode> q = new LinkedList(); //Initialize array to store queueNode
        queueNode s = new queueNode(start); //Initialize queueNode for starting point
        q.add(s); //Add starting point queueNode to queue

        //BFS until queue is empty//
        while (!q.isEmpty()) {
            queueNode firstOut = q.peek(); //Queue peek to get the first member
            Cell current = firstOut.point; //Set current cell as the first member of the queue

            if(current.i == finish.i && current.j == finish.j){
                cellArray.get(findIndex(current.i,current.j)).visitedSolve = true;
                while(solution.get(0) != "START"){
                    solution.add(0, cellArray.get(findIndex(current.i,current.j)).dir);
                    cellArray.get(findIndex(current.i, current.j)).solutionPath = true;
                    if     (cellArray.get(findIndex(current.i,current.j)).dir == "S") {current.j = current.j - 1;}
                    else if(cellArray.get(findIndex(current.i,current.j)).dir == "W") {current.i = current.i + 1;}
                    else if(cellArray.get(findIndex(current.i,current.j)).dir == "N") {current.j = current.j + 1;}
                    else if(cellArray.get(findIndex(current.i,current.j)).dir == "E") {current.i = current.i - 1;}
                }
                solution.add(solution.size()-1, exitDir);
                //println(solution);
                flagSolve = false;
                flagPrintSolution = true;
                current = map.get(findIndex(startI, startJ));
                return;
            }

            q.remove();

            for(int i = 0; i < 4; i++) {
                int nextI = current.i + moveI[i]; //Next cell index (row)
                int nextJ = current.j + moveJ[i]; //Next cell index (column)
                cellArray.get(findIndex(current.i,current.j)).visitedSolve = true; //Mark current cell as visited

                //If next cell is not out of bounds, not blocked by walls, and not marked as visited//
                if(isValid(nextI, nextJ) && canMove(current.i, current.j, i, cellArray) && !cellArray.get(findIndex(nextI, nextJ)).visitedSolve) {

                    //Mark next cell to visit that prior cell is to its N,E,S,W//
                    switch(i){
                        case 0: cellArray.get(findIndex(nextI, nextJ)).dir = "N"; break;
                        case 1: cellArray.get(findIndex(nextI, nextJ)).dir = "E"; break;
                        case 2: cellArray.get(findIndex(nextI, nextJ)).dir = "S"; break;
                        case 3: cellArray.get(findIndex(nextI, nextJ)).dir = "W"; break;
                    }
                    MainApplet.queueNode Adjcell = new MainApplet.queueNode(new Cell(nextI, nextJ)); //Initialize next cell as a queueNode
                    q.add(Adjcell); //Add the queueNode to queue
                }
            }
        }
    }



    public static void solutionPrint(){
        if(!solution.isEmpty()){
            if(count < solution.size()-2) {
                String nextDir = solution.get(count+1);
                println(nextDir);
                if      (nextDir.equals("N")) { next = map.get(findIndex(current.i   , current.j-1));  }
                else if (nextDir.equals("E")) { next = map.get(findIndex(current.i+1 , current.j  ));  }
                else if (nextDir.equals("S")) { next = map.get(findIndex(current.i   , current.j+1));  }
                else if (nextDir.equals("W")) { next = map.get(findIndex(current.i-1 , current.j  ));  }
                current = next;
                count++;
            } else {
                println("End");
                count = 0;
                flagPrintSolution = false;
                flagMazeGen = false;
            }
        }
    }
}
