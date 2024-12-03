/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 */
package com.sun.xml.ws.api.server;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.server.WSEndpoint;
import com.sun.xml.ws.transport.http.HttpAdapter;

public abstract class HttpEndpoint {
    public static HttpEndpoint create(@NotNull WSEndpoint endpoint) {
        return new com.sun.xml.ws.transport.http.server.HttpEndpoint(null, HttpAdapter.createAlone(endpoint));
    }

    public abstract void publish(@NotNull String var1);

    public abstract void stop();
}

