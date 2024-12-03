/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.HttpInetConnection
 */
package org.apache.http.conn;

import javax.net.ssl.SSLSession;
import org.apache.http.HttpInetConnection;
import org.apache.http.conn.routing.HttpRoute;

@Deprecated
public interface HttpRoutedConnection
extends HttpInetConnection {
    public boolean isSecure();

    public HttpRoute getRoute();

    public SSLSession getSSLSession();
}

