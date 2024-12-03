/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.util.concurrent.OrderedEventExecutor
 */
package io.netty.channel;

import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.OrderedEventExecutor;

public interface EventLoop
extends OrderedEventExecutor,
EventLoopGroup {
    public EventLoopGroup parent();
}

