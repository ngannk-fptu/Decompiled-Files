/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.KeyValue
 *  io.micrometer.common.KeyValues
 *  io.micrometer.common.lang.Nullable
 *  org.apache.http.HttpException
 *  org.apache.http.HttpRequest
 *  org.apache.http.HttpResponse
 */
package io.micrometer.core.instrument.binder.httpcomponents;

import io.micrometer.common.KeyValue;
import io.micrometer.common.KeyValues;
import io.micrometer.common.lang.Nullable;
import io.micrometer.core.instrument.binder.http.Outcome;
import io.micrometer.core.instrument.binder.httpcomponents.ApacheHttpClientContext;
import io.micrometer.core.instrument.binder.httpcomponents.ApacheHttpClientObservationConvention;
import io.micrometer.core.instrument.binder.httpcomponents.ApacheHttpClientObservationDocumentation;
import io.micrometer.core.instrument.binder.httpcomponents.HttpContextUtils;
import java.io.IOException;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

public class DefaultApacheHttpClientObservationConvention
implements ApacheHttpClientObservationConvention {
    public static final DefaultApacheHttpClientObservationConvention INSTANCE = new DefaultApacheHttpClientObservationConvention();

    protected DefaultApacheHttpClientObservationConvention() {
    }

    public String getName() {
        return "httpcomponents.httpclient.request";
    }

    public String getContextualName(ApacheHttpClientContext context) {
        return "HTTP " + this.getMethodString((HttpRequest)context.getCarrier());
    }

    public KeyValues getLowCardinalityKeyValues(ApacheHttpClientContext context) {
        KeyValues keyValues = KeyValues.of((KeyValue[])new KeyValue[]{ApacheHttpClientObservationDocumentation.ApacheHttpClientKeyNames.METHOD.withValue(this.getMethodString((HttpRequest)context.getCarrier())), ApacheHttpClientObservationDocumentation.ApacheHttpClientKeyNames.URI.withValue(context.getUriMapper().apply((HttpRequest)context.getCarrier())), ApacheHttpClientObservationDocumentation.ApacheHttpClientKeyNames.STATUS.withValue(this.getStatusValue((HttpResponse)context.getResponse(), context.getError())), ApacheHttpClientObservationDocumentation.ApacheHttpClientKeyNames.OUTCOME.withValue(this.getStatusOutcome((HttpResponse)context.getResponse()).name())});
        if (context.shouldExportTagsForRoute()) {
            keyValues = keyValues.and(HttpContextUtils.generateTagStringsForRoute(context.getApacheHttpContext()));
        }
        return keyValues;
    }

    Outcome getStatusOutcome(@Nullable HttpResponse response) {
        return response != null ? Outcome.forStatus(response.getStatusLine().getStatusCode()) : Outcome.UNKNOWN;
    }

    String getStatusValue(@Nullable HttpResponse response, Throwable error) {
        if (error instanceof IOException || error instanceof HttpException || error instanceof RuntimeException) {
            return "IO_ERROR";
        }
        return response != null ? Integer.toString(response.getStatusLine().getStatusCode()) : "CLIENT_ERROR";
    }

    String getMethodString(@Nullable HttpRequest request) {
        return request != null && request.getRequestLine().getMethod() != null ? request.getRequestLine().getMethod() : "UNKNOWN";
    }
}

