/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.cluster.ClusterEventWrapper
 *  com.atlassian.confluence.event.events.monitoring.JmxConfigChangedEvent
 *  com.atlassian.confluence.jmx.JmxConfigService
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.event.events.PluginFrameworkShutdownEvent
 *  com.atlassian.plugin.event.events.PluginFrameworkStartedEvent
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.edgeindex;

import com.atlassian.confluence.event.events.cluster.ClusterEventWrapper;
import com.atlassian.confluence.event.events.monitoring.JmxConfigChangedEvent;
import com.atlassian.confluence.jmx.JmxConfigService;
import com.atlassian.confluence.plugins.edgeindex.EdgeIndexQueueSize;
import com.atlassian.confluence.plugins.edgeindex.EdgeIndexTaskQueue;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.event.events.PluginFrameworkShutdownEvent;
import com.atlassian.plugin.event.events.PluginFrameworkStartedEvent;
import java.lang.management.ManagementFactory;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public final class EdgeIndexQueueSizeMBeanManager {
    private static final Logger log = LoggerFactory.getLogger(EdgeIndexQueueSizeMBeanManager.class);
    private static final String MBEAN_BASE = "com.atlassian.confluence:type=metrics";
    private static final String MBEAN_CATEGORY = "category00=confluence,category01=indexTaskQueue";
    private static final String MBEAN_NAME = "name=size,tag.queueName=edgeTaskQueue";
    static final String MBEAN_FULL_NAME = "com.atlassian.confluence:type=metrics,category00=confluence,category01=indexTaskQueue,name=size,tag.queueName=edgeTaskQueue";
    private final EventPublisher eventPublisher;
    private final JmxConfigService jmxConfigService;
    private final EdgeIndexTaskQueue edgeIndexTaskQueue;
    private final ObjectName objectName;

    @Autowired
    public EdgeIndexQueueSizeMBeanManager(EventPublisher eventPublisher, JmxConfigService jmxConfigService, EdgeIndexTaskQueue edgeIndexTaskQueue) throws MalformedObjectNameException {
        this.eventPublisher = eventPublisher;
        this.jmxConfigService = jmxConfigService;
        this.edgeIndexTaskQueue = edgeIndexTaskQueue;
        this.objectName = new ObjectName(MBEAN_FULL_NAME);
    }

    @PostConstruct
    public void setup() {
        this.eventPublisher.register((Object)this);
    }

    @PreDestroy
    public void preDestroy() {
        this.eventPublisher.unregister((Object)this);
    }

    @EventListener
    public void onPluginFrameworkStarted(PluginFrameworkStartedEvent event) {
        if (this.isJmxDisabledPropertySet()) {
            return;
        }
        if (this.isJmxEnabledOnCluster()) {
            this.registerMBean(new EdgeIndexQueueSize(this.edgeIndexTaskQueue), this.objectName);
        }
    }

    private void registerMBean(Object mBean, ObjectName objectName) {
        try {
            ManagementFactory.getPlatformMBeanServer().registerMBean(mBean, objectName);
        }
        catch (InstanceAlreadyExistsException | MBeanRegistrationException | NotCompliantMBeanException e) {
            log.warn("Error registering MBean " + objectName, (Throwable)e);
        }
    }

    @EventListener
    public void onPluginFrameworkShutdown(PluginFrameworkShutdownEvent event) {
        this.unregisterMBean(this.objectName);
    }

    private void unregisterMBean(ObjectName objectName) {
        try {
            MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
            if (mBeanServer.isRegistered(objectName)) {
                mBeanServer.unregisterMBean(objectName);
            }
        }
        catch (InstanceNotFoundException | MBeanRegistrationException e) {
            log.warn("Error unregistering MBean " + objectName, (Throwable)e);
        }
    }

    @EventListener
    public void onJmxConfigChanged(JmxConfigChangedEvent event) {
        this.handleJmxConfigChange(event.getJmxEnabled());
    }

    private void handleJmxConfigChange(boolean jmxEnabled) {
        if (this.isJmxDisabledPropertySet()) {
            return;
        }
        if (jmxEnabled) {
            this.registerMBean(new EdgeIndexQueueSize(this.edgeIndexTaskQueue), this.objectName);
        } else {
            this.unregisterMBean(this.objectName);
        }
    }

    @EventListener
    public void onClusteredJmxConfigChanged(ClusterEventWrapper clusterEvent) {
        if (!(clusterEvent.getEvent() instanceof JmxConfigChangedEvent)) {
            return;
        }
        JmxConfigChangedEvent event = (JmxConfigChangedEvent)clusterEvent.getEvent();
        this.handleJmxConfigChange(event.getJmxEnabled());
    }

    private boolean isJmxEnabledOnCluster() {
        return this.jmxConfigService.isJmxEnabledOnCluster();
    }

    private boolean isJmxDisabledPropertySet() {
        return System.getProperty("confluence.jmx.disabled") != null;
    }
}

