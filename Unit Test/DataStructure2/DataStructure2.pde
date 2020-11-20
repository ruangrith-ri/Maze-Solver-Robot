import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

Table<Integer, Integer, String> mazeData = HashBasedTable.create();

void setup() {
  mazeData.put(1, 1, "a");
  mazeData.put(1, 2, "b");

  println(mazeData);
  println(mazeData.get(1, 1));
  println(mazeData.get(1, 2));
}

void draw() {
}
