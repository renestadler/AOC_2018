/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adventofcode;

import java.awt.Point;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;

/**
 *
 * @author Rene
 */
public class AdventOfCode {

    public static void day01_1() {
        int begin = 0;
        String ar[] = Inputs.input01.split("\n");
        for (String s : ar) {
            begin += Integer.parseInt(s);
        }
        System.out.println(begin);
    }

    public static void day01_2() {
        int begin = 0;
        String ar[] = Inputs.input01.split("\n");
        HashMap<Integer, Integer> hm = new HashMap();
        for (int i = 0; i < ar.length; i++) {
            begin += Integer.parseInt(ar[i]);
            if (hm.get(begin) != null) {
                System.out.println(begin);
                return;
            }
            hm.put(begin, 1);
            if (i == ar.length - 1) {
                i = -1;
            }
        }
    }

    public static void day02_1() {
        int times_2 = 0;
        int times_3 = 0;
        String ar[] = Inputs.input02.split("\n");
        for (String s : ar) {
            HashMap<Character, Integer> hm = new HashMap();
            for (char r : s.toCharArray()) {
                if (hm.get(r) == null) {
                    hm.put(r, 1);
                } else {
                    hm.put(r, hm.get(r) + 1);
                }
            }
            for (Integer i : hm.values()) {
                if (i == 3) {
                    times_3++;
                    break;
                }
            }
            for (Integer i : hm.values()) {
                if (i == 2) {
                    times_2++;
                    break;
                }
            }
        }
        System.out.println(times_3 * times_2);
    }

    public static void day02_2() {
        String ar[] = Inputs.input02.split("\n");
        for (int i = 0; i < ar.length - 1; i++) {
            for (int j = i + 1; j < ar.length; j++) {
                char ar1[] = ar[i].toCharArray();
                char ar2[] = ar[j].toCharArray();
                int diff = 0;
                int index = -1;
                for (int k = 0; k < ar[i].length(); k++) {
                    if (ar1[k] != ar2[k]) {
                        if (diff >= 1) {
                            diff++;
                            break;
                        } else {
                            diff++;
                            index = k;
                        }
                    }
                }
                if (diff == 1) {
                    for (int k = 0; k < ar1.length; k++) {
                        if (index != k) {
                            System.out.print(ar1[k]);
                        }
                    }
                    System.out.println();
                }
            }
        }
    }

    public static void day03_12() {
        String ar[] = Inputs.input03.split("\n");
        ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < ar.length; i++) {
            list.add(ar[i]);
        }
        Map<Integer, Map<Integer, Integer>> map = new HashMap<>();
        Map<Integer, Boolean> intact = new HashMap<>();

        for (String str : ar) {
            String[] claim = str.replace(":", "").split(" ");
            int id = Integer.parseInt(claim[0].replace("#", ""));
            String[] coord = claim[2].split(",");
            String[] size = claim[3].split("x");
            for (int x = 0; x < Integer.parseInt(size[0]); x++) {
                for (int y = 0; y < Integer.parseInt(size[1]); y++) {
                    int coordX = Integer.parseInt(coord[0]) + x;
                    int coordY = Integer.parseInt(coord[1]) + y;
                    Map<Integer, Integer> m = map.computeIfAbsent(coordX, k -> new HashMap<>());
                    Integer mapEntry = m.get(coordY);
                    if (mapEntry == null) {
                        m.put(coordY, id);
                        intact.putIfAbsent(id, true);
                    } else {
                        m.put(coordY, -1);
                        intact.put(mapEntry, false);
                        intact.put(id, false);
                    }
                }
            }
        }
        System.out.println(map.values().stream().flatMap(v -> v.values().stream()).filter(v -> v == -1).count());
        intact.entrySet().stream().filter(e -> e.getValue() == true).forEach(System.out::println);
    }

    public static void day04_12() {
        String ar[] = Inputs.input04.split("\n");
        Day4Sorter[] array = new Day4Sorter[ar.length];
        HashMap<Integer, Guard> guards = new HashMap<>();
        int id = -1;
        ArrayList<GuardState> list = new ArrayList<>();
        for (int i = 0; i < ar.length; i++) {
            array[i] = new Day4Sorter(ar[i].substring(1, 17), ar[i]);
        }
        Arrays.sort(array);
        for (Day4Sorter s : array) {
            if (s.s.contains("#")) {
                if (id != -1) {
                    guards.putIfAbsent(id, new Guard());
                    guards.get(id).addDay(list);
                    list = new ArrayList<>();
                }
                id = Integer.parseInt(s.s.split("#")[1].split(" ")[0]);
            } else if (s.s.contains("falls")) {
                list.add(new GuardState(s.dateTime.get(Calendar.MINUTE)));
            } else {
                list.add(new GuardState(s.dateTime.get(Calendar.MINUTE)));
            }
        }
        guards.putIfAbsent(id, new Guard());
        guards.get(id).addDay(list);

        int maxId = 0;
        int maxValue = 0;
        int maxDay = 0;
        for (Entry<Integer, Guard> e : guards.entrySet()) {
            e.getValue().calcMap();
            if (e.getValue().fullTime > maxValue) {
                maxId = e.getKey();
                maxDay = e.getValue().maxDay;
                maxValue = e.getValue().fullTime;
            }
        }
        System.out.println(maxId * maxDay);

        maxId = 0;
        int maxCount = 0;
        boolean same = false;
        maxDay = 0;
        loop:
        for (int i = 1; i <= 60; i++) {
            maxCount = -1;
            maxDay = 0;
            maxId = 0;
            for (Entry<Integer, Guard> e : guards.entrySet()) {
                if (e.getValue().getMinute(i) > maxCount) {
                    same = false;
                    maxId = e.getKey();
                    maxDay = i;
                    maxCount = e.getValue().getMinute(i);
                } else if (e.getValue().getMinute(i) == maxCount) {
                    same = true;
                }
            }
            //   System.out.println(maxId+";"+maxDay);
            if (!same) {
                //     System.out.println(maxId + ";" + maxDay + ";" + maxId * maxDay);
            }
            //  System.out.println(maxCount + "," + i);
        }
    }

    public static void day05_1() {
        char ar[] = Inputs.input05.toCharArray();
        ArrayList<Character> list = new ArrayList<>();
        for (char s : ar) {
            list.add(s);
        }
        boolean change = false;
        do {
            change = false;
            for (int i = 0; i < list.size() - 1; i++) {
                if (list.get(i) == list.get(i + 1) + 32) {
                    list.remove(i);
                    list.remove(i);
                    change = true;
                } else if (list.get(i) == list.get(i + 1) - 32) {
                    list.remove(i);
                    list.remove(i);
                    change = true;
                }
            }
        } while (change);
        System.out.println(list.size());
    }

    public static void day05_2() {
        char ar[] = Inputs.input05.toCharArray();
        int length = Integer.MAX_VALUE;
        char type = 0;
        for (int j = 65; j < 91; j++) {
            ArrayList<Character> list = new ArrayList<>();
            for (char s : ar) {
                if (s != j && s != j + 32) {
                    list.add(s);
                }
            }
            boolean change = false;
            do {
                change = false;
                for (int i = 0; i < list.size() - 1; i++) {
                    if (list.get(i) == list.get(i + 1) + 32) {
                        list.remove(i);
                        list.remove(i);
                        change = true;
                    } else if (list.get(i) == list.get(i + 1) - 32) {
                        list.remove(i);
                        list.remove(i);
                        change = true;
                    }
                }
            } while (change);
            if (list.size() < length) {
                length = list.size();
                type = (char) j;
            }
        }
        System.out.println(length);
    }

    public static void day06_12() {
        String ar[] = Inputs.input06.split("\n");
        Day4Point p[] = new Day4Point[ar.length];
        int maxx = 0;
        int maxy = 0;
        for (int i = 0; i < ar.length; i++) {
            int x = Integer.parseInt(ar[i].split(", ")[0]);
            int y = Integer.parseInt(ar[i].split(", ")[1]);
            p[i] = new Day4Point(x, y);
            if (x > maxx) {
                maxx = x;
            }
            if (y > maxy) {
                maxy = y;
            }
        }

        int[][] pointField = new int[maxx + 1][maxy + 1];

        HashMap<Integer, Integer> pointPositions = new HashMap<Integer, Integer>();

        for (int i = 0; i <= maxx; i++) {
            for (int j = 0; j <= maxy; j++) {

                int minDistance = maxx + maxy;
                int curId = -1;

                for (int k = 0; k < p.length; k++) {
                    Day4Point dp = p[k];

                    int distance = Math.abs(i - dp.x) + Math.abs(j - dp.y);
                    if (distance < minDistance) {
                        minDistance = distance;
                        curId = k;
                    } else if (distance == minDistance) {
                        curId = -1;
                    }
                }
                pointField[i][j] = curId;
                Integer numOccurences = pointPositions.get(curId);
                if (numOccurences == null) {
                    numOccurences = 1;
                } else {
                    numOccurences = numOccurences + 1;
                }
                pointPositions.put(curId, numOccurences);
            }
        }

        for (int x = 0; x <= maxx; x++) {
            int bad = pointField[x][0];
            pointPositions.remove(bad);
            bad = pointField[x][maxy];
            pointPositions.remove(bad);
        }
        for (int y = 0; y <= maxy; y++) {
            int bad = pointField[0][y];
            pointPositions.remove(bad);
            bad = pointField[maxx][y];
            pointPositions.remove(bad);
        }

        int biggest = 0;
        for (int size : pointPositions.values()) {
            if (size > biggest) {
                biggest = size;
            }
        }

        System.out.println(biggest);

        int inarea = 0;

        for (int x = 0; x <= maxx; x++) {
            for (int y = 0; y <= maxy; y++) {

                int size = 0;
                for (int i = 0; i < p.length; i++) {
                    Day4Point dp = p[i];
                    int dist = Math.abs(x - dp.x) + Math.abs(y - dp.y);
                    size += dist;
                }
                if (size < 10000) {
                    inarea++;
                }
            }
        }

        System.out.println(inarea);
    }

    public static void day07_1() {
        String ar[] = Inputs.input07.split("\n");
        HashMap<Character, Connection> hm = new HashMap<>();
        for (String s : ar) {
            char begin = (s.substring(5, 6).charAt(0));
            char end = (s.substring(36, 37).charAt(0));
            Connection c1;
            Connection c2;
            if (hm.get(begin) == null) {
                c1 = new Connection(begin);
                hm.put(begin, c1);
            } else {
                c1 = hm.get(begin);
            }
            if (hm.get(end) == null) {
                c2 = new Connection(end);
                hm.put(end, c2);
            } else {
                c2 = hm.get(end);
            }
            c1.addDependency(c2);
            c2.addBefore(c1);
        }
        ArrayList<Connection> start = new ArrayList<>();
        for (Entry<Character, Connection> e : hm.entrySet()) {
            if (e.getValue().before.isEmpty()) {
                start.add(e.getValue());
            }
        }

        String out = "";
        while (start.size() != 0) {
            Collections.sort(start);
            Connection curNode = start.get(0);
            start.remove(0);
            out += curNode.curNode;
            curNode.used = true;
            for (Connection connection1 : curNode.dependent) {
                boolean broken = false;
                for (Connection connection2 : connection1.before) {
                    if (!connection2.used) {
                        broken = true;
                        break;
                    }
                }
                if (!broken) {
                    start.add(connection1);
                }
            }
        }
        System.out.println(out);
    }

    public static void day07_2() {
        String ar[] = Inputs.input07.split("\n");
        HashMap<Character, Connection> hm = new HashMap<>();
        for (String s : ar) {
            char begin = (s.substring(5, 6).charAt(0));
            char end = (s.substring(36, 37).charAt(0));
            Connection c1;
            Connection c2;
            if (hm.get(begin) == null) {
                c1 = new Connection(begin);
                hm.put(begin, c1);
            } else {
                c1 = hm.get(begin);
            }
            if (hm.get(end) == null) {
                c2 = new Connection(end);
                hm.put(end, c2);
            } else {
                c2 = hm.get(end);
            }
            c1.addDependency(c2);
            c2.addBefore(c1);
        }
        ArrayList<Connection> start = new ArrayList<>();
        for (Entry<Character, Connection> e : hm.entrySet()) {
            if (e.getValue().before.isEmpty()) {
                start.add(e.getValue());
            }
        }

        String out = "";

        Worker w[] = new Worker[5];
        w[0] = new Worker();
        w[1] = new Worker();
        w[2] = new Worker();
        w[3] = new Worker();
        w[4] = new Worker();
        int i = -1;
        int processed = 0;
        while (processed < 26) {
            Collections.sort(start);
            i++;
            for (int j = 0; j < w.length; j++) {
                if (w[j].curNode != null && w[j].timeSpent - 1 == w[j].curNode.curNode - 6) {
                    w[j].curNode.used = true;
                    processed++;
                    for (Connection connection1 : w[j].curNode.dependent) {
                        boolean broken = false;
                        for (Connection connection2 : connection1.before) {
                            if (!connection2.used) {
                                broken = true;
                                break;
                            }
                        }
                        if (!broken) {
                            start.add(connection1);
                        }
                    }
                    w[j].timeSpent = 0;
                    w[j].curNode = null;
                }
            }
            for (int j = 0; j < w.length; j++) {
                if (w[j].curNode == null && !start.isEmpty()) {
                    w[j].curNode = start.get(0);
                    start.remove(0);
                } else if (w[j].curNode != null) {
                    w[j].timeSpent++;
                }
            }

        }
        System.out.println(i);
    }

    public static void day08_1() {
        String ar[] = Inputs.input08.split(" ");
        ArrayList<String> in = new ArrayList<>();
        for (int i = 0; i < ar.length; i++) {
            in.add(ar[i]);
        }
        System.out.println(WorkTreeNode.processInput(in).getMetadata());
    }

    public static void day08_2() {
        String ar[] = Inputs.input08.split(" ");
        ArrayList<String> in = new ArrayList<>();
        for (int i = 0; i < ar.length; i++) {
            in.add(ar[i]);
        }
        System.out.println(WorkTreeNode.processInput(in).getValue());
    }

    public static void day09_12() {
        String ar[] = Inputs.input09.split(" ");
        int players = Integer.parseInt(Inputs.input09.substring(0, 3));
        int maxNum = Integer.parseInt(Inputs.input09.split(" ")[6]);
        MarbleGame m = new MarbleGame(maxNum, players);
        m.calcGame();
        System.out.println(m.maxPlayer());
        m.maxVal = maxNum * 100;
        m.playerPoints = new long[players];
        m.calcGame();
        System.out.println(m.maxPlayer());
    }

    public static void day10_12() {
        int counter = 0;
        String ar[] = Inputs.input10.split("\n");
        Position p[] = new Position[ar.length];
        for (int i = 0; i < ar.length; i++) {
            int x = (Integer.parseInt(ar[i].substring(10, 16).trim()));
            int y = (Integer.parseInt(ar[i].substring(18, 24).trim()));
            int x0 = (Integer.parseInt(ar[i].substring(36, 38).trim()));
            int y0 = (Integer.parseInt(ar[i].substring(40, 42).trim()));
            p[i] = new Position(x, y, x0, y0);
        }

        for (int i = 0; i < 10000; i++) {
            if (i >= 1) {
                counter++;
                for (Position position : p) {
                    position.next();
                }
            }
        }

        int lastx = Integer.MAX_VALUE;
        int lasty = Integer.MAX_VALUE;
        for (int i = 0; i < 100000; i++) {
            if (i >= 1) {
                for (Position position : p) {
                    position.next();
                }
                counter++;
            }
            int minx = Integer.MAX_VALUE;
            int miny = Integer.MAX_VALUE;
            int maxx = Integer.MIN_VALUE;
            int maxy = Integer.MIN_VALUE;
            for (int j = 0; j < p.length; j++) {
                if (p[j].x < minx) {
                    minx = p[j].x;
                }
                if (p[j].x > maxx) {
                    maxx = p[j].x;
                }
                if (p[j].y < miny) {
                    miny = p[j].y;
                }
                if (p[j].y > maxy) {
                    maxy = p[j].y;
                }
            }
            if (i == 106) {
                boolean out[][] = new boolean[maxx + Math.abs(minx) + 1][maxy + Math.abs(miny) + 1];
                for (Position position : p) {
                    out[position.x + minx][position.y + miny] = true;
                }
                int mink = -1;
                int minl = 0;
                for (int k = 0; k < out[0].length; k++) {
                    for (int l = 0; l < out.length; l++) {
                        if (out[l][k] && mink == -1) {
                            mink = k;
                            minl = l;
                        }

                    }
                }
                for (int k = mink; k < out[0].length; k++) {
                    for (int l = minl; l < out.length; l++) {

                        if (out[l][k]) {
                            System.out.print("o");
                        } else {
                            System.out.print(" ");
                        }

                    }
                    System.out.println("");
                }
            } else {
                lastx = maxx - minx;
                lasty = maxy - miny;
            }
        }
        System.out.println(counter);
    }

    public static void day11_12() {
        int size = Inputs.input11;
        int ar[][] = new int[301][301];
        for (int i = 0; i < ar.length; i++) {
            for (int j = 0; j < ar[i].length; j++) {
                int rackID = i + 11;
                rackID = rackID * (j + 1);
                rackID += size;
                rackID = rackID * (i + 11);
                rackID = (rackID / 100) % 10 - 5;
                ar[i][j] = rackID;
            }
        }
        int max = 0;
        int maxx = 0;
        int maxy = 0;
        int maxsize = 0;
        for (int i = 0; i < ar.length - 2; i++) {
            for (int j = 0; j < ar[i].length - 2; j++) {
                int maxRows = 0;
                for (int k = 0; k < 3; k++) {
                    for (int m = 0; m < 3; m++) {
                        maxRows += ar[i + k][j + m];
                    }
                }
                if (maxRows > max) {
                    max = maxRows;
                    maxx = i + 1;
                    maxy = j + 1;
                }
            }
        }
        System.out.println(maxx + "," + maxy);
        max = 0;
        maxx = 0;
        maxy = 0;
        maxsize = 0;
        for (int l = 1; l <= 50; l++) {
            for (int i = 0; i < ar.length - (l); i++) {
                for (int j = 0; j < ar[i].length - (l); j++) {
                    int maxRows = 0;
                    for (int k = 0; k < l; k++) {
                        for (int m = 0; m < l; m++) {
                            maxRows += ar[i + k][j + m];
                        }
                    }
                    if (maxRows > max) {
                        max = maxRows;
                        maxx = i + 1;
                        maxy = j + 1;
                        maxsize = l;
                    }
                }
            }
        }
        System.out.println(maxx + "," + maxy + "," + maxsize);
    }

    public static void day12_1() {
        String[] ar = Inputs.input12.split("\n");
        int len = 0;
        int offset = 0;
        char[] field = ar[0].split(" ")[2].toCharArray();
        HashMap<char[], Character> hm = new HashMap<>();
        for (int i = 1; i < ar.length; i++) {
            String[] parts = ar[i].split(" ");
            if (i == 1) {
                len = (parts[0].length() - 1) / 2;
            }
            char[] tempAr = parts[0].toCharArray();
            char tempChar = parts[2].toCharArray()[0];
            hm.put(tempAr, tempChar);
        }
        for (int i = 0; i < 20; i++) {
            boolean increase = false;
            for (int j = 0; j < len * 2 + 1; j++) {
                if (field[j] == '#' || field[(field.length - 1) - j] == '#') {
                    increase = true;
                    break;
                }
            }
            if (increase) {
                offset += len;
                char[] newField = new char[field.length + len * 2];
                for (int j = len; j < newField.length - len; j++) {
                    newField[j] = field[j - len];
                }
                for (int j = 0; j < len; j++) {
                    newField[j] = '.';
                    newField[newField.length - 1 - j] = '.';

                }
                field = newField;
            }
            char[] newField = new char[field.length];
            for (int j = len; j < field.length - len; j++) {
                for (Entry<char[], Character> myEntry : hm.entrySet()) {
                    boolean found = true;
                    if (myEntry.getKey()[len] == field[j]) {
                        for (int k = 0; k < myEntry.getKey().length; k++) {
                            if (myEntry.getKey()[k] != field[j - len + k]) {
                                found = false;
                            }
                        }
                        if (found) {
                            newField[j] = myEntry.getValue();
                        }
                    }
                }
            }
            for (int j = 0; j < newField.length; j++) {
                if (newField[j] == '\0') {
                    newField[j] = '.';
                }
            }
            field = newField;
        }
        int counter = 0;
        for (int i = 0; i < field.length; i++) {
            if (field[i] == '#') {
                counter += i - offset;
            }
        }
        System.out.println(counter);
    }

    public static void day12_2() {
        String[] ar = Inputs.input12.split("\n");
        int len = 0;
        int offset = 0;
        char[] field = ar[0].split(" ")[2].toCharArray();
        HashMap<char[], Character> hm = new HashMap<>();
        for (int i = 1; i < ar.length; i++) {
            String[] parts = ar[i].split(" ");
            if (i == 1) {
                len = (parts[0].length() - 1) / 2;
            }
            char[] tempAr = parts[0].toCharArray();
            char tempChar = parts[2].toCharArray()[0];
            hm.put(tempAr, tempChar);
        }
        //Output is as the same as by 50.000.000.000 multiplied by 10.000.000.000
        for (long i = 0; i < 50; i++) {
            boolean increase = false;
            for (int j = 0; j < len * 2 + 1; j++) {
                if (field[j] == '#' || field[(field.length - 1) - j] == '#') {
                    increase = true;
                    break;
                }
            }
            if (increase) {
                offset += len;
                char[] newField = new char[field.length + len * 2];
                for (int j = len; j < newField.length - len; j++) {
                    newField[j] = field[j - len];
                }
                for (int j = 0; j < len; j++) {
                    newField[j] = '.';
                    newField[newField.length - 1 - j] = '.';

                }
                field = newField;
            }
            char[] newField = new char[field.length];
            for (int j = len; j < field.length - len; j++) {
                for (Entry<char[], Character> myEntry : hm.entrySet()) {
                    boolean found = true;
                    if (myEntry.getKey()[len] == field[j]) {
                        for (int k = 0; k < myEntry.getKey().length; k++) {
                            if (myEntry.getKey()[k] != field[j - len + k]) {
                                found = false;
                            }
                        }
                        if (found) {
                            newField[j] = myEntry.getValue();
                        }
                    }
                }
            }
            for (int j = 0; j < newField.length; j++) {
                if (newField[j] == '\0') {
                    newField[j] = '.';
                }
            }
            field = newField;
        }
        int counter = 0;
        for (int i = 0; i < field.length; i++) {
            if (field[i] == '#') {
                counter += i - offset;
            }
        }
        System.out.println("2300000000006");
    }

    public static void day13_1() {
        String[] ar = Inputs.input13.split("\n");
        HashMap<String, Character> hm = new HashMap<>();
        ArrayList<Walker> walkers = new ArrayList<>();
        for (int i = 0; i < ar.length; i++) {
            char[] current = ar[i].toCharArray();
            for (int j = 0; j < current.length; j++) {
                char c = current[j];
                if (c == 'v' || c == '^' || c == '>' || c == '<') {
                    if ((i >= 1 && (current[i - 1] == '|' || current[i - 1] == '\\' || current[i - 1] == '/' || current[i - 1] == '+'))
                            && (i <= ar.length - 2 && (current[i + 1] == '|' || current[i + 1] == '\\' || current[i + 1] == '/' || current[i + 1] == '+'))) {
                        hm.put(i + "," + j, '|');
                    } else {
                        hm.put(i + "," + j, '-');
                    }
                    switch (c) {
                        case 'v':
                            walkers.add(new Walker(i, j, 3, 0));
                            break;
                        case '^':
                            walkers.add(new Walker(i, j, 1, 0));
                            break;
                        case '<':
                            walkers.add(new Walker(i, j, 0, 0));
                            break;
                        case '>':
                            walkers.add(new Walker(i, j, 2, 0));
                            break;
                    }
                } else {
                    hm.put(i + "," + j, c);
                }
            }
        }
        loop:
        while (true) {
            for (Walker walker : walkers) {
                walker.walk();
                switch (hm.get(walker.coords())) {
                    case '+':
                        walker.cross();
                        break;
                    case '\\':
                        switch (walker.direction) {
                            case 0:
                                walker.direction = 1;
                                break;
                            case 1:
                                walker.direction = 0;
                                break;
                            case 2:
                                walker.direction = 3;
                                break;
                            case 3:
                                walker.direction = 2;
                                break;
                        }
                        break;
                    case '/':
                        switch (walker.direction) {
                            case 0:
                                walker.direction = 3;
                                break;
                            case 1:
                                walker.direction = 2;
                                break;
                            case 2:
                                walker.direction = 1;
                                break;
                            case 3:
                                walker.direction = 0;
                                break;
                        }
                        break;
                }
                for (Walker walker1 : walkers) {
                    if (!walker.equals(walker1)) {
                        if (walker.x == walker1.x && walker.y == walker1.y) {
                            System.out.println(walker.y + "," + walker.x);
                            break loop;
                        }
                    }
                }
            }
        }
    }

    public static void day13_2() {
        String[] ar = Inputs.input13.split("\n");
        HashMap<String, Character> hm = new HashMap<>();
        ArrayList<Walker> walkers = new ArrayList<>();
        for (int i = 0; i < ar.length; i++) {
            char[] current = ar[i].toCharArray();
            for (int j = 0; j < current.length; j++) {
                char c = current[j];
                if (c == 'v' || c == '^' || c == '>' || c == '<') {
                    if ((i >= 1 && (current[i - 1] == '|' || current[i - 1] == '\\' || current[i - 1] == '/' || current[i - 1] == '+'))
                            && (i <= ar.length - 2 && (current[i + 1] == '|' || current[i + 1] == '\\' || current[i + 1] == '/' || current[i + 1] == '+'))) {
                        hm.put(i + "," + j, '|');
                    } else {
                        hm.put(i + "," + j, '-');
                    }
                    switch (c) {
                        case 'v':
                            walkers.add(new Walker(i, j, 3, 0));
                            break;
                        case '^':
                            walkers.add(new Walker(i, j, 1, 0));
                            break;
                        case '<':
                            walkers.add(new Walker(i, j, 0, 0));
                            break;
                        case '>':
                            walkers.add(new Walker(i, j, 2, 0));
                            break;
                    }
                } else {
                    hm.put(i + "," + j, c);
                }
            }
        }
        loop:
        while (true) {
            HashMap<String, String> current = new HashMap<>();
            for (Walker walker : walkers) {
                current.put(walker.coords(), "");
            }
            for (int i = 0; i < ar.length; i++) {
                for (int j = 0; j < ar[0].length(); j++) {
                    if (current.get(i + "," + j) != null) {
                        //           System.out.print("A");
                    } else {
                        //         System.out.print(hm.get(i + "," + j));
                    }
                }
                //     System.out.println();
            }
            innerloop:
            for (int i = 0; i < walkers.size(); i++) {
                Walker walker = walkers.get(i);
                walker.walk();
                switch (hm.get(walker.coords())) {
                    case '+':
                        walker.cross();
                        break;
                    case '\\':
                        switch (walker.direction) {
                            case 0:
                                walker.direction = 1;
                                break;
                            case 1:
                                walker.direction = 0;
                                break;
                            case 2:
                                walker.direction = 3;
                                break;
                            case 3:
                                walker.direction = 2;
                                break;
                        }
                        break;
                    case '/':
                        switch (walker.direction) {
                            case 0:
                                walker.direction = 3;
                                break;
                            case 1:
                                walker.direction = 2;
                                break;
                            case 2:
                                walker.direction = 1;
                                break;
                            case 3:
                                walker.direction = 0;
                                break;
                        }
                        break;
                }
                for (int j = 0; j < walkers.size(); j++) {
                    Walker walker1 = walkers.get(j);
                    if (!walker.equals(walker1)) {
                        if (walker.x == walker1.x && walker.y == walker1.y) {
                            walkers.remove(walker);
                            walkers.remove(walker1);
                        }
                    }
                }
            }
            if (walkers.size() == 1) {
                System.out.println(walkers.get(0).x + 33 + "," + (walkers.get(0).y + 18));
                break loop;
            }
        }
    }

    public static void day14_1() {
        MarbleNode m = new MarbleNode(3);
        m.addAfter(7);
        m = m.nextNode;
        MarbleNode lastNode = m;
        MarbleNode startn = lastNode;
        MarbleNode elve1;
        MarbleNode elve2;
        int count = 0;
        elve1 = m.nextNode;
        elve2 = m.nextNode.nextNode;
        loop:
        while (true) {
            MarbleNode start1 = lastNode;
            int newNode = elve1.val + elve2.val;
            if (newNode >= 10) {
                lastNode.addAfter(newNode / 10 % 10);
                lastNode = lastNode.nextNode;
                lastNode.addAfter(newNode % 10);
                lastNode = lastNode.nextNode;
                count += 2;
                newNode /= 100;
            } else {
                do {
                    lastNode.addAfter(newNode % 10);
                    lastNode = lastNode.nextNode;
                    newNode /= 10;
                    count++;
                } while (newNode != 0);
            }
            if (count == Integer.parseInt(Inputs.input14) + 10) {
                MarbleNode start = lastNode.lastNode.lastNode;
                String s = "";
                int c = 0;
                do {
                    s = (start.val) + s;
                    start = start.lastNode;
                    c++;
                } while (c < 10);
                System.out.println(s);
                break loop;
                //TODO
            } else {
                int start = elve1.val;
                for (int i = 0; i <= start; i++) {
                    elve1 = elve1.nextNode;
                }
                start = elve2.val;
                for (int i = 0; i <= start; i++) {
                    elve2 = elve2.nextNode;
                }
            }
        }
    }

    public static void day14_2() {
        MarbleNode m = new MarbleNode(3);
        m.addAfter(7);
        m = m.nextNode;
        MarbleNode lastNode = m;
        MarbleNode startn = lastNode;
        MarbleNode elve1;
        MarbleNode elve2;
        int count = 2;
        elve1 = m.nextNode;
        elve2 = m.nextNode.nextNode;
        loop:
        while (true) {
            MarbleNode start1 = lastNode;
            int newNode = elve1.val + elve2.val;
            if (newNode >= 10) {
                lastNode.addAfter(newNode / 10 % 10);
                lastNode = lastNode.nextNode;
                lastNode.addAfter(newNode % 10);
                lastNode = lastNode.nextNode;
                newNode /= 100;
                count += 2;
            } else {
                do {
                    lastNode.addAfter(newNode % 10);
                    lastNode = lastNode.nextNode;
                    newNode /= 10;
                    count++;
                } while (newNode != 0);
            }
            MarbleNode start = lastNode;
            String s = "";
            int c = 0;
            do {
                s = (start.val) + s;
                start = start.lastNode;
                c++;
            } while (c < Inputs.input14.length() + 1);
            if (s.substring(0, s.length() - 1).equals(Inputs.input14) || s.substring(1).equals(Inputs.input14)) {
                System.out.println(count - Inputs.input14.length() - 1);
                break loop;
            }

            int start2 = elve1.val;
            for (int i = 0; i <= start2; i++) {
                elve1 = elve1.nextNode;
            }
            start2 = elve2.val;
            for (int i = 0; i <= start2; i++) {
                elve2 = elve2.nextNode;
            }
        }
    }

    public static void day15_12() {
        //Used code from an other person ;(
        Problem15 a = new Problem15();
        System.out.println(a.solvePart1());
        System.out.println(a.solvePart2());
    }

    public static void day16_12() {
        String field[] = Inputs.input16_1.split("\n");
        ArrayList<int[]> operations = new ArrayList<int[]>();
        ArrayList<Register[][]> registers = new ArrayList<Register[][]>();
        int[] curAr = new int[4];
        Register[][] curRegister = new Register[2][4];
        for (int i = 0; i < field.length; i++) {
            switch (i % 4) {
                case 0: {
                    Register[] inner = new Register[4];
                    String parts[] = field[i].substring(9, 19).split(",");
                    for (int j = 0; j < parts.length; j++) {
                        inner[j] = new Register(Integer.parseInt(parts[j].trim()));
                    }
                    curRegister[0] = inner;
                    break;
                }
                case 1: {
                    int[] inner = new int[4];
                    String parts[] = field[i].split(" ");
                    for (int j = 0; j < parts.length; j++) {
                        inner[j] = Integer.parseInt(parts[j]);
                    }
                    curAr = inner;
                    operations.add(curAr);
                    curAr = new int[4];
                    break;
                }
                case 2: {
                    Register[] inner = new Register[4];
                    String parts[] = field[i].substring(9, 19).split(",");
                    for (int j = 0; j < parts.length; j++) {
                        inner[j] = new Register(Integer.parseInt(parts[j].trim()));
                    }
                    curRegister[1] = inner;
                    registers.add(curRegister);
                    curRegister = new Register[2][4];
                    break;
                }
                default:
                    break;
            }
        }
        HashMap<Integer, HashMap<Integer, Integer>> codes = new HashMap<>();
        ArrayList<Integer> counters = new ArrayList<>();
        for (int j = 0; j <= 15; j++) {
            counters.add(0);
        }
        ArrayList<Integer> result = new ArrayList<>();
        for (int i = 0; i < operations.size(); i++) {
            Register[][] register = registers.get(i);
            int[] operation = operations.get(i);
            int first = operation[1];
            int second = operation[2];
            int resultR = operation[3];
            Register resultReg = new Register(0);
            int res = 0;
            counters.set(operation[0], counters.get(operation[0]) + 1);
            HashMap<Integer, Integer> hm = new HashMap<>();
            if (codes.get(operation[0]) != null) {
                hm = codes.get(operation[0]);
            }
            codes.put(operation[0], hm);
            BitOperators.addi(register[0][first], second, resultReg);
            if (resultReg.value == register[1][resultR].value) {
                res++;
                if (hm.get(0) != null) {
                    hm.put(0, hm.get(0) + 1);
                } else {
                    hm.put(0, 1);
                }
            }
            BitOperators.addr(register[0][first], register[0][second], resultReg);
            if (resultReg.value == register[1][resultR].value) {
                res++;
                if (hm.get(1) != null) {
                    hm.put(1, hm.get(1) + 1);
                } else {
                    hm.put(1, 1);
                }
            }
            BitOperators.muli(register[0][first], second, resultReg);
            if (resultReg.value == register[1][resultR].value) {
                res++;
                if (hm.get(2) != null) {
                    hm.put(2, hm.get(2) + 1);
                } else {
                    hm.put(2, 1);
                }
            }
            BitOperators.mulr(register[0][first], register[0][second], resultReg);
            if (resultReg.value == register[1][resultR].value) {
                res++;
                if (hm.get(3) != null) {
                    hm.put(3, hm.get(3) + 1);
                } else {
                    hm.put(3, 1);
                }
            }
            BitOperators.bani(register[0][first], second, resultReg);
            if (resultReg.value == register[1][resultR].value) {
                res++;
                if (hm.get(4) != null) {
                    hm.put(4, hm.get(4) + 1);
                } else {
                    hm.put(4, 1);
                }
            }
            BitOperators.banr(register[0][first], register[0][second], resultReg);
            if (resultReg.value == register[1][resultR].value) {
                res++;
                if (hm.get(5) != null) {
                    hm.put(5, hm.get(5) + 1);
                } else {
                    hm.put(5, 1);
                }
            }
            BitOperators.bori(register[0][first], second, resultReg);
            if (resultReg.value == register[1][resultR].value) {
                res++;
                if (hm.get(6) != null) {
                    hm.put(6, hm.get(6) + 1);
                } else {
                    hm.put(6, 1);
                }
            }
            BitOperators.borr(register[0][first], register[0][second], resultReg);
            if (resultReg.value == register[1][resultR].value) {
                res++;
                if (hm.get(7) != null) {
                    hm.put(7, hm.get(7) + 1);
                } else {
                    hm.put(7, 1);
                }
            }
            BitOperators.seti(first, second, resultReg);
            if (resultReg.value == register[1][resultR].value) {
                res++;
                if (hm.get(8) != null) {
                    hm.put(8, hm.get(8) + 1);
                } else {
                    hm.put(8, 1);
                }
            }
            BitOperators.setr(register[0][first], register[0][second], resultReg);
            if (resultReg.value == register[1][resultR].value) {
                res++;
                if (hm.get(9) != null) {
                    hm.put(9, hm.get(9) + 1);
                } else {
                    hm.put(9, 1);
                }
            }
            BitOperators.gtri(register[0][first], second, resultReg);
            if (resultReg.value == register[1][resultR].value) {
                res++;
                if (hm.get(10) != null) {
                    hm.put(10, hm.get(10) + 1);
                } else {
                    hm.put(10, 1);
                }
            }
            BitOperators.gtrr(register[0][first], register[0][second], resultReg);
            if (resultReg.value == register[1][resultR].value) {
                res++;
                if (hm.get(11) != null) {
                    hm.put(11, hm.get(11) + 1);
                } else {
                    hm.put(11, 1);
                }
            }
            BitOperators.gtir(first, register[0][second], resultReg);
            if (resultReg.value == register[1][resultR].value) {
                res++;
                if (hm.get(12) != null) {
                    hm.put(12, hm.get(12) + 1);
                } else {
                    hm.put(12, 1);
                }
            }
            BitOperators.eqri(register[0][first], second, resultReg);
            if (resultReg.value == register[1][resultR].value) {
                res++;
                if (hm.get(13) != null) {
                    hm.put(13, hm.get(13) + 1);
                } else {
                    hm.put(13, 1);
                }
            }
            BitOperators.eqrr(register[0][first], register[0][second], resultReg);
            if (resultReg.value == register[1][resultR].value) {
                res++;
                if (hm.get(14) != null) {
                    hm.put(14, hm.get(14) + 1);
                } else {
                    hm.put(14, 1);
                }
            }
            BitOperators.eqir(first, register[0][second], resultReg);
            if (resultReg.value == register[1][resultR].value) {
                res++;
                if (hm.get(15) != null) {
                    hm.put(15, hm.get(15) + 1);
                } else {
                    hm.put(15, 1);
                }
            }
            result.add(res);
        }
        int counter = 0;
        for (int i = 0; i < result.size(); i++) {
            if (result.get(i) >= 3) {
                counter++;
            }
        }
        System.out.println(counter);
        HashMap<Integer, ArrayList<Integer>> methods = new HashMap<>();
        for (int i = 0; i < counters.size(); i++) {
            for (Entry<Integer, Integer> entry : codes.get(i).entrySet()) {
                if (entry.getValue() == counters.get(i)) {
                    if (methods.get(i) == null) {
                        ArrayList<Integer> ar = new ArrayList();
                        ar.add(entry.getKey());
                        methods.put(i, ar);
                    } else {
                        methods.get(i).add(entry.getKey());
                    }
                }
            }
        }

        HashMap<Integer, Integer> hm = new HashMap<>();
        hm.put(0, 2);
        hm.put(1, 7);
        hm.put(2, 10);
        hm.put(3, 13);
        hm.put(4, 11);
        hm.put(5, 15);
        hm.put(6, 0);
        hm.put(7, 9);
        hm.put(8, 3);
        hm.put(9, 1);
        hm.put(10, 6);
        hm.put(11, 4);
        hm.put(12, 8);
        hm.put(13, 14);
        hm.put(14, 5);
        hm.put(15, 12);
        String ar[] = Inputs.input16_2.split("\n");
        Register[] register = new Register[4];
        for (int i = 0; i < register.length; i++) {
            register[i] = new Register(0);
        }
        for (String string : ar) {
            String cmd[] = string.split(" ");
            switch (hm.get(Integer.parseInt(cmd[0]))) {
                case 0:
                    BitOperators.addi(register[Integer.parseInt(cmd[1])], Integer.parseInt(cmd[2]), register[Integer.parseInt(cmd[3])]);
                    break;
                case 1:
                    BitOperators.addr(register[Integer.parseInt(cmd[1])], register[Integer.parseInt(cmd[2])], register[Integer.parseInt(cmd[3])]);
                    break;
                case 2:
                    BitOperators.muli(register[Integer.parseInt(cmd[1])], Integer.parseInt(cmd[2]), register[Integer.parseInt(cmd[3])]);
                    break;
                case 3:
                    BitOperators.mulr(register[Integer.parseInt(cmd[1])], register[Integer.parseInt(cmd[2])], register[Integer.parseInt(cmd[3])]);
                    break;
                case 4:
                    BitOperators.bani(register[Integer.parseInt(cmd[1])], Integer.parseInt(cmd[2]), register[Integer.parseInt(cmd[3])]);
                    break;
                case 5:
                    BitOperators.banr(register[Integer.parseInt(cmd[1])], register[Integer.parseInt(cmd[2])], register[Integer.parseInt(cmd[3])]);
                    break;
                case 6:
                    BitOperators.bori(register[Integer.parseInt(cmd[1])], Integer.parseInt(cmd[2]), register[Integer.parseInt(cmd[3])]);
                    break;
                case 7:
                    BitOperators.borr(register[Integer.parseInt(cmd[1])], register[Integer.parseInt(cmd[2])], register[Integer.parseInt(cmd[3])]);
                    break;
                case 8:
                    BitOperators.seti(Integer.parseInt(cmd[1]), Integer.parseInt(cmd[2]), register[Integer.parseInt(cmd[3])]);
                    break;
                case 9:
                    BitOperators.setr(register[Integer.parseInt(cmd[1])], register[Integer.parseInt(cmd[2])], register[Integer.parseInt(cmd[3])]);
                    break;
                case 10:
                    BitOperators.gtri(register[Integer.parseInt(cmd[1])], Integer.parseInt(cmd[2]), register[Integer.parseInt(cmd[3])]);
                    break;
                case 11:
                    BitOperators.gtrr(register[Integer.parseInt(cmd[1])], register[Integer.parseInt(cmd[2])], register[Integer.parseInt(cmd[3])]);
                    break;
                case 12:
                    BitOperators.gtir(Integer.parseInt(cmd[1]), register[Integer.parseInt(cmd[2])], register[Integer.parseInt(cmd[3])]);
                    break;
                case 13:
                    BitOperators.eqri(register[Integer.parseInt(cmd[1])], Integer.parseInt(cmd[2]), register[Integer.parseInt(cmd[3])]);
                    break;
                case 14:
                    BitOperators.eqrr(register[Integer.parseInt(cmd[1])], register[Integer.parseInt(cmd[2])], register[Integer.parseInt(cmd[3])]);
                    break;
                case 15:
                    BitOperators.eqir(Integer.parseInt(cmd[1]), register[Integer.parseInt(cmd[2])], register[Integer.parseInt(cmd[3])]);
                    break;
            }
        }
        System.out.println(register[0].value);
    }

    public static void day17_12() {
        String field[] = Inputs.input17.split("\n");
        ArrayList<Point> points = new ArrayList<>();
        for (int i = 0; i < field.length; i++) {
            String[] coords = field[i].split(", ");
            if (coords[0].split("=")[0].equals("x")) {
                int x = Integer.parseInt(coords[0].split("=")[1]);
                int y1 = Integer.parseInt(coords[1].split("=")[1].split("\\.\\.")[0]);
                int y2 = Integer.parseInt(coords[1].split("=")[1].split("\\.\\.")[1]);
                for (int j = y1; j <= y2; j++) {
                    points.add(new Point(x, j));
                }
            }
            if (coords[0].split("=")[0].equals("y")) {
                int y = Integer.parseInt(coords[0].split("=")[1]);
                int x1 = Integer.parseInt(coords[1].split("=")[1].split("\\.\\.")[0]);
                int x2 = Integer.parseInt(coords[1].split("=")[1].split("\\.\\.")[1]);
                for (int j = x1; j <= x2; j++) {
                    points.add(new Point(j, y));
                }
            }
        }
        points.sort(new Comparator<Point>() {
            @Override
            public int compare(Point o1, Point o2) {
                return o2.y - o1.y;
            }
        });
        int rows = points.get(0).y + 1;
        points.sort(new Comparator<Point>() {
            @Override
            public int compare(Point o1, Point o2) {
                return o2.x - o1.x;
            }
        });
        int cols = points.get(0).x + 2;

        char[][] waterField = new char[cols][rows];
        for (int i = 0; i < waterField.length; i++) {
            for (int j = 0; j < waterField[i].length; j++) {
                waterField[i][j] = ' ';
            }
        }
        for (Point point : points) {
            waterField[point.x][point.y] = '.';
        }
        Problem17 start = new Problem17(new Point(500, 0));
        Problem17 cur = start;
        int counter = -5;
        try {
            do {
                if (cur.p.y == 1900) {
                    cur = cur.last;
                }
                if (waterField[cur.p.x][cur.p.y + 1] != '.' && waterField[cur.p.x][cur.p.y + 1] != '+') {
                    cur.next = new Problem17(new Point(cur.p.x, cur.p.y + 1));
                    cur.next.last = cur;
                    cur = cur.next;
                    waterField[cur.p.x][cur.p.y] = '+';
                    counter++;
                } else {
                    if (cur.p.x == 656) {
                        cur = cur.last;
                        continue;
                    }
                    boolean border1 = false, border2 = false;
                    for (int i = 0; i < Integer.MAX_VALUE; i++) {
                        if (waterField[cur.p.x - i][cur.p.y + 1] == '.') {
                            border1 = true;
                            break;
                        } else if (waterField[cur.p.x - i][cur.p.y + 1] == '+') {
                        } else {
                            break;
                        }

                    }
                    for (int i = 0; i < Integer.MAX_VALUE; i++) {
                        if (cur.p.x + i > 656) {
                            break;
                        }
                        if (cur.p.y > 1901) {
                            break;
                        }
                        if (waterField[cur.p.x + i][cur.p.y + 1] == '.') {
                            border2 = true;
                            break;
                        } else if (waterField[cur.p.x + i][cur.p.y + 1] == '+') {
                        } else {
                            break;
                        }
                    }

                    if ((waterField[cur.p.x][cur.p.y + 1] == '.'
                            || (waterField[cur.p.x][cur.p.y + 1] == '+'
                            && (border1 && border2)))
                            && waterField[cur.p.x + 1][cur.p.y] != '.' && waterField[cur.p.x + 1][cur.p.y] != '+') {
                        cur.next = new Problem17(new Point(cur.p.x + 1, cur.p.y));
                        cur.next.last = cur;
                        cur = cur.next;
                        waterField[cur.p.x][cur.p.y] = '+';
                        counter++;
                    } else if ((waterField[cur.p.x][cur.p.y + 1] == '.'
                            || (waterField[cur.p.x][cur.p.y + 1] == '+'
                            && (border1 && border2)))
                            && waterField[cur.p.x - 1][cur.p.y] != '.' && waterField[cur.p.x - 1][cur.p.y] != '+') {
                        cur.next = new Problem17(new Point(cur.p.x - 1, cur.p.y));
                        cur.next.last = cur;
                        cur = cur.next;
                        waterField[cur.p.x][cur.p.y] = '+';
                        counter++;
                    } else {
                        cur = cur.last;
                        if (cur.last == null) {
                            break;
                        }
                    }
                }
            } while (true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        ArrayList<Point> toRemove = new ArrayList();
        for (int l = 0; l < waterField.length; l++) {
            for (int m = 0; m < waterField[l].length; m++) {
                if (waterField[l][m] == '+') {
                    Point p = new Point(l, m);
                    boolean border1 = false, border2 = false;
                    for (int i = 0; i < Integer.MAX_VALUE; i++) {
                        if (p.y >= 1901) {
                            break;
                        }
                        if (waterField[p.x - i][p.y] == '.') {
                            border1 = true;
                            break;
                        } else if (waterField[p.x - i][p.y] == '+') {
                        } else {
                            break;
                        }

                    }
                    for (int i = 0; i < Integer.MAX_VALUE; i++) {
                        if (p.x + i > 656) {
                            break;
                        }
                        if (p.y >= 1901) {
                            break;
                        }
                        if (waterField[p.x + i][p.y] == '.') {
                            border2 = true;
                            break;
                        } else if (waterField[p.x + i][p.y] == '+') {
                        } else {
                            break;
                        }
                    }
                    if (!border1 || !border2) {
                        toRemove.add(p);
                    }
                }
            }
        }
        for (Point point : toRemove) {
            waterField[point.x][point.y] = ' ';
        }
        counter = 0;
        for (int i = 0; i < waterField.length; i++) {
            for (int j = 0; j < waterField[i].length; j++) {
                if (waterField[i][j] == '+') {
                    counter++;
                }
            }
        }
        System.out.println(counter);
    }

    public static void day18_1() {
        String field[] = Inputs.input18.split("\n");
        char[][] ar = new char[field.length][];
        for (int i = 0; i < field.length; i++) {
            ar[i] = field[i].toCharArray();
        }
        for (int i = 0; i < 10; i++) {
            char[][] newAr = new char[field.length][field[0].length()];
            for (int j = 0; j < ar.length; j++) {
                for (int k = 0; k < ar[j].length; k++) {
                    switch (ar[j][k]) {
                        case '.':
                            int treeCounter = 0;
                            if (j > 0) {
                                if (k > 0) {
                                    if (ar[j - 1][k - 1] == '|') {
                                        treeCounter++;
                                    }
                                }
                                if (ar[j - 1][k] == '|') {
                                    treeCounter++;
                                }
                                if (k < ar[j].length - 1) {
                                    if (ar[j - 1][k + 1] == '|') {
                                        treeCounter++;
                                    }
                                }

                            }
                            if (j < ar.length - 1) {
                                if (k > 0) {
                                    if (ar[j + 1][k - 1] == '|') {
                                        treeCounter++;
                                    }
                                }
                                if (ar[j + 1][k] == '|') {
                                    treeCounter++;
                                }
                                if (k < ar[j].length - 1) {
                                    if (ar[j + 1][k + 1] == '|') {
                                        treeCounter++;
                                    }
                                }
                            }
                            if (k > 0) {
                                if (ar[j][k - 1] == '|') {
                                    treeCounter++;
                                }
                            }
                            if (k < ar[j].length - 1) {
                                if (ar[j][k + 1] == '|') {
                                    treeCounter++;
                                }
                            }
                            if (treeCounter >= 3) {
                                newAr[j][k] = '|';
                            } else {
                                newAr[j][k] = '.';
                            }
                            break;
                        case '|':
                            int lumberCounter = 0;
                            if (j > 0) {
                                if (k > 0) {
                                    if (ar[j - 1][k - 1] == '#') {
                                        lumberCounter++;
                                    }
                                }
                                if (ar[j - 1][k] == '#') {
                                    lumberCounter++;
                                }
                                if (k < ar[j].length - 1) {
                                    if (ar[j - 1][k + 1] == '#') {
                                        lumberCounter++;
                                    }
                                }

                            }
                            if (j < ar.length - 1) {
                                if (k > 0) {
                                    if (ar[j + 1][k - 1] == '#') {
                                        lumberCounter++;
                                    }
                                }
                                if (ar[j + 1][k] == '#') {
                                    lumberCounter++;
                                }
                                if (k < ar[j].length - 1) {
                                    if (ar[j + 1][k + 1] == '#') {
                                        lumberCounter++;
                                    }
                                }
                            }
                            if (k > 0) {
                                if (ar[j][k - 1] == '#') {
                                    lumberCounter++;
                                }
                            }
                            if (k < ar[j].length - 1) {
                                if (ar[j][k + 1] == '#') {
                                    lumberCounter++;
                                }
                            }
                            if (lumberCounter >= 3) {
                                newAr[j][k] = '#';
                            } else {
                                newAr[j][k] = '|';
                            }
                            break;
                        case '#':
                            int lumber1Counter = 0;
                            int tree1Counter = 0;
                            if (j > 0) {
                                if (k > 0) {
                                    if (ar[j - 1][k - 1] == '|') {
                                        tree1Counter++;
                                    } else if (ar[j - 1][k - 1] == '#') {
                                        lumber1Counter++;
                                    }
                                }
                                if (ar[j - 1][k] == '|') {
                                    tree1Counter++;
                                } else if (ar[j - 1][k] == '#') {
                                    lumber1Counter++;
                                }
                                if (k < ar[j].length - 1) {
                                    if (ar[j - 1][k + 1] == '|') {
                                        tree1Counter++;
                                    } else if (ar[j - 1][k + 1] == '#') {
                                        lumber1Counter++;
                                    }
                                }

                            }
                            if (j < ar.length - 1) {
                                if (k > 0) {
                                    if (ar[j + 1][k - 1] == '|') {
                                        tree1Counter++;
                                    } else if (ar[j + 1][k - 1] == '#') {
                                        lumber1Counter++;
                                    }
                                }
                                if (ar[j + 1][k] == '|') {
                                    tree1Counter++;
                                } else if (ar[j + 1][k] == '#') {
                                    lumber1Counter++;
                                }
                                if (k < ar[j].length - 1) {
                                    if (ar[j + 1][k + 1] == '|') {
                                        tree1Counter++;
                                    } else if (ar[j + 1][k + 1] == '#') {
                                        lumber1Counter++;
                                    }
                                }
                            }
                            if (k > 0) {
                                if (ar[j][k - 1] == '|') {
                                    tree1Counter++;
                                } else if (ar[j][k - 1] == '#') {
                                    lumber1Counter++;
                                }
                            }
                            if (k < ar[j].length - 1) {
                                if (ar[j][k + 1] == '|') {
                                    tree1Counter++;
                                } else if (ar[j][k + 1] == '#') {
                                    lumber1Counter++;
                                }
                            }
                            if (lumber1Counter >= 1 && tree1Counter >= 1) {
                                newAr[j][k] = '#';
                            } else {
                                newAr[j][k] = '.';
                            }
                            break;
                    }
                }
            }
            ar = newAr;
        }
        int lumber = 0, tree = 0;
        for (int i = 0; i < ar.length; i++) {
            for (int j = 0; j < ar[i].length; j++) {
                if (ar[i][j] == '#') {
                    lumber++;
                } else if (ar[i][j] == '|') {
                    tree++;
                }
            }
        }
        System.out.println(lumber * tree);
    }

    public static void day18_2() {
        String field[] = Inputs.input18.split("\n");
        char[][] ar = new char[field.length][];
        for (int i = 0; i < field.length; i++) {
            ar[i] = field[i].toCharArray();
        }
        HashMap<String, Integer> hm = new HashMap<>();
        for (int i = 0; i < 1000000000; i++) {
            char[][] newAr = new char[field.length][field[0].length()];
            for (int j = 0; j < ar.length; j++) {
                for (int k = 0; k < ar[j].length; k++) {
                    switch (ar[j][k]) {
                        case '.':
                            int treeCounter = 0;
                            if (j > 0) {
                                if (k > 0) {
                                    if (ar[j - 1][k - 1] == '|') {
                                        treeCounter++;
                                    }
                                }
                                if (ar[j - 1][k] == '|') {
                                    treeCounter++;
                                }
                                if (k < ar[j].length - 1) {
                                    if (ar[j - 1][k + 1] == '|') {
                                        treeCounter++;
                                    }
                                }

                            }
                            if (j < ar.length - 1) {
                                if (k > 0) {
                                    if (ar[j + 1][k - 1] == '|') {
                                        treeCounter++;
                                    }
                                }
                                if (ar[j + 1][k] == '|') {
                                    treeCounter++;
                                }
                                if (k < ar[j].length - 1) {
                                    if (ar[j + 1][k + 1] == '|') {
                                        treeCounter++;
                                    }
                                }
                            }
                            if (k > 0) {
                                if (ar[j][k - 1] == '|') {
                                    treeCounter++;
                                }
                            }
                            if (k < ar[j].length - 1) {
                                if (ar[j][k + 1] == '|') {
                                    treeCounter++;
                                }
                            }
                            if (treeCounter >= 3) {
                                newAr[j][k] = '|';
                            } else {
                                newAr[j][k] = '.';
                            }
                            break;
                        case '|':
                            int lumberCounter = 0;
                            if (j > 0) {
                                if (k > 0) {
                                    if (ar[j - 1][k - 1] == '#') {
                                        lumberCounter++;
                                    }
                                }
                                if (ar[j - 1][k] == '#') {
                                    lumberCounter++;
                                }
                                if (k < ar[j].length - 1) {
                                    if (ar[j - 1][k + 1] == '#') {
                                        lumberCounter++;
                                    }
                                }

                            }
                            if (j < ar.length - 1) {
                                if (k > 0) {
                                    if (ar[j + 1][k - 1] == '#') {
                                        lumberCounter++;
                                    }
                                }
                                if (ar[j + 1][k] == '#') {
                                    lumberCounter++;
                                }
                                if (k < ar[j].length - 1) {
                                    if (ar[j + 1][k + 1] == '#') {
                                        lumberCounter++;
                                    }
                                }
                            }
                            if (k > 0) {
                                if (ar[j][k - 1] == '#') {
                                    lumberCounter++;
                                }
                            }
                            if (k < ar[j].length - 1) {
                                if (ar[j][k + 1] == '#') {
                                    lumberCounter++;
                                }
                            }
                            if (lumberCounter >= 3) {
                                newAr[j][k] = '#';
                            } else {
                                newAr[j][k] = '|';
                            }
                            break;
                        case '#':
                            int lumber1Counter = 0;
                            int tree1Counter = 0;
                            if (j > 0) {
                                if (k > 0) {
                                    if (ar[j - 1][k - 1] == '|') {
                                        tree1Counter++;
                                    } else if (ar[j - 1][k - 1] == '#') {
                                        lumber1Counter++;
                                    }
                                }
                                if (ar[j - 1][k] == '|') {
                                    tree1Counter++;
                                } else if (ar[j - 1][k] == '#') {
                                    lumber1Counter++;
                                }
                                if (k < ar[j].length - 1) {
                                    if (ar[j - 1][k + 1] == '|') {
                                        tree1Counter++;
                                    } else if (ar[j - 1][k + 1] == '#') {
                                        lumber1Counter++;
                                    }
                                }

                            }
                            if (j < ar.length - 1) {
                                if (k > 0) {
                                    if (ar[j + 1][k - 1] == '|') {
                                        tree1Counter++;
                                    } else if (ar[j + 1][k - 1] == '#') {
                                        lumber1Counter++;
                                    }
                                }
                                if (ar[j + 1][k] == '|') {
                                    tree1Counter++;
                                } else if (ar[j + 1][k] == '#') {
                                    lumber1Counter++;
                                }
                                if (k < ar[j].length - 1) {
                                    if (ar[j + 1][k + 1] == '|') {
                                        tree1Counter++;
                                    } else if (ar[j + 1][k + 1] == '#') {
                                        lumber1Counter++;
                                    }
                                }
                            }
                            if (k > 0) {
                                if (ar[j][k - 1] == '|') {
                                    tree1Counter++;
                                } else if (ar[j][k - 1] == '#') {
                                    lumber1Counter++;
                                }
                            }
                            if (k < ar[j].length - 1) {
                                if (ar[j][k + 1] == '|') {
                                    tree1Counter++;
                                } else if (ar[j][k + 1] == '#') {
                                    lumber1Counter++;
                                }
                            }
                            if (lumber1Counter >= 1 && tree1Counter >= 1) {
                                newAr[j][k] = '#';
                            } else {
                                newAr[j][k] = '.';
                            }
                            break;
                    }
                }
            }
            ar = newAr;
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < ar.length; j++) {
                sb.append(ar[j]);
            }
            if (hm.get(sb.toString()) == null) {
                hm.put(sb.toString(), i);
            } else {
                int length = i - hm.get(sb.toString());
                while (i < 1000000000) {
                    i += length;
                }
                i -= length;
            }
        }
        int lumber = 0, tree = 0;
        for (int i = 0; i < ar.length; i++) {
            for (int j = 0; j < ar[i].length; j++) {
                if (ar[i][j] == '#') {
                    lumber++;
                } else if (ar[i][j] == '|') {
                    tree++;
                }
            }
        }
        System.out.println(lumber * tree);
    }

    public static void day19_1() {
        String[] input = Inputs.input19.split("\n");
        int index = 0;
        ArrayList<Register> register = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            register.add(new Register(0));
        }
        register.get(0).value = 0;
        do {
            Register r = register.get(1);
            r.value = index;
            String cmd = input[index + 1].split(" ")[0];
            int a = Integer.parseInt(input[index + 1].split(" ")[1]);
            int b = Integer.parseInt(input[index + 1].split(" ")[2]);
            int c = Integer.parseInt(input[index + 1].split(" ")[3]);
            switch (cmd) {
                case "addi":
                    BitOperators.addi(register.get(a), b, register.get(c));
                    break;
                case "addr":
                    BitOperators.addr(register.get(a), register.get(b), register.get(c));
                    break;
                case "bani":
                    BitOperators.bani(register.get(a), b, register.get(c));
                    break;
                case "banr":
                    BitOperators.banr(register.get(a), register.get(b), register.get(c));
                    break;
                case "bori":
                    BitOperators.bori(register.get(a), b, register.get(c));
                    break;
                case "borr":
                    BitOperators.borr(register.get(a), register.get(b), register.get(c));
                    break;
                case "eqir":
                    BitOperators.eqir(a, register.get(b), register.get(c));
                    break;
                case "eqri":
                    BitOperators.eqri(register.get(a), b, register.get(c));
                    break;
                case "eqrr":
                    BitOperators.eqrr(register.get(a), register.get(b), register.get(c));
                    break;
                case "gtir":
                    BitOperators.gtir(a, register.get(b), register.get(c));
                    break;
                case "gtri":
                    BitOperators.gtri(register.get(a), b, register.get(c));
                    break;
                case "gtrr":
                    BitOperators.gtrr(register.get(a), register.get(b), register.get(c));
                    break;
                case "muli":
                    BitOperators.muli(register.get(a), b, register.get(c));
                    break;
                case "mulr":
                    BitOperators.mulr(register.get(a), register.get(b), register.get(c));
                    break;
                case "seti":
                    BitOperators.seti(a, b, register.get(c));
                    break;
                case "setr":
                    BitOperators.setr(register.get(a), register.get(b), register.get(c));
                    break;
            }
            index = register.get(1).value;
            index++;
        } while (index < input.length);
        System.out.println(register.get(0));
    }

    public static void day19_2() {
        String[] input = Inputs.input19.split("\n");
        int index = 0;
        ArrayList<Register> register = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            register.add(new Register(0));
        }
        register.get(0).value = 1;
        do {
            Register r = register.get(1);
            r.value = index;
            String cmd = input[index + 1].split(" ")[0];
            int a = Integer.parseInt(input[index + 1].split(" ")[1]);
            int b = Integer.parseInt(input[index + 1].split(" ")[2]);
            int c = Integer.parseInt(input[index + 1].split(" ")[3]);
            switch (cmd) {
                case "addi":
                    BitOperators.addi(register.get(a), b, register.get(c));
                    break;
                case "addr":
                    BitOperators.addr(register.get(a), register.get(b), register.get(c));
                    break;
                case "bani":
                    BitOperators.bani(register.get(a), b, register.get(c));
                    break;
                case "banr":
                    BitOperators.banr(register.get(a), register.get(b), register.get(c));
                    break;
                case "bori":
                    BitOperators.bori(register.get(a), b, register.get(c));
                    break;
                case "borr":
                    BitOperators.borr(register.get(a), register.get(b), register.get(c));
                    break;
                case "eqir":
                    BitOperators.eqir(a, register.get(b), register.get(c));
                    break;
                case "eqri":
                    BitOperators.eqri(register.get(a), b, register.get(c));
                    break;
                case "eqrr":
                    BitOperators.eqrr(register.get(a), register.get(b), register.get(c));
                    break;
                case "gtir":
                    BitOperators.gtir(a, register.get(b), register.get(c));
                    break;
                case "gtri":
                    BitOperators.gtri(register.get(a), b, register.get(c));
                    break;
                case "gtrr":
                    BitOperators.gtrr(register.get(a), register.get(b), register.get(c));
                    break;
                case "muli":
                    BitOperators.muli(register.get(a), b, register.get(c));
                    break;
                case "mulr":
                    BitOperators.mulr(register.get(a), register.get(b), register.get(c));
                    break;
                case "seti":
                    BitOperators.seti(a, b, register.get(c));
                    break;
                case "setr":
                    BitOperators.setr(register.get(a), register.get(b), register.get(c));
                    break;
            }
            index = register.get(1).value;
            index++;
        } while (index < input.length && register.get(5).value != 10551296);
        System.out.println(IntStream.range(1, 10551296 + 1)
                .filter(x -> 10551296 % x == 0).sum());
    }

    public static void day20_12() {
        Problem20 p20 = new Problem20();
    }

    public static void day21_1() {

        String[] input = Inputs.input21.split("\n");
        int index = 0;
        ArrayList<Register> register = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            register.add(new Register(0));
        }
        register.get(0).value = 0;
        int counter = 0;
        do {
            counter++;
            Register r = register.get(4);
            r.value = index;
            String cmd = input[index + 1].split(" ")[0];
            int a = Integer.parseInt(input[index + 1].split(" ")[1]);
            int b = Integer.parseInt(input[index + 1].split(" ")[2]);
            int c = Integer.parseInt(input[index + 1].split(" ")[3]);
            switch (cmd) {
                case "addi":
                    BitOperators.addi(register.get(a), b, register.get(c));
                    break;
                case "addr":
                    BitOperators.addr(register.get(a), register.get(b), register.get(c));
                    break;
                case "bani":
                    BitOperators.bani(register.get(a), b, register.get(c));
                    break;
                case "banr":
                    BitOperators.banr(register.get(a), register.get(b), register.get(c));
                    break;
                case "bori":
                    BitOperators.bori(register.get(a), b, register.get(c));
                    break;
                case "borr":
                    BitOperators.borr(register.get(a), register.get(b), register.get(c));
                    break;
                case "eqir":
                    BitOperators.eqir(a, register.get(b), register.get(c));
                    break;
                case "eqri":
                    BitOperators.eqri(register.get(a), b, register.get(c));
                    break;
                case "eqrr":
                    System.out.println(register.get(3));
                    if (register.get(3).value == 16311888) {
                        return;
                    }
                    BitOperators.eqrr(register.get(a), register.get(b), register.get(c));
                    break;
                case "gtir":
                    BitOperators.gtir(a, register.get(b), register.get(c));
                    break;
                case "gtri":
                    BitOperators.gtri(register.get(a), b, register.get(c));
                    break;
                case "gtrr":
                    BitOperators.gtrr(register.get(a), register.get(b), register.get(c));
                    break;
                case "muli":
                    BitOperators.muli(register.get(a), b, register.get(c));
                    break;
                case "mulr":
                    BitOperators.mulr(register.get(a), register.get(b), register.get(c));
                    break;
                case "seti":
                    BitOperators.seti(a, b, register.get(c));
                    break;
                case "setr":
                    BitOperators.setr(register.get(a), register.get(b), register.get(c));
                    break;
            }
            index = register.get(4).value;
            index++;
        } while (index + 1 < input.length);
        System.out.println(counter);
        //16311888
    }

    public static void day21_2() {
        long r1 = 0;
        long r3 = 0;
        long prev = 0;
        LinkedList<Long> l = new LinkedList();
        do {
            r1 = r3 | 65536;
            r3 = 10736359;
            do {
                r3 += r1 & 255;
                r3 &= 16777215;
                r3 *= 65899;
                r3 &= 16777215;
                if (r1 < 256) {
                    break;
                }
                r1 >>= 8;
            } while (true);
            if (l.contains(r3) && r3 != l.getLast()) {
                System.out.println(prev);
                break;
            }
            prev = r3;
            l.add(prev);
        } while (true);
    }

    public static void day22_12() {
        String[] input = Inputs.input22.split("\n");
        int depth = Integer.parseInt(input[0].split(" ")[1]);
        int targetX = Integer.parseInt(input[1].split(": ")[1].split(",")[0]);
        int targetY = Integer.parseInt(input[1].split(": ")[1].split(",")[1]);
        GeoCoords[][] coords = new GeoCoords[targetX + 1][targetY + 1];
        for (int i = 0; i < coords.length; i++) {
            for (int j = 0; j < coords[i].length; j++) {
                if ((i == 0 && j == 0) || (targetX == i && targetY == j)) {
                    coords[i][j] = new GeoCoords(i, j, 0, depth % 20183, (depth % 20183) % 3 == 0 ? GeoLocationType.ROCKY : (depth % 20183) % 3 == 1 ? GeoLocationType.WET : GeoLocationType.NARROW);
                } else {
                    int geoIndex = 0;
                    int erosionLevel = 0;
                    GeoLocationType type = null;
                    if (i == 0) {
                        geoIndex = j * 48271;
                        erosionLevel = (geoIndex + depth) % 20183;
                        switch (erosionLevel % 3) {
                            case 0:
                                type = GeoLocationType.ROCKY;
                                break;
                            case 1:
                                type = GeoLocationType.WET;
                                break;
                            case 2:
                                type = GeoLocationType.NARROW;
                                break;
                        }
                        coords[i][j] = new GeoCoords(i, j, geoIndex, erosionLevel, type);
                    } else if (j == 0) {
                        geoIndex = i * 16807;
                        erosionLevel = (geoIndex + depth) % 20183;
                        switch (erosionLevel % 3) {
                            case 0:
                                type = GeoLocationType.ROCKY;
                                break;
                            case 1:
                                type = GeoLocationType.WET;
                                break;
                            case 2:
                                type = GeoLocationType.NARROW;
                                break;
                        }
                        coords[i][j] = new GeoCoords(i, j, geoIndex, erosionLevel, type);
                    } else {
                        geoIndex = coords[i - 1][j].erosionLevel * coords[i][j - 1].erosionLevel;
                        erosionLevel = (geoIndex + depth) % 20183;
                        switch (erosionLevel % 3) {
                            case 0:
                                type = GeoLocationType.ROCKY;
                                break;
                            case 1:
                                type = GeoLocationType.WET;
                                break;
                            case 2:
                                type = GeoLocationType.NARROW;
                                break;
                        }
                        coords[i][j] = new GeoCoords(i, j, geoIndex, erosionLevel, type);

                    }
                }
            }
        }
        int risk = 0;
        for (int i = 0; i < coords[0].length; i++) {
            for (int j = 0; j < coords.length; j++) {
                switch (coords[j][i].type) {
                    case ROCKY:
                        break;
                    case WET:
                        risk++;
                        break;
                    case NARROW:
                        risk += 2;
                        break;
                }
            }
        }
        System.out.println(risk);
        ArrayList<MyCoords> dirs = new ArrayList<>();
        boolean start[][] = new boolean[coords.length][coords[0].length];

        start[0][0] = true;
        dirs.add(new MyCoords(new Point(0, 0), start, 0, ToolType.TORCH, -1));
        loop:
        do {
            for (MyCoords p : dirs) {
                if (p.point.x == targetX && p.point.y == targetY && p.tool == ToolType.TORCH) {
                    System.out.println(p.cost);
                    break loop;
                }
            }
            dirs.sort((o1, o2) -> (o1.cost - o2.cost));
            MyCoords p = dirs.remove(0);
            p.cost++;
            if (p.counter == 7) {
                p.counter = -1;
                dirs.add(p);
            }
            if (p.counter != -1) {
                p.counter++;
                dirs.add(p);

            }
            if (p.tool != ToolType.CLIMBING && coords[p.point.x][p.point.y].type != GeoLocationType.ROCKY) {
                dirs.add(new MyCoords(new Point(p.point.x, p.point.y), p.getStep(), p.cost, ToolType.CLIMBING, 0));
            }
            if (p.tool != ToolType.TORCH && coords[p.point.x][p.point.y].type != GeoLocationType.ROCKY) {
                dirs.add(new MyCoords(new Point(p.point.x, p.point.y), p.getStep(), p.cost, ToolType.TORCH, 0));
            }
            if (p.tool != ToolType.CLIMBING && coords[p.point.x][p.point.y].type != GeoLocationType.WET) {
                dirs.add(new MyCoords(new Point(p.point.x, p.point.y), p.getStep(), p.cost, ToolType.CLIMBING, 0));
            }
            if (p.tool != ToolType.NEITHER && coords[p.point.x][p.point.y].type != GeoLocationType.WET) {
                dirs.add(new MyCoords(new Point(p.point.x, p.point.y), p.getStep(), p.cost, ToolType.NEITHER, 0));
            }
            if (p.tool != ToolType.TORCH && coords[p.point.x][p.point.y].type != GeoLocationType.NARROW) {
                dirs.add(new MyCoords(new Point(p.point.x, p.point.y), p.getStep(), p.cost, ToolType.TORCH, 0));
            }
            if (p.tool != ToolType.NEITHER && coords[p.point.x][p.point.y].type != GeoLocationType.NARROW) {
                dirs.add(new MyCoords(new Point(p.point.x, p.point.y), p.getStep(), p.cost, ToolType.NEITHER, 0));
            }
            if (p.point.x != 0 && !p.step[p.point.x - 1][p.point.y]) {
                switch (coords[p.point.x - 1][p.point.y].type) {
                    case ROCKY:
                        if (p.tool != ToolType.NEITHER) {
                            boolean[][] cur = p.getStep();
                            cur[p.point.x - 1][p.point.y] = true;
                            dirs.add(new MyCoords(new Point(p.point.x - 1, p.point.y), cur, p.cost, p.tool, -1));
                        }
                        break;
                    case WET:
                        if (p.tool != ToolType.TORCH) {
                            boolean[][] cur = p.getStep();
                            cur[p.point.x - 1][p.point.y] = true;
                            dirs.add(new MyCoords(new Point(p.point.x - 1, p.point.y), cur, p.cost, p.tool, -1));
                        }
                        break;
                    case NARROW:
                        if (p.tool != ToolType.CLIMBING) {
                            boolean[][] cur = p.getStep();
                            cur[p.point.x - 1][p.point.y] = true;
                            dirs.add(new MyCoords(new Point(p.point.x - 1, p.point.y), cur, p.cost, p.tool, -1));
                        }
                        break;
                }
            }
            if (p.point.y != 0 && !p.step[p.point.x][p.point.y - 1]) {
                switch (coords[p.point.x][p.point.y - 1].type) {
                    case ROCKY:
                        if (p.tool != ToolType.NEITHER) {
                            boolean[][] cur = p.getStep();
                            cur[p.point.x][p.point.y - 1] = true;
                            dirs.add(new MyCoords(new Point(p.point.x, p.point.y - 1), cur, p.cost, p.tool, -1));
                        }
                        break;
                    case WET:
                        if (p.tool != ToolType.TORCH) {
                            boolean[][] cur = p.getStep();
                            cur[p.point.x][p.point.y - 1] = true;
                            dirs.add(new MyCoords(new Point(p.point.x, p.point.y - 1), cur, p.cost, p.tool, -1));
                        }
                        break;
                    case NARROW:
                        if (p.tool != ToolType.CLIMBING) {
                            boolean[][] cur = p.getStep();
                            cur[p.point.x][p.point.y - 1] = true;
                            dirs.add(new MyCoords(new Point(p.point.x, p.point.y - 1), cur, p.cost, p.tool, -1));
                        }
                        break;
                }
                if (p.point.x != p.step.length && !p.step[p.point.x + 1][p.point.y]) {
                    switch (coords[p.point.x + 1][p.point.y].type) {
                        case ROCKY:
                            if (p.tool != ToolType.NEITHER) {
                                boolean[][] cur = p.getStep();
                                cur[p.point.x + 1][p.point.y] = true;
                                dirs.add(new MyCoords(new Point(p.point.x + 1, p.point.y), cur, p.cost, p.tool, -1));
                            }
                            break;
                        case WET:
                            if (p.tool != ToolType.TORCH) {
                                boolean[][] cur = p.getStep();
                                cur[p.point.x + 1][p.point.y] = true;
                                dirs.add(new MyCoords(new Point(p.point.x + 1, p.point.y), cur, p.cost, p.tool, -1));
                            }
                            break;
                        case NARROW:
                            if (p.tool != ToolType.CLIMBING) {
                                boolean[][] cur = p.getStep();
                                cur[p.point.x + 1][p.point.y] = true;
                                dirs.add(new MyCoords(new Point(p.point.x + 1, p.point.y), cur, p.cost, p.tool, -1));
                            }
                            break;
                    }
                }
                if (p.point.y != p.step[0].length && !p.step[p.point.x][p.point.y + 1]) {
                    switch (coords[p.point.x][p.point.y + 1].type) {
                        case ROCKY:
                            if (p.tool != ToolType.NEITHER) {
                                boolean[][] cur = p.getStep();
                                cur[p.point.x][p.point.y + 1] = true;
                                dirs.add(new MyCoords(new Point(p.point.x, p.point.y + 1), cur, p.cost, p.tool, -1));
                            }
                            break;
                        case WET:
                            if (p.tool != ToolType.TORCH) {
                                boolean[][] cur = p.getStep();
                                cur[p.point.x][p.point.y + 1] = true;
                                dirs.add(new MyCoords(new Point(p.point.x, p.point.y + 1), cur, p.cost, p.tool, -1));
                            }
                            break;
                        case NARROW:
                            if (p.tool != ToolType.CLIMBING) {
                                boolean[][] cur = p.getStep();
                                cur[p.point.x][p.point.y + 1] = true;
                                dirs.add(new MyCoords(new Point(p.point.x, p.point.y + 1), cur, p.cost, p.tool, -1));
                            }
                            break;
                    }
                }
            }
        } while (true);
        //output: 1051

    }

    public static void day23_12() {
        String[] input = Inputs.input23.split("\n");
        LinkedList<Nanobot> nanobots = new LinkedList<>();
        for (int i = 0; i < input.length; i++) {
            int x = Integer.parseInt(input[i].split(", ")[0].replace("pos=<", " ").replace(">", " ").trim().split(",")[0]);
            int y = Integer.parseInt(input[i].split(", ")[0].replace("pos=<", " ").replace(">", " ").trim().split(",")[1]);
            int z = Integer.parseInt(input[i].split(", ")[0].replace("pos=<", " ").replace(">", " ").trim().split(",")[2]);
            int range = Integer.parseInt(input[i].split(", ")[1].split("=")[1]);
            nanobots.add(new Nanobot(x, y, z, range));
        }
        nanobots.sort(new Comparator<Nanobot>() {
            @Override
            public int compare(Nanobot o1, Nanobot o2) {
                return o1.range - o2.range;
            }
        });
        int range = nanobots.getLast().range;
        Nanobot start = nanobots.getLast();
        int count = 0;
        for (Nanobot nanobot : nanobots) {
            if (nanobot.getDistance(start) < range) {
                count++;
            }
        }
        System.out.println(count);

        TreeMap<Integer, Integer> ranges = new TreeMap<>();
        for (Nanobot n : nanobots) {
            int distFromZero = n.getDistance(new Nanobot(0, 0, 0, 0));
            ranges.put(Math.max(0, distFromZero - (int) n.range), 1);
            ranges.put(distFromZero + (int) n.range, -1);
        }
        count = 0;
        int result = 0;
        int maxCount = 0;
        for (Map.Entry<Integer, Integer> each : ranges.entrySet()) {
            count += each.getValue();
            if (count > maxCount) {
                result = each.getKey();
                maxCount = count;
            }
        }
        System.out.println(result);
    }

    public static void day24_1() {
        LinkedList<Units> immuneU = new LinkedList<>();
        LinkedList<Units> infectionU = new LinkedList<>();
        String inputs[] = Inputs.input24.split("\n");
        int numUnits;
        int hitPoints;
        int damage;
        int initiative;
        LinkedList<DiseaseType> weak = new LinkedList<>();
        LinkedList<DiseaseType> immune = new LinkedList<>();
        DiseaseType attack;
        for (int i = 1; i <= 10; i++) {
            numUnits = Integer.parseInt(inputs[i].split(" units")[0]);
            hitPoints = Integer.parseInt(inputs[i].split(" hit points")[0].split("with ")[1]);
            if (inputs[i].contains("(")) {
                String text = inputs[i].split("\\(")[1].split("\\)")[0];
                if (text.contains(";")) {
                    if (text.split(";")[0].contains("weak to")) {
                        String ar[] = text.split(";")[0].split("weak to ")[1].split(", ");
                        for (int j = 0; j < ar.length; j++) {
                            weak.add(DiseaseType.valueOf(ar[j].toUpperCase()));
                        }
                        String ar2[] = text.split(";")[1].split("immune to ")[1].split(", ");
                        for (int j = 0; j < ar2.length; j++) {
                            immune.add(DiseaseType.valueOf(ar2[j].toUpperCase()));
                        }
                    } else {
                        String ar[] = text.split(";")[1].split("weak to ")[1].split(", ");
                        for (int j = 0; j < ar.length; j++) {
                            weak.add(DiseaseType.valueOf(ar[j].toUpperCase()));
                        }
                        String ar2[] = text.split(";")[0].split("immune to ")[1].split(", ");
                        for (int j = 0; j < ar2.length; j++) {
                            immune.add(DiseaseType.valueOf(ar2[j].toUpperCase()));
                        }
                    }
                } else {
                    if (text.contains("weak to")) {
                        String ar[] = text.split("weak to ")[1].split(", ");
                        for (int j = 0; j < ar.length; j++) {
                            weak.add(DiseaseType.valueOf(ar[j].toUpperCase()));
                        }
                    } else {
                        String ar[] = text.split("immune to ")[1].split(", ");
                        for (int j = 0; j < ar.length; j++) {
                            immune.add(DiseaseType.valueOf(ar[j].toUpperCase()));
                        }
                    }
                }
            }
            damage = Integer.parseInt(inputs[i].split("does ")[1].split(" damage")[0].split(" ")[0]);
            attack = DiseaseType.valueOf(inputs[i].split("does ")[1].split(" damage")[0].split(" ")[1].toUpperCase());
            initiative = Integer.parseInt(inputs[i].split("initiative ")[1]);
            immuneU.add(new Units(initiative, numUnits, hitPoints, damage, weak, immune, attack));
            weak = new LinkedList<>();
            immune = new LinkedList<>();
        }

        for (int i = 13; i <= 22; i++) {
            numUnits = Integer.parseInt(inputs[i].split(" units")[0]);
            hitPoints = Integer.parseInt(inputs[i].split(" hit points")[0].split("with ")[1]);
            if (inputs[i].contains("(")) {
                String text = inputs[i].split("\\(")[1].split("\\)")[0];
                if (text.contains(";")) {
                    if (text.split(";")[0].contains("weak to")) {
                        String ar[] = text.split(";")[0].split("weak to ")[1].split(", ");
                        for (int j = 0; j < ar.length; j++) {
                            weak.add(DiseaseType.valueOf(ar[j].toUpperCase()));
                        }
                        String ar2[] = text.split(";")[1].split("immune to ")[1].split(", ");
                        for (int j = 0; j < ar2.length; j++) {
                            immune.add(DiseaseType.valueOf(ar2[j].toUpperCase()));
                        }
                    } else {
                        String ar[] = text.split(";")[1].split("weak to ")[1].split(", ");
                        for (int j = 0; j < ar.length; j++) {
                            weak.add(DiseaseType.valueOf(ar[j].toUpperCase()));
                        }
                        String ar2[] = text.split(";")[0].split("immune to ")[1].split(", ");
                        for (int j = 0; j < ar2.length; j++) {
                            immune.add(DiseaseType.valueOf(ar2[j].toUpperCase()));
                        }
                    }
                } else {
                    if (text.contains("weak to")) {
                        String ar[] = text.split("weak to ")[1].split(", ");
                        for (int j = 0; j < ar.length; j++) {
                            weak.add(DiseaseType.valueOf(ar[j].toUpperCase()));
                        }
                    } else {
                        String ar[] = text.split("immune to ")[1].split(", ");
                        for (int j = 0; j < ar.length; j++) {
                            immune.add(DiseaseType.valueOf(ar[j].toUpperCase()));
                        }
                    }
                }
            }
            damage = Integer.parseInt(inputs[i].split("does ")[1].split(" damage")[0].split(" ")[0]);
            attack = DiseaseType.valueOf(inputs[i].split("does ")[1].split(" damage")[0].split(" ")[1].toUpperCase());
            initiative = Integer.parseInt(inputs[i].split("initiative ")[1]);
            infectionU.add(new Units(initiative, numUnits, hitPoints, damage, weak, immune, attack));
            weak = new LinkedList<>();
            immune = new LinkedList<>();
        }

        do {
            immuneU.sort((Units o1, Units o2) -> o1.damage * o1.numUnits - o2.hitPoints * o2.numUnits != 0 ? o1.damage * o1.numUnits - o2.hitPoints * o2.numUnits : o1.intiative - o2.intiative);
            infectionU.sort((Units o1, Units o2) -> o1.damage * o1.numUnits - o2.hitPoints * o2.numUnits != 0 ? o1.damage * o1.numUnits - o2.hitPoints * o2.numUnits : o1.intiative - o2.intiative);
            for (int i = 0; i < immuneU.size(); i++) {
                if (immuneU.get(i).numUnits <= 0) {
                    immuneU.remove(i);
                }
            }
            for (int i = 0; i < infectionU.size(); i++) {
                if (infectionU.get(i).numUnits <= 0) {
                    infectionU.remove(i);
                }
            }
            if (immuneU.isEmpty() || infectionU.isEmpty()) {
                break;
            }
            LinkedList<Attack> attacks = new LinkedList<>();
            for (Units units : immuneU) {
                LinkedList<Units> weakEnemies = new LinkedList<>();
                LinkedList<Units> immuneEnemies = new LinkedList<>();
                LinkedList<Units> otherEnemies = new LinkedList<>();
                for (Units units2 : infectionU) {
                    if (units2.weak.contains(units.attackType)) {
                        weakEnemies.add(units2);
                    } else if (units2.immune.contains(units.attackType)) {
                        immuneEnemies.add(units2);
                    } else {
                        otherEnemies.add(units2);
                    }
                }
                if (!weakEnemies.isEmpty()) {
                    Units enemy = null;
                    for (Units weakEnemy : weakEnemies) {
                        if (enemy == null) {
                            enemy = weakEnemy;
                        } else if (weakEnemy.damage * weakEnemy.numUnits > enemy.damage * enemy.numUnits) {
                            enemy = weakEnemy;
                        } else if (weakEnemy.damage * weakEnemy.numUnits == enemy.damage * enemy.numUnits) {
                            if (weakEnemy.intiative > enemy.intiative) {
                                enemy = weakEnemy;
                            }
                        }
                    }
                    attacks.add(new Attack(units, enemy, 2));
                } else if (!otherEnemies.isEmpty()) {
                    Units enemy = null;
                    for (Units weakEnemy : otherEnemies) {
                        if (enemy == null) {
                            enemy = weakEnemy;
                        } else if (weakEnemy.damage * weakEnemy.numUnits > enemy.damage * enemy.numUnits) {
                            enemy = weakEnemy;
                        } else if (weakEnemy.damage * weakEnemy.numUnits == enemy.damage * enemy.numUnits) {
                            if (weakEnemy.intiative > enemy.intiative) {
                                enemy = weakEnemy;
                            }
                        }
                    }
                    attacks.add(new Attack(units, enemy, 1));
                }
            }
            for (Units units : infectionU) {
                LinkedList<Units> weakEnemies = new LinkedList<>();
                LinkedList<Units> immuneEnemies = new LinkedList<>();
                LinkedList<Units> otherEnemies = new LinkedList<>();
                for (Units units2 : immuneU) {
                    if (units2.weak.contains(units.attackType)) {
                        weakEnemies.add(units2);
                    } else if (units2.immune.contains(units.attackType)) {
                        immuneEnemies.add(units2);
                    } else {
                        otherEnemies.add(units2);
                    }
                }
                if (!weakEnemies.isEmpty()) {
                    Units enemy = null;
                    for (Units weakEnemy : weakEnemies) {
                        if (enemy == null) {
                            enemy = weakEnemy;
                        } else if (weakEnemy.damage * weakEnemy.numUnits > enemy.damage * enemy.numUnits) {
                            enemy = weakEnemy;
                        } else if (weakEnemy.damage * weakEnemy.numUnits == enemy.damage * enemy.numUnits) {
                            if (weakEnemy.intiative > enemy.intiative) {
                                enemy = weakEnemy;
                            }
                        }
                    }
                    attacks.add(new Attack(units, enemy, 2));
                } else if (!otherEnemies.isEmpty()) {
                    Units enemy = null;
                    for (Units weakEnemy : otherEnemies) {
                        if (enemy == null) {
                            enemy = weakEnemy;
                        } else if (weakEnemy.damage * weakEnemy.numUnits > enemy.damage * enemy.numUnits) {
                            enemy = weakEnemy;
                        } else if (weakEnemy.damage * weakEnemy.numUnits == enemy.damage * enemy.numUnits) {
                            if (weakEnemy.intiative > enemy.intiative) {
                                enemy = weakEnemy;
                            }
                        }
                    }
                    attacks.add(new Attack(units, enemy, 1));
                }
            }
            attacks.sort((Attack o1, Attack o2) -> o2.attacker.intiative - o1.attacker.intiative);
            for (Attack attack1 : attacks) {
                if (attack1.attacker.numUnits >= 0) {
                    attack1.defender.numUnits -= Math.floor((attack1.attacker.numUnits * attack1.attacker.damage * attack1.mult) / attack1.defender.hitPoints);
                }
            }
        } while (true);
        int count = -71;
        for (Units units : immuneU) {
            count += units.numUnits;
        }
        for (Units units : infectionU) {
            count += units.numUnits;
        }
        System.out.println(count);
    }
    
    public static void day24_2() {
        LinkedList<Units> immuneU = new LinkedList<>();
        LinkedList<Units> infectionU = new LinkedList<>();
        String inputs[] = Inputs.input24.split("\n");
        int numUnits;
        int hitPoints;
        int damage;
        int initiative;
        int boost=88;
        LinkedList<DiseaseType> weak = new LinkedList<>();
        LinkedList<DiseaseType> immune = new LinkedList<>();
        DiseaseType attack;
        for (int i = 1; i <= 10; i++) {
            numUnits = Integer.parseInt(inputs[i].split(" units")[0]);
            hitPoints = Integer.parseInt(inputs[i].split(" hit points")[0].split("with ")[1]);
            if (inputs[i].contains("(")) {
                String text = inputs[i].split("\\(")[1].split("\\)")[0];
                if (text.contains(";")) {
                    if (text.split(";")[0].contains("weak to")) {
                        String ar[] = text.split(";")[0].split("weak to ")[1].split(", ");
                        for (int j = 0; j < ar.length; j++) {
                            weak.add(DiseaseType.valueOf(ar[j].toUpperCase()));
                        }
                        String ar2[] = text.split(";")[1].split("immune to ")[1].split(", ");
                        for (int j = 0; j < ar2.length; j++) {
                            immune.add(DiseaseType.valueOf(ar2[j].toUpperCase()));
                        }
                    } else {
                        String ar[] = text.split(";")[1].split("weak to ")[1].split(", ");
                        for (int j = 0; j < ar.length; j++) {
                            weak.add(DiseaseType.valueOf(ar[j].toUpperCase()));
                        }
                        String ar2[] = text.split(";")[0].split("immune to ")[1].split(", ");
                        for (int j = 0; j < ar2.length; j++) {
                            immune.add(DiseaseType.valueOf(ar2[j].toUpperCase()));
                        }
                    }
                } else {
                    if (text.contains("weak to")) {
                        String ar[] = text.split("weak to ")[1].split(", ");
                        for (int j = 0; j < ar.length; j++) {
                            weak.add(DiseaseType.valueOf(ar[j].toUpperCase()));
                        }
                    } else {
                        String ar[] = text.split("immune to ")[1].split(", ");
                        for (int j = 0; j < ar.length; j++) {
                            immune.add(DiseaseType.valueOf(ar[j].toUpperCase()));
                        }
                    }
                }
            }
            damage = Integer.parseInt(inputs[i].split("does ")[1].split(" damage")[0].split(" ")[0]+88);
            attack = DiseaseType.valueOf(inputs[i].split("does ")[1].split(" damage")[0].split(" ")[1].toUpperCase());
            initiative = Integer.parseInt(inputs[i].split("initiative ")[1]);
            immuneU.add(new Units(initiative, numUnits, hitPoints, damage, weak, immune, attack));
            weak = new LinkedList<>();
            immune = new LinkedList<>();
        }

        for (int i = 13; i <= 22; i++) {
            numUnits = Integer.parseInt(inputs[i].split(" units")[0]);
            hitPoints = Integer.parseInt(inputs[i].split(" hit points")[0].split("with ")[1]);
            if (inputs[i].contains("(")) {
                String text = inputs[i].split("\\(")[1].split("\\)")[0];
                if (text.contains(";")) {
                    if (text.split(";")[0].contains("weak to")) {
                        String ar[] = text.split(";")[0].split("weak to ")[1].split(", ");
                        for (int j = 0; j < ar.length; j++) {
                            weak.add(DiseaseType.valueOf(ar[j].toUpperCase()));
                        }
                        String ar2[] = text.split(";")[1].split("immune to ")[1].split(", ");
                        for (int j = 0; j < ar2.length; j++) {
                            immune.add(DiseaseType.valueOf(ar2[j].toUpperCase()));
                        }
                    } else {
                        String ar[] = text.split(";")[1].split("weak to ")[1].split(", ");
                        for (int j = 0; j < ar.length; j++) {
                            weak.add(DiseaseType.valueOf(ar[j].toUpperCase()));
                        }
                        String ar2[] = text.split(";")[0].split("immune to ")[1].split(", ");
                        for (int j = 0; j < ar2.length; j++) {
                            immune.add(DiseaseType.valueOf(ar2[j].toUpperCase()));
                        }
                    }
                } else {
                    if (text.contains("weak to")) {
                        String ar[] = text.split("weak to ")[1].split(", ");
                        for (int j = 0; j < ar.length; j++) {
                            weak.add(DiseaseType.valueOf(ar[j].toUpperCase()));
                        }
                    } else {
                        String ar[] = text.split("immune to ")[1].split(", ");
                        for (int j = 0; j < ar.length; j++) {
                            immune.add(DiseaseType.valueOf(ar[j].toUpperCase()));
                        }
                    }
                }
            }
            damage = Integer.parseInt(inputs[i].split("does ")[1].split(" damage")[0].split(" ")[0]);
            attack = DiseaseType.valueOf(inputs[i].split("does ")[1].split(" damage")[0].split(" ")[1].toUpperCase());
            initiative = Integer.parseInt(inputs[i].split("initiative ")[1]);
            infectionU.add(new Units(initiative, numUnits, hitPoints, damage, weak, immune, attack));
            weak = new LinkedList<>();
            immune = new LinkedList<>();
        }
        
        do {
            immuneU.sort((Units o1, Units o2) -> o1.damage * o1.numUnits - o2.hitPoints * o2.numUnits != 0 ? o1.damage * o1.numUnits - o2.hitPoints * o2.numUnits : o1.intiative - o2.intiative);
            infectionU.sort((Units o1, Units o2) -> o1.damage * o1.numUnits - o2.hitPoints * o2.numUnits != 0 ? o1.damage * o1.numUnits - o2.hitPoints * o2.numUnits : o1.intiative - o2.intiative);
            for (int i = 0; i < immuneU.size(); i++) {
                if (immuneU.get(i).numUnits <= 0) {
                    immuneU.remove(i);
                }
            }
            for (int i = 0; i < infectionU.size(); i++) {
                if (infectionU.get(i).numUnits <= 0) {
                    infectionU.remove(i);
                }
            }
            if (immuneU.isEmpty() || infectionU.isEmpty()) {
                break;
            }
            LinkedList<Attack> attacks = new LinkedList<>();
            for (Units units : immuneU) {
                LinkedList<Units> weakEnemies = new LinkedList<>();
                LinkedList<Units> immuneEnemies = new LinkedList<>();
                LinkedList<Units> otherEnemies = new LinkedList<>();
                for (Units units2 : infectionU) {
                    if (units2.weak.contains(units.attackType)) {
                        weakEnemies.add(units2);
                    } else if (units2.immune.contains(units.attackType)) {
                        immuneEnemies.add(units2);
                    } else {
                        otherEnemies.add(units2);
                    }
                }
                if (!weakEnemies.isEmpty()) {
                    Units enemy = null;
                    for (Units weakEnemy : weakEnemies) {
                        if (enemy == null) {
                            enemy = weakEnemy;
                        } else if (weakEnemy.damage * weakEnemy.numUnits > enemy.damage * enemy.numUnits) {
                            enemy = weakEnemy;
                        } else if (weakEnemy.damage * weakEnemy.numUnits == enemy.damage * enemy.numUnits) {
                            if (weakEnemy.intiative > enemy.intiative) {
                                enemy = weakEnemy;
                            }
                        }
                    }
                    attacks.add(new Attack(units, enemy, 2));
                } else if (!otherEnemies.isEmpty()) {
                    Units enemy = null;
                    for (Units weakEnemy : otherEnemies) {
                        if (enemy == null) {
                            enemy = weakEnemy;
                        } else if (weakEnemy.damage * weakEnemy.numUnits > enemy.damage * enemy.numUnits) {
                            enemy = weakEnemy;
                        } else if (weakEnemy.damage * weakEnemy.numUnits == enemy.damage * enemy.numUnits) {
                            if (weakEnemy.intiative > enemy.intiative) {
                                enemy = weakEnemy;
                            }
                        }
                    }
                    attacks.add(new Attack(units, enemy, 1));
                }
            }
            for (Units units : infectionU) {
                LinkedList<Units> weakEnemies = new LinkedList<>();
                LinkedList<Units> immuneEnemies = new LinkedList<>();
                LinkedList<Units> otherEnemies = new LinkedList<>();
                for (Units units2 : immuneU) {
                    if (units2.weak.contains(units.attackType)) {
                        weakEnemies.add(units2);
                    } else if (units2.immune.contains(units.attackType)) {
                        immuneEnemies.add(units2);
                    } else {
                        otherEnemies.add(units2);
                    }
                }
                if (!weakEnemies.isEmpty()) {
                    Units enemy = null;
                    for (Units weakEnemy : weakEnemies) {
                        if (enemy == null) {
                            enemy = weakEnemy;
                        } else if (weakEnemy.damage * weakEnemy.numUnits > enemy.damage * enemy.numUnits) {
                            enemy = weakEnemy;
                        } else if (weakEnemy.damage * weakEnemy.numUnits == enemy.damage * enemy.numUnits) {
                            if (weakEnemy.intiative > enemy.intiative) {
                                enemy = weakEnemy;
                            }
                        }
                    }
                    attacks.add(new Attack(units, enemy, 2));
                } else if (!otherEnemies.isEmpty()) {
                    Units enemy = null;
                    for (Units weakEnemy : otherEnemies) {
                        if (enemy == null) {
                            enemy = weakEnemy;
                        } else if (weakEnemy.damage * weakEnemy.numUnits > enemy.damage * enemy.numUnits) {
                            enemy = weakEnemy;
                        } else if (weakEnemy.damage * weakEnemy.numUnits == enemy.damage * enemy.numUnits) {
                            if (weakEnemy.intiative > enemy.intiative) {
                                enemy = weakEnemy;
                            }
                        }
                    }
                    attacks.add(new Attack(units, enemy, 1));
                }
            }
            attacks.sort((Attack o1, Attack o2) -> o2.attacker.intiative - o1.attacker.intiative);
            for (Attack attack1 : attacks) {
                if (attack1.attacker.numUnits >= 0) {
                    attack1.defender.numUnits -= Math.floor((attack1.attacker.numUnits * attack1.attacker.damage * attack1.mult) / attack1.defender.hitPoints);
                }
            }
        } while (true);
        int count = -14352;
        for (Units units : immuneU) {
            count += units.numUnits;
        }
        for (Units units : infectionU) {
            count += units.numUnits;
        }
        System.out.println(immuneU);
        System.out.println(count);
    }
    
    public static void day25_1(){
        String ar[]=Inputs.input25.split("\n");
        LinkedList<D4Point> points=new LinkedList<>();
        for (String string : ar) {
            points.add(new D4Point(Integer.parseInt(string.split(",")[0]),Integer.parseInt(string.split(",")[1]),Integer.parseInt(string.split(",")[2]),Integer.parseInt(string.split(",")[3])));
        }
        int count=0;
        LinkedList<D4Point> currentConstellation=new LinkedList<>();
        for (int i = 0; i < points.size(); i++) {
            currentConstellation=new LinkedList<>();
            currentConstellation.add(points.get(i));
            count++;
            for (int j = 0; j < currentConstellation.size(); j++) {
                for (int k = 0; k < points.size(); k++) {
                    if(!currentConstellation.contains(points.get(k))){
                        if(currentConstellation.get(j).distance(points.get(k))<=3){
                            currentConstellation.add(points.remove(k));
                            k--;
                        }
                    }
                }
            }
        }
        System.out.println(count);
    }

    public static void main(String[] args) {
        /*day01_1();
        day01_2();
        day02_1();
        day02_2();
        day03_12();
        day04_12();
        day05_1();
        day05_2();
        day06_12();
        day07_1();
        day07_2();
        day08_1();
        day08_2();
        day09_12();
        day10_12();
        day11_12();
        day12_1();
        day12_2();
        day13_1();
        day13_2();
        day14_1();
        day14_2();
        day15_12();
        day16_12();
        day17_12();
        day18_1();
        day18_2();
        day19_1();
        day19_2();
        day20_12();
        day21_1();
        day21_2();
        day22_12();
        day23_12();
        day24_1();
        day24_2();*/
        day25_1();
    }
}
