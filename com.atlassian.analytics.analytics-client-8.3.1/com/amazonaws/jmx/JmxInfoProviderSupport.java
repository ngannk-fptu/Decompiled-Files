/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.LogFactory
 */
package com.amazonaws.jmx;

import com.amazonaws.jmx.MBeans;
import com.amazonaws.jmx.SdkMBeanRegistrySupport;
import com.amazonaws.jmx.spi.JmxInfoProvider;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.List;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.apache.commons.logging.LogFactory;

public class JmxInfoProviderSupport
implements JmxInfoProvider {
    @Override
    public long[] getFileDecriptorInfo() {
        MBeanServer mbsc = MBeans.getMBeanServer();
        try {
            AttributeList attributes = mbsc.getAttributes(new ObjectName("java.lang:type=OperatingSystem"), new String[]{"OpenFileDescriptorCount", "MaxFileDescriptorCount"});
            List<Attribute> attrList = attributes.asList();
            long openFdCount = (Long)attrList.get(0).getValue();
            long maxFdCount = (Long)attrList.get(1).getValue();
            long[] fdCounts = new long[]{openFdCount, maxFdCount};
            return fdCounts;
        }
        catch (Exception e) {
            LogFactory.getLog(SdkMBeanRegistrySupport.class).debug((Object)"Failed to retrieve file descriptor info", (Throwable)e);
            return null;
        }
    }

    @Override
    public int getThreadCount() {
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        return threadMXBean.getThreadCount();
    }

    @Override
    public int getDaemonThreadCount() {
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        return threadMXBean.getDaemonThreadCount();
    }

    @Override
    public int getPeakThreadCount() {
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        return threadMXBean.getPeakThreadCount();
    }

    @Override
    public long getTotalStartedThreadCount() {
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        return threadMXBean.getTotalStartedThreadCount();
    }

    @Override
    public long[] findDeadlockedThreads() {
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        return threadMXBean.findDeadlockedThreads();
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

