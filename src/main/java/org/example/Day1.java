package org.example;

import java.util.Map;
import java.util.stream.Stream;

public class Day1 {

    private static final Map<String, Character> nums = Map.of(
            "one", '1',
            "two", '2',
            "three", '3',
            "four", '4',
            "five", '5',
            "six", '6',
            "seven", '7',
            "eight", '8',
            "nine", '9');

    static String aoc1(Stream<String> input) {
        return input
                .map(Day1::getNumber)
                .reduce(0L, Long::sum)
                .toString();
    }

    static String aoc1a(Stream<String> input) {
        return input
                .map(Day1::getNumberOrWord)
                .reduce(0L, Long::sum)
                .toString();
    }

    private static Long getNumber(String line) {
        Character firstNum = null, lastNum = null;
        for (char c : line.toCharArray()) {
            if (Character.isDigit(c)) {
                if (firstNum == null) {
                    firstNum = c;
                }
                lastNum = c;
            }
        }
        return Long.parseLong("" + firstNum + lastNum);
    }

    private static Long getNumberOrWord(String line) {
        Character firstNum = null, lastNum = null;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            Character num = null;
            if (Character.isDigit(c)) {
                num = c;
            } else {
                for (int j = 2; j < 5; j++) {
                    if (i >= j) {
                        String lastSome = line.substring(i - j, i + 1);
                        if (nums.containsKey(lastSome)) {
                            num = nums.get(lastSome);
                            break;
                        }
                    }
                }
            }
            if (num != null) {
                if (firstNum == null) {
                    firstNum = num;
                }
                lastNum = num;
            }
        }
        return Long.parseLong("" + firstNum + lastNum);
    }
}

