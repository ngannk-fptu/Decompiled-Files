/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.Channel
 */
package io.netty.channel.unix;

import io.netty.channel.Channel;
import io.netty.channel.unix.DomainDatagramChannelConfig;
import io.netty.channel.unix.DomainSocketAddress;
import io.netty.channel.unix.UnixChannel;

public interface DomainDatagramChannel
extends UnixChannel,
Channel {
    public DomainDatagramChannelConfig config();

    public boolean isConnected();

    public DomainSocketAddress localAddress();

    public DomainSocketAddress remoteAddress();
}

