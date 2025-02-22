/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.HttpHost
 *  org.apache.http.params.HttpParams
 *  org.apache.http.protocol.HttpContext
 *  org.apache.http.util.Args
 *  org.apache.http.util.Asserts
 */
package org.apache.http.impl.conn;

import java.io.IOException;
import java.io.InterruptedIOException;
import org.apache.http.HttpHost;
import org.apache.http.conn.ClientConnectionOperator;
import org.apache.http.conn.OperatedClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.RouteTracker;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;
import org.apache.http.util.Asserts;

@Deprecated
public abstract class AbstractPoolEntry {
    protected final ClientConnectionOperator connOperator;
    protected final OperatedClientConnection connection;
    protected volatile HttpRoute route;
    protected volatile Object state;
    protected volatile RouteTracker tracker;

    protected AbstractPoolEntry(ClientConnectionOperator connOperator, HttpRoute route) {
        Args.notNull((Object)connOperator, (String)"Connection operator");
        this.connOperator = connOperator;
        this.connection = connOperator.createConnection();
        this.route = route;
        this.tracker = null;
    }

    public Object getState() {
        return this.state;
    }

    public void setState(Object state) {
        this.state = state;
    }

    public void open(HttpRoute route, HttpContext context, HttpParams params) throws IOException {
        Args.notNull((Object)route, (String)"Route");
        Args.notNull((Object)params, (String)"HTTP parameters");
        if (this.tracker != null) {
            Asserts.check((!this.tracker.isConnected() ? 1 : 0) != 0, (String)"Connection already open");
        }
        this.tracker = new RouteTracker(route);
        HttpHost proxy = route.getProxyHost();
        this.connOperator.openConnection(this.connection, proxy != null ? proxy : route.getTargetHost(), route.getLocalAddress(), context, params);
        RouteTracker localTracker = this.tracker;
        if (localTracker == null) {
            throw new InterruptedIOException("Request aborted");
        }
        if (proxy == null) {
            localTracker.connectTarget(this.connection.isSecure());
        } else {
            localTracker.connectProxy(proxy, this.connection.isSecure());
        }
    }

    public void tunnelTarget(boolean secure, HttpParams params) throws IOException {
        Args.notNull((Object)params, (String)"HTTP parameters");
        Asserts.notNull((Object)this.tracker, (String)"Route tracker");
        Asserts.check((boolean)this.tracker.isConnected(), (String)"Connection not open");
        Asserts.check((!this.tracker.isTunnelled() ? 1 : 0) != 0, (String)"Connection is already tunnelled");
        this.connection.update(null, this.tracker.getTargetHost(), secure, params);
        this.tracker.tunnelTarget(secure);
    }

    public void tunnelProxy(HttpHost next, boolean secure, HttpParams params) throws IOException {
        Args.notNull((Object)next, (String)"Next proxy");
        Args.notNull((Object)params, (String)"Parameters");
        Asserts.notNull((Object)this.tracker, (String)"Route tracker");
        Asserts.check((boolean)this.tracker.isConnected(), (String)"Connection not open");
        this.connection.update(null, next, secure, params);
        this.tracker.tunnelProxy(next, secure);
    }

    public void layerProtocol(HttpContext context, HttpParams params) throws IOException {
        Args.notNull((Object)params, (String)"HTTP parameters");
        Asserts.notNull((Object)this.tracker, (String)"Route tracker");
        Asserts.check((boolean)this.tracker.isConnected(), (String)"Connection not open");
        Asserts.check((boolean)this.tracker.isTunnelled(), (String)"Protocol layering without a tunnel not supported");
        Asserts.check((!this.tracker.isLayered() ? 1 : 0) != 0, (String)"Multiple protocol layering not supported");
        HttpHost target = this.tracker.getTargetHost();
        this.connOperator.updateSecureConnection(this.connection, target, context, params);
        this.tracker.layerProtocol(this.connection.isSecure());
    }

    protected void shutdownEntry() {
        this.tracker = null;
        this.state = null;
    }
}

