/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.ServerChannel
 */
package io.netty.channel.unix;

import io.netty.channel.ServerChannel;
import io.netty.channel.unix.DomainSocketAddress;
import io.netty.channel.unix.UnixChannel;

public interface ServerDomainSocketChannel
extends ServerChannel,
UnixChannel {
    public DomainSocketAddress remoteAddress();

    public DomainSocketAddress localAddress();
}

