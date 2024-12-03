/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi;

import com.hazelcast.spi.Operation;

public interface PostJoinAwareService {
    public Operation getPostJoinOperation();
}

