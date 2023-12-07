package org.example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class Day7 {

    static class Hand implements Comparable<Hand> {

        private final String line;
        private final int[] type;
        private final long bid;
        private final Map<Character, Integer> strMap = new HashMap<>();

        Hand(String[] line) {
            this.line = line[0];
            this.bid = Long.parseLong(line[1]);
            this.type = new int[6];
            Map<Character, Integer> freq = new HashMap<>();
            for (char c : this.line.toCharArray()) {
                freq.put(c, freq.getOrDefault(c, 0) + 1);
            }
            freq.values().forEach(v -> type[v]++);

            int i = 13;
            for (String s : "A, K, Q, J, T, 9, 8, 7, 6, 5, 4, 3, 2".split(", ")) {
                strMap.put(s.charAt(0), i);
                i--;
            }
        }

        @Override
        public int compareTo(Hand o) {
            for (int i = 5; i > 0; i--) {
                if (type[i] != o.type[i]) {
                    return Integer.compare(type[i], o.type[i]);
                }
            }
            for (int i = 0; i < 5; i++) {
                Integer c1 = strMap.get(line.charAt(i));
                Integer c2 = strMap.get(o.line.charAt(i));
                if (!c1.equals(c2)) {
                    return Integer.compare(c1, c2);
                }
            }
            return 0;
        }
    }

    static long aoc7(Stream<String> input) {
        List<Hand> list = input
                .map(line -> new Hand(line.split(" ")))
                .sorted()
                .toList();
        long out = 0L;
        for (int i = 0; i < list.size(); i++) {
            out += ((i + 1) * list.get(i).bid);
        }
        return out;
    }
}
