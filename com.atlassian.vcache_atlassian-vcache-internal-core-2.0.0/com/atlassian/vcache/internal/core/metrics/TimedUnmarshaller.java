/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.marshalling.api.MarshallingException
 *  com.atlassian.marshalling.api.Unmarshaller
 *  com.atlassian.vcache.internal.MetricLabel
 */
package com.atlassian.vcache.internal.core.metrics;

import com.atlassian.marshalling.api.MarshallingException;
import com.atlassian.marshalling.api.Unmarshaller;
import com.atlassian.vcache.internal.MetricLabel;
import com.atlassian.vcache.internal.core.metrics.CacheType;
import com.atlassian.vcache.internal.core.metrics.ElapsedTimer;
import com.atlassian.vcache.internal.core.metrics.MetricsRecorder;
import java.util.Objects;

class TimedUnmarshaller<T>
implements Unmarshaller<T> {
    private final Unmarshaller<T> delegate;
    private final MetricsRecorder metricsRecorder;
    private final String cacheName;

    TimedUnmarshaller(Unmarshaller<T> delegate, MetricsRecorder metricsRecorder, String cacheName) {
        this.delegate = Objects.requireNonNull(delegate);
        this.metricsRecorder = Objects.requireNonNull(metricsRecorder);
        this.cacheName = Objects.requireNonNull(cacheName);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public T unmarshallFrom(byte[] raw) throws MarshallingException {
        this.metricsRecorder.record(this.cacheName, CacheType.EXTERNAL, MetricLabel.NUMBER_OF_BYTES_UNMARSHALLED, raw.length);
        try (ElapsedTimer ignored = new ElapsedTimer(t -> this.metricsRecorder.record(this.cacheName, CacheType.EXTERNAL, MetricLabel.TIMED_UNMARSHALL_CALL, t));){
            Object object = this.delegate.unmarshallFrom(raw);
            return (T)object;
        }
        catch (MarshallingException me) {
            this.metricsRecorder.record(this.cacheName, CacheType.EXTERNAL, MetricLabel.NUMBER_OF_FAILED_UNMARSHALL, 1L);
            throw me;
        }
    }
}

