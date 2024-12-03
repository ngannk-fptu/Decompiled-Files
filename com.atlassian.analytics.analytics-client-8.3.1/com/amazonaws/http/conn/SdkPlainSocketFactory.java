/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.conn.socket.PlainConnectionSocketFactory
 *  org.apache.http.protocol.HttpContext
 */
package com.amazonaws.http.conn;

import com.amazonaws.http.apache.utils.HttpContextUtils;
import java.io.IOException;
import java.net.Proxy;
import java.net.Socket;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.protocol.HttpContext;

public class SdkPlainSocketFactory
extends PlainConnectionSocketFactory {
    public Socket createSocket(HttpContext ctx) throws IOException {
        if (HttpContextUtils.disableSocketProxy(ctx)) {
            return new Socket(Proxy.NO_PROXY);
        }
        return super.createSocket(ctx);
    }
}

