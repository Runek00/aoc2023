package org.example;

import java.util.*;
import java.util.stream.Collectors;
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
    static int[][] topsMap = new int[10][10];
    static Map<Integer, Set<Brick>> levelBricks = new HashMap<>();

    public static long aoc22(Stream<String> input) {
        String[][][] tower = parseTower(input);
//        for(int k = 0; k < 15; k++) {
//            for (int i = 0; i < 3; i++) {
//                for (int j = 0; j < 3; j++) {
//                    System.out.print(tower[i][j][k] + "  ");
//                }
//                System.out.print("\n");
//            }
//            System.out.print("\n");
//            System.out.print("\n");
//        }
        return countRemovable(tower);
    }

    private static int countRemovable(String[][][] tower) {
        Set<Brick> emptyCombos = new HashSet<>();
        for(int k = 0; k < 400; k++) {
            if(!levelBricks.containsKey(k)) {
                break;
            }
            Set<Brick> bricks = levelBricks.get(k);
            int finalK = k;
            Map<Brick, Set<String>> onThemMap = bricks.stream().collect(Collectors.toMap(brick -> brick, brick -> getHigherBricks(brick, finalK, tower)));
            emptyCombos.addAll(getRemovableSet(onThemMap));
        }
        return emptyCombos.size();
    }

    private static Set<Brick> getRemovableSet(Map<Brick, Set<String>> onThemMap) {
        Set<Brick> output = new HashSet<>();
        for(Brick b : onThemMap.keySet()) {
            Set<String> set1 = new HashSet<>(onThemMap.get(b));
            Set<String> set2 = onThemMap.entrySet().stream().filter(entry -> entry.getKey() != b).flatMap(entry -> entry.getValue().stream()).collect(Collectors.toSet());
            set1.removeAll(set2);
            if(set1.isEmpty()) {
                output.add(b);
            }
        }
        return output;
    }

    private static Set<String> getHigherBricks(Brick brick, int k, String[][][] tower) {
        Set<String> output = new HashSet<>();
        for(int i = brick.lowerX(); i <= brick.higherX(); i++) {
            for (int j = brick.lowerY(); j <= brick.higherY(); j++) {
                if(tower[i][j][k+1] != null) {
                    output.add(tower[i][j][k+1]);
                }
            }
        }
        return output;
    }

    private static String[][][] parseTower(Stream<String> input) {
        return input
                .map(line -> new Brick(cubeFromString(line.split("~")[0]), cubeFromString(line.split("~")[1]), line))
                .sorted(Comparator.comparing(Brick::lowerZ))
                .reduce(new String[10][10][400], Day22::putBrick, (a, b) -> (a));
    }

    private static String[][][] putBrick(String[][][] result, Brick brick) {
        int top = 0;
        for (int i = brick.lowerX(); i <= brick.higherX(); i++) {
            for (int j = brick.lowerY(); j <= brick.higherY(); j++) {
                top = Math.max(top, topsMap[i][j]);
            }
        }
        for (int i = brick.lowerX(); i <= brick.higherX(); i++) {
            for (int j = brick.lowerY(); j <= brick.higherY(); j++) {
                topsMap[i][j] = top + 1 + brick.higherZ() - brick.lowerZ();
                for (int k = top; k < top + 1 + brick.higherZ() - brick.lowerZ(); k++) {
                    result[i][j][k] = brick.name();
                    Set<Brick> set = levelBricks.getOrDefault(k, new HashSet<>());
                    set.add(brick);
                    levelBricks.put(k, set);
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
