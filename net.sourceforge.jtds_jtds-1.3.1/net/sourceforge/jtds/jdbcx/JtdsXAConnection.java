/*
 * Decompiled with CFR 0.152.
 */
package net.sourceforge.jtds.jdbcx;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.XAConnection;
import javax.transaction.xa.XAResource;
import net.sourceforge.jtds.jdbc.XASupport;
import net.sourceforge.jtds.jdbcx.JtdsDataSource;
import net.sourceforge.jtds.jdbcx.JtdsXAResource;
import net.sourceforge.jtds.jdbcx.PooledConnection;

public class JtdsXAConnection
extends PooledConnection
implements XAConnection {
    private final XAResource resource;
    private final JtdsDataSource dataSource;
    private final int xaConnectionId;

    public JtdsXAConnection(JtdsDataSource dataSource, Connection connection) throws SQLException {
        super(connection);
        this.resource = new JtdsXAResource(this, connection);
        this.dataSource = dataSource;
        this.xaConnectionId = XASupport.xa_open(connection);
    }

    int getXAConnectionID() {
        return this.xaConnectionId;
    }

    @Override
    public XAResource getXAResource() throws SQLException {
        return this.resource;
    }

    @Override
    public synchronized void close() throws SQLException {
        try {
            XASupport.xa_close(this.connection, this.xaConnectionId);
        }
        catch (SQLException sQLException) {
            // empty catch block
        }
        super.close();
    }

    protected JtdsDataSource getXADataSource() {
        return this.dataSource;
    }
}

