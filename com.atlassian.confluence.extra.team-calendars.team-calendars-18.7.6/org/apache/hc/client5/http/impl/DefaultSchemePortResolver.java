/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.client5.http.impl;

import org.apache.hc.client5.http.SchemePortResolver;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.URIScheme;
import org.apache.hc.core5.net.NamedEndpoint;
import org.apache.hc.core5.util.Args;

@Contract(threading=ThreadingBehavior.STATELESS)
public class DefaultSchemePortResolver
implements SchemePortResolver {
    public static final DefaultSchemePortResolver INSTANCE = new DefaultSchemePortResolver();

    @Override
    public int resolve(HttpHost host) {
        Args.notNull(host, "HTTP host");
        return this.resolve(host.getSchemeName(), host);
    }

    @Override
    public int resolve(String scheme, NamedEndpoint endpoint) {
        Args.notNull(endpoint, "Endpoint");
        int port = endpoint.getPort();
        if (port > 0) {
            return port;
        }
        if (URIScheme.HTTP.same(scheme)) {
            return 80;
        }
        if (URIScheme.HTTPS.same(scheme)) {
            return 443;
        }
        return -1;
    }
}

