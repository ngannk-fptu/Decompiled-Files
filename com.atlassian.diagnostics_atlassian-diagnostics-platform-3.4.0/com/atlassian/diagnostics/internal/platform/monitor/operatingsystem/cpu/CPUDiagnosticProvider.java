/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.internal.jmx.JmxService
 */
package com.atlassian.diagnostics.internal.platform.monitor.operatingsystem.cpu;

import com.atlassian.diagnostics.internal.jmx.JmxService;
import com.atlassian.diagnostics.internal.platform.monitor.operatingsystem.cpu.CPUDiagnostic;

public class CPUDiagnosticProvider {
    private final JmxService jmxService;

    public CPUDiagnosticProvider(JmxService jmxService) {
        this.jmxService = jmxService;
    }

    CPUDiagnostic getDiagnostics() {
        return new CPUDiagnostic(this.jmxService);
    }
}

