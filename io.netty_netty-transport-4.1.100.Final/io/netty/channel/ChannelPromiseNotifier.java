/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.util.concurrent.Promise
 *  io.netty.util.concurrent.PromiseNotifier
 */
package io.netty.channel;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelPromise;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.PromiseNotifier;

@Deprecated
public final class ChannelPromiseNotifier
extends PromiseNotifier<Void, ChannelFuture>
implements ChannelFutureListener {
    public ChannelPromiseNotifier(ChannelPromise ... promises) {
        super((Promise[])promises);
    }

    public ChannelPromiseNotifier(boolean logNotifyFailure, ChannelPromise ... promises) {
        super(logNotifyFailure, (Promise[])promises);
    }
}

