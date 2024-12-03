/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.NonNull
 *  org.apache.hc.client5.http.HttpRoute
 *  org.apache.hc.core5.pool.ConnPoolControl
 */
package io.micrometer.core.instrument.binder.httpcomponents.hc5;

import io.micrometer.common.lang.NonNull;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.MeterBinder;
import org.apache.hc.client5.http.HttpRoute;
import org.apache.hc.core5.pool.ConnPoolControl;

public class PoolingHttpClientConnectionManagerMetricsBinder
implements MeterBinder {
    private final ConnPoolControl<HttpRoute> connPoolControl;
    private final Iterable<Tag> tags;

    public PoolingHttpClientConnectionManagerMetricsBinder(ConnPoolControl<HttpRoute> connPoolControl, String name, String ... tags) {
        this(connPoolControl, name, Tags.of(tags));
    }

    public PoolingHttpClientConnectionManagerMetricsBinder(ConnPoolControl<HttpRoute> connPoolControl, String name, Iterable<Tag> tags) {
        this.connPoolControl = connPoolControl;
        this.tags = Tags.concat(tags, "httpclient", name);
    }

    @Override
    public void bindTo(@NonNull MeterRegistry registry) {
        this.registerTotalMetrics(registry);
    }

    private void registerTotalMetrics(MeterRegistry registry) {
        Gauge.builder("httpcomponents.httpclient.pool.total.max", this.connPoolControl, connPoolControl -> connPoolControl.getTotalStats().getMax()).description("The configured maximum number of allowed persistent connections for all routes.").tags(this.tags).register(registry);
        Gauge.builder("httpcomponents.httpclient.pool.total.connections", this.connPoolControl, connPoolControl -> connPoolControl.getTotalStats().getAvailable()).description("The number of persistent and available connections for all routes.").tags(this.tags).tag("state", "available").register(registry);
        Gauge.builder("httpcomponents.httpclient.pool.total.connections", this.connPoolControl, connPoolControl -> connPoolControl.getTotalStats().getLeased()).description("The number of persistent and leased connections for all routes.").tags(this.tags).tag("state", "leased").register(registry);
        Gauge.builder("httpcomponents.httpclient.pool.total.pending", this.connPoolControl, connPoolControl -> connPoolControl.getTotalStats().getPending()).description("The number of connection requests being blocked awaiting a free connection for all routes.").tags(this.tags).register(registry);
        Gauge.builder("httpcomponents.httpclient.pool.route.max.default", this.connPoolControl, ConnPoolControl::getDefaultMaxPerRoute).description("The configured default maximum number of allowed persistent connections per route.").tags(this.tags).register(registry);
    }
}

