/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.HttpException
 *  org.apache.http.HttpRequest
 *  org.apache.http.annotation.Contract
 *  org.apache.http.annotation.ThreadingBehavior
 *  org.apache.http.protocol.HttpContext
 *  org.apache.http.util.Args
 */
package org.apache.http.client.protocol;

import java.io.IOException;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.auth.AuthState;
import org.apache.http.client.protocol.RequestAuthenticationBase;
import org.apache.http.conn.HttpRoutedConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;

@Deprecated
@Contract(threading=ThreadingBehavior.IMMUTABLE)
public class RequestProxyAuthentication
extends RequestAuthenticationBase {
    public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
        Args.notNull((Object)request, (String)"HTTP request");
        Args.notNull((Object)context, (String)"HTTP context");
        if (request.containsHeader("Proxy-Authorization")) {
            return;
        }
        HttpRoutedConnection conn = (HttpRoutedConnection)context.getAttribute("http.connection");
        if (conn == null) {
            this.log.debug((Object)"HTTP connection not set in the context");
            return;
        }
        HttpRoute route = conn.getRoute();
        if (route.isTunnelled()) {
            return;
        }
        AuthState authState = (AuthState)context.getAttribute("http.auth.proxy-scope");
        if (authState == null) {
            this.log.debug((Object)"Proxy auth state not set in the context");
            return;
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)("Proxy auth state: " + (Object)((Object)authState.getState())));
        }
        this.process(authState, request, context);
    }
}

