/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.cluster.impl;

import com.hazelcast.core.HazelcastException;

public class VersionMismatchException
extends HazelcastException {
    public VersionMismatchException(String message) {
        super(message);
    }
}

