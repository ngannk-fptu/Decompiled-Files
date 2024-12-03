/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.HttpHost
 */
package com.atlassian.security.auth.trustedapps;

import com.atlassian.security.auth.trustedapps.Null;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.util.List;
import org.apache.http.HttpHost;

final class ProxyHostSelector {
    private final ProxySelector proxySelector;

    static ProxyHostSelector withDefaultProxySelector() {
        return new ProxyHostSelector(ProxySelector.getDefault());
    }

    static ProxyHostSelector withProxySelector(ProxySelector p) {
        Null.not("Proxy selector", p);
        return new ProxyHostSelector(p);
    }

    private ProxyHostSelector(ProxySelector p) {
        Null.not("Proxy selector", p);
        this.proxySelector = p;
    }

    ProxySelector getProxySelector() {
        return this.proxySelector;
    }

    HttpHost select(URI uri) {
        Null.not("URI", uri);
        Proxy p = ProxyHostSelector.chooseProxy(this.proxySelector.select(uri));
        return p.type() == Proxy.Type.HTTP ? ProxyHostSelector.proxyToProxyHost(p) : null;
    }

    private static HttpHost proxyToProxyHost(Proxy p) {
        InetSocketAddress isa = (InetSocketAddress)p.address();
        return new HttpHost(ProxyHostSelector.getHost(isa), isa.getPort());
    }

    private static Proxy chooseProxy(List<Proxy> candidates) {
        for (Proxy proxy : candidates) {
            switch (proxy.type()) {
                case DIRECT: 
                case HTTP: {
                    return proxy;
                }
            }
        }
        return Proxy.NO_PROXY;
    }

    private static String getHost(InetSocketAddress isa) {
        return isa.isUnresolved() ? isa.getHostName() : isa.getAddress().getHostAddress();
    }
}

