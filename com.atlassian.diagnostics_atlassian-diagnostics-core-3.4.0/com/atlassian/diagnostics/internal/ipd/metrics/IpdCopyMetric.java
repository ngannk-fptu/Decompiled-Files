/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.ipd.internal.spi.IpdMetric
 *  com.atlassian.diagnostics.ipd.internal.spi.MetricOptions
 *  com.atlassian.util.profiling.MetricTag$RequiredMetricTag
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.diagnostics.internal.ipd.metrics;

import com.atlassian.diagnostics.internal.ipd.IpdMetricBuilder;
import com.atlassian.diagnostics.internal.ipd.exceptions.IpdRegisterException;
import com.atlassian.diagnostics.internal.ipd.metrics.IpdMicrometerMetric;
import com.atlassian.diagnostics.internal.jmx.ReadOnlyProxyMBean;
import com.atlassian.diagnostics.ipd.internal.spi.IpdMetric;
import com.atlassian.diagnostics.ipd.internal.spi.MetricOptions;
import com.atlassian.util.profiling.MetricTag;
import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.List;
import javax.management.DynamicMBean;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IpdCopyMetric
extends IpdMicrometerMetric {
    private static final Logger LOG = LoggerFactory.getLogger(IpdCopyMetric.class);
    private final ObjectName objectToCopy;
    private final DynamicMBean jmxBean;

    protected IpdCopyMetric(MetricOptions options, MBeanServer mBeanServer, ObjectName objectToCopy, List<String> allAttributes, List<String> shortAttributes) {
        super(options, mBeanServer, allAttributes, shortAttributes);
        this.objectToCopy = objectToCopy;
        this.jmxBean = new ReadOnlyProxyMBean(objectToCopy, mBeanServer);
        this.ensureJmxBeanIsRegistered();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void ensureJmxBeanIsRegistered() {
        if (this.mBeanServer.isRegistered(this.getObjectName())) {
            this.jmxRegistered.set(true);
        } else {
            IpdCopyMetric ipdCopyMetric = this;
            synchronized (ipdCopyMetric) {
                if (!this.jmxRegistered.get()) {
                    try {
                        this.mBeanServer.registerMBean(this.jmxBean, this.getObjectName());
                        this.jmxRegistered.set(true);
                    }
                    catch (Exception e) {
                        throw new IpdRegisterException(String.format("Unable to register JMX bean for metric %s", this.getMetricKey().getMetricName()), e);
                    }
                }
            }
        }
    }

    private boolean sourceBeanExists() {
        return this.mBeanServer.isRegistered(this.objectToCopy);
    }

    public void update() {
        if (!this.sourceBeanExists()) {
            this.unregisterJmx();
            this.removeJmxBeanIfExists();
        } else {
            this.ensureJmxBeanIsRegistered();
        }
    }

    private void removeJmxBeanIfExists() {
        try {
            if (this.mBeanServer.isRegistered(this.getObjectName())) {
                this.mBeanServer.unregisterMBean(this.getObjectName());
            }
        }
        catch (Exception e) {
            LOG.warn("Unable to unregister JMX bean for metric {}", (Object)this.getMetricKey().getMetricName(), (Object)e);
        }
    }

    @Override
    protected ObjectName getDataSourceObjectName() {
        return this.objectToCopy;
    }

    @Override
    public void unregisterJmx() {
        if (!this.jmxRegistered.compareAndSet(true, false)) {
            return;
        }
        this.removeJmxBeanIfExists();
    }

    public static IpdMetricBuilder<IpdCopyMetric> builder(String metricName, ObjectName objectNameToCopy, List<String> allAttributes, List<String> shortAttributes, MetricTag.RequiredMetricTag ... staticTags) {
        return new IpdMetricBuilder<IpdCopyMetric>(metricName, Arrays.asList(staticTags), options -> IpdCopyMetric.create(objectNameToCopy, options, allAttributes, shortAttributes), IpdCopyMetric::verifyExpectedMetricType);
    }

    private static IpdCopyMetric create(ObjectName objectToCopy, MetricOptions options, List<String> allAttributes, List<String> shortAttributes) {
        return new IpdCopyMetric(options, ManagementFactory.getPlatformMBeanServer(), objectToCopy, allAttributes, shortAttributes);
    }

    private static void verifyExpectedMetricType(IpdMetric ipdMetric) throws ClassCastException {
        if (ipdMetric instanceof IpdCopyMetric) {
            return;
        }
        throw new ClassCastException(String.format("Metric type was %s, but expected %s", ipdMetric.getClass(), IpdCopyMetric.class));
    }
}

