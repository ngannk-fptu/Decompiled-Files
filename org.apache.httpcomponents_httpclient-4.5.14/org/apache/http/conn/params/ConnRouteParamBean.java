/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.HttpHost
 *  org.apache.http.params.HttpAbstractParamBean
 *  org.apache.http.params.HttpParams
 */
package org.apache.http.conn.params;

import java.net.InetAddress;
import org.apache.http.HttpHost;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.params.HttpAbstractParamBean;
import org.apache.http.params.HttpParams;

@Deprecated
public class ConnRouteParamBean
extends HttpAbstractParamBean {
    public ConnRouteParamBean(HttpParams params) {
        super(params);
    }

    public void setDefaultProxy(HttpHost defaultProxy) {
        this.params.setParameter("http.route.default-proxy", (Object)defaultProxy);
    }

    public void setLocalAddress(InetAddress address) {
        this.params.setParameter("http.route.local-address", (Object)address);
    }

    public void setForcedRoute(HttpRoute route) {
        this.params.setParameter("http.route.forced-route", (Object)route);
    }
}

