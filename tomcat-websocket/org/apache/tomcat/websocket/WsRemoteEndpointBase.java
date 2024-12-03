/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.websocket.RemoteEndpoint
 */
package org.apache.tomcat.websocket;

import java.io.IOException;
import java.nio.ByteBuffer;
import javax.websocket.RemoteEndpoint;
import org.apache.tomcat.websocket.WsRemoteEndpointImplBase;

public abstract class WsRemoteEndpointBase
implements RemoteEndpoint {
    protected final WsRemoteEndpointImplBase base;

    WsRemoteEndpointBase(WsRemoteEndpointImplBase base) {
        this.base = base;
    }

    public final void setBatchingAllowed(boolean batchingAllowed) throws IOException {
        this.base.setBatchingAllowed(batchingAllowed);
    }

    public final boolean getBatchingAllowed() {
        return this.base.getBatchingAllowed();
    }

    public final void flushBatch() throws IOException {
        this.base.flushBatch();
    }

    public final void sendPing(ByteBuffer applicationData) throws IOException, IllegalArgumentException {
        this.base.sendPing(applicationData);
    }

    public final void sendPong(ByteBuffer applicationData) throws IOException, IllegalArgumentException {
        this.base.sendPong(applicationData);
    }
}

