/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.client5.http;

import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.net.NamedEndpoint;

@Contract(threading=ThreadingBehavior.STATELESS)
public interface SchemePortResolver {
    public int resolve(HttpHost var1);

    default public int resolve(String scheme, NamedEndpoint endpoint) {
        return this.resolve(new HttpHost(scheme, endpoint));
    }
}

