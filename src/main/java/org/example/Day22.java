package org.example;

import org.example.Utils.Point;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

record Cube(int x, int y, int z) {
}

record Brick(Cube start, Cube end, String name) {

    int lowerX() {
        return Math.min(start().x(), end().x());
    }

    int higherX() {
        return Math.max(start().x(), end().x());
    }

    int lowerY() {
        return Math.min(start().y(), end().y());
    }

    int higherY() {
        return Math.max(start().y(), end().y());
    }

    int lowerZ() {
        return Math.min(start().z(), end().z());
    }

    int higherZ() {
        return Math.max(start().z(), end().z());
    }
}

public class Day22 {
    static Map<Point, Integer> topsMap = new HashMap<>();

    public static long aoc22(Stream<String> input) {
        String[][][] tower = input
                .map(line -> new Brick(cubeFromString(line.split("~")[0]), cubeFromString(line.split("~")[1]), line))
                .sorted(Comparator.comparing(Brick::lowerZ))
                .reduce(new String[10][10][400], Day22::putBrick, (a, b) -> (a));
        for(int k = 0; k < 15; k++) {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    System.out.print(tower[i][j][k] + "  ");
                }
                System.out.print("\n");
            }
            System.out.print("\n");
            System.out.print("\n");
        }
        return 0;
    }
    private static String[][][] putBrick(String[][][] result, Brick brick) {
        int top = -1;
        for (int i = brick.lowerX(); i <= brick.higherX(); i++) {
            for (int j = brick.lowerY(); j <= brick.higherY(); j++) {
                top = Math.max(top, topsMap.getOrDefault(new Point(i, j), -1));
            }
        }
        for (int i = brick.lowerX(); i <= brick.higherX(); i++) {
            for (int j = brick.lowerY(); j <= brick.higherY(); j++) {
                topsMap.put(new Point(i, j), top + 1 + brick.higherZ() - brick.lowerZ());
                for (int k = top + 1; k <= top + 1 + brick.higherZ() - brick.lowerZ(); k++) {
                    result[i][j][k] = brick.name();
                }
            }
        }
        return result;
    }

    static Cube cubeFromString(String xyz) {
        String[] spxyz = xyz.split(",");
        return new Cube(Integer.parseInt(spxyz[0]), Integer.parseInt(spxyz[1]), Integer.parseInt(spxyz[2]));
    }
}
