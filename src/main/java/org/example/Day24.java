package org.example;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Stream;

public class Day24 {

    record Hailstone(BigDecimal x, BigDecimal y, BigDecimal vx, BigDecimal vy) {
        Hailstone(long x, long y, int vx, int vy) {
            this(BigDecimal.valueOf(x), BigDecimal.valueOf(y), BigDecimal.valueOf(vx), BigDecimal.valueOf(vy));
        }

        BigDecimal a() {
            return vy.divide(vx, 10, RoundingMode.HALF_UP);
        }

        BigDecimal b() {
            return y().subtract(vy.divide(vx, 10, RoundingMode.HALF_UP).multiply(x()));
        }

        boolean xGoesUp() {
            return vx().longValue() > 0;
        }

        BigDecimal calculateY(BigDecimal x) {
            return a().multiply(x).add(b());
        }
    }

    public static long aoc24(Stream<String> input) {
        final long[] range = new long[]{200000000000000L, 400000000000000L};
        long output = 0L;
        List<Hailstone> stones = input.map(Day24::eqFromLine).toList();
        for (Hailstone s1 : stones) {
            for (Hailstone s2 : stones) {
                if (s1.equals(s2)) {
                    continue;
                }
                if (intersects(s1, s2, range)) {
                    output++;
                }
            }
        }
        return output / 2;
    }

    private static Hailstone eqFromLine(String line) {
        String[] spline = line.split(" @ ");
        String[] point = spline[0].split(", ");
        long x = Long.parseLong(point[0]);
        long y = Long.parseLong(point[1]);
        int vx = Integer.parseInt(spline[1].split(", ")[0].trim());
        int vy = Integer.parseInt(spline[1].split(", ")[1].trim());
        return new Hailstone(x, y, vx, vy);
    }

    static boolean intersects(Hailstone e1, Hailstone e2, long[] range) {
        if (e1.a().equals(e2.a())) {
            return false;
        }
        BigDecimal x = e1.b().subtract(e2.b()).divide(e2.a().subtract(e1.a()), 10, RoundingMode.HALF_UP);
        boolean qualified = x.floatValue() > e1.x().floatValue() == e1.xGoesUp();
        qualified = qualified && ((x.floatValue() > e2.x().floatValue()) == e2.xGoesUp());
        qualified = qualified && x.floatValue() >= range[0] && x.floatValue() <= range[1];
        qualified = qualified && e1.calculateY(x).floatValue() >= range[0] && e1.calculateY(x).floatValue() <= range[1];
        return qualified;
    }
}
