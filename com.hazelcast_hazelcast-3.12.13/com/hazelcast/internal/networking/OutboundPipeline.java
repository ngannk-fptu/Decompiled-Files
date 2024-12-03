/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.networking;

import com.hazelcast.internal.networking.OutboundHandler;

public interface OutboundPipeline {
    public OutboundPipeline addLast(OutboundHandler ... var1);

    public OutboundPipeline replace(OutboundHandler var1, OutboundHandler ... var2);

    public OutboundPipeline remove(OutboundHandler var1);

    public OutboundPipeline wakeup();
}

