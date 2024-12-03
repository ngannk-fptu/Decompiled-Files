/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.httpclient.protocol;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.apache.commons.httpclient.protocol.DefaultProtocolSocketFactory;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.commons.httpclient.protocol.SSLProtocolSocketFactory;
import org.apache.commons.httpclient.protocol.SecureProtocolSocketFactory;
import org.apache.commons.httpclient.util.LangUtils;

public class Protocol {
    private static final Map PROTOCOLS = Collections.synchronizedMap(new HashMap());
    private String scheme;
    private ProtocolSocketFactory socketFactory;
    private int defaultPort;
    private boolean secure;

    public static void registerProtocol(String id, Protocol protocol) {
        if (id == null) {
            throw new IllegalArgumentException("id is null");
        }
        if (protocol == null) {
            throw new IllegalArgumentException("protocol is null");
        }
        PROTOCOLS.put(id, protocol);
    }

    public static void unregisterProtocol(String id) {
        if (id == null) {
            throw new IllegalArgumentException("id is null");
        }
        PROTOCOLS.remove(id);
    }

    public static Protocol getProtocol(String id) throws IllegalStateException {
        if (id == null) {
            throw new IllegalArgumentException("id is null");
        }
        Protocol protocol = (Protocol)PROTOCOLS.get(id);
        if (protocol == null) {
            protocol = Protocol.lazyRegisterProtocol(id);
        }
        return protocol;
    }

    private static Protocol lazyRegisterProtocol(String id) throws IllegalStateException {
        if ("http".equals(id)) {
            Protocol http = new Protocol("http", DefaultProtocolSocketFactory.getSocketFactory(), 80);
            Protocol.registerProtocol("http", http);
            return http;
        }
        if ("https".equals(id)) {
            Protocol https = new Protocol("https", SSLProtocolSocketFactory.getSocketFactory(), 443);
            Protocol.registerProtocol("https", https);
            return https;
        }
        throw new IllegalStateException("unsupported protocol: '" + id + "'");
    }

    public Protocol(String scheme, ProtocolSocketFactory factory, int defaultPort) {
        if (scheme == null) {
            throw new IllegalArgumentException("scheme is null");
        }
        if (factory == null) {
            throw new IllegalArgumentException("socketFactory is null");
        }
        if (defaultPort <= 0) {
            throw new IllegalArgumentException("port is invalid: " + defaultPort);
        }
        this.scheme = scheme;
        this.socketFactory = factory;
        this.defaultPort = defaultPort;
        this.secure = factory instanceof SecureProtocolSocketFactory;
    }

    public Protocol(String scheme, SecureProtocolSocketFactory factory, int defaultPort) {
        this(scheme, (ProtocolSocketFactory)factory, defaultPort);
    }

    public int getDefaultPort() {
        return this.defaultPort;
    }

    public ProtocolSocketFactory getSocketFactory() {
        return this.socketFactory;
    }

    public String getScheme() {
        return this.scheme;
    }

    public boolean isSecure() {
        return this.secure;
    }

    public int resolvePort(int port) {
        return port <= 0 ? this.getDefaultPort() : port;
    }

    public String toString() {
        return this.scheme + ":" + this.defaultPort;
    }

    public boolean equals(Object obj) {
        if (obj instanceof Protocol) {
            Protocol p = (Protocol)obj;
            return this.defaultPort == p.getDefaultPort() && this.scheme.equalsIgnoreCase(p.getScheme()) && this.secure == p.isSecure() && this.socketFactory.equals(p.getSocketFactory());
        }
        return false;
    }

    public int hashCode() {
        int hash = 17;
        hash = LangUtils.hashCode(hash, this.defaultPort);
        hash = LangUtils.hashCode(hash, this.scheme.toLowerCase(Locale.ENGLISH));
        hash = LangUtils.hashCode(hash, this.secure);
        hash = LangUtils.hashCode(hash, this.socketFactory);
        return hash;
    }
}

