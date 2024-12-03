/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Memory
 *  com.sun.jna.Pointer
 *  com.sun.jna.platform.unix.LibCAPI$size_t
 *  com.sun.jna.platform.unix.LibCAPI$size_t$ByReference
 */
package oshi.hardware.platform.unix.freebsd;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.platform.unix.LibCAPI;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.common.AbstractSensors;
import oshi.jna.platform.unix.FreeBsdLibc;

@ThreadSafe
final class FreeBsdSensors
extends AbstractSensors {
    FreeBsdSensors() {
    }

    @Override
    public double queryCpuTemperature() {
        return FreeBsdSensors.queryKldloadCoretemp();
    }

    private static double queryKldloadCoretemp() {
        String name = "dev.cpu.%d.temperature";
        LibCAPI.size_t.ByReference size = new LibCAPI.size_t.ByReference(new LibCAPI.size_t((long)FreeBsdLibc.INT_SIZE));
        Memory p = new Memory(size.longValue());
        int cpu = 0;
        double sumTemp = 0.0;
        while (0 == FreeBsdLibc.INSTANCE.sysctlbyname(String.format(name, cpu), (Pointer)p, size, null, LibCAPI.size_t.ZERO)) {
            sumTemp += (double)p.getInt(0L) / 10.0 - 273.15;
            ++cpu;
        }
        return cpu > 0 ? sumTemp / (double)cpu : Double.NaN;
    }

    @Override
    public int[] queryFanSpeeds() {
        return new int[0];
    }

    @Override
    public double queryCpuVoltage() {
        return 0.0;
    }
}

