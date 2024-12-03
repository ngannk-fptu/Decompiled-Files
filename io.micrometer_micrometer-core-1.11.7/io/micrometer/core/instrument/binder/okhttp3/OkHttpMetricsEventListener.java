/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.NonNullApi
 *  io.micrometer.common.lang.NonNullFields
 *  io.micrometer.common.lang.Nullable
 *  okhttp3.Call
 *  okhttp3.EventListener
 *  okhttp3.Request
 *  okhttp3.Response
 */
package io.micrometer.core.instrument.binder.okhttp3;

import io.micrometer.common.lang.NonNullApi;
import io.micrometer.common.lang.NonNullFields;
import io.micrometer.common.lang.Nullable;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.binder.http.Outcome;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import okhttp3.Call;
import okhttp3.EventListener;
import okhttp3.Request;
import okhttp3.Response;

@NonNullApi
@NonNullFields
public class OkHttpMetricsEventListener
extends EventListener {
    public static final String URI_PATTERN = "URI_PATTERN";
    private static final boolean REQUEST_TAG_CLASS_EXISTS = OkHttpMetricsEventListener.getMethod(Class.class) != null;
    private static final String TAG_TARGET_SCHEME = "target.scheme";
    private static final String TAG_TARGET_HOST = "target.host";
    private static final String TAG_TARGET_PORT = "target.port";
    private static final String TAG_VALUE_UNKNOWN = "UNKNOWN";
    private static final Tags TAGS_TARGET_UNKNOWN = Tags.of("target.scheme", "UNKNOWN", "target.host", "UNKNOWN", "target.port", "UNKNOWN");
    private final MeterRegistry registry;
    private final String requestsMetricName;
    private final Function<Request, String> urlMapper;
    private final Iterable<Tag> extraTags;
    private final Iterable<BiFunction<Request, Response, Tag>> contextSpecificTags;
    private final Iterable<Tag> unknownRequestTags;
    private final boolean includeHostTag;
    final ConcurrentMap<Call, CallState> callState = new ConcurrentHashMap<Call, CallState>();

    @Nullable
    private static Method getMethod(Class<?> ... parameterTypes) {
        try {
            return Request.class.getMethod("tag", parameterTypes);
        }
        catch (NoSuchMethodException e) {
            return null;
        }
    }

    protected OkHttpMetricsEventListener(MeterRegistry registry, String requestsMetricName, Function<Request, String> urlMapper, Iterable<Tag> extraTags, Iterable<BiFunction<Request, Response, Tag>> contextSpecificTags) {
        this(registry, requestsMetricName, urlMapper, extraTags, contextSpecificTags, Collections.emptyList(), true);
    }

    OkHttpMetricsEventListener(MeterRegistry registry, String requestsMetricName, Function<Request, String> urlMapper, Iterable<Tag> extraTags, Iterable<BiFunction<Request, Response, Tag>> contextSpecificTags, Iterable<String> requestTagKeys, boolean includeHostTag) {
        this.registry = registry;
        this.requestsMetricName = requestsMetricName;
        this.urlMapper = urlMapper;
        this.extraTags = extraTags;
        this.contextSpecificTags = contextSpecificTags;
        this.includeHostTag = includeHostTag;
        ArrayList<Tag> unknownRequestTags = new ArrayList<Tag>();
        for (String requestTagKey : requestTagKeys) {
            unknownRequestTags.add(Tag.of(requestTagKey, TAG_VALUE_UNKNOWN));
        }
        this.unknownRequestTags = unknownRequestTags;
    }

    public static Builder builder(MeterRegistry registry, String name) {
        return new Builder(registry, name);
    }

    public void callStart(Call call) {
        this.callState.put(call, new CallState(this.registry.config().clock().monotonicTime(), call.request()));
    }

    public void callFailed(Call call, IOException e) {
        CallState state = (CallState)this.callState.remove(call);
        if (state != null) {
            state.exception = e;
            this.time(state);
        }
    }

    public void callEnd(Call call) {
        this.callState.remove(call);
    }

    public void responseHeadersEnd(Call call, Response response) {
        CallState state = (CallState)this.callState.remove(call);
        if (state != null) {
            state.response = response;
            this.time(state);
        }
    }

    void time(CallState state) {
        Request request = state.request;
        boolean requestAvailable = request != null;
        Tags tags = Tags.of("method", requestAvailable ? request.method() : TAG_VALUE_UNKNOWN, "uri", this.getUriTag(state, request), "status", this.getStatusMessage(state.response, state.exception)).and(this.getStatusOutcome(state.response).asTag()).and(this.extraTags).and(StreamSupport.stream(this.contextSpecificTags.spliterator(), false).map(contextTag -> (Tag)contextTag.apply(request, state.response)).collect(Collectors.toList())).and(this.getRequestTags(request)).and(this.generateTagsForRoute(request));
        if (this.includeHostTag) {
            tags = Tags.of(tags).and("host", requestAvailable ? request.url().host() : TAG_VALUE_UNKNOWN);
        }
        ((Timer.Builder)Timer.builder(this.requestsMetricName).tags((Iterable)tags)).description("Timer of OkHttp operation").register(this.registry).record(this.registry.config().clock().monotonicTime() - state.startTime, TimeUnit.NANOSECONDS);
    }

    private Tags generateTagsForRoute(@Nullable Request request) {
        if (request == null) {
            return TAGS_TARGET_UNKNOWN;
        }
        return Tags.of(TAG_TARGET_SCHEME, request.url().scheme(), TAG_TARGET_HOST, request.url().host(), TAG_TARGET_PORT, Integer.toString(request.url().port()));
    }

    private String getUriTag(CallState state, @Nullable Request request) {
        if (request == null) {
            return TAG_VALUE_UNKNOWN;
        }
        return state.response != null && (state.response.code() == 404 || state.response.code() == 301) ? "NOT_FOUND" : this.urlMapper.apply(request);
    }

    private Iterable<Tag> getRequestTags(@Nullable Request request) {
        Object requestTag;
        if (request == null) {
            return this.unknownRequestTags;
        }
        if (REQUEST_TAG_CLASS_EXISTS && (requestTag = (Tags)request.tag(Tags.class)) != null) {
            return requestTag;
        }
        requestTag = request.tag();
        if (requestTag instanceof Tags) {
            return (Tags)requestTag;
        }
        return Tags.empty();
    }

    private Outcome getStatusOutcome(@Nullable Response response) {
        if (response == null) {
            return Outcome.UNKNOWN;
        }
        return Outcome.forStatus(response.code());
    }

    private String getStatusMessage(@Nullable Response response, @Nullable IOException exception) {
        if (exception != null) {
            return "IO_ERROR";
        }
        if (response == null) {
            return "CLIENT_ERROR";
        }
        return Integer.toString(response.code());
    }

    public static class Builder {
        private final MeterRegistry registry;
        private final String name;
        private Function<Request, String> uriMapper = request -> Optional.ofNullable(request.header(OkHttpMetricsEventListener.URI_PATTERN)).orElse("none");
        private Tags tags = Tags.empty();
        private Collection<BiFunction<Request, Response, Tag>> contextSpecificTags = new ArrayList<BiFunction<Request, Response, Tag>>();
        private boolean includeHostTag = true;
        private Iterable<String> requestTagKeys = Collections.emptyList();

        Builder(MeterRegistry registry, String name) {
            this.registry = registry;
            this.name = name;
        }

        public Builder tags(Iterable<Tag> tags) {
            this.tags = this.tags.and(tags);
            return this;
        }

        public Builder tag(Tag tag) {
            this.tags = this.tags.and(tag);
            return this;
        }

        public Builder tag(BiFunction<Request, Response, Tag> contextSpecificTag) {
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

        public OkHttpMetricsEventListener build() {
            return new OkHttpMetricsEventListener(this.registry, this.name, this.uriMapper, this.tags, this.contextSpecificTags, this.requestTagKeys, this.includeHostTag);
        }
    }

    static class CallState {
        final long startTime;
        @Nullable
        final Request request;
        @Nullable
        Response response;
        @Nullable
        IOException exception;

        CallState(long startTime, @Nullable Request request) {
            this.startTime = startTime;
            this.request = request;
        }
    }
}

