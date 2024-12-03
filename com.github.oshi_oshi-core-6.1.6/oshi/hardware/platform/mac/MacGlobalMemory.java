/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Native
 *  com.sun.jna.Structure
 *  com.sun.jna.platform.mac.SystemB
 *  com.sun.jna.platform.mac.SystemB$VMStatistics
 *  com.sun.jna.ptr.IntByReference
 *  com.sun.jna.ptr.LongByReference
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package oshi.hardware.platform.mac;

import com.sun.jna.Native;
import com.sun.jna.Structure;
import com.sun.jna.platform.mac.SystemB;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.PhysicalMemory;
import oshi.hardware.VirtualMemory;
import oshi.hardware.common.AbstractGlobalMemory;
import oshi.hardware.platform.mac.MacVirtualMemory;
import oshi.util.ExecutingCommand;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.platform.mac.SysctlUtil;

@ThreadSafe
final class MacGlobalMemory
extends AbstractGlobalMemory {
    private static final Logger LOG = LoggerFactory.getLogger(MacGlobalMemory.class);
    private final Supplier<Long> available = Memoizer.memoize(this::queryVmStats, Memoizer.defaultExpiration());
    private final Supplier<Long> total = Memoizer.memoize(MacGlobalMemory::queryPhysMem);
    private final Supplier<Long> pageSize = Memoizer.memoize(MacGlobalMemory::queryPageSize);
    private final Supplier<VirtualMemory> vm = Memoizer.memoize(this::createVirtualMemory);

    MacGlobalMemory() {
    }

    @Override
    public long getAvailable() {
        return this.available.get();
    }

    @Override
    public long getTotal() {
        return this.total.get();
    }

    @Override
    public long getPageSize() {
        return this.pageSize.get();
    }

    @Override
    public VirtualMemory getVirtualMemory() {
        return this.vm.get();
    }

    @Override
    public List<PhysicalMemory> getPhysicalMemory() {
        ArrayList<PhysicalMemory> pmList = new ArrayList<PhysicalMemory>();
        List<String> sp = ExecutingCommand.runNative("system_profiler SPMemoryDataType");
        int bank = 0;
        String bankLabel = "unknown";
        long capacity = 0L;
        long speed = 0L;
        String manufacturer = "unknown";
        String memoryType = "unknown";
        for (String line : sp) {
            String[] split;
            if (line.trim().startsWith("BANK")) {
                int colon;
                if (bank++ > 0) {
                    pmList.add(new PhysicalMemory(bankLabel, capacity, speed, manufacturer, memoryType));
                }
                if ((colon = (bankLabel = line.trim()).lastIndexOf(58)) <= 0) continue;
                bankLabel = bankLabel.substring(0, colon - 1);
                continue;
            }
            if (bank <= 0 || (split = line.trim().split(":")).length != 2) continue;
            switch (split[0]) {
                case "Size": {
                    capacity = ParseUtil.parseDecimalMemorySizeToBinary(split[1].trim());
                    break;
                }
                case "Type": {
                    memoryType = split[1].trim();
                    break;
                }
                case "Speed": {
                    speed = ParseUtil.parseHertz(split[1]);
                    break;
                }
                case "Manufacturer": {
                    manufacturer = split[1].trim();
                    break;
                }
            }
        }
        pmList.add(new PhysicalMemory(bankLabel, capacity, speed, manufacturer, memoryType));
        return pmList;
    }

    private long queryVmStats() {
        SystemB.VMStatistics vmStats = new SystemB.VMStatistics();
        if (0 != SystemB.INSTANCE.host_statistics(SystemB.INSTANCE.mach_host_self(), 2, (Structure)vmStats, new IntByReference(vmStats.size() / SystemB.INT_SIZE))) {
            LOG.error("Failed to get host VM info. Error code: {}", (Object)Native.getLastError());
            return 0L;
        }
        return (long)(vmStats.free_count + vmStats.inactive_count) * this.getPageSize();
    }

    private static long queryPhysMem() {
        return SysctlUtil.sysctl("hw.memsize", 0L);
    }

    private static long queryPageSize() {
        LongByReference pPageSize = new LongByReference();
        if (0 == SystemB.INSTANCE.host_page_size(SystemB.INSTANCE.mach_host_self(), pPageSize)) {
            return pPageSize.getValue();
        }
        LOG.error("Failed to get host page size. Error code: {}", (Object)Native.getLastError());
        return 4098L;
    }

    private VirtualMemory createVirtualMemory() {
        return new MacVirtualMemory(this);
    }
}

