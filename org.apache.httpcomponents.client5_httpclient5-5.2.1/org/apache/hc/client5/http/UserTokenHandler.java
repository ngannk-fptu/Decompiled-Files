/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.annotation.Contract
 *  org.apache.hc.core5.annotation.ThreadingBehavior
 *  org.apache.hc.core5.http.HttpRequest
 *  org.apache.hc.core5.http.protocol.HttpContext
 */
package org.apache.hc.client5.http;

import org.apache.hc.client5.http.HttpRoute;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.protocol.HttpContext;

@Contract(threading=ThreadingBehavior.STATELESS)
public interface UserTokenHandler {
    public Object getUserToken(HttpRoute var1, HttpContext var2);

    default public Object getUserToken(HttpRoute route, HttpRequest request, HttpContext context) {
        return this.getUserToken(route, context);
    }
}

