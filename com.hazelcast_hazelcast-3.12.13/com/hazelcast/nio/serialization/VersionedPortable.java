/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio.serialization;

import com.hazelcast.nio.serialization.Portable;

public interface VersionedPortable
extends Portable {
    public int getClassVersion();
}

