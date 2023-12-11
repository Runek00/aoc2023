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
        List<List<Character>> listTab = getMarkedLists(input);
        Set<Point> galaxies = getGalaxies(listTab);
        return galaxies.stream()
                .flatMap(g -> getDistances(g, galaxies, listTab, 2))
                .mapToLong(s -> s)
                .sum() / 2;
    }

    public static long aoc11a(Stream<String> input) {
        List<List<Character>> listTab = getMarkedLists(input);
        Set<Point> galaxies = getGalaxies(listTab);
        return galaxies.stream()
                .flatMap(g -> getDistances(g, galaxies, listTab, 1000000))
                .mapToLong(s -> s)
                .sum() / 2;
    }

    private static List<List<Character>> getMarkedLists(Stream<String> input) {
        List<List<Character>> listTab = input
                .map(line -> line.contains("#") ? line : new String(new char[line.length()]).replace("\0", "X"))
                .map(Day11::toCharacterList)
                .toList();

        for (int i = listTab.get(0).size() - 1; i >= 0; i--) {
            int finalI = i;
            if (listTab.stream().map(list -> list.get(finalI)).noneMatch(character -> character == '#')) {
                listTab.forEach(list -> list.set(finalI, 'X'));
            }
        }
        return listTab;
    }

    private static Set<Point> getGalaxies(List<List<Character>> listTab) {
        Set<Point> galaxies = new HashSet<>();
        for (int i = 0; i < listTab.size(); i++) {
            for (int j = 0; j < listTab.get(0).size(); j++) {
                if (listTab.get(i).get(j) == '#') {
                    galaxies.add(new Point(i, j));
                }
            }
        }
        return galaxies;
    }

    private static Stream<Long> getDistances(Point g, Set<Point> galaxies, List<List<Character>> listTab, long factor) {
        return galaxies.stream()
                .filter(gal -> !gal.equals(g))
                .map(gal -> {
                    long len = 0;
                    int xStart = (int)(Math.min(g.x(), gal.x()));
                    int xEnd = (int)(Math.max(g.x(), gal.x()));
                    int yStart = (int)(Math.min(g.y(), gal.y()));
                    int yEnd = (int)(Math.max(g.y(), gal.y()));
                    for (int i = xStart + 1; i <= xEnd; i++) {
                        if(listTab.get(i).get(yStart) == 'X') {
                            len += factor;
                        } else {
                            len += 1;
                        }
                    }
                    for (int i = yStart + 1; i <= yEnd; i += (yStart < yEnd) ? 1 : -1) {
                        if(listTab.get(xEnd).get(i) == 'X') {
                            len += factor;
                        } else {
                            len += 1;
                        }
                    }
                    return len;
                });
    }

    private static List<Character> toCharacterList(String line) {
        ArrayList<Character> output = new ArrayList<>();
        for (char c : line.toCharArray()) {
            output.add(c);
        }
        return output;
    }
}
