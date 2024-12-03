/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.websocket.EncodeException
 *  javax.websocket.RemoteEndpoint$Basic
 */
package org.apache.tomcat.websocket;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.ByteBuffer;
import javax.websocket.EncodeException;
import javax.websocket.RemoteEndpoint;
import org.apache.tomcat.websocket.WsRemoteEndpointBase;
import org.apache.tomcat.websocket.WsRemoteEndpointImplBase;

public class WsRemoteEndpointBasic
extends WsRemoteEndpointBase
implements RemoteEndpoint.Basic {
    WsRemoteEndpointBasic(WsRemoteEndpointImplBase base) {
        super(base);
    }

    public void sendText(String text) throws IOException {
        this.base.sendString(text);
    }

    public void sendBinary(ByteBuffer data) throws IOException {
        this.base.sendBytes(data);
    }

    public void sendText(String fragment, boolean isLast) throws IOException {
        this.base.sendPartialString(fragment, isLast);
    }

    public void sendBinary(ByteBuffer partialByte, boolean isLast) throws IOException {
        this.base.sendPartialBytes(partialByte, isLast);
    }

    public OutputStream getSendStream() throws IOException {
        return this.base.getSendStream();
    }

    public Writer getSendWriter() throws IOException {
        return this.base.getSendWriter();
    }

    public void sendObject(Object o) throws IOException, EncodeException {
        this.base.sendObject(o);
    }
}

