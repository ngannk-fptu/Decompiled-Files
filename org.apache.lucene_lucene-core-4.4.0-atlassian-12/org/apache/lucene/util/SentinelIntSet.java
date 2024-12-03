/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util;

import java.util.Arrays;
import org.apache.lucene.util.BitUtil;

public class SentinelIntSet {
    public int[] keys;
    public int count;
    public final int emptyVal;
    public int rehashCount;

    public SentinelIntSet(int size, int emptyVal) {
        this.emptyVal = emptyVal;
        int tsize = Math.max(BitUtil.nextHighestPowerOfTwo(size), 1);
        this.rehashCount = tsize - (tsize >> 2);
        if (size >= this.rehashCount) {
            this.rehashCount = (tsize <<= 1) - (tsize >> 2);
        }
        this.keys = new int[tsize];
        if (emptyVal != 0) {
            this.clear();
        }
    }

    public void clear() {
        Arrays.fill(this.keys, this.emptyVal);
        this.count = 0;
    }

    public int hash(int key) {
        return key;
    }

    public int size() {
        return this.count;
    }

    public int getSlot(int key) {
        assert (key != this.emptyVal);
        int h = this.hash(key);
        int s = h & this.keys.length - 1;
        if (this.keys[s] == key || this.keys[s] == this.emptyVal) {
            return s;
        }
        int increment = h >> 7 | 1;
        while (this.keys[s = s + increment & this.keys.length - 1] != key && this.keys[s] != this.emptyVal) {
        }
        return s;
    }

    public int find(int key) {
        assert (key != this.emptyVal);
        int h = this.hash(key);
        int s = h & this.keys.length - 1;
        if (this.keys[s] == key) {
            return s;
        }
        if (this.keys[s] == this.emptyVal) {
            return -s - 1;
        }
        int increment = h >> 7 | 1;
        do {
            if (this.keys[s = s + increment & this.keys.length - 1] != key) continue;
            return s;
        } while (this.keys[s] != this.emptyVal);
        return -s - 1;
    }

    public boolean exists(int key) {
        return this.find(key) >= 0;
    }

    public int put(int key) {
        int s = this.find(key);
        if (s < 0) {
            ++this.count;
            if (this.count >= this.rehashCount) {
                this.rehash();
                s = this.getSlot(key);
            } else {
                s = -s - 1;
            }
            this.keys[s] = key;
        }
        return s;
    }

    public void rehash() {
        int newSize = this.keys.length << 1;
        int[] oldKeys = this.keys;
        this.keys = new int[newSize];
        if (this.emptyVal != 0) {
            Arrays.fill(this.keys, this.emptyVal);
        }
        for (int key : oldKeys) {
            if (key == this.emptyVal) continue;
            int newSlot = this.getSlot(key);
            this.keys[newSlot] = key;
        }
        this.rehashCount = newSize - (newSize >> 2);
    }
}

