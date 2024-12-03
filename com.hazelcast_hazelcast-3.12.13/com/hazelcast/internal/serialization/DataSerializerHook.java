/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.serialization;

import com.hazelcast.nio.serialization.DataSerializableFactory;

public interface DataSerializerHook {
    public static final int F_ID_OFFSET_WEBMODULE = -1000;
    public static final int F_ID_OFFSET_HIBERNATE = -2000;

    public int getFactoryId();

    public DataSerializableFactory createFactory();
}

