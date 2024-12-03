/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.transaction.SystemException
 *  javax.transaction.Transaction
 *  javax.transaction.TransactionManager
 *  org.jboss.logging.Logger
 */
package org.hibernate.resource.transaction.backend.jta.internal;

import java.sql.Connection;
import java.sql.SQLException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import org.hibernate.HibernateException;
import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatform;
import org.hibernate.resource.transaction.spi.DdlTransactionIsolator;
import org.hibernate.tool.schema.internal.exec.JdbcContext;
import org.jboss.logging.Logger;

public class DdlTransactionIsolatorJtaImpl
implements DdlTransactionIsolator {
    private static final Logger log = Logger.getLogger(DdlTransactionIsolatorJtaImpl.class);
    private final JdbcContext jdbcContext;
    private final Transaction suspendedTransaction;
    private final Connection jdbcConnection;

    public DdlTransactionIsolatorJtaImpl(JdbcContext jdbcContext) {
        this.jdbcContext = jdbcContext;
        try {
            JtaPlatform jtaPlatform = jdbcContext.getServiceRegistry().getService(JtaPlatform.class);
            log.tracef("DdlTransactionIsolatorJtaImpl#prepare: JtaPlatform -> %s", (Object)jtaPlatform);
            TransactionManager tm = jtaPlatform.retrieveTransactionManager();
            if (tm == null) {
                throw new HibernateException("DdlTransactionIsolatorJtaImpl could not locate TransactionManager to suspend any current transaction; base JtaPlatform impl (" + jtaPlatform.toString() + ")?");
            }
            log.tracef("DdlTransactionIsolatorJtaImpl#prepare: TransactionManager -> %s", (Object)tm);
            this.suspendedTransaction = tm.suspend();
            log.tracef("DdlTransactionIsolatorJtaImpl#prepare: suspended Transaction -> %s", (Object)this.suspendedTransaction);
        }
        catch (SystemException e) {
            throw new HibernateException("Unable to suspend current JTA transaction in preparation for DDL execution");
        }
        try {
            this.jdbcConnection = jdbcContext.getJdbcConnectionAccess().obtainConnection();
        }
        catch (SQLException e) {
            throw jdbcContext.getSqlExceptionHelper().convert(e, "Unable to open JDBC Connection for DDL execution");
        }
        try {
            this.jdbcConnection.setAutoCommit(true);
        }
        catch (SQLException e) {
            throw jdbcContext.getSqlExceptionHelper().convert(e, "Unable set JDBC Connection for DDL execution to autocommit");
        }
    }

    @Override
    public JdbcContext getJdbcContext() {
        return this.jdbcContext;
    }

    @Override
    public void prepare() {
    }

    @Override
    public Connection getIsolatedConnection() {
        return this.jdbcConnection;
    }

    @Override
    public void release() {
        if (this.jdbcConnection != null) {
            try {
                this.jdbcContext.getJdbcConnectionAccess().releaseConnection(this.jdbcConnection);
            }
            catch (SQLException e) {
                throw this.jdbcContext.getSqlExceptionHelper().convert(e, "Unable to release JDBC Connection used for DDL execution");
            }
        }
        if (this.suspendedTransaction != null) {
            try {
                this.jdbcContext.getServiceRegistry().getService(JtaPlatform.class).retrieveTransactionManager().resume(this.suspendedTransaction);
            }
            catch (Exception e) {
                throw new HibernateException("Unable to resume JTA transaction after DDL execution");
            }
        }
    }
}

