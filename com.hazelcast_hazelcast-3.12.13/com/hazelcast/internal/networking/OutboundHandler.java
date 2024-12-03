/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.networking;

import com.hazelcast.internal.networking.ChannelHandler;
import com.hazelcast.internal.networking.ChannelOption;
import com.hazelcast.internal.networking.ChannelOptions;
import com.hazelcast.internal.networking.HandlerStatus;
import com.hazelcast.nio.IOUtil;
import java.nio.ByteBuffer;

public abstract class OutboundHandler<S, D>
extends ChannelHandler<OutboundHandler, S, D> {
    public abstract HandlerStatus onWrite() throws Exception;

    protected final void initDstBuffer() {
        this.initDstBuffer(this.channel.options().getOption(ChannelOption.SO_SNDBUF));
    }

    protected final void initDstBuffer(int sizeBytes) {
        this.initDstBuffer(sizeBytes, null);
    }

    protected final void initDstBuffer(int sizeBytes, byte[] bytes) {
        if (bytes != null && bytes.length > sizeBytes) {
            throw new IllegalArgumentException("Buffer overflow. Can't initialize dstBuffer for " + this + " and channel" + this.channel + " because too many bytes, sizeBytes " + sizeBytes + ". bytes.length " + bytes.length);
        }
        ChannelOptions config = this.channel.options();
        ByteBuffer buffer = IOUtil.newByteBuffer(sizeBytes, config.getOption(ChannelOption.DIRECT_BUF));
        if (bytes != null) {
            buffer.put(bytes);
        }
        buffer.flip();
        this.dst = buffer;
    }
}

