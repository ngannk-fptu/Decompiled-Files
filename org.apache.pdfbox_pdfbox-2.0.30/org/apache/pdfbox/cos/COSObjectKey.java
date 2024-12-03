/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.cos;

import org.apache.pdfbox.cos.COSObject;

public class COSObjectKey
implements Comparable<COSObjectKey> {
    private final long number;
    private int generation;

    public COSObjectKey(COSObject object) {
        this(object.getObjectNumber(), object.getGenerationNumber());
    }

    public COSObjectKey(long num, int gen) {
        this.number = num;
        this.generation = gen;
    }

    public boolean equals(Object obj) {
        COSObjectKey objToBeCompared = obj instanceof COSObjectKey ? (COSObjectKey)obj : null;
        return objToBeCompared != null && objToBeCompared.getNumber() == this.getNumber() && objToBeCompared.getGeneration() == this.getGeneration();
    }

    public int getGeneration() {
        return this.generation;
    }

    public void fixGeneration(int genNumber) {
        this.generation = genNumber;
    }

    public long getNumber() {
        return this.number;
    }

    public int hashCode() {
        return Long.valueOf((this.number << 4) + (long)this.generation).hashCode();
    }

    public String toString() {
        return this.number + " " + this.generation + " R";
    }

    @Override
    public int compareTo(COSObjectKey other) {
        if (this.getNumber() < other.getNumber()) {
            return -1;
        }
        if (this.getNumber() > other.getNumber()) {
            return 1;
        }
        if (this.getGeneration() < other.getGeneration()) {
            return -1;
        }
        if (this.getGeneration() > other.getGeneration()) {
            return 1;
        }
        return 0;
    }
}

