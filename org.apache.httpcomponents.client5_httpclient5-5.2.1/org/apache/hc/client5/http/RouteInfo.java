/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.http.HttpHost
 */
package org.apache.hc.client5.http;

import java.net.InetAddress;
import org.apache.hc.core5.http.HttpHost;

public interface RouteInfo {
    public HttpHost getTargetHost();

    public InetAddress getLocalAddress();

    public int getHopCount();

    public HttpHost getHopTarget(int var1);

    public HttpHost getProxyHost();

    public TunnelType getTunnelType();

    public boolean isTunnelled();

    public LayerType getLayerType();

    public boolean isLayered();

    public boolean isSecure();

    public static enum LayerType {
        PLAIN,
        LAYERED;

    }

    public static enum TunnelType {
        PLAIN,
        TUNNELLED;

    }
}

