/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi;

import com.hazelcast.spi.Operation;

public interface PreJoinAwareService {
    public Operation getPreJoinOperation();
}

