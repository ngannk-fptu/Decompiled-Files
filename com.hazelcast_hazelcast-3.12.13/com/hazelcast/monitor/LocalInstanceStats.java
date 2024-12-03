/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.monitor;

import com.hazelcast.internal.management.JsonSerializable;

public interface LocalInstanceStats
extends JsonSerializable {
    public static final long STAT_NOT_AVAILABLE = -99L;

    public long getCreationTime();
}

