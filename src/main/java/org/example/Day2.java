package org.example;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day2 {

    record Game(Long id, List<Map<String, Long>> rounds) {
    }

    static String aoc2(Stream<String> input) {
        final Map<String, Long> loaded = Map.of("red", 12L, "green", 13L, "blue", 14L);
        return input
                .map(Day2::lineToGame)
                .filter(game -> validGame(loaded, game))
                .map(Game::id)
                .reduce(0L, Long::sum)
                .toString();
    }

    static String aoc2a(Stream<String> input) {
        return input
                .map(Day2::lineToGame)
                .map(Day2::toSetPower)
                .reduce(0L, Long::sum)
                .toString();
    }

    private static Long toSetPower(Game game) {
        Map<String, Long> maxMap = game.rounds()
                .stream()
                .flatMap(round -> round.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a > b ? a : b));
        return maxMap.values().stream().reduce(1L, (a, b) -> a * b);
    }

    private static Game lineToGame(String line) {
        String[] lsplit = line.split(": ");
        long id = Long.parseLong(lsplit[0].split(" ")[1]);
        List<Map<String, Long>> rounds = Arrays.stream(lsplit[1].split("; "))
                .map(roundString -> Arrays.stream(roundString.split(", "))
                        .map(cubeString -> cubeString.split(" "))
                        .collect(Collectors.toMap(csplit -> csplit[1], csplit -> Long.parseLong(csplit[0]))))
                .toList();
        return new Game(id, rounds);
    }

    private static boolean validGame(Map<String, Long> loaded, Game game) {
        return game.rounds().stream()
                .noneMatch(r -> r.keySet().stream()
                        .anyMatch(k -> r.get(k) > loaded.getOrDefault(k, 0L)));
    }
}
