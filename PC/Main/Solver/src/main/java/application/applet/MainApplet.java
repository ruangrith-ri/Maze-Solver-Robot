package application.applet;

import application.dataType.Cell;
import application.service.SerialEventBus;
import controlP5.ControlEvent;
import controlP5.ControlP5;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

import static application.dataType.Cell.mazeSolve;
import static application.dataType.Cell.solutionPrint;


public class MainApplet extends PApplet {

    public PApplet processing;

    ControlP5 cp5;

    ArrayList<Cell> stack = new ArrayList<>();
    static public ArrayList<Cell> grid = new ArrayList<>();

    Stack<Cell> stackMap = new Stack<>();
    static public ArrayList<Cell> map = new ArrayList<>();

    static public ArrayList<String> solution = new ArrayList<>();


    //Parameters for maze generation//
    public static int cols, rows; //Number of (column,row) = (width/w, height/w)
    public static int startI = 5; //Starting X-coordinate
    public static int startJ = 5; //Starting Y-coordinate
    int stroke = 0; //Stroke weight
    int w = 80; // Width of each grid
    int fork = 0; //Number of walls randomly removed in the middle of the maze
    int mazeWidth = 800;
    int mazeHeight = 800;

    //Parameters for maze solving//
    static int solveStartI = startI;
    static int solveStartJ = startJ;
    public static String exitDir;

    //Handshaking//

    boolean mode = true;
    boolean flagEnd = true;

    public static boolean flagSolve = false;
    public static boolean flagPrintSolution = false;

    public static boolean flagMazeGen = false;
    String nextDirection;

    //Initialization//
    Cell cell;
    public static Cell current;
    public static Cell next;

    public static Cell start = new Cell(solveStartI, solveStartJ);
    public static Cell finish = new Cell(0, 0);

    SerialEventBus serialEventBus;
    /*-------------------------------------------------------------------------------------------*/

    @Override
    public void settings() {
        size(1220, 800);
    }

    @Override
    public void setup() {
        Cell.setCanvas(super.g);
        processing = this;


        background(0);
        frameRate(60);

        //myPort = new Serial(this, Serial.list()[0], 9600);
        //myPort.bufferUntil('\n');
        cp5 = new ControlP5(this);

        String forkString = str(fork); //TODO: Update On Button Click

        cp5.addButton("Mode")
                .setPosition(950, 60)
                .setSize(200, 19);
        cp5.addButton("GenerateNewMaze")
                .setPosition(950, 100)
                .setSize(200, 19);
        cp5.addButton("PlayPause")
                .setPosition(950, 125)
                .setSize(200, 19);
        cp5.addButton("Minus")
                .setPosition(950, 200)
                .setSize(40, 40);
        cp5.addButton("Plus")
                .setPosition(1060, 200)
                .setSize(40, 40);
        cp5.addButton("MazeSolve")
                .setPosition(950, 250)
                .setSize(200, 19);

        cols = floor(mazeWidth / w);
        rows = floor(mazeHeight / w);

        initialize(grid);
        mazeGenInterface();
        createFork();
        createExit();
        initialize(map);

        serialEventBus = new SerialEventBus("COM8", 115200);
    }

    @Override
    public void draw() {

        if (flagMazeGen) {
            mapGen();
        }
        if (flagSolve) {
            mazeSolve(map);
        }
        if (flagPrintSolution) {
            solutionPrint();
        }
        mapUpdate();
        finish.highlightGreen(stroke, w);
        current.highlight(stroke, w);
    }

    /*-------------------------------------------------------------------------------------------*/

    public static class queueNode {
        public Cell point; // The coordinates of a cell

        public queueNode(Cell pointPos) {
            point = pointPos;
        }
    }

    /*-------------------------------------------------------------------------------------------*/

    public void controlEvent(ControlEvent theEvent) {
        //println(theEvent.getController().getName());
    }

    public void Mode(int theValue) {
        //modeButton.setLabel("Interface Mode");
        //modeButton.setLabel("Robot Mode");
        mode = ! mode;

        println(mode ? "Interface" : "Robot");
    }

    public void GenerateNewMaze(int theValue) {
        mazeGenInterface();
        createFork();
        createExit();
        mapClear(map);
       // flagEnd = true; TODO:ForMockUpMode
    }

    public void PlayPause(int theValue) {
        flagMazeGen = ! flagMazeGen;
        println(flagMazeGen ? "Play" : "Pause");
    }

    public void Plus(int theValue) {
        fork++;
        String forkString = str(fork);
        //forkCount.setLabel(forkString);
    }

    public void Minus(int theValue) {
        fork--;
        String forkString = str(fork);
        //forkCount.setLabel(forkString);
    }

    public void MazeSolve(int theValue) {
        flagSolve = true;
    }

    String[] wallReceive() {

        String[] wallSerial = new String[4];

        wallSerial[0] = serialEventBus.readNonContain("wallN");
        wallSerial[1] = serialEventBus.readNonContain("wallE");
        wallSerial[2] = serialEventBus.readNonContain("wallS");
        wallSerial[3] = serialEventBus.readNonContain("wallW");

        if(wallSerial[0] != ""){
            flagEnd = true;
        }

       // System.out.println("Wall Receive" + Arrays.toString(wallSerial));
        return wallSerial;
    }

    boolean[] parseWall(String[] wallSerial) {

        boolean[] incomingWall = {true, true, true, true};
        for (int i = 0; i < 4; i++) {
            if (wallSerial[i].equals("1")) {
                incomingWall[i] = false;
            } else if (wallSerial[i].equals("0")) {
                incomingWall[i] = true;
            }
        }
       //println("ParseWall" + Arrays.toString(incomingWall));
        //flagEnd = true;
        return incomingWall;

    }
    /*-------------------------------------------------------------------------------------------*/

    void mazeGenInterface() {
        mapClear(grid);
        while (allVisited(grid)) {

            //STEP 1: Mark current cell as visited
            //current.highlight();
            current.visited = true;

            //STEP 2: While there are unvisited cells
            //STEP 2.1: If the current cell has any neighbors which have not been visited

            //STEP 2.1.1: Choose randomly one of the unvisited neighbors
            next = current.checkNeighbors();
            if (next != null) {
                next.visited = true;

                //STEP 2.1.2: Push the current cell to the stack
                stack.add(current);

                //STEP 2.1.3: Remove the wall between the current cell and the chosen cell
                removeWalls(current, next);

                //STEP 2.1.4: Make the chosen cell the current cell and mark it as visited
                current = next;
            }

            //STEP 2.2: Else if stack is not empty
            else if (stack.size() > 0) {
                current = stack.remove(stack.size() - 1);
            }
        }
        visitedClear(grid);
    }

    boolean allVisited(ArrayList<Cell> grid) {
        for (int i = 0; i < grid.size(); i++) {
            if (! grid.get(i).visited) {
                return true;
            }
        }
        return false;
    }


//FUNCTIONS//

    //Function to update map display
    void gridUpdate() {
        background(255);
        for (int i = 0; i < grid.size(); i++) {
            grid.get(i).show(stroke, w);
        }
    }

    void mapUpdate() {
        background(255);
        for (int i = 0; i < grid.size(); i++) {
            map.get(i).show(stroke, w);
        }
    }

    //Function to create fork path
    void createFork() {
        for (int k = 0; k < fork; k++) {
            int random_i = floor(random(1, cols - 1));
            int random_j = floor(random(1, rows - 1));
            int randomIndex = findIndex(random_i, random_j);
            ArrayList<Integer> removableWalls = checkRemove(randomIndex);
            if (removableWalls.size() > 0) {
                int wallToRemove = removableWalls.get((int) random(0, removableWalls.size()));
                grid.get(randomIndex).walls[wallToRemove] = false;
                switch (wallToRemove) {
                    case 0:
                        grid.get(findIndex(random_i, random_j - 1)).walls[2] = false;
                        break;
                    case 1:
                        grid.get(findIndex(random_i + 1, random_j)).walls[3] = false;
                        break;
                    case 2:
                        grid.get(findIndex(random_i, random_j + 1)).walls[0] = false;
                        break;
                    case 3:
                        grid.get(findIndex(random_i - 1, random_j)).walls[1] = false;
                        break;
                }
            } else {
                k = k - 1;
            }
        }
    }

    void createExit() {
        int exitDir = floor(random(0, 3));
        int exitCell = floor(random(0, 9));
        switch (exitDir) {
            case 0:
                grid.get(findIndex(exitCell, 0)).walls[exitDir] = false;
            case 1:
                grid.get(findIndex(9, exitCell)).walls[exitDir] = false;
            case 2:
                grid.get(findIndex(exitCell, 9)).walls[exitDir] = false;
            case 3:
                grid.get(findIndex(0, exitCell)).walls[exitDir] = false;
        }

    }

    void exitCheck(Cell currentCell) {
        if (currentCell.j == 0 && ! currentCell.walls[0]) {
            finish = currentCell;
            exitDir = "N";
        }
        if (currentCell.i == 9 && ! currentCell.walls[1]) {
            finish = currentCell;
            exitDir = "E";
        }
        if (currentCell.j == 9 && ! currentCell.walls[2]) {
            finish = currentCell;
            exitDir = "S";
        }
        if (currentCell.i == 0 && ! currentCell.walls[3]) {
            finish = currentCell;
            exitDir = "W";
        }
    }

    //Function to check existing walls for removal. Return matrix whose indices indicates which side can be removed.
    ArrayList<Integer> checkRemove(int index) {
        ArrayList<Integer> removableWallIndice = new ArrayList<Integer>();
        for (int i = 0; i < 4; i++) {
            if (grid.get(index).walls[i]) {
                removableWallIndice.add(i);
            }
        }
        return removableWallIndice;
    }

    //Function to convert 1D array to 2D array//
    public static int findIndex(int i, int j) {
        if (i < 0 || j < 0 || i > cols - 1 || j > rows - 1) {
            return 0;
        }
        return i + j * cols;
    }

    //Function to remove walls//
    void removeWalls(Cell a, Cell b) {
        int x = a.i - b.i; //difference of i index of matrix a and b (x-axis)
        //A's west, B's east
        if (x == 1) {
            a.walls[3] = false;
            b.walls[1] = false;
        }
        //A's east, B's west
        else if (x == - 1) {
            a.walls[1] = false;
            b.walls[3] = false;
        }
        int y = a.j - b.j; //difference of j index of matrix a and b (y-axis)
        //A's north, B's south
        if (y == 1) {
            a.walls[0] = false;
            b.walls[2] = false;
        }
        //A's south, B's north
        else if (y == - 1) {
            a.walls[2] = false;
            b.walls[0] = false;
        }
    }

    void initialize(ArrayList<Cell> cellArray) {
        for (int j = 0; j < rows; j++) {
            for (int i = 0; i < cols; i++) {
                cell = new Cell(i, j);
                cellArray.add(cell);
            }
        }
        current = cellArray.get(findIndex(startI, startJ));
        finish = new Cell(0, 0);
    }

    void mapClear(ArrayList<Cell> cellArray) {
        cellArray.clear();
        initialize(cellArray);
    }

    void visitedClear(ArrayList<Cell> cellArray) {
        for (int j = 0; j < rows; j++) {
            for (int i = 0; i < cols; i++) {
                cellArray.get(findIndex(i, j)).visited = false;
            }
        }
        //gridUpdate();
    }

    void mapGen() {
        current.visited = true;
        exitCheck(current);
        boolean[] nextCellWall = new boolean[4];

        if (mode) {
            nextCellWall = grid.get(findIndex(current.i, current.j)).walls;
        } else if (! mode) {
            nextCellWall = parseWall(wallReceive());
        }

        if (flagMazeGen) {
            System.arraycopy(nextCellWall, 0, map.get(findIndex(current.i, current.j)).walls, 0, 4);

            Cell next = current.checkNeighborsRobot();

            if (next != null) {
                reportNextCell(current, next);
                next.visited = true;
                stackMap.push(current);
                current = next;
                exitCheck(current);
                println("A");
            } else if (stackMap.size() > 0) {
                reportNextCell(current, stackMap.peek());
                current = stackMap.pop();
                exitCheck(current);
                println("B");
            } else {
                flagEnd = false;
                println("C");
            }
           // println("flagMazeGen");
        }

        if (flagEnd) {
            println("FlagEnd : " + nextDirection);
            serialEventBus.send("direction", nextDirection);
            flagEnd = false;
        }
        //serialEventBus.send("direction", nextDirection);
    }

    void reportNextCell(Cell current, Cell next) {
        if (next.i == current.i && next.j == current.j - 1) {
            nextDirection = "N";
            //current.j -= 1;
            //current = next;
        } else if (next.i == current.i + 1 && next.j == current.j) {
            nextDirection = "E";
            //current.i += 1;
        } else if (next.i == current.i && next.j == current.j + 1) {
            nextDirection = "S";
            //current.j += 1;
        } else if (next.i == current.i - 1 && next.j == current.j) {
            nextDirection = "W";
            //current.i -= 1;
        }
    }
}
