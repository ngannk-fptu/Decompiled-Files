/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.NonNullApi
 *  io.micrometer.common.lang.NonNullFields
 *  io.micrometer.common.lang.Nullable
 *  org.apache.catalina.Manager
 */
package io.micrometer.core.instrument.binder.tomcat;

import io.micrometer.common.lang.NonNullApi;
import io.micrometer.common.lang.NonNullFields;
import io.micrometer.common.lang.Nullable;
import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.FunctionTimer;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.TimeGauge;
import io.micrometer.core.instrument.binder.MeterBinder;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import javax.management.InstanceNotFoundException;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanServer;
import javax.management.MBeanServerDelegate;
import javax.management.MBeanServerFactory;
import javax.management.MBeanServerNotification;
import javax.management.MalformedObjectNameException;
import javax.management.Notification;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import org.apache.catalina.Manager;

@NonNullApi
@NonNullFields
public class TomcatMetrics
implements MeterBinder,
AutoCloseable {
    private static final String JMX_DOMAIN_EMBEDDED = "Tomcat";
    private static final String JMX_DOMAIN_STANDALONE = "Catalina";
    private static final String OBJECT_NAME_SERVER_SUFFIX = ":type=Server";
    private static final String OBJECT_NAME_SERVER_EMBEDDED = "Tomcat:type=Server";
    private static final String OBJECT_NAME_SERVER_STANDALONE = "Catalina:type=Server";
    @Nullable
    private final Manager manager;
    private final MBeanServer mBeanServer;
    private final Iterable<Tag> tags;
    private final Set<NotificationListener> notificationListeners = ConcurrentHashMap.newKeySet();
    private volatile String jmxDomain;

    public TomcatMetrics(@Nullable Manager manager, Iterable<Tag> tags) {
        this(manager, tags, TomcatMetrics.getMBeanServer());
    }

    public TomcatMetrics(@Nullable Manager manager, Iterable<Tag> tags, MBeanServer mBeanServer) {
        this.manager = manager;
        this.tags = tags;
        this.mBeanServer = mBeanServer;
        if (manager != null) {
            this.jmxDomain = manager.getContext().getDomain();
        }
    }

    public static void monitor(MeterRegistry registry, @Nullable Manager manager, String ... tags) {
        TomcatMetrics.monitor(registry, manager, Tags.of(tags));
    }

    public static void monitor(MeterRegistry registry, @Nullable Manager manager, Iterable<Tag> tags) {
        new TomcatMetrics(manager, tags).bindTo(registry);
    }

    public static MBeanServer getMBeanServer() {
        ArrayList<MBeanServer> mBeanServers = MBeanServerFactory.findMBeanServer(null);
        if (!mBeanServers.isEmpty()) {
            return (MBeanServer)mBeanServers.get(0);
        }
        return ManagementFactory.getPlatformMBeanServer();
    }

    @Override
    public void bindTo(MeterRegistry registry) {
        this.registerGlobalRequestMetrics(registry);
        this.registerServletMetrics(registry);
        this.registerCacheMetrics(registry);
        this.registerThreadPoolMetrics(registry);
        this.registerSessionMetrics(registry);
    }

    private void registerSessionMetrics(MeterRegistry registry) {
        if (this.manager == null) {
            return;
        }
        Gauge.builder("tomcat.sessions.active.max", this.manager, Manager::getMaxActive).tags(this.tags).baseUnit("sessions").register(registry);
        Gauge.builder("tomcat.sessions.active.current", this.manager, Manager::getActiveSessions).tags(this.tags).baseUnit("sessions").register(registry);
        FunctionCounter.builder("tomcat.sessions.created", this.manager, Manager::getSessionCounter).tags(this.tags).baseUnit("sessions").register(registry);
        FunctionCounter.builder("tomcat.sessions.expired", this.manager, Manager::getExpiredSessions).tags(this.tags).baseUnit("sessions").register(registry);
        FunctionCounter.builder("tomcat.sessions.rejected", this.manager, Manager::getRejectedSessions).tags(this.tags).baseUnit("sessions").register(registry);
        TimeGauge.builder("tomcat.sessions.alive.max", this.manager, TimeUnit.SECONDS, Manager::getSessionMaxAliveTime).tags(this.tags).register(registry);
    }

    private void registerThreadPoolMetrics(MeterRegistry registry) {
        this.registerMetricsEventually(":type=ThreadPool,name=*", (name, allTags) -> {
            Gauge.builder("tomcat.threads.config.max", this.mBeanServer, s -> this.safeDouble(() -> s.getAttribute((ObjectName)name, "maxThreads"))).tags((Iterable<Tag>)allTags).baseUnit("threads").register(registry);
            Gauge.builder("tomcat.threads.busy", this.mBeanServer, s -> this.safeDouble(() -> s.getAttribute((ObjectName)name, "currentThreadsBusy"))).tags((Iterable<Tag>)allTags).baseUnit("threads").register(registry);
            Gauge.builder("tomcat.threads.current", this.mBeanServer, s -> this.safeDouble(() -> s.getAttribute((ObjectName)name, "currentThreadCount"))).tags((Iterable<Tag>)allTags).baseUnit("threads").register(registry);
            Gauge.builder("tomcat.connections.current", this.mBeanServer, s -> this.safeDouble(() -> s.getAttribute((ObjectName)name, "connectionCount"))).tags((Iterable<Tag>)allTags).baseUnit("connections").register(registry);
            Gauge.builder("tomcat.connections.keepalive.current", this.mBeanServer, s -> this.safeDouble(() -> s.getAttribute((ObjectName)name, "keepAliveCount"))).tags((Iterable<Tag>)allTags).baseUnit("connections").register(registry);
            Gauge.builder("tomcat.connections.config.max", this.mBeanServer, s -> this.safeDouble(() -> s.getAttribute((ObjectName)name, "maxConnections"))).tags((Iterable<Tag>)allTags).baseUnit("connections").register(registry);
        });
    }

    private void registerCacheMetrics(MeterRegistry registry) {
        this.registerMetricsEventually(":type=StringCache", (name, allTags) -> {
            FunctionCounter.builder("tomcat.cache.access", this.mBeanServer, s -> this.safeDouble(() -> s.getAttribute((ObjectName)name, "accessCount"))).tags((Iterable<Tag>)allTags).register(registry);
            FunctionCounter.builder("tomcat.cache.hit", this.mBeanServer, s -> this.safeDouble(() -> s.getAttribute((ObjectName)name, "hitCount"))).tags((Iterable<Tag>)allTags).register(registry);
        });
    }

    private void registerServletMetrics(MeterRegistry registry) {
        this.registerMetricsEventually(":j2eeType=Servlet,name=*,*", (name, allTags) -> {
            FunctionCounter.builder("tomcat.servlet.error", this.mBeanServer, s -> this.safeDouble(() -> s.getAttribute((ObjectName)name, "errorCount"))).tags((Iterable<Tag>)allTags).register(registry);
            FunctionTimer.builder("tomcat.servlet.request", this.mBeanServer, s -> this.safeLong(() -> s.getAttribute((ObjectName)name, "requestCount")), s -> this.safeDouble(() -> s.getAttribute((ObjectName)name, "processingTime")), TimeUnit.MILLISECONDS).tags((Iterable<Tag>)allTags).register(registry);
            TimeGauge.builder("tomcat.servlet.request.max", this.mBeanServer, TimeUnit.MILLISECONDS, s -> this.safeDouble(() -> s.getAttribute((ObjectName)name, "maxTime"))).tags((Iterable<Tag>)allTags).register(registry);
        });
    }

    private void registerGlobalRequestMetrics(MeterRegistry registry) {
        this.registerMetricsEventually(":type=GlobalRequestProcessor,name=*", (name, allTags) -> {
            FunctionCounter.builder("tomcat.global.sent", this.mBeanServer, s -> this.safeDouble(() -> s.getAttribute((ObjectName)name, "bytesSent"))).tags((Iterable<Tag>)allTags).baseUnit("bytes").register(registry);
            FunctionCounter.builder("tomcat.global.received", this.mBeanServer, s -> this.safeDouble(() -> s.getAttribute((ObjectName)name, "bytesReceived"))).tags((Iterable<Tag>)allTags).baseUnit("bytes").register(registry);
            FunctionCounter.builder("tomcat.global.error", this.mBeanServer, s -> this.safeDouble(() -> s.getAttribute((ObjectName)name, "errorCount"))).tags((Iterable<Tag>)allTags).register(registry);
            FunctionTimer.builder("tomcat.global.request", this.mBeanServer, s -> this.safeLong(() -> s.getAttribute((ObjectName)name, "requestCount")), s -> this.safeDouble(() -> s.getAttribute((ObjectName)name, "processingTime")), TimeUnit.MILLISECONDS).tags((Iterable<Tag>)allTags).register(registry);
            TimeGauge.builder("tomcat.global.request.max", this.mBeanServer, TimeUnit.MILLISECONDS, s -> this.safeDouble(() -> s.getAttribute((ObjectName)name, "maxTime"))).tags((Iterable<Tag>)allTags).register(registry);
        });
    }

    private void registerMetricsEventually(final String namePatternSuffix, final BiConsumer<ObjectName, Iterable<Tag>> perObject) {
        Set<ObjectName> objectNames;
        if (this.getJmxDomain() != null && !(objectNames = this.mBeanServer.queryNames(this.getNamePattern(namePatternSuffix), null)).isEmpty()) {
            objectNames.forEach(objectName -> perObject.accept((ObjectName)objectName, Tags.concat(this.tags, this.nameTag((ObjectName)objectName))));
            return;
        }
        NotificationListener notificationListener = new NotificationListener(){

            @Override
            public void handleNotification(Notification notification, Object handback) {
                MBeanServerNotification mBeanServerNotification = (MBeanServerNotification)notification;
                ObjectName objectName = mBeanServerNotification.getMBeanName();
                perObject.accept(objectName, Tags.concat((Iterable<? extends Tag>)TomcatMetrics.this.tags, TomcatMetrics.this.nameTag(objectName)));
                if (TomcatMetrics.this.getNamePattern(namePatternSuffix).isPattern()) {
                    return;
                }
                try {
                    TomcatMetrics.this.mBeanServer.removeNotificationListener(MBeanServerDelegate.DELEGATE_NAME, this);
                    TomcatMetrics.this.notificationListeners.remove(this);
                }
                catch (InstanceNotFoundException | ListenerNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            }
        };
        this.notificationListeners.add(notificationListener);
        NotificationFilter notificationFilter = notification -> {
            if (!"JMX.mbean.registered".equals(notification.getType())) {
                return false;
            }
            ObjectName objectName = ((MBeanServerNotification)notification).getMBeanName();
            return this.getNamePattern(namePatternSuffix).apply(objectName);
        };
        try {
            this.mBeanServer.addNotificationListener(MBeanServerDelegate.DELEGATE_NAME, notificationListener, notificationFilter, null);
        }
        catch (InstanceNotFoundException e) {
            throw new RuntimeException("Error registering MBean listener", e);
        }
    }

    private ObjectName getNamePattern(String namePatternSuffix) {
        try {
            return new ObjectName(this.getJmxDomain() + namePatternSuffix);
        }
        catch (MalformedObjectNameException e) {
            throw new RuntimeException("Error registering Tomcat JMX based metrics", e);
        }
    }

    private String getJmxDomain() {
        if (this.jmxDomain == null) {
            if (this.hasObjectName(OBJECT_NAME_SERVER_EMBEDDED)) {
                this.jmxDomain = JMX_DOMAIN_EMBEDDED;
            } else if (this.hasObjectName(OBJECT_NAME_SERVER_STANDALONE)) {
                this.jmxDomain = JMX_DOMAIN_STANDALONE;
            }
        }
        return this.jmxDomain;
    }

    public void setJmxDomain(String jmxDomain) {
        this.jmxDomain = jmxDomain;
    }

    private boolean hasObjectName(String name) {
        try {
            return this.mBeanServer.queryNames(new ObjectName(name), null).size() == 1;
        }
        catch (MalformedObjectNameException ex) {
            throw new RuntimeException(ex);
        }
    }

    private double safeDouble(Callable<Object> callable) {
        try {
            return Double.parseDouble(callable.call().toString());
        }
        catch (Exception e) {
            return Double.NaN;
        }
    }

    private long safeLong(Callable<Object> callable) {
        try {
            return Long.parseLong(callable.call().toString());
        }
        catch (Exception e) {
            return 0L;
        }
    }

    private Iterable<Tag> nameTag(ObjectName name) {
        String nameTagValue = name.getKeyProperty("name");
        if (nameTagValue != null) {
            return Tags.of("name", nameTagValue.replaceAll("\"", ""));
        }
        return Collections.emptyList();
    }

    @Override
    public void close() {
        for (NotificationListener notificationListener : this.notificationListeners) {
            try {
                this.mBeanServer.removeNotificationListener(MBeanServerDelegate.DELEGATE_NAME, notificationListener);
            }
            catch (InstanceNotFoundException | ListenerNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}

