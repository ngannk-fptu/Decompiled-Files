/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management.events;

import com.hazelcast.internal.management.events.Event;
import com.hazelcast.util.Clock;

public abstract class AbstractEventBase
implements Event {
    private final long timestamp = Clock.currentTimeMillis();

    AbstractEventBase() {
    }

    @Override
    public long getTimestamp() {
        return this.timestamp;
    }
}

