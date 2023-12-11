package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.example.Day1.aoc1;
import static org.example.Day1.aoc1a;
import static org.example.Day10.aoc10;
import static org.example.Day10.aoc10a;
import static org.example.Day11.aoc11;
import static org.example.Day2.aoc2;
import static org.example.Day2.aoc2a;
import static org.example.Day3.aoc3;
import static org.example.Day3.aoc3a;
import static org.example.Day4.aoc4;
import static org.example.Day4.aoc4a;
import static org.example.Day5.aoc5;
import static org.example.Day5.aoc5a;
import static org.example.Day6.aoc6;
import static org.example.Day6.aoc6a;
import static org.example.Day7.aoc7;
import static org.example.Day7.aoc7a;
import static org.example.Day8.aoc8;
import static org.example.Day8.aoc8a;
import static org.example.Day9.aoc9;
import static org.example.Day9.aoc9a;

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
        System.out.println(aoc5a(Files.readString(Path.of(input5))));
        System.out.println(aoc6(Files.readString(Path.of(input6))));
        System.out.println(aoc6a(Files.readString(Path.of(input6))));
        System.out.println(aoc7(fileReader(input7)));
        System.out.println(aoc7a(fileReader(input7)));
        System.out.println(aoc8(fileReader(input8)));
        System.out.println(aoc8a(fileReader(input8)));
        System.out.println(aoc9(fileReader(input9)));
        System.out.println(aoc9a(fileReader(input9)));
        System.out.println(aoc10(fileReader(input10)));
        System.out.println(aoc10a(fileReader(input10)));
        System.out.println(aoc11(fileReader(input11)));
    }

    private static final String test = "src/main/java/org/example/inputs/testInput";
    private static final String input1 = "src/main/java/org/example/inputs/day1Input";
    private static final String input2 = "src/main/java/org/example/inputs/day2input";
    private static final String input3 = "src/main/java/org/example/inputs/day3input";
    private static final String input4 = "src/main/java/org/example/inputs/day4input";
    private static final String input5 = "src/main/java/org/example/inputs/day5input";
    private static final String input6 = "src/main/java/org/example/inputs/day6input";
    private static final String input7 = "src/main/java/org/example/inputs/day7input";
    private static final String input8 = "src/main/java/org/example/inputs/day8input";
    private static final String input9 = "src/main/java/org/example/inputs/day9input";
    private static final String input10 = "src/main/java/org/example/inputs/day10input";
    private static final String input11 = "src/main/java/org/example/inputs/day11input";

    static Stream<String> fileReader(String filePath) throws IOException {
        return Files.lines(Path.of(filePath));
    }
}