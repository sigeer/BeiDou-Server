package org.gms.util;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Randomizer {

    private final static Random rand = new Random();

    public static int nextInt() {
        return rand.nextInt();
    }

    public static int nextInt(final int arg0) {
        return rand.nextInt(arg0);
    }

    public static void nextBytes(final byte[] bytes) {
        rand.nextBytes(bytes);
    }

    public static boolean nextBoolean() {
        return rand.nextBoolean();
    }

    public static double nextDouble() {
        return rand.nextDouble();
    }

    public static float nextFloat() {
        return rand.nextFloat();
    }

    public static long nextLong() {
        return rand.nextLong();
    }

    public static int rand(final int lbound, final int ubound) {
        return (int) ((rand.nextDouble() * (ubound - lbound + 1)) + lbound);
    }

    public static int pickByWeight(Map<Integer, Integer> weightMap) {
        if (weightMap == null || weightMap.isEmpty()) {
            return 0;
        }

        long totalWeight = 0;
        for (int w : weightMap.values()) {
            totalWeight += w;
        }

        long random = ThreadLocalRandom.current().nextLong(totalWeight);
        long cumulative = 0;
        for (Map.Entry<Integer, Integer> entry : weightMap.entrySet()) {
            cumulative += entry.getValue();
            if (random < cumulative) {
                return entry.getKey();
            }
        }

        // 理论上不会执行
        return 0;
    }
}