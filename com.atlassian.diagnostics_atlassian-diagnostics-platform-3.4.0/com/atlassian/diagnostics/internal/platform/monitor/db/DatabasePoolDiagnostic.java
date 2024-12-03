/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.diagnostics.internal.platform.monitor.db;

import java.util.Objects;

public class DatabasePoolDiagnostic {
    public static final DatabasePoolDiagnostic EMPTY = new DatabasePoolDiagnostic(0, 0, 0);
    private final int maxConnections;
    private final int activeConnections;
    private final int idleConnections;

    public DatabasePoolDiagnostic(int idleConnections, int activeConnections, int maxConnections) {
        this.idleConnections = idleConnections;
        this.activeConnections = activeConnections;
        this.maxConnections = maxConnections;
    }

    public int getIdleConnections() {
        return this.idleConnections;
    }

    public int getActiveConnections() {
        return this.activeConnections;
    }

    public int getMaxConnections() {
        return this.maxConnections;
    }

    public boolean isEmpty() {
        return this == EMPTY || this.maxConnections == 0;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        DatabasePoolDiagnostic that = (DatabasePoolDiagnostic)o;
        return this.maxConnections == that.maxConnections && this.activeConnections == that.activeConnections && this.idleConnections == that.idleConnections;
    }

    public int hashCode() {
        return Objects.hash(this.maxConnections, this.activeConnections, this.idleConnections);
    }
}

