/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import java.util.Arrays;
import java.util.NoSuchElementException;
import javax.media.jai.JaiI18N;

public class IntegerSequence {
    private int min;
    private int max;
    private static final int DEFAULT_CAPACITY = 16;
    private int[] iArray = null;
    private int capacity = 0;
    private int numElts = 0;
    private boolean isSorted = false;
    private int currentIndex = -1;

    public IntegerSequence(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException(JaiI18N.getString("IntegerSequence1"));
        }
        this.min = min;
        this.max = max;
        this.capacity = 16;
        this.iArray = new int[this.capacity];
        this.numElts = 0;
        this.isSorted = true;
    }

    public IntegerSequence() {
        this(Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public void insert(int element) {
        if (element < this.min || element > this.max) {
            return;
        }
        if (this.numElts >= this.capacity) {
            int newCapacity = 2 * this.capacity;
            int[] newArray = new int[newCapacity];
            System.arraycopy(this.iArray, 0, newArray, 0, this.capacity);
            this.capacity = newCapacity;
            this.iArray = newArray;
        }
        this.isSorted = false;
        this.iArray[this.numElts++] = element;
    }

    public void startEnumeration() {
        if (!this.isSorted) {
            Arrays.sort(this.iArray, 0, this.numElts);
            int readPos = 1;
            int writePos = 1;
            int prevElt = this.iArray[0];
            for (readPos = 1; readPos < this.numElts; ++readPos) {
                int currElt = this.iArray[readPos];
                if (currElt == prevElt) continue;
                this.iArray[writePos++] = currElt;
                prevElt = currElt;
            }
            this.numElts = writePos;
            this.isSorted = true;
        }
        this.currentIndex = 0;
    }

    public boolean hasMoreElements() {
        return this.currentIndex < this.numElts;
    }

    public int nextElement() {
        if (this.currentIndex < this.numElts) {
            return this.iArray[this.currentIndex++];
        }
        throw new NoSuchElementException(JaiI18N.getString("IntegerSequence0"));
    }

    public int getNumElements() {
        return this.numElts;
    }

    public String toString() {
        String s;
        if (this.numElts == 0) {
            s = "[<empty>]";
        } else {
            s = "[";
            this.startEnumeration();
            for (int i = 0; i < this.numElts - 1; ++i) {
                s = s + this.iArray[i];
                s = s + ", ";
            }
            s = s + this.iArray[this.numElts - 1];
            s = s + "]";
        }
        return s;
    }
}

