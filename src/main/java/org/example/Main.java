package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.example.Day1.aoc1;
import static org.example.Day1.aoc1a;
import static org.example.Day2.aoc2;
import static org.example.Day2.aoc2a;
import static org.example.Day3.aoc3;
import static org.example.Day3.aoc3a;
import static org.example.Day4.aoc4;
import static org.example.Day4.aoc4a;
import static org.example.Day5.aoc5;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println(aoc1(fileReader(input1)));
        System.out.println(aoc1a(fileReader(input1)));
        System.out.println(aoc2(fileReader(input2)));
        System.out.println(aoc2a(fileReader(input2)));
        System.out.println(aoc3(fileReader(input3)));
        System.out.println(aoc3a(fileReader(input3)));
        System.out.println(aoc4(fileReader(input4)));
        System.out.println(aoc4a(fileReader(input4)));
        System.out.println(aoc5(Files.readString(Path.of(input5))));
    }

    private static final String test = "src/main/java/org/example/inputs/testInput";
    private static final String input1 = "src/main/java/org/example/inputs/day1Input";
    private static final String input2 = "src/main/java/org/example/inputs/day2input";
    private static final String input3 = "src/main/java/org/example/inputs/day3input";
    private static final String input4 = "src/main/java/org/example/inputs/day4input";
    private static final String input5 = "src/main/java/org/example/inputs/day5input";

    static Stream<String> fileReader(String filePath) throws IOException {
        return Files.lines(Path.of(filePath));
    }
}