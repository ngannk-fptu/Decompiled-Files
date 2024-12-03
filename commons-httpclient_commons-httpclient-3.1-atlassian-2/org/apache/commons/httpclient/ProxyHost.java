/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.httpclient;

import org.apache.commons.httpclient.HttpHost;
import org.apache.commons.httpclient.protocol.Protocol;

public class ProxyHost
extends HttpHost {
    public ProxyHost(ProxyHost httpproxy) {
        super(httpproxy);
    }

    public ProxyHost(String hostname, int port) {
        super(hostname, port, Protocol.getProtocol("http"));
    }

    public ProxyHost(String hostname) {
        this(hostname, -1);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        ProxyHost copy = (ProxyHost)super.clone();
        return copy;
    }
}

