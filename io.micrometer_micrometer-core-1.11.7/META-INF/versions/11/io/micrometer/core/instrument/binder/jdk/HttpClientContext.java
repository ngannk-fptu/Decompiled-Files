/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.observation.transport.RequestReplySenderContext
 */
package io.micrometer.core.instrument.binder.jdk;

import io.micrometer.observation.transport.RequestReplySenderContext;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;
import java.util.function.Function;

public class HttpClientContext
extends RequestReplySenderContext<HttpRequest.Builder, HttpResponse<?>> {
    private final Function<HttpRequest, String> uriMapper;

    public HttpClientContext(Function<HttpRequest, String> uriMapper) {
        super((carrier, key, value) -> Objects.requireNonNull(carrier).header(key, value));
        this.uriMapper = uriMapper;
    }

    public Function<HttpRequest, String> getUriMapper() {
        return this.uriMapper;
    }
}

