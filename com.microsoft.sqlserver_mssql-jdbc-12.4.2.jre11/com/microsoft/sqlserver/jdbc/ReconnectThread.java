/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.DriverError;
import com.microsoft.sqlserver.jdbc.SQLServerConnection;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.SQLState;
import com.microsoft.sqlserver.jdbc.SharedTimer;
import com.microsoft.sqlserver.jdbc.TDSCommand;
import com.microsoft.sqlserver.jdbc.TDSTimeoutTask;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

final class ReconnectThread
extends Thread {
    static final Logger loggerExternal = Logger.getLogger("com.microsoft.sqlserver.jdbc.ReconnectThread");
    private SQLServerConnection con = null;
    private SQLServerException eReceived = null;
    private TDSCommand command = null;
    private volatile boolean stopRequested = false;
    private int connectRetryCount = 0;

    private ReconnectThread() {
    }

    ReconnectThread(SQLServerConnection sqlC, TDSCommand cmd) {
        this.con = sqlC;
        this.command = cmd;
        this.connectRetryCount = this.con.getRetryCount();
        this.eReceived = null;
        this.stopRequested = false;
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.finer("ReconnectThread initialized. Connection retry count = " + this.connectRetryCount + "; Command = " + cmd.toString());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void run() {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.finer("Starting ReconnectThread for command: " + this.command.toString());
        }
        boolean interruptsEnabled = this.command.getInterruptsEnabled();
        this.command.setInterruptsEnabled(true);
        this.command.attachThread(this);
        SharedTimer timer = null;
        ScheduledFuture<?> timeout = null;
        if (this.command.getQueryTimeoutSeconds() > 0) {
            timer = SharedTimer.getTimer();
            timeout = timer.schedule(new TDSTimeoutTask(this.command, null), this.command.getQueryTimeoutSeconds());
        }
        boolean keepRetrying = true;
        while (this.connectRetryCount > 0 && !this.stopRequested && keepRetrying) {
            if (loggerExternal.isLoggable(Level.FINER)) {
                loggerExternal.finer("Running reconnect for command: " + this.command.toString() + " ; ConnectRetryCount = " + this.connectRetryCount);
            }
            try {
                this.eReceived = null;
                this.con.connect(null, this.con.getPooledConnectionParent());
                keepRetrying = false;
            }
            catch (SQLServerException e) {
                if (this.stopRequested) continue;
                this.eReceived = e;
                if (this.con.isFatalError(e)) {
                    keepRetrying = false;
                    continue;
                }
                try {
                    if (this.connectRetryCount <= 1) continue;
                    Thread.sleep((long)this.con.getRetryInterval() * 1000L);
                }
                catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    this.eReceived = new SQLServerException(SQLServerException.getErrString("R_queryTimedOut"), SQLState.STATEMENT_CANCELED, DriverError.NOT_SET, null);
                    keepRetrying = false;
                }
            }
            finally {
                --this.connectRetryCount;
                try {
                    this.command.checkForInterrupt();
                }
                catch (SQLServerException e) {
                    keepRetrying = false;
                    this.eReceived = e;
                }
            }
        }
        if (this.connectRetryCount == 0 && keepRetrying) {
            this.eReceived = new SQLServerException(SQLServerException.getErrString("R_crClientAllRecoveryAttemptsFailed"), this.eReceived);
        }
        this.command.setInterruptsEnabled(interruptsEnabled);
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.finer("ReconnectThread exiting for command: " + this.command.toString());
        }
        if (timeout != null) {
            timeout.cancel(false);
            timeout = null;
        }
        if (timer != null) {
            timer.removeRef();
            timer = null;
        }
    }

    void stop(boolean blocking) {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.finer("ReconnectThread stop requested for command: " + this.command.toString());
        }
        this.stopRequested = true;
        if (blocking && this.isAlive()) {
            while (this.getState() != Thread.State.TERMINATED) {
            }
        }
    }

    SQLServerException getException() {
        return this.eReceived;
    }
}

