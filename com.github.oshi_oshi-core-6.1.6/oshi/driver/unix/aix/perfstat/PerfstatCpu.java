/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.platform.unix.aix.Perfstat
 *  com.sun.jna.platform.unix.aix.Perfstat$perfstat_cpu_t
 *  com.sun.jna.platform.unix.aix.Perfstat$perfstat_cpu_total_t
 *  com.sun.jna.platform.unix.aix.Perfstat$perfstat_id_t
 */
package oshi.driver.unix.aix.perfstat;

import com.sun.jna.platform.unix.aix.Perfstat;
import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public final class PerfstatCpu {
    private static final Perfstat PERF = Perfstat.INSTANCE;

    private PerfstatCpu() {
    }

    public static Perfstat.perfstat_cpu_total_t queryCpuTotal() {
        Perfstat.perfstat_cpu_total_t cpu = new Perfstat.perfstat_cpu_total_t();
        int ret = PERF.perfstat_cpu_total(null, cpu, cpu.size(), 1);
        if (ret > 0) {
            return cpu;
        }
        return new Perfstat.perfstat_cpu_total_t();
    }

    public static Perfstat.perfstat_cpu_t[] queryCpu() {
        Perfstat.perfstat_cpu_t[] statp;
        Perfstat.perfstat_id_t firstcpu;
        int ret;
        Perfstat.perfstat_cpu_t cpu = new Perfstat.perfstat_cpu_t();
        int cputotal = PERF.perfstat_cpu(null, null, cpu.size(), 0);
        if (cputotal > 0 && (ret = PERF.perfstat_cpu(firstcpu = new Perfstat.perfstat_id_t(), statp = (Perfstat.perfstat_cpu_t[])cpu.toArray(cputotal), cpu.size(), cputotal)) > 0) {
            return statp;
        }
        return new Perfstat.perfstat_cpu_t[0];
    }

    public static long queryCpuAffinityMask() {
        int cpus = PerfstatCpu.queryCpuTotal().ncpus;
        if (cpus < 63) {
            return (1L << cpus) - 1L;
        }
        return cpus == 63 ? Long.MAX_VALUE : -1L;
    }
}

