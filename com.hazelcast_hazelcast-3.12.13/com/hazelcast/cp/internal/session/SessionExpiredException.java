/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.session;

import com.hazelcast.core.HazelcastException;

public class SessionExpiredException
extends HazelcastException {
    public SessionExpiredException() {
    }

    public SessionExpiredException(String message) {
        super(message);
    }

    public SessionExpiredException(String message, Throwable cause) {
        super(message, cause);
    }
}

