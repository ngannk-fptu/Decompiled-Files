/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.SystemUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.healthcheck.impl;

import com.atlassian.troubleshooting.api.healthcheck.OperatingSystemInfo;
import java.util.Objects;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class DefaultOperatingSystemInfo
implements OperatingSystemInfo {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultOperatingSystemInfo.class);
    private final MBeanServer mbeanServer;
    private final ObjectName operatingSystemMxBeanName;

    @Autowired
    public DefaultOperatingSystemInfo(MBeanServer mbeanServer) {
        this.mbeanServer = Objects.requireNonNull(mbeanServer);
        try {
            this.operatingSystemMxBeanName = new ObjectName("java.lang:type=OperatingSystem");
        }
        catch (MalformedObjectNameException e) {
            throw new RuntimeException("Error creating object name java.lang:type=OperatingSystem", e);
        }
    }

    @Override
    public String getName() {
        return this.getAttribute("Name", "");
    }

    @Override
    public String getArch() {
        return this.getAttribute("Arch", "");
    }

    @Override
    public String getVersion() {
        return this.getAttribute("Version", "");
    }

    @Override
    public int getAvailableProcessors() {
        return this.getAttribute("AvailableProcessors", -1);
    }

    @Override
    public double getSystemLoadAverage() {
        return this.getAttribute("SystemLoadAverage", -1.0);
    }

    @Override
    public long getCommittedVirtualMemorySize() {
        return this.getAttribute("CommittedVirtualMemorySize", -1L);
    }

    @Override
    public long getTotalSwapSpaceSize() {
        return this.getAttribute("TotalSwapSpaceSize", -1L);
    }

    @Override
    public long getFreeSwapSpaceSize() {
        return this.getAttribute("FreeSwapSpaceSize", -1L);
    }

    @Override
    public long getTotalPhysicalMemorySize() {
        return this.getAttribute("TotalPhysicalMemorySize", -1L);
    }

    @Override
    public long getFreePhysicalMemorySize() {
        return this.getAttribute("FreePhysicalMemorySize", -1L);
    }

    @Override
    public double getSystemCpuLoad() {
        return this.getAttribute("SystemCpuLoad", -1.0);
    }

    @Override
    public double getProcessCpuLoad() {
        return this.getAttribute("ProcessCpuLoad", -1.0);
    }

    @Override
    public long getMaxFileDescriptorCount() {
        return this.getAttribute("MaxFileDescriptorCount", -1L);
    }

    @Override
    public long getOpenFileDescriptorCount() {
        return this.getAttribute("OpenFileDescriptorCount", -1L);
    }

    private <T> T getAttribute(String name, T defaultValue) {
        Object value = null;
        try {
            value = this.mbeanServer.getAttribute(this.operatingSystemMxBeanName, name);
        }
        catch (AttributeNotFoundException | InstanceNotFoundException | MBeanException | ReflectionException e) {
            LOG.debug("Error getting java.lang:type=OperatingSystem attribute " + name, (Throwable)e);
        }
        return (T)(value == null ? defaultValue : value);
    }

    @Override
    public boolean isOsUnix() {
        return SystemUtils.IS_OS_UNIX;
    }
}

