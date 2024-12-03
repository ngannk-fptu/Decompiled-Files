/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.ByteBufHolder
 */
package io.netty.channel.socket;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.DefaultAddressedEnvelope;
import java.net.InetSocketAddress;

public class DatagramPacket
extends DefaultAddressedEnvelope<ByteBuf, InetSocketAddress>
implements ByteBufHolder {
    public DatagramPacket(ByteBuf data, InetSocketAddress recipient) {
        super(data, recipient);
    }

    public DatagramPacket(ByteBuf data, InetSocketAddress recipient, InetSocketAddress sender) {
        super(data, recipient, sender);
    }

    public DatagramPacket copy() {
        return this.replace(((ByteBuf)this.content()).copy());
    }

    public DatagramPacket duplicate() {
        return this.replace(((ByteBuf)this.content()).duplicate());
    }

    public DatagramPacket retainedDuplicate() {
        return this.replace(((ByteBuf)this.content()).retainedDuplicate());
    }

    public DatagramPacket replace(ByteBuf content) {
        return new DatagramPacket(content, (InetSocketAddress)this.recipient(), (InetSocketAddress)this.sender());
    }

    @Override
    public DatagramPacket retain() {
        super.retain();
        return this;
    }

    @Override
    public DatagramPacket retain(int increment) {
        super.retain(increment);
        return this;
    }

    @Override
    public DatagramPacket touch() {
        super.touch();
        return this;
    }

    @Override
    public DatagramPacket touch(Object hint) {
        super.touch(hint);
        return this;
    }
}

