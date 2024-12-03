/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

class HistogramHash {
    int capacity;
    int[] colors;
    int[] counts;
    int size;
    int hashsize;
    boolean packed = false;
    int[] newColors;
    int[] newCounts;

    public HistogramHash(int capacity) {
        this.capacity = capacity;
        this.hashsize = capacity * 4 / 3;
        this.colors = new int[this.hashsize];
        this.counts = new int[this.hashsize];
    }

    void init() {
        this.size = 0;
        this.packed = false;
        for (int i = 0; i < this.hashsize; ++i) {
            this.colors[i] = -1;
            this.counts[i] = 0;
        }
    }

    boolean insert(int node) {
        int hashPos = this.hashCode(node);
        if (this.colors[hashPos] == -1) {
            this.colors[hashPos] = node;
            int n = hashPos;
            this.counts[n] = this.counts[n] + 1;
            ++this.size;
            return this.size <= this.capacity;
        }
        if (this.colors[hashPos] == node) {
            int n = hashPos;
            this.counts[n] = this.counts[n] + 1;
            return this.size <= this.capacity;
        }
        for (int next = hashPos + 1; next != hashPos; ++next) {
            if (this.colors[next %= this.hashsize] == -1) {
                this.colors[next] = node;
                int n = next;
                this.counts[n] = this.counts[n] + 1;
                ++this.size;
                return this.size <= this.capacity;
            }
            if (this.colors[next] != node) continue;
            int n = next;
            this.counts[n] = this.counts[n] + 1;
            return this.size <= this.capacity;
        }
        return this.size <= this.capacity;
    }

    boolean isFull() {
        return this.size > this.capacity;
    }

    void put(int node, int value) {
        int hashPos = this.hashCode(node);
        if (this.colors[hashPos] == -1) {
            this.colors[hashPos] = node;
            this.counts[hashPos] = value;
            ++this.size;
            return;
        }
        if (this.colors[hashPos] == node) {
            this.counts[hashPos] = value;
            return;
        }
        for (int next = hashPos + 1; next != hashPos; ++next) {
            if (this.colors[next %= this.hashsize] == -1) {
                this.colors[next] = node;
                this.counts[next] = value;
                ++this.size;
                return;
            }
            if (this.colors[next] != node) continue;
            this.counts[next] = value;
            return;
        }
    }

    int get(int node) {
        int hashPos = this.hashCode(node);
        if (this.colors[hashPos] == node) {
            return this.counts[hashPos];
        }
        for (int next = hashPos + 1; next != hashPos; ++next) {
            if (this.colors[next %= this.hashsize] != node) continue;
            return this.counts[next];
        }
        return -1;
    }

    int[] getCounts() {
        if (!this.packed) {
            this.pack();
        }
        return this.newCounts;
    }

    int[] getColors() {
        if (!this.packed) {
            this.pack();
        }
        return this.newColors;
    }

    void pack() {
        this.newColors = new int[this.capacity];
        this.newCounts = new int[this.capacity];
        int j = 0;
        for (int i = 0; i < this.hashsize; ++i) {
            if (this.colors[i] == -1) continue;
            this.newColors[j] = this.colors[i];
            this.newCounts[j] = this.counts[i];
            ++j;
        }
        this.packed = true;
    }

    int hashCode(int value) {
        return ((value >> 16) * 33023 + (value >> 8 & 0xFF) * 30013 + (value & 0xFF) * 27011) % this.hashsize;
    }
}

