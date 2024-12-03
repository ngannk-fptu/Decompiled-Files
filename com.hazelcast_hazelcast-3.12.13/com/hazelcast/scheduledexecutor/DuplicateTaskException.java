/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.scheduledexecutor;

import com.hazelcast.core.HazelcastException;

public class DuplicateTaskException
extends HazelcastException {
    public DuplicateTaskException(String msg) {
        super(msg);
    }
}

