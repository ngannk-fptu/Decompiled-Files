/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.HeaderElement
 *  org.apache.http.HttpResponse
 *  org.apache.http.annotation.Contract
 *  org.apache.http.annotation.ThreadingBehavior
 *  org.apache.http.message.BasicHeaderElementIterator
 *  org.apache.http.protocol.HttpContext
 *  org.apache.http.util.Args
 */
package org.apache.http.impl.client;

import org.apache.http.HeaderElement;
import org.apache.http.HttpResponse;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;

@Contract(threading=ThreadingBehavior.IMMUTABLE)
public class DefaultConnectionKeepAliveStrategy
implements ConnectionKeepAliveStrategy {
    public static final DefaultConnectionKeepAliveStrategy INSTANCE = new DefaultConnectionKeepAliveStrategy();

    @Override
    public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
        Args.notNull((Object)response, (String)"HTTP response");
        BasicHeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator("Keep-Alive"));
        while (it.hasNext()) {
            HeaderElement he = it.nextElement();
            String param = he.getName();
            String value = he.getValue();
            if (value == null || !param.equalsIgnoreCase("timeout")) continue;
            try {
                return Long.parseLong(value) * 1000L;
            }
            catch (NumberFormatException numberFormatException) {
            }
        }
        return -1L;
    }
}

