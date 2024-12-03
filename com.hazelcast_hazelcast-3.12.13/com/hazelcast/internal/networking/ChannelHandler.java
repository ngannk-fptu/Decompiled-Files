/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.networking;

import com.hazelcast.internal.networking.Channel;

public abstract class ChannelHandler<H extends ChannelHandler, S, D> {
    protected Channel channel;
    protected S src;
    protected D dst;

    public S src() {
        return this.src;
    }

    public void src(S src) {
        this.src = src;
    }

    public D dst() {
        return this.dst;
    }

    public void dst(D dst) {
        this.dst = dst;
    }

    public final H setChannel(Channel channel) {
        this.channel = channel;
        return (H)this;
    }

    public void handlerAdded() {
    }

    public void interceptError(Throwable error) throws Throwable {
    }
}

