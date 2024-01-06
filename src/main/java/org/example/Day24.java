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

    record Hail3D(BigDecimal x, BigDecimal y, BigDecimal z, BigDecimal vx, BigDecimal vy, BigDecimal vz) {
        public Hail3D(long x, long y, long z, int vx, int vy, int vz) {
            this(BigDecimal.valueOf(x), BigDecimal.valueOf(y), BigDecimal.valueOf(z), BigDecimal.valueOf(vx), BigDecimal.valueOf(vy), BigDecimal.valueOf(vz));
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


    public static long aoc24a(Stream<String> input) {
        List<Hail3D> stones = input.map(Day24::eq3DFromLine).limit(3).toList();
        Hail3D result = solve(stones.get(0), stones.get(1), stones.get(2));
        return result.x().multiply(result.y()).multiply(result.z()).longValue();
    }

    private static Hail3D solve(Hail3D hail0, Hail3D hail1, Hail3D hail2) {
        return new Hail3D(0,0,0,0,0,0);// TODO
    }

    private static Hail3D eq3DFromLine(String line) {
        String[] spline = line.split(" @ ");
        String[] point = spline[0].split(", ");
        long x = Long.parseLong(point[0]);
        long y = Long.parseLong(point[1]);
        long z = Long.parseLong(point[2]);
        int vx = Integer.parseInt(spline[1].split(", ")[0].trim());
        int vy = Integer.parseInt(spline[1].split(", ")[1].trim());
        int vz = Integer.parseInt(spline[1].split(", ")[2].trim());
        return new Hail3D(x, y, z, vx, vy, vz);
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
    /*
    x + t1*vx = x1 + t1*vx1
    y + t1*vy = y1 + t1*vy1
    z + t1*vz = z1 + t1*vz1
    x + t2*vx = x2 + t2*vx2
    y + t2*vy = y2 + t2*vy2
    z + t2*vz = z2 + t2*vz2
    x + t3*vx = x3 + t3*vx3
    y + t3*vy = y3 + t3*vy3
    z + t3*vz = z3 + t3*vz3

    x = x1 + t1*(vx1-vx)
    y = y1 + t1*(vy1-vy)
    z = z1 + t1*(vz1-vz)
    x = x2 + t2*(vx2-vx)
    y = y2 + t2*(vy2-vy)
    z = z2 + t2*(vz2-vz)
    x = x3 + t3*(vx3-vx)
    y = y3 + t3*(vy3-vy)
    z = z3 + t3*(vz3-vz)

    t1 = (x-x1)/(vx1-vx)
    t2 = (x-x2)/(vx2-vx)
    t3 = (x-x3)/(vx3-vx)

    y + vy*(x-x1)/(vx1-vx) = y1 + vy1*(x-x1)/(vx1-vx)
    z + vz*(x-x1)/(vx1-vx) = z1 + vz1*(x-x1)/(vx1-vx)
    y + vy*(x-x2)/(vx2-vx) = y2 + vy2*(x-x2)/(vx2-vx)
    z + vz*(x-x2)/(vx2-vx) = z2 + vz2*(x-x2)/(vx2-vx)
    y + vy*(x-x3)/(vx3-vx) = y3 + vy3*(x-x3)/(vx3-vx)
    z + vz*(x-x3)/(vx3-vx) = z3 + vz3*(x-x3)/(vx3-vx)

    vz = (z3-z)*(vx3-vx)/(x-x3) + vz3
    z + ((z3-z)*(vx3-vx)/(x-x3) + vz3)*(x-x2)/(vx2-vx) = z2 + vz2*(x-x2)/(vx2-vx)
    z - (z*(vx3-vx)/(x-x3))*(x-x2)/(vx2-vx) = z2 + (vz2 - vz3 - z3 *(vx3-vx)/(x-x3))*(x-x2)/(vx2-vx)
    z = (z2 + (vz2 - vz3 - z3 *(vx3-vx)/(x-x3))*(x-x2)/(vx2-vx))/






    x1 + t1*(vx1-vx) = x2 + t2*(vx2-vx)
    t1 = (x2 - x1 + t2*(vx2-vx))/(vx1-vx)
    y + ((x2 - x1 + t2*(vx2-vx))/(vx1-vx)) * vy +

     */
}
