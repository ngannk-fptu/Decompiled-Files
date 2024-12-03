/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerConnection;
import com.microsoft.sqlserver.jdbc.SQLServerDriverStringProperty;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.ServerPortPlaceHolder;
import java.text.MessageFormat;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

final class FailoverInfo {
    private String failoverPartner;
    private int portNumber;
    private String failoverInstance;
    private boolean setUpInfocalled;
    private final Lock lock = new ReentrantLock();
    private boolean useFailoverPartner;

    boolean getUseFailoverPartner() {
        return this.useFailoverPartner;
    }

    FailoverInfo(String failover, boolean actualFailoverPartner) {
        this.failoverPartner = failover;
        this.useFailoverPartner = actualFailoverPartner;
        this.portNumber = -1;
    }

    void log(SQLServerConnection con) {
        if (con.getConnectionLogger().isLoggable(Level.FINE)) {
            con.getConnectionLogger().fine(con.toString() + " Failover server :" + this.failoverPartner + " Failover partner is primary : " + this.useFailoverPartner);
        }
    }

    private void setupInfo(SQLServerConnection con) throws SQLServerException {
        if (this.setUpInfocalled) {
            return;
        }
        if (0 == this.failoverPartner.length()) {
            this.portNumber = SQLServerConnection.DEFAULTPORT;
        } else {
            int px = this.failoverPartner.indexOf(92);
            if (px >= 0) {
                if (con.getConnectionLogger().isLoggable(Level.FINE)) {
                    con.getConnectionLogger().fine(con.toString() + " Failover server :" + this.failoverPartner);
                }
                String instanceValue = this.failoverPartner.substring(px + 1, this.failoverPartner.length());
                this.failoverPartner = this.failoverPartner.substring(0, px);
                con.validateMaxSQLLoginName(SQLServerDriverStringProperty.INSTANCE_NAME.toString(), instanceValue);
                this.failoverInstance = instanceValue;
                String instancePort = con.getInstancePort(this.failoverPartner, instanceValue);
                try {
                    this.portNumber = Integer.parseInt(instancePort);
                }
                catch (NumberFormatException e) {
                    MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidPortNumber"));
                    Object[] msgArgs = new Object[]{instancePort};
                    SQLServerException.makeFromDriverError(con, null, form.format(msgArgs), null, false);
                }
            } else {
                this.portNumber = SQLServerConnection.DEFAULTPORT;
            }
        }
        this.setUpInfocalled = true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    ServerPortPlaceHolder failoverPermissionCheck(SQLServerConnection con, boolean link) throws SQLServerException {
        this.lock.lock();
        try {
            this.setupInfo(con);
            ServerPortPlaceHolder serverPortPlaceHolder = new ServerPortPlaceHolder(this.failoverPartner, this.portNumber, this.failoverInstance, link);
            return serverPortPlaceHolder;
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void failoverAdd(SQLServerConnection connection, boolean actualUseFailoverPartner, String actualFailoverPartner) {
        this.lock.lock();
        try {
            if (this.useFailoverPartner != actualUseFailoverPartner) {
                if (connection.getConnectionLogger().isLoggable(Level.FINE)) {
                    connection.getConnectionLogger().fine(connection.toString() + " Failover detected. failover partner=" + actualFailoverPartner);
                }
                this.useFailoverPartner = actualUseFailoverPartner;
            }
            if (!actualUseFailoverPartner && !this.failoverPartner.equals(actualFailoverPartner)) {
                this.failoverPartner = actualFailoverPartner;
                this.setUpInfocalled = false;
            }
        }
        finally {
            this.lock.unlock();
        }
    }
}

