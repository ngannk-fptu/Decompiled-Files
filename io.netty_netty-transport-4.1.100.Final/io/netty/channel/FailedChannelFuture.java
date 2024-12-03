/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.util.concurrent.EventExecutor
 *  io.netty.util.internal.ObjectUtil
 *  io.netty.util.internal.PlatformDependent
 */
package io.netty.channel;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.CompleteChannelFuture;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;

final class FailedChannelFuture
extends CompleteChannelFuture {
    private final Throwable cause;

    FailedChannelFuture(Channel channel, EventExecutor executor, Throwable cause) {
        super(channel, executor);
        this.cause = (Throwable)ObjectUtil.checkNotNull((Object)cause, (String)"cause");
    }

    public Throwable cause() {
        return this.cause;
    }

    public boolean isSuccess() {
        return false;
    }

    @Override
    public ChannelFuture sync() {
        PlatformDependent.throwException((Throwable)this.cause);
        return this;
    }

    @Override
    public ChannelFuture syncUninterruptibly() {
        PlatformDependent.throwException((Throwable)this.cause);
        return this;
    }
}

