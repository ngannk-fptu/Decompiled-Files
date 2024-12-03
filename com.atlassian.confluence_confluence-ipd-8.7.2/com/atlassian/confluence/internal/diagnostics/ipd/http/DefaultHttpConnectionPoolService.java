/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.internal.diagnostics.ipd.http;

import com.atlassian.confluence.internal.diagnostics.ipd.exception.UnableReadAttributeException;
import com.atlassian.confluence.internal.diagnostics.ipd.http.HttpConnectionPoolMetric;
import java.lang.management.ManagementFactory;
import java.util.Set;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultHttpConnectionPoolService {
    private static final Logger log = LoggerFactory.getLogger(DefaultHttpConnectionPoolService.class);
    private static final String SOURCE_METRIC_HTTP_NUM_MAX = "maxThreads";
    private static final String SOURCE_METRIC_HTTP_NUM_ACTIVE = "currentThreadsBusy";
    private static final String SOURCE_METRIC_HTTP_NUM_CURRENT = "currentThreadCount";
    private static final String SOURCE_MBEAN = "*:name=\"http*\",type=ThreadPool";

    DefaultHttpConnectionPoolService() {
    }

    public MBeanServer getPlatformMBeanServer() {
        return ManagementFactory.getPlatformMBeanServer();
    }

    HttpConnectionPoolMetric getHttpPoolSizeValue() {
        MBeanServer platformMBeanServer = this.getPlatformMBeanServer();
        Set<ObjectName> sourceMbeanNames = this.findTomcatManagerBeanNames(platformMBeanServer);
        int numMax = 0;
        int numActive = 0;
        int numCurrent = 0;
        for (ObjectName sourceMbeanName : sourceMbeanNames) {
            try {
                numMax += ((Integer)platformMBeanServer.getAttribute(sourceMbeanName, SOURCE_METRIC_HTTP_NUM_MAX)).intValue();
                numActive += ((Integer)platformMBeanServer.getAttribute(sourceMbeanName, SOURCE_METRIC_HTTP_NUM_ACTIVE)).intValue();
                numCurrent += ((Integer)platformMBeanServer.getAttribute(sourceMbeanName, SOURCE_METRIC_HTTP_NUM_CURRENT)).intValue();
            }
            catch (Exception ex) {
                log.warn("Unable to read attribute", (Throwable)ex);
            }
        }
        return new HttpConnectionPoolMetric(numMax, numCurrent, numActive);
    }

    private Set<ObjectName> findTomcatManagerBeanNames(MBeanServer platformMBeanServer) {
        ObjectName objectName;
        try {
            objectName = new ObjectName(SOURCE_MBEAN);
        }
        catch (MalformedObjectNameException e) {
            throw new UnableReadAttributeException(e, new String[0]);
        }
        return platformMBeanServer.queryNames(objectName, null);
    }
}

