/*
 * Decompiled with CFR 0.152.
 */
package org.HdrHistogram;

public interface ValueRecorder {
    public void recordValue(long var1) throws ArrayIndexOutOfBoundsException;

    public void recordValueWithCount(long var1, long var3) throws ArrayIndexOutOfBoundsException;

    public void recordValueWithExpectedInterval(long var1, long var3) throws ArrayIndexOutOfBoundsException;

    public void reset();
}

