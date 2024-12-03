/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.util.collection;

public interface LongCursor {
    public boolean advance();

    public long value();

    public void reset();
}

