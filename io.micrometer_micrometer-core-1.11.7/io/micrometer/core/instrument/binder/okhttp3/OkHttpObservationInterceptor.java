/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.KeyValue
 *  io.micrometer.common.KeyValues
 *  io.micrometer.common.lang.NonNull
 *  io.micrometer.common.lang.Nullable
 *  io.micrometer.observation.Observation
 *  io.micrometer.observation.ObservationRegistry
 *  okhttp3.Interceptor
 *  okhttp3.Interceptor$Chain
 *  okhttp3.Request
 *  okhttp3.Request$Builder
 *  okhttp3.Response
 */
package io.micrometer.core.instrument.binder.okhttp3;

import io.micrometer.common.KeyValue;
import io.micrometer.common.KeyValues;
import io.micrometer.common.lang.NonNull;
import io.micrometer.common.lang.Nullable;
import io.micrometer.core.instrument.binder.okhttp3.DefaultOkHttpObservationConvention;
import io.micrometer.core.instrument.binder.okhttp3.OkHttpContext;
import io.micrometer.core.instrument.binder.okhttp3.OkHttpObservationConvention;
import io.micrometer.core.instrument.binder.okhttp3.OkHttpObservationDocumentation;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttpObservationInterceptor
implements Interceptor {
    private final ObservationRegistry registry;
    private OkHttpObservationConvention observationConvention;
    private final String requestMetricName;
    private final Function<Request, String> urlMapper;
    private final Iterable<KeyValue> extraTags;
    private final Iterable<BiFunction<Request, Response, KeyValue>> contextSpecificTags;
    private final Iterable<KeyValue> unknownRequestTags;
    private final boolean includeHostTag;

    public OkHttpObservationInterceptor(ObservationRegistry registry, OkHttpObservationConvention observationConvention, String requestsMetricName, Function<Request, String> urlMapper, Iterable<KeyValue> extraTags, Iterable<BiFunction<Request, Response, KeyValue>> contextSpecificTags, Iterable<String> requestTagKeys, boolean includeHostTag) {
        this.registry = registry;
        this.observationConvention = observationConvention;
        this.requestMetricName = requestsMetricName;
        this.urlMapper = urlMapper;
        this.extraTags = extraTags;
        this.contextSpecificTags = contextSpecificTags;
        this.includeHostTag = includeHostTag;
        ArrayList<KeyValue> unknownRequestTags = new ArrayList<KeyValue>();
        for (String requestTagKey : requestTagKeys) {
            unknownRequestTags.add(KeyValue.of((String)requestTagKey, (String)"UNKNOWN"));
        }
        this.unknownRequestTags = unknownRequestTags;
    }

    public static Builder builder(ObservationRegistry registry, String name) {
        return new Builder(registry, name);
    }

    @NonNull
    public Response intercept(@NonNull Interceptor.Chain chain) throws IOException {
        Request request = chain.request();
        Request.Builder newRequestBuilder = request.newBuilder();
        OkHttpContext okHttpContext = new OkHttpContext(this.urlMapper, this.extraTags, this.contextSpecificTags, this.unknownRequestTags, this.includeHostTag, request);
        okHttpContext.setCarrier(newRequestBuilder);
        okHttpContext.setState(new CallState(newRequestBuilder.build()));
        Observation observation = OkHttpObservationDocumentation.DEFAULT.observation(this.observationConvention, new DefaultOkHttpObservationConvention(this.requestMetricName), okHttpContext, this.registry).start();
        Request newRequest = newRequestBuilder.build();
        CallState callState = new CallState(newRequest);
        okHttpContext.setState(callState);
        try {
            Response response = chain.proceed(newRequest);
            okHttpContext.setResponse(response);
            callState.response = response;
            Response response2 = response;
            return response2;
        }
        catch (IOException ex) {
            callState.exception = ex;
            observation.error((Throwable)ex);
            throw ex;
        }
        finally {
            observation.stop();
        }
    }

    public void setObservationConvention(OkHttpObservationConvention observationConvention) {
        this.observationConvention = observationConvention;
    }

    public static class Builder {
        public static final String URI_PATTERN = "URI_PATTERN";
        private final String name;
        private final ObservationRegistry registry;
        private Function<Request, String> uriMapper = request -> Optional.ofNullable(request.header(URI_PATTERN)).orElse("none");
        private KeyValues tags = KeyValues.empty();
        private Collection<BiFunction<Request, Response, KeyValue>> contextSpecificTags = new ArrayList<BiFunction<Request, Response, KeyValue>>();
        private boolean includeHostTag = true;
        private Iterable<String> requestTagKeys = Collections.emptyList();
        private OkHttpObservationConvention observationConvention;

        Builder(ObservationRegistry registry, String name) {
            this.registry = registry;
            this.name = name;
        }

        public Builder tags(Iterable<KeyValue> tags) {
            this.tags = this.tags.and(tags);
            return this;
        }

        public Builder observationConvention(OkHttpObservationConvention observationConvention) {
            this.observationConvention = observationConvention;
            return this;
        }

        public Builder tag(KeyValue tag) {
            this.tags = this.tags.and(new KeyValue[]{tag});
            return this;
        }

        public Builder tag(BiFunction<Request, Response, KeyValue> contextSpecificTag) {
            this.contextSpecificTags.add(contextSpecificTag);
            return this;
        }

        public Builder uriMapper(Function<Request, String> uriMapper) {
            this.uriMapper = uriMapper;
            return this;
        }

        public Builder includeHostTag(boolean includeHostTag) {
            this.includeHostTag = includeHostTag;
            return this;
        }

        public Builder requestTagKeys(String ... requestTagKeys) {
            return this.requestTagKeys(Arrays.asList(requestTagKeys));
        }

        public Builder requestTagKeys(Iterable<String> requestTagKeys) {
            this.requestTagKeys = requestTagKeys;
            return this;
        }

        public OkHttpObservationInterceptor build() {
            return new OkHttpObservationInterceptor(this.registry, this.observationConvention, this.name, this.uriMapper, (Iterable<KeyValue>)this.tags, this.contextSpecificTags, this.requestTagKeys, this.includeHostTag);
        }
    }

    static class CallState {
        @Nullable
        final Request request;
        @Nullable
        Response response;
        @Nullable
        IOException exception;

        CallState(@Nullable Request request) {
            this.request = request;
        }
    }
}

