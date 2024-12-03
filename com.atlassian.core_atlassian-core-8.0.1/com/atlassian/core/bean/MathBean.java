/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.core.bean;

public class MathBean {
    public int getPercentageWidth(int numberOfColumns, int column) {
        int columnWidth = this.divide(100, numberOfColumns);
        if (numberOfColumns == column) {
            return this.subtract(100, this.multiply(columnWidth, this.subtract(numberOfColumns, 1)));
        }
        return columnWidth;
    }

    public long getPercentage(double portion, double total) {
        long columnWidth = Math.round(portion / total * 100.0);
        return columnWidth;
    }

    public long getPercentage(long portion, long total) {
        long columnWidth = Math.round((double)portion / (double)total * 100.0);
        return columnWidth;
    }

    public int add(int i1, int i2) {
        return i1 + i2;
    }

    public int subtract(int i1, int i2) {
        return i1 - i2;
    }

    public long substract(long i1, long i2) {
        return i1 - i2;
    }

    public int multiply(int i1, int i2) {
        return i1 * i2;
    }

    public int divide(int i1, int i2) {
        return i1 / i2;
    }

    public long divide(long l1, long l2) {
        return l1 / l2;
    }

    public long max(long l1, long l2) {
        return Math.max(l1, l2);
    }

    public long min(long l1, long l2) {
        return Math.min(l1, l2);
    }
}

