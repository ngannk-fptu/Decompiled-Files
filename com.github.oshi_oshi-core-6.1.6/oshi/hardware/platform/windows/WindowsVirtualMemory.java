/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.platform.win32.Kernel32
 *  com.sun.jna.platform.win32.Psapi
 *  com.sun.jna.platform.win32.Psapi$PERFORMANCE_INFORMATION
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package oshi.hardware.platform.windows;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Psapi;
import java.util.Map;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.windows.perfmon.MemoryInformation;
import oshi.driver.windows.perfmon.PagingFile;
import oshi.hardware.common.AbstractVirtualMemory;
import oshi.hardware.platform.windows.WindowsGlobalMemory;
import oshi.util.Memoizer;
import oshi.util.tuples.Pair;
import oshi.util.tuples.Triplet;

@ThreadSafe
final class WindowsVirtualMemory
extends AbstractVirtualMemory {
    private static final Logger LOG = LoggerFactory.getLogger(WindowsVirtualMemory.class);
    private final WindowsGlobalMemory global;
    private final Supplier<Long> used = Memoizer.memoize(WindowsVirtualMemory::querySwapUsed, Memoizer.defaultExpiration());
    private final Supplier<Triplet<Long, Long, Long>> totalVmaxVused = Memoizer.memoize(WindowsVirtualMemory::querySwapTotalVirtMaxVirtUsed, Memoizer.defaultExpiration());
    private final Supplier<Pair<Long, Long>> swapInOut = Memoizer.memoize(WindowsVirtualMemory::queryPageSwaps, Memoizer.defaultExpiration());

    WindowsVirtualMemory(WindowsGlobalMemory windowsGlobalMemory) {
        this.global = windowsGlobalMemory;
    }

    @Override
    public long getSwapUsed() {
        return this.global.getPageSize() * this.used.get();
    }

    @Override
    public long getSwapTotal() {
        return this.global.getPageSize() * this.totalVmaxVused.get().getA();
    }

    @Override
    public long getVirtualMax() {
        return this.global.getPageSize() * this.totalVmaxVused.get().getB();
    }

    @Override
    public long getVirtualInUse() {
        return this.global.getPageSize() * this.totalVmaxVused.get().getC();
    }

    @Override
    public long getSwapPagesIn() {
        return this.swapInOut.get().getA();
    }

    @Override
    public long getSwapPagesOut() {
        return this.swapInOut.get().getB();
    }

    private static long querySwapUsed() {
        return PagingFile.querySwapUsed().getOrDefault(PagingFile.PagingPercentProperty.PERCENTUSAGE, 0L);
    }

    private static Triplet<Long, Long, Long> querySwapTotalVirtMaxVirtUsed() {
        Psapi.PERFORMANCE_INFORMATION perfInfo = new Psapi.PERFORMANCE_INFORMATION();
        if (!Psapi.INSTANCE.GetPerformanceInfo(perfInfo, perfInfo.size())) {
            LOG.error("Failed to get Performance Info. Error code: {}", (Object)Kernel32.INSTANCE.GetLastError());
            return new Triplet<Long, Long, Long>(0L, 0L, 0L);
        }
        return new Triplet<Long, Long, Long>(perfInfo.CommitLimit.longValue() - perfInfo.PhysicalTotal.longValue(), perfInfo.CommitLimit.longValue(), perfInfo.CommitTotal.longValue());
    }

    private static Pair<Long, Long> queryPageSwaps() {
        Map<MemoryInformation.PageSwapProperty, Long> valueMap = MemoryInformation.queryPageSwaps();
        return new Pair<Long, Long>(valueMap.getOrDefault(MemoryInformation.PageSwapProperty.PAGESINPUTPERSEC, 0L), valueMap.getOrDefault(MemoryInformation.PageSwapProperty.PAGESOUTPUTPERSEC, 0L));
    }
}

