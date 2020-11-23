void setup() {
  
}

int manhattanDistance(int currentX, int currentY, int goalX, int goalY) {
  return abs(currentX - goalX) + abs(currentY - goalY);
}
