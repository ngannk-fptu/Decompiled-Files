/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.marshalling.api.Marshaller
 *  com.atlassian.marshalling.api.MarshallingException
 *  com.atlassian.vcache.internal.MetricLabel
 */
package com.atlassian.vcache.internal.core.metrics;

import com.atlassian.marshalling.api.Marshaller;
import com.atlassian.marshalling.api.MarshallingException;
import com.atlassian.vcache.internal.MetricLabel;
import com.atlassian.vcache.internal.core.metrics.CacheType;
import com.atlassian.vcache.internal.core.metrics.ElapsedTimer;
import com.atlassian.vcache.internal.core.metrics.MetricsRecorder;
import java.util.Objects;

class TimedMarshaller<T>
implements Marshaller<T> {
    private final Marshaller<T> delegate;
    private final MetricsRecorder metricsRecorder;
    private final String cacheName;

    TimedMarshaller(Marshaller<T> delegate, MetricsRecorder metricsRecorder, String cacheName) {
        this.delegate = Objects.requireNonNull(delegate);
        this.metricsRecorder = Objects.requireNonNull(metricsRecorder);
        this.cacheName = Objects.requireNonNull(cacheName);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public byte[] marshallToBytes(T obj) throws MarshallingException {
        try (ElapsedTimer ignored = new ElapsedTimer(t -> this.metricsRecorder.record(this.cacheName, CacheType.EXTERNAL, MetricLabel.TIMED_MARSHALL_CALL, t));){
            byte[] result = this.delegate.marshallToBytes(obj);
            this.metricsRecorder.record(this.cacheName, CacheType.EXTERNAL, MetricLabel.NUMBER_OF_BYTES_MARSHALLED, result.length);
            byte[] byArray = result;
            return byArray;
        }
        catch (MarshallingException me) {
            this.metricsRecorder.record(this.cacheName, CacheType.EXTERNAL, MetricLabel.NUMBER_OF_FAILED_MARSHALL, 1L);
            throw me;
        }
    }
}

