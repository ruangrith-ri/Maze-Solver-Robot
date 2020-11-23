class Cell {
  private int columnIndex, rowIndex;

  public boolean isVisit = false;

  public Result N;
  public Result E;
  public Result W;
  public Result S;

  public Cell(int columnIndex, int rowIndex) {
    this.columnIndex = columnIndex;
    this.rowIndex = rowIndex;
  }

  public Cell visit() {
    isVisit = true;
    return this;
  }

  public Cell setN(Result result) {
    N = result;
    return this;
  }

  public Cell setS(Result result) {
    S = result;
    return this;
  }

  public Cell setE(Result result) {
    E = result;
    return this;
  }

  public Cell setW(Result result) {
    W = result;
    return this;
  }

  public boolean isSurveyComplete() {
    return (N != null && S != null && E != null && W != null);
  }
}
