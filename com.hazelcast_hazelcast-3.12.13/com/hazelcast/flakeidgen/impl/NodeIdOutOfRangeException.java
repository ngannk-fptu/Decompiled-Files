/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.flakeidgen.impl;

import com.hazelcast.core.HazelcastException;

public class NodeIdOutOfRangeException
extends HazelcastException {
    public NodeIdOutOfRangeException(String message) {
        super(message);
    }
}

