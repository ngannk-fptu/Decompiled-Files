/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

import com.hazelcast.core.HazelcastException;

public class RuntimeInterruptedException
extends HazelcastException {
    public RuntimeInterruptedException() {
    }

    public RuntimeInterruptedException(String message) {
        super(message);
    }
}

