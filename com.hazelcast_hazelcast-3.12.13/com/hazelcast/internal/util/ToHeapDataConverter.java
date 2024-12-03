/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.util;

import com.hazelcast.internal.serialization.impl.HeapData;
import com.hazelcast.nio.serialization.Data;

public final class ToHeapDataConverter {
    private ToHeapDataConverter() {
    }

    public static Data toHeapData(Data data) {
        if (data == null || data instanceof HeapData) {
            return data;
        }
        return new HeapData(data.toByteArray());
    }
}

