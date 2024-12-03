/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.reactor;

import java.nio.channels.SocketChannel;

final class ChannelEntry {
    final SocketChannel channel;
    final Object attachment;

    public ChannelEntry(SocketChannel channel, Object attachment) {
        this.channel = channel;
        this.attachment = attachment;
    }

    public String toString() {
        return "[channel=" + this.channel + ", attachment=" + this.attachment + ']';
    }
}

