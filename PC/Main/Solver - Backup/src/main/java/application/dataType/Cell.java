package application.dataType;

public class Cell {
    public int columnIndex, rowIndex;

    public boolean isVisit = false;
    public boolean isSolutionPath = false;

    public Result N;
    public Result E;
    public Result W;
    public Result S;

    public Cell(int columnIndex, int rowIndex) {
        this.columnIndex = columnIndex;
        this.rowIndex = rowIndex;
    }

    public application.dataType.Cell visit() {
        isVisit = true;
        return this;
    }

    public application.dataType.Cell setN(Result result) {
        N = result;
        return this;
    }

    public application.dataType.Cell setS(Result result) {
        S = result;
        return this;
    }

    public application.dataType.Cell setE(Result result) {
        E = result;
        return this;
    }

    public application.dataType.Cell setW(Result result) {
        W = result;
        return this;
    }

    public boolean isSurveyComplete() {
        return (N != null && S != null && E != null && W != null);
    }
}


