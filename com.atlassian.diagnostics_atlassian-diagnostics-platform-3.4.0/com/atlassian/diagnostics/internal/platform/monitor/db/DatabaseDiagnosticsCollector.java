/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.diagnostics.internal.platform.monitor.db;

import com.atlassian.diagnostics.internal.platform.monitor.db.SqlOperation;
import java.sql.Connection;
import java.sql.SQLException;

public interface DatabaseDiagnosticsCollector {
    public boolean isEnabled();

    public void trackConnection(Connection var1);

    public void removeTrackedConnection(Connection var1);

    public <T> T recordExecutionTime(SqlOperation<T> var1, String var2) throws SQLException;
}

