/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.FailoverInfo;
import com.microsoft.sqlserver.jdbc.ReconnectThread;
import com.microsoft.sqlserver.jdbc.SQLServerConnection;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.SessionStateTable;
import com.microsoft.sqlserver.jdbc.TDSCommand;
import com.microsoft.sqlserver.jdbc.Util;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

class IdleConnectionResiliency {
    private static final Logger loggerExternal = Logger.getLogger("com.microsoft.sqlserver.jdbc.IdleConnectionResiliency");
    private boolean connectionRecoveryNegotiated;
    private int connectRetryCount;
    private SQLServerConnection connection;
    private SessionStateTable sessionStateTable;
    private ReconnectThread reconnectThread;
    private AtomicInteger unprocessedResponseCount = new AtomicInteger();
    private boolean connectionRecoveryPossible;
    private SQLServerException reconnectErrorReceived = null;
    private String loginInstanceValue;
    private int loginNPort;
    private FailoverInfo loginFailoverInfo;
    private int loginLoginTimeoutSeconds;

    IdleConnectionResiliency(SQLServerConnection connection) {
        this.connection = connection;
    }

    boolean isConnectionRecoveryNegotiated() {
        return this.connectionRecoveryNegotiated;
    }

    void setConnectionRecoveryNegotiated(boolean connectionRecoveryNegotiated) {
        this.connectionRecoveryNegotiated = connectionRecoveryNegotiated;
    }

    int getConnectRetryCount() {
        return this.connectRetryCount;
    }

    void setConnectRetryCount(int connectRetryCount) {
        this.connectRetryCount = connectRetryCount;
    }

    SQLServerConnection getConnection() {
        return this.connection;
    }

    void setConnection(SQLServerConnection connection) {
        this.connection = connection;
    }

    boolean isReconnectRunning() {
        return this.reconnectThread != null && this.reconnectThread.getState() != Thread.State.TERMINATED;
    }

    SessionStateTable getSessionStateTable() {
        return this.sessionStateTable;
    }

    void setSessionStateTable(SessionStateTable sessionStateTable) {
        this.sessionStateTable = sessionStateTable;
    }

    boolean isConnectionRecoveryPossible() {
        return this.connectionRecoveryPossible;
    }

    void setConnectionRecoveryPossible(boolean connectionRecoveryPossible) {
        this.connectionRecoveryPossible = connectionRecoveryPossible;
    }

    int getUnprocessedResponseCount() {
        return this.unprocessedResponseCount.get();
    }

    void resetUnprocessedResponseCount() {
        this.unprocessedResponseCount.set(0);
    }

    void parseInitialSessionStateData(byte[] data, byte[][] sessionStateInitial) {
        int sessionStateLength;
        for (int bytesRead = 0; bytesRead < data.length; bytesRead += sessionStateLength) {
            short sessionStateId = (short)(data[bytesRead] & 0xFF);
            int byteLength = data[++bytesRead] & 0xFF;
            ++bytesRead;
            if (byteLength == 255) {
                sessionStateLength = (int)((long)Util.readInt(data, bytesRead) & 0xFFFFFFFFL);
                bytesRead += 4;
            } else {
                sessionStateLength = byteLength;
            }
            sessionStateInitial[sessionStateId] = new byte[sessionStateLength];
            System.arraycopy(data, bytesRead, sessionStateInitial[sessionStateId], 0, sessionStateLength);
        }
    }

    void incrementUnprocessedResponseCount() {
        if (this.connection.getRetryCount() > 0 && !this.isReconnectRunning() && this.unprocessedResponseCount.incrementAndGet() < 0) {
            if (loggerExternal.isLoggable(Level.FINER)) {
                loggerExternal.finer("unprocessedResponseCount < 0 on increment. Disabling connection resiliency.");
            }
            this.setConnectionRecoveryPossible(false);
        }
    }

    void decrementUnprocessedResponseCount() {
        if (this.connection.getRetryCount() > 0 && !this.isReconnectRunning() && this.unprocessedResponseCount.decrementAndGet() < 0) {
            if (loggerExternal.isLoggable(Level.FINER)) {
                loggerExternal.finer("unprocessedResponseCount < 0 on decrement. Disabling connection resiliency.");
            }
            this.setConnectionRecoveryPossible(false);
        }
    }

    void setLoginParameters(String instanceValue, int nPort, FailoverInfo fo, int loginTimeoutSeconds) {
        this.loginInstanceValue = instanceValue;
        this.loginNPort = nPort;
        this.loginFailoverInfo = fo;
        this.loginLoginTimeoutSeconds = loginTimeoutSeconds;
    }

    String getInstanceValue() {
        return this.loginInstanceValue;
    }

    int getNPort() {
        return this.loginNPort;
    }

    FailoverInfo getFailoverInfo() {
        return this.loginFailoverInfo;
    }

    int getLoginTimeoutSeconds() {
        return this.loginLoginTimeoutSeconds;
    }

    void reconnect(TDSCommand cmd) throws InterruptedException {
        this.reconnectErrorReceived = null;
        this.reconnectThread = new ReconnectThread(this.connection, cmd);
        this.reconnectThread.start();
        this.reconnectThread.join();
        this.reconnectErrorReceived = this.reconnectThread.getException();
        this.reconnectThread = null;
    }

    SQLServerException getReconnectException() {
        return this.reconnectErrorReceived;
    }
}

