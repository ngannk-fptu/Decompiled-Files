/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.HttpException
 *  org.apache.http.HttpHost
 *  org.apache.http.HttpRequest
 *  org.apache.http.impl.conn.DefaultRoutePlanner
 *  org.apache.http.protocol.HttpContext
 */
package com.atlassian.sal.core.net;

import com.atlassian.sal.core.net.ProxyConfig;
import com.atlassian.sal.core.net.ProxyUtil;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.impl.conn.DefaultRoutePlanner;
import org.apache.http.protocol.HttpContext;

public class ProxyRoutePlanner
extends DefaultRoutePlanner {
    private final HttpHost proxy;
    private final String[] nonProxyHosts;

    public ProxyRoutePlanner(ProxyConfig proxyConfig) {
        super(null);
        this.proxy = new HttpHost(proxyConfig.getHost(), proxyConfig.getPort());
        this.nonProxyHosts = proxyConfig.getNonProxyHosts();
    }

    protected HttpHost determineProxy(HttpHost target, HttpRequest request, HttpContext context) throws HttpException {
        return ProxyUtil.shouldBeProxied(target.getHostName(), this.nonProxyHosts) ? this.proxy : null;
    }
}

