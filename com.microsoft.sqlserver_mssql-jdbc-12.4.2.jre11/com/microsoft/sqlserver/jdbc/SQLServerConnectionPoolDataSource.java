/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import com.microsoft.sqlserver.jdbc.SQLServerPooledConnection;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.logging.Level;
import javax.naming.Reference;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.PooledConnection;

public class SQLServerConnectionPoolDataSource
extends SQLServerDataSource
implements ConnectionPoolDataSource {
    @Override
    public PooledConnection getPooledConnection() throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "getPooledConnection");
        }
        PooledConnection pcon = this.getPooledConnection(this.getUser(), this.getPassword());
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.exiting(this.getClassNameLogging(), "getPooledConnection", pcon);
        }
        return pcon;
    }

    @Override
    public PooledConnection getPooledConnection(String user, String password) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "getPooledConnection", new Object[]{user, "Password not traced"});
        }
        SQLServerPooledConnection pc = new SQLServerPooledConnection(this, user, password);
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.exiting(this.getClassNameLogging(), "getPooledConnection", pc);
        }
        return pc;
    }

    @Override
    public Reference getReference() {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "getReference");
        }
        Reference ref = this.getReferenceInternal("com.microsoft.sqlserver.jdbc.SQLServerConnectionPoolDataSource");
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
        private static final long serialVersionUID = 654661379842314126L;

        SerializationProxy(SQLServerConnectionPoolDataSource ds) {
            this.ref = ds.getReferenceInternal(null);
        }

        private Object readResolve() {
            SQLServerConnectionPoolDataSource ds = new SQLServerConnectionPoolDataSource();
            ds.initializeFromReference(this.ref);
            return ds;
        }
    }
}

