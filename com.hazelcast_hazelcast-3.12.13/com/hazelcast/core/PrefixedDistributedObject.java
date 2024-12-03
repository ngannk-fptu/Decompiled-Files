/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

import com.hazelcast.core.DistributedObject;

public interface PrefixedDistributedObject
extends DistributedObject {
    public String getPrefixedName();
}

