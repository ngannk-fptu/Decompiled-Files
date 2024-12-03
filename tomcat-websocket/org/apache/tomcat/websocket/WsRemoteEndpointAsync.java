/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.websocket.RemoteEndpoint$Async
 *  javax.websocket.SendHandler
 */
package org.apache.tomcat.websocket;

import java.nio.ByteBuffer;
import java.util.concurrent.Future;
import javax.websocket.RemoteEndpoint;
import javax.websocket.SendHandler;
import org.apache.tomcat.websocket.WsRemoteEndpointBase;
import org.apache.tomcat.websocket.WsRemoteEndpointImplBase;

public class WsRemoteEndpointAsync
extends WsRemoteEndpointBase
implements RemoteEndpoint.Async {
    WsRemoteEndpointAsync(WsRemoteEndpointImplBase base) {
        super(base);
    }

    public long getSendTimeout() {
        return this.base.getSendTimeout();
    }

    public void setSendTimeout(long timeout) {
        this.base.setSendTimeout(timeout);
    }

    public void sendText(String text, SendHandler completion) {
        this.base.sendStringByCompletion(text, completion);
    }

    public Future<Void> sendText(String text) {
        return this.base.sendStringByFuture(text);
    }

    public Future<Void> sendBinary(ByteBuffer data) {
        return this.base.sendBytesByFuture(data);
    }

    public void sendBinary(ByteBuffer data, SendHandler completion) {
        this.base.sendBytesByCompletion(data, completion);
    }

    public Future<Void> sendObject(Object obj) {
        return this.base.sendObjectByFuture(obj);
    }

    public void sendObject(Object obj, SendHandler completion) {
        this.base.sendObjectByCompletion(obj, completion);
    }
}

