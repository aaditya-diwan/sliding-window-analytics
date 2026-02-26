package com.assignment;

/**
 * SlidingWindowEngine - A high-performance streaming analytics engine using monotonic deques.
 */
public class SlidingWindowEngine {

    private int windowSize;

    private double[] buffer;

    private int currentIndex;

    private MonotonicDeque maxDeque;

    private MonotonicDeque minDeque;

    /**
     * Constructs a SlidingWindowEngine with specified window size.
     * @param windowSize the size of the sliding window (must be positive)
     * @throws IllegalArgumentException if windowSize ≤ 0
     */
    public SlidingWindowEngine(int windowSize) {
        if (windowSize <= 0) {
            throw new IllegalArgumentException("Window size must be positive");
        }

        this.windowSize = windowSize;
        this.buffer = new double[windowSize];
        this.currentIndex = 0;
        this.maxDeque = new MonotonicDeque(buffer, true);  // true = max deque
        this.minDeque = new MonotonicDeque(buffer, false); // false = min deque
    }

    /**
     * Adds a new value to the stream and updates sliding window statistics.
     * @param value the new stream value to add
     */
    public void add(double value) {
        int bufferIndex = currentIndex % windowSize;

        buffer[bufferIndex] = value;

        maxDeque.push(bufferIndex);

        minDeque.push(bufferIndex);

        maxDeque.popOutdated(currentIndex, windowSize);
        minDeque.popOutdated(currentIndex, windowSize);

        currentIndex++;
    }

    public double getMax() {
        if (maxDeque.isEmpty()) {
            return Double.NEGATIVE_INFINITY; // No elements in window
        }
        return buffer[maxDeque.getFrontIndex()];
    }

    public double getMin() {
        if (minDeque.isEmpty()) {
            return Double.POSITIVE_INFINITY; // No elements in window
        }
        return buffer[minDeque.getFrontIndex()];
    }

    /**
     * Dynamically resizes the sliding window without halting stream processing.
     * @param newWindowSize the new window size (must be positive)
     * @throws IllegalArgumentException if newWindowSize ≤ 0
     */
    public void resize(int newWindowSize) {
        if (newWindowSize <= 0) {
            throw new IllegalArgumentException("Window size must be positive");
        }

        double[] newBuffer = new double[newWindowSize];

        int start = Math.max(0, currentIndex - windowSize);
        int elementsToKeep = Math.min(currentIndex - start, newWindowSize);

        int newIndex = 0;
        for (int i = currentIndex - elementsToKeep; i < currentIndex; i++) {
            newBuffer[newIndex++] = buffer[i % windowSize];
        }

        this.buffer = newBuffer;
        this.windowSize = newWindowSize;
        this.currentIndex = newIndex;

        maxDeque.clear();
        minDeque.clear();

        for (int i = 0; i < currentIndex; i++) {
            maxDeque.push(i);
            minDeque.push(i);
        }
    }

    public String getStats() {
        return String.format("Max: %.4f, Min: %.4f", getMax(), getMin());
    }

    public int getWindowSize() {
        return windowSize;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }
}
