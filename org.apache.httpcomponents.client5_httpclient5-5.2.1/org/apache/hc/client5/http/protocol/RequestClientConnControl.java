/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.annotation.Contract
 *  org.apache.hc.core5.annotation.ThreadingBehavior
 *  org.apache.hc.core5.http.EntityDetails
 *  org.apache.hc.core5.http.HttpException
 *  org.apache.hc.core5.http.HttpRequest
 *  org.apache.hc.core5.http.HttpRequestInterceptor
 *  org.apache.hc.core5.http.Method
 *  org.apache.hc.core5.http.protocol.HttpContext
 *  org.apache.hc.core5.util.Args
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.hc.client5.http.protocol;

import java.io.IOException;
import org.apache.hc.client5.http.RouteInfo;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpRequestInterceptor;
import org.apache.hc.core5.http.Method;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.util.Args;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Contract(threading=ThreadingBehavior.STATELESS)
public class RequestClientConnControl
implements HttpRequestInterceptor {
    private static final Logger LOG = LoggerFactory.getLogger(RequestClientConnControl.class);

    public void process(HttpRequest request, EntityDetails entity, HttpContext context) throws HttpException, IOException {
        Args.notNull((Object)request, (String)"HTTP request");
        String method = request.getMethod();
        if (Method.CONNECT.isSame(method)) {
            return;
        }
        HttpClientContext clientContext = HttpClientContext.adapt(context);
        String exchangeId = clientContext.getExchangeId();
        RouteInfo route = clientContext.getHttpRoute();
        if (route == null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("{} Connection route not set in the context", (Object)exchangeId);
            }
            return;
        }
        if ((route.getHopCount() == 1 || route.isTunnelled()) && !request.containsHeader("Connection")) {
            request.addHeader("Connection", (Object)"keep-alive");
        }
    }
}

