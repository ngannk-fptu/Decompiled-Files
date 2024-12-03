/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Native
 *  com.sun.jna.Structure
 *  com.sun.jna.platform.mac.SystemB
 *  com.sun.jna.platform.mac.SystemB$VMStatistics
 *  com.sun.jna.platform.mac.SystemB$XswUsage
 *  com.sun.jna.ptr.IntByReference
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package oshi.hardware.platform.mac;

import com.sun.jna.Native;
import com.sun.jna.Structure;
import com.sun.jna.platform.mac.SystemB;
import com.sun.jna.ptr.IntByReference;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.common.AbstractVirtualMemory;
import oshi.hardware.platform.mac.MacGlobalMemory;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.platform.mac.SysctlUtil;
import oshi.util.tuples.Pair;

@ThreadSafe
final class MacVirtualMemory
extends AbstractVirtualMemory {
    private static final Logger LOG = LoggerFactory.getLogger(MacVirtualMemory.class);
    private final MacGlobalMemory global;
    private final Supplier<Pair<Long, Long>> usedTotal = Memoizer.memoize(MacVirtualMemory::querySwapUsage, Memoizer.defaultExpiration());
    private final Supplier<Pair<Long, Long>> inOut = Memoizer.memoize(MacVirtualMemory::queryVmStat, Memoizer.defaultExpiration());

    MacVirtualMemory(MacGlobalMemory macGlobalMemory) {
        this.global = macGlobalMemory;
    }

    @Override
    public long getSwapUsed() {
        return this.usedTotal.get().getA();
    }

    @Override
    public long getSwapTotal() {
        return this.usedTotal.get().getB();
    }

    @Override
    public long getVirtualMax() {
        return this.global.getTotal() + this.getSwapTotal();
    }

    @Override
    public long getVirtualInUse() {
        return this.global.getTotal() - this.global.getAvailable() + this.getSwapUsed();
    }

    @Override
    public long getSwapPagesIn() {
        return this.inOut.get().getA();
    }

    @Override
    public long getSwapPagesOut() {
        return this.inOut.get().getB();
    }

    private static Pair<Long, Long> querySwapUsage() {
        long swapUsed = 0L;
        long swapTotal = 0L;
        SystemB.XswUsage xswUsage = new SystemB.XswUsage();
        if (SysctlUtil.sysctl("vm.swapusage", (Structure)xswUsage)) {
            swapUsed = xswUsage.xsu_used;
            swapTotal = xswUsage.xsu_total;
        }
        return new Pair<Long, Long>(swapUsed, swapTotal);
    }

    private static Pair<Long, Long> queryVmStat() {
        long swapPagesIn = 0L;
        long swapPagesOut = 0L;
        SystemB.VMStatistics vmStats = new SystemB.VMStatistics();
        if (0 == SystemB.INSTANCE.host_statistics(SystemB.INSTANCE.mach_host_self(), 2, (Structure)vmStats, new IntByReference(vmStats.size() / SystemB.INT_SIZE))) {
            swapPagesIn = ParseUtil.unsignedIntToLong(vmStats.pageins);
            swapPagesOut = ParseUtil.unsignedIntToLong(vmStats.pageouts);
        } else {
            LOG.error("Failed to get host VM info. Error code: {}", (Object)Native.getLastError());
        }
        return new Pair<Long, Long>(swapPagesIn, swapPagesOut);
    }
}

