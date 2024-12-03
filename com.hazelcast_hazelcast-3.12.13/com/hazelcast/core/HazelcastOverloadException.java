/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

import com.hazelcast.core.HazelcastException;

public class HazelcastOverloadException
extends HazelcastException {
    public HazelcastOverloadException(String message) {
        super(message);
    }

    public HazelcastOverloadException(String message, Throwable cause) {
        super(message, cause);
    }
}

