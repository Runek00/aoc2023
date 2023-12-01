package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.Stream;

import static org.example.Day1.aoc1;
import static org.example.Day1.aoc1a;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        System.out.println(aoc1(Objects.requireNonNull(fileReader(input1))));
        System.out.println(aoc1a(Objects.requireNonNull(fileReader(input1))));
    }

    private static final String test = "src/main/java/org/example/inputs/testInput";
    private static final String input1 = "src/main/java/org/example/inputs/day1Input";

    static Stream<String> fileReader(String filePath) {
        try {
            return Files.lines(Path.of(filePath));
        } catch (IOException e) {
            return null;
        }
    }
}