/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.httpclient;

import org.apache.commons.httpclient.ConnectionPoolTimeoutException;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpConnection;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;

public interface HttpConnectionManager {
    public HttpConnection getConnection(HostConfiguration var1);

    public HttpConnection getConnection(HostConfiguration var1, long var2) throws HttpException;

    public HttpConnection getConnectionWithTimeout(HostConfiguration var1, long var2) throws ConnectionPoolTimeoutException;

    public void releaseConnection(HttpConnection var1);

    public void closeIdleConnections(long var1);

    public HttpConnectionManagerParams getParams();

    public void setParams(HttpConnectionManagerParams var1);
}

