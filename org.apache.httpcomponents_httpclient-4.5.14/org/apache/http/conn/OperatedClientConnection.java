/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.HttpClientConnection
 *  org.apache.http.HttpHost
 *  org.apache.http.HttpInetConnection
 *  org.apache.http.params.HttpParams
 */
package org.apache.http.conn;

import java.io.IOException;
import java.net.Socket;
import org.apache.http.HttpClientConnection;
import org.apache.http.HttpHost;
import org.apache.http.HttpInetConnection;
import org.apache.http.params.HttpParams;

@Deprecated
public interface OperatedClientConnection
extends HttpClientConnection,
HttpInetConnection {
    public HttpHost getTargetHost();

    public boolean isSecure();

    public Socket getSocket();

    public void opening(Socket var1, HttpHost var2) throws IOException;

    public void openCompleted(boolean var1, HttpParams var2) throws IOException;

    public void update(Socket var1, HttpHost var2, boolean var3, HttpParams var4) throws IOException;
}

