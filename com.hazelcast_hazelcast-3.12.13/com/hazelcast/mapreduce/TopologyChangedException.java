/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce;

import com.hazelcast.core.HazelcastException;

@Deprecated
public class TopologyChangedException
extends HazelcastException {
    public TopologyChangedException() {
    }

    public TopologyChangedException(String message) {
        super(message);
    }
}

