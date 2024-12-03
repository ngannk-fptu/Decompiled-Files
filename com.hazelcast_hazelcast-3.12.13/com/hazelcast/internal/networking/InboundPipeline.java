/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.networking;

import com.hazelcast.internal.networking.InboundHandler;

public interface InboundPipeline {
    public InboundPipeline addLast(InboundHandler ... var1);

    public InboundPipeline replace(InboundHandler var1, InboundHandler ... var2);

    public InboundPipeline remove(InboundHandler var1);

    public InboundPipeline wakeup();
}

