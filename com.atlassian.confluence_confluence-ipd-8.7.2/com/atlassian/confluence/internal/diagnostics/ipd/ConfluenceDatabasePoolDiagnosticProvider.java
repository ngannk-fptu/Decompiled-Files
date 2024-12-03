/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.internal.jmx.JmxService
 *  com.atlassian.diagnostics.internal.platform.monitor.db.DatabasePoolDiagnosticProvider
 */
package com.atlassian.confluence.internal.diagnostics.ipd;

import com.atlassian.diagnostics.internal.jmx.JmxService;
import com.atlassian.diagnostics.internal.platform.monitor.db.DatabasePoolDiagnosticProvider;
import java.util.Objects;

public class ConfluenceDatabasePoolDiagnosticProvider
extends DatabasePoolDiagnosticProvider {
    public ConfluenceDatabasePoolDiagnosticProvider(JmxService jmxService) {
        super(Objects.requireNonNull(jmxService));
    }
}

