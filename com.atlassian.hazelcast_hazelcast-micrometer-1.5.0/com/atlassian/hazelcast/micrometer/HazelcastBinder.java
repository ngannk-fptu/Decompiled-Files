/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hazelcast.config.Config
 *  com.hazelcast.config.ListenerConfig
 *  com.hazelcast.core.LifecycleListener
 *  io.micrometer.core.instrument.MeterRegistry
 */
package com.atlassian.hazelcast.micrometer;

import com.atlassian.hazelcast.micrometer.DistributedObjectMetricsListener;
import com.atlassian.hazelcast.micrometer.JmxBinder;
import com.atlassian.hazelcast.micrometer.MembershipMetricsListener;
import com.atlassian.hazelcast.micrometer.MigrationMetricsListener;
import com.hazelcast.config.Config;
import com.hazelcast.config.ListenerConfig;
import com.hazelcast.core.LifecycleListener;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.EventListener;
import javax.management.MBeanServer;

public final class HazelcastBinder {
    private final MeterRegistry meterRegistry;
    private final MBeanServer mbeanServer;

    public HazelcastBinder(MeterRegistry meterRegistry, MBeanServer mbeanServer) {
        this.meterRegistry = meterRegistry;
        this.mbeanServer = mbeanServer;
    }

    public void bind(Config hazelcastConfig) {
        hazelcastConfig.addListenerConfig(new ListenerConfig((EventListener)((Object)new MigrationMetricsListener(this.meterRegistry))));
        hazelcastConfig.addListenerConfig(new ListenerConfig((EventListener)((Object)new MembershipMetricsListener(this.meterRegistry))));
        hazelcastConfig.addListenerConfig(new ListenerConfig((EventListener)((Object)new DistributedObjectMetricsListener(this.meterRegistry))));
        hazelcastConfig.addListenerConfig(new ListenerConfig((EventListener)((LifecycleListener)event -> {
            switch (event.getState()) {
                case STARTED: {
                    HazelcastBinder.bind(new JmxBinder(this.mbeanServer, this.meterRegistry));
                }
            }
        })));
    }

    private static void bind(JmxBinder jmxBinder) {
        jmxBinder.bind("HazelcastInstance.ConnectionManager", "clientConnectionCount", "activeConnectionCount", "connectionCount").bind("HazelcastInstance.EventService", "eventThreadCount", "eventQueueCapacity", "eventQueueSize").bind("HazelcastInstance.OperationService", "responseQueueSize", "runningOperationsCount", "remoteOperationCount", "executedOperationCount", "operationThreadCount").bind("HazelcastInstance.PartitionServiceMBean", "partitionCount", "activePartitionCount").bind("HazelcastInstance.ManagedExecutorService", "queueSize", "poolSize", "remainingQueueCapacity", "maximumPoolSize", "completedTaskCount");
    }
}

