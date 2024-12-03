/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  brave.ErrorParser
 *  brave.SpanCustomizer
 *  brave.internal.Nullable
 */
package brave.http;

import brave.ErrorParser;
import brave.SpanCustomizer;
import brave.http.HttpAdapter;
import brave.http.HttpResponseParser;
import brave.internal.Nullable;

@Deprecated
public class HttpParser {
    static final ErrorParser ERROR_PARSER = new ErrorParser();

    protected ErrorParser errorParser() {
        return ERROR_PARSER;
    }

    public <Req> void request(HttpAdapter<Req, ?> adapter, Req req, SpanCustomizer customizer) {
        String path;
        String method;
        String name = this.spanName(adapter, req);
        if (name != null) {
            customizer.name(name);
        }
        if ((method = adapter.method(req)) != null) {
            customizer.tag("http.method", method);
        }
        if ((path = adapter.path(req)) != null) {
            customizer.tag("http.path", path);
        }
    }

    @Nullable
    protected <Req> String spanName(HttpAdapter<Req, ?> adapter, Req req) {
        return adapter.method(req);
    }

    public <Resp> void response(HttpAdapter<?, Resp> adapter, @Nullable Resp res, @Nullable Throwable error, SpanCustomizer customizer) {
        int statusCode = 0;
        if (res != null) {
            String maybeStatus;
            statusCode = adapter.statusCodeAsInt(res);
            String nameFromRoute = HttpParser.spanNameFromRoute(adapter, res, statusCode);
            if (nameFromRoute != null) {
                customizer.name(nameFromRoute);
            }
            if ((maybeStatus = this.maybeStatusAsString(statusCode, 299)) != null) {
                customizer.tag("http.status_code", maybeStatus);
            }
        }
        this.error(statusCode, error, customizer);
    }

    @Nullable
    String maybeStatusAsString(int statusCode, int upperRange) {
        if (statusCode != 0 && (statusCode < 200 || statusCode > upperRange)) {
            return String.valueOf(statusCode);
        }
        return null;
    }

    static <Resp> String spanNameFromRoute(HttpAdapter<?, Resp> adapter, Resp res, int statusCode) {
        String method = adapter.methodFromResponse(res);
        if (method == null) {
            return null;
        }
        String route = adapter.route(res);
        if (route == null) {
            return null;
        }
        if (!"".equals(route)) {
            return method + " " + route;
        }
        return HttpResponseParser.Default.catchAllName(method, statusCode);
    }

    protected void error(@Nullable Integer httpStatus, @Nullable Throwable error, SpanCustomizer customizer) {
        if (error != null) {
            this.errorParser().error(error, customizer);
            return;
        }
        if (httpStatus == null) {
            return;
        }
        int httpStatusInt = httpStatus;
        if (httpStatusInt == 0) {
            return;
        }
        if (httpStatusInt < 100 || httpStatusInt > 399) {
            customizer.tag("error", String.valueOf(httpStatusInt));
        }
    }
}

