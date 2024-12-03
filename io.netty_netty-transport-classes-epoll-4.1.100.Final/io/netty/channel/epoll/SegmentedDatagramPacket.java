/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.channel.unix.SegmentedDatagramPacket
 */
package io.netty.channel.epoll;

import io.netty.buffer.ByteBuf;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.Native;
import java.net.InetSocketAddress;

@Deprecated
public final class SegmentedDatagramPacket
extends io.netty.channel.unix.SegmentedDatagramPacket {
    public SegmentedDatagramPacket(ByteBuf data, int segmentSize, InetSocketAddress recipient) {
        super(data, segmentSize, recipient);
        SegmentedDatagramPacket.checkIsSupported();
    }

    public SegmentedDatagramPacket(ByteBuf data, int segmentSize, InetSocketAddress recipient, InetSocketAddress sender) {
        super(data, segmentSize, recipient, sender);
        SegmentedDatagramPacket.checkIsSupported();
    }

    public static boolean isSupported() {
        return Epoll.isAvailable() && Native.IS_SUPPORTING_SENDMMSG && Native.IS_SUPPORTING_UDP_SEGMENT;
    }

    public SegmentedDatagramPacket copy() {
        return new SegmentedDatagramPacket(((ByteBuf)this.content()).copy(), this.segmentSize(), (InetSocketAddress)this.recipient(), (InetSocketAddress)this.sender());
    }

    public SegmentedDatagramPacket duplicate() {
        return new SegmentedDatagramPacket(((ByteBuf)this.content()).duplicate(), this.segmentSize(), (InetSocketAddress)this.recipient(), (InetSocketAddress)this.sender());
    }

    public SegmentedDatagramPacket retainedDuplicate() {
        return new SegmentedDatagramPacket(((ByteBuf)this.content()).retainedDuplicate(), this.segmentSize(), (InetSocketAddress)this.recipient(), (InetSocketAddress)this.sender());
    }

    public SegmentedDatagramPacket replace(ByteBuf content) {
        return new SegmentedDatagramPacket(content, this.segmentSize(), (InetSocketAddress)this.recipient(), (InetSocketAddress)this.sender());
    }

    public SegmentedDatagramPacket retain() {
        super.retain();
        return this;
    }

    public SegmentedDatagramPacket retain(int increment) {
        super.retain(increment);
        return this;
    }

    public SegmentedDatagramPacket touch() {
        super.touch();
        return this;
    }

    public SegmentedDatagramPacket touch(Object hint) {
        super.touch(hint);
        return this;
    }

    private static void checkIsSupported() {
        if (!SegmentedDatagramPacket.isSupported()) {
            throw new IllegalStateException();
        }
    }
}

