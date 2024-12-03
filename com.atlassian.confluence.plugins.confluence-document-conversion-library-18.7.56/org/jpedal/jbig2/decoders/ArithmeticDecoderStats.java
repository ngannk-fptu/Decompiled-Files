/*
 * Decompiled with CFR 0.152.
 */
package org.jpedal.jbig2.decoders;

public class ArithmeticDecoderStats {
    private int contextSize;
    private int[] codingContextTable;

    public ArithmeticDecoderStats(int n) {
        this.contextSize = n;
        this.codingContextTable = new int[n];
        this.reset();
    }

    public void reset() {
        for (int i = 0; i < this.contextSize; ++i) {
            this.codingContextTable[i] = 0;
        }
    }

    public void setEntry(int n, int n2, int n3) {
        this.codingContextTable[n] = (n2 << n2) + n3;
    }

    public int getContextCodingTableValue(int n) {
        return this.codingContextTable[n];
    }

    public void setContextCodingTableValue(int n, int n2) {
        this.codingContextTable[n] = n2;
    }

    public int getContextSize() {
        return this.contextSize;
    }

    public void overwrite(ArithmeticDecoderStats arithmeticDecoderStats) {
        for (int i = 0; i < this.contextSize; ++i) {
            this.codingContextTable[i] = arithmeticDecoderStats.codingContextTable[i];
        }
    }

    public ArithmeticDecoderStats copy() {
        ArithmeticDecoderStats arithmeticDecoderStats = new ArithmeticDecoderStats(this.contextSize);
        for (int i = 0; i < this.contextSize; ++i) {
            arithmeticDecoderStats.codingContextTable[i] = this.codingContextTable[i];
        }
        return arithmeticDecoderStats;
    }
}

