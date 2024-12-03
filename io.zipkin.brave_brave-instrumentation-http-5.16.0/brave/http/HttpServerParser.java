/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  brave.SpanCustomizer
 *  brave.internal.Nullable
 */
package brave.http;

import brave.SpanCustomizer;
import brave.http.HttpAdapter;
import brave.http.HttpParser;
import brave.internal.Nullable;

@Deprecated
public class HttpServerParser
extends HttpParser {
    @Override
    public <Req> void request(HttpAdapter<Req, ?> adapter, Req req, SpanCustomizer customizer) {
        super.request(adapter, req, customizer);
    }

    @Override
    public <Resp> void response(HttpAdapter<?, Resp> adapter, @Nullable Resp res, @Nullable Throwable error, SpanCustomizer customizer) {
        super.response(adapter, res, error, customizer);
    }
}

