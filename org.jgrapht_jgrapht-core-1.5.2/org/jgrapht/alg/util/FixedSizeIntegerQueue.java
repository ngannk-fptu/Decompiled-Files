/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.util;

public final class FixedSizeIntegerQueue {
    private final int[] vs;
    private int i = 0;
    private int n = 0;

    public FixedSizeIntegerQueue(int capacity) {
        assert (capacity > 0);
        this.vs = new int[capacity];
    }

    public void enqueue(int e) {
        assert (this.n < this.vs.length);
        this.vs[this.n++] = e;
    }

    public int poll() {
        assert (!this.isEmpty());
        return this.vs[this.i++];
    }

    public boolean isEmpty() {
        return this.i == this.n;
    }

    public int size() {
        return this.n - this.i;
    }

    public void clear() {
        this.i = 0;
        this.n = 0;
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        for (int j = this.i; j < this.n; ++j) {
            s.append(this.vs[j]).append(" ");
        }
        return s.toString();
    }
}

