/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.NonNullApi
 *  io.micrometer.common.lang.NonNullFields
 *  io.micrometer.common.lang.Nullable
 */
package io.micrometer.core.instrument.binder.kafka;

import io.micrometer.common.lang.NonNullApi;
import io.micrometer.common.lang.NonNullFields;
import io.micrometer.common.lang.Nullable;
import io.micrometer.core.annotation.Incubating;
import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.TimeGauge;
import io.micrometer.core.instrument.binder.MeterBinder;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.ToDoubleFunction;
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

@NonNullApi
@NonNullFields
@Deprecated
@Incubating(since="1.1.0")
public class KafkaConsumerMetrics
implements MeterBinder,
AutoCloseable {
    private static final String JMX_DOMAIN = "kafka.consumer";
    private static final String METRIC_NAME_PREFIX = "kafka.consumer.";
    private final MBeanServer mBeanServer;
    private final Iterable<Tag> tags;
    @Nullable
    private Integer kafkaMajorVersion;
    private final List<Runnable> notificationListenerCleanUpRunnables = new CopyOnWriteArrayList<Runnable>();

    public KafkaConsumerMetrics() {
        this(Collections.emptyList());
    }

    public KafkaConsumerMetrics(Iterable<Tag> tags) {
        this(KafkaConsumerMetrics.getMBeanServer(), tags);
    }

    public KafkaConsumerMetrics(MBeanServer mBeanServer, Iterable<Tag> tags) {
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
    public void bindTo(MeterRegistry registry) {
        this.registerMetricsEventually(registry, "consumer-fetch-manager-metrics", (o, tags) -> {
            ArrayList<Meter> meters = new ArrayList<Meter>();
            if (tags.stream().anyMatch(t -> t.getKey().equals("topic")) && tags.stream().anyMatch(t -> t.getKey().equals("partition"))) {
                meters.add(this.registerGaugeForObject(registry, (ObjectName)o, "records-lag", (Tags)tags, "The latest lag of the partition", "records"));
                meters.add(this.registerGaugeForObject(registry, (ObjectName)o, "records-lag-avg", (Tags)tags, "The average lag of the partition", "records"));
                meters.add(this.registerGaugeForObject(registry, (ObjectName)o, "records-lag-max", (Tags)tags, "The maximum lag in terms of number of records for any partition in this window. An increasing value over time is your best indication that the consumer group is not keeping up with the producers.", "records"));
                if (this.kafkaMajorVersion((Tags)tags) >= 2) {
                    meters.add(this.registerGaugeForObject(registry, (ObjectName)o, "records-lead", (Tags)tags, "The latest lead of the partition.", "records"));
                    meters.add(this.registerGaugeForObject(registry, (ObjectName)o, "records-lead-min", (Tags)tags, "The min lead of the partition. The lag between the consumer offset and the start offset of the log. If this gets close to zero, it's an indication that the consumer may lose data soon.", "records"));
                    meters.add(this.registerGaugeForObject(registry, (ObjectName)o, "records-lead-avg", (Tags)tags, "The average lead of the partition.", "records"));
                }
            } else if (tags.stream().anyMatch(t -> t.getKey().equals("topic"))) {
                meters.add(this.registerGaugeForObject(registry, (ObjectName)o, "fetch-size-avg", (Tags)tags, "The average number of bytes fetched per request.", "bytes"));
                meters.add(this.registerGaugeForObject(registry, (ObjectName)o, "fetch-size-max", (Tags)tags, "The maximum number of bytes fetched per request.", "bytes"));
                meters.add(this.registerGaugeForObject(registry, (ObjectName)o, "records-per-request-avg", (Tags)tags, "The average number of records in each request.", "records"));
                meters.add(this.registerFunctionCounterForObject(registry, (ObjectName)o, "bytes-consumed-total", (Tags)tags, "The total number of bytes consumed.", "bytes"));
                meters.add(this.registerFunctionCounterForObject(registry, (ObjectName)o, "records-consumed-total", (Tags)tags, "The total number of records consumed.", "records"));
            } else {
                meters.add(this.registerFunctionCounterForObject(registry, (ObjectName)o, "fetch-total", (Tags)tags, "The number of fetch requests.", "requests"));
                meters.add(this.registerTimeGaugeForObject(registry, (ObjectName)o, "fetch-latency-avg", (Tags)tags, "The average time taken for a fetch request."));
                meters.add(this.registerTimeGaugeForObject(registry, (ObjectName)o, "fetch-latency-max", (Tags)tags, "The max time taken for a fetch request."));
                meters.add(this.registerTimeGaugeForObject(registry, (ObjectName)o, "fetch-throttle-time-avg", (Tags)tags, "The average throttle time. When quotas are enabled, the broker may delay fetch requests in order to throttle a consumer which has exceeded its limit. This metric indicates how throttling time has been added to fetch requests on average."));
                meters.add(this.registerTimeGaugeForObject(registry, (ObjectName)o, "fetch-throttle-time-max", (Tags)tags, "The maximum throttle time."));
            }
            return meters;
        });
        this.registerMetricsEventually(registry, "consumer-coordinator-metrics", (o, tags) -> {
            ArrayList<Gauge> meters = new ArrayList<Gauge>();
            meters.add(this.registerGaugeForObject(registry, (ObjectName)o, "assigned-partitions", (Tags)tags, "The number of partitions currently assigned to this consumer.", "partitions"));
            meters.add(this.registerGaugeForObject(registry, (ObjectName)o, "commit-rate", (Tags)tags, "The number of commit calls per second.", "commits"));
            meters.add(this.registerGaugeForObject(registry, (ObjectName)o, "join-rate", (Tags)tags, "The number of group joins per second. Group joining is the first phase of the rebalance protocol. A large value indicates that the consumer group is unstable and will likely be coupled with increased lag.", "joins"));
            meters.add(this.registerGaugeForObject(registry, (ObjectName)o, "sync-rate", (Tags)tags, "The number of group syncs per second. Group synchronization is the second and last phase of the rebalance protocol. A large value indicates group instability.", "syncs"));
            meters.add(this.registerGaugeForObject(registry, (ObjectName)o, "heartbeat-rate", (Tags)tags, "The average number of heartbeats per second. After a rebalance, the consumer sends heartbeats to the coordinator to keep itself active in the group. You may see a lower rate than configured if the processing loop is taking more time to handle message batches. Usually this is OK as long as you see no increase in the join rate.", "heartbeats"));
            meters.add(this.registerTimeGaugeForObject(registry, (ObjectName)o, "commit-latency-avg", (Tags)tags, "The average time taken for a commit request."));
            meters.add(this.registerTimeGaugeForObject(registry, (ObjectName)o, "commit-latency-max", (Tags)tags, "The max time taken for a commit request."));
            meters.add(this.registerTimeGaugeForObject(registry, (ObjectName)o, "join-time-avg", (Tags)tags, "The average time taken for a group rejoin. This value can get as high as the configured session timeout for the consumer, but should usually be lower."));
            meters.add(this.registerTimeGaugeForObject(registry, (ObjectName)o, "join-time-max", (Tags)tags, "The max time taken for a group rejoin. This value should not get much higher than the configured session timeout for the consumer."));
            meters.add(this.registerTimeGaugeForObject(registry, (ObjectName)o, "sync-time-avg", (Tags)tags, "The average time taken for a group sync."));
            meters.add(this.registerTimeGaugeForObject(registry, (ObjectName)o, "sync-time-max", (Tags)tags, "The max time taken for a group sync."));
            meters.add(this.registerTimeGaugeForObject(registry, (ObjectName)o, "heartbeat-response-time-max", (Tags)tags, "The max time taken to receive a response to a heartbeat request."));
            meters.add(this.registerTimeGaugeForObject(registry, (ObjectName)o, "last-heartbeat-seconds-ago", "last-heartbeat", (Tags)tags, "The time since the last controller heartbeat.", TimeUnit.SECONDS));
            return meters;
        });
        this.registerMetricsEventually(registry, "consumer-metrics", (o, tags) -> {
            ArrayList<Gauge> meters = new ArrayList<Gauge>();
            meters.add(this.registerGaugeForObject(registry, (ObjectName)o, "connection-count", (Tags)tags, "The current number of active connections.", "connections"));
            meters.add(this.registerGaugeForObject(registry, (ObjectName)o, "connection-creation-total", (Tags)tags, "New connections established.", "connections"));
            meters.add(this.registerGaugeForObject(registry, (ObjectName)o, "connection-close-total", (Tags)tags, "Connections closed.", "connections"));
            meters.add(this.registerGaugeForObject(registry, (ObjectName)o, "io-ratio", (Tags)tags, "The fraction of time the I/O thread spent doing I/O.", null));
            meters.add(this.registerGaugeForObject(registry, (ObjectName)o, "io-wait-ratio", (Tags)tags, "The fraction of time the I/O thread spent waiting.", null));
            meters.add(this.registerGaugeForObject(registry, (ObjectName)o, "select-total", (Tags)tags, "Number of times the I/O layer checked for new I/O to perform.", null));
            meters.add(this.registerTimeGaugeForObject(registry, (ObjectName)o, "io-time-ns-avg", "io-time-avg", (Tags)tags, "The average length of time for I/O per select call.", TimeUnit.NANOSECONDS));
            meters.add(this.registerTimeGaugeForObject(registry, (ObjectName)o, "io-wait-time-ns-avg", "io-wait-time-avg", (Tags)tags, "The average length of time the I/O thread spent waiting for a socket to be ready for reads or writes.", TimeUnit.NANOSECONDS));
            if (this.kafkaMajorVersion((Tags)tags) >= 2) {
                meters.add(this.registerGaugeForObject(registry, (ObjectName)o, "successful-authentication-total", "authentication-attempts", Tags.concat((Iterable<? extends Tag>)tags, "result", "successful"), "The number of successful authentication attempts.", null));
                meters.add(this.registerGaugeForObject(registry, (ObjectName)o, "failed-authentication-total", "authentication-attempts", Tags.concat((Iterable<? extends Tag>)tags, "result", "failed"), "The number of failed authentication attempts.", null));
                meters.add(this.registerGaugeForObject(registry, (ObjectName)o, "network-io-total", (Tags)tags, "", "bytes"));
                meters.add(this.registerGaugeForObject(registry, (ObjectName)o, "outgoing-byte-total", (Tags)tags, "", "bytes"));
                meters.add(this.registerGaugeForObject(registry, (ObjectName)o, "request-total", (Tags)tags, "", "requests"));
                meters.add(this.registerGaugeForObject(registry, (ObjectName)o, "response-total", (Tags)tags, "", "responses"));
                meters.add(this.registerTimeGaugeForObject(registry, (ObjectName)o, "io-waittime-total", "io-wait-time-total", (Tags)tags, "Time spent on the I/O thread waiting for a socket to be ready for reads or writes.", TimeUnit.NANOSECONDS));
                meters.add(this.registerTimeGaugeForObject(registry, (ObjectName)o, "iotime-total", "io-time-total", (Tags)tags, "Time spent in I/O during select calls.", TimeUnit.NANOSECONDS));
            }
            return meters;
        });
    }

    private Gauge registerGaugeForObject(MeterRegistry registry, ObjectName o, String jmxMetricName, String meterName, Tags allTags, String description, @Nullable String baseUnit) {
        AtomicReference<Gauge> gaugeReference = new AtomicReference<Gauge>();
        Gauge gauge = Gauge.builder(METRIC_NAME_PREFIX + meterName, this.mBeanServer, this.getJmxAttribute(registry, gaugeReference, o, jmxMetricName)).description(description).baseUnit(baseUnit).tags(allTags).register(registry);
        gaugeReference.set(gauge);
        return gauge;
    }

    private Gauge registerGaugeForObject(MeterRegistry registry, ObjectName o, String jmxMetricName, Tags allTags, String description, @Nullable String baseUnit) {
        return this.registerGaugeForObject(registry, o, jmxMetricName, KafkaConsumerMetrics.sanitize(jmxMetricName), allTags, description, baseUnit);
    }

    private FunctionCounter registerFunctionCounterForObject(MeterRegistry registry, ObjectName o, String jmxMetricName, Tags allTags, String description, @Nullable String baseUnit) {
        AtomicReference<FunctionCounter> counterReference = new AtomicReference<FunctionCounter>();
        FunctionCounter counter = FunctionCounter.builder(METRIC_NAME_PREFIX + KafkaConsumerMetrics.sanitize(jmxMetricName), this.mBeanServer, this.getJmxAttribute(registry, counterReference, o, jmxMetricName)).description(description).baseUnit(baseUnit).tags(allTags).register(registry);
        counterReference.set(counter);
        return counter;
    }

    private TimeGauge registerTimeGaugeForObject(MeterRegistry registry, ObjectName o, String jmxMetricName, String meterName, Tags allTags, String description, TimeUnit timeUnit) {
        AtomicReference<TimeGauge> timeGaugeReference = new AtomicReference<TimeGauge>();
        TimeGauge timeGauge = TimeGauge.builder(METRIC_NAME_PREFIX + meterName, this.mBeanServer, timeUnit, this.getJmxAttribute(registry, timeGaugeReference, o, jmxMetricName)).description(description).tags(allTags).register(registry);
        timeGaugeReference.set(timeGauge);
        return timeGauge;
    }

    private TimeGauge registerTimeGaugeForObject(MeterRegistry registry, ObjectName o, String jmxMetricName, String meterName, Tags allTags, String description) {
        return this.registerTimeGaugeForObject(registry, o, jmxMetricName, meterName, allTags, description, TimeUnit.MILLISECONDS);
    }

    private ToDoubleFunction<MBeanServer> getJmxAttribute(MeterRegistry registry, AtomicReference<? extends Meter> meter, ObjectName o, String jmxMetricName) {
        return s -> this.safeDouble(() -> {
            if (!s.isRegistered(o)) {
                registry.remove((Meter)meter.get());
            }
            return s.getAttribute(o, jmxMetricName);
        });
    }

    private TimeGauge registerTimeGaugeForObject(MeterRegistry registry, ObjectName o, String jmxMetricName, Tags allTags, String description) {
        return this.registerTimeGaugeForObject(registry, o, jmxMetricName, KafkaConsumerMetrics.sanitize(jmxMetricName), allTags, description);
    }

    int kafkaMajorVersion(Tags tags) {
        if (this.kafkaMajorVersion == null || this.kafkaMajorVersion == -1) {
            this.kafkaMajorVersion = tags.stream().filter(t -> "client.id".equals(t.getKey())).findAny().map(clientId -> {
                try {
                    String version = (String)this.mBeanServer.getAttribute(new ObjectName("kafka.consumer:type=app-info,client-id=" + clientId.getValue()), "version");
                    return Integer.parseInt(version.substring(0, version.indexOf(46)));
                }
                catch (Throwable e) {
                    return -1;
                }
            }).orElse(-1);
        }
        return this.kafkaMajorVersion;
    }

    private void registerMetricsEventually(MeterRegistry registry, String type, BiFunction<ObjectName, Tags, List<Meter>> perObject) {
        try {
            Set<ObjectName> objs = this.mBeanServer.queryNames(new ObjectName("kafka.consumer:type=" + type + ",*"), null);
            if (!objs.isEmpty()) {
                for (ObjectName o : objs) {
                    List<Meter> meters = perObject.apply(o, Tags.concat(this.tags, this.nameTag(o)));
                    this.addUnregistrationListener(registry, type, o, meters);
                }
                return;
            }
        }
        catch (MalformedObjectNameException e) {
            throw new RuntimeException("Error registering Kafka JMX based metrics", e);
        }
        this.registerNotificationListener(registry, type, perObject);
    }

    private void registerNotificationListener(MeterRegistry registry, String type, BiFunction<ObjectName, Tags, List<Meter>> perObject) {
        NotificationListener registrationListener = (notification, handback) -> {
            MBeanServerNotification mbs = (MBeanServerNotification)notification;
            ObjectName o = mbs.getMBeanName();
            List meters = (List)perObject.apply(o, Tags.concat(this.tags, this.nameTag(o)));
            this.addUnregistrationListener(registry, type, o, meters);
        };
        NotificationFilter registrationFilter = this.createNotificationFilter(type, "JMX.mbean.registered");
        this.addNotificationListener(registrationListener, registrationFilter);
        this.notificationListenerCleanUpRunnables.add(() -> this.removeNotificationListener(registrationListener));
    }

    private void removeNotificationListener(NotificationListener notificationListener) {
        try {
            this.mBeanServer.removeNotificationListener(MBeanServerDelegate.DELEGATE_NAME, notificationListener);
        }
        catch (InstanceNotFoundException | ListenerNotFoundException operationsException) {
            // empty catch block
        }
    }

    private void addUnregistrationListener(final MeterRegistry registry, String type, final ObjectName o, final List<Meter> meters) {
        NotificationListener unregistrationListener = new NotificationListener(){

            @Override
            public void handleNotification(Notification notification2, Object handback2) {
                MBeanServerNotification mbs2 = (MBeanServerNotification)notification2;
                ObjectName o2 = mbs2.getMBeanName();
                if (o2.equals(o)) {
                    meters.stream().forEach(registry::remove);
                }
                KafkaConsumerMetrics.this.removeNotificationListener(this);
            }
        };
        NotificationFilter unregistrationFilter = this.createNotificationFilter(type, "JMX.mbean.unregistered");
        this.addNotificationListener(unregistrationListener, unregistrationFilter);
    }

    private NotificationFilter createNotificationFilter(String type, String notificationType) {
        return notification -> {
            if (!notificationType.equals(notification.getType())) {
                return false;
            }
            ObjectName obj = ((MBeanServerNotification)notification).getMBeanName();
            return obj.getDomain().equals(JMX_DOMAIN) && obj.getKeyProperty("type").equals(type);
        };
    }

    private void addNotificationListener(NotificationListener listener, NotificationFilter filter) {
        try {
            this.mBeanServer.addNotificationListener(MBeanServerDelegate.DELEGATE_NAME, listener, filter, null);
        }
        catch (InstanceNotFoundException e) {
            throw new RuntimeException("Error registering Kafka MBean listener", e);
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

    private Iterable<Tag> nameTag(ObjectName name) {
        String partition;
        String topic;
        Tags tags = Tags.empty();
        String clientId = name.getKeyProperty("client-id");
        if (clientId != null) {
            tags = Tags.concat((Iterable<? extends Tag>)tags, "client.id", clientId);
        }
        if ((topic = name.getKeyProperty("topic")) != null) {
            tags = Tags.concat((Iterable<? extends Tag>)tags, "topic", topic);
        }
        if ((partition = name.getKeyProperty("partition")) != null) {
            tags = Tags.concat((Iterable<? extends Tag>)tags, "partition", partition);
        }
        return tags;
    }

    private static String sanitize(String value) {
        return value.replaceAll("-", ".");
    }

    @Override
    public void close() {
        this.notificationListenerCleanUpRunnables.forEach(Runnable::run);
    }
}

