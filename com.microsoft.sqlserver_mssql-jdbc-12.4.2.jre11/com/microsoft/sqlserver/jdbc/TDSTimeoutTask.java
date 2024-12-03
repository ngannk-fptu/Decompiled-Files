/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerConnection;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.TDSCommand;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;

class TDSTimeoutTask
implements Runnable {
    private static final AtomicLong COUNTER = new AtomicLong(0L);
    private final UUID connectionId;
    private final TDSCommand command;
    private final SQLServerConnection sqlServerConnection;

    public TDSTimeoutTask(TDSCommand command, SQLServerConnection sqlServerConnection) {
        this.connectionId = sqlServerConnection == null ? null : sqlServerConnection.getClientConIdInternal();
        this.command = command;
        this.sqlServerConnection = sqlServerConnection;
    }

    @Override
    public final void run() {
        String name = "mssql-timeout-task-" + COUNTER.incrementAndGet() + "-" + this.connectionId;
        Thread thread = new Thread(this::interrupt, name);
        thread.setDaemon(true);
        thread.start();
    }

    protected void interrupt() {
        try {
            if (null == this.command) {
                if (null != this.sqlServerConnection) {
                    this.sqlServerConnection.terminate(3, SQLServerException.getErrString("R_connectionIsClosed"));
                }
            } else {
                this.command.interrupt(SQLServerException.getErrString("R_queryTimedOut"));
            }
        }
        catch (SQLServerException e) {
            assert (null != this.command);
            this.command.log(Level.WARNING, "Command could not be timed out. Reason: " + e.getMessage());
        }
    }
}

