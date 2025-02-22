/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.networking;

import com.hazelcast.util.Preconditions;

public final class ChannelOption<T> {
    public static final ChannelOption<Integer> SO_RCVBUF = new ChannelOption("SO_RCVBUF");
    public static final ChannelOption<Integer> SO_SNDBUF = new ChannelOption("SO_SNDBUF");
    public static final ChannelOption<Boolean> SO_KEEPALIVE = new ChannelOption("SO_KEEPALIVE");
    public static final ChannelOption<Integer> SO_LINGER = new ChannelOption("SO_LINGER");
    public static final ChannelOption<Integer> SO_TIMEOUT = new ChannelOption("SO_TIMEOUT");
    public static final ChannelOption<Boolean> SO_REUSEADDR = new ChannelOption("SO_REUSEADDR");
    public static final ChannelOption<Boolean> TCP_NODELAY = new ChannelOption("TCP_NODELAY");
    public static final ChannelOption<Boolean> DIRECT_BUF = new ChannelOption("DIRECT_BUF");
    private final String name;

    public ChannelOption(String name) {
        this.name = Preconditions.checkNotNull(name, "name can't be null");
    }

    public String name() {
        return this.name;
    }

    public String toString() {
        return this.name;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ChannelOption that = (ChannelOption)o;
        return this.name.equals(that.name);
    }

    public int hashCode() {
        return this.name.hashCode();
    }
}

