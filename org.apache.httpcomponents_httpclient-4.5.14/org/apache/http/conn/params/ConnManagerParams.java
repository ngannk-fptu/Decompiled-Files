/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.annotation.Contract
 *  org.apache.http.annotation.ThreadingBehavior
 *  org.apache.http.params.HttpParams
 *  org.apache.http.util.Args
 */
package org.apache.http.conn.params;

import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.conn.params.ConnManagerPNames;
import org.apache.http.conn.params.ConnPerRoute;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.params.HttpParams;
import org.apache.http.util.Args;

@Deprecated
@Contract(threading=ThreadingBehavior.IMMUTABLE)
public final class ConnManagerParams
implements ConnManagerPNames {
    public static final int DEFAULT_MAX_TOTAL_CONNECTIONS = 20;
    private static final ConnPerRoute DEFAULT_CONN_PER_ROUTE = new ConnPerRoute(){

        @Override
        public int getMaxForRoute(HttpRoute route) {
            return 2;
        }
    };

    @Deprecated
    public static long getTimeout(HttpParams params) {
        Args.notNull((Object)params, (String)"HTTP parameters");
        return params.getLongParameter("http.conn-manager.timeout", 0L);
    }

    @Deprecated
    public static void setTimeout(HttpParams params, long timeout) {
        Args.notNull((Object)params, (String)"HTTP parameters");
        params.setLongParameter("http.conn-manager.timeout", timeout);
    }

    public static void setMaxConnectionsPerRoute(HttpParams params, ConnPerRoute connPerRoute) {
        Args.notNull((Object)params, (String)"HTTP parameters");
        params.setParameter("http.conn-manager.max-per-route", (Object)connPerRoute);
    }

    public static ConnPerRoute getMaxConnectionsPerRoute(HttpParams params) {
        Args.notNull((Object)params, (String)"HTTP parameters");
        ConnPerRoute connPerRoute = (ConnPerRoute)params.getParameter("http.conn-manager.max-per-route");
        if (connPerRoute == null) {
            connPerRoute = DEFAULT_CONN_PER_ROUTE;
        }
        return connPerRoute;
    }

    public static void setMaxTotalConnections(HttpParams params, int maxTotalConnections) {
        Args.notNull((Object)params, (String)"HTTP parameters");
        params.setIntParameter("http.conn-manager.max-total", maxTotalConnections);
    }

    public static int getMaxTotalConnections(HttpParams params) {
        Args.notNull((Object)params, (String)"HTTP parameters");
        return params.getIntParameter("http.conn-manager.max-total", 20);
    }
}

