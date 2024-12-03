/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.flakeidgen;

import com.hazelcast.core.IdGenerator;

public interface FlakeIdGenerator
extends IdGenerator {
    @Override
    public long newId();

    @Override
    @Deprecated
    public boolean init(long var1);
}

