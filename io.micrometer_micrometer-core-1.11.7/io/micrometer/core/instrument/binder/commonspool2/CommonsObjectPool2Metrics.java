/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.NonNull
 *  io.micrometer.common.lang.Nullable
 *  io.micrometer.common.util.internal.logging.InternalLogger
 *  io.micrometer.common.util.internal.logging.InternalLoggerFactory
 */
package io.micrometer.core.instrument.binder.commonspool2;

import io.micrometer.common.lang.NonNull;
import io.micrometer.common.lang.Nullable;
import io.micrometer.common.util.internal.logging.InternalLogger;
import io.micrometer.common.util.internal.logging.InternalLoggerFactory;
import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.TimeGauge;
import io.micrometer.core.instrument.binder.MeterBinder;
import io.micrometer.core.instrument.util.NamedThreadFactory;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.ToDoubleFunction;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MBeanServerDelegate;
import javax.management.MBeanServerFactory;
import javax.management.MBeanServerNotification;
import javax.management.MalformedObjectNameException;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.ReflectionException;

public class CommonsObjectPool2Metrics
implements MeterBinder,
AutoCloseable {
    private static final InternalLogger log = InternalLoggerFactory.getInstance(CommonsObjectPool2Metrics.class);
    private static final String JMX_DOMAIN = "org.apache.commons.pool2";
    private static final String METRIC_NAME_PREFIX = "commons.pool2.";
    private static final String[] TYPES = new String[]{"GenericObjectPool", "GenericKeyedObjectPool"};
    private final ExecutorService executor = Executors.newSingleThreadExecutor(new NamedThreadFactory("commons-pool-metrics-updater"));
    private final MBeanServer mBeanServer;
    private final Iterable<Tag> tags;
    private final List<Runnable> notificationListenerCleanUpRunnables = new CopyOnWriteArrayList<Runnable>();

    public CommonsObjectPool2Metrics() {
        this(Collections.emptyList());
    }

    public CommonsObjectPool2Metrics(Iterable<Tag> tags) {
        this(CommonsObjectPool2Metrics.getMBeanServer(), tags);
    }

    public CommonsObjectPool2Metrics(MBeanServer mBeanServer, Iterable<Tag> tags) {
        this.mBeanServer = mBeanServer;
        this.tags = tags;
    }

    private static MBeanServer getMBeanServer() {
        ArrayList<MBeanServer> mBeanServers = MBeanServerFactory.findMBeanServer(null);
        if (!mBeanServers.isEmpty()) {
            return (MBeanServer)mBeanServers.get(0);
        }
        return ManagementFactory.getPlatformMBeanServer();
    }

    @Override
    public void bindTo(@NonNull MeterRegistry registry) {
        for (String type : TYPES) {
            this.registerMetricsEventually(type, (o, tags) -> {
                this.registerGaugeForObject(registry, (ObjectName)o, "NumIdle", "num.idle", (Tags)tags, "The number of instances currently idle in this pool", "objects");
                this.registerGaugeForObject(registry, (ObjectName)o, "NumActive", "num.active", (Tags)tags, "The number of instances currently active in this pool", "objects");
                this.registerGaugeForObject(registry, (ObjectName)o, "NumWaiters", "num.waiters", (Tags)tags, "The estimate of the number of threads currently blocked waiting for an object from the pool", "threads");
                this.registerFunctionCounterForObject(registry, (ObjectName)o, "CreatedCount", "created", (Tags)tags, "The total number of objects created for this pool over the lifetime of the pool", "objects");
                this.registerFunctionCounterForObject(registry, (ObjectName)o, "BorrowedCount", "borrowed", (Tags)tags, "The total number of objects successfully borrowed from this pool over the lifetime of the pool", "objects");
                this.registerFunctionCounterForObject(registry, (ObjectName)o, "ReturnedCount", "returned", (Tags)tags, "The total number of objects returned to this pool over the lifetime of the pool", "objects");
                this.registerFunctionCounterForObject(registry, (ObjectName)o, "DestroyedCount", "destroyed", (Tags)tags, "The total number of objects destroyed by this pool over the lifetime of the pool", "objects");
                this.registerFunctionCounterForObject(registry, (ObjectName)o, "DestroyedByEvictorCount", "destroyed.by.evictor", (Tags)tags, "The total number of objects destroyed by the evictor associated with this pool over the lifetime of the pool", "objects");
                this.registerFunctionCounterForObject(registry, (ObjectName)o, "DestroyedByBorrowValidationCount", "destroyed.by.borrow.validation", (Tags)tags, "The total number of objects destroyed by this pool as a result of failing validation during borrowObject() over the lifetime of the pool", "objects");
                this.registerTimeGaugeForObject(registry, (ObjectName)o, "MaxBorrowWaitTimeMillis", "max.borrow.wait", (Tags)tags, "The maximum time a thread has waited to borrow objects from the pool");
                this.registerTimeGaugeForObject(registry, (ObjectName)o, "MeanActiveTimeMillis", "mean.active", (Tags)tags, "The mean time objects are active");
                this.registerTimeGaugeForObject(registry, (ObjectName)o, "MeanIdleTimeMillis", "mean.idle", (Tags)tags, "The mean time objects are idle");
                this.registerTimeGaugeForObject(registry, (ObjectName)o, "MeanBorrowWaitTimeMillis", "mean.borrow.wait", (Tags)tags, "The mean time threads wait to borrow an object");
            });
        }
    }

    private Iterable<Tag> nameTag(ObjectName name, String type) throws AttributeNotFoundException, MBeanException, ReflectionException, InstanceNotFoundException {
        Tags tags = Tags.of("name", name.getKeyProperty("name"), "type", type);
        if (Objects.equals(type, "GenericObjectPool")) {
            String factoryType = this.mBeanServer.getAttribute(name, "FactoryType").toString();
            tags = Tags.concat((Iterable<? extends Tag>)tags, "factoryType", factoryType);
        }
        return tags;
    }

    private void registerMetricsEventually(String type, BiConsumer<ObjectName, Tags> perObject) {
        try {
            Set<ObjectName> objs = this.mBeanServer.queryNames(new ObjectName("org.apache.commons.pool2:type=" + type + ",*"), null);
            for (ObjectName o : objs) {
                Iterable<Object> nameTags = Collections.emptyList();
                try {
                    nameTags = this.nameTag(o, type);
                }
                catch (Exception e) {
                    log.error("exception in determining name tag", (Throwable)e);
                }
                perObject.accept(o, Tags.concat(this.tags, nameTags));
            }
        }
        catch (MalformedObjectNameException e) {
            throw new RuntimeException("Error registering commons pool2 based metrics", e);
        }
        this.registerNotificationListener(type, perObject);
    }

    private void registerNotificationListener(String type, BiConsumer<ObjectName, Tags> perObject) {
        NotificationListener notificationListener = (notification, handback) -> this.executor.execute(() -> {
            MBeanServerNotification mbs = (MBeanServerNotification)notification;
            ObjectName o = mbs.getMBeanName();
            Iterable<Object> nameTags = Collections.emptyList();
            int maxTries = 3;
            for (int i = 0; i < maxTries; ++i) {
                try {
                    Thread.sleep(1000L);
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                }
                try {
                    nameTags = this.nameTag(o, type);
                    break;
                }
                catch (AttributeNotFoundException | InstanceNotFoundException | MBeanException | ReflectionException e) {
                    if (i != maxTries - 1) continue;
                    log.error("can not set name tag", (Throwable)e);
                    continue;
                }
            }
            perObject.accept(o, Tags.concat(this.tags, nameTags));
        });
        NotificationFilter filter = notification -> {
            if (!"JMX.mbean.registered".equals(notification.getType())) {
                return false;
            }
            ObjectName obj = ((MBeanServerNotification)notification).getMBeanName();
            return obj.getDomain().equals(JMX_DOMAIN) && obj.getKeyProperty("type").equals(type);
        };
        try {
            this.mBeanServer.addNotificationListener(MBeanServerDelegate.DELEGATE_NAME, notificationListener, filter, null);
            this.notificationListenerCleanUpRunnables.add(() -> {
                try {
                    this.mBeanServer.removeNotificationListener(MBeanServerDelegate.DELEGATE_NAME, notificationListener);
                }
                catch (InstanceNotFoundException | ListenerNotFoundException operationsException) {
                    // empty catch block
                }
            });
        }
        catch (InstanceNotFoundException instanceNotFoundException) {
            // empty catch block
        }
    }

    @Override
    public void close() {
        this.notificationListenerCleanUpRunnables.forEach(Runnable::run);
        this.executor.shutdown();
    }

    private void registerGaugeForObject(MeterRegistry registry, ObjectName o, String jmxMetricName, String meterName, Tags allTags, String description, @Nullable String baseUnit) {
        AtomicReference<Gauge> gauge = new AtomicReference<Gauge>();
        gauge.set(Gauge.builder(METRIC_NAME_PREFIX + meterName, this.mBeanServer, this.getJmxAttribute(registry, gauge, o, jmxMetricName)).description(description).baseUnit(baseUnit).tags(allTags).register(registry));
    }

    private void registerFunctionCounterForObject(MeterRegistry registry, ObjectName o, String jmxMetricName, String meterName, Tags allTags, String description, @Nullable String baseUnit) {
        AtomicReference<FunctionCounter> counter = new AtomicReference<FunctionCounter>();
        counter.set(FunctionCounter.builder(METRIC_NAME_PREFIX + meterName, this.mBeanServer, this.getJmxAttribute(registry, counter, o, jmxMetricName)).description(description).baseUnit(baseUnit).tags(allTags).register(registry));
    }

    private void registerTimeGaugeForObject(MeterRegistry registry, ObjectName o, String jmxMetricName, String meterName, Tags allTags, String description) {
        AtomicReference<TimeGauge> timeGauge = new AtomicReference<TimeGauge>();
        timeGauge.set(TimeGauge.builder(METRIC_NAME_PREFIX + meterName, this.mBeanServer, TimeUnit.MILLISECONDS, this.getJmxAttribute(registry, timeGauge, o, jmxMetricName)).description(description).tags(allTags).register(registry));
    }

    private ToDoubleFunction<MBeanServer> getJmxAttribute(MeterRegistry registry, AtomicReference<? extends Meter> meter, ObjectName o, String jmxMetricName) {
        return s -> this.safeDouble(() -> {
            if (!s.isRegistered(o)) {
                registry.remove((Meter)meter.get());
            }
            return s.getAttribute(o, jmxMetricName);
        });
    }

    private double safeDouble(Callable<Object> callable) {
        try {
            return Double.parseDouble(callable.call().toString());
        }
        catch (Exception e) {
            return Double.NaN;
        }
    }
}

