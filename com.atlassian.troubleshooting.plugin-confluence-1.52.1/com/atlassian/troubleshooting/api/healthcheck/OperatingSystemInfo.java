/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.api.healthcheck;

public interface OperatingSystemInfo {
    public String getName();

    public String getArch();

    public String getVersion();

    public int getAvailableProcessors();

    public double getSystemLoadAverage();

    public long getCommittedVirtualMemorySize();

    public long getTotalSwapSpaceSize();

    public long getFreeSwapSpaceSize();

    public long getTotalPhysicalMemorySize();

    public long getFreePhysicalMemorySize();

    public double getSystemCpuLoad();

    public double getProcessCpuLoad();

    public long getMaxFileDescriptorCount();

    public long getOpenFileDescriptorCount();

    public boolean isOsUnix();
}

