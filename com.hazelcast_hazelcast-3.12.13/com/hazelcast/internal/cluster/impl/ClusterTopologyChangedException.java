/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.cluster.impl;

import com.hazelcast.spi.exception.RetryableHazelcastException;

public class ClusterTopologyChangedException
extends RetryableHazelcastException {
    public ClusterTopologyChangedException() {
    }

    public ClusterTopologyChangedException(String message) {
        super(message);
    }
}

