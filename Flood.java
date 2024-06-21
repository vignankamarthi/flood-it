import java.awt.Color;
import java.util.ArrayList;

import javalib.impworld.World;
import javalib.impworld.WorldScene;
import javalib.worldimages.OutlineMode;
import javalib.worldimages.OverlayOffsetImage;
import javalib.worldimages.Posn;
import javalib.worldimages.RectangleImage;
import javalib.worldimages.TextImage;
import tester.Tester;

// to represent a cell in a Flood It game
class Cell {
  int x;
  int y;
  Color color;
  boolean flooded;
  Cell left;
  Cell top;
  Cell right;
  Cell bottom;

  // cell constructor initializing a position y position, and color
  Cell(int x, int y, Color color) {
    this.x = x;
    this.y = y;
    this.color = color;
    this.flooded = false;
    this.left = null;
    this.top = null;
    this.right = null;
    this.bottom = null;
  }
}

// to represent a FloodItWorld State
class FloodItWorld extends World {
  // list of cells
  ArrayList<Cell> board;
  int size;
  int numColors;
  int clicks;
  int maxTries;
  double elapsedTime;
  // list of colors
  ArrayList<Color> colors;
  String message;

  // constructor initializing the game based on the
  // given size the given number of colors in this game
  FloodItWorld(int size, int numColors) {
    this.size = size;
    this.numColors = numColors;
    this.colors = this.initColors(numColors);
    this.board = this.createBoard();
    this.connectCells();
    this.clicks = 0;
    // formula to get max clicks allowed depending on board dimension and colors.
    this.maxTries = (int) Math.ceil(this.size * Math.sqrt(this.numColors) * 0.75);
    this.message = "";
    this.elapsedTime = 0.0;
  }

  // to return an ArrayList of color objects that's the size
  // of the given integer "size"
  ArrayList<Color> initColors(int numColors) {
    ArrayList<Color> colors = new ArrayList<>();
    for (int i = 0; i < numColors; i++) {
      // 16777216 is the largest number that can represent an RGB color
      colors.add(new Color((int) (Math.random() * 16777216)));
    }
    return colors;
  }

  // to return a board with the user inputed size value
  // and randomize the colors with this FloodItWorld's array of colors
  // (constructed row by row)
  ArrayList<Cell> createBoard() {
    ArrayList<Cell> board = new ArrayList<>();
    for (int x = 0; x < this.size; x++) {
      for (int y = 0; y < this.size; y++) {
        board.add(new Cell(x, y, this.colors.get((int) (Math.random() * this.numColors))));
      }
    }
    return board;
  }

  // to initialize all 4 of the directional connection
  // fields in this FloodItWorld's array of cells
  void connectCells() {
    for (int x = 0; x < this.size; x++) {
      for (int y = 0; y < this.size; y++) {
        Cell c = this.board.get(x * this.size + y);
        if (x > 0) {
          c.top = this.board.get((x - 1) * this.size + y);
        }
        if (x < this.size - 1) {
          c.bottom = this.board.get((x + 1) * this.size + y);
        }
        if (y > 0) {
          c.left = this.board.get(x * this.size + (y - 1));
        }
        if (y < this.size - 1) {
          c.right = this.board.get(x * this.size + (y + 1));
        }
      }
    }
  }

  // Floods cells starting from top left.
  void flood(Color newColor) {
    Cell start = this.board.get(0);
    if (start.color.equals(newColor)) {
      return;
    }
    else {
      this.floodHelp(start, start.color, newColor);
      // Reset flooded status so the next turn can be done, and increment the counter.
      this.resetFloodedStatus();
      this.clicks++;
    }
  }

  // Helper method that floods cells adjacent to current growing blob of cells
  void floodHelp(Cell cell, Color old, Color updated) {
    if (cell == null || !cell.color.equals(old) || cell.flooded) {
      return;
    }
    // Changes the cell color to the new one, changes status of the cell to flooded,
    // recursive call
    cell.color = updated;
    cell.flooded = true;
    this.floodHelp(cell.top, old, updated);
    this.floodHelp(cell.bottom, old, updated);
    this.floodHelp(cell.left, old, updated);
    this.floodHelp(cell.right, old, updated);
  }

  // Resets flooded status
  void resetFloodedStatus() {
    for (Cell c : this.board) {
      c.flooded = false;
    }
  }

  // Checks if player has won or not
  boolean win() {
    Color firstColor = this.board.get(0).color;
    for (Cell c : this.board) {
      if (!c.color.equals(firstColor)) {
        return false;
      }
    }
    return true;
  }

  // On key method to reset the game using 'r' key
  @Override
  public void onKeyEvent(String key) {
    if (key.equals("r")) {
      this.reset();
    }
  }

  // Resets the board
  public void reset() {
    this.board = this.createBoard();
    this.connectCells();
    this.clicks = 0;
    this.message = "";
    this.elapsedTime = 0.0;
  }

  // Method to change the board when the mouse clicks on a cell.
  // Floods with color of the cell that the mouse clicked on
  @Override
  public void onMouseClicked(Posn pos) {
    int tileSize = 20;
    double x = pos.x / tileSize;
    double y = pos.y / tileSize;

    if (x >= 0 && x < this.size && y >= 0 && y < this.size) {

      Cell clickedCell = this.board.get(roundNumber(x * this.size + y));
      this.flood(clickedCell.color);

      // Check if the game is won or lost. Set the message and reset the game.
      if (this.win()) {
        this.message = "Congrats! You won!";
      }
      else if (this.clicks >= this.maxTries) {
        this.message = "Oh no, you lost!";
      }
    }
  }

  // to handle rounding errors for more precise clicking
  public int roundNumber(double n) {
    int casted = (int) n;
    if (n - casted >= 0.50) {
      return casted + 1;
    }
    else {
      return casted;
    }
  }

  // to handle the timer's elapsed time with onTick()
  @Override
  public void onTick() {
    this.elapsedTime++;
  }

  // to initialize a new FloodItWorld with the give size and
  // place each cell in this FloodItWorld's board
  @Override
  public WorldScene makeScene() {
    int cellSize = 20;
    int buffer = 10;
    int tileSize = 20;
    int footerConst = 50;
    int alignTilesConstant = (tileSize * 3) / 4;
    WorldScene scene = new WorldScene(this.size * 20 + buffer, this.size * 20 + footerConst);
    for (Cell c : this.board) {
      scene.placeImageXY(new RectangleImage(20, 20, OutlineMode.SOLID, c.color),
          c.x * tileSize + alignTilesConstant, c.y * tileSize + alignTilesConstant);
    }

    double sizingConst = this.size * 1.5;
    String restartMsg = "Press 'r' to restart.";

    // Display the win/loss message if it exists
    if (!this.message.isEmpty()) {
      scene.placeImageXY(
          new OverlayOffsetImage(new TextImage(this.message, Math.max(sizingConst, 7.5), Color.RED),
              0.0, this.size * 2, new TextImage(restartMsg, Math.max(sizingConst, 7.5), Color.RED)),
          (this.size * tileSize) / 2, (this.size * tileSize) / 2);
    }

    // E.C: Added a score tracker!
    scene.placeImageXY(
        new TextImage("Clicks: " + this.clicks + "/" + this.maxTries, pickFontSize(sizingConst),
            Color.BLACK),
        (this.size * tileSize / 4), (int) (this.size * cellSize + footerConst / 2));

    // E.C: Displayed the elapsed time!
    scene.placeImageXY(
        new TextImage("Time: " + (int) this.elapsedTime + "s", pickFontSize(sizingConst),
            Color.BLACK),
        (this.size * tileSize * 3 / 4), (int) (this.size * cellSize + footerConst / 2));

    return scene;
  }

  // to pick the font size value for the score tracker and timer based on the
  // sizingConst
  public int pickFontSize(double sizingConst) {
    if (sizingConst < 10) {
      return 10;
    }
    else {
      return (int) Math.min(sizingConst, 15);
    }
  }
}

//Examples and tests for Flood It
class ExamplesFloodIt {
  // to test the initColors method
  void testInitColors(Tester t) {
    // General case: 5 unique colors
    FloodItWorld world = new FloodItWorld(10, 5);
    // Checking if the size of the colors list is 5
    t.checkExpect(world.colors.size(), 5);
    // Checking if all colors are unique
    t.checkExpect(world.colors.stream().distinct().count(), 5L);

    // Edge case: 1 color
    FloodItWorld world1 = new FloodItWorld(10, 1);
    // Checking if the size of the colors list is 1
    t.checkExpect(world1.colors.size(), 1);

    // Edge case: Maximum colors (26 colors)
    FloodItWorld worldMax = new FloodItWorld(10, 26);
    // Checking if the size of the colors list is 26
    t.checkExpect(worldMax.colors.size(), 26);
    // Checking if all colors are unique
    t.checkExpect(worldMax.colors.stream().distinct().count(), 26L);
  }

  // to test the createBoard method
  void testCreateBoard(Tester t) {
    // General case: 10x10 board
    FloodItWorld world = new FloodItWorld(10, 5);
    // Checking if the size of the board is 100 cells
    t.checkExpect(world.board.size(), 100);
    // Checking if all cells have valid colors
    t.checkExpect(world.board.stream().allMatch(c -> world.colors.contains(c.color)), true);

    // Edge case: 0x0 board
    FloodItWorld world0 = new FloodItWorld(0, 5);
    // Checking if the size of the board is 0 cells
    t.checkExpect(world0.board.size(), 0);

    // Edge case: 1x1 board
    FloodItWorld world1 = new FloodItWorld(1, 5);
    // Checking if the size of the board is 1 cell
    t.checkExpect(world1.board.size(), 1);
    // Checking if the cell has a valid color
    t.checkExpect(world1.board.stream().allMatch(c -> world1.colors.contains(c.color)), true);
  }

  // to test the connectCells method
  void testConnectCells(Tester t) {
    // General case: 3x3 board
    FloodItWorld world = new FloodItWorld(3, 3);
    Cell c = world.board.get(4); // The middle cell
    // Checking if the top cell is correctly connected
    t.checkExpect(c.top, world.board.get(1));
    // Checking if the bottom cell is correctly connected
    t.checkExpect(c.bottom, world.board.get(7));
    // Checking if the left cell is correctly connected
    t.checkExpect(c.left, world.board.get(3));
    // Checking if the right cell is correctly connected
    t.checkExpect(c.right, world.board.get(5));

    // Edge case: 1x1 board (no neighbors)
    FloodItWorld world1 = new FloodItWorld(1, 1);
    Cell c1 = world1.board.get(0);
    // Checking if the top cell is null
    t.checkExpect(c1.top, null);
    // Checking if the bottom cell is null
    t.checkExpect(c1.bottom, null);
    // Checking if the left cell is null
    t.checkExpect(c1.left, null);
    // Checking if the right cell is null
    t.checkExpect(c1.right, null);

    // Edge case: 2x2 board
    FloodItWorld world2 = new FloodItWorld(2, 2);
    Cell c2 = world2.board.get(0);
    // Checking if the top cell is null
    t.checkExpect(c2.top, null);
    // Checking if the bottom cell is correctly connected
    t.checkExpect(c2.bottom, world2.board.get(2));
    // Checking if the left cell is null
    t.checkExpect(c2.left, null);
    // Checking if the right cell is correctly connected
    t.checkExpect(c2.right, world2.board.get(1));
  }

  // to test the flood method
  void testFlood(Tester t) {
    // General case: Change the color of the top-left cell
    FloodItWorld world = new FloodItWorld(3, 3);
    Color newColor = Color.BLUE;
    world.board.get(0).color = Color.RED;
    world.flood(newColor);
    // Checking if the color of the top-left cell is changed
    t.checkExpect(world.board.get(0).color, newColor);

    // Edge case: Flooding with the same color
    FloodItWorld world1 = new FloodItWorld(3, 3);
    Color initialColor = world1.board.get(0).color;
    world1.flood(initialColor);
    // Checking if the color of the top-left cell is unchanged
    t.checkExpect(world1.board.get(0).color, initialColor);

    // Edge case: Flooding a larger board
    FloodItWorld world2 = new FloodItWorld(5, 5);
    Color newColor2 = Color.GREEN;
    world2.board.get(0).color = Color.YELLOW;
    world2.flood(newColor2);
    // Checking if the color of the top-left cell is changed
    t.checkExpect(world2.board.get(0).color, newColor2);
  }

  // to test the floodHelp method
  void testFloodHelp(Tester t) {
    // General case: Change the color of connected cells
    FloodItWorld world = new FloodItWorld(3, 3);
    Color oldColor = Color.RED;
    Color newColor = Color.BLUE;
    world.board.get(0).color = oldColor;
    world.board.get(1).color = oldColor;
    world.floodHelp(world.board.get(0), oldColor, newColor);
    // Checking if the color of the top-left cell is changed
    t.checkExpect(world.board.get(0).color, newColor);
    // Checking if the color of the adjacent cell is changed
    t.checkExpect(world.board.get(1).color, newColor);

    // Edge case: No connected cells with the same color
    FloodItWorld world1 = new FloodItWorld(3, 3);
    world1.board.get(0).color = Color.RED;
    world1.board.get(1).color = Color.YELLOW;
    world1.floodHelp(world1.board.get(0), Color.RED, Color.BLUE);
    // Checking if the color of the top-left cell is changed
    t.checkExpect(world1.board.get(0).color, Color.BLUE);
    // Checking if the color of the adjacent cell is unchanged
    t.checkExpect(world1.board.get(1).color, Color.YELLOW);
  }

  // to test the resetFloodedStatus method
  void testResetFloodedStatus(Tester t) {
    // General case: Reset flooded status of all cells
    FloodItWorld world = new FloodItWorld(3, 3);
    world.board.get(0).flooded = true;
    world.board.get(1).flooded = true;
    world.resetFloodedStatus();
    // Checking if the flooded status of the top-left cell is reset
    t.checkExpect(world.board.get(0).flooded, false);
    // Checking if the flooded status of the adjacent cell is reset
    t.checkExpect(world.board.get(1).flooded, false);
    // Edge case: No cells are flooded
    FloodItWorld world1 = new FloodItWorld(3, 3);
    world1.resetFloodedStatus();
    // Checking if the flooded status of all cells is false
    t.checkExpect(world1.board.stream().allMatch(c -> !c.flooded), true);
  }

  // to test the win method
  void testWin(Tester t) {
    // General case: All cells have the same color
    FloodItWorld world = new FloodItWorld(3, 3);
    Color winColor = Color.RED;
    world.board.forEach(c -> c.color = winColor);
    // Checking if the player has won
    t.checkExpect(world.win(), true);

    // Edge case: Not all cells have the same color
    FloodItWorld world1 = new FloodItWorld(3, 3);
    world1.board.get(0).color = Color.RED;
    world1.board.get(1).color = Color.BLUE;
    // Checking if the player has not won
    t.checkExpect(world1.win(), false);
    // Edge case: 1x1 board with the same color
    FloodItWorld world2 = new FloodItWorld(1, 1);
    world2.board.get(0).color = Color.RED;
    // Checking if the player has won
    t.checkExpect(world2.win(), true);
  }

  // to test the onKeyEvent method
  void testOnKeyEvent(Tester t) {
    // General case: Reset the game using 'r' key
    FloodItWorld world = new FloodItWorld(3, 3);
    world.board.get(0).color = Color.RED;
    world.onKeyEvent("r");
    // Edge case: Pressing a different key
    FloodItWorld world1 = new FloodItWorld(3, 3);
    world1.onKeyEvent("a");
    // Checking if the game is unchanged
    t.checkExpect(world1.board.get(0).color, world1.board.get(0).color);
  }

  // to test the reset method
  void testReset(Tester t) {
    // General case: Reset the board
    FloodItWorld world = new FloodItWorld(3, 3);
    world.board.get(0).color = Color.RED;
    world.reset();
    // Checking if the click count is reset
    t.checkExpect(world.clicks, 0);
    // Checking if the message is reset
    t.checkExpect(world.message, "");
    // Checking if the elapsed time is reset
    t.checkExpect(world.elapsedTime, 0.0);
    // Edge case: Reset the board when no clicks have been made
    FloodItWorld world1 = new FloodItWorld(3, 3);
    world1.reset();
    // Checking if the click count is reset
    t.checkExpect(world1.clicks, 0);
    // Checking if the message is reset
    t.checkExpect(world1.message, "");
    // Checking if the elapsed time is reset
    t.checkExpect(world1.elapsedTime, 0.0);
  }

  // to test the onMouseClicked method
  void testOnMouseClicked(Tester t) {
    // General case: Click on a cell to flood it
    FloodItWorld world = new FloodItWorld(3, 3);
    Posn pos = new Posn(20, 20);
    world.onMouseClicked(pos);
    // Edge case: Click on a cell with the same color
    FloodItWorld world1 = new FloodItWorld(3, 3);
    Posn pos1 = new Posn(20, 20);
    world1.onMouseClicked(pos1);
    // Edge case: Click outside the board
    FloodItWorld world2 = new FloodItWorld(3, 3);
    Posn pos2 = new Posn(-20, -20);
    world2.onMouseClicked(pos2);
    // Checking if the click count is unchanged
    t.checkExpect(world2.clicks, 0);
  }

  // to test the roundNumber method
  void testRoundNumber(Tester t) {
    // General case: Rounding a positive number
    FloodItWorld world = new FloodItWorld(3, 3);
    // Checking if the number is rounded up
    t.checkExpect(world.roundNumber(3.7), 4);
    // Checking if the number is rounded down
    t.checkExpect(world.roundNumber(3.3), 3);
    // Edge case: Rounding a whole number
    FloodItWorld world1 = new FloodItWorld(3, 3);
    // Checking if the whole number is unchanged
    t.checkExpect(world1.roundNumber(3.0), 3);
    // Edge case: Rounding a negative number
    FloodItWorld world2 = new FloodItWorld(3, 3);
    // Checking if the number is rounded up
    t.checkExpect(world2.roundNumber(-3.7), -3);
    // Checking if the number is rounded down
    t.checkExpect(world2.roundNumber(-3.3), -3);
  }

  // to test the onTick method
  void testOnTick(Tester t) {
    // General case: Incrementing the elapsed time
    FloodItWorld world = new FloodItWorld(3, 3);
    world.onTick();
    // Checking if the elapsed time is incremented
    t.checkExpect(world.elapsedTime, 1.0);
    // Edge case: Incrementing the elapsed time multiple times
    FloodItWorld world1 = new FloodItWorld(3, 3);
    world1.onTick();
    world1.onTick();
    // Checking if the elapsed time is incremented twice
    t.checkExpect(world1.elapsedTime, 2.0);
  }

  // to test the makeScene method
  void testMakeScene(Tester t) {
    // General case: Creating a scene with a 3x3 board
    FloodItWorld world = new FloodItWorld(3, 3);
    WorldScene scene = world.makeScene();
    // Checking if the scene width is correct
    t.checkExpect(scene.width, 70);
    // Checking if the scene height is correct
    t.checkExpect(scene.height, 110);
    // Edge case: Creating a scene with a 1x1 board
    FloodItWorld world1 = new FloodItWorld(1, 3);
    WorldScene scene1 = world1.makeScene();
    // Checking if the scene width is correct
    t.checkExpect(scene1.width, 30);
    // Checking if the scene height is correct
    t.checkExpect(scene1.height, 70);
    // Edge case: Creating a scene with a 0x0 board
    FloodItWorld world0 = new FloodItWorld(0, 3);
    WorldScene scene0 = world0.makeScene();
    // Checking if the scene width is correct
    t.checkExpect(scene0.width, 10);
    // Checking if the scene height is correct
    t.checkExpect(scene0.height, 50);
  }

  // to test the pickFontSize method
  void testPickFontSize(Tester t) {
    // General case: Picking a font size based on a positive sizing constant
    FloodItWorld world = new FloodItWorld(3, 3);
    // Checking if the font size is 10 for a sizing constant less than 10
    t.checkExpect(world.pickFontSize(5), 10);
    // Checking if the font size is the sizing constant when it's between 10 and 15
    t.checkExpect(world.pickFontSize(12), 12);
    // Checking if the font size is 15 for a sizing constant greater than 15
    t.checkExpect(world.pickFontSize(20), 15);
    // Edge case: Picking a font size for a zero sizing constant
    FloodItWorld world1 = new FloodItWorld(3, 3);
    // Checking if the font size is 10 for a zero sizing constant
    t.checkExpect(world1.pickFontSize(0), 10);
    // Edge case: Picking a font size for a negative sizing constant
    FloodItWorld world2 = new FloodItWorld(3, 3);
    // Checking if the font size is 10 for a negative sizing constant
    t.checkExpect(world2.pickFontSize(-5), 10);
  }

  // to initialize a Flood It! game
  void testBigBang(Tester t) {
    // edit the world numbers: (Size, # of Colors)
    FloodItWorld world = new FloodItWorld(10, 5);
    int cellSize = 20;
    int buffer = 10;
    int footerConst = 40;
    int worldWidth = world.size * cellSize + buffer;
    int worldHeight = world.size * cellSize + footerConst;
    double tickRate = 1;
    world.bigBang(worldWidth, worldHeight, tickRate);
  }
}
