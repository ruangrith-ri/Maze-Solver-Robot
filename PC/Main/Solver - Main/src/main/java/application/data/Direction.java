package application.data;

public enum Direction {
    NORTH,
    EAST,
    SOUTH,
    WAST;

    public String toStringCommand() {
        String convert = super.toString();

        switch (convert) {
            case "NORTH":
                return "N";
            case "EAST":
                return "E";
            case "SOUTH":
                return "S";
            case "WAST":
                return "W";
            default:
                return "";
        }
    }

    public Direction flip(){
        switch (this){
            case NORTH:
                return SOUTH;
            case EAST:
                return WAST;
            case SOUTH:
                return NORTH;
            case WAST:
                return EAST;
        }
        return null;
    }

    public int toColumnFactor() {
        switch (this) {
            case EAST:
                return 1;
            case WAST:
                return - 1;
            case NORTH:
            case SOUTH:
            default:
                return 0;
        }
    }

    public int toRowFactor() {
        switch (this) {
            case NORTH:
                return - 1;
            case SOUTH:
                return 1;
            case EAST:
            case WAST:
            default:
                return 0;
        }
    }
}
