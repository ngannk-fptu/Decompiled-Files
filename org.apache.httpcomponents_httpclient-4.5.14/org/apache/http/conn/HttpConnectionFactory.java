/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.HttpConnection
 *  org.apache.http.config.ConnectionConfig
 */
package org.apache.http.conn;

import org.apache.http.HttpConnection;
import org.apache.http.config.ConnectionConfig;

public interface HttpConnectionFactory<T, C extends HttpConnection> {
    public C create(T var1, ConnectionConfig var2);
}

