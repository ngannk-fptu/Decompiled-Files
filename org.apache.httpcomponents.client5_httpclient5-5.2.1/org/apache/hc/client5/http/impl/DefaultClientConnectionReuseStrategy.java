/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.annotation.Contract
 *  org.apache.hc.core5.annotation.ThreadingBehavior
 *  org.apache.hc.core5.http.HttpRequest
 *  org.apache.hc.core5.http.HttpResponse
 *  org.apache.hc.core5.http.Method
 *  org.apache.hc.core5.http.impl.DefaultConnectionReuseStrategy
 *  org.apache.hc.core5.http.protocol.HttpContext
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

    public boolean keepAlive(HttpRequest request, HttpResponse response, HttpContext context) {
        if (Method.CONNECT.isSame(request.getMethod()) && response.getCode() == 200) {
            return true;
        }
        return super.keepAlive(request, response, context);
    }
}

