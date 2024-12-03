/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.internal;

import java.sql.Connection;
import java.sql.SQLException;
import org.hibernate.engine.jdbc.connections.spi.JdbcConnectionAccess;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.resource.transaction.spi.DdlTransactionIsolator;
import org.hibernate.tool.schema.internal.exec.JdbcConnectionAccessProvidedConnectionImpl;
import org.hibernate.tool.schema.internal.exec.JdbcContext;
import org.hibernate.tool.schema.spi.SchemaManagementException;

class DdlTransactionIsolatorProvidedConnectionImpl
implements DdlTransactionIsolator {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(DdlTransactionIsolatorProvidedConnectionImpl.class);
    private final JdbcContext jdbcContext;

    public DdlTransactionIsolatorProvidedConnectionImpl(JdbcContext jdbcContext) {
        assert (jdbcContext.getJdbcConnectionAccess() instanceof JdbcConnectionAccessProvidedConnectionImpl);
        this.jdbcContext = jdbcContext;
    }

    @Override
    public JdbcContext getJdbcContext() {
        return this.jdbcContext;
    }

    @Override
    public Connection getIsolatedConnection() {
        try {
            return this.jdbcContext.getJdbcConnectionAccess().obtainConnection();
        }
        catch (SQLException e) {
            throw new SchemaManagementException("Error accessing user-provided Connection via JdbcConnectionAccessProvidedConnectionImpl", e);
        }
    }

    @Override
    public void prepare() {
    }

    @Override
    public void release() {
        JdbcConnectionAccess connectionAccess = this.jdbcContext.getJdbcConnectionAccess();
        if (!(connectionAccess instanceof JdbcConnectionAccessProvidedConnectionImpl)) {
            throw new IllegalStateException("DdlTransactionIsolatorProvidedConnectionImpl should always use a JdbcConnectionAccessProvidedConnectionImpl");
        }
        try {
            connectionAccess.releaseConnection(null);
        }
        catch (SQLException ignore) {
            LOG.unableToReleaseIsolatedConnection(ignore);
        }
    }
}

