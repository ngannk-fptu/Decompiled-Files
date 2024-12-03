/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.coalesce;

public interface CoalesceChecker {
    public boolean checkCoalesce(Object var1, Object var2);

    public int coalesceHash(Object var1);
}

