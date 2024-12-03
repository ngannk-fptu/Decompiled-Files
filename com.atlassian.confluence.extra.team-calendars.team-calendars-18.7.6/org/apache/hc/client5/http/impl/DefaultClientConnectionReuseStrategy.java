/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.client5.http.impl;

import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.Method;
import org.apache.hc.core5.http.impl.DefaultConnectionReuseStrategy;
import org.apache.hc.core5.http.protocol.HttpContext;

@Contract(threading=ThreadingBehavior.STATELESS)
public class DefaultClientConnectionReuseStrategy
extends DefaultConnectionReuseStrategy {
    public static final DefaultClientConnectionReuseStrategy INSTANCE = new DefaultClientConnectionReuseStrategy();

    @Override
    public boolean keepAlive(HttpRequest request, HttpResponse response, HttpContext context) {
        if (Method.CONNECT.isSame(request.getMethod()) && response.getCode() == 200) {
            return true;
        }
        return super.keepAlive(request, response, context);
    }
}

