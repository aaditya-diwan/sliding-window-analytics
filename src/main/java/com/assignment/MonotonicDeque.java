package com.assignment;

import java.util.Deque;
import java.util.ArrayDeque;

/**
 * MonotonicDeque - An index-based deque maintaining monotonic ordering for efficient extrema queries.
 */
public class MonotonicDeque {

   
    private Deque<Integer> deque = new ArrayDeque<>();

    private double[] data;

    private boolean isMaxDeque;

    /**
     * Constructs a MonotonicDeque with specified configuration.
     *
     * @param data reference to the circular buffer containing stream values
     * @param isMaxDeque true for maximum tracking (decreasing order), false for minimum (increasing order)
     */
    public MonotonicDeque(double[] data, boolean isMaxDeque) {
        this.data = data;
        this.isMaxDeque = isMaxDeque;
    }

    public void push(int index) {
        if (isMaxDeque) {
            while (!deque.isEmpty() && data[deque.peekLast()] <= data[index]) {
                deque.pollLast();
            }
        } else {

            while (!deque.isEmpty() && data[deque.peekLast()] >= data[index]) {
                deque.pollLast();
            }
        }

        deque.offerLast(index);
    }

    public void popOutdated(int currentIndex, int windowSize) {
        if (!deque.isEmpty() && deque.peekFirst() <= currentIndex - windowSize) {
            deque.pollFirst();
        }
    }

    public int getFrontIndex() {
        return deque.peekFirst();
    }


    public boolean isEmpty() {
        return deque.isEmpty();
    }
    public void clear() {
        deque.clear();
    }
}
