import java.util.Random;
import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;
import java.util.ArrayList;

// represent a minesweeper cell object
class Cell {
  boolean hidden;
  boolean mine;
  ArrayList<Cell> neighbors;
  boolean flag;

  // construct an instance of a cell object
  Cell(boolean hidden, boolean mine, ArrayList<Cell> neighbors) {
    this.hidden = hidden;
    this.mine = mine;
    this.neighbors = neighbors;
    this.flag = false;
  }

  // construct a cell with an empty list of neighbors
  Cell(boolean hidden, boolean mine) {
    this(hidden, mine, new ArrayList<>());
  }

  // update the 'neighbors' field of this cell to include some other cell.
  // furthermore, update the neighboring cell's 'neighbors' field to include this
  // cell.
  void updateNeighbors(Cell neighbor) {
    this.updateNeighborsHelp(neighbor);
    neighbor.updateNeighborsHelp(this);
  }

  // helper method for update neighbors.
  // this allows us to update both cells' 'neighbors' fields to include each other
  void updateNeighborsHelp(Cell neighbor) {
    this.neighbors.add(neighbor);
  }

  // count the number of mines that neighbor this cell
  int countNeighboringMines() {
    int numNeighboringMines = 0;
    for (Cell neighbor : this.neighbors) {
      if (neighbor.mine) {
        numNeighboringMines += 1;
      }
    }
    return numNeighboringMines;
  }

  // make this cell visible if it is hidden
  void makeVisible() {
    if (this.hidden) {
      this.hidden = false;
      if (this.countNeighboringMines() == 0) {
        for (Cell neighbor : this.neighbors) {
          if (neighbor.countNeighboringMines() == 0) {
            neighbor.makeVisible();
          }
        }
      }
    }
  }
}

// Examples for Cells
class ExamplesCell {
  Cell safe;
  Cell safe2;
  Cell mine;
  Cell mine2;
  ArrayList<Cell> mineNeighbor;
  ArrayList<Cell> safeNeighbor;
  ArrayList<Cell> mineAndSafeNeighbors;

  // initialize variables
  void init() {
    safe = new Cell(true, false);
    safe2 = new Cell(false, false);
    mine = new Cell(true, true);
    mine2 = new Cell(false, true);

    safeNeighbor = new ArrayList<>();
    safeNeighbor.add(safe);

    mineNeighbor = new ArrayList<>();
    mineNeighbor.add(mine);

    mineAndSafeNeighbors = new ArrayList<>();
    mineAndSafeNeighbors.add(mine);
    mineAndSafeNeighbors.add(safe2);
    mineAndSafeNeighbors.add(mine2);
  }

  // test the updateNeighbors method
  void testUpdateNeighbors(Tester t) {
    init();

    t.checkExpect(safe.neighbors, new ArrayList<Cell>());
    // testing on a cell with no neighbors

    safe.updateNeighbors(mine);
    t.checkExpect(safe.neighbors, mineNeighbor);
    t.checkExpect(mine.neighbors, safeNeighbor);
    // updating the safe cell's list of neighbors to include the mine cell should
    // also update the mine's list of neighbors to include the safe cell

    safe.updateNeighbors(safe2);
    safe.updateNeighbors(mine2);
    t.checkExpect(safe.neighbors, mineAndSafeNeighbors);
    // testing on a cell with multiple neighbors.
    // this test incorporates all 4 types of cells:
    // visible safe, hidden safe, visible mine, and hidden mine
  }

  void testCountNeighboringMines(Tester t) {
    init();

    t.checkExpect(safe.countNeighboringMines(), 0);
    // testing on a safe cell with no neighbors

    safe.updateNeighbors(mine);
    t.checkExpect(safe.countNeighboringMines(), 1);
    // testing on a safe cell that borders a mine

    safe.updateNeighbors(safe2);
    safe.updateNeighbors(mine2);
    t.checkExpect(safe.countNeighboringMines(), 2);
    // testing on a safe cell with multiple neighboring mines.
    // this test incorporates all 4 types of cells:
    // visible safe, hidden safe, visible mine, and hidden mine

    t.checkExpect(mine.countNeighboringMines(), 0);
    // testing on a mine with no neighboring mines

    mine.updateNeighbors(mine2);
    t.checkExpect(mine.countNeighboringMines(), 1);
    // testing on a mine with a neighboring mines
  }
}

// represent a random mine generator
class MineGenerator {
  Random rand;
  int gridWidth;
  int gridHeight;
  int numMines;
  ArrayList<Boolean> mineArray;

  // construct an instance of a random mine generator
  MineGenerator(int gridWidth, int gridHeight, int numMines) {
    this.rand = new Random();
    this.gridWidth = gridWidth;
    this.gridHeight = gridHeight;
    this.numMines = numMines;
    this.mineArray = makeMineArray();
  }

  // constructor with random seed argument
  MineGenerator(int gridWidth, int gridHeight, int numMines, int seed) {
    this(gridWidth, gridHeight, numMines);
    this.rand = new Random(seed);
  }

  // make a boolean arraylist whose elements represent
  // whether to make the next generated cell a mine
  ArrayList<Boolean> makeMineArray() {
    mineArray = new ArrayList<>();
    for (int i = 0; i < this.gridWidth * this.gridHeight; i++) {
      if (i < numMines) {
        mineArray.add(true);
      }
      else {
        mineArray.add(false);
      }
    }
    return mineArray;
  }

  // choose a random boolean from the makeMineArray arraylist
  boolean nextMine() {
    int randomIndex = rand.nextInt(mineArray.size());
    boolean createMine = this.mineArray.get(randomIndex);
    this.mineArray.remove(randomIndex);
    return createMine;
  }
}

// examples for mineGenerator
class ExamplesMineGenerator {
  MineGenerator mg;
  ArrayList<Boolean> mineArray;

  // initialize variables
  void init() {
    mg = new MineGenerator(3, 3, 2, 9);
    mineArray = new ArrayList<>();

    mineArray.add(true);
    mineArray.add(true);
    mineArray.add(false);
    mineArray.add(false);
    mineArray.add(false);
    mineArray.add(false);
    mineArray.add(false);
    mineArray.add(false);
    mineArray.add(false);
  }

  // test the nextMine method
  void testNextMine(Tester t) {
    init();
    t.checkExpect(mg.nextMine(), true);
    t.checkExpect(mg.nextMine(), false);
    t.checkExpect(mg.nextMine(), false);

    t.checkExpect(mg.nextMine(), false);
    t.checkExpect(mg.nextMine(), false);
    t.checkExpect(mg.nextMine(), false);

    t.checkExpect(mg.nextMine(), false);
    t.checkExpect(mg.nextMine(), false);
    t.checkExpect(mg.nextMine(), true);

    t.checkException("calling next mine when the mine arraylist is empty",
        new IllegalArgumentException("bound must be positive"), mg, "nextMine");
    /*
     * these checkExpects test that game board will look like the following:
     * 
     * X O O O O O O O X
     * 
     * (X = mine, O = safe)
     */
  }

  // test the mineArray method
  boolean testMineArray(Tester t) {
    init();
    return t.checkExpect(mg.mineArray, mineArray)
        /*
         * test that the produced game board looks like the following:
         * 
         * X O O O O O O O X
         * 
         * (X = mine, O = safe)
         */
        ;
  }
}

// represent a mine sweeper game board
class Board extends World {
  ArrayList<ArrayList<Cell>> cellGrid;
  int gridWidth;
  int gridHeight;
  int cellWidth;
  int cellHeight;
  MineGenerator mineGen;
  int timer;
  boolean gameEnd;
  int numMines;

  // construct a representation of a Minesweeper game board using native Java
  // objects
  Board(int gridWidth, int gridHeight, int numMines) {
    this.cellGrid = new ArrayList<>();
    this.gridHeight = gridHeight;
    this.gridWidth = gridWidth;
    this.cellWidth = 50;
    this.cellHeight = 50;
    this.timer = 0;
    this.gameEnd = false;
    this.numMines = numMines;

    checkSize(gridWidth, gridHeight);

    this.mineGen = new MineGenerator(gridWidth, gridHeight, numMines);

    createBoard(gridWidth, gridHeight);
  }

  // constructor that takes a random seed to pass to the mine generator
  Board(int gridWidth, int gridHeight, int numMines, int seed) {
    this(gridWidth, gridHeight, numMines);

    checkSize(gridWidth, gridHeight);

    this.cellGrid = new ArrayList<>();
    this.mineGen = new MineGenerator(gridWidth, gridHeight, numMines, seed);

    createBoard(gridWidth, gridHeight);
  }

  // ensure that the board is constructed with possible Minesweeper grid
  // dimensions
  void checkSize(int gridWidth, int gridHeight) {
    if (gridWidth < 1 || gridHeight < 1) {
      throw new IllegalArgumentException("grid dimensions are too small");
    }
  }

  // create an ArrayList representation of the Minesweeper board
  void createBoard(int gridWidth, int gridHeight) {

    for (int row = 0; row < gridHeight; row++) {
      ArrayList<Cell> cellRow = new ArrayList<>();
      for (int col = 0; col < gridWidth; col++) {
        // decide if the next cell added to the board will be a mine
        Cell cell = new Cell(true, this.mineGen.nextMine());
        cellRow.add(cell);

        // update horizontal neighboring cells
        if (0 < col) {
          cell.updateNeighbors(cellRow.get(col - 1));
        }
      }
      this.cellGrid.add(cellRow);
      // update vertical and vertical-diagonal neighbors
      updateVertNeighbor(this.cellGrid);
    }
  }

  // update cells' 'neighbor' fields to include the cells that neighbor it
  // vertically
  void updateVertNeighbor(ArrayList<ArrayList<Cell>> cellGrid) {

    // a cell will have a top neighbor iff there is more than 1 row in the grid
    if (1 < cellGrid.size()) {

      // the last and second to last rows, respectively
      ArrayList<Cell> tailRow = cellGrid.get(cellGrid.size() - 1);
      ArrayList<Cell> tailRowNeighbor = cellGrid.get(cellGrid.size() - 2);

      // iterate over each cell in the last row and update its 'neighbor' field
      // to include the cell directly above and diagonally bordering from above.
      // this will also update the above cell's 'neighbor' field to include the below
      // cell.
      for (int colIndex = 0; colIndex < tailRow.size(); colIndex++) {
        Cell cell = tailRow.get(colIndex);
        // update top neighbor
        Cell topNeighbor = tailRowNeighbor.get(colIndex);
        cell.updateNeighbors(topNeighbor);
        // update top left neighbor
        if (colIndex > 0) {
          Cell diagCell = tailRowNeighbor.get(colIndex - 1);
          cell.updateNeighbors(diagCell);
        }
        // update top right neighbor
        if (colIndex < tailRow.size() - 1) {
          Cell diagCell = tailRowNeighbor.get(colIndex + 1);
          cell.updateNeighbors(diagCell);
        }
      }
    }
  }

  // initialize the layout for an empty world scene
  WorldScene initializeWorld() {
    // initialize an empty world
    WorldScene world = new WorldScene(this.gridWidth * this.cellWidth,
        this.gridHeight * this.cellHeight);
    // draw the board background
    RectangleImage boardBackground = new RectangleImage(this.gridWidth * this.cellWidth,
        this.gridHeight * this.cellHeight, OutlineMode.SOLID, new Color(200, 200, 200));
    world.placeImageXY(boardBackground, this.gridWidth * (this.cellWidth / 2),
        this.gridHeight * (this.cellHeight / 2));
    return world;
  }

  // draw the mine sweeper game board as a WorldScene
  public WorldScene makeScene() {
    // initialize an empty world
    WorldScene world = initializeWorld();

    // draw the timer
    drawTimer(world);
    // draw flag count
    drawFlagCount(world);
    // Iterate over the cells in the cellGrid and outline each cell
    for (int row = 0; row < this.gridHeight; row++) {
      WorldImage cellImage;
      for (int col = 0; col < this.gridWidth; col++) {
        Cell cellToDraw = this.cellGrid.get(row).get(col);
        if (cellToDraw.hidden) {
          cellImage = drawHiddenCell(cellToDraw.flag);
        }
        else {
          cellImage = drawVisibleCell(cellToDraw.mine, numNeighboringMines(cellToDraw),
              cellToDraw.flag);
        }
        world.placeImageXY(cellImage, col * this.cellWidth + this.cellWidth / 2,
            row * this.cellHeight + this.cellHeight / 2);
      }
      if (this.gameEnd) {
        gameOverScene(world);
      }
    }
    return world;
  }

  // count the number of hidden cells remaining on the board
  int countHiddenCells() {
    int hiddenCells = 0;
    for (ArrayList<Cell> cellRow : this.cellGrid) {
      for (Cell cell : cellRow) {
        if (cell.hidden) {
          hiddenCells += 1;
        }
      }
    }
    return hiddenCells;
  }

  // has the user uncovered all cells that aren't mines?
  boolean foundAllMines() {
    return countHiddenCells() == this.numMines;
  }

  // ******EXTRA CREDIT*****
  // draw the end-of-game scene
  void gameOverScene(WorldScene world) {
    RectangleImage endBackground = new RectangleImage(160, 150, OutlineMode.SOLID,
        new Color(255, 255, 255, 100));

    TextImage gameOver;
    if (foundAllMines()) {
      gameOver = new TextImage("You Win!", 20, FontStyle.BOLD, new Color(0, 200, 100));
    }
    else {
      gameOver = new TextImage("Game Over!", 20, FontStyle.BOLD, Color.RED);
    }

    WorldImage levelSelect = levelSelect();

    WorldImage gameOverImage = new OverlayOffsetImage(levelSelect, 0, -this.cellHeight, gameOver);
    WorldImage endScene = new OverlayOffsetImage(gameOverImage, 0, 15, endBackground);
    world.placeImageXY(endScene, this.gridWidth * this.cellWidth / 2,
        this.gridHeight * this.cellHeight / 2);
  }

  // ******EXTRA CREDIT*****
  // draw the timer onto the scene
  void drawTimer(WorldScene world) {
    String timerText = "Timer: " + this.timer;
    TextImage timerImage = new TextImage(timerText, 20, Color.BLACK);
    // draw the timer
    world.placeImageXY(timerImage, this.gridWidth * this.cellWidth / 4,
        (this.gridHeight + 1) * this.cellHeight - (this.cellHeight / 2));
  }

  // ******EXTRA CREDIT*****
  // draw the number of flags remaining
  void drawFlagCount(WorldScene world) {
    String flagCount = "=" + Integer.toString(this.numMines - numFlags());
    TextImage flagCountImage = new TextImage(flagCount, 20, Color.BLACK);
    WorldImage flagsRemaining = new OverlayOffsetAlign(AlignModeX.CENTER, AlignModeY.MIDDLE,
        drawFlag(), 20, 0, flagCountImage);
    world.placeImageXY(flagsRemaining, 3 * this.gridWidth * this.cellWidth / 4,
        (this.gridHeight + 1) * this.cellHeight - (this.cellHeight / 2));
  }

  // ******EXTRA CREDIT*****
  // menu for selecting level difficulty
  WorldImage levelSelect() {
    TextImage press = new TextImage("Press:", 14, Color.BLACK);
    TextImage beginner = new TextImage("b for beginner", 14, Color.BLACK);
    TextImage intermediate = new TextImage("i  for intermediate", 14, Color.BLACK);
    TextImage expert = new TextImage("e for expert", 14, Color.BLACK);
    WorldImage endText;
    WorldImage levelOptions;
    levelOptions = new OverlayOffsetAlign(AlignModeX.LEFT, AlignModeY.TOP, beginner, 2, 16,
        intermediate);
    levelOptions = new OverlayOffsetAlign(AlignModeX.LEFT, AlignModeY.TOP, levelOptions, 1, 24,
        expert);
    endText = new OverlayOffsetImage(press, 50, 40, levelOptions);
    return endText;
  }

  // draw a cell with its number of neighboring mines displayed
  OverlayImage drawSafe(int numNeighboringMines) {
    String countMines = "";
    Color textColor;
    // ******EXTRA CREDIT*****
    if (numNeighboringMines > 0) {
      countMines = Integer.toString(numNeighboringMines);
    }

    if (numNeighboringMines == 1) {
      textColor = new Color(19, 37, 235);
    }
    else if (numNeighboringMines == 2) {
      textColor = new Color(38, 145, 16);
    }
    else if (numNeighboringMines == 3) {
      textColor = new Color(240, 17, 17);
    }
    else if (numNeighboringMines == 4) {
      textColor = new Color(7, 14, 97);
    }
    else if (numNeighboringMines == 5) {
      textColor = new Color(110, 9, 9);
    }
    else if (numNeighboringMines == 6) {
      textColor = new Color(21, 130, 121);
    }
    else if (numNeighboringMines == 7) {
      textColor = Color.BLACK;
    }
    else {
      textColor = Color.GRAY;
    }

    TextImage numMinesText = new TextImage(countMines, 30, FontStyle.BOLD, textColor);
    RectangleImage cellOutline = new RectangleImage(this.cellWidth, this.cellHeight,
        OutlineMode.OUTLINE, Color.BLACK);
    return new OverlayImage(cellOutline, numMinesText);
  }

  // method to draw the mine
  OverlayImage drawMine() {
    // this mine is composed of different shapes to enhance game graphics
    // ******EXTRA CREDIT*****
    CircleImage bombBody = new CircleImage(this.cellWidth / 3, OutlineMode.SOLID, Color.DARK_GRAY);

    StarImage fireOut = new StarImage((this.cellWidth / 8.0) + 2, 8, 2, OutlineMode.OUTLINE,
        Color.red);

    OverlayImage fire = new OverlayImage(fireOut,
        new StarImage(this.cellWidth / 8.0, 8, 2, OutlineMode.SOLID, Color.ORANGE));

    OverlayOffsetAlign bomb = new OverlayOffsetAlign(AlignModeX.RIGHT, AlignModeY.TOP, fire,
        this.cellWidth / 3.0, this.cellWidth / 6.0, bombBody);

    RectangleImage cellOutline = new RectangleImage(this.cellWidth, this.cellHeight,
        OutlineMode.OUTLINE, Color.BLACK);

    // Combine the bomb image and the outline
    return new OverlayImage(cellOutline, bomb);
  }

  // determine what type of tile to return
  WorldImage drawVisibleCell(boolean isMine, int numNeighboringMines, boolean isFlag) {
    if (isMine && !isFlag) {
      return drawMine();
    }
    else {
      return drawSafe(numNeighboringMines);
    }
  }

  // drawing a hidden cell (not clicked)
  WorldImage drawHiddenCell(boolean isFlag) {
    // these different components of the hidden cell image are purely aesthetic.
    // ******EXTRA CREDIT*****
    RectangleImage cellShine = new RectangleImage(this.cellWidth, this.cellHeight,
        OutlineMode.SOLID, new Color(100, 100, 100));
    RectangleImage cellFace = new RectangleImage(4 * this.cellWidth / 5, 4 * this.cellHeight / 5,
        OutlineMode.SOLID, Color.LIGHT_GRAY);
    RectangleImage cellOutline = new RectangleImage(this.cellWidth, this.cellHeight,
        OutlineMode.OUTLINE, Color.BLACK);
    WorldImage cellImage;
    cellImage = new OverlayImage(cellOutline, cellShine);
    cellImage = new OverlayImage(drawCellShadow(), cellImage);
    cellImage = new OverlayImage(cellFace, cellImage);
    if (isFlag) {
      return new OverlayImage(drawFlag(), cellImage);
    }
    else {
      return cellImage;
    }
  }

  // ******EXTRA CREDIT*****
  // draw the cell's shadow
  WorldImage drawCellShadow() {
    Posn v1 = new Posn(0, 0);
    Posn v2 = new Posn(0, this.cellHeight);
    Posn v3 = new Posn(this.cellWidth, 0);
    return new TriangleImage(v1, v2, v3, OutlineMode.SOLID, new Color(255, 255, 255));
  }

  // flag graphics
  WorldImage drawFlag() {
    // draw the flag pole
    RectangleImage pole = new RectangleImage(this.cellWidth / 10, this.cellHeight / 2,
        OutlineMode.SOLID, Color.BLACK);
    RectangleImage base1 = new RectangleImage(this.cellWidth / 2, this.cellHeight / 10,
        OutlineMode.SOLID, Color.BLACK);
    RectangleImage base2 = new RectangleImage(2 * this.cellWidth / 7, this.cellHeight / 12,
        OutlineMode.SOLID, Color.BLACK);
    WorldImage base = new OverlayOffsetImage(base2, 0, this.cellHeight / 10.0, base1);
    WorldImage flagpole = new OverlayOffsetImage(pole, 0, this.cellHeight / 4.0, base);

    // draw the flag
    Posn v1 = new Posn(0, -this.cellHeight / 3);
    Posn v2 = new Posn(-this.cellWidth / 3, -this.cellHeight / 2);
    Posn v3 = new Posn(0, -2 * this.cellHeight / 3);
    TriangleImage flagShape = new TriangleImage(v1, v2, v3, OutlineMode.SOLID, Color.RED);

    // overlay the flag onto the flagpole
    return new OverlayOffsetImage(flagShape, this.cellWidth / 9.0, this.cellHeight / 5.0, flagpole);
  }

  // method for a left click
  void updateCell(int colIndex, int rowIndex) {
    ArrayList<Cell> cellRow = this.cellGrid.get(rowIndex);
    Cell cell = cellRow.get(colIndex);
    // you can only click on a hidden cell if it is not flagged
    if (!cell.flag) {
      cell.makeVisible();
    }
  }

  // method for a right click
  void updateCellFlag(int colIndex, int rowIndex) {
    ArrayList<Cell> cellRow = this.cellGrid.get(rowIndex);
    Cell cell = cellRow.get(colIndex);
    // you can only place as many flags as there are mines
    if ((numFlags() != this.numMines) || ((numFlags() == this.numMines) && cell.flag)) {
      cell.flag = !cell.flag;
    }
  }

  // get the number of flags placed on the board
  int numFlags() {
    int flags = 0;
    for (ArrayList<Cell> cellRow : this.cellGrid) {
      for (Cell cell : cellRow) {
        if (cell.flag) {
          flags += 1;
        }
      }
    }
    return flags;
  }

  // the number of neighboring mines to a cell
  int numNeighboringMines(Cell cell) {
    int numMines = 0;
    for (Cell neighbor : cell.neighbors) {
      if (neighbor.mine) {
        numMines += 1;
      }
    }
    return numMines;
  }

  // redefine the inherited onTick method
  public void onTick() {
    if (!this.gameEnd) {
      this.timer += 1;
    }
  }

  // method to update game based off mouse click
  public void onMouseClicked(Posn posn, String key) {
    int colIndex = (posn.x - (posn.x % 50)) / 50;
    int rowIndex = (posn.y - (posn.y % 50)) / 50;

    if (!this.gameEnd) {
      // if user clicks to uncover a cell's contents
      if ("LeftButton".equals(key)) {
        updateCell(colIndex, rowIndex);
        Cell clickedCell = this.cellGrid.get(rowIndex).get(colIndex);
        // end the game if the user clicks a mine or uncovers all cells that aren't
        // mines
        if ((clickedCell.mine && !(clickedCell.flag)) || foundAllMines()) {
          this.gameEnd = true;
        }
      }
      // if user clicks to mark a cell with a flag
      else if ("RightButton".equals(key)) {
        updateCellFlag(colIndex, rowIndex);
      }
    }
  }

  // redefine the inherited onKeyEvent
  // method to handle mine count
  public void onKeyEvent(String key) {
    if (this.gameEnd) {
      // if user presses "b", make the difficulty "beginner"
      if (key.equals("b")) {
        Board board = new Board(8, 8, 10);
        board.playMinesweeper();
      }
      // if user presses "i", make the difficulty "intermediate"
      else if (key.equals("i")) {
        Board board = new Board(16, 16, 40);
        board.playMinesweeper();
      }
      // if user presses "e", make the difficulty "expert"
      else if (key.equals("e")) {
        Board board = new Board(30, 16, 99);
        board.playMinesweeper();
      }
    }
  }

  // call bigbang to render the javalib code
  public void playMinesweeper() {
    this.bigBang(this.cellWidth * this.gridWidth, this.cellHeight * (this.gridHeight + 1), 1);
  }
}

// example and test for the board
class ExamplesBoard {
  Board board;
  Cell safe1;
  Cell safe2;
  Cell safe3;
  Cell safe4;
  Cell safe5;
  Cell safe6;
  Cell safe7;
  Cell mine1;
  Cell mine2;
  ArrayList<ArrayList<Cell>> cells;
  ArrayList<Cell> cellRow1;
  ArrayList<Cell> cellRow2;
  ArrayList<Cell> cellRow3;

  // initialize variables
  void init() {
    board = new Board(3, 3, 2, 9);

    /*
     * 
     * when random seed = 9, the above board looks like the following image:
     * 
     * X O O O O O O O X
     * 
     * (X = mine, O = safe cell)
     * 
     */

    mine1 = new Cell(true, true);
    safe1 = new Cell(true, false);
    safe2 = new Cell(true, false);

    safe3 = new Cell(true, false);
    safe4 = new Cell(true, false);
    safe5 = new Cell(true, false);

    safe6 = new Cell(true, false);
    safe7 = new Cell(true, false);
    mine2 = new Cell(true, true);

    safe1.updateNeighbors(mine1);
    safe2.updateNeighbors(safe1);

    safe4.updateNeighbors(safe3);
    safe5.updateNeighbors(safe4);

    safe3.updateNeighbors(mine1);
    safe3.updateNeighbors(safe1);
    safe4.updateNeighbors(safe1);
    safe4.updateNeighbors(mine1);
    safe4.updateNeighbors(safe2);
    safe5.updateNeighbors(safe2);
    safe5.updateNeighbors(safe1);

    safe7.updateNeighbors(safe6);
    mine2.updateNeighbors(safe7);

    safe6.updateNeighbors(safe3);
    safe6.updateNeighbors(safe4);
    safe7.updateNeighbors(safe4);
    safe7.updateNeighbors(safe3);
    safe7.updateNeighbors(safe5);
    mine2.updateNeighbors(safe5);
    mine2.updateNeighbors(safe4);

    cellRow1 = new ArrayList<>();
    cellRow1.add(mine1);
    cellRow1.add(safe1);
    cellRow1.add(safe2);

    cellRow2 = new ArrayList<>();
    cellRow2.add(safe3);
    cellRow2.add(safe4);
    cellRow2.add(safe5);

    cellRow3 = new ArrayList<>();
    cellRow3.add(safe6);
    cellRow3.add(safe7);
    cellRow3.add(mine2);

    cells = new ArrayList<>();
    cells.add(cellRow1);
    cells.add(cellRow2);
    cells.add(cellRow3);
  }

  // test the checkSize method
  void testCheckSize(Tester t) {
    init();

    t.checkException("construct a board with invalid grid width",
        new IllegalArgumentException("grid dimensions are too small"), this.board, "checkSize", 1,
        0);
    // test explanation given in above check exception

    t.checkException("construct a board with invalid grid height",
        new IllegalArgumentException("grid dimensions are too small"), this.board, "checkSize", 0,
        1);
    // test explanation given in above check exception

    t.checkException("construct a board with invalid grid width/height",
        new IllegalArgumentException("grid dimensions are too small"), this.board, "checkSize", 0,
        0);
    // test explanation given in above check exception
  }

  // test the countHiddenCells method
  void testCountHiddenCells(Tester t) {
    init();

    t.checkExpect(this.board.countHiddenCells(), 9);
    // (before mutation) all cells are hidden

    this.board.onMouseClicked(new Posn(0, 0), "LeftButton");
    t.checkExpect(this.board.countHiddenCells(), 8);
    // (after mutation)
    // after the user clicks a cell, 1 cell isn't hidden
  }

  // test the onMouseClicked method
  void testOnMouseClicked(Tester t) {
    init();

    t.checkExpect(board.foundAllMines(), false);
    // (before mutation) you clearly haven't found all mines if you just initialized
    // the game board

    this.board.onMouseClicked(new Posn(0, 50), "LeftButton");
    this.board.onMouseClicked(new Posn(0, 100), "LeftButton");
    this.board.onMouseClicked(new Posn(50, 0), "LeftButton");
    this.board.onMouseClicked(new Posn(50, 50), "LeftButton");
    this.board.onMouseClicked(new Posn(50, 100), "LeftButton");
    this.board.onMouseClicked(new Posn(100, 50), "LeftButton");
    this.board.onMouseClicked(new Posn(100, 100), "LeftButton");
    t.checkExpect(this.board.foundAllMines(), true);
    // (after mutation) when you click all the
    // safe cells, you have found all the mines
  }

  // test the num flags method
  void testNumFlags(Tester t) {
    init();

    t.checkExpect(this.board.numFlags(), 0);
    // testing before mutation

    this.board.onMouseClicked(new Posn(0, 50), "RightButton");
    t.checkExpect(this.board.numFlags(), 1);
    // testing after mutation (ie, after the user places a flag)
  }

  // test the updateCellFlag method
  void testUpdateCellFlag(Tester t) {
    init();

    t.checkExpect(this.board.numFlags(), 0);
    // testing before mutation

    this.board.updateCellFlag(1, 1);
    t.checkExpect(this.board.numFlags(), 1);
    // testing after mutation

    this.board.updateCellFlag(1, 1);
    t.checkExpect(this.board.numFlags(), 0);
    // testing after reversing the mutation (ie, un-flagging a cell)
  }

  // test the createBoard method
  void testCreateBoard(Tester t) {
    init();

    t.checkExpect(this.board.cellGrid, this.cells);
    // test that the createBoard method creates an array
    // list representation of the expected game board
  }

  // testing the method that takes a cell as an
  // argument and returns the number of neighboring mines
  void testNumNeighboringMines(Tester t) {
    init();

    t.checkExpect(this.board.numNeighboringMines(this.mine1), 0);
    // testing on a mine

    t.checkExpect(this.board.numNeighboringMines(this.safe1), 1);
    // testing on a safe cell with one neighboring mine

    t.checkExpect(this.board.numNeighboringMines(this.safe2), 0);
    // testing on a safe cell with no neighboring mines

    t.checkExpect(this.board.numNeighboringMines(this.safe4), 2);
    // testing on a safe cell with >1 neighboring mines
  }

  // test the makeScene method constructs the expected world scene during bigBang
  void testPlayGame(Tester t) {
    int gridWidth = 4;
    int gridHeight = 4;
    int numMines = 2;
    // Note: unused t method argument exists to conform to tester.Main API
    new Board(gridWidth, gridHeight, numMines).playMinesweeper();
    // display the game board to ensure that it looks correct
  }
}
