package com.assignment;

import java.util.ArrayList;
import java.util.List;

/**
 * NaiveWindow - Baseline O(W) implementation for sliding window extrema computation.
 */
public class NaiveWindow {

    private List<Double> window;

    private int windowSize;

    public NaiveWindow(int windowSize) {
        if (windowSize <= 0) {
            throw new IllegalArgumentException("Window size must be positive");
        }

        this.windowSize = windowSize;
        this.window = new ArrayList<>(windowSize);
    }

    /**
     * Adds a new value to the window.
     * @param value the new value to add to the window
     */
    public void add(double value) {
        // Remove oldest element if window is full
        if (window.size() == windowSize) {
            window.remove(0); // O(W) operation - shifts all elements left
        }

        // Append new value to end of window
        window.add(value); // O(1) amortized (ArrayList expansion)
    }

    /**
     * Computes the maximum value in the current window.
     * @return the maximum value in the window, or Double.NEGATIVE_INFINITY if window is empty
     */
    public double getMax() {
        if (window.isEmpty()) {
            return Double.NEGATIVE_INFINITY; // No elements in window
        }

        double max = window.get(0);
        for (int i = 1; i < window.size(); i++) {
            if (window.get(i) > max) {
                max = window.get(i);
            }
        }
        return max;
    }

    /**
     * Computes the minimum value in the current window.
     * @return the minimum value in the window, or Double.POSITIVE_INFINITY if window is empty
     */
    public double getMin() {
        if (window.isEmpty()) {
            return Double.POSITIVE_INFINITY; // No elements in window
        }

        double min = window.get(0);
        for (int i = 1; i < window.size(); i++) {
            if (window.get(i) < min) {
                min = window.get(i);
            }
        }
        return min;
    }

    /**
     * Resizes the window to a new size.
     * @param newWindowSize the new window size (must be positive)
     * @throws IllegalArgumentException if newWindowSize ≤ 0
     */
    public void resize(int newWindowSize) {
        if (newWindowSize <= 0) {
            throw new IllegalArgumentException("Window size must be positive");
        }

        this.windowSize = newWindowSize;

        if (window.size() > newWindowSize) {
            int itemsToRemove = window.size() - newWindowSize;
            for (int i = 0; i < itemsToRemove; i++) {
                window.remove(0); // O(W) per removal - inefficient
            }
        }
    }

    /**
     * Returns the current number of elements in the window.
     * @return the number of elements currently stored
     */
    public int size() {
        return window.size();
    }

    /**
     * Returns the configured window size.
     * @return the maximum window size
     */
    public int getWindowSize() {
        return windowSize;
    }
}
