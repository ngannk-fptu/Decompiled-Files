/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl.operation;

public interface MutableOperation {
    public static final int IGNORE_COMPLETION = -1;

    public int getCompletionId();

    public void setCompletionId(int var1);
}

