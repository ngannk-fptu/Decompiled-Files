/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.crdt;

import com.hazelcast.core.HazelcastException;

public class MutationDisallowedException
extends HazelcastException {
    public MutationDisallowedException(String message) {
        super(message);
    }
}

