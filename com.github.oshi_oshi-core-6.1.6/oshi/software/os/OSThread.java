/*
 * Decompiled with CFR 0.152.
 */
package oshi.software.os;

import oshi.software.os.OSProcess;

public interface OSThread {
    public int getThreadId();

    default public String getName() {
        return "";
    }

    public OSProcess.State getState();

    public double getThreadCpuLoadCumulative();

    public double getThreadCpuLoadBetweenTicks(OSThread var1);

    public int getOwningProcessId();

    default public long getStartMemoryAddress() {
        return 0L;
    }

    default public long getContextSwitches() {
        return 0L;
    }

    default public long getMinorFaults() {
        return 0L;
    }

    default public long getMajorFaults() {
        return 0L;
    }

    public long getKernelTime();

    public long getUserTime();

    public long getUpTime();

    public long getStartTime();

    public int getPriority();

    default public boolean updateAttributes() {
        return false;
    }
}

