/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.durableexecutor;

import com.hazelcast.core.HazelcastException;
import com.hazelcast.spi.exception.SilentException;

public class StaleTaskIdException
extends HazelcastException
implements SilentException {
    public StaleTaskIdException(String message) {
        super(message);
    }
}

