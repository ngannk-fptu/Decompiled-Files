/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.internal.diagnostics.ipd.index;

import com.atlassian.confluence.internal.diagnostics.ipd.index.IndexQueueSizeMetric;
import com.atlassian.confluence.internal.diagnostics.ipd.index.IndexQueueSizeService;
import java.lang.management.ManagementFactory;
import java.util.Set;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultIndexQueueSizeService
implements IndexQueueSizeService {
    private static final Logger log = LoggerFactory.getLogger(DefaultIndexQueueSizeService.class);
    private static final String SOURCE_MBEAN = "com.atlassian.confluence:*,category01=indexTaskQueue";
    private static final String MAIN_INDEX_QUEUE = "contentTaskQueue";
    private static final String CHANGE_INDEX_QUEUE = "changeTaskQueue";
    private static final String EDGE_INDEX_QUEUE = "edgeTaskQueue";

    @Override
    public IndexQueueSizeMetric getIndexQueueSizeMetric() {
        MBeanServer platformMBeanServer = this.getPlatformMBeanServer();
        Set<ObjectName> sourceMBeanNames = this.findMBeanNames(platformMBeanServer);
        long mainIndexQueueSize = 0L;
        long changeIndexQueueSize = 0L;
        long edgeIndexQueueSize = 0L;
        for (ObjectName sourceMBeanName : sourceMBeanNames) {
            String canonicalName = sourceMBeanName.getCanonicalName();
            if (canonicalName.contains(MAIN_INDEX_QUEUE)) {
                mainIndexQueueSize = this.getMetricValue(platformMBeanServer, sourceMBeanName);
            }
            if (canonicalName.contains(CHANGE_INDEX_QUEUE)) {
                changeIndexQueueSize = this.getMetricValue(platformMBeanServer, sourceMBeanName);
            }
            if (!canonicalName.contains(EDGE_INDEX_QUEUE)) continue;
            edgeIndexQueueSize = this.getMetricValue(platformMBeanServer, sourceMBeanName);
        }
        return new IndexQueueSizeMetric(mainIndexQueueSize, changeIndexQueueSize, edgeIndexQueueSize);
    }

    MBeanServer getPlatformMBeanServer() {
        return ManagementFactory.getPlatformMBeanServer();
    }

    private Set<ObjectName> findMBeanNames(MBeanServer platformMBeanServer) {
        ObjectName objectName = this.getMBeanObject();
        return platformMBeanServer.queryNames(objectName, null);
    }

    private ObjectName getMBeanObject() {
        try {
            return new ObjectName(SOURCE_MBEAN);
        }
        catch (MalformedObjectNameException e) {
            log.warn("Invalid MBean name", (Throwable)e);
            return null;
        }
    }

    private Long getMetricValue(MBeanServer platformMBeanServer, ObjectName mbeanObject) {
        try {
            return ((Double)platformMBeanServer.getAttribute(mbeanObject, "Value")).longValue();
        }
        catch (Exception e) {
            log.warn("Unable to read attribute", (Throwable)e);
            return 0L;
        }
    }
}

