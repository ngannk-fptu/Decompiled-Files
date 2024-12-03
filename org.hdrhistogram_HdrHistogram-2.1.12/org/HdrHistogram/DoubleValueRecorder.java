/*
 * Decompiled with CFR 0.152.
 */
package org.HdrHistogram;

public interface DoubleValueRecorder {
    public void recordValue(double var1) throws ArrayIndexOutOfBoundsException;

    public void recordValueWithCount(double var1, long var3) throws ArrayIndexOutOfBoundsException;

    public void recordValueWithExpectedInterval(double var1, double var3) throws ArrayIndexOutOfBoundsException;

    public void reset();
}

