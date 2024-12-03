/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.util.BufferUtil
 *  org.eclipse.jetty.util.Index
 *  org.eclipse.jetty.util.Index$Builder
 */
package org.eclipse.jetty.http;

import java.nio.ByteBuffer;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.Index;

public enum HttpScheme {
    HTTP("http", 80),
    HTTPS("https", 443),
    WS("ws", 80),
    WSS("wss", 443);

    public static final Index<HttpScheme> CACHE;
    private final String _string;
    private final ByteBuffer _buffer;
    private final int _defaultPort;

    private HttpScheme(String s, int port) {
        this._string = s;
        this._buffer = BufferUtil.toBuffer((String)s);
        this._defaultPort = port;
    }

    public ByteBuffer asByteBuffer() {
        return this._buffer.asReadOnlyBuffer();
    }

    public boolean is(String s) {
        return this._string.equalsIgnoreCase(s);
    }

    public String asString() {
        return this._string;
    }

    public int getDefaultPort() {
        return this._defaultPort;
    }

    public int normalizePort(int port) {
        return port == this._defaultPort ? 0 : port;
    }

    public String toString() {
        return this._string;
    }

    public static int getDefaultPort(String scheme) {
        HttpScheme httpScheme = scheme == null ? null : (HttpScheme)((Object)CACHE.get(scheme));
        return httpScheme == null ? HTTP.getDefaultPort() : httpScheme.getDefaultPort();
    }

    public static int normalizePort(String scheme, int port) {
        HttpScheme httpScheme = scheme == null ? null : (HttpScheme)((Object)CACHE.get(scheme));
        return httpScheme == null ? port : httpScheme.normalizePort(port);
    }

    static {
        CACHE = new Index.Builder().caseSensitive(false).withAll((Object[])HttpScheme.values(), HttpScheme::asString).build();
    }
}

