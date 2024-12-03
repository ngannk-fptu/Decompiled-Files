/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.internal.jmx.JmxService
 */
package com.atlassian.diagnostics.internal.platform.monitor.operatingsystem.cpu;

import com.atlassian.diagnostics.internal.jmx.JmxService;

class CPUDiagnostic {
    private static final String OPERATING_SYSTEM_OBJECT_NAME = "java.lang:type=OperatingSystem";
    private static final String SYSTEM_CPU_LOAD_ATTRIBUTE_NAME = "SystemCpuLoad";
    private final JmxService jmxService;

    CPUDiagnostic(JmxService jmxService) {
        this.jmxService = jmxService;
    }

    double getSystemCpuLoad() {
        Double systemCpuLoad = (Double)this.jmxService.getJmxAttribute(OPERATING_SYSTEM_OBJECT_NAME, SYSTEM_CPU_LOAD_ATTRIBUTE_NAME);
        if (systemCpuLoad != null && systemCpuLoad > 0.0) {
            return systemCpuLoad * 100.0;
        }
        return 0.0;
    }
}

