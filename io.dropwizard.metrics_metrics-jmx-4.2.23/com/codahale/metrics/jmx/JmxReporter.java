/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.codahale.metrics.Counter
 *  com.codahale.metrics.Gauge
 *  com.codahale.metrics.Histogram
 *  com.codahale.metrics.Meter
 *  com.codahale.metrics.Metered
 *  com.codahale.metrics.Metric
 *  com.codahale.metrics.MetricFilter
 *  com.codahale.metrics.MetricRegistry
 *  com.codahale.metrics.MetricRegistryListener
 *  com.codahale.metrics.Reporter
 *  com.codahale.metrics.Timer
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.codahale.metrics.jmx;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.Metered;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.MetricRegistryListener;
import com.codahale.metrics.Reporter;
import com.codahale.metrics.Timer;
import com.codahale.metrics.jmx.DefaultObjectNameFactory;
import com.codahale.metrics.jmx.ObjectNameFactory;
import java.io.Closeable;
import java.lang.management.ManagementFactory;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.JMException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JmxReporter
implements Reporter,
Closeable {
    private static final Logger LOGGER = LoggerFactory.getLogger(JmxReporter.class);
    private final MetricRegistry registry;
    private final JmxListener listener;

    public static Builder forRegistry(MetricRegistry registry) {
        return new Builder(registry);
    }

    private JmxReporter(MBeanServer mBeanServer, String domain, MetricRegistry registry, MetricFilter filter, MetricTimeUnits timeUnits, ObjectNameFactory objectNameFactory) {
        this.registry = registry;
        this.listener = new JmxListener(mBeanServer, domain, filter, timeUnits, objectNameFactory);
    }

    public void start() {
        this.registry.addListener((MetricRegistryListener)this.listener);
    }

    public void stop() {
        this.registry.removeListener((MetricRegistryListener)this.listener);
        this.listener.unregisterAll();
    }

    @Override
    public void close() {
        this.stop();
    }

    ObjectNameFactory getObjectNameFactory() {
        return this.listener.objectNameFactory;
    }

    private static class MetricTimeUnits {
        private final TimeUnit defaultRate;
        private final TimeUnit defaultDuration;
        private final Map<String, TimeUnit> rateOverrides;
        private final Map<String, TimeUnit> durationOverrides;

        MetricTimeUnits(TimeUnit defaultRate, TimeUnit defaultDuration, Map<String, TimeUnit> rateOverrides, Map<String, TimeUnit> durationOverrides) {
            this.defaultRate = defaultRate;
            this.defaultDuration = defaultDuration;
            this.rateOverrides = rateOverrides;
            this.durationOverrides = durationOverrides;
        }

        public TimeUnit durationFor(String name) {
            return this.durationOverrides.getOrDefault(name, this.defaultDuration);
        }

        public TimeUnit rateFor(String name) {
            return this.rateOverrides.getOrDefault(name, this.defaultRate);
        }
    }

    public static class Builder {
        private final MetricRegistry registry;
        private MBeanServer mBeanServer;
        private TimeUnit rateUnit;
        private TimeUnit durationUnit;
        private ObjectNameFactory objectNameFactory;
        private MetricFilter filter = MetricFilter.ALL;
        private String domain;
        private Map<String, TimeUnit> specificDurationUnits;
        private Map<String, TimeUnit> specificRateUnits;

        private Builder(MetricRegistry registry) {
            this.registry = registry;
            this.rateUnit = TimeUnit.SECONDS;
            this.durationUnit = TimeUnit.MILLISECONDS;
            this.domain = "metrics";
            this.objectNameFactory = new DefaultObjectNameFactory();
            this.specificDurationUnits = Collections.emptyMap();
            this.specificRateUnits = Collections.emptyMap();
        }

        public Builder registerWith(MBeanServer mBeanServer) {
            this.mBeanServer = mBeanServer;
            return this;
        }

        public Builder convertRatesTo(TimeUnit rateUnit) {
            this.rateUnit = rateUnit;
            return this;
        }

        public Builder createsObjectNamesWith(ObjectNameFactory onFactory) {
            if (onFactory == null) {
                throw new IllegalArgumentException("null objectNameFactory");
            }
            this.objectNameFactory = onFactory;
            return this;
        }

        public Builder convertDurationsTo(TimeUnit durationUnit) {
            this.durationUnit = durationUnit;
            return this;
        }

        public Builder filter(MetricFilter filter) {
            this.filter = filter;
            return this;
        }

        public Builder inDomain(String domain) {
            this.domain = domain;
            return this;
        }

        public Builder specificDurationUnits(Map<String, TimeUnit> specificDurationUnits) {
            this.specificDurationUnits = Collections.unmodifiableMap(specificDurationUnits);
            return this;
        }

        public Builder specificRateUnits(Map<String, TimeUnit> specificRateUnits) {
            this.specificRateUnits = Collections.unmodifiableMap(specificRateUnits);
            return this;
        }

        public JmxReporter build() {
            MetricTimeUnits timeUnits = new MetricTimeUnits(this.rateUnit, this.durationUnit, this.specificRateUnits, this.specificDurationUnits);
            if (this.mBeanServer == null) {
                this.mBeanServer = ManagementFactory.getPlatformMBeanServer();
            }
            return new JmxReporter(this.mBeanServer, this.domain, this.registry, this.filter, timeUnits, this.objectNameFactory);
        }
    }

    private static class JmxListener
    implements MetricRegistryListener {
        private final String name;
        private final MBeanServer mBeanServer;
        private final MetricFilter filter;
        private final MetricTimeUnits timeUnits;
        private final Map<ObjectName, ObjectName> registered;
        private final ObjectNameFactory objectNameFactory;

        private JmxListener(MBeanServer mBeanServer, String name, MetricFilter filter, MetricTimeUnits timeUnits, ObjectNameFactory objectNameFactory) {
            this.mBeanServer = mBeanServer;
            this.name = name;
            this.filter = filter;
            this.timeUnits = timeUnits;
            this.registered = new ConcurrentHashMap<ObjectName, ObjectName>();
            this.objectNameFactory = objectNameFactory;
        }

        private void registerMBean(Object mBean, ObjectName objectName) throws InstanceAlreadyExistsException, JMException {
            ObjectInstance objectInstance = this.mBeanServer.registerMBean(mBean, objectName);
            if (objectInstance != null) {
                this.registered.put(objectName, objectInstance.getObjectName());
            } else {
                this.registered.put(objectName, objectName);
            }
        }

        private void unregisterMBean(ObjectName originalObjectName) throws InstanceNotFoundException, MBeanRegistrationException {
            ObjectName storedObjectName = this.registered.remove(originalObjectName);
            if (storedObjectName != null) {
                this.mBeanServer.unregisterMBean(storedObjectName);
            } else {
                this.mBeanServer.unregisterMBean(originalObjectName);
            }
        }

        public void onGaugeAdded(String name, Gauge<?> gauge) {
            try {
                if (this.filter.matches(name, gauge)) {
                    ObjectName objectName = this.createName("gauges", name);
                    this.registerMBean(new JmxGauge(gauge, objectName), objectName);
                }
            }
            catch (InstanceAlreadyExistsException e) {
                LOGGER.debug("Unable to register gauge", (Throwable)e);
            }
            catch (JMException e) {
                LOGGER.warn("Unable to register gauge", (Throwable)e);
            }
        }

        public void onGaugeRemoved(String name) {
            try {
                ObjectName objectName = this.createName("gauges", name);
                this.unregisterMBean(objectName);
            }
            catch (InstanceNotFoundException e) {
                LOGGER.debug("Unable to unregister gauge", (Throwable)e);
            }
            catch (MBeanRegistrationException e) {
                LOGGER.warn("Unable to unregister gauge", (Throwable)e);
            }
        }

        public void onCounterAdded(String name, Counter counter) {
            try {
                if (this.filter.matches(name, (Metric)counter)) {
                    ObjectName objectName = this.createName("counters", name);
                    this.registerMBean(new JmxCounter(counter, objectName), objectName);
                }
            }
            catch (InstanceAlreadyExistsException e) {
                LOGGER.debug("Unable to register counter", (Throwable)e);
            }
            catch (JMException e) {
                LOGGER.warn("Unable to register counter", (Throwable)e);
            }
        }

        public void onCounterRemoved(String name) {
            try {
                ObjectName objectName = this.createName("counters", name);
                this.unregisterMBean(objectName);
            }
            catch (InstanceNotFoundException e) {
                LOGGER.debug("Unable to unregister counter", (Throwable)e);
            }
            catch (MBeanRegistrationException e) {
                LOGGER.warn("Unable to unregister counter", (Throwable)e);
            }
        }

        public void onHistogramAdded(String name, Histogram histogram) {
            try {
                if (this.filter.matches(name, (Metric)histogram)) {
                    ObjectName objectName = this.createName("histograms", name);
                    this.registerMBean(new JmxHistogram(histogram, objectName), objectName);
                }
            }
            catch (InstanceAlreadyExistsException e) {
                LOGGER.debug("Unable to register histogram", (Throwable)e);
            }
            catch (JMException e) {
                LOGGER.warn("Unable to register histogram", (Throwable)e);
            }
        }

        public void onHistogramRemoved(String name) {
            try {
                ObjectName objectName = this.createName("histograms", name);
                this.unregisterMBean(objectName);
            }
            catch (InstanceNotFoundException e) {
                LOGGER.debug("Unable to unregister histogram", (Throwable)e);
            }
            catch (MBeanRegistrationException e) {
                LOGGER.warn("Unable to unregister histogram", (Throwable)e);
            }
        }

        public void onMeterAdded(String name, Meter meter) {
            try {
                if (this.filter.matches(name, (Metric)meter)) {
                    ObjectName objectName = this.createName("meters", name);
                    this.registerMBean(new JmxMeter((Metered)meter, objectName, this.timeUnits.rateFor(name)), objectName);
                }
            }
            catch (InstanceAlreadyExistsException e) {
                LOGGER.debug("Unable to register meter", (Throwable)e);
            }
            catch (JMException e) {
                LOGGER.warn("Unable to register meter", (Throwable)e);
            }
        }

        public void onMeterRemoved(String name) {
            try {
                ObjectName objectName = this.createName("meters", name);
                this.unregisterMBean(objectName);
            }
            catch (InstanceNotFoundException e) {
                LOGGER.debug("Unable to unregister meter", (Throwable)e);
            }
            catch (MBeanRegistrationException e) {
                LOGGER.warn("Unable to unregister meter", (Throwable)e);
            }
        }

        public void onTimerAdded(String name, Timer timer) {
            try {
                if (this.filter.matches(name, (Metric)timer)) {
                    ObjectName objectName = this.createName("timers", name);
                    this.registerMBean(new JmxTimer(timer, objectName, this.timeUnits.rateFor(name), this.timeUnits.durationFor(name)), objectName);
                }
            }
            catch (InstanceAlreadyExistsException e) {
                LOGGER.debug("Unable to register timer", (Throwable)e);
            }
            catch (JMException e) {
                LOGGER.warn("Unable to register timer", (Throwable)e);
            }
        }

        public void onTimerRemoved(String name) {
            try {
                ObjectName objectName = this.createName("timers", name);
                this.unregisterMBean(objectName);
            }
            catch (InstanceNotFoundException e) {
                LOGGER.debug("Unable to unregister timer", (Throwable)e);
            }
            catch (MBeanRegistrationException e) {
                LOGGER.warn("Unable to unregister timer", (Throwable)e);
            }
        }

        private ObjectName createName(String type, String name) {
            return this.objectNameFactory.createName(type, this.name, name);
        }

        void unregisterAll() {
            for (ObjectName name : this.registered.keySet()) {
                try {
                    this.unregisterMBean(name);
                }
                catch (InstanceNotFoundException e) {
                    LOGGER.debug("Unable to unregister metric", (Throwable)e);
                }
                catch (MBeanRegistrationException e) {
                    LOGGER.warn("Unable to unregister metric", (Throwable)e);
                }
            }
            this.registered.clear();
        }
    }

    static class JmxTimer
    extends JmxMeter
    implements JmxTimerMBean {
        private final Timer metric;
        private final double durationFactor;
        private final String durationUnit;

        private JmxTimer(Timer metric, ObjectName objectName, TimeUnit rateUnit, TimeUnit durationUnit) {
            super((Metered)metric, objectName, rateUnit);
            this.metric = metric;
            this.durationFactor = 1.0 / (double)durationUnit.toNanos(1L);
            this.durationUnit = durationUnit.toString().toLowerCase(Locale.US);
        }

        @Override
        public double get50thPercentile() {
            return this.metric.getSnapshot().getMedian() * this.durationFactor;
        }

        @Override
        public double getMin() {
            return (double)this.metric.getSnapshot().getMin() * this.durationFactor;
        }

        @Override
        public double getMax() {
            return (double)this.metric.getSnapshot().getMax() * this.durationFactor;
        }

        @Override
        public double getMean() {
            return this.metric.getSnapshot().getMean() * this.durationFactor;
        }

        @Override
        public double getStdDev() {
            return this.metric.getSnapshot().getStdDev() * this.durationFactor;
        }

        @Override
        public double get75thPercentile() {
            return this.metric.getSnapshot().get75thPercentile() * this.durationFactor;
        }

        @Override
        public double get95thPercentile() {
            return this.metric.getSnapshot().get95thPercentile() * this.durationFactor;
        }

        @Override
        public double get98thPercentile() {
            return this.metric.getSnapshot().get98thPercentile() * this.durationFactor;
        }

        @Override
        public double get99thPercentile() {
            return this.metric.getSnapshot().get99thPercentile() * this.durationFactor;
        }

        @Override
        public double get999thPercentile() {
            return this.metric.getSnapshot().get999thPercentile() * this.durationFactor;
        }

        @Override
        public long[] values() {
            return this.metric.getSnapshot().getValues();
        }

        @Override
        public String getDurationUnit() {
            return this.durationUnit;
        }
    }

    public static interface JmxTimerMBean
    extends JmxMeterMBean {
        public double getMin();

        public double getMax();

        public double getMean();

        public double getStdDev();

        public double get50thPercentile();

        public double get75thPercentile();

        public double get95thPercentile();

        public double get98thPercentile();

        public double get99thPercentile();

        public double get999thPercentile();

        public long[] values();

        public String getDurationUnit();
    }

    private static class JmxMeter
    extends AbstractBean
    implements JmxMeterMBean {
        private final Metered metric;
        private final double rateFactor;
        private final String rateUnit;

        private JmxMeter(Metered metric, ObjectName objectName, TimeUnit rateUnit) {
            super(objectName);
            this.metric = metric;
            this.rateFactor = rateUnit.toSeconds(1L);
            this.rateUnit = ("events/" + this.calculateRateUnit(rateUnit)).intern();
        }

        @Override
        public long getCount() {
            return this.metric.getCount();
        }

        @Override
        public double getMeanRate() {
            return this.metric.getMeanRate() * this.rateFactor;
        }

        @Override
        public double getOneMinuteRate() {
            return this.metric.getOneMinuteRate() * this.rateFactor;
        }

        @Override
        public double getFiveMinuteRate() {
            return this.metric.getFiveMinuteRate() * this.rateFactor;
        }

        @Override
        public double getFifteenMinuteRate() {
            return this.metric.getFifteenMinuteRate() * this.rateFactor;
        }

        @Override
        public String getRateUnit() {
            return this.rateUnit;
        }

        private String calculateRateUnit(TimeUnit unit) {
            String s = unit.toString().toLowerCase(Locale.US);
            return s.substring(0, s.length() - 1);
        }
    }

    public static interface JmxMeterMBean
    extends MetricMBean {
        public long getCount();

        public double getMeanRate();

        public double getOneMinuteRate();

        public double getFiveMinuteRate();

        public double getFifteenMinuteRate();

        public String getRateUnit();
    }

    private static class JmxHistogram
    implements JmxHistogramMBean {
        private final ObjectName objectName;
        private final Histogram metric;

        private JmxHistogram(Histogram metric, ObjectName objectName) {
            this.metric = metric;
            this.objectName = objectName;
        }

        @Override
        public ObjectName objectName() {
            return this.objectName;
        }

        @Override
        public double get50thPercentile() {
            return this.metric.getSnapshot().getMedian();
        }

        @Override
        public long getCount() {
            return this.metric.getCount();
        }

        @Override
        public long getMin() {
            return this.metric.getSnapshot().getMin();
        }

        @Override
        public long getMax() {
            return this.metric.getSnapshot().getMax();
        }

        @Override
        public double getMean() {
            return this.metric.getSnapshot().getMean();
        }

        @Override
        public double getStdDev() {
            return this.metric.getSnapshot().getStdDev();
        }

        @Override
        public double get75thPercentile() {
            return this.metric.getSnapshot().get75thPercentile();
        }

        @Override
        public double get95thPercentile() {
            return this.metric.getSnapshot().get95thPercentile();
        }

        @Override
        public double get98thPercentile() {
            return this.metric.getSnapshot().get98thPercentile();
        }

        @Override
        public double get99thPercentile() {
            return this.metric.getSnapshot().get99thPercentile();
        }

        @Override
        public double get999thPercentile() {
            return this.metric.getSnapshot().get999thPercentile();
        }

        @Override
        public long[] values() {
            return this.metric.getSnapshot().getValues();
        }

        @Override
        public long getSnapshotSize() {
            return this.metric.getSnapshot().size();
        }
    }

    public static interface JmxHistogramMBean
    extends MetricMBean {
        public long getCount();

        public long getMin();

        public long getMax();

        public double getMean();

        public double getStdDev();

        public double get50thPercentile();

        public double get75thPercentile();

        public double get95thPercentile();

        public double get98thPercentile();

        public double get99thPercentile();

        public double get999thPercentile();

        public long[] values();

        public long getSnapshotSize();
    }

    private static class JmxCounter
    extends AbstractBean
    implements JmxCounterMBean {
        private final Counter metric;

        private JmxCounter(Counter metric, ObjectName objectName) {
            super(objectName);
            this.metric = metric;
        }

        @Override
        public long getCount() {
            return this.metric.getCount();
        }
    }

    public static interface JmxCounterMBean
    extends MetricMBean {
        public long getCount();
    }

    private static class JmxGauge
    extends AbstractBean
    implements JmxGaugeMBean {
        private final Gauge<?> metric;

        private JmxGauge(Gauge<?> metric, ObjectName objectName) {
            super(objectName);
            this.metric = metric;
        }

        @Override
        public Object getValue() {
            return this.metric.getValue();
        }

        @Override
        public Number getNumber() {
            Object value = this.metric.getValue();
            return value instanceof Number ? (Number)((Number)value) : (Number)0;
        }
    }

    public static interface JmxGaugeMBean
    extends MetricMBean {
        public Object getValue();

        public Number getNumber();
    }

    private static abstract class AbstractBean
    implements MetricMBean {
        private final ObjectName objectName;

        AbstractBean(ObjectName objectName) {
            this.objectName = objectName;
        }

        @Override
        public ObjectName objectName() {
            return this.objectName;
        }
    }

    public static interface MetricMBean {
        public ObjectName objectName();
    }
}

