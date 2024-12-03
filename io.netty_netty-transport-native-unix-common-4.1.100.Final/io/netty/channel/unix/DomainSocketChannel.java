/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.socket.DuplexChannel
 */
package io.netty.channel.unix;

import io.netty.channel.socket.DuplexChannel;
import io.netty.channel.unix.DomainSocketAddress;
import io.netty.channel.unix.DomainSocketChannelConfig;
import io.netty.channel.unix.UnixChannel;

public interface DomainSocketChannel
extends UnixChannel,
DuplexChannel {
    public DomainSocketAddress remoteAddress();

    public DomainSocketAddress localAddress();

    public DomainSocketChannelConfig config();
}

