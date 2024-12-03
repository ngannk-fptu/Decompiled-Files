/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.net.InetAddressUtils
 */
package org.apache.hc.core5.http2.impl.nio;

import java.net.SocketAddress;
import org.apache.hc.core5.http2.impl.nio.AbstractH2IOEventHandler;
import org.apache.hc.core5.http2.impl.nio.ClientH2StreamMultiplexer;
import org.apache.hc.core5.net.InetAddressUtils;

public class ClientH2IOEventHandler
extends AbstractH2IOEventHandler {
    public ClientH2IOEventHandler(ClientH2StreamMultiplexer streamMultiplexer) {
        super(streamMultiplexer);
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        InetAddressUtils.formatAddress((StringBuilder)buf, (SocketAddress)this.getLocalAddress());
        buf.append("->");
        InetAddressUtils.formatAddress((StringBuilder)buf, (SocketAddress)this.getRemoteAddress());
        buf.append(" [");
        this.streamMultiplexer.appendState(buf);
        buf.append("]");
        return buf.toString();
    }
}

