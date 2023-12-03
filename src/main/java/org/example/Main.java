package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.Stream;

import static org.example.Day1.aoc1;
import static org.example.Day1.aoc1a;
import static org.example.Day2.aoc2;
import static org.example.Day2.aoc2a;
import static org.example.Day3.aoc3;
import static org.example.Day3.aoc3a;

public class Main {
    public static void main(String[] args) {
        System.out.println(aoc1(Objects.requireNonNull(fileReader(input1))));
        System.out.println(aoc1a(Objects.requireNonNull(fileReader(input1))));
        System.out.println(aoc2(Objects.requireNonNull(fileReader(input2))));
        System.out.println(aoc2a(Objects.requireNonNull(fileReader(input2))));
        System.out.println(aoc3(Objects.requireNonNull(fileReader(input3))));
        System.out.println(aoc3a(Objects.requireNonNull(fileReader(input3))));
    }

    private static final String test = "src/main/java/org/example/inputs/testInput";
    private static final String input1 = "src/main/java/org/example/inputs/day1Input";
    private static final String input2 = "src/main/java/org/example/inputs/day2input";
    private static final String input3 = "src/main/java/org/example/inputs/day3input";

    static Stream<String> fileReader(String filePath) {
       try {
            return Files.lines(Path.of(filePath));
        } catch (IOException e) {
            return null;
        }
    }
}