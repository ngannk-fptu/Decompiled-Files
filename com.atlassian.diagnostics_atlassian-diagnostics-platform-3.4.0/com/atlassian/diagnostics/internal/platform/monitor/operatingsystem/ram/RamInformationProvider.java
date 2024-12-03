/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  oshi.SystemInfo
 *  oshi.hardware.HardwareAbstractionLayer
 */
package com.atlassian.diagnostics.internal.platform.monitor.operatingsystem.ram;

import com.atlassian.diagnostics.internal.platform.monitor.operatingsystem.MemoryInformation;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;

public class RamInformationProvider
extends MemoryInformation {
    private final HardwareAbstractionLayer hardware = new SystemInfo().getHardware();

    long freeMemory() {
        return this.asMegaBytes(this.hardware.getMemory().getAvailable());
    }

    long totalMemory() {
        return this.asMegaBytes(this.hardware.getMemory().getTotal());
    }
}

