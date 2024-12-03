/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  brave.SpanCustomizer
 *  brave.internal.Nullable
 *  brave.propagation.TraceContext
 */
package brave.http;

import brave.SpanCustomizer;
import brave.http.HttpRequest;
import brave.http.HttpTags;
import brave.internal.Nullable;
import brave.propagation.TraceContext;

public interface HttpRequestParser {
    public static final HttpRequestParser DEFAULT = new Default();

    public void parse(HttpRequest var1, TraceContext var2, SpanCustomizer var3);

    public static class Default
    implements HttpRequestParser {
        @Override
        public void parse(HttpRequest req, TraceContext context, SpanCustomizer span) {
            String name = this.spanName(req, context);
            if (name != null) {
                span.name(name);
            }
            HttpTags.METHOD.tag((Object)req, context, span);
            HttpTags.PATH.tag((Object)req, context, span);
        }

        @Nullable
        protected String spanName(HttpRequest req, TraceContext context) {
            return req.method();
        }
    }
}

