/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.exception;

import com.hazelcast.core.HazelcastException;

public class MaxMessageSizeExceeded
extends HazelcastException {
    public MaxMessageSizeExceeded() {
        super("The size of the message exceeds the maximum value of 2147483647 bytes.");
    }
}

