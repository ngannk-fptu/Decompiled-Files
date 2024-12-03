/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.durableexecutor.impl;

import com.hazelcast.durableexecutor.StaleTaskIdException;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.Callable;
import java.util.concurrent.RejectedExecutionException;

public class TaskRingBuffer {
    private Object[] ringItems;
    private int[] sequences;
    private boolean[] isTask;
    private int head = -1;
    private int callableCounter;

    public TaskRingBuffer() {
    }

    public TaskRingBuffer(int capacity) {
        this.ringItems = new Object[capacity];
        this.isTask = new boolean[capacity];
        this.sequences = new int[capacity];
    }

    public int add(Callable task) {
        int index = this.findEmptySpot();
        ++this.callableCounter;
        this.ringItems[index] = task;
        this.isTask[index] = true;
        this.sequences[index] = this.head;
        return this.head;
    }

    private int findEmptySpot() {
        if (this.callableCounter == this.ringItems.length) {
            throw new RejectedExecutionException("Capacity (" + this.ringItems.length + ") is reached!");
        }
        for (Object ringItem : this.ringItems) {
            ++this.head;
            int index = this.toIndex(this.head);
            if (this.isTask[index]) continue;
            return index;
        }
        throw new IllegalStateException();
    }

    public void remove(int sequence) {
        int index = this.toIndex(sequence);
        this.ringItems[index] = null;
        this.isTask[index] = false;
        --this.head;
        --this.callableCounter;
    }

    void putBackup(int sequence, Callable task) {
        this.head = Math.max(this.head, sequence);
        ++this.callableCounter;
        int index = this.toIndex(sequence);
        this.ringItems[index] = task;
        this.isTask[index] = true;
        this.sequences[index] = sequence;
    }

    void replaceTaskWithResult(int sequence, Object response) {
        int index = this.toIndex(sequence);
        if (this.sequences[index] != sequence) {
            return;
        }
        this.ringItems[index] = response;
        this.isTask[index] = false;
        --this.callableCounter;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    Object retrieveAndDispose(int sequence) {
        int index = this.toIndex(sequence);
        this.checkSequence(index, sequence);
        try {
            Object object = this.ringItems[index];
            return object;
        }
        finally {
            this.ringItems[index] = null;
            this.isTask[index] = false;
            --this.head;
        }
    }

    public void dispose(int sequence) {
        int index = this.toIndex(sequence);
        this.checkSequence(index, sequence);
        if (this.isTask[index]) {
            --this.callableCounter;
        }
        this.ringItems[index] = null;
        this.isTask[index] = false;
    }

    public Object retrieve(int sequence) {
        int index = this.toIndex(sequence);
        this.checkSequence(index, sequence);
        return this.ringItems[index];
    }

    boolean isTask(int sequence) {
        int index = this.toIndex(sequence);
        this.checkSequence(index, sequence);
        return this.isTask[index];
    }

    private void checkSequence(int index, int sequence) {
        if (this.sequences[index] != sequence) {
            throw new StaleTaskIdException("The sequence has been overwritten");
        }
    }

    private int toIndex(int sequence) {
        return Math.abs(sequence % this.ringItems.length);
    }

    public void write(ObjectDataOutput out) throws IOException {
        out.writeInt(this.head);
        out.writeInt(this.ringItems.length);
        for (int i = 0; i < this.ringItems.length; ++i) {
            out.writeBoolean(this.isTask[i]);
            out.writeInt(this.sequences[i]);
            out.writeObject(this.ringItems[i]);
        }
    }

    public void read(ObjectDataInput in) throws IOException {
        this.head = in.readInt();
        int length = in.readInt();
        this.ringItems = new Object[length];
        this.isTask = new boolean[length];
        this.sequences = new int[length];
        for (int i = 0; i < length; ++i) {
            this.isTask[i] = in.readBoolean();
            this.sequences[i] = in.readInt();
            this.ringItems[i] = in.readObject();
        }
    }

    public DurableIterator iterator() {
        return new DurableIterator();
    }

    public class DurableIterator
    implements Iterator {
        int index = -1;

        @Override
        public boolean hasNext() {
            return this.index + 1 < TaskRingBuffer.this.ringItems.length;
        }

        public Object next() {
            if (++this.index == TaskRingBuffer.this.ringItems.length) {
                throw new NoSuchElementException();
            }
            return TaskRingBuffer.this.ringItems[this.index];
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        public int getSequence() {
            return TaskRingBuffer.this.sequences[this.index];
        }

        public boolean isTask() {
            return TaskRingBuffer.this.isTask[this.index];
        }
    }
}

