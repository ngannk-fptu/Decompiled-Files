/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.ByteBufHolder
 *  io.netty.channel.DefaultAddressedEnvelope
 */
package io.netty.channel.unix;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.DefaultAddressedEnvelope;
import io.netty.channel.unix.DomainSocketAddress;
import java.net.SocketAddress;

public final class DomainDatagramPacket
extends DefaultAddressedEnvelope<ByteBuf, DomainSocketAddress>
implements ByteBufHolder {
    public DomainDatagramPacket(ByteBuf data, DomainSocketAddress recipient) {
        super((Object)data, (SocketAddress)recipient);
    }

    public DomainDatagramPacket(ByteBuf data, DomainSocketAddress recipient, DomainSocketAddress sender) {
        super((Object)data, (SocketAddress)recipient, (SocketAddress)sender);
    }

    public DomainDatagramPacket copy() {
        return this.replace(((ByteBuf)this.content()).copy());
    }

    public DomainDatagramPacket duplicate() {
        return this.replace(((ByteBuf)this.content()).duplicate());
    }

    public DomainDatagramPacket replace(ByteBuf content) {
        return new DomainDatagramPacket(content, (DomainSocketAddress)this.recipient(), (DomainSocketAddress)this.sender());
    }

    public DomainDatagramPacket retain() {
        super.retain();
        return this;
    }

    public DomainDatagramPacket retain(int increment) {
        super.retain(increment);
        return this;
    }

    public DomainDatagramPacket retainedDuplicate() {
        return this.replace(((ByteBuf)this.content()).retainedDuplicate());
    }

    public DomainDatagramPacket touch() {
        super.touch();
        return this;
    }

    public DomainDatagramPacket touch(Object hint) {
        super.touch(hint);
        return this;
    }
}

