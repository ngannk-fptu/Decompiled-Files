/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.ipd.internal.spi.IpdMetric
 *  com.atlassian.diagnostics.ipd.internal.spi.MetricOptions
 *  com.atlassian.util.profiling.MetricKey
 *  com.atlassian.util.profiling.MetricTag$RequiredMetricTag
 *  com.atlassian.util.profiling.micrometer.util.QualifiedCompatibleHierarchicalNameMapper
 *  io.micrometer.core.instrument.Meter$Id
 *  io.micrometer.core.instrument.Meter$Type
 *  io.micrometer.core.instrument.Tag
 *  io.micrometer.core.instrument.Tags
 *  io.micrometer.core.instrument.config.NamingConvention
 *  io.micrometer.core.instrument.util.HierarchicalNameMapper
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.diagnostics.internal.ipd.metrics;

import com.atlassian.diagnostics.ipd.internal.spi.IpdMetric;
import com.atlassian.diagnostics.ipd.internal.spi.MetricOptions;
import com.atlassian.util.profiling.MetricKey;
import com.atlassian.util.profiling.MetricTag;
import com.atlassian.util.profiling.micrometer.util.QualifiedCompatibleHierarchicalNameMapper;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.config.NamingConvention;
import io.micrometer.core.instrument.util.HierarchicalNameMapper;
import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import org.apache.commons.lang3.StringUtils;

public abstract class AbstractIpdMetric
implements IpdMetric {
    private static final String JMX_TAG_PREFIX = "tag.";
    public static final HierarchicalNameMapper DEFAULT_NAME_MAPPER = new QualifiedCompatibleHierarchicalNameMapper();
    private final MetricKey metricKey;
    private final Supplier<Boolean> enabledCheck;
    private final ObjectName objectName;
    private final MetricOptions metricOptions;
    private boolean closed = false;

    protected AbstractIpdMetric(MetricOptions metricOptions) {
        this.metricKey = metricOptions.getIpdMetricKey();
        this.enabledCheck = metricOptions.getEnabledCheck();
        this.objectName = AbstractIpdMetric.constructObjectName(metricOptions.getProductPrefix(), this.metricKey);
        this.metricOptions = metricOptions;
    }

    public MetricKey getMetricKey() {
        return this.metricKey;
    }

    public MetricOptions getOptions() {
        return this.metricOptions;
    }

    public boolean isEnabled() {
        return !this.closed && Boolean.TRUE.equals(this.enabledCheck.get());
    }

    public void close() {
        this.unregisterJmx();
        this.closed = true;
    }

    public ObjectName getObjectName() {
        return this.objectName;
    }

    private static ObjectName constructObjectName(String productPrefix, MetricKey metricKey) {
        Meter.Id dummyMeterId = new Meter.Id(metricKey.getMetricName(), AbstractIpdMetric.getMicrometerTags(metricKey.getTags()), null, null, Meter.Type.OTHER);
        String objectName = productPrefix + ":" + DEFAULT_NAME_MAPPER.toHierarchicalName(dummyMeterId, NamingConvention.dot);
        try {
            return new ObjectName(objectName);
        }
        catch (MalformedObjectNameException e) {
            throw new RuntimeException(e);
        }
    }

    private static Tags getMicrometerTags(Collection<MetricTag.RequiredMetricTag> tags) {
        return Tags.of((Iterable)tags.stream().map(t -> Tag.of((String)t.getKey(), (String)t.getValue())).collect(Collectors.toList()));
    }

    protected static String appendToMetricName(String name, String postfix) {
        if (StringUtils.isEmpty((CharSequence)name) || name.endsWith(".")) {
            return name + postfix;
        }
        return name + "." + postfix;
    }

    protected static Map<String, String> readTags(ObjectName objectName) {
        return objectName.getKeyPropertyList().entrySet().stream().filter(entry -> ((String)entry.getKey()).startsWith(JMX_TAG_PREFIX)).collect(Collectors.toMap(entry -> ((String)entry.getKey()).substring(JMX_TAG_PREFIX.length()), Map.Entry::getValue));
    }
}

