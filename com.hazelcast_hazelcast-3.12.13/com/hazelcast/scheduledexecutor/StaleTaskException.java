/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.scheduledexecutor;

import com.hazelcast.core.HazelcastException;
import com.hazelcast.spi.exception.SilentException;

public class StaleTaskException
extends HazelcastException
implements SilentException {
    public StaleTaskException(String msg) {
        super(msg);
    }
}

