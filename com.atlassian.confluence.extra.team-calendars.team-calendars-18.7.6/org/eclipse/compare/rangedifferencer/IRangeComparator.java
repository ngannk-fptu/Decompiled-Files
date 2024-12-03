/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.compare.rangedifferencer;

public interface IRangeComparator {
    public int getRangeCount();

    public boolean rangesEqual(int var1, IRangeComparator var2, int var3);

    public boolean skipRangeComparison(int var1, int var2, IRangeComparator var3);
}

