/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.internal.jmx.JmxService
 *  javax.annotation.Nonnull
 */
package com.atlassian.diagnostics.internal.platform.monitor.db;

import com.atlassian.diagnostics.internal.jmx.JmxService;
import com.atlassian.diagnostics.internal.platform.monitor.db.DatabasePoolDiagnostic;
import com.atlassian.diagnostics.internal.platform.monitor.db.JmxDatabasePoolAttributes;
import java.time.Duration;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nonnull;

public class DatabasePoolDiagnosticProvider {
    private static final int IDLE_CONNECTION_ATTRIBUTE_INDEX = 0;
    private static final int ACTIVE_CONNECTION_ATTRIBUTE_INDEX = 1;
    private static final int MAX_CONNECTION_ATTRIBUTE_INDEX = 2;
    private final JmxService jmxService;
    private JmxDatabasePoolAttributes jmxDatabasePoolAttributes = JmxDatabasePoolAttributes.UNKNOWN;

    public DatabasePoolDiagnosticProvider(JmxService jmxService) {
        this.jmxService = jmxService;
        this.resolveJmxDatabasePoolAttributes();
    }

    @Nonnull
    public DatabasePoolDiagnostic getDiagnostic() {
        this.resolveJmxDatabasePoolAttributes();
        List databasePoolAttributeValues = this.jmxService.getJmxAttributes(this.jmxDatabasePoolAttributes.instanceOfQuery, new String[]{this.jmxDatabasePoolAttributes.idleConnectionsAttributeName, this.jmxDatabasePoolAttributes.activeConnectionsAttribute, this.jmxDatabasePoolAttributes.maxConnectionsAttribute});
        if (databasePoolAttributeValues.size() == 3) {
            return new DatabasePoolDiagnostic((Integer)databasePoolAttributeValues.get(0), (Integer)databasePoolAttributeValues.get(1), (Integer)databasePoolAttributeValues.get(2));
        }
        return DatabasePoolDiagnostic.EMPTY;
    }

    @Nonnull
    public Duration getPoolConnectionLeakTimeout() {
        this.resolveJmxDatabasePoolAttributes();
        Integer abandonedTimeoutValue = (Integer)this.jmxService.getJmxAttribute(this.jmxDatabasePoolAttributes.instanceOfQuery, this.jmxDatabasePoolAttributes.abandonedTimeoutAttributeName);
        if (abandonedTimeoutValue != null && abandonedTimeoutValue != Integer.MAX_VALUE) {
            return Duration.ofSeconds(abandonedTimeoutValue.intValue());
        }
        return Duration.ZERO;
    }

    private void resolveJmxDatabasePoolAttributes() {
        if (this.jmxDatabasePoolAttributes.equals((Object)JmxDatabasePoolAttributes.UNKNOWN)) {
            this.jmxDatabasePoolAttributes = EnumSet.allOf(JmxDatabasePoolAttributes.class).stream().filter(jmxDatabasePoolAttributes -> this.jmxService.hasObjectName(jmxDatabasePoolAttributes.instanceOfQuery)).findAny().orElse(JmxDatabasePoolAttributes.UNKNOWN);
        }
    }
}

