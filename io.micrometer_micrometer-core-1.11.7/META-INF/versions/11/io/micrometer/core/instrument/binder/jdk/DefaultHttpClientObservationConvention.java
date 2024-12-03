/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.KeyValue
 *  io.micrometer.common.KeyValues
 *  io.micrometer.common.lang.NonNull
 *  io.micrometer.common.lang.Nullable
 */
package io.micrometer.core.instrument.binder.jdk;

import io.micrometer.common.KeyValue;
import io.micrometer.common.KeyValues;
import io.micrometer.common.lang.NonNull;
import io.micrometer.common.lang.Nullable;
import io.micrometer.core.instrument.binder.http.Outcome;
import io.micrometer.core.instrument.binder.jdk.HttpClientContext;
import io.micrometer.core.instrument.binder.jdk.HttpClientObservationConvention;
import io.micrometer.core.instrument.binder.jdk.HttpClientObservationDocumentation;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.Function;

public class DefaultHttpClientObservationConvention
implements HttpClientObservationConvention {
    public static DefaultHttpClientObservationConvention INSTANCE = new DefaultHttpClientObservationConvention();

    public KeyValues getLowCardinalityKeyValues(HttpClientContext context) {
        if (context.getCarrier() == null) {
            return KeyValues.empty();
        }
        HttpRequest httpRequest = ((HttpRequest.Builder)context.getCarrier()).build();
        KeyValues keyValues = KeyValues.of((KeyValue[])new KeyValue[]{HttpClientObservationDocumentation.LowCardinalityKeys.METHOD.withValue(httpRequest.method()), HttpClientObservationDocumentation.LowCardinalityKeys.URI.withValue(this.getUriTag(httpRequest, (HttpResponse)context.getResponse(), context.getUriMapper()))});
        if (context.getResponse() != null) {
            keyValues = keyValues.and(new KeyValue[]{HttpClientObservationDocumentation.LowCardinalityKeys.STATUS.withValue(String.valueOf(((HttpResponse)context.getResponse()).statusCode()))}).and(new KeyValue[]{HttpClientObservationDocumentation.LowCardinalityKeys.OUTCOME.withValue(Outcome.forStatus(((HttpResponse)context.getResponse()).statusCode()).name())});
        }
        return keyValues;
    }

    String getUriTag(@Nullable HttpRequest request, @Nullable HttpResponse<?> httpResponse, Function<HttpRequest, String> uriMapper) {
        if (request == null) {
            return null;
        }
        return httpResponse != null && (httpResponse.statusCode() == 404 || httpResponse.statusCode() == 301) ? "NOT_FOUND" : uriMapper.apply(request);
    }

    @NonNull
    public String getName() {
        return "http.client.requests";
    }

    @Nullable
    public String getContextualName(HttpClientContext context) {
        if (context.getCarrier() == null) {
            return null;
        }
        return "HTTP " + ((HttpRequest.Builder)context.getCarrier()).build().method();
    }
}

