/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal.diagnostics;

import java.time.Duration;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Supplier;

class SlidingWindowSeries {
    private final long window;
    private final Deque<SeriesItem> items;
    private final Supplier<Long> timeSource;
    private static final int QUEUE_SIZE_LIMIT = 1000;

    SlidingWindowSeries(Supplier<Long> timeSource, Duration window) {
        this.timeSource = timeSource;
        this.window = window.toNanos();
        this.items = new ConcurrentLinkedDeque<SeriesItem>();
    }

    public void add(long value) {
        if (this.items.size() >= 1000) {
            return;
        }
        long now = this.timeSource.get();
        this.items.addLast(new SeriesItem(value, now));
    }

    public void removeOldElements() {
        SeriesItem first;
        long now = this.timeSource.get();
        while ((first = this.items.peekFirst()) != null && now - first.getTime() > this.window) {
            this.items.pollFirst();
        }
    }

    public int size() {
        return this.items.size();
    }

    public long sumValue() {
        long now = this.timeSource.get();
        long result = 0L;
        for (SeriesItem item : this.items) {
            if (now - item.getTime() > this.window) continue;
            result += item.getValue();
        }
        return result;
    }

    public long getDistance() {
        SeriesItem first = this.items.peekFirst();
        SeriesItem last = this.items.peekLast();
        return first == null || last == null ? 0L : last.getValue() - first.getValue();
    }

    public long getWindow() {
        return this.window;
    }

    public void clear() {
        this.items.clear();
    }

    private static class SeriesItem {
        private final long value;
        private final long time;

        SeriesItem(long value, long time) {
            this.value = value;
            this.time = time;
        }

        public long getValue() {
            return this.value;
        }

        public long getTime() {
            return this.time;
        }
    }
}

