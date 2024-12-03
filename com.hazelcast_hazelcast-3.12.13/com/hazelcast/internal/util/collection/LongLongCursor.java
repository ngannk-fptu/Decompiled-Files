/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.util.collection;

public interface LongLongCursor {
    public boolean advance();

    public long key();

    public long value();
}

