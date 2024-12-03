/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.amazonaws.util;

import com.amazonaws.annotation.NotThreadSafe;
import com.amazonaws.metrics.MetricType;
import com.amazonaws.util.AWSRequestMetrics;
import com.amazonaws.util.TimingInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@NotThreadSafe
public class AWSRequestMetricsFullSupport
extends AWSRequestMetrics {
    private final Map<String, List<Object>> properties = new HashMap<String, List<Object>>();
    private final Map<String, TimingInfo> eventsBeingProfiled = new HashMap<String, TimingInfo>();
    private static final Log latencyLogger = LogFactory.getLog((String)"com.amazonaws.latency");
    private static final Object KEY_VALUE_SEPARATOR = "=";
    private static final Object COMMA_SEPARATOR = ", ";

    public AWSRequestMetricsFullSupport() {
        super(TimingInfo.startTimingFullSupport());
    }

    @Override
    public void startEvent(String eventName) {
        this.eventsBeingProfiled.put(eventName, TimingInfo.startTimingFullSupport(System.currentTimeMillis(), System.nanoTime()));
    }

    @Override
    public void startEvent(MetricType f) {
        this.startEvent(f.name());
    }

    @Override
    public void endEvent(String eventName) {
        TimingInfo event = this.eventsBeingProfiled.get(eventName);
        if (event == null) {
            LogFactory.getLog(this.getClass()).warn((Object)("Trying to end an event which was never started: " + eventName));
            return;
        }
        event.endTiming();
        this.timingInfo.addSubMeasurement(eventName, TimingInfo.unmodifiableTimingInfo(event.getStartEpochTimeMilliIfKnown(), event.getStartTimeNano(), event.getEndTimeNano()));
    }

    @Override
    public void endEvent(MetricType f) {
        this.endEvent(f.name());
    }

    @Override
    public void incrementCounter(String event) {
        this.timingInfo.incrementCounter(event);
    }

    @Override
    public void incrementCounter(MetricType f) {
        this.incrementCounter(f.name());
    }

    @Override
    public void setCounter(String counterName, long count) {
        this.timingInfo.setCounter(counterName, count);
    }

    @Override
    public void setCounter(MetricType f, long count) {
        this.setCounter(f.name(), count);
    }

    @Override
    public void addProperty(String propertyName, Object value) {
        List<Object> propertyList = this.properties.get(propertyName);
        if (propertyList == null) {
            propertyList = new ArrayList<Object>();
            this.properties.put(propertyName, propertyList);
        }
        propertyList.add(value);
    }

    @Override
    public void addProperty(MetricType f, Object value) {
        this.addProperty(f.name(), value);
    }

    @Override
    public void log() {
        if (latencyLogger.isDebugEnabled()) {
            StringBuilder builder = new StringBuilder();
            for (Map.Entry<String, List<Object>> entry : this.properties.entrySet()) {
                this.keyValueFormat(entry.getKey(), entry.getValue(), builder);
            }
            for (Map.Entry<String, Object> entry : this.timingInfo.getAllCounters().entrySet()) {
                this.keyValueFormat(entry.getKey(), entry.getValue(), builder);
            }
            for (Map.Entry<String, Object> entry : this.timingInfo.getSubMeasurementsByName().entrySet()) {
                this.keyValueFormat(entry.getKey(), entry.getValue(), builder);
            }
            latencyLogger.debug((Object)builder.toString());
        }
    }

    private void keyValueFormat(Object key, Object value, StringBuilder builder) {
        builder.append(key).append(KEY_VALUE_SEPARATOR).append(value).append(COMMA_SEPARATOR);
    }

    @Override
    public List<Object> getProperty(String propertyName) {
        return this.properties.get(propertyName);
    }

    @Override
    public List<Object> getProperty(MetricType f) {
        return this.getProperty(f.name());
    }

    @Override
    public final boolean isEnabled() {
        return true;
    }
}

