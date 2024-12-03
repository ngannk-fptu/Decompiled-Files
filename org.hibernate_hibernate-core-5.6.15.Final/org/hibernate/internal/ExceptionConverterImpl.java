/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.EntityExistsException
 *  javax.persistence.EntityNotFoundException
 *  javax.persistence.LockTimeoutException
 *  javax.persistence.NoResultException
 *  javax.persistence.NonUniqueResultException
 *  javax.persistence.OptimisticLockException
 *  javax.persistence.PersistenceException
 *  javax.persistence.PessimisticLockException
 *  javax.persistence.QueryTimeoutException
 *  javax.persistence.RollbackException
 */
package org.hibernate.internal;

import java.io.Serializable;
import java.sql.SQLException;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;
import org.hibernate.HibernateException;
import org.hibernate.JDBCException;
import org.hibernate.LockOptions;
import org.hibernate.NonUniqueObjectException;
import org.hibernate.NonUniqueResultException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.PessimisticLockException;
import org.hibernate.QueryException;
import org.hibernate.QueryTimeoutException;
import org.hibernate.StaleObjectStateException;
import org.hibernate.StaleStateException;
import org.hibernate.TransientObjectException;
import org.hibernate.UnresolvableObjectException;
import org.hibernate.dialect.lock.LockingStrategyException;
import org.hibernate.dialect.lock.OptimisticEntityLockException;
import org.hibernate.dialect.lock.PessimisticEntityLockException;
import org.hibernate.engine.spi.ExceptionConverter;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.exception.LockAcquisitionException;
import org.hibernate.exception.LockTimeoutException;
import org.hibernate.internal.EntityManagerMessageLogger;
import org.hibernate.internal.HEMLogging;
import org.hibernate.loader.MultipleBagFetchException;

public class ExceptionConverterImpl
implements ExceptionConverter {
    private static final EntityManagerMessageLogger log = HEMLogging.messageLogger(ExceptionConverterImpl.class);
    private final SharedSessionContractImplementor sharedSessionContract;
    private final boolean isJpaBootstrap;
    private final boolean nativeExceptionHandling51Compliance;

    public ExceptionConverterImpl(SharedSessionContractImplementor sharedSessionContract) {
        this.sharedSessionContract = sharedSessionContract;
        this.isJpaBootstrap = sharedSessionContract.getFactory().getSessionFactoryOptions().isJpaBootstrap();
        this.nativeExceptionHandling51Compliance = sharedSessionContract.getFactory().getSessionFactoryOptions().nativeExceptionHandling51Compliance();
    }

    @Override
    public RuntimeException convertCommitException(RuntimeException e) {
        if (this.isJpaBootstrap) {
            RuntimeException wrappedException;
            if (e instanceof HibernateException) {
                wrappedException = this.convert((HibernateException)((Object)e));
            } else if (e instanceof PersistenceException) {
                Throwable cause;
                Throwable throwable = cause = e.getCause() == null ? e : e.getCause();
                wrappedException = cause instanceof HibernateException ? this.convert((HibernateException)((Object)cause)) : cause;
            } else {
                wrappedException = e;
            }
            try {
                this.sharedSessionContract.getTransaction().rollback();
            }
            catch (Exception exception) {
                // empty catch block
            }
            return new RollbackException("Error while committing the transaction", (Throwable)wrappedException);
        }
        return e;
    }

    @Override
    public RuntimeException convert(HibernateException e, LockOptions lockOptions) {
        if (!this.nativeExceptionHandling51Compliance) {
            HibernateException cause = e;
            if (cause instanceof StaleStateException) {
                PersistenceException converted = this.wrapStaleStateException((StaleStateException)cause);
                this.handlePersistenceException(converted);
                return converted;
            }
            if (cause instanceof LockAcquisitionException) {
                PersistenceException converted = this.wrapLockException(cause, lockOptions);
                this.handlePersistenceException(converted);
                return converted;
            }
            if (cause instanceof LockingStrategyException) {
                PersistenceException converted = this.wrapLockException(cause, lockOptions);
                this.handlePersistenceException(converted);
                return converted;
            }
            if (cause instanceof PessimisticLockException) {
                PersistenceException converted = this.wrapLockException(cause, lockOptions);
                this.handlePersistenceException(converted);
                return converted;
            }
            if (cause instanceof QueryTimeoutException) {
                javax.persistence.QueryTimeoutException converted = new javax.persistence.QueryTimeoutException(((Throwable)((Object)cause)).getMessage(), (Throwable)((Object)cause));
                this.handlePersistenceException((PersistenceException)converted);
                return converted;
            }
            if (cause instanceof ObjectNotFoundException) {
                EntityNotFoundException converted = new EntityNotFoundException(((Throwable)((Object)cause)).getMessage());
                this.handlePersistenceException((PersistenceException)converted);
                return converted;
            }
            if (cause instanceof NonUniqueObjectException) {
                EntityExistsException converted = new EntityExistsException(((Throwable)((Object)cause)).getMessage());
                this.handlePersistenceException((PersistenceException)converted);
                return converted;
            }
            if (cause instanceof NonUniqueResultException) {
                javax.persistence.NonUniqueResultException converted = new javax.persistence.NonUniqueResultException(((Throwable)((Object)cause)).getMessage());
                this.handlePersistenceException((PersistenceException)converted);
                return converted;
            }
            if (cause instanceof UnresolvableObjectException) {
                EntityNotFoundException converted = new EntityNotFoundException(((Throwable)((Object)cause)).getMessage());
                this.handlePersistenceException((PersistenceException)converted);
                return converted;
            }
            if (cause instanceof QueryException) {
                return new IllegalArgumentException((Throwable)((Object)cause));
            }
            if (cause instanceof MultipleBagFetchException) {
                return new IllegalArgumentException((Throwable)((Object)cause));
            }
            if (cause instanceof TransientObjectException) {
                try {
                    this.sharedSessionContract.markForRollbackOnly();
                }
                catch (Exception ne) {
                    log.unableToMarkForRollbackOnTransientObjectException(ne);
                }
                return new IllegalStateException((Throwable)((Object)e));
            }
            PersistenceException converted = new PersistenceException((Throwable)((Object)cause));
            this.handlePersistenceException(converted);
            return converted;
        }
        if (e instanceof QueryException) {
            return e;
        }
        if (e instanceof MultipleBagFetchException) {
            return e;
        }
        try {
            this.sharedSessionContract.markForRollbackOnly();
        }
        catch (Exception ne) {
            log.unableToMarkForRollbackOnTransientObjectException(ne);
        }
        return e;
    }

    @Override
    public RuntimeException convert(HibernateException e) {
        return this.convert(e, null);
    }

    @Override
    public RuntimeException convert(RuntimeException e) {
        RuntimeException result = e;
        if (e instanceof HibernateException) {
            result = this.convert((HibernateException)((Object)e));
        } else {
            this.sharedSessionContract.markForRollbackOnly();
        }
        return result;
    }

    @Override
    public RuntimeException convert(RuntimeException e, LockOptions lockOptions) {
        RuntimeException result = e;
        if (e instanceof HibernateException) {
            result = this.convert((HibernateException)((Object)e), lockOptions);
        } else {
            this.sharedSessionContract.markForRollbackOnly();
        }
        return result;
    }

    @Override
    public JDBCException convert(SQLException e, String message) {
        return this.sharedSessionContract.getJdbcServices().getSqlExceptionHelper().convert(e, message);
    }

    protected PersistenceException wrapStaleStateException(StaleStateException e) {
        OptimisticLockException pe;
        block7: {
            if (e instanceof StaleObjectStateException) {
                StaleObjectStateException sose = (StaleObjectStateException)e;
                Serializable identifier = sose.getIdentifier();
                if (identifier != null) {
                    try {
                        Object entity = this.sharedSessionContract.internalLoad(sose.getEntityName(), identifier, false, true);
                        if (entity instanceof Serializable) {
                            pe = new OptimisticLockException(e.getMessage(), (Throwable)((Object)e), entity);
                            break block7;
                        }
                        pe = new OptimisticLockException(e.getMessage(), (Throwable)((Object)e));
                    }
                    catch (EntityNotFoundException enfe) {
                        pe = new OptimisticLockException(e.getMessage(), (Throwable)((Object)e));
                    }
                } else {
                    pe = new OptimisticLockException(e.getMessage(), (Throwable)((Object)e));
                }
            } else {
                pe = new OptimisticLockException(e.getMessage(), (Throwable)((Object)e));
            }
        }
        return pe;
    }

    protected PersistenceException wrapLockException(HibernateException e, LockOptions lockOptions) {
        Object pe;
        if (e instanceof OptimisticEntityLockException) {
            OptimisticEntityLockException lockException = (OptimisticEntityLockException)e;
            pe = new OptimisticLockException(lockException.getMessage(), (Throwable)((Object)lockException), lockException.getEntity());
        } else if (e instanceof LockTimeoutException) {
            pe = new javax.persistence.LockTimeoutException(e.getMessage(), (Throwable)((Object)e), null);
        } else if (e instanceof PessimisticEntityLockException) {
            PessimisticEntityLockException lockException = (PessimisticEntityLockException)e;
            pe = lockOptions != null && lockOptions.getTimeOut() > -1 ? new javax.persistence.LockTimeoutException(lockException.getMessage(), (Throwable)((Object)lockException), lockException.getEntity()) : new javax.persistence.PessimisticLockException(lockException.getMessage(), (Throwable)((Object)lockException), lockException.getEntity());
        } else if (e instanceof PessimisticLockException) {
            PessimisticLockException jdbcLockException = (PessimisticLockException)e;
            pe = lockOptions != null && lockOptions.getTimeOut() > -1 ? new javax.persistence.LockTimeoutException(jdbcLockException.getMessage(), (Throwable)((Object)jdbcLockException), null) : new javax.persistence.PessimisticLockException(jdbcLockException.getMessage(), (Throwable)((Object)jdbcLockException), null);
        } else {
            pe = new OptimisticLockException((Throwable)((Object)e));
        }
        return pe;
    }

    private void handlePersistenceException(PersistenceException e) {
        if (e instanceof NoResultException) {
            return;
        }
        if (e instanceof javax.persistence.NonUniqueResultException) {
            return;
        }
        if (e instanceof javax.persistence.LockTimeoutException) {
            return;
        }
        if (e instanceof javax.persistence.QueryTimeoutException) {
            return;
        }
        try {
            this.sharedSessionContract.markForRollbackOnly();
        }
        catch (Exception ne) {
            log.unableToMarkForRollbackOnPersistenceException(ne);
        }
    }
}

