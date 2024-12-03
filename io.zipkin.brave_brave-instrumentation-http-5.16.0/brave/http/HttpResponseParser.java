/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  brave.SpanCustomizer
 *  brave.Tags
 *  brave.internal.Nullable
 *  brave.propagation.TraceContext
 */
package brave.http;

import brave.SpanCustomizer;
import brave.Tags;
import brave.http.HttpResponse;
import brave.http.HttpTags;
import brave.internal.Nullable;
import brave.propagation.TraceContext;

public interface HttpResponseParser {
    public static final HttpResponseParser DEFAULT = new Default();

    public void parse(HttpResponse var1, TraceContext var2, SpanCustomizer var3);

    public static class Default
    implements HttpResponseParser {
        @Override
        public void parse(HttpResponse response, TraceContext context, SpanCustomizer span) {
            int statusCode = 0;
            if (response != null) {
                statusCode = response.statusCode();
                String nameFromRoute = Default.spanNameFromRoute(response, statusCode);
                if (nameFromRoute != null) {
                    span.name(nameFromRoute);
                }
                if (statusCode < 200 || statusCode > 299) {
                    HttpTags.STATUS_CODE.tag((Object)response, context, span);
                }
            }
            this.error(statusCode, response.error(), span);
        }

        @Nullable
        static String spanNameFromRoute(HttpResponse response, int statusCode) {
            String method = response.method();
            if (method == null) {
                return null;
            }
            String route = response.route();
            if (route == null) {
                return null;
            }
            if (!"".equals(route)) {
                return method + " " + route;
            }
            return Default.catchAllName(method, statusCode);
        }

        protected void error(int httpStatus, @Nullable Throwable error, SpanCustomizer span) {
            if (error != null) {
                return;
            }
            if (httpStatus == 0) {
                return;
            }
            if (httpStatus < 100 || httpStatus > 399) {
                span.tag(Tags.ERROR.key(), HttpTags.statusCodeString(httpStatus));
            }
        }

        @Nullable
        static String catchAllName(String method, int statusCode) {
            switch (statusCode) {
                case 301: 
                case 302: 
                case 303: 
                case 305: 
                case 306: 
                case 307: {
                    return method + " redirected";
                }
                case 404: {
                    return method + " not_found";
                }
            }
            return null;
        }
    }
}

