/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.wan;

import com.hazelcast.core.HazelcastException;

public class WANReplicationQueueFullException
extends HazelcastException {
    public WANReplicationQueueFullException(String message) {
        super(message);
    }
}

