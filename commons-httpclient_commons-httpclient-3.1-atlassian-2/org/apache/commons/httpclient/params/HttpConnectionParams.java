/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.httpclient.params;

import org.apache.commons.httpclient.params.DefaultHttpParams;

public class HttpConnectionParams
extends DefaultHttpParams {
    public static final String SO_TIMEOUT = "http.socket.timeout";
    public static final String TCP_NODELAY = "http.tcp.nodelay";
    public static final String SO_SNDBUF = "http.socket.sendbuffer";
    public static final String SO_RCVBUF = "http.socket.receivebuffer";
    public static final String SO_LINGER = "http.socket.linger";
    public static final String CONNECTION_TIMEOUT = "http.connection.timeout";
    public static final String STALE_CONNECTION_CHECK = "http.connection.stalecheck";

    public int getSoTimeout() {
        return this.getIntParameter(SO_TIMEOUT, 0);
    }

    public void setSoTimeout(int timeout) {
        this.setIntParameter(SO_TIMEOUT, timeout);
    }

    public void setTcpNoDelay(boolean value) {
        this.setBooleanParameter(TCP_NODELAY, value);
    }

    public boolean getTcpNoDelay() {
        return this.getBooleanParameter(TCP_NODELAY, true);
    }

    public int getSendBufferSize() {
        return this.getIntParameter(SO_SNDBUF, -1);
    }

    public void setSendBufferSize(int size) {
        this.setIntParameter(SO_SNDBUF, size);
    }

    public int getReceiveBufferSize() {
        return this.getIntParameter(SO_RCVBUF, -1);
    }

    public void setReceiveBufferSize(int size) {
        this.setIntParameter(SO_RCVBUF, size);
    }

    public int getLinger() {
        return this.getIntParameter(SO_LINGER, -1);
    }

    public void setLinger(int value) {
        this.setIntParameter(SO_LINGER, value);
    }

    public int getConnectionTimeout() {
        return this.getIntParameter(CONNECTION_TIMEOUT, 0);
    }

    public void setConnectionTimeout(int timeout) {
        this.setIntParameter(CONNECTION_TIMEOUT, timeout);
    }

    public boolean isStaleCheckingEnabled() {
        return this.getBooleanParameter(STALE_CONNECTION_CHECK, true);
    }

    public void setStaleCheckingEnabled(boolean value) {
        this.setBooleanParameter(STALE_CONNECTION_CHECK, value);
    }
}

