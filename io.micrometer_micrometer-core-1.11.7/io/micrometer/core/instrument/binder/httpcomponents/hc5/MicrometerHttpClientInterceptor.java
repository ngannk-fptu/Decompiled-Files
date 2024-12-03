/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.http.HttpRequest
 *  org.apache.hc.core5.http.HttpRequestInterceptor
 *  org.apache.hc.core5.http.HttpResponseInterceptor
 *  org.apache.hc.core5.http.protocol.HttpContext
 */
package io.micrometer.core.instrument.binder.httpcomponents.hc5;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.binder.http.Outcome;
import io.micrometer.core.instrument.binder.httpcomponents.hc5.DefaultUriMapper;
import io.micrometer.core.instrument.binder.httpcomponents.hc5.HttpContextUtils;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpRequestInterceptor;
import org.apache.hc.core5.http.HttpResponseInterceptor;
import org.apache.hc.core5.http.protocol.HttpContext;

public class MicrometerHttpClientInterceptor {
    private static final String METER_NAME = "httpcomponents.httpclient.request";
    private final Map<HttpContext, Timer.ResourceSample> timerByHttpContext = new ConcurrentHashMap<HttpContext, Timer.ResourceSample>();
    private final HttpRequestInterceptor requestInterceptor = (request, entityDetails, context) -> this.timerByHttpContext.put(context, (Timer.ResourceSample)Timer.resource(meterRegistry, METER_NAME).tags("method", request.getMethod(), "uri", (String)uriMapper.apply(request)));
    private final HttpResponseInterceptor responseInterceptor = (response, entityDetails, context) -> ((Timer.ResourceSample)((Timer.ResourceSample)((Timer.ResourceSample)((Timer.ResourceSample)this.timerByHttpContext.remove(context).tag("status", Integer.toString(response.getCode()))).tag("outcome", Outcome.forStatus(response.getCode()).name())).tags(exportTagsForRoute ? HttpContextUtils.generateTagsForRoute(context) : Tags.empty())).tags(extraTags)).close();

    public MicrometerHttpClientInterceptor(MeterRegistry meterRegistry, Function<HttpRequest, String> uriMapper, Iterable<Tag> extraTags, boolean exportTagsForRoute) {
    }

    public MicrometerHttpClientInterceptor(MeterRegistry meterRegistry, Iterable<Tag> extraTags, boolean exportTagsForRoute) {
        this(meterRegistry, new DefaultUriMapper(), extraTags, exportTagsForRoute);
    }

    public HttpRequestInterceptor getRequestInterceptor() {
        return this.requestInterceptor;
    }

    public HttpResponseInterceptor getResponseInterceptor() {
        return this.responseInterceptor;
    }
}

