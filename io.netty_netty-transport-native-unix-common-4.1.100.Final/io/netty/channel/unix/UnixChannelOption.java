/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.ChannelOption
 */
package io.netty.channel.unix;

import io.netty.channel.ChannelOption;
import io.netty.channel.unix.DomainSocketReadMode;

public class UnixChannelOption<T>
extends ChannelOption<T> {
    public static final ChannelOption<Boolean> SO_REUSEPORT = UnixChannelOption.valueOf(UnixChannelOption.class, (String)"SO_REUSEPORT");
    public static final ChannelOption<DomainSocketReadMode> DOMAIN_SOCKET_READ_MODE = ChannelOption.valueOf(UnixChannelOption.class, (String)"DOMAIN_SOCKET_READ_MODE");

    protected UnixChannelOption() {
        super(null);
    }

    UnixChannelOption(String name) {
        super(name);
    }
}

