/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.KeyValue
 *  io.micrometer.common.KeyValues
 *  io.micrometer.common.lang.NonNullApi
 *  io.micrometer.common.lang.NonNullFields
 *  io.micrometer.common.lang.Nullable
 *  okhttp3.Request
 *  okhttp3.Response
 */
package io.micrometer.core.instrument.binder.okhttp3;

import io.micrometer.common.KeyValue;
import io.micrometer.common.KeyValues;
import io.micrometer.common.lang.NonNullApi;
import io.micrometer.common.lang.NonNullFields;
import io.micrometer.common.lang.Nullable;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.http.Outcome;
import io.micrometer.core.instrument.binder.okhttp3.OkHttpContext;
import io.micrometer.core.instrument.binder.okhttp3.OkHttpObservationConvention;
import io.micrometer.core.instrument.binder.okhttp3.OkHttpObservationDocumentation;
import io.micrometer.core.instrument.binder.okhttp3.OkHttpObservationInterceptor;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import okhttp3.Request;
import okhttp3.Response;

@NonNullApi
@NonNullFields
public class DefaultOkHttpObservationConvention
implements OkHttpObservationConvention {
    static final boolean REQUEST_TAG_CLASS_EXISTS = DefaultOkHttpObservationConvention.getMethod(Class.class) != null;
    private static final String TAG_TARGET_SCHEME = "target.scheme";
    private static final String TAG_TARGET_HOST = "target.host";
    private static final String TAG_TARGET_PORT = "target.port";
    private static final String TAG_VALUE_UNKNOWN = "UNKNOWN";
    private static final KeyValues TAGS_TARGET_UNKNOWN = KeyValues.of((String[])new String[]{"target.scheme", "UNKNOWN", "target.host", "UNKNOWN", "target.port", "UNKNOWN"});
    private final String metricName;

    @Nullable
    private static Method getMethod(Class<?> ... parameterTypes) {
        try {
            return Request.class.getMethod("tag", parameterTypes);
        }
        catch (NoSuchMethodException e) {
            return null;
        }
    }

    public DefaultOkHttpObservationConvention(String metricName) {
        this.metricName = metricName;
    }

    public KeyValues getLowCardinalityKeyValues(OkHttpContext context) {
        OkHttpObservationInterceptor.CallState state = context.getState();
        Request request = state.request;
        boolean requestAvailable = request != null;
        Function<Request, String> urlMapper = context.getUrlMapper();
        Iterable<KeyValue> extraTags = context.getExtraTags();
        Iterable<BiFunction<Request, Response, KeyValue>> contextSpecificTags = context.getContextSpecificTags();
        Iterable<KeyValue> unknownRequestTags = context.getUnknownRequestTags();
        boolean includeHostTag = context.isIncludeHostTag();
        KeyValues keyValues = KeyValues.of((KeyValue[])new KeyValue[]{OkHttpObservationDocumentation.OkHttpLegacyLowCardinalityTags.METHOD.withValue(requestAvailable ? request.method() : TAG_VALUE_UNKNOWN), OkHttpObservationDocumentation.OkHttpLegacyLowCardinalityTags.URI.withValue(this.getUriTag(urlMapper, state, request)), OkHttpObservationDocumentation.OkHttpLegacyLowCardinalityTags.STATUS.withValue(this.getStatusMessage(state.response, state.exception)), OkHttpObservationDocumentation.OkHttpLegacyLowCardinalityTags.OUTCOME.withValue(this.getStatusOutcome(state.response).name())}).and(extraTags).and((Iterable)StreamSupport.stream(contextSpecificTags.spliterator(), false).map(contextTag -> (KeyValue)contextTag.apply(request, state.response)).map(tag -> KeyValue.of((String)tag.getKey(), (String)tag.getValue())).collect(Collectors.toList())).and(this.getRequestTags(request, unknownRequestTags)).and((Iterable)this.generateTagsForRoute(request));
        if (includeHostTag) {
            keyValues = KeyValues.of((Iterable)keyValues).and(new KeyValue[]{OkHttpObservationDocumentation.OkHttpLegacyLowCardinalityTags.HOST.withValue(requestAvailable ? request.url().host() : TAG_VALUE_UNKNOWN)});
        }
        return keyValues;
    }

    private String getUriTag(Function<Request, String> urlMapper, OkHttpObservationInterceptor.CallState state, @Nullable Request request) {
        if (request == null) {
            return TAG_VALUE_UNKNOWN;
        }
        return state.response != null && (state.response.code() == 404 || state.response.code() == 301) ? "NOT_FOUND" : urlMapper.apply(request);
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

    private Iterable<KeyValue> getRequestTags(@Nullable Request request, Iterable<KeyValue> unknownRequestTags) {
        Object requestTag;
        if (request == null) {
            return unknownRequestTags;
        }
        if (REQUEST_TAG_CLASS_EXISTS) {
            requestTag = (Tags)request.tag(Tags.class);
            if (requestTag != null) {
                return this.tagsToKeyValues(((Tags)requestTag).stream());
            }
            KeyValues keyValues = (KeyValues)request.tag(KeyValues.class);
            if (keyValues != null) {
                return keyValues;
            }
        }
        if ((requestTag = request.tag()) instanceof Tags) {
            return this.tagsToKeyValues(((Tags)requestTag).stream());
        }
        if (requestTag instanceof KeyValues) {
            return (KeyValues)requestTag;
        }
        return KeyValues.empty();
    }

    private List<KeyValue> tagsToKeyValues(Stream<Tag> requestTag) {
        return requestTag.map(tag -> KeyValue.of((String)tag.getKey(), (String)tag.getValue())).collect(Collectors.toList());
    }

    private KeyValues generateTagsForRoute(@Nullable Request request) {
        if (request == null) {
            return TAGS_TARGET_UNKNOWN;
        }
        return KeyValues.of((String[])new String[]{TAG_TARGET_SCHEME, request.url().scheme(), TAG_TARGET_HOST, request.url().host(), TAG_TARGET_PORT, Integer.toString(request.url().port())});
    }

    public String getName() {
        return this.metricName;
    }

    @Nullable
    public String getContextualName(OkHttpContext context) {
        Request request = context.getOriginalRequest();
        if (request == null) {
            return null;
        }
        return request.method();
    }
}

