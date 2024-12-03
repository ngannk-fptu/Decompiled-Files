/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.net.InetAddressUtils
 */
package org.apache.hc.core5.http2.impl.nio;

import java.net.SocketAddress;
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
        InetAddressUtils.formatAddress((StringBuilder)buf, (SocketAddress)this.getRemoteAddress());
        buf.append("->");
        InetAddressUtils.formatAddress((StringBuilder)buf, (SocketAddress)this.getLocalAddress());
        buf.append(" [");
        this.streamMultiplexer.appendState(buf);
        buf.append("]");
        return buf.toString();
    }
}

