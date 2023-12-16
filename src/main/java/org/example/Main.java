package org.example;

import java.io.IOException;

import static org.example.Day1.aoc1;
import static org.example.Day1.aoc1a;
import static org.example.Day10.aoc10;
import static org.example.Day10.aoc10a;
import static org.example.Day11.aoc11;
import static org.example.Day11.aoc11a;
import static org.example.Day12.aoc12;
import static org.example.Day13.aoc13;
import static org.example.Day13.aoc13a;
import static org.example.Day14.aoc14;
import static org.example.Day14.aoc14a;
import static org.example.Day15.aoc15;
import static org.example.Day15.aoc15a;
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
import static org.example.Utils.fileToStream;
import static org.example.Utils.fileToString;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println(aoc1(fileToStream(input1)));
        System.out.println(aoc1a(fileToStream(input1)));
        System.out.println(aoc2(fileToStream(input2)));
        System.out.println(aoc2a(fileToStream(input2)));
        System.out.println(aoc3(fileToStream(input3)));
        System.out.println(aoc3a(fileToStream(input3)));
        System.out.println(aoc4(fileToStream(input4)));
        System.out.println(aoc4a(fileToStream(input4)));
        System.out.println(aoc5(fileToString(input5)));
        System.out.println(aoc5a(fileToString(input5)));
        System.out.println(aoc6(fileToString(input6)));
        System.out.println(aoc6a(fileToString(input6)));
        System.out.println(aoc7(fileToStream(input7)));
        System.out.println(aoc7a(fileToStream(input7)));
        System.out.println(aoc8(fileToStream(input8)));
        System.out.println(aoc8a(fileToStream(input8)));
        System.out.println(aoc9(fileToStream(input9)));
        System.out.println(aoc9a(fileToStream(input9)));
        System.out.println(aoc10(fileToStream(input10)));
        System.out.println(aoc10a(fileToStream(input10)));
        System.out.println(aoc11(fileToStream(input11)));
        System.out.println(aoc11a(fileToStream(input11)));
        System.out.println(aoc12(fileToStream(input12)));
        // System.out.println(aoc12a(fileReader(input12)));
        System.out.println(aoc13(fileToString(input13)));
        System.out.println(aoc13a(fileToString(input13)));
        System.out.println(aoc14(fileToStream(input14)));
        System.out.println(aoc14a(fileToStream(input14)));
        System.out.println(aoc15(fileToString(input15)));
        System.out.println(aoc15a(fileToString(input15)));
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
    private static final String input12 = "src/main/java/org/example/inputs/day12input";
    private static final String input13 = "src/main/java/org/example/inputs/day13input";
    private static final String input14 = "src/main/java/org/example/inputs/day14input";
    private static final String input15 = "src/main/java/org/example/inputs/day15input";

}