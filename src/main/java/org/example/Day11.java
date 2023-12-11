package org.example;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class Day11 {

    record Point(long x, long y) {
    }

    public static long aoc11(Stream<String> input) {
        List<List<Character>> listTab = input
                .flatMap(line -> line.contains("#") ? Stream.of(line) : Stream.of(line, line))
                .map(Day11::toCharacterList)
                .toList();

        for (int i = listTab.get(0).size() - 1; i >= 0; i--) {
            int finalI = i;
            if (listTab.stream().map(list -> list.get(finalI)).noneMatch(character -> character == '#')) {
                listTab.forEach(list -> list.add(finalI + 1, '.'));
            }
        }

        Set<Point> galaxies = new HashSet<>();
        for (int i = 0; i < listTab.size(); i++) {
            for (int j = 0; j < listTab.get(0).size(); j++) {
                if (listTab.get(i).get(j) == '#') {
                    galaxies.add(new Point(i, j));
                }
            }
        }
        return galaxies.stream()
                .flatMap(g -> getDistances(g, galaxies))
                .mapToLong(s -> s)
                .sum() / 2;
    }

    private static Stream<Long> getDistances(Point g, Set<Point> galaxies) {
        return galaxies.stream()
                .filter(gal -> !gal.equals(g))
                .map(gal -> Math.abs(g.x() - gal.x()) + Math.abs(g.y() - gal.y()));
    }

    private static List<Character> toCharacterList(String line) {
        ArrayList<Character> output = new ArrayList<>();
        for (char c : line.toCharArray()) {
            output.add(c);
        }
        return output;
    }
}
