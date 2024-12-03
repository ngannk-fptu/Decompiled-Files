/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.observation.transport.RequestReplySenderContext
 *  org.apache.hc.core5.http.HttpRequest
 *  org.apache.hc.core5.http.HttpResponse
 *  org.apache.hc.core5.http.protocol.HttpContext
 */
package io.micrometer.core.instrument.binder.httpcomponents.hc5;

import io.micrometer.observation.transport.RequestReplySenderContext;
import java.util.function.Function;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.protocol.HttpContext;

public class ApacheHttpClientContext
extends RequestReplySenderContext<HttpRequest, HttpResponse> {
    private final HttpContext apacheHttpContext;
    private final Function<HttpRequest, String> uriMapper;
    private final boolean exportTagsForRoute;

    public ApacheHttpClientContext(HttpRequest request, HttpContext apacheHttpContext, Function<HttpRequest, String> uriMapper, boolean exportTagsForRoute) {
        super((httpRequest, key, value) -> {
            if (httpRequest != null) {
                httpRequest.addHeader(key, (Object)value);
            }
        });
        this.uriMapper = uriMapper;
        this.exportTagsForRoute = exportTagsForRoute;
        this.setCarrier(request);
        this.apacheHttpContext = apacheHttpContext;
    }

    public HttpContext getApacheHttpContext() {
        return this.apacheHttpContext;
    }

    public Function<HttpRequest, String> getUriMapper() {
        return this.uriMapper;
    }

    public boolean shouldExportTagsForRoute() {
        return this.exportTagsForRoute;
    }
}

