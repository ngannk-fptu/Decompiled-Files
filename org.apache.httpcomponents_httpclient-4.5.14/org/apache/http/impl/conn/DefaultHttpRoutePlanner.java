/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.HttpException
 *  org.apache.http.HttpHost
 *  org.apache.http.HttpRequest
 *  org.apache.http.annotation.Contract
 *  org.apache.http.annotation.ThreadingBehavior
 *  org.apache.http.protocol.HttpContext
 *  org.apache.http.util.Args
 *  org.apache.http.util.Asserts
 */
package org.apache.http.impl.conn;

import java.net.InetAddress;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;
import org.apache.http.util.Asserts;

@Deprecated
@Contract(threading=ThreadingBehavior.SAFE)
public class DefaultHttpRoutePlanner
implements HttpRoutePlanner {
    protected final SchemeRegistry schemeRegistry;

    public DefaultHttpRoutePlanner(SchemeRegistry schreg) {
        Args.notNull((Object)schreg, (String)"Scheme registry");
        this.schemeRegistry = schreg;
    }

    @Override
    public HttpRoute determineRoute(HttpHost target, HttpRequest request, HttpContext context) throws HttpException {
        Scheme schm;
        Args.notNull((Object)request, (String)"HTTP request");
        HttpRoute route = ConnRouteParams.getForcedRoute(request.getParams());
        if (route != null) {
            return route;
        }
        Asserts.notNull((Object)target, (String)"Target host");
        InetAddress local = ConnRouteParams.getLocalAddress(request.getParams());
        HttpHost proxy = ConnRouteParams.getDefaultProxy(request.getParams());
        try {
            schm = this.schemeRegistry.getScheme(target.getSchemeName());
        }
        catch (IllegalStateException ex) {
            throw new HttpException(ex.getMessage());
        }
        boolean secure = schm.isLayered();
        route = proxy == null ? new HttpRoute(target, local, secure) : new HttpRoute(target, local, proxy, secure);
        return route;
    }
}

