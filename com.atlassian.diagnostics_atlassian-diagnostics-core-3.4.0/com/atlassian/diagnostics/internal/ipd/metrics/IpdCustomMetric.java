/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.ipd.internal.spi.IpdMetric
 *  com.atlassian.diagnostics.ipd.internal.spi.IpdMetricValue
 *  com.atlassian.diagnostics.ipd.internal.spi.MetricOptions
 *  com.atlassian.util.profiling.MetricTag$RequiredMetricTag
 *  com.google.common.collect.ImmutableList
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.codehaus.jackson.type.TypeReference
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.diagnostics.internal.ipd.metrics;

import com.atlassian.diagnostics.internal.ipd.IpdMetricBuilder;
import com.atlassian.diagnostics.internal.ipd.IpdMetricTypeVerifier;
import com.atlassian.diagnostics.internal.ipd.exceptions.IpdCustomMetricRegisterException;
import com.atlassian.diagnostics.internal.ipd.metrics.AbstractIpdMetric;
import com.atlassian.diagnostics.ipd.internal.spi.IpdMetric;
import com.atlassian.diagnostics.ipd.internal.spi.IpdMetricValue;
import com.atlassian.diagnostics.ipd.internal.spi.MetricOptions;
import com.atlassian.util.profiling.MetricTag;
import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import javax.management.MBeanServer;
import javax.management.MXBean;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IpdCustomMetric<T>
extends AbstractIpdMetric {
    private static final Logger LOG = LoggerFactory.getLogger(IpdCustomMetric.class);
    private static final String SINGLE_MX_BEAN_REQUIRED_EXCEPTION = "IpdCustomMetric requires the Type %s to implement exactly one interface with @MXBean annotation.";
    private static final TypeReference<Map<String, Object>> MAP_TYPE_REFERENCE = new TypeReference<Map<String, Object>>(){};
    private final Class<?> dataType;
    private final ObjectMapper mapper;
    private final MBeanServer mBeanServer;
    private final Consumer<IpdMetric> updateListener;
    private final Map<String, String> immutableTags;
    private final AtomicBoolean jmxRegistered = new AtomicBoolean(false);
    private final Class<?> mBeanInterface;
    private final T mBean;

    protected IpdCustomMetric(T mBean, ObjectMapper mapper, MBeanServer mBeanServer, MetricOptions options) {
        super(options);
        this.dataType = mBean.getClass();
        this.mapper = mapper;
        this.mBeanServer = mBeanServer;
        this.mBean = mBean;
        this.updateListener = options.getMetricUpdateListener();
        this.immutableTags = IpdCustomMetric.readTags(this.getObjectName());
        this.mBeanInterface = this.findMBeanInterface(this.dataType);
        if (this.isEnabled()) {
            this.registerMBean();
        }
    }

    private Class<?> findMBeanInterface(Class<?> type) {
        return Arrays.stream(type.getInterfaces()).filter(i -> i.isAnnotationPresent(MXBean.class)).reduce((a, b) -> {
            throw new IpdCustomMetricRegisterException(String.format(SINGLE_MX_BEAN_REQUIRED_EXCEPTION, type.getName()));
        }).orElseThrow(() -> new IpdCustomMetricRegisterException(String.format(SINGLE_MX_BEAN_REQUIRED_EXCEPTION, type.getName())));
    }

    public T getMBeanObject() {
        return this.mBean;
    }

    protected Map<String, Object> readAttributes() throws IOException {
        String attributes = this.mapper.writerWithType(this.mBeanInterface).writeValueAsString(this.mBean);
        return (Map)this.mapper.readValue(attributes, MAP_TYPE_REFERENCE);
    }

    public List<IpdMetricValue> readValues(boolean extraAttributes) {
        try {
            if (this.isEnabled()) {
                this.registerMBean();
            }
            return ImmutableList.of((Object)new IpdMetricValue(this.getMetricKey().getMetricName(), this.getObjectName().getCanonicalName(), this.immutableTags, this.readAttributes()));
        }
        catch (Exception e) {
            LOG.error(String.format("Couldn't read values for Custom IPD metric for metric %s of type %s", this.getMetricKey(), this.dataType.getName()), (Throwable)e);
            return Collections.emptyList();
        }
    }

    private void registerMBean() {
        if (!this.jmxRegistered.compareAndSet(false, true)) {
            return;
        }
        try {
            this.mBeanServer.registerMBean(this.mBean, this.getObjectName());
        }
        catch (Exception e) {
            throw new IpdCustomMetricRegisterException("Exception occurred while registering MBean for IpdCustomMetric with type " + this.dataType.getName(), e);
        }
    }

    public void unregisterJmx() {
        try {
            if (this.jmxRegistered.compareAndSet(true, false)) {
                this.mBeanServer.unregisterMBean(this.getObjectName());
            }
        }
        catch (Exception e) {
            throw new IpdCustomMetricRegisterException(String.format("Failed to unregister metric %s of type %s", this.getMetricKey(), this.dataType));
        }
    }

    public void update(Consumer<T> updater) {
        if (!this.isEnabled()) {
            return;
        }
        this.registerMBean();
        updater.accept(this.mBean);
        this.updateListener.accept(this);
    }

    public static <T> IpdMetricBuilder<IpdCustomMetric<T>> builder(String metricName, Class<T> type, MetricTag.RequiredMetricTag ... staticTags) {
        T mBeanObject;
        try {
            mBeanObject = type.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
        }
        catch (Exception e) {
            throw new IpdCustomMetricRegisterException(String.format("Couldn't create instance with default constructor for metric %s of type %s", metricName, type.getName()), e);
        }
        return IpdCustomMetric.builder(metricName, mBeanObject, staticTags);
    }

    public static <T> IpdMetricBuilder<IpdCustomMetric<T>> builder(String metricName, T object, MetricTag.RequiredMetricTag ... staticTags) {
        return new IpdMetricBuilder<IpdCustomMetric<T>>(IpdCustomMetric.appendToMetricName(metricName, "custom"), Arrays.asList(staticTags), options -> IpdCustomMetric.create(object, options), IpdCustomMetric.getMetricTypeVerifier(object.getClass()));
    }

    public static <T> IpdCustomMetric<T> create(T object, MetricOptions options) {
        return new IpdCustomMetric<T>(object, new ObjectMapper(), ManagementFactory.getPlatformMBeanServer(), options);
    }

    private static IpdMetricTypeVerifier getMetricTypeVerifier(Class<?> mBeanType) {
        return ipdMetric -> {
            if (!(ipdMetric instanceof IpdCustomMetric)) {
                throw new ClassCastException(String.format("Metric type was %s, but expected %s", ipdMetric.getClass(), IpdCustomMetric.class));
            }
            Class<?> thatMbeanType = ((IpdCustomMetric)ipdMetric).getMBeanObject().getClass();
            if (!thatMbeanType.equals(mBeanType)) {
                throw new ClassCastException(String.format("Metric type was IpdCustomMetric, but the mBean type was different: %s, but expected %s", thatMbeanType, mBeanType));
            }
        };
    }
}

