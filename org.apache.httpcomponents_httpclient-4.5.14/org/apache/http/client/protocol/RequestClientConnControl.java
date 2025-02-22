/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.apache.http.HttpException
 *  org.apache.http.HttpRequest
 *  org.apache.http.HttpRequestInterceptor
 *  org.apache.http.annotation.Contract
 *  org.apache.http.annotation.ThreadingBehavior
 *  org.apache.http.protocol.HttpContext
 *  org.apache.http.util.Args
 */
package org.apache.http.client.protocol;

import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.routing.RouteInfo;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;

@Contract(threading=ThreadingBehavior.IMMUTABLE)
public class RequestClientConnControl
implements HttpRequestInterceptor {
    private final Log log = LogFactory.getLog(this.getClass());
    private static final String PROXY_CONN_DIRECTIVE = "Proxy-Connection";

    public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
        Args.notNull((Object)request, (String)"HTTP request");
        String method = request.getRequestLine().getMethod();
        if (method.equalsIgnoreCase("CONNECT")) {
            request.setHeader(PROXY_CONN_DIRECTIVE, "Keep-Alive");
            return;
        }
        HttpClientContext clientContext = HttpClientContext.adapt(context);
        RouteInfo route = clientContext.getHttpRoute();
        if (route == null) {
            this.log.debug((Object)"Connection route not set in the context");
            return;
        }
        if ((route.getHopCount() == 1 || route.isTunnelled()) && !request.containsHeader("Connection")) {
            request.addHeader("Connection", "Keep-Alive");
        }
        if (route.getHopCount() == 2 && !route.isTunnelled() && !request.containsHeader(PROXY_CONN_DIRECTIVE)) {
            request.addHeader(PROXY_CONN_DIRECTIVE, "Keep-Alive");
        }
    }
}

