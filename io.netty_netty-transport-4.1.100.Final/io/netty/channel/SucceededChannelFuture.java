/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.util.concurrent.EventExecutor
 */
package io.netty.channel;

import io.netty.channel.Channel;
import io.netty.channel.CompleteChannelFuture;
import io.netty.util.concurrent.EventExecutor;

final class SucceededChannelFuture
extends CompleteChannelFuture {
    SucceededChannelFuture(Channel channel, EventExecutor executor) {
        super(channel, executor);
    }

    public Throwable cause() {
        return null;
    }

    public boolean isSuccess() {
        return true;
    }
}

