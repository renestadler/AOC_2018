/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adventofcode;

import com.google.common.collect.Lists;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import java.awt.Point;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm.SingleSourcePaths;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

/**
 *
 * @author Rene
 */
public class HelpObjects {
}

class Day4Sorter implements Comparable {

    Calendar dateTime;
    String s;

    public Day4Sorter(String date, String s) {
        int year = Integer.parseInt(date.substring(0, 4));
        int month = Integer.parseInt(date.substring(5, 7));
        int day = Integer.parseInt(date.substring(8, 10));
        int hour = Integer.parseInt(date.substring(11, 13));
        int minute = Integer.parseInt(date.substring(14, 16));
        dateTime = new Calendar.Builder().setDate(year, month - 1, day).setTimeOfDay(hour, minute, 0).build();
        this.s = s;
    }

    @Override
    public int compareTo(Object o) {
        if (dateTime.getTimeInMillis() - ((Day4Sorter) o).dateTime.getTimeInMillis() < 0) {
            return -1;
        } else if (dateTime.getTimeInMillis() - ((Day4Sorter) o).dateTime.getTimeInMillis() > 0) {
            return 1;
        }
        return 0;
    }

    @Override
    public String toString() {
        return "Day4Sorter{" + "dateTime=" + dateTime.getTimeInMillis() + ", s=" + s + '}';
    }

}

class Guard {

    ArrayList<ArrayList<GuardState>> days;
    HashMap<Integer, Integer> map;
    int fullTime = 0;
    int maxDay = 0;

    public Guard() {
        days = new ArrayList();
        map = new HashMap();
    }

    void addDay(ArrayList<GuardState> ar) {
        days.add(ar);
    }

    void calcMap() {
        fullTime = 0;
        for (ArrayList<GuardState> day : days) {
            for (int i = 0; i < day.size() - 1; i++) {
                int min = day.get(i).time;
                int max = day.get(i + 1).time;
                for (int j = min; j < max; j++) {
                    if (map.get(j) == null) {
                        map.put(j, 1);
                    } else {
                        map.put(j, map.get(j) + 1);
                    }
                }
                fullTime += (max - min);
                i++;
            }
        }
        for (Entry<Integer, Integer> e : map.entrySet()) {
            if (maxDay == 0) {
                maxDay = e.getKey();
            }
            if (e.getValue() >= map.get(maxDay)) {
                maxDay = e.getKey();
            }
        }
    }

    int getMinute(int i) {
        if (map.get(i) == null) {
            return 0;
        } else {
            return map.get(i);
        }
    }
}

class GuardState {

    int time;

    GuardState(int get) {
        time = get;
    }
}

class Area extends Rectangle {

    public Area(int x, int y, int width, int heigth) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = heigth;
    }

    Area(Rectangle intersection) {
        super(intersection);
    }

    @Override
    public String toString() {
        return "Area{" + x + ", " + y + ", " + width + ", " + height + '}';
    }

}

class Day4Point {

    int x;
    int y;

    public Day4Point(int x, int y) {
        this.x = x;
        this.y = y;

    }
}

class Connection implements Comparable {

    char curNode;
    boolean used = false;
    ArrayList<Connection> dependent;
    ArrayList<Connection> before;

    public Connection(char curNode) {
        this.curNode = curNode;
        this.dependent = new ArrayList();
        this.before = new ArrayList();
    }

    public void addDependency(Connection dependent) {
        this.dependent.add(dependent);
        Collections.sort(this.dependent);
    }

    public void addBefore(Connection before) {
        this.before.add(before);
        Collections.sort(this.before);
    }

    @Override
    public int compareTo(Object o) {
        return curNode - ((Connection) o).curNode;
    }

    @Override
    public String toString() {
        return "Connection{" + "curNode=" + curNode + "}\n";
    }

}

class Worker {

    Connection curNode;
    int timeSpent;
}

class WorkTreeNode {

    int[] metadata;
    WorkTreeNode childs[];
    int next;

    public WorkTreeNode(int metadata, int childs) {
        this.metadata = new int[metadata];
        this.childs = new WorkTreeNode[childs];
    }

    public static WorkTreeNode processInput(ArrayList<String> input) {
        int childs = Integer.parseInt(input.remove(0));
        int metadata = Integer.parseInt(input.remove(0));
        WorkTreeNode curNode = new WorkTreeNode(metadata, childs);
        for (int i = 0; i < childs; i++) {
            curNode.childs[i] = processInput(input);
        }
        for (int i = 0; i < metadata; i++) {
            curNode.metadata[i] = Integer.parseInt(input.remove(0));
        }
        return curNode;
    }

    public int getMetadata() {
        int metadata = 0;
        for (int i = 0; i < this.metadata.length; i++) {
            metadata += this.metadata[i];
        }
        for (int i = 0; i < this.childs.length; i++) {
            metadata += childs[i].getMetadata();
        }
        return metadata;
    }

    public int getValue() {
        int metadata = 0;
        if (this.childs.length == 0) {
            for (int i = 0; i < this.metadata.length; i++) {
                metadata += this.metadata[i];
            }
        } else {
            ArrayList<Integer> childPos = new ArrayList<>();
            for (int i = 0; i < this.metadata.length; i++) {
                if (this.metadata[i] <= childs.length && this.metadata[i] > 0) {
                    childPos.add(this.metadata[i] - 1);
                }
            }
            for (int i = 0; i < childPos.size(); i++) {
                metadata += childs[childPos.get(i)].getValue();
            }
            Collections.sort(childPos);
        }
        return metadata;
    }

    @Override
    public String toString() {
        return "WorkTreeNode{" + "metadata=" + metadata + ", childs=" + childs + ", next=" + next + '}';
    }

}

class MarbleGame {

    long maxVal;
    long playerPoints[];
    MarbleNode curNode;

    public MarbleGame(long maxVal, int playerPoints) {
        this.maxVal = maxVal;
        this.playerPoints = new long[playerPoints];
    }

    public void calcGame() {
        for (long i = 0; i <= maxVal; i++) {
            if (i == 0) {
                curNode = new MarbleNode(0);
            } else if (i % 23 == 0) {
                playerPoints[(int) i % playerPoints.length] += i;
                curNode = curNode.lastNode.lastNode.lastNode.lastNode.lastNode.lastNode.lastNode.lastNode;
                playerPoints[(int) i % playerPoints.length] += curNode.removeAfter(i);
                curNode = curNode.nextNode;

            } else {
                curNode = curNode.nextNode;
                curNode.addAfter(i);
                curNode = curNode.nextNode;
            }
        }
    }

    public long maxPlayer() {
        if (playerPoints == null) {
            return -1;
        }
        long max = 0;
        for (long i : playerPoints) {
            if (i > max) {
                max = i;
            }
        }
        return max;
    }

    class MarbleNode {

        long val;
        MarbleNode nextNode;
        MarbleNode lastNode;

        private MarbleNode(long i) {
            val = i;
            nextNode = this;
            lastNode = this;
        }

        private void addAfter(long i) {
            MarbleNode temp = nextNode;
            nextNode = new MarbleNode(i);
            temp.lastNode = nextNode;
            nextNode.nextNode = temp;
            nextNode.lastNode = this;
        }

        private long removeAfter(long i) {
            MarbleNode temp = nextNode;
            nextNode = temp.nextNode;
            nextNode.lastNode = this;
            return temp.val;
        }
    }
}

class Position {

    int x;
    int y;
    int xOffset;
    int yOffset;

    public Position(int x, int y, int xOffset, int yOffset) {
        this.x = x;
        this.y = y;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    public void next() {
        x += xOffset;
        y += yOffset;
    }
}

class Walker {

    int x, y, direction, walkDir;

    public Walker(int x, int y, int direction, int walkDir) {
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.walkDir = walkDir;
    }

    void walk() {
        switch (direction) {
            case 0:
                y--;
                break;
            case 1:
                x--;
                break;
            case 2:
                y++;
                break;
            case 3:
                x++;
                break;
        }
    }

    String coords() {
        return x + "," + y;
    }

    void cross() {
        if (walkDir == 0) {
            direction--;
            if (direction == -1) {
                direction = 3;
            }
        }
        if (walkDir == 2) {
            direction++;
            if (direction == 4) {
                direction = 0;
            }
        }
        walkDir++;
        if (walkDir > 2) {
            walkDir = 0;
        }
    }

    Walker copy() {
        return new Walker(x, y, direction, walkDir);
    }
}

class MarbleNode {

    int val;
    MarbleNode nextNode;
    MarbleNode lastNode;

    public MarbleNode(int i) {
        val = i;
        nextNode = this;
        lastNode = this;
    }

    void addAfter(int i) {
        MarbleNode temp = nextNode;
        nextNode = new MarbleNode(i);
        temp.lastNode = nextNode;
        nextNode.lastNode = this;
        nextNode.nextNode = temp;
        nextNode.lastNode = this;
    }

    int removeAfter(int i) {
        MarbleNode temp = nextNode;
        nextNode = temp.nextNode;
        nextNode.lastNode = this;
        return temp.val;
    }

}

class Unit implements Comparable {

    boolean type;
    int x, y;
    int health = 200;
    final int ATTACK = 3;
    Unit enemy = null;

    public Unit(boolean type, int x, int y) {
        this.type = type;
        this.x = x;
        this.y = y;
    }

    @Override
    public int compareTo(Object o) {
        if (y - ((Unit) (o)).y == 0) {
            return x - ((Unit) (o)).x;
        } else {
            return y - ((Unit) (o)).y;
        }
    }

    @Override
    public String toString() {
        return "Unit{" + "type=" + type + ", x=" + x + ", y=" + y + ", health=" + health + ", ATTACK=" + ATTACK + '}';
    }

    boolean setNearest(ArrayList<Unit> people) {
        enemy = null;
        for (Unit unit : people) {
            if (unit.type != type) {

            }
        }
        if (enemy != null) {
            return true;
        } else {
            return false;
        }
    }

}

class BitOperators {

    static void addi(Register a, int add, Register result) {
        result.value = a.value + add;
    }

    static void addr(Register a, Register b, Register result) {
        result.value = a.value + b.value;
    }

    static void muli(Register a, int add, Register result) {
        result.value = a.value * add;
    }

    static void mulr(Register a, Register b, Register result) {
        result.value = a.value * b.value;
    }

    static void bani(Register a, int add, Register result) {
        result.value = a.value & add;
    }

    static void banr(Register a, Register b, Register result) {
        result.value = a.value & b.value;
    }

    static void bori(Register a, int add, Register result) {
        result.value = a.value | add;
    }

    static void borr(Register a, Register b, Register result) {
        result.value = a.value | b.value;
    }

    static void seti(int a, int add, Register result) {
        result.value = a;
    }

    static void setr(Register a, Register b, Register result) {
        result.value = a.value;
    }

    static void gtir(int a, Register b, Register result) {
        result.value = a > b.value ? 1 : 0;
    }

    static void gtri(Register a, int b, Register result) {
        result.value = a.value > b ? 1 : 0;
    }

    static void gtrr(Register a, Register b, Register result) {
        result.value = a.value > b.value ? 1 : 0;
    }

    static void eqir(int a, Register b, Register result) {
        result.value = a == b.value ? 1 : 0;
    }

    static void eqri(Register a, int b, Register result) {
        result.value = a.value == b ? 1 : 0;
    }

    static void eqrr(Register a, Register b, Register result) {
        result.value = a.value == b.value ? 1 : 0;
    }

}

class Register {

    int value;

    public Register(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value + "";
    }

}

class Problem15 {

    public String solvePart1() {
        processInput(3);

        // Simulate combat
        while (!combatOver()) {
            simulateRound();
        }

        return String.valueOf(getOutcome());
    }

    public String solvePart2() {
        /*
     * Unfortunately, checking to see if a particular elven attack power satisfies this problem is expensive.
     * Thus, we should minimize the number of tests we need to do in order to find our answer.
     *
     * Our total search space is from 4 (our given minimum) to 200 (at which elves one-shot goblins) since there is no
     * benefit to survivability once they are that powerful.
     *
     * We will use binary search to aggressively check our search space, and we will do so in parallel for an extra
     * boost.
         */
        final List powers = IntStream.rangeClosed(4, 200).boxed().collect(Collectors.toList());

        int minSuccessPower = Integer.MAX_VALUE;
        int maxFailedPower = Integer.MIN_VALUE;

        final int numCores = Runtime.getRuntime().availableProcessors();

        final ConcurrentHashMap outcomes = new ConcurrentHashMap<>();
        final ConcurrentLinkedQueue successes = new ConcurrentLinkedQueue<Integer>();
        final ConcurrentLinkedQueue failures = new ConcurrentLinkedQueue<Integer>();
        final ConcurrentLinkedQueue<Predicate<Integer>> removals = new ConcurrentLinkedQueue<Predicate<Integer>>();

        while (maxFailedPower + 1 != minSuccessPower && !powers.isEmpty()) {
            // Parallelize the search
            Lists.partition(powers, (powers.size() / numCores) + 1).parallelStream().forEach(searchSpace -> {
                // Pick the midpoint of our assigned search space
                final int power = (int) (((List) searchSpace).get(((Collection) searchSpace).size() / 2));

                if (outcomes.containsKey(power)) {
                    return; // Skip if we've already tested this
                }

                // Test it
                final Problem15 problem = new Problem15();
                problem.processInput(power);
                final int numElves = problem.elves.size();
                while (!problem.combatOver()) {
                    problem.simulateRound();
                }

                if (problem.elves.size() == numElves) {
                    // Elves survived
                    successes.add(power);
                    outcomes.put(power, problem.getOutcome());
                    removals.add(i -> i >= power);
                } else {
                    // Elves did not survive
                    failures.add(power);
                    removals.add(i -> i <= power);
                }
            });

            // Aggregate the parallelized search results
            removals.forEach(powers::removeIf);
            while (!successes.isEmpty()) {
                minSuccessPower = Math.min(minSuccessPower, (int) successes.poll());
            }
            while (!failures.isEmpty()) {
                maxFailedPower = Math.max(maxFailedPower, (int) failures.poll());
            }
        }

        return String.valueOf(outcomes.get(minSuccessPower));
    }

    /**
     * Checks if combat is over. Happens when either all the elves or all the
     * goblins have been slain
     */
    private boolean combatOver() {
        return elves.isEmpty() || goblins.isEmpty();
    }

    /**
     * Gets the outcome of the combat
     */
    private int getOutcome() {
        final int totalHp = Stream.of(elves, goblins).flatMap(Collection::stream).mapToInt(unit -> unit.hitPoints).sum();
        return rounds * totalHp;
    }

    /**
     * How many rounds of combat we have seen so far
     */
    private int rounds;

    /**
     * The cave's layout
     */
    private char[][] cave;

    /**
     * The list of alive elves
     */
    private List<Unit> elves;

    /**
     * The list of alive goblins
     */
    private List<Unit> goblins;

    /**
     * A cached of currently occupied points (occupied by elves or goblins)
     */
    private Set<Point> occupiedPoints;

    /**
     * A cache of adjacent points from a given point
     */
    private Map<Point, List<Point>> cachedAdjacentPoints;

    /**
     * Simulates a round of combat.
     */
    private void simulateRound() {
        // Get all the units that are alive at the start of the round
        final Collection startingUnits = Stream.of(elves, goblins)
                .flatMap(Collection::stream)
                .sorted(UNIT_READING_ORDER)
                .collect(Collectors.toList());

        // Simulate each unit's turn
        for (final Object unit : startingUnits) {
            // Check if combat is over
            if (combatOver()) {
                return;
            }

            // Check if the unit is alive
            if (!elves.contains(unit) && !goblins.contains(unit)) {
                continue;
            }

            // Try to see if there's someone next to us that we can attack
            if (attack((Unit) unit)) {
                continue; // We successfully attacked
            }

            // If there's no one we could attack, we have to move so that we are within attack range and then attack
            move((Unit) unit);
            attack((Unit) unit);
        }

        rounds++; // We completed the round without combat ending
    }

    /**
     * Moves the unit according to its movement rules.
     */
    private void move(final Unit unit) {
        // Get our list of enemies
        final ArrayList enemies = new ArrayList<Unit>();
        if (elves.contains(unit)) {
            enemies.addAll(goblins);
        }
        if (goblins.contains(unit)) {
            enemies.addAll(elves);
        }

        final PathFinder pathFinder = new PathFinder(cave, occupiedPoints, unit.position);
        pathFinder.calculateMove(enemies).ifPresent(newPosition -> {
            occupiedPoints.remove(unit.position);
            unit.position = (Point) (newPosition);
            occupiedPoints.add(unit.position);
        });
    }

    /**
     * Causes the unit to attack according to its attack rules. Returns true if
     * it successfully attacked another unit.
     */
    private boolean attack(final Unit unit) {
        // Get our list of enemies
        final ArrayList enemies = new ArrayList<Unit>();
        if (elves.contains(unit)) {
            enemies.addAll(goblins);
        }
        if (goblins.contains(unit)) {
            enemies.addAll(elves);
        }

        // Find if any of our enemies are adjacent to us
        final Collection adjacentEnemies = (Collection) enemies.stream()
                .filter(enemy -> getAdjacentPoints(unit.position)
                .stream()
                .anyMatch(point -> ((Unit) enemy).position.x == point.x && ((Unit) enemy).position.y == point.y))
                .collect(Collectors.toList());

        if (adjacentEnemies.isEmpty()) {
            return false; // There are no enemies adjacent to us to attack
        }

        // Find the enemy with the fewest hit points
        final HashMap<Integer, ArrayList<Unit>> e = (HashMap) adjacentEnemies.stream()
                // Group enemies by their hit points
                .collect(Collectors.groupingBy(enemy -> ((Unit) enemy).hitPoints, Collectors.toList()));
        final Unit preferredEnemy = e.entrySet().stream()
                // Get enemies who have the fewest hit points
                .min(Comparator.comparingInt(Map.Entry::getKey)).map(Map.Entry::getValue).orElse(null).stream()
                // Tie-break using reading order
                .min(UNIT_READING_ORDER).orElseThrow(null);

        // Attack!
        preferredEnemy.hitPoints -= unit.attackPower;

        // Check for and resolve enemy deaths
        if (preferredEnemy.hitPoints <= 0) {
            elves.remove(preferredEnemy);
            goblins.remove(preferredEnemy);
            occupiedPoints.remove(preferredEnemy.position);
        }

        return true;
    }

    /**
     * Returns all points adjacent to the given point.
     */
    private List<Point> getAdjacentPoints(final Point point) {
        if (!cachedAdjacentPoints.containsKey(point)) {
            final ArrayList<Point> adjacentPoints = new ArrayList<Point>(4);
            // Test if the point to the left is reachable
            if (point.x - 1 >= 0 && cave[point.x - 1][point.y] != '#') {
                adjacentPoints.add(new Point(point.x - 1, point.y));
            }
            // Test if the point to the right is reachable
            if (point.x + 1 < cave.length && cave[point.x + 1][point.y] != '#') {
                adjacentPoints.add(new Point(point.x + 1, point.y));
            }
            // Test if the point up is reachable
            if (point.y - 1 >= 0 && cave[point.x][point.y - 1] != '#') {
                adjacentPoints.add(new Point(point.x, point.y - 1));
            }
            // Test if the point down is reachable
            if (point.y + 1 < cave[0].length && cave[point.x][point.y + 1] != '#') {
                adjacentPoints.add(new Point(point.x, point.y + 1));
            }
            cachedAdjacentPoints.put(point, adjacentPoints);
        }
        return cachedAdjacentPoints.get(point);
    }

    /**
     * Processes the input given the provided elven attack power.
     */
    private void processInput(final int elvenAttackPower) {
        final String[] lines = Inputs.input15.split("\n");

        final int width = lines[0].length();
        final int height = lines.length;

        // Process cave structure
        cave = new char[width][height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (lines[y].charAt(x) == '#') {
                    cave[x][y] = '#';
                } else {
                    cave[x][y] = '.';
                }
            }
        }

        // Process elves and goblins
        elves = new ArrayList<>();
        goblins = new ArrayList<>();
        occupiedPoints = new HashSet<>();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                switch (lines[y].charAt(x)) {
                    case 'E':
                        final Point elfLocation = new Point(x, y);
                        occupiedPoints.add(elfLocation);
                        final Unit elf = new Unit(elfLocation, elvenAttackPower);
                        elves.add(elf);
                        break;
                    case 'G':
                        final Point goblinLocation = new Point(x, y);
                        occupiedPoints.add(goblinLocation);
                        final Unit goblin = new Unit(goblinLocation);
                        goblins.add(goblin);
                        break;
                }
            }
        }

        // Initialize the number of rounds
        rounds = 0;

        // Initialize a cache for adjacent points
        cachedAdjacentPoints = new ConcurrentHashMap<>();
    }

    /**
     * Unit defines an elf or goblin with a given position, hit points, and
     * attack power.
     */
    static class Unit {

        private static final int GOBLIN_ATTACK_POWER = 3;

        Point position;
        int hitPoints = 200;
        final int attackPower;

        Unit(Point position) {
            this(position, GOBLIN_ATTACK_POWER);
        }

        Unit(Point position, int attackPower) {
            this.position = position;
            this.attackPower = attackPower;
        }
    }

    /**
     * Class used for determining the ideal route between two points in the
     * cave.
     */
    static class PathFinder {

        private final char[][] cave;
        private final Set<Point> occupiedPoints;
        private final Graph<Point, DefaultEdge> graph;
        private final Point startingPoint;

        PathFinder(final char[][] cave, final Set<Point> occupiedPoints, final Point startingPoint) {
            this.cave = cave;
            this.occupiedPoints = occupiedPoints;
            this.startingPoint = startingPoint;

            // Build the graph
            graph = new DefaultDirectedGraph<>(DefaultEdge.class);
            final HashSet<Point> visited = new HashSet<Point>();
            final Stack<Point> stack = new Stack<Point>();
            stack.push(startingPoint);
            while (!stack.isEmpty()) {
                final Point p = stack.pop();
                if (visited.add(p)) {
                    graph.addVertex(p);
                    for (final Point adjacent : getReachableAdjacentEdges(p)) {
                        graph.addVertex(adjacent);
                        graph.addEdge(p, adjacent);
                        stack.push(adjacent);
                    }
                }
            }
        }

        /**
         * Gets the neighbors around a point
         */
        final Function<Point, Stream<Point>> neighbors = p -> Stream.of(
                new Point(p.x - 1, p.y),
                new Point(p.x + 1, p.y),
                new Point(p.x, p.y - 1),
                new Point(p.x, p.y + 1));

        /**
         * Calculates the best move to make to reach the given enemies
         */
        Optional<Point> calculateMove(final List<Unit> enemies) {
            final DijkstraShortestPath dijkstra = new DijkstraShortestPath<>(graph);
            final ConcurrentHashMap<Point, SingleSourcePaths<Point, DefaultEdge>> paths = new ConcurrentHashMap<Point, SingleSourcePaths<Point, DefaultEdge>>();

            // Get the points surrounding enemies
            return enemies.stream().map(enemy -> enemy.position).flatMap(neighbors)
                    // Remove any unreachable points
                    .filter(graph::containsVertex)
                    .filter(point -> paths.computeIfAbsent(startingPoint, s -> dijkstra.getPaths(startingPoint)).getPath(point) != null)
                    // Collect the points into a distance map
                    .collect(Collectors.groupingBy(point -> paths.get(startingPoint).getPath(point).getLength(), Collectors.toList())).entrySet().stream()
                    // Get the points that have the shortest distance
                    .min(Comparator.comparingInt(Map.Entry::getKey)).map(Map.Entry::getValue).orElse(Collections.emptyList()).stream()
                    // Tie-break points by reading order
                    .min(POINT_READING_ORDER)
                    .map(destination
                            -> // Get the points surrounding the starting point
                            neighbors.apply(startingPoint)
                            // Remove any unreachable points
                            .filter(graph::containsVertex)
                            .filter(point -> paths.computeIfAbsent(destination, s -> dijkstra.getPaths(destination)).getPath(point) != null)
                            // Collect the points into a map of how fast they are
                            .collect(Collectors.groupingBy(point -> paths.get(destination).getPath(point).getLength(), Collectors.toList())).entrySet().stream()
                            // Get the points that get to our destination the fastest
                            .min(Comparator.comparingInt(Map.Entry::getKey)).map(Map.Entry::getValue).orElse(Collections.emptyList()).stream()
                            // Tie-break points by reading order
                            .min(POINT_READING_ORDER).orElseThrow(null));
        }

        /**
         * Get all reachable adjacent edges from the given point
         */
        List<Point> getReachableAdjacentEdges(final Point point) {
            final ArrayList<Point> adjacentPoints = new ArrayList<Point>(4);
            // Test if the point to the left is reachable
            if (point.x - 1 >= 0 && cave[point.x - 1][point.y] != '#') {
                final Point p = new Point(point.x - 1, point.y);
                if (!occupiedPoints.contains(p)) {
                    adjacentPoints.add(p);
                }
            }
            // Test if the point to the right is reachable
            if (point.x + 1 < cave.length && cave[point.x + 1][point.y] != '#') {
                final Point p = new Point(point.x + 1, point.y);
                if (!occupiedPoints.contains(p)) {
                    adjacentPoints.add(p);
                }
            }
            // Test if the point above is reachable
            if (point.y - 1 >= 0 && cave[point.x][point.y - 1] != '#') {
                final Point p = new Point(point.x, point.y - 1);
                if (!occupiedPoints.contains(p)) {
                    adjacentPoints.add(p);
                }
            }
            // Test if the point below is reachable
            if (point.y + 1 < cave[0].length && cave[point.x][point.y + 1] != '#') {
                final Point p = new Point(point.x, point.y + 1);
                if (!occupiedPoints.contains(p)) {
                    adjacentPoints.add(p);
                }
            }
            return adjacentPoints;
        }
    }

    /**
     * Compares points by their reading order.
     */
    private static final Comparator<Point> POINT_READING_ORDER = Comparator
            .comparingInt((Point point) -> point.y)
            .thenComparingInt(point -> point.x);

    /**
     * Compares units by their reading order.
     */
    private static final Comparator<Unit> UNIT_READING_ORDER = Comparator
            .comparingInt((Unit unit) -> unit.position.y)
            .thenComparingInt(unit -> unit.position.x);
}

class Problem17 {

    Point p;
    Problem17 last;
    Problem17 next;

    public Problem17(Point p) {
        this.p = p;
    }

}

class Problem20 {

    int maxx = 0, maxy = 0, minx = 2000, miny = 2000;
    int[][] dist = new int[2000][2000];
    boolean[][] visited = new boolean[2000][2000];
    char[][] map = new char[2000][2000];
    String regex = Inputs.input20.replace('^', ' ').replace('$', ' ').trim();
    int sum = 0;
    int maxDistance = 0;

    public Problem20() {
        int x = 1000, y = 1000;
        map[x][y] = 'X';
        calcPath(x, y, 0, 0);
        System.out.println(maxDistance);
        System.out.println(sum);
    }

    private void calcPath(int x, int y, int i, int d) {
        while (i < regex.length()) {
            if (regex.charAt(i) == 'E') {
                x++;
                map[x][y] = '|';
                x++;
                map[x][y] = '.';
                if (x > maxx) {
                    maxx = x;
                }
            } else if (regex.charAt(i) == 'W') {
                x--;
                map[x][y] = '|';
                x--;
                map[x][y] = '.';
                if (x < minx) {
                    minx = x;
                }
            } else if (regex.charAt(i) == 'N') {
                y--;
                map[x][y] = '-';
                y--;
                map[x][y] = '.';
                if (y < miny) {
                    miny = y;
                }
            } else if (regex.charAt(i) == 'S') {
                y++;
                map[x][y] = '-';
                y++;
                map[x][y] = '.';
                if (y > maxy) {
                    maxy = y;
                }
            } else if (regex.charAt(i) == '(') {
                int parenLevel = 0;
                boolean newCond = true;
                while (i < regex.length()) {
                    i++;
                    if (regex.charAt(i) == '(') {
                        parenLevel++;
                    } else if (regex.charAt(i) == ')') {
                        parenLevel--;
                        if (parenLevel < 0) {
                            calcPath(x, y, i + 1, d);
                            return;
                        }
                    } else if (regex.charAt(i) == '|') {
                        if (parenLevel == 0) {
                            newCond = true;
                        }
                    } else if (parenLevel == 0) {
                        if (newCond) {
                            calcPath(x, y, i, d);
                            newCond = false;
                        }
                    }
                }
            } else {
                return;
            }
            i++;
            d++;
            if (d >= 1000 && !visited[x][y]) {
                visited[x][y] = true;
                sum++;
            }
            if (dist[x][y] == 0 || dist[x][y] > d) {
                dist[x][y] = d;
                if (d > maxDistance) {
                    maxDistance = d;
                }
            }
        }
    }
}

class GeoCoords {

    int x, y;
    int geoIndex;
    int erosionLevel;
    GeoLocationType type;

    public GeoCoords(int x, int y, int geoIndex, int erosionLevel, GeoLocationType type) {
        this.x = x;
        this.y = y;
        this.geoIndex = geoIndex;
        this.erosionLevel = erosionLevel;
        this.type = type;
    }

}

enum GeoLocationType {
    ROCKY, WET, NARROW
}

enum ToolType {
    TORCH, CLIMBING, NEITHER
}

class MyCoords {

    Point point;
    boolean step[][];
    int cost;
    ToolType tool;
    int counter;

    public MyCoords(Point point, boolean[][] step, int cost, ToolType tool, int counter) {
        this.point = point;
        this.step = step;
        this.cost = cost;
        this.tool = tool;
        this.counter = counter;

    }

    public boolean[][] getStep() {
        boolean ar[][] = new boolean[step.length][step[0].length];
        for (int i = 0; i < step.length; i++) {
            for (int j = 0; j < step[i].length; j++) {
                ar[i][j] = step[i][j];
            }
        }
        return ar;
    }
}

class Nanobot {

    int x;
    int y;
    int z;
    int range;

    public Nanobot(int x, int y, int z, int range) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.range = range;
    }

    @Override
    public String toString() {
        return "Nanobot{" + "x=" + x + ", y=" + y + ", z=" + z + ", range=" + range + '}';
    }

    public int getDistance(Nanobot n) {
        int sum = Math.abs(this.x - n.x);
        sum += Math.abs(this.y - n.y);
        sum += Math.abs(this.z - n.z);
        return sum;
    }

}

enum DiseaseType {
    COLD("cold"),
    FIRE("fire"),
    SLASHING("slashing"),
    RADIATION("radiation"),
    BLUDGEONING("bludgeoning");

    private String action;

    // getter method 
    public String getAction() {
        return this.action;
    }

    // enum constructor - cannot be public or protected 
    private DiseaseType(String action) {
        this.action = action;
    }
}

class Units {

    int intiative;
    int numUnits;
    int hitPoints;
    int damage;
    LinkedList<DiseaseType> weak;
    LinkedList<DiseaseType> immune;
    DiseaseType attackType;

    public Units(int intiative, int numUnits, int hitPoints, int damage, LinkedList<DiseaseType> weak, LinkedList<DiseaseType> immune, DiseaseType attackType) {
        this.intiative = intiative;
        this.numUnits = numUnits;
        this.hitPoints = hitPoints;
        this.damage = damage;
        this.weak = weak;
        this.immune = immune;
        this.attackType = attackType;
    }

    @Override
    public String toString() {
        return "Units{" + "intiative=" + intiative + ", numUnits=" + numUnits + ", hitPoints=" + hitPoints + ", damage=" + damage + ", weak=" + weak + ", immune=" + immune + ", attackType=" + attackType + "}\n";
    }
}

class Attack {

    Units attacker;
    Units defender;
    int mult;

    public Attack(Units attacker, Units defender, int mult) {
        this.attacker = attacker;
        this.defender = defender;
        this.mult = mult;
    }

    @Override
    public String toString() {
        return "Attack{" + "attacker=" + attacker + ", defender=" + defender + ", multiplicator=" + mult + '}';
    }
}

class D4Point{
    int w,x,y,z;

    public D4Point(int w, int x, int y, int z) {
        this.w = w;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public String toString() {
        return "D4Point{" + "w=" + w + ", x=" + x + ", y=" + y + ", z=" + z + '}';
    }
    
    public int distance(D4Point p){
        return Math.abs(w-p.w)+Math.abs(x-p.x)+Math.abs(y-p.y)+Math.abs(z-p.z);
    }
}
