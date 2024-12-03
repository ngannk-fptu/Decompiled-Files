/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http2.impl.nio;

import org.apache.hc.core5.http2.impl.nio.AbstractH2IOEventHandler;
import org.apache.hc.core5.http2.impl.nio.ServerH2StreamMultiplexer;
import org.apache.hc.core5.net.InetAddressUtils;

public class ServerH2IOEventHandler
extends AbstractH2IOEventHandler {
    public ServerH2IOEventHandler(ServerH2StreamMultiplexer streamMultiplexer) {
        super(streamMultiplexer);
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        InetAddressUtils.formatAddress(buf, this.getRemoteAddress());
        buf.append("->");
        InetAddressUtils.formatAddress(buf, this.getLocalAddress());
        buf.append(" [");
        this.streamMultiplexer.appendState(buf);
        buf.append("]");
        return buf.toString();
    }
}

