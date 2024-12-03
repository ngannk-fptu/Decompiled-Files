/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.core.HazelcastException;

public class EntryOffloadableLockMismatchException
extends HazelcastException {
    public EntryOffloadableLockMismatchException(String message) {
        super(message);
    }
}

