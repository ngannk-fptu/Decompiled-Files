/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.networking;

import com.hazelcast.internal.networking.ChannelHandler;
import com.hazelcast.internal.networking.ChannelOption;
import com.hazelcast.internal.networking.ChannelOptions;
import com.hazelcast.internal.networking.HandlerStatus;
import com.hazelcast.nio.IOUtil;

public abstract class InboundHandler<S, D>
extends ChannelHandler<InboundHandler, S, D> {
    public abstract HandlerStatus onRead() throws Exception;

    protected final void initSrcBuffer() {
        this.initSrcBuffer(this.channel.options().getOption(ChannelOption.SO_RCVBUF));
    }

    protected final void initSrcBuffer(int sizeBytes) {
        ChannelOptions config = this.channel.options();
        this.src = IOUtil.newByteBuffer(sizeBytes, config.getOption(ChannelOption.DIRECT_BUF));
    }
}

