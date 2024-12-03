/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

public class HazelcastInstanceNotActiveException
extends IllegalStateException {
    public HazelcastInstanceNotActiveException() {
        super("Hazelcast instance is not active!");
    }

    public HazelcastInstanceNotActiveException(String message) {
        super(message);
    }
}

