/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.HttpHost
 */
package com.atlassian.analytics.client.proxy;

import org.apache.http.HttpHost;

public class ProxyUtils {
    private static final String HTTPS_PROXY_HOST_PROPERTY = "https.proxyHost";
    private static final String HTTPS_PROXY_PORT_PROPERTY = "https.proxyPort";

    public static HttpHost getProxy() {
        String proxyHost = ProxyUtils.getProxyHost();
        Integer proxyPort = ProxyUtils.getProxyPort();
        if (proxyHost != null && proxyPort != -1) {
            return new HttpHost(proxyHost, proxyPort.intValue());
        }
        return null;
    }

    public static String getProxyHost() {
        return System.getProperty(HTTPS_PROXY_HOST_PROPERTY);
    }

    public static int getProxyPort() {
        String proxyPort = System.getProperty(HTTPS_PROXY_PORT_PROPERTY);
        return proxyPort == null ? -1 : Integer.parseInt(proxyPort);
    }
}

