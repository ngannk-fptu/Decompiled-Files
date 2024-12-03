/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.LogFactory
 */
package com.amazonaws.metrics;

import com.amazonaws.annotation.NotThreadSafe;
import com.amazonaws.metrics.ServiceMetricType;
import com.amazonaws.util.TimingInfo;
import org.apache.commons.logging.LogFactory;

@NotThreadSafe
public class ServiceLatencyProvider {
    private final long startNano;
    private long endNano;
    private final ServiceMetricType serviceMetricType;

    public ServiceLatencyProvider(ServiceMetricType type) {
        this.endNano = this.startNano = System.nanoTime();
        this.serviceMetricType = type;
    }

    public ServiceMetricType getServiceMetricType() {
        return this.serviceMetricType;
    }

    public ServiceLatencyProvider endTiming() {
        if (this.endNano != this.startNano) {
            throw new IllegalStateException();
        }
        this.endNano = System.nanoTime();
        return this;
    }

    public double getDurationMilli() {
        if (this.endNano == this.startNano) {
            LogFactory.getLog(this.getClass()).debug((Object)"Likely to be a missing invocation of endTiming().");
        }
        return TimingInfo.durationMilliOf(this.startNano, this.endNano);
    }

    public String getProviderId() {
        return super.toString();
    }

    public String toString() {
        return String.format("providerId=%s, serviceMetricType=%s, startNano=%d, endNano=%d", this.getProviderId(), this.serviceMetricType, this.startNano, this.endNano);
    }
}

