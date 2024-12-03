/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.observation.transport.RequestReplySenderContext
 *  org.apache.http.HttpRequest
 *  org.apache.http.HttpResponse
 *  org.apache.http.protocol.HttpContext
 */
package io.micrometer.core.instrument.binder.httpcomponents;

import io.micrometer.observation.transport.RequestReplySenderContext;
import java.util.function.Function;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;

public class ApacheHttpClientContext
extends RequestReplySenderContext<HttpRequest, HttpResponse> {
    private final HttpContext apacheHttpContext;
    private final Function<HttpRequest, String> uriMapper;
    private final boolean exportTagsForRoute;

    public ApacheHttpClientContext(HttpRequest request, HttpContext apacheHttpContext, Function<HttpRequest, String> uriMapper, boolean exportTagsForRoute) {
        super((httpRequest, key, value) -> {
            if (httpRequest != null) {
                httpRequest.addHeader(key, value);
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

