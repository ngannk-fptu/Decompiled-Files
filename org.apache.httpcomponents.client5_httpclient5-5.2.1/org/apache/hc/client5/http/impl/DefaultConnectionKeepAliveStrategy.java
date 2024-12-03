/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.annotation.Contract
 *  org.apache.hc.core5.annotation.ThreadingBehavior
 *  org.apache.hc.core5.http.HeaderElement
 *  org.apache.hc.core5.http.HttpResponse
 *  org.apache.hc.core5.http.MessageHeaders
 *  org.apache.hc.core5.http.message.MessageSupport
 *  org.apache.hc.core5.http.protocol.HttpContext
 *  org.apache.hc.core5.util.Args
 *  org.apache.hc.core5.util.TimeValue
 */
package org.apache.hc.client5.http.impl;

import java.util.Iterator;
import org.apache.hc.client5.http.ConnectionKeepAliveStrategy;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.HeaderElement;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.MessageHeaders;
import org.apache.hc.core5.http.message.MessageSupport;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.util.Args;
import org.apache.hc.core5.util.TimeValue;

@Contract(threading=ThreadingBehavior.STATELESS)
public class DefaultConnectionKeepAliveStrategy
implements ConnectionKeepAliveStrategy {
    public static final DefaultConnectionKeepAliveStrategy INSTANCE = new DefaultConnectionKeepAliveStrategy();

    @Override
    public TimeValue getKeepAliveDuration(HttpResponse response, HttpContext context) {
        Args.notNull((Object)response, (String)"HTTP response");
        Iterator it = MessageSupport.iterate((MessageHeaders)response, (String)"keep-alive");
        while (it.hasNext()) {
            HeaderElement he = (HeaderElement)it.next();
            String param = he.getName();
            String value = he.getValue();
            if (value == null || !param.equalsIgnoreCase("timeout")) continue;
            try {
                return TimeValue.ofSeconds((long)Long.parseLong(value));
            }
            catch (NumberFormatException numberFormatException) {
            }
        }
        HttpClientContext clientContext = HttpClientContext.adapt(context);
        RequestConfig requestConfig = clientContext.getRequestConfig();
        return requestConfig.getConnectionKeepAlive();
    }
}

