/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

import com.hazelcast.core.DistributedObject;

@Deprecated
public interface IdGenerator
extends DistributedObject {
    public boolean init(long var1);

    public long newId();
}

