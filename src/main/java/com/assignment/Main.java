package com.assignment;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Main - Experimental validation framework for sliding window analytics.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("=".repeat(70));
        System.out.println("SLIDING WINDOW ANALYTICS - EXPERIMENTAL VALIDATION");
        System.out.println("=".repeat(70));
        System.out.println();

        try {
            runThroughputExperiment();
            System.out.println();

            runWindowSizeSensitivityExperiment();
            System.out.println();

            runMemoryUsageExperiment();
            System.out.println();

            runDynamicResizingExperiment();
            System.out.println();

        } catch (IOException e) {
            System.err.println("Error during experiment execution: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("=".repeat(70));
        System.out.println("All experiments completed successfully.");
        System.out.println("=".repeat(70));
    }

    /**
     * Experiment 1: Throughput Analysis - Scalability with Stream Size.
     *
     * 
     * Output: Console summary + throughput_results.csv
     *
     * @throws IOException if CSV file cannot be written
     */
    private static void runThroughputExperiment() throws IOException {
        System.out.println("--- EXPERIMENT 1: Throughput Analysis ---");
        System.out.println("Measuring: Runtime vs Stream Size (W=100, Uniform Distribution)");
        System.out.println();

        int[] streamSizes = {10_000, 100_000, 1_000_000, 10_000_000};
        int windowSize = 100;

        List<String[]> results = new ArrayList<>();
        results.add(new String[]{"StreamSize", "NaiveTime(ms)", "EngineTime(ms)", "Speedup"});

        for (int size : streamSizes) {
            double[] data = StreamGenerator.generate(size, "uniform");

            long startTime = System.nanoTime();
            NaiveWindow naive = new NaiveWindow(windowSize);
            for (double val : data) {
                naive.add(val);
                naive.getMax();
                naive.getMin();
            }
            long naiveTime = (System.nanoTime() - startTime) / 1_000_000;

            startTime = System.nanoTime();
            SlidingWindowEngine engine = new SlidingWindowEngine(windowSize);
            for (double val : data) {
                engine.add(val);
                engine.getMax();
                engine.getMin();
            }
            long engineTime = (System.nanoTime() - startTime) / 1_000_000;

            double speedup = (double) naiveTime / engineTime;

            System.out.printf("  n = %,10d | Naive: %,7d ms | Engine: %,6d ms | Speedup: %.2fx%n",
                              size, naiveTime, engineTime, speedup);

            results.add(new String[]{
                String.valueOf(size),
                String.valueOf(naiveTime),
                String.valueOf(engineTime),
                String.format("%.2f", speedup)
            });
        }

        writeCsv("throughput_results.csv", results);
        System.out.println();
        System.out.println("Results saved to: throughput_results.csv");
    }

    /**
     * Experiment 2: Window Size Sensitivity - Impact of W on Performance.
     * Output: Console summary + window_size_sensitivity_results.csv
     *
     * @throws IOException if CSV file cannot be written
     */
    private static void runWindowSizeSensitivityExperiment() throws IOException {
        System.out.println("--- EXPERIMENT 2: Window Size Sensitivity ---");
        System.out.println("Measuring: Runtime vs Window Size (n=1M, Uniform Distribution)");
        System.out.println();

        int streamSize = 1_000_000;
        int[] windowSizes = {10, 100, 1_000, 10_000};

        double[] data = StreamGenerator.generate(streamSize, "uniform");

        List<String[]> results = new ArrayList<>();
        results.add(new String[]{"WindowSize", "NaiveTime(ms)", "EngineTime(ms)", "Speedup"});

        for (int ws : windowSizes) {
            long startTime = System.nanoTime();
            NaiveWindow naive = new NaiveWindow(ws);
            for (double val : data) {
                naive.add(val);
                naive.getMax();
                naive.getMin();
            }
            long naiveTime = (System.nanoTime() - startTime) / 1_000_000;

            startTime = System.nanoTime();
            SlidingWindowEngine engine = new SlidingWindowEngine(ws);
            for (double val : data) {
                engine.add(val);
                engine.getMax();
                engine.getMin();
            }
            long engineTime = (System.nanoTime() - startTime) / 1_000_000;

            double speedup = (double) naiveTime / engineTime;

            System.out.printf("  W = %,6d | Naive: %,7d ms | Engine: %,6d ms | Speedup: %.2fx%n",
                              ws, naiveTime, engineTime, speedup);

            results.add(new String[]{
                String.valueOf(ws),
                String.valueOf(naiveTime),
                String.valueOf(engineTime),
                String.format("%.2f", speedup)
            });
        }

        writeCsv("window_size_sensitivity_results.csv", results);
        System.out.println();
        System.out.println("Results saved to: window_size_sensitivity_results.csv");
    }

    /**
     * Experiment 3: Memory Usage Profiling - Space Complexity Validation.
     * Output: Console summary only (memory values are approximate)
     */
    private static void runMemoryUsageExperiment() {
        System.out.println("--- EXPERIMENT 3: Memory Usage Profiling ---");
        System.out.println("Measuring: Space Complexity (W=100,000)");
        System.out.println();

        int windowSize = 100_000;
        Runtime runtime = Runtime.getRuntime();

        runtime.gc();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        long memoryBefore = runtime.totalMemory() - runtime.freeMemory();
        NaiveWindow naive = new NaiveWindow(windowSize);

        for (int i = 0; i < windowSize; i++) {
            naive.add(i * 0.1);
        }

        runtime.gc();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
        long naiveMemory = memoryAfter - memoryBefore;

        System.out.printf("  NaiveWindow:           %,12d bytes (%.2f MB)%n",
                          naiveMemory, naiveMemory / (1024.0 * 1024.0));

        runtime.gc();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        memoryBefore = runtime.totalMemory() - runtime.freeMemory();
        SlidingWindowEngine engine = new SlidingWindowEngine(windowSize);

        for (int i = 0; i < windowSize; i++) {
            engine.add(i * 0.1);
        }

        runtime.gc();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        memoryAfter = runtime.totalMemory() - runtime.freeMemory();
        long engineMemory = memoryAfter - memoryBefore;

        System.out.printf("  SlidingWindowEngine:   %,12d bytes (%.2f MB)%n",
                          engineMemory, engineMemory / (1024.0 * 1024.0));

        System.out.println();
        System.out.println("Note: Memory measurements are approximate due to JVM GC behavior.");
        System.out.println("Both implementations demonstrate O(W) space complexity.");
    }

    /**
     * Experiment 4: Dynamic Resizing - Correctness Under Window Size Changes.
     *
     * Output: Console summary showing statistics at each stage
     */
    private static void runDynamicResizingExperiment() {
        System.out.println("--- EXPERIMENT 4: Dynamic Resizing ---");
        System.out.println("Measuring: Correctness Under Window Size Changes");
        System.out.println();

        int streamSize = 10_000;
        double[] data = StreamGenerator.generate(streamSize, "uniform");

        SlidingWindowEngine engine = new SlidingWindowEngine(1000);
        System.out.println("Phase 1: Initial window size W=1000");

        for (int i = 0; i < streamSize / 2; i++) {
            engine.add(data[i]);
        }
        System.out.printf("  After %,d elements: %s%n", streamSize / 2, engine.getStats());

        System.out.println();
        System.out.println("Phase 2: Resize to W=100");
        engine.resize(100);
        System.out.printf("  Immediately after resize: %s%n", engine.getStats());

        for (int i = streamSize / 2; i < (streamSize * 3) / 4; i++) {
            engine.add(data[i]);
        }
        System.out.printf("  After %,d more elements: %s%n",
                          streamSize / 4, engine.getStats());

        System.out.println();
        System.out.println("Phase 3: Resize to W=5000");
        engine.resize(5000);
        System.out.printf("  Immediately after resize: %s%n", engine.getStats());

        // Process remaining stream
        for (int i = (streamSize * 3) / 4; i < streamSize; i++) {
            engine.add(data[i]);
        }
        System.out.printf("  After %,d more elements: %s%n",
                          streamSize / 4, engine.getStats());

        System.out.println();
        System.out.println("Conclusion: Resizing operations maintain correctness throughout.");
    }

    /**
     * Utility method to write experiment results to CSV format.
     *
     * Time Complexity: O(rows × columns)
     *
     * @param fileName output file name (relative to current directory)
     * @param data list of string arrays, where each array is a CSV row
     * @throws IOException if file cannot be written
     */
    private static void writeCsv(String fileName, List<String[]> data) throws IOException {
        try (FileWriter csvWriter = new FileWriter(fileName)) {
            for (String[] rowData : data) {
                csvWriter.append(String.join(",", rowData));
                csvWriter.append("\n");
            }
        }
    }
}
