/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerConnection;
import com.microsoft.sqlserver.jdbc.SQLServerConnection43;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.Util;
import java.sql.BatchUpdateException;

final class DriverJDBCVersion {
    static final int MAJOR = 4;
    static final int MINOR = 3;
    private static double jvmVersion = Double.parseDouble(Util.SYSTEM_SPEC_VERSION);
    private static int pid = 0;

    private DriverJDBCVersion() {
        throw new UnsupportedOperationException(SQLServerException.getErrString("R_notSupported"));
    }

    static final boolean checkSupportsJDBC43() {
        return true;
    }

    static final void throwBatchUpdateException(SQLServerException lastError, long[] updateCounts) throws BatchUpdateException {
        throw new BatchUpdateException(lastError.getMessage(), lastError.getSQLState(), lastError.getErrorCode(), updateCounts, new Throwable(lastError.getMessage()));
    }

    static SQLServerConnection getSQLServerConnection(String parentInfo) throws SQLServerException {
        return jvmVersion >= 9.0 ? new SQLServerConnection43(parentInfo) : new SQLServerConnection(parentInfo);
    }

    static int getProcessId() {
        return pid;
    }

    static {
        long pidLong = 0L;
        try {
            pidLong = ProcessHandle.current().pid();
        }
        catch (NoClassDefFoundError noClassDefFoundError) {
            // empty catch block
        }
        pid = pidLong > Integer.MAX_VALUE ? 0 : (int)pidLong;
    }
}

