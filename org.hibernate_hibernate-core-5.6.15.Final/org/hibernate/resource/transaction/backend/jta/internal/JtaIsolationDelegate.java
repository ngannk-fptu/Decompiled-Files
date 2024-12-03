/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.transaction.NotSupportedException
 *  javax.transaction.SystemException
 *  javax.transaction.Transaction
 *  javax.transaction.TransactionManager
 */
package org.hibernate.resource.transaction.backend.jta.internal;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Callable;
import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import org.hibernate.HibernateException;
import org.hibernate.engine.jdbc.connections.spi.JdbcConnectionAccess;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.hibernate.engine.transaction.spi.IsolationDelegate;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.jdbc.WorkExecutor;
import org.hibernate.jdbc.WorkExecutorVisitable;

public class JtaIsolationDelegate
implements IsolationDelegate {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(JtaIsolationDelegate.class);
    private final JdbcConnectionAccess connectionAccess;
    private final SqlExceptionHelper sqlExceptionHelper;
    private final TransactionManager transactionManager;

    public JtaIsolationDelegate(JdbcConnectionAccess connectionAccess, SqlExceptionHelper sqlExceptionHelper, TransactionManager transactionManager) {
        this.connectionAccess = connectionAccess;
        this.sqlExceptionHelper = sqlExceptionHelper;
        this.transactionManager = transactionManager;
    }

    protected JdbcConnectionAccess jdbcConnectionAccess() {
        return this.connectionAccess;
    }

    protected SqlExceptionHelper sqlExceptionHelper() {
        return this.sqlExceptionHelper;
    }

    @Override
    public <T> T delegateWork(final WorkExecutorVisitable<T> work, final boolean transacted) throws HibernateException {
        return this.doInSuspendedTransaction(new HibernateCallable<T>(){

            @Override
            public T call() throws HibernateException {
                HibernateCallable workCallable = new HibernateCallable<T>(){

                    @Override
                    public T call() throws HibernateException {
                        return JtaIsolationDelegate.this.doTheWork(work);
                    }
                };
                if (transacted) {
                    return JtaIsolationDelegate.this.doInNewTransaction(workCallable, JtaIsolationDelegate.this.transactionManager);
                }
                return workCallable.call();
            }
        });
    }

    @Override
    public <T> T delegateCallable(final Callable<T> callable, final boolean transacted) throws HibernateException {
        return this.doInSuspendedTransaction(new HibernateCallable<T>(){

            @Override
            public T call() throws HibernateException {
                HibernateCallable workCallable = new HibernateCallable<T>(){

                    @Override
                    public T call() throws HibernateException {
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
                };
                if (transacted) {
                    return JtaIsolationDelegate.this.doInNewTransaction(workCallable, JtaIsolationDelegate.this.transactionManager);
                }
                return workCallable.call();
            }
        });
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private <T> T doInSuspendedTransaction(HibernateCallable<T> callable) {
        try {
            Transaction surroundingTransaction = this.transactionManager.suspend();
            LOG.debugf("Surrounding JTA transaction suspended [%s]", surroundingTransaction);
            boolean hadProblems = false;
            try {
                T t = callable.call();
                return t;
            }
            catch (HibernateException e) {
                hadProblems = true;
                throw e;
            }
            finally {
                block11: {
                    try {
                        this.transactionManager.resume(surroundingTransaction);
                        LOG.debugf("Surrounding JTA transaction resumed [%s]", surroundingTransaction);
                    }
                    catch (Throwable t) {
                        if (hadProblems) break block11;
                        throw new HibernateException("Unable to resume previously suspended transaction", t);
                    }
                }
            }
        }
        catch (SystemException e) {
            throw new HibernateException("Unable to suspend current JTA transaction", e);
        }
    }

    private <T> T doInNewTransaction(HibernateCallable<T> callable, TransactionManager transactionManager) {
        try {
            transactionManager.begin();
            try {
                T result = callable.call();
                transactionManager.commit();
                return result;
            }
            catch (Exception e) {
                try {
                    transactionManager.rollback();
                }
                catch (Exception ignore) {
                    LOG.unableToRollbackIsolatedTransaction(e, ignore);
                }
                throw new HibernateException("Could not apply work", e);
            }
        }
        catch (SystemException e) {
            throw new HibernateException("Unable to start isolated transaction", e);
        }
        catch (NotSupportedException e) {
            throw new HibernateException("Unable to start isolated transaction", e);
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private <T> T doTheWork(WorkExecutorVisitable<T> work) {
        try {
            Connection connection = this.jdbcConnectionAccess().obtainConnection();
            try {
                Object t = work.accept(new WorkExecutor(), connection);
                return t;
            }
            catch (HibernateException e) {
                throw e;
            }
            catch (Exception e) {
                throw new HibernateException("Unable to perform isolated work", e);
            }
            finally {
                try {
                    this.jdbcConnectionAccess().releaseConnection(connection);
                }
                catch (Throwable ignore) {
                    LOG.unableToReleaseIsolatedConnection(ignore);
                }
            }
        }
        catch (SQLException e) {
            throw this.sqlExceptionHelper().convert(e, "unable to obtain isolated JDBC connection");
        }
    }

    private static interface HibernateCallable<T> {
        public T call() throws HibernateException;
    }
}

