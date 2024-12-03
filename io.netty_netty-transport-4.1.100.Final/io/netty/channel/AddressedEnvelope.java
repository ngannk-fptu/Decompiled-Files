/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.util.ReferenceCounted
 */
package io.netty.channel;

import io.netty.util.ReferenceCounted;
import java.net.SocketAddress;

public interface AddressedEnvelope<M, A extends SocketAddress>
extends ReferenceCounted {
    public M content();

    public A sender();

    public A recipient();

    public AddressedEnvelope<M, A> retain();

    public AddressedEnvelope<M, A> retain(int var1);

    public AddressedEnvelope<M, A> touch();

    public AddressedEnvelope<M, A> touch(Object var1);
}

