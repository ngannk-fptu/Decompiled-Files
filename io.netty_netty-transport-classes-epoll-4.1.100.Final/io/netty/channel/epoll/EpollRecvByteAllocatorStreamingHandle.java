/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.RecvByteBufAllocator$ExtendedHandle
 */
package io.netty.channel.epoll;

import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.epoll.EpollRecvByteAllocatorHandle;

final class EpollRecvByteAllocatorStreamingHandle
extends EpollRecvByteAllocatorHandle {
    EpollRecvByteAllocatorStreamingHandle(RecvByteBufAllocator.ExtendedHandle handle) {
        super(handle);
    }

    @Override
    boolean maybeMoreDataToRead() {
        return this.lastBytesRead() == this.attemptedBytesRead() || this.isReceivedRdHup();
    }
}

