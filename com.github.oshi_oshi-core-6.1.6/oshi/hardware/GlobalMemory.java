/*
 * Decompiled with CFR 0.152.
 */
package oshi.hardware;

import java.util.List;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.PhysicalMemory;
import oshi.hardware.VirtualMemory;

@ThreadSafe
public interface GlobalMemory {
    public long getTotal();

    public long getAvailable();

    public long getPageSize();

    public VirtualMemory getVirtualMemory();

    public List<PhysicalMemory> getPhysicalMemory();
}

