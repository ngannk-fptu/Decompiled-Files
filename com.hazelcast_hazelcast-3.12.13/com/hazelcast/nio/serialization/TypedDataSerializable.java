/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio.serialization;

import com.hazelcast.nio.serialization.DataSerializable;

public interface TypedDataSerializable
extends DataSerializable {
    public Class getClassType();
}

