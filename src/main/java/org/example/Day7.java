package org.example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class Day7 {

    private final static Map<Character, Integer> strMap = new HashMap<>();

    static class Hand implements Comparable<Hand> {

        private final String line;
        private final int[] type;
        private final long bid;

        Hand(String[] line) {
            this.line = line[0];
            this.bid = Long.parseLong(line[1]);
            this.type = new int[6];
            Map<Character, Integer> freq = new HashMap<>();
            for (char c : this.line.toCharArray()) {
                freq.put(c, freq.getOrDefault(c, 0) + 1);
            }
            freq.values().forEach(v -> type[v]++);

        }

        @Override
        public int compareTo(Hand o) {
            for (int i = 5; i > 0; i--) {
                if (type[i] != o.type[i]) {
                    return Integer.compare(type[i], o.type[i]);
                }
            }
            for (int i = 0; i < 5; i++) {
                int c1 = strMap.get(line.charAt(i));
                int c2 = strMap.get(o.line.charAt(i));
                if (c1 != c2) {
                    return Integer.compare(c1, c2);
                }
            }
            return 0;
        }
    }

    static long aoc7(Stream<String> input) {
        int n = 0;
        for (String s : "A, K, Q, J, T, 9, 8, 7, 6, 5, 4, 3, 2".split(", ")) {
            strMap.put(s.charAt(0), n);
            n--;
        }
        List<Hand> list = input
                .map(line -> new Hand(line.split(" ")))
                .sorted()
                .toList();
        long out = 0L;
        for (int i = 0; i < list.size(); i++) {
            int rank = i+1;
            out += (rank * list.get(i).bid);
        }
        return out;
    }
}
