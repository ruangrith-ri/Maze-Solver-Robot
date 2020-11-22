import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

Table<Integer, Integer, Cell> mazeData = HashBasedTable.create();

void setup() {
  mazeData.put(0, 0, new Cell(0, 0));

  Cell a = new Cell(1, 0);
  a.visit();
  mazeData.put(1, 0, a);

  println(mazeData);
  println(mazeData.get(0, 0).isSurveyComplete());
  println(mazeData.get(1, 0).isSurveyComplete());
}

void draw() {
}
