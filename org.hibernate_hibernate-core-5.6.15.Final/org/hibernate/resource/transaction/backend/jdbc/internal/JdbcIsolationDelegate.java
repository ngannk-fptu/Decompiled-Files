/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.resource.transaction.backend.jdbc.internal;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Callable;
import org.hibernate.HibernateException;
import org.hibernate.engine.jdbc.connections.spi.JdbcConnectionAccess;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.hibernate.engine.transaction.spi.IsolationDelegate;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.jdbc.WorkExecutor;
import org.hibernate.jdbc.WorkExecutorVisitable;

public class JdbcIsolationDelegate
implements IsolationDelegate {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(JdbcIsolationDelegate.class);
    private final JdbcConnectionAccess connectionAccess;
    private final SqlExceptionHelper sqlExceptionHelper;

    public JdbcIsolationDelegate(JdbcConnectionAccess connectionAccess, SqlExceptionHelper sqlExceptionHelper) {
        this.connectionAccess = connectionAccess;
        this.sqlExceptionHelper = sqlExceptionHelper;
    }

    protected JdbcConnectionAccess jdbcConnectionAccess() {
        return this.connectionAccess;
    }

    protected SqlExceptionHelper sqlExceptionHelper() {
        return this.sqlExceptionHelper;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public <T> T delegateWork(WorkExecutorVisitable<T> work, boolean transacted) throws HibernateException {
        boolean wasAutoCommit = false;
        try {
            Connection connection = this.jdbcConnectionAccess().obtainConnection();
            try {
                if (transacted && connection.getAutoCommit()) {
                    wasAutoCommit = true;
                    connection.setAutoCommit(false);
                }
                Object result = work.accept(new WorkExecutor(), connection);
                if (transacted) {
                    connection.commit();
                }
                Object t = result;
                return t;
            }
            catch (Exception e) {
                try {
                    if (transacted && !connection.isClosed()) {
                        connection.rollback();
                    }
                }
                catch (Exception ignore) {
                    LOG.unableToRollbackConnection(ignore);
                }
                if (e instanceof HibernateException) {
                    throw (HibernateException)((Object)e);
                }
                if (!(e instanceof SQLException)) throw new HibernateException("error performing isolated work", e);
                throw this.sqlExceptionHelper().convert((SQLException)e, "error performing isolated work");
            }
            finally {
                if (transacted && wasAutoCommit) {
                    try {
                        connection.setAutoCommit(true);
                    }
                    catch (Exception ignore) {
                        LOG.trace("was unable to reset connection back to auto-commit");
                    }
                }
                try {
                    this.jdbcConnectionAccess().releaseConnection(connection);
                }
                catch (Exception ignore) {
                    LOG.unableToReleaseIsolatedConnection(ignore);
                }
            }
        }
        catch (SQLException sqle) {
            throw this.sqlExceptionHelper().convert(sqle, "unable to obtain isolated JDBC connection");
        }
    }

    @Override
    public <T> T delegateCallable(Callable<T> callable, boolean transacted) throws HibernateException {
        try {
            return callable.call();
        }
        catch (HibernateException e) {
            throw e;
        }
        catch (Exception e) {
            throw new HibernateException(e);
        }
    }
}

