/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.HttpRequest
 *  org.apache.http.HttpRequestInterceptor
 *  org.apache.http.HttpResponseInterceptor
 *  org.apache.http.protocol.HttpContext
 */
package io.micrometer.core.instrument.binder.httpcomponents;

import io.micrometer.core.annotation.Incubating;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.binder.http.Outcome;
import io.micrometer.core.instrument.binder.httpcomponents.DefaultUriMapper;
import io.micrometer.core.instrument.binder.httpcomponents.HttpContextUtils;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.protocol.HttpContext;

@Incubating(since="1.4.0")
public class MicrometerHttpClientInterceptor {
    private static final String METER_NAME = "httpcomponents.httpclient.request";
    private final Map<HttpContext, Timer.ResourceSample> timerByHttpContext = new ConcurrentHashMap<HttpContext, Timer.ResourceSample>();
    private final HttpRequestInterceptor requestInterceptor = (request, context) -> this.timerByHttpContext.put(context, (Timer.ResourceSample)Timer.resource(meterRegistry, METER_NAME).tags("method", request.getRequestLine().getMethod(), "uri", (String)uriMapper.apply(request)));
    private final HttpResponseInterceptor responseInterceptor = (response, context) -> ((Timer.ResourceSample)((Timer.ResourceSample)((Timer.ResourceSample)((Timer.ResourceSample)this.timerByHttpContext.remove(context).tag("status", Integer.toString(response.getStatusLine().getStatusCode()))).tag("outcome", Outcome.forStatus(response.getStatusLine().getStatusCode()).name())).tags(exportTagsForRoute ? HttpContextUtils.generateTagsForRoute(context) : Tags.empty())).tags(extraTags)).close();

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

