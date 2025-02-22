/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.networking.nio;

import com.hazelcast.internal.networking.InboundHandler;
import com.hazelcast.internal.util.counters.Counter;

public abstract class InboundHandlerWithCounters<S, D>
extends InboundHandler<S, D> {
    protected Counter normalPacketsRead;
    protected Counter priorityPacketsRead;

    public void setNormalPacketsRead(Counter normalPacketsRead) {
        this.normalPacketsRead = normalPacketsRead;
    }

    public void setPriorityPacketsRead(Counter priorityPacketsRead) {
        this.priorityPacketsRead = priorityPacketsRead;
    }
}

