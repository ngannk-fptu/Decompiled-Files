/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.KeyValue
 *  io.micrometer.observation.transport.Kind
 *  io.micrometer.observation.transport.RequestReplySenderContext
 *  okhttp3.Request
 *  okhttp3.Request$Builder
 *  okhttp3.Response
 */
package io.micrometer.core.instrument.binder.okhttp3;

import io.micrometer.common.KeyValue;
import io.micrometer.core.instrument.binder.okhttp3.OkHttpObservationInterceptor;
import io.micrometer.observation.transport.Kind;
import io.micrometer.observation.transport.RequestReplySenderContext;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttpContext
extends RequestReplySenderContext<Request.Builder, Response>
implements Supplier<OkHttpContext> {
    private final Function<Request, String> urlMapper;
    private final Iterable<KeyValue> extraTags;
    private final Iterable<BiFunction<Request, Response, KeyValue>> contextSpecificTags;
    private final Iterable<KeyValue> unknownRequestTags;
    private final boolean includeHostTag;
    private final Request originalRequest;
    private OkHttpObservationInterceptor.CallState state;

    public OkHttpContext(Function<Request, String> urlMapper, Iterable<KeyValue> extraTags, Iterable<BiFunction<Request, Response, KeyValue>> contextSpecificTags, Iterable<KeyValue> unknownRequestTags, boolean includeHostTag, Request originalRequest) {
        super((carrier, key, value) -> {
            if (carrier != null) {
                carrier.header(key, value);
            }
        }, Kind.CLIENT);
        this.urlMapper = urlMapper;
        this.extraTags = extraTags;
        this.contextSpecificTags = contextSpecificTags;
        this.unknownRequestTags = unknownRequestTags;
        this.includeHostTag = includeHostTag;
        this.originalRequest = originalRequest;
    }

    public void setState(OkHttpObservationInterceptor.CallState state) {
        this.state = state;
    }

    public OkHttpObservationInterceptor.CallState getState() {
        return this.state;
    }

    public Function<Request, String> getUrlMapper() {
        return this.urlMapper;
    }

    public Iterable<KeyValue> getExtraTags() {
        return this.extraTags;
    }

    public Iterable<BiFunction<Request, Response, KeyValue>> getContextSpecificTags() {
        return this.contextSpecificTags;
    }

    public Iterable<KeyValue> getUnknownRequestTags() {
        return this.unknownRequestTags;
    }

    public boolean isIncludeHostTag() {
        return this.includeHostTag;
    }

    public Request getOriginalRequest() {
        return this.originalRequest;
    }

    @Override
    public OkHttpContext get() {
        return this;
    }
}

