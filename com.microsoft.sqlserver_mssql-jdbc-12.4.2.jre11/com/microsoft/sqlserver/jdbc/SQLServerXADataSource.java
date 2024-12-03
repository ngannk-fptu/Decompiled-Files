/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerConnection;
import com.microsoft.sqlserver.jdbc.SQLServerConnectionPoolDataSource;
import com.microsoft.sqlserver.jdbc.SQLServerXAConnection;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Reference;
import javax.sql.XAConnection;
import javax.sql.XADataSource;

public final class SQLServerXADataSource
extends SQLServerConnectionPoolDataSource
implements XADataSource {
    static Logger xaLogger = Logger.getLogger("com.microsoft.sqlserver.jdbc.internals.XA");

    @Override
    public XAConnection getXAConnection(String user, String password) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "getXAConnection", new Object[]{user, "Password not traced"});
        }
        SQLServerXAConnection pooledXAConnection = new SQLServerXAConnection(this, user, password);
        if (xaLogger.isLoggable(Level.FINER)) {
            xaLogger.finer(this.toString() + " user:" + user + pooledXAConnection.toString());
        }
        if (xaLogger.isLoggable(Level.FINER)) {
            xaLogger.finer(this.toString() + " Start get physical connection.");
        }
        SQLServerConnection physicalConnection = pooledXAConnection.getPhysicalConnection();
        if (xaLogger.isLoggable(Level.FINE)) {
            xaLogger.fine(this.toString() + " End get physical connection, " + physicalConnection.toString());
        }
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.exiting(this.getClassNameLogging(), "getXAConnection", pooledXAConnection);
        }
        return pooledXAConnection;
    }

    @Override
    public XAConnection getXAConnection() throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "getXAConnection");
        }
        return this.getXAConnection(this.getUser(), this.getPassword());
    }

    @Override
    public Reference getReference() {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "getReference");
        }
        Reference ref = this.getReferenceInternal("com.microsoft.sqlserver.jdbc.SQLServerXADataSource");
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.exiting(this.getClassNameLogging(), "getReference", ref);
        }
        return ref;
    }

    private Object writeReplace() throws ObjectStreamException {
        return new SerializationProxy(this);
    }

    private void readObject(ObjectInputStream stream) throws InvalidObjectException {
        throw new InvalidObjectException("");
    }

    private static class SerializationProxy
    implements Serializable {
        private final Reference ref;
        private static final long serialVersionUID = 454661379842314126L;

        SerializationProxy(SQLServerXADataSource ds) {
            this.ref = ds.getReferenceInternal(null);
        }

        private Object readResolve() {
            SQLServerXADataSource ds = new SQLServerXADataSource();
            ds.initializeFromReference(this.ref);
            return ds;
        }
    }
}

