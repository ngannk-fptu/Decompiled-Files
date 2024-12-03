/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.ipd.internal.spi.IpdMetric
 *  com.atlassian.diagnostics.ipd.internal.spi.IpdMetricValue
 *  com.atlassian.diagnostics.ipd.internal.spi.MetricOptions
 *  com.atlassian.util.profiling.MetricKey
 *  com.atlassian.util.profiling.Metrics
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.diagnostics.internal.ipd.metrics;

import com.atlassian.diagnostics.internal.ipd.metrics.AbstractIpdMetric;
import com.atlassian.diagnostics.ipd.internal.spi.IpdMetric;
import com.atlassian.diagnostics.ipd.internal.spi.IpdMetricValue;
import com.atlassian.diagnostics.ipd.internal.spi.MetricOptions;
import com.atlassian.util.profiling.MetricKey;
import com.atlassian.util.profiling.Metrics;
import java.lang.management.ManagementFactory;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.management.Attribute;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class IpdMicrometerMetric
extends AbstractIpdMetric {
    private static final Logger LOG = LoggerFactory.getLogger(IpdMicrometerMetric.class);
    protected final MBeanServer mBeanServer;
    protected final AtomicBoolean jmxRegistered;
    private final String[] allAttributes;
    private final String[] shortAttributes;
    private final Consumer<IpdMetric> onUpdateListener;

    protected IpdMicrometerMetric(MetricOptions options, List<String> allAttributes, List<String> shortAttributes) {
        this(options, ManagementFactory.getPlatformMBeanServer(), allAttributes, shortAttributes);
    }

    protected IpdMicrometerMetric(MetricOptions options, MBeanServer mBeanServer, List<String> allAttributes, List<String> shortAttributes) {
        super(options);
        this.mBeanServer = mBeanServer;
        this.onUpdateListener = options.getMetricUpdateListener();
        this.allAttributes = allAttributes.toArray(new String[0]);
        this.shortAttributes = shortAttributes.toArray(new String[0]);
        this.jmxRegistered = new AtomicBoolean(false);
    }

    protected void metricUpdated() {
        this.jmxRegistered.set(true);
        this.onUpdateListener.accept(this);
    }

    protected IpdMetricValue readMetricValue(ObjectName objectName, String[] attributes) {
        try {
            Map<String, Object> attributeValues = this.readAttributes(objectName, attributes);
            Map<String, String> tagValues = IpdMicrometerMetric.readTags(objectName);
            return new IpdMetricValue(this.getMetricKey().getMetricName(), objectName.getCanonicalName(), tagValues, attributeValues);
        }
        catch (Exception e) {
            return null;
        }
    }

    protected Map<String, Object> readAttributes(ObjectName objectName, String[] attributes) throws ReflectionException, InstanceNotFoundException {
        return this.mBeanServer.getAttributes(objectName, attributes).asList().stream().collect(Collectors.toMap(this::getKey, this::getValue));
    }

    public List<IpdMetricValue> readValues(boolean extraAttributes) {
        if (!this.jmxRegistered.get()) {
            LOG.debug("Couldn't read value for metric {} because it's missing value in JMX", (Object)this.getMetricKey());
            return Collections.emptyList();
        }
        try {
            return this.mBeanServer.queryNames(this.getDataSourceObjectName(), null).stream().map(queriedObjectName -> this.readMetricValue((ObjectName)queriedObjectName, extraAttributes ? this.allAttributes : this.shortAttributes)).filter(Objects::nonNull).collect(Collectors.toList());
        }
        catch (Exception e) {
            LOG.error(String.format("Couldn't read values for metric %s", this.getMetricKey()), (Throwable)e);
            return Collections.emptyList();
        }
    }

    protected ObjectName getDataSourceObjectName() {
        return this.getObjectName();
    }

    private String getKey(Attribute attribute) {
        return String.format("_%s", StringUtils.uncapitalize((String)attribute.getName()));
    }

    private String getValue(Attribute attribute) {
        return String.valueOf(attribute.getValue());
    }

    public void unregisterJmx() {
        if (!this.jmxRegistered.compareAndSet(true, false)) {
            return;
        }
        MetricKey metricKey = this.getMetricKey();
        try {
            Metrics.resetMetric((MetricKey)metricKey);
            LOG.debug("Unregistering metric: {}", (Object)metricKey);
        }
        catch (Exception e) {
            LOG.error("Couldn't unregister metric: {} due to error.", (Object)metricKey, (Object)e);
        }
    }
}

