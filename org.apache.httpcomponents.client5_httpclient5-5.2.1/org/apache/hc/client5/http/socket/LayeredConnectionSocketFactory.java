/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.annotation.Contract
 *  org.apache.hc.core5.annotation.ThreadingBehavior
 *  org.apache.hc.core5.http.protocol.HttpContext
 */
package org.apache.hc.client5.http.socket;

import java.io.IOException;
import java.net.Socket;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.protocol.HttpContext;

@Contract(threading=ThreadingBehavior.STATELESS)
public interface LayeredConnectionSocketFactory
extends ConnectionSocketFactory {
    public Socket createLayeredSocket(Socket var1, String var2, int var3, HttpContext var4) throws IOException;

    default public Socket createLayeredSocket(Socket socket, String target, int port, Object attachment, HttpContext context) throws IOException {
        return this.createLayeredSocket(socket, target, port, context);
    }
}

