/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.statistics.archive;

import java.util.Arrays;

public class CircularBuffer<E> {
    private final E[] buffer;
    private int writeIndex;
    private int size;

    public CircularBuffer(int size) {
        this.buffer = new Object[size];
    }

    public int capacity() {
        return this.buffer.length;
    }

    public synchronized E insert(E object) {
        E old = this.buffer[this.writeIndex];
        this.buffer[this.writeIndex] = object;
        ++this.writeIndex;
        this.size = Math.max(this.writeIndex, this.size);
        this.writeIndex %= this.buffer.length;
        return old;
    }

    public synchronized <T> T[] toArray(Class<T[]> type) {
        if (this.size < this.buffer.length) {
            return Arrays.copyOfRange(this.buffer, 0, this.writeIndex, type);
        }
        T[] copy = Arrays.copyOfRange(this.buffer, this.writeIndex, this.writeIndex + this.buffer.length, type);
        System.arraycopy(this.buffer, 0, copy, this.buffer.length - this.writeIndex, this.writeIndex);
        return copy;
    }
}

