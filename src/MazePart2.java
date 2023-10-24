import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;

import javalib.impworld.World;
import javalib.impworld.WorldScene;
import javalib.worldimages.RectangleImage;
import tester.Tester;

//represents a Stack
class Stack<T> {
  ArrayList<T> list;

  Stack(ArrayList<T> list) {
    this.list = list;
  }

  // adds to the top of stack
  public void add(T t) {
    list.add(0, t);
  }

  // removes from the top of the stack
  public T pop() {
    T result = list.get(0);
    list.remove(0);
    return result;
  }
}

//represents a Queue
class Queue<T> {
  ArrayList<T> list;

  Queue(ArrayList<T> list) {
    this.list = list;
  }

  // adds to the bottom of the queue
  public void add(T t) {
    list.add(list.size(), t);
  }

  // removes from the top of the queue
  public T pop() {
    T result = list.get(0);
    list.remove(0);
    return result;
  }
}

interface ICell {
  public void getNeighborsHelper(ArrayList<Cell> neighbors);

}

class Cell implements ICell {
  ICell left;
  ICell top;
  ICell right;
  ICell bottom;
  ArrayList<Cell> neighbors;
  boolean visited;
  boolean solution;

  int x;
  int y;

  Cell(int x, int y) {
    this.x = x;
    this.y = y;
    this.neighbors = new ArrayList<Cell>();
    this.visited = false;
    this.solution = false;
  }

  // overrides hashCode to provide unique hashcodes
  // to use for equalities
  public int hashCode() {
    return this.x * 10 - this.y;
  }

  // overrides the equals method for generic purposes
  // Eases the rules of using instanceof
  public boolean equals(Object other) {
    if (other instanceof Cell) {
      return this.x == ((Cell) other).x && this.y == ((Cell) other).y;
    }
    else {
      return false;
    }
  }

  // returns a list of all a cell's neighbors
  public ArrayList<Cell> getNeighbors() {
    this.neighbors = new ArrayList<>();

    this.left.getNeighborsHelper(this.neighbors);
    this.top.getNeighborsHelper(this.neighbors);
    this.right.getNeighborsHelper(this.neighbors);
    this.bottom.getNeighborsHelper(this.neighbors);

    return this.neighbors;
  }

  // helper that will add a cell to a list of neighbors if it is not already in
  // the list
  public void getNeighborsHelper(ArrayList<Cell> neighbors) {
    if (!neighbors.contains(this)) {
      neighbors.add(this);
    }
  }

  void drawCellWithOutline(WorldScene scene, int cellSize, Color fillColor, Color outlineColor) {
    scene.placeImageXY(new RectangleImage(cellSize, cellSize, "solid", fillColor),
        (this.x * cellSize) + (cellSize / 2), (this.y * cellSize) + (cellSize / 2));
    scene.placeImageXY(new RectangleImage(cellSize, cellSize, "outline", outlineColor),
        (this.x * cellSize) + (cellSize / 2), (this.y * cellSize) + (cellSize / 2));
  }

  // draws a cell on the map with a full black outline on each side
  // Effect: no effect on the cell, but an effect on the world scene
  void drawCell(WorldScene scene, int cellSize) {
    if (!visited && !solution) {
      drawCellWithOutline(scene, cellSize, Color.gray, Color.black);
    }
    else if (visited && !solution) {
      drawCellWithOutline(scene, cellSize, new Color(3, 157, 252), Color.black);
    }
    else if (solution) {
      drawCellWithOutline(scene, cellSize, new Color(55, 81, 212), Color.black);
    }
  }

}

class EmptyCell implements ICell {
  EmptyCell() {
  }

  public void getNeighborsHelper(ArrayList<Cell> neighbors) {
    // do nothing if there is the case of an empty cell
  }

}

interface IEdge {

}

class Edge implements IEdge {
  Cell from;
  Cell to;
  int weight;

  Edge(Cell from, Cell to, int weight) {
    this.from = from;
    this.to = to;
    this.weight = weight;
  }

  // draws an empty space to remove a wall from the given WorldSize
  void drawEdge(WorldScene scene, int cellSize) {
    if (!from.visited && !from.solution) {
      if (this.from.x == this.to.x - 1) {
        scene.placeImageXY(new RectangleImage(2, cellSize - 1, "solid", Color.gray),
            (this.from.x * cellSize) + cellSize, (this.from.y * cellSize) + (cellSize / 2) + 1);

      }
      else if (this.from.y == this.to.y + 1) {
        scene.placeImageXY(new RectangleImage(cellSize - 1, 2, "solid", Color.gray),
            (this.from.x * cellSize) + (cellSize / 2) + 1, (this.from.y * cellSize));

      }
      else if (this.from.x == this.to.x + 1) {
        scene.placeImageXY(new RectangleImage(2, cellSize - 1, "solid", Color.gray),
            (this.from.x * cellSize), (this.from.y * cellSize) + (cellSize / 2) + 1);
      }
      else if (this.from.y == this.to.y - 1) {
        scene.placeImageXY(new RectangleImage(cellSize - 1, 2, "solid", Color.gray),
            (this.from.x * cellSize) + (cellSize / 2) + 1, (this.from.y * cellSize) + (cellSize));
      }
    }
    else if (from.visited && !from.solution) {
      if (this.from.x == this.to.x - 1) {
        scene.placeImageXY(new RectangleImage(2, cellSize - 1, "solid", new Color(3, 157, 252)),
            (this.from.x * cellSize) + cellSize, (this.from.y * cellSize) + (cellSize / 2) + 1);

      }
      else if (this.from.y == this.to.y + 1) {
        scene.placeImageXY(new RectangleImage(cellSize - 1, 2, "solid", new Color(3, 157, 252)),
            (this.from.x * cellSize) + (cellSize / 2) + 1, (this.from.y * cellSize));

      }
      else if (this.from.x == this.to.x + 1) {
        scene.placeImageXY(new RectangleImage(2, cellSize - 1, "solid", new Color(3, 157, 252)),
            (this.from.x * cellSize), (this.from.y * cellSize) + (cellSize / 2) + 1);
      }
      else if (this.from.y == this.to.y - 1) {
        scene.placeImageXY(new RectangleImage(cellSize - 1, 2, "solid", new Color(3, 157, 252)),
            (this.from.x * cellSize) + (cellSize / 2) + 1, (this.from.y * cellSize) + (cellSize));
      }
    }
    else if (from.solution) {
      if (this.from.x == this.to.x - 1) {
        scene.placeImageXY(new RectangleImage(2, cellSize - 1, "solid", new Color(55, 81, 212)),
            (this.from.x * cellSize) + cellSize, (this.from.y * cellSize) + (cellSize / 2) + 1);

      }
      else if (this.from.y == this.to.y + 1) {
        scene.placeImageXY(new RectangleImage(cellSize - 1, 2, "solid", new Color(55, 81, 212)),
            (this.from.x * cellSize) + (cellSize / 2) + 1, (this.from.y * cellSize));

      }
      else if (this.from.x == this.to.x + 1) {
        scene.placeImageXY(new RectangleImage(2, cellSize - 1, "solid", new Color(55, 81, 212)),
            (this.from.x * cellSize), (this.from.y * cellSize) + (cellSize / 2) + 1);
      }
      else if (this.from.y == this.to.y - 1) {
        scene.placeImageXY(new RectangleImage(cellSize - 1, 2, "solid", new Color(55, 81, 212)),
            (this.from.x * cellSize) + (cellSize / 2) + 1, (this.from.y * cellSize) + (cellSize));
      }
    }
  }
}

class EmptyEdge implements IEdge {
  EmptyEdge() {
  }
}

// If edge1 is greater than edge2, return positive. Vice versa if edge2 is
// larger than edge1. Return 0 if equal.
class WeightComparator implements Comparator<Edge> {

  WeightComparator() {
  }

  public int compare(Edge edge1, Edge edge2) {
    return edge1.weight - edge2.weight;
  }

}

class MazeWorld extends World {
  // The empty canvas to work off
  WorldScene scene = new WorldScene(600, 600);
  ArrayList<Cell> graph;
  int height;
  int width;
  int cellSize;
  Random rand;
  ArrayList<Edge> loEdge;
  boolean breadthSearch;
  boolean depthSearch;
  ArrayList<Cell> visited;
  ArrayList<Cell> solutionPath;
  boolean solved;
  boolean solvedStage;

  // regular constructor
  MazeWorld(int height, int width) {
    this.height = height;
    this.width = width;
    this.cellSize = 600 / height;
    this.rand = new Random();
    this.loEdge = new ArrayList<Edge>();
    this.makeGraph();
    // this.makeScene();
    this.breadthSearch = false;
    this.depthSearch = false;
    this.visited = new ArrayList<Cell>();
    this.solutionPath = new ArrayList<Cell>();
    this.solved = false;
    this.solvedStage = false;

  }

  // convenience constructor
  MazeWorld(int seed) {
    this.height = 2;
    this.width = 2;
    this.cellSize = 600 / height;
    this.rand = new Random(seed);
    this.loEdge = new ArrayList<Edge>();
    this.makeGraph();
    // this.makeScene();
    this.breadthSearch = false;
    this.depthSearch = false;
    this.visited = new ArrayList<Cell>();
    this.solutionPath = new ArrayList<Cell>();
    this.solved = false;
    this.solvedStage = false;
  }

  // uses two for loops to draw every cell and every edge
  public WorldScene makeScene() {

    for (Cell cell : graph) {
      cell.drawCell(scene, cellSize);
    }
    this.graph.get(0).drawCellWithOutline(scene, cellSize, new Color(99, 194, 108), Color.black);
    this.graph.get(this.height * this.width - 1).drawCellWithOutline(scene, cellSize,
        new Color(133, 48, 140), Color.black);
    for (Edge edge : loEdge) {
      edge.drawEdge(scene, cellSize);
    }


    return scene;
  }

  // Makes all the cells of the graph
  void makeGraph() {
    this.graph = new ArrayList<Cell>();

    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        this.graph.add(new Cell(j, i));
      }
    }
    this.makeConnections();
  }

  // Initializes each cell's neighbors
  void makeConnections() {
    for (int i = 0; i < graph.size(); i++) {
      Cell cell = this.graph.get(i);
      if (cell.x > 0) {
        cell.left = this.graph.get(i - 1);
      }
      else {
        cell.left = new EmptyCell();
      }
      if (cell.y > 0) {
        cell.top = this.graph.get(i - width);
      }
      else {
        cell.top = new EmptyCell();
      }
      if (cell.x < width - 1) {
        cell.right = this.graph.get(i + 1);
      }
      else {
        cell.right = new EmptyCell();
      }
      if (cell.y < height - 1) {
        cell.bottom = this.graph.get(i + width);
      }
      else {
        cell.bottom = new EmptyCell();
      }
    }
    this.makeEdges();
  }

  // Makes the edges between each cell's neighbors with random weights
  void makeEdges() {
    for (int i = 0; i < graph.size(); i++) {
      Cell cell = this.graph.get(i);
      if (cell.x < width - 1) {
        this.loEdge.add(new Edge(this.graph.get(i), this.graph.get(i + 1), rand.nextInt(100)));
      }
      if (cell.y < height - 1) {
        this.loEdge.add(new Edge(this.graph.get(i), this.graph.get(i + width), rand.nextInt(100)));
      }
    }

    this.kruskal();
  }

  // Sets up a unified maze that has a viable path from the top left to the bottom
  // right
  void kruskal() {
    // Sorts the list of edges by their weights
    Collections.sort(this.loEdge, new WeightComparator());

    HashMap<Cell, Cell> representatives = new HashMap<>();
    ArrayList<Edge> solution = new ArrayList<>();

    for (Cell cell : this.graph) {
      representatives.put(cell, cell);
    }

    while (solution.size() < this.graph.size() - 1) {
      if (!this.cycles(representatives, this.loEdge.get(0).from, this.loEdge.get(0).to)) {
        solution.add(this.loEdge.get(0));
      }
      this.loEdge.remove(0);
    }
    this.loEdge = solution;

  }

  // finds out if a given two items within the hashmap cause a cycle
  // Generic for testing purposes
  <T> boolean cycles(HashMap<T, T> representatives, T from, T to) {
    T fromRoot = this.root(representatives, from);
    T toRoot = this.root(representatives, to);
    // if they exist in separate circles
    if (!fromRoot.equals(toRoot)) {
      // replace with their root connectors
      representatives.replace(toRoot, fromRoot);
    }
    // returns the state of the cycle
    return fromRoot.equals(toRoot);

  }

  // goes through the representatives hashMap to find matches
  // returns the root connection
  <T> T root(HashMap<T, T> representatives, T vertex) {
    T root = vertex;
    T parent = representatives.get(root);
    while (parent != root) {
      root = parent;
      parent = representatives.get(root);
    }
    return root;
  }

  public void onKeyEvent(String key) {
    if (key.equals("d")) {
      // if (this.player.current.equals(graph.get(0))) {
      // Start the DFS algorithm from the player's current cell
      this.solveDFS();
      // this.drawSolutionPath();
      // }
      this.depthSearch = !this.depthSearch;
    }
    if (key.equals("b")) {
      // if (this.player.current.equals(graph.get(0))) {
      // Start the BFS algorithm from the player's current cell
      this.solveBFS();
      // }
      this.breadthSearch = !this.breadthSearch;
    }
  }

  public boolean containsEdge(Edge edge) {
    for (Edge e : this.loEdge) {
      if ((e.from.equals(edge.from) && e.to.equals(edge.to))
          || (e.to.equals(edge.from) && e.from.equals(edge.to))) {
        return true;
      }
    }
    return false;
  }

  public void solveDFS() {
    HashMap<ICell, ICell> cameFrom = new HashMap<>();
    Stack<Cell> worklist = new Stack<Cell>(new ArrayList<Cell>());
    Cell start = this.graph.get(0);
    Cell goal = this.graph.get(graph.size() - 1);
    worklist.add(start);
    cameFrom.put(start, new EmptyCell());

    while (worklist.list.size() > 0) {
      Cell next = worklist.pop();

      if (next.equals(goal)) {
        ArrayList<Cell> path = new ArrayList<>();
        Cell cell = next;
        while (!cell.equals(this.graph.get(0))) {
          path.add(0, cell);
          cell = (Cell) cameFrom.get(cell);
        }
        path.add(0, cell);
        this.solutionPath = path;
        System.err.println("Maze Solved!");
        solved = true;
        return;
      }
      else if (!visited.contains(next)) {
        visited.add(next);
        for (Cell n : next.getNeighbors()) {
          if (!visited.contains(n) && this.containsEdge(new Edge(next, n, 1))) {
            worklist.add(n);
            cameFrom.put(n, next);
          }
        }
      }
    }

  }

  public void solveBFS() {
    HashMap<ICell, ICell> cameFrom = new HashMap<>();
    Queue<Cell> worklist = new Queue<Cell>(new ArrayList<Cell>());
    Cell start = this.graph.get(0);
    Cell goal = this.graph.get(graph.size() - 1);
    worklist.add(start);
    cameFrom.put(start, new EmptyCell());

    while (worklist.list.size() > 0) {
      Cell next = worklist.pop();

      if (next.equals(goal)) {
        ArrayList<Cell> path = new ArrayList<>();
        Cell cell = next;
        while (!cell.equals(this.graph.get(0))) {
          path.add(0, cell);
          cell = (Cell) cameFrom.get(cell);
        }
        path.add(0, cell);
        this.solutionPath = path;
        System.err.println("Maze Solved!");
        solved = true;
        return;
      }
      else if (!visited.contains(next)) {
        visited.add(next);
        for (Cell n : next.getNeighbors()) {
          if (!visited.contains(n) && this.containsEdge(new Edge(next, n, 1))) {
            worklist.add(n);
            cameFrom.put(n, next);
          }
        }
      }
    }

  }

  public WorldScene drawPlay() {
    this.visited.get(0).drawCellWithOutline(scene, cellSize, new Color(3, 157, 252),
        new Color(3, 157, 252));
    return this.scene;
  }

  public WorldScene drawFinished() {
    this.solutionPath.get(0).drawCellWithOutline(scene, cellSize, new Color(55, 81, 212),
        new Color(55, 81, 212));
    return this.scene;
  }

  public void onTick() {
    if (solved) {
      if (!this.solvedStage) {
        this.visited.get(0).visited = true;
        this.visited.remove(0);
        if (visited.size() == 0) {
          this.solvedStage = true;
        }
      }
      else {
        this.solutionPath.get(0).solution = true;
        this.solutionPath.remove(0);
        if (solutionPath.size() == 0) {
          this.solvedStage = false;
          this.solved = false;
        }
      }
    }
  }

}

class ExamplesMaze {

  int cellSize = 30;

  Queue<String> testQueue1;
  Queue<Integer> testQueue2;

  ICell empty;
  Cell cell1;
  Cell cell2;
  Cell cell3;
  Cell cell4;

  Edge edge1;
  Edge edge2;
  Edge edge3;

  WeightComparator comparator;

  WorldScene testScene;

  MazeWorld testMaze;

  void initData() {

    ArrayList<String> list1 = new ArrayList<String>(Arrays.asList("a", "b", "c"));
    ArrayList<Integer> list2 = new ArrayList<Integer>(Arrays.asList(1, 2, 3));
    this.testQueue1 = new Queue<String>(list1);
    this.testQueue2 = new Queue<Integer>(list2);

    this.empty = new EmptyCell();
    this.cell1 = new Cell(0, 0);
    this.cell2 = new Cell(1, 0);
    this.cell3 = new Cell(0, 1);
    this.cell4 = new Cell(10, 13);

    this.edge1 = new Edge(cell1, cell2, 10);
    this.edge2 = new Edge(cell2, cell3, 15);
    this.edge3 = new Edge(cell3, cell1, 20);

    this.comparator = new WeightComparator();

    this.testScene = new WorldScene(2 * cellSize, 2 * cellSize);

    this.testMaze = new MazeWorld(1234);
  }

  /////////////////////////// STACK METHODS //////////////////////////////////

  // tests the add method in Stack class
  void testAddStack(Tester t) {
    // create a stack of integers
    ArrayList<Integer> intList = new ArrayList<Integer>();
    intList.add(2);
    intList.add(1);
    Stack<Integer> intStack = new Stack<Integer>(intList);

    // add an integer to the top of the stack
    intStack.add(3);
    t.checkExpect(intStack.list, Arrays.asList(3, 2, 1));

    // create a stack of strings
    ArrayList<String> strList = new ArrayList<String>();
    strList.add("hello");
    strList.add("world");
    Stack<String> strStack = new Stack<String>(strList);

    // add a string to the top of the stack
    strStack.add("foo");
    t.checkExpect(strStack.list, Arrays.asList("foo", "hello", "world"));
  }

  // tests the pop method in Stack class
  void testPopStack(Tester t) {
    Stack<String> stack = new Stack<String>(new ArrayList<String>(Arrays.asList("a", "b", "c")));

    t.checkExpect(stack.pop(), "a");
    t.checkExpect(stack.pop(), "b");
    t.checkExpect(stack.pop(), "c");
  }

  /////////////////////////// QUEUE METHODS //////////////////////////////////

  // test add method on a Queue of Strings
  void testAddQueue(Tester t) {
    initData();
    testQueue1.add("d");
    testQueue2.add(4);

    t.checkExpect(testQueue1.list, new ArrayList<String>(Arrays.asList("a", "b", "c", "d")));
    t.checkExpect(testQueue2.list, new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4)));

  }

  void testPopQueue(Tester t) {
    initData();

    Integer first = testQueue2.pop();
    t.checkExpect(first, 1);
    t.checkExpect(testQueue2.list, new ArrayList<Integer>(Arrays.asList(2, 3)));
  }

  /////////////////////////// CELL METHODS ////////////////////////////////////

  // tests the hashcode method in ICell
  void testHashCode(Tester t) {
    initData();

    t.checkExpect(this.cell1.hashCode(), 0);
    t.checkExpect(this.cell2.hashCode(), 10);
    t.checkExpect(this.cell3.hashCode(), -1);
    t.checkExpect(this.cell4.hashCode(), 87);
  }

  // tests the equals method in ICell
  void testEquals(Tester t) {
    initData();

    t.checkExpect(cell1.equals(new Cell(0, 0)), true);
    t.checkExpect(cell1.equals(cell2), false);
    t.checkExpect(new Cell(3, 5).equals(new Cell(3, 5)), true);
    t.checkExpect(cell4.equals(cell4), true);
    t.checkExpect(cell4.equals(cell3), false);

  }

  // tests the getNeighbors method in the cell class
  void testGetNeighbors(Tester t) {
    initData();

    // Test getNeighbors with all neighbors
    cell1.left = empty;
    cell1.top = empty;
    cell1.right = cell2;
    cell1.bottom = cell3;

    cell2.left = cell1;
    cell2.top = empty;
    cell2.right = empty;
    cell2.bottom = cell3;

    cell3.left = empty;
    cell3.top = cell1;
    cell3.right = empty;
    cell3.bottom = cell4;

    cell4.left = empty;
    cell4.top = cell3;
    cell4.right = empty;
    cell4.bottom = empty;

    ArrayList<Cell> allNeighbors = cell1.getNeighbors();
    t.checkExpect(allNeighbors.size(), 2);
    t.checkExpect(allNeighbors.contains(cell1), false);
    t.checkExpect(allNeighbors.contains(cell2), true);
    t.checkExpect(allNeighbors.contains(cell3), true);
    t.checkExpect(allNeighbors.contains(cell4), false);
  }

  // tests the getNeighborsHelper method in the cell class
  void testGetNeighborsHelper(Tester t) {
    initData();

    // add cell2 to cell1's neighbors list
    ArrayList<Cell> neighbors = new ArrayList<Cell>();
    cell1.getNeighborsHelper(neighbors);
    cell2.getNeighborsHelper(neighbors);

    t.checkExpect(neighbors, new ArrayList<Cell>(Arrays.asList(cell1, cell2)));

    // add cell3 to cell2's neighbors list
    neighbors = new ArrayList<Cell>();
    cell2.getNeighborsHelper(neighbors);
    cell3.getNeighborsHelper(neighbors);

    t.checkExpect(neighbors, new ArrayList<Cell>(Arrays.asList(cell2, cell3)));

    // add cell2 to cell3's neighbors list
    neighbors = new ArrayList<Cell>();
    cell3.getNeighborsHelper(neighbors);
    cell2.getNeighborsHelper(neighbors);

    t.checkExpect(neighbors, new ArrayList<Cell>(Arrays.asList(cell3, cell2)));

    // make sure duplicates aren't added
    neighbors = new ArrayList<Cell>();
    cell1.getNeighborsHelper(neighbors);
    cell2.getNeighborsHelper(neighbors);
    cell2.getNeighborsHelper(neighbors);

    t.checkExpect(neighbors, new ArrayList<Cell>(Arrays.asList(cell1, cell2)));

  }

  // tests the drawCell method in ICell
  void testDrawCell(Tester t) {
    this.initData();

    int cellSize = 30;

    WorldScene expected1 = new WorldScene(2 * cellSize, 2 * cellSize);
    expected1.placeImageXY(new RectangleImage(cellSize, cellSize, "solid", Color.gray),
        cellSize / 2, cellSize / 2);
    expected1.placeImageXY(new RectangleImage(cellSize, cellSize, "outline", Color.black),
        cellSize / 2, cellSize / 2);

    WorldScene expected2 = new WorldScene(2 * cellSize, 2 * cellSize);
    expected2.placeImageXY(new RectangleImage(cellSize, cellSize, "solid", Color.gray),
        (cell2.x * cellSize) + (cellSize / 2), (cell2.y * cellSize) + (cellSize / 2));
    expected2.placeImageXY(new RectangleImage(cellSize, cellSize, "outline", Color.black),
        (cell2.x * cellSize) + (cellSize / 2), (cell2.y * cellSize) + (cellSize / 2));

    WorldScene expected3 = new WorldScene(2 * cellSize, 2 * cellSize);
    expected3.placeImageXY(new RectangleImage(cellSize, cellSize, "solid", Color.gray),
        (cell3.x * cellSize) + (cellSize / 2), (cell3.y * cellSize) + (cellSize / 2));
    expected3.placeImageXY(new RectangleImage(cellSize, cellSize, "outline", Color.black),
        (cell3.x * cellSize) + (cellSize / 2), (cell3.y * cellSize) + (cellSize / 2));

    WorldScene actual1 = new WorldScene(2 * cellSize, 2 * cellSize);
    this.cell1.drawCell(actual1, cellSize);

    WorldScene actual2 = new WorldScene(2 * cellSize, 2 * cellSize);
    this.cell2.drawCell(actual2, cellSize);

    WorldScene actual3 = new WorldScene(2 * cellSize, 2 * cellSize);
    this.cell3.drawCell(actual3, cellSize);

    // test for cell1
    t.checkExpect(actual1, expected1);
    // test for cell2
    t.checkExpect(actual2, expected2);
    // test for cell3
    t.checkExpect(actual3, expected3);

  }

  ///////////////////////////// IEDGE METHODS //////////////////////////////////

  // tests the drawEdge method in IEdge
  void testDrawEdge(Tester t) {
    initData();

    int cellSize = 30;

    WorldScene expected1 = new WorldScene(2 * cellSize, 2 * cellSize);
    expected1.placeImageXY(new RectangleImage(2, cellSize - 1, "solid", Color.gray),
        (this.edge1.from.x * cellSize) + cellSize,
        (this.edge1.from.y * cellSize) + (cellSize / 2) + 1);

    WorldScene expected2 = new WorldScene(2 * cellSize, 2 * cellSize);
    expected2.placeImageXY(new RectangleImage(2, 29, "solid", Color.gray),
        (this.edge2.from.x * cellSize), (this.edge2.from.y * cellSize) + (cellSize / 2) + 1);

    WorldScene expected3 = new WorldScene(2 * cellSize, 2 * cellSize);
    expected3.placeImageXY(new RectangleImage(29, 2, "solid", Color.gray),
        (this.edge3.from.x * cellSize) + (cellSize / 2) + 1, (this.edge3.from.y * cellSize));

    WorldScene actualEdge1 = new WorldScene(2 * cellSize, 2 * cellSize);
    edge1.drawEdge(actualEdge1, cellSize);

    WorldScene actualEdge2 = new WorldScene(2 * cellSize, 2 * cellSize);
    edge2.drawEdge(actualEdge2, cellSize);

    WorldScene actualEdge3 = new WorldScene(2 * cellSize, 2 * cellSize);
    edge3.drawEdge(actualEdge3, cellSize);

    t.checkExpect(actualEdge1, expected1);
    t.checkExpect(actualEdge2, expected2);
    t.checkExpect(actualEdge3, expected3);
  }

  /////////////////////// WEIGHT COMPARATOR METHODS ////////////////////////////

  // tests the compare method in WeightComparator
  void testCompare(Tester t) {
    initData();

    t.checkExpect(comparator.compare(edge1, edge2), -5);
    t.checkExpect(comparator.compare(edge2, edge1), 5);
    t.checkExpect(comparator.compare(edge1, edge1), 0);
    t.checkExpect(comparator.compare(edge2, edge3), -5);
    t.checkExpect(comparator.compare(edge3, edge2), 5);

  }

  //////////////////////////// MAZEWORLD METHODS ///////////////////////////////

  // tests the makeScene method in MazeWorld
  void testMakeScene(Tester t) {
    initData();

    MazeWorld maze1 = new MazeWorld(999);
    WorldScene expected1 = new WorldScene(600, 600);
    maze1.graph.get(0).drawCell(expected1, 300);
    maze1.graph.get(1).drawCell(expected1, 300);
    maze1.graph.get(2).drawCell(expected1, 300);
    maze1.graph.get(3).drawCell(expected1, 300);
    maze1.graph.get(0).drawCellWithOutline(expected1, 300, new Color(99, 194, 108), Color.black);
    maze1.graph.get(3).drawCellWithOutline(expected1, 300, new Color(133, 48, 140), Color.black);
    maze1.loEdge.get(0).drawEdge(expected1, 300);
    maze1.loEdge.get(1).drawEdge(expected1, 300);
    maze1.loEdge.get(2).drawEdge(expected1, 300);

    initData();

    MazeWorld maze2 = new MazeWorld(1234);
    WorldScene expected2 = new WorldScene(600, 600);
    maze2.graph.get(0).drawCell(expected2, 300);
    maze2.graph.get(1).drawCell(expected2, 300);
    maze2.graph.get(2).drawCell(expected2, 300);
    maze2.graph.get(3).drawCell(expected2, 300);
    maze2.graph.get(0).drawCellWithOutline(expected2, 300, new Color(99, 194, 108), Color.black);
    maze2.graph.get(3).drawCellWithOutline(expected2, 300, new Color(133, 48, 140), Color.black);
    maze2.loEdge.get(0).drawEdge(expected2, 300);
    maze2.loEdge.get(1).drawEdge(expected2, 300);
    maze2.loEdge.get(2).drawEdge(expected2, 300);

    WorldScene actual1 = maze1.makeScene();
    WorldScene actual2 = maze2.makeScene();

    t.checkExpect(actual1, expected1);
    t.checkExpect(actual2, expected2);
  }

  // tests the makeGraph method in MazeWorld
  void testMakeGraph(Tester t) {
    initData();

    testMaze.makeGraph();

    t.checkExpect(testMaze.graph.size(), testMaze.width * testMaze.height);
    t.checkExpect(testMaze.graph.get(0).x, 0);
    t.checkExpect(testMaze.graph.get(0).y, 0);
    t.checkExpect(testMaze.graph.get(testMaze.graph.size() - 1).x, testMaze.width - 1);
    t.checkExpect(testMaze.graph.get(testMaze.graph.size() - 1).y, testMaze.height - 1);
    t.checkExpect(testMaze.graph.get(testMaze.width).left, new EmptyCell());
    t.checkExpect(testMaze.graph.get(testMaze.width).top, testMaze.graph.get(0));
    t.checkExpect(testMaze.graph.get(testMaze.width).right, testMaze.graph.get(testMaze.width + 1));
    t.checkExpect(testMaze.graph.get(testMaze.width).bottom, new EmptyCell());
  }

  // tests the makeConnections method in MazeWorld
  void testMakeConnections(Tester t) {
    initData();

    testMaze.makeConnections();

    // Test cell 0,0
    t.checkExpect(testMaze.graph.get(0).left, new EmptyCell());
    t.checkExpect(testMaze.graph.get(0).top, new EmptyCell());
    t.checkExpect(testMaze.graph.get(0).right, testMaze.graph.get(1));
    t.checkExpect(testMaze.graph.get(0).bottom, testMaze.graph.get(2));

    // Test cell 1,0
    t.checkExpect(testMaze.graph.get(1).left, testMaze.graph.get(0));
    t.checkExpect(testMaze.graph.get(1).top, new EmptyCell());
    t.checkExpect(testMaze.graph.get(1).right, new EmptyCell());
    t.checkExpect(testMaze.graph.get(1).bottom, testMaze.graph.get(3));

    // Test cell 0,1
    t.checkExpect(testMaze.graph.get(2).left, new EmptyCell());
    t.checkExpect(testMaze.graph.get(2).top, testMaze.graph.get(0));
    t.checkExpect(testMaze.graph.get(2).right, testMaze.graph.get(3));
    t.checkExpect(testMaze.graph.get(2).bottom, new EmptyCell());

    // Test cell 1,1
    t.checkExpect(testMaze.graph.get(3).left, testMaze.graph.get(2));
    t.checkExpect(testMaze.graph.get(3).top, testMaze.graph.get(1));
    t.checkExpect(testMaze.graph.get(3).right, new EmptyCell());
    t.checkExpect(testMaze.graph.get(3).bottom, new EmptyCell());
  }

  // tests the makeEdges method in MazeWorld
  void testMakeEdges(Tester t) {
    // Initialize data
    initData();

    // Test 1 - check that the number of edges is correct
    testMaze.makeGraph();
    testMaze.makeEdges();
    t.checkExpect(testMaze.loEdge.size(), 3);

    // Test 2 - check that all edges connect neighboring cells
    boolean edgeConnectsNeighboringCells = true;
    for (Edge e : testMaze.loEdge) {
      int dx = Math.abs(e.from.x - e.to.x);
      int dy = Math.abs(e.from.y - e.to.y);
      if (!((dx == 1 && dy == 0) || (dx == 0 && dy == 1))) {
        edgeConnectsNeighboringCells = false;
      }
    }
    t.checkExpect(edgeConnectsNeighboringCells, true);

    // Test 3 - check that weights of edges are within the expected range
    boolean edgeWeightsInRange = true;
    for (Edge e : testMaze.loEdge) {
      if (e.weight < 0 || e.weight > 99) {
        edgeWeightsInRange = false;
      }
    }
    t.checkExpect(edgeWeightsInRange, true);
  }

  // tests the kruskal method in MazeWorld
  void testKruskal(Tester t) {
    initData();

    // Generate a maze using kruskal
    testMaze.kruskal();

    // Check that the generated maze has the correct number of edges
    t.checkExpect(testMaze.loEdge.size(), testMaze.graph.size() - 1);

    // Check that there are no cycles in the maze
    HashMap<Cell, Cell> representatives = new HashMap<>();
    for (Cell cell : testMaze.graph) {
      representatives.put(cell, cell);
    }
    for (Edge edge : testMaze.loEdge) {
      t.checkExpect(testMaze.cycles(representatives, edge.from, edge.to), false);
    }

  }

  // tests the cycles method in MazeWorld
  void testCycles(Tester t) {
    initData();

    HashMap<String, String> representatives = new HashMap<>();
    representatives.put("a", "a");
    representatives.put("b", "b");
    representatives.put("c", "c");
    representatives.put("d", "d");

    t.checkExpect(this.testMaze.cycles(representatives, "c", "d"), false);
    t.checkExpect(this.testMaze.cycles(representatives, "a", "b"), false);
    t.checkExpect(this.testMaze.cycles(representatives, "b", "c"), false);
    t.checkExpect(this.testMaze.cycles(representatives, "a", "d"), true);
  }

  // tests the root method in MazeWorld
  void testRoot(Tester t) {
    initData();

    HashMap<String, String> representatives = new HashMap<>();
    representatives.put("a", "a");
    representatives.put("b", "b");
    representatives.put("c", "c");
    representatives.put("d", "d");

    t.checkExpect(this.testMaze.root(representatives, "a"), "a");
    t.checkExpect(this.testMaze.root(representatives, "b"), "b");
    t.checkExpect(this.testMaze.root(representatives, "c"), "c");
    t.checkExpect(this.testMaze.root(representatives, "d"), "d");

  }

  void testOnKeyEvent(Tester t) {
    initData();

    this.testMaze.onKeyEvent("d");
    // Check if depthSearch is true after pressing "d"
    t.checkExpect(this.testMaze.depthSearch, true);
    // Check if the solution path is not empty after pressing "d"
    t.checkExpect(this.testMaze.solutionPath.size() > 0, true);

    this.testMaze.onKeyEvent("b");
    // Check if breadthSearch is true after pressing "b"
    t.checkExpect(this.testMaze.breadthSearch, true);
    // Check if the solution path is not empty after pressing "b"
    t.checkExpect(this.testMaze.solutionPath.size() > 0, true);

  }

  void testContainsEdge(Tester t) {
    initData();

    // edge from cell1 (0,0) to cell2 (1,0)
    t.checkExpect(testMaze.containsEdge(edge1), true);
    // edge from cell2 (1,0) to cell3 (0,1)
    t.checkExpect(testMaze.containsEdge(edge2), false);
    // edge from cell3 (0, 1) to cell1 (0,0)
    t.checkExpect(testMaze.containsEdge(edge3), true);

  }

  void testSolveDFS(Tester t) {
    initData();

    testMaze.solveDFS();
    // test that the solution path contains cells
    t.checkExpect(testMaze.solutionPath.size(), 3);
    // test the that maze is solved from start to end
    t.checkExpect(testMaze.solved, true);
    ArrayList<Cell> solutionPath = this.testMaze.solutionPath;
    Cell firstCell = solutionPath.get(0);
    Cell lastCell = solutionPath.get(solutionPath.size() - 1);
    int mazeWidth = this.testMaze.width - 1;
    int mazeHeight = this.testMaze.height - 1;
    // test the x and y coordinates of start and ending cells (top left & bottom
    // right)
    t.checkExpect(firstCell.x, 0);
    t.checkExpect(firstCell.y, 0);
    t.checkExpect(lastCell.x, mazeWidth);
    t.checkExpect(lastCell.y, mazeHeight);

    // Test that solveDFS works correctly for a 20x20 maze
    MazeWorld maze2 = new MazeWorld(20, 20);
    maze2.solveDFS();
    t.checkExpect(maze2.solutionPath.get(0), maze2.graph.get(0));
    t.checkExpect(maze2.solutionPath.get(maze2.solutionPath.size() - 1),
        maze2.graph.get(maze2.graph.size() - 1));

  }

  void testSolveBFS(Tester t) {
    initData();

    testMaze.solveBFS();
    t.checkExpect(testMaze.solutionPath.size(), 3);

    ArrayList<Cell> solutionPath = this.testMaze.solutionPath;
    Cell firstCell = solutionPath.get(0);
    Cell lastCell = solutionPath.get(solutionPath.size() - 1);
    int mazeWidth = this.testMaze.width - 1;
    int mazeHeight = this.testMaze.height - 1;
    // test the x and y coordinates of start and ending cells (top left & bottom
    // right)
    t.checkExpect(firstCell.x, 0);
    t.checkExpect(firstCell.y, 0);
    t.checkExpect(lastCell.x, mazeWidth);
    t.checkExpect(lastCell.y, mazeHeight);

    // Test that solveBFS works correctly for a 20x20 maze
    MazeWorld maze3 = new MazeWorld(20, 20);
    maze3.solveBFS();
    t.checkExpect(maze3.solutionPath.get(0), maze3.graph.get(0));
    t.checkExpect(maze3.solutionPath.get(maze3.solutionPath.size() - 1),
        maze3.graph.get(maze3.graph.size() - 1));

  }

  void drawPlay(Tester t) {
    initData();
  }

  void drawFinished(Tester t) {
    initData();
  }

  // tests big bang
  void testBigBang(Tester t) {
    // size must be a factor of 600 to fit the crop of the background
    // Possible sizes: 1, 2, 3, 4, 5, 6, 8, 10, 12, 15, 20, 24, 25, 30, 40, 50,
    // 60, 75 ...
    int size = 15;
    MazeWorld scene = new MazeWorld(size, size);
    int sceneSizeX = 600;
    int sceneSizeY = 600;

    // Creates the game with a 750x800 canvas
    scene.bigBang(sceneSizeX, sceneSizeY, 0.001);

  }

}