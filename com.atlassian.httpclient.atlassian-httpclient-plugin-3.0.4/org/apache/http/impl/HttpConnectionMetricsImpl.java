/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl;

import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpConnectionMetrics;
import org.apache.http.io.HttpTransportMetrics;

public class HttpConnectionMetricsImpl
implements HttpConnectionMetrics {
    public static final String REQUEST_COUNT = "http.request-count";
    public static final String RESPONSE_COUNT = "http.response-count";
    public static final String SENT_BYTES_COUNT = "http.sent-bytes-count";
    public static final String RECEIVED_BYTES_COUNT = "http.received-bytes-count";
    private final HttpTransportMetrics inTransportMetric;
    private final HttpTransportMetrics outTransportMetric;
    private long requestCount = 0L;
    private long responseCount = 0L;
    private Map<String, Object> metricsCache;

    public HttpConnectionMetricsImpl(HttpTransportMetrics inTransportMetric, HttpTransportMetrics outTransportMetric) {
        this.inTransportMetric = inTransportMetric;
        this.outTransportMetric = outTransportMetric;
    }

    @Override
    public long getReceivedBytesCount() {
        return this.inTransportMetric != null ? this.inTransportMetric.getBytesTransferred() : -1L;
    }

    @Override
    public long getSentBytesCount() {
        return this.outTransportMetric != null ? this.outTransportMetric.getBytesTransferred() : -1L;
    }

    @Override
    public long getRequestCount() {
        return this.requestCount;
    }

    public void incrementRequestCount() {
        ++this.requestCount;
    }

    @Override
    public long getResponseCount() {
        return this.responseCount;
    }

    public void incrementResponseCount() {
        ++this.responseCount;
    }

    @Override
    public Object getMetric(String metricName) {
        Object value = null;
        if (this.metricsCache != null) {
            value = this.metricsCache.get(metricName);
        }
        if (value == null) {
            if (REQUEST_COUNT.equals(metricName)) {
                value = this.requestCount;
            } else if (RESPONSE_COUNT.equals(metricName)) {
                value = this.responseCount;
            } else {
                if (RECEIVED_BYTES_COUNT.equals(metricName)) {
                    return this.inTransportMetric != null ? Long.valueOf(this.inTransportMetric.getBytesTransferred()) : null;
                }
                if (SENT_BYTES_COUNT.equals(metricName)) {
                    return this.outTransportMetric != null ? Long.valueOf(this.outTransportMetric.getBytesTransferred()) : null;
                }
            }
        }
        return value;
    }

    public void setMetric(String metricName, Object obj) {
        if (this.metricsCache == null) {
            this.metricsCache = new HashMap<String, Object>();
        }
        this.metricsCache.put(metricName, obj);
    }

    @Override
    public void reset() {
        if (this.outTransportMetric != null) {
            this.outTransportMetric.reset();
        }
        if (this.inTransportMetric != null) {
            this.inTransportMetric.reset();
        }
        this.requestCount = 0L;
        this.responseCount = 0L;
        this.metricsCache = null;
    }
}

