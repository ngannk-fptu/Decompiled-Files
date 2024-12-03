/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.rest.common.expand.parameter;

import java.util.SortedSet;

public interface Indexes {
    public boolean isRange();

    public int getMinIndex(int var1);

    public int getMaxIndex(int var1);

    public boolean contains(int var1, int var2);

    public SortedSet<Integer> getIndexes(int var1);
}

