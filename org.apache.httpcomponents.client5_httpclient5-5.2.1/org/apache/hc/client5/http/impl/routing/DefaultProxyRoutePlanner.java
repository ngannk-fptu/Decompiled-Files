/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.annotation.Contract
 *  org.apache.hc.core5.annotation.ThreadingBehavior
 *  org.apache.hc.core5.http.HttpException
 *  org.apache.hc.core5.http.HttpHost
 *  org.apache.hc.core5.http.protocol.HttpContext
 *  org.apache.hc.core5.util.Args
 */
package org.apache.hc.client5.http.impl.routing;

import org.apache.hc.client5.http.SchemePortResolver;
import org.apache.hc.client5.http.impl.routing.DefaultRoutePlanner;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.util.Args;

@Contract(threading=ThreadingBehavior.STATELESS)
public class DefaultProxyRoutePlanner
extends DefaultRoutePlanner {
    private final HttpHost proxy;

    public DefaultProxyRoutePlanner(HttpHost proxy, SchemePortResolver schemePortResolver) {
        super(schemePortResolver);
        this.proxy = (HttpHost)Args.notNull((Object)proxy, (String)"Proxy host");
    }

    public DefaultProxyRoutePlanner(HttpHost proxy) {
        this(proxy, null);
    }

    @Override
    protected HttpHost determineProxy(HttpHost target, HttpContext context) throws HttpException {
        return this.proxy;
    }
}

