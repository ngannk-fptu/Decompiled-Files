/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

import com.hazelcast.core.HazelcastException;

public class ConsistencyLostException
extends HazelcastException {
    public ConsistencyLostException(String msg) {
        super(msg);
    }
}

