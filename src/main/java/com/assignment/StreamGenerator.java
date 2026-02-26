package com.assignment;

import java.util.Random;

/**
 * StreamGenerator - Synthetic data stream generator for experimental validation.
 */
public class StreamGenerator {

    private static final Random RANDOM = new Random(42); // Fixed seed for reproducibility

    /**
     * Generates a synthetic data stream with specified pattern.
     * @param n the number of elements to generate (must be positive)
     * @param patternType the distribution type: "uniform", "gaussian", "sinusoidal", or "spike"
     * @return array of n generated values
     * @throws IllegalArgumentException if patternType is not recognized or n ≤ 0
     */
    public static double[] generate(int n, String patternType) {
        if (n <= 0) {
            throw new IllegalArgumentException("Stream size must be positive");
        }

        double[] data = new double[n];

        switch (patternType.toLowerCase()) {
            case "uniform":
                for (int i = 0; i < n; i++) {
                    data[i] = RANDOM.nextDouble();
                }
                break;

            case "gaussian":
                for (int i = 0; i < n; i++) {
                    data[i] = RANDOM.nextGaussian();
                }
                break;

            case "sinusoidal":
                for (int i = 0; i < n; i++) {
                    data[i] = Math.sin(i / 10.0);
                }
                break;

            case "spike":
                for (int i = 0; i < n; i++) {
                    data[i] = RANDOM.nextDouble();
                }

                int numSpikes = Math.max(1, n / 100); // 1% spikes
                for (int i = 0; i < numSpikes; i++) {
                    int spikeIndex = RANDOM.nextInt(n);
                    data[spikeIndex] = RANDOM.nextDouble() * 100; // 100x magnitude
                }
                break;

            default:
                throw new IllegalArgumentException(
                    "Unknown pattern type: " + patternType +
                    ". Valid options: uniform, gaussian, sinusoidal, spike"
                );
        }

        return data;
    }

    /**
     * Generates a strictly increasing stream - worst case for monotonic deque.
     * @param n the number of elements to generate
     * @return strictly increasing sequence [0, 1, 2, ..., n-1]
     */
    public static double[] generateIncreasing(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("Stream size must be positive");
        }

        double[] data = new double[n];
        for (int i = 0; i < n; i++) {
            data[i] = i;
        }
        return data;
    }

    /**
     * @param n the number of elements to generate
     * @return strictly decreasing sequence [n-1, n-2, ..., 1, 0]
     */
    public static double[] generateDecreasing(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("Stream size must be positive");
        }

        double[] data = new double[n];
        for (int i = 0; i < n; i++) {
            data[i] = n - i - 1;
        }
        return data;
    }

    /**
     * Generates a constant stream - degenerate case.
     * @param n the number of elements to generate
     * @param value the constant value to repeat
     * @return array of n copies of value
     */
    public static double[] generateConstant(int n, double value) {
        if (n <= 0) {
            throw new IllegalArgumentException("Stream size must be positive");
        }

        double[] data = new double[n];
        for (int i = 0; i < n; i++) {
            data[i] = value;
        }
        return data;
    }
}
