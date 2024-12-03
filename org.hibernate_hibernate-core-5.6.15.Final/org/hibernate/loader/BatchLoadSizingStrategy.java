/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader;

public interface BatchLoadSizingStrategy {
    public int determineOptimalBatchLoadSize(int var1, int var2);
}

