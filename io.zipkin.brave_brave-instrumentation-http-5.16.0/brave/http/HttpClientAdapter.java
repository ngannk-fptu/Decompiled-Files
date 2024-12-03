/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  zipkin2.Endpoint$Builder
 */
package brave.http;

import brave.http.HttpAdapter;
import zipkin2.Endpoint;

@Deprecated
public abstract class HttpClientAdapter<Req, Resp>
extends HttpAdapter<Req, Resp> {
    @Deprecated
    public boolean parseServerIpAndPort(Req req, Endpoint.Builder builder) {
        return false;
    }
}

