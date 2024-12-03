/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.io.ole2;

import java.util.NoSuchElementException;

final class SIdChain {
    int[] chain = new int[16];
    int size = 0;
    int next = 0;

    void addSID(int n) {
        this.ensureCapacity();
        this.chain[this.size++] = n;
    }

    private void ensureCapacity() {
        if (this.chain.length == this.size) {
            int[] nArray = new int[this.size << 1];
            System.arraycopy(this.chain, 0, nArray, 0, this.size);
            this.chain = nArray;
        }
    }

    public int[] getChain() {
        int[] nArray = new int[this.size];
        System.arraycopy(this.chain, 0, nArray, 0, this.size);
        return nArray;
    }

    public void reset() {
        this.next = 0;
    }

    public boolean hasNext() {
        return this.next < this.size;
    }

    public int next() {
        if (this.next >= this.size) {
            throw new NoSuchElementException("No element");
        }
        return this.chain[this.next++];
    }

    public int get(int n) {
        return this.chain[n];
    }

    public int length() {
        return this.size;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(this.size * 5);
        stringBuilder.append('[');
        for (int i = 0; i < this.size; ++i) {
            if (i != 0) {
                stringBuilder.append(',');
            }
            stringBuilder.append(this.chain[i]);
        }
        stringBuilder.append(']');
        return stringBuilder.toString();
    }
}

