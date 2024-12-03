/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Preconditions
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.impl.content.render.xhtml.analytics;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.impl.content.render.xhtml.analytics.MarshallerMetricsCollector;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class MetricsCollectingMarshaller<T>
implements Marshaller<T> {
    private final Marshaller<T> delegate;
    private final MarshallerMetricsCollector metricsCollector;

    public static <T> @NonNull Marshaller<T> forMarshaller(MarshallerMetricsCollector metricsCollector, Marshaller<T> marshaller) {
        return new MetricsCollectingMarshaller<T>(marshaller, metricsCollector);
    }

    @VisibleForTesting
    MetricsCollectingMarshaller(Marshaller<T> delegate, MarshallerMetricsCollector metricsCollector) {
        this.metricsCollector = (MarshallerMetricsCollector)Preconditions.checkNotNull((Object)metricsCollector);
        this.delegate = (Marshaller)Preconditions.checkNotNull(delegate);
    }

    @Override
    public Streamable marshal(T object, @Nullable ConversionContext conversionContext) throws XhtmlException {
        MarshallerMetricsCollector.Timer executionTimer = this.metricsCollector.executionStart();
        Streamable result = this.delegate.marshal(object, conversionContext);
        executionTimer.stop();
        return writer -> {
            MarshallerMetricsCollector.Timer streamingTimer = this.metricsCollector.streamingStart();
            result.writeTo(writer);
            streamingTimer.stop();
            this.metricsCollector.publish();
        };
    }
}

