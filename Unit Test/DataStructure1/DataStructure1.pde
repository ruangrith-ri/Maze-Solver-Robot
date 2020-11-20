import java.util.HashMap;

HashMap<Index, String> mazeData = new HashMap<Index, String>();

void setup() {
  mazeData.put(new Index(1,1),"DataOne");
  mazeData.put(new Index(1,2),"DataTwo");
  
  println(mazeData);
  println(mazeData.containsKey(new Index(1,1)));
}

void draw() {
}
