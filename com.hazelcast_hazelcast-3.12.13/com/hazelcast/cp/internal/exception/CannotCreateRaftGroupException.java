/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.exception;

import com.hazelcast.core.HazelcastException;

public class CannotCreateRaftGroupException
extends HazelcastException {
    private static final long serialVersionUID = 3849291601278316593L;

    public CannotCreateRaftGroupException(String message) {
        super(message);
    }
}

