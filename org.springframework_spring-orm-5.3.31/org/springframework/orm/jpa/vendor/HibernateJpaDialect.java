/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.EntityManager
 *  javax.persistence.PersistenceException
 *  org.hibernate.ConnectionReleaseMode
 *  org.hibernate.FlushMode
 *  org.hibernate.HibernateException
 *  org.hibernate.JDBCException
 *  org.hibernate.NonUniqueObjectException
 *  org.hibernate.NonUniqueResultException
 *  org.hibernate.ObjectDeletedException
 *  org.hibernate.PersistentObjectException
 *  org.hibernate.PessimisticLockException
 *  org.hibernate.PropertyValueException
 *  org.hibernate.QueryException
 *  org.hibernate.QueryTimeoutException
 *  org.hibernate.Session
 *  org.hibernate.StaleObjectStateException
 *  org.hibernate.StaleStateException
 *  org.hibernate.TransientObjectException
 *  org.hibernate.UnresolvableObjectException
 *  org.hibernate.WrongClassException
 *  org.hibernate.dialect.lock.OptimisticEntityLockException
 *  org.hibernate.dialect.lock.PessimisticEntityLockException
 *  org.hibernate.engine.spi.SessionImplementor
 *  org.hibernate.exception.ConstraintViolationException
 *  org.hibernate.exception.DataException
 *  org.hibernate.exception.JDBCConnectionException
 *  org.hibernate.exception.LockAcquisitionException
 *  org.hibernate.exception.SQLGrammarException
 *  org.springframework.dao.CannotAcquireLockException
 *  org.springframework.dao.DataAccessException
 *  org.springframework.dao.DataAccessResourceFailureException
 *  org.springframework.dao.DataIntegrityViolationException
 *  org.springframework.dao.DuplicateKeyException
 *  org.springframework.dao.IncorrectResultSizeDataAccessException
 *  org.springframework.dao.InvalidDataAccessApiUsageException
 *  org.springframework.dao.InvalidDataAccessResourceUsageException
 *  org.springframework.dao.PessimisticLockingFailureException
 *  org.springframework.dao.QueryTimeoutException
 *  org.springframework.jdbc.datasource.ConnectionHandle
 *  org.springframework.jdbc.datasource.DataSourceUtils
 *  org.springframework.jdbc.support.SQLExceptionTranslator
 *  org.springframework.lang.Nullable
 *  org.springframework.transaction.InvalidIsolationLevelException
 *  org.springframework.transaction.TransactionDefinition
 *  org.springframework.transaction.TransactionException
 *  org.springframework.transaction.support.ResourceTransactionDefinition
 */
package org.springframework.orm.jpa.vendor;

import java.sql.Connection;
import java.sql.SQLException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import org.hibernate.ConnectionReleaseMode;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.JDBCException;
import org.hibernate.NonUniqueObjectException;
import org.hibernate.NonUniqueResultException;
import org.hibernate.ObjectDeletedException;
import org.hibernate.PersistentObjectException;
import org.hibernate.PessimisticLockException;
import org.hibernate.PropertyValueException;
import org.hibernate.QueryException;
import org.hibernate.Session;
import org.hibernate.StaleObjectStateException;
import org.hibernate.StaleStateException;
import org.hibernate.TransientObjectException;
import org.hibernate.UnresolvableObjectException;
import org.hibernate.WrongClassException;
import org.hibernate.dialect.lock.OptimisticEntityLockException;
import org.hibernate.dialect.lock.PessimisticEntityLockException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.DataException;
import org.hibernate.exception.JDBCConnectionException;
import org.hibernate.exception.LockAcquisitionException;
import org.hibernate.exception.SQLGrammarException;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.jdbc.datasource.ConnectionHandle;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.lang.Nullable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.orm.jpa.DefaultJpaDialect;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.transaction.InvalidIsolationLevelException;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.ResourceTransactionDefinition;

public class HibernateJpaDialect
extends DefaultJpaDialect {
    boolean prepareConnection = true;
    @Nullable
    private SQLExceptionTranslator jdbcExceptionTranslator;

    public void setPrepareConnection(boolean prepareConnection) {
        this.prepareConnection = prepareConnection;
    }

    public void setJdbcExceptionTranslator(SQLExceptionTranslator jdbcExceptionTranslator) {
        this.jdbcExceptionTranslator = jdbcExceptionTranslator;
    }

    @Override
    public Object beginTransaction(EntityManager entityManager, TransactionDefinition definition) throws PersistenceException, SQLException, TransactionException {
        SessionImplementor session = this.getSession(entityManager);
        if (definition.getTimeout() != -1) {
            session.getTransaction().setTimeout(definition.getTimeout());
        }
        boolean isolationLevelNeeded = definition.getIsolationLevel() != -1;
        Integer previousIsolationLevel = null;
        Connection preparedCon = null;
        if (isolationLevelNeeded || definition.isReadOnly()) {
            if (this.prepareConnection && ConnectionReleaseMode.ON_CLOSE.equals((Object)session.getJdbcCoordinator().getLogicalConnection().getConnectionHandlingMode().getReleaseMode())) {
                preparedCon = session.connection();
                previousIsolationLevel = DataSourceUtils.prepareConnectionForTransaction((Connection)preparedCon, (TransactionDefinition)definition);
            } else if (isolationLevelNeeded) {
                throw new InvalidIsolationLevelException("HibernateJpaDialect is not allowed to support custom isolation levels: make sure that its 'prepareConnection' flag is on (the default) and that the Hibernate connection release mode is set to ON_CLOSE.");
            }
        }
        entityManager.getTransaction().begin();
        FlushMode previousFlushMode = this.prepareFlushMode((Session)session, definition.isReadOnly());
        if (definition instanceof ResourceTransactionDefinition && ((ResourceTransactionDefinition)definition).isLocalResource()) {
            previousFlushMode = null;
            if (definition.isReadOnly()) {
                session.setDefaultReadOnly(true);
            }
        }
        return new SessionTransactionData(session, previousFlushMode, preparedCon != null, previousIsolationLevel, definition.isReadOnly());
    }

    @Override
    public Object prepareTransaction(EntityManager entityManager, boolean readOnly, @Nullable String name) throws PersistenceException {
        SessionImplementor session = this.getSession(entityManager);
        FlushMode previousFlushMode = this.prepareFlushMode((Session)session, readOnly);
        return new SessionTransactionData(session, previousFlushMode, false, null, readOnly);
    }

    @Nullable
    protected FlushMode prepareFlushMode(Session session, boolean readOnly) throws PersistenceException {
        FlushMode flushMode = session.getHibernateFlushMode();
        if (readOnly) {
            if (!flushMode.equals((Object)FlushMode.MANUAL)) {
                session.setHibernateFlushMode(FlushMode.MANUAL);
                return flushMode;
            }
        } else if (flushMode.lessThan(FlushMode.COMMIT)) {
            session.setHibernateFlushMode(FlushMode.AUTO);
            return flushMode;
        }
        return null;
    }

    @Override
    public void cleanupTransaction(@Nullable Object transactionData) {
        if (transactionData instanceof SessionTransactionData) {
            ((SessionTransactionData)transactionData).resetSessionState();
        }
    }

    @Override
    public ConnectionHandle getJdbcConnection(EntityManager entityManager, boolean readOnly) throws PersistenceException, SQLException {
        SessionImplementor session = this.getSession(entityManager);
        return new HibernateConnectionHandle(session);
    }

    @Override
    @Nullable
    public DataAccessException translateExceptionIfPossible(RuntimeException ex) {
        if (ex instanceof HibernateException) {
            return this.convertHibernateAccessException((HibernateException)ex);
        }
        if (ex instanceof PersistenceException && ex.getCause() instanceof HibernateException) {
            return this.convertHibernateAccessException((HibernateException)ex.getCause());
        }
        return EntityManagerFactoryUtils.convertJpaAccessExceptionIfPossible(ex);
    }

    protected DataAccessException convertHibernateAccessException(HibernateException ex) {
        UnresolvableObjectException hibEx;
        JDBCException jdbcEx;
        if (this.jdbcExceptionTranslator != null && ex instanceof JDBCException) {
            jdbcEx = (JDBCException)ex;
            DataAccessException dae = this.jdbcExceptionTranslator.translate("Hibernate operation: " + jdbcEx.getMessage(), jdbcEx.getSQL(), jdbcEx.getSQLException());
            if (dae != null) {
                return dae;
            }
        }
        if (ex instanceof JDBCConnectionException) {
            return new DataAccessResourceFailureException(ex.getMessage(), (Throwable)ex);
        }
        if (ex instanceof SQLGrammarException) {
            jdbcEx = (SQLGrammarException)ex;
            return new InvalidDataAccessResourceUsageException(ex.getMessage() + "; SQL [" + jdbcEx.getSQL() + "]", (Throwable)ex);
        }
        if (ex instanceof org.hibernate.QueryTimeoutException) {
            jdbcEx = (org.hibernate.QueryTimeoutException)ex;
            return new QueryTimeoutException(ex.getMessage() + "; SQL [" + jdbcEx.getSQL() + "]", (Throwable)ex);
        }
        if (ex instanceof LockAcquisitionException) {
            jdbcEx = (LockAcquisitionException)ex;
            return new CannotAcquireLockException(ex.getMessage() + "; SQL [" + jdbcEx.getSQL() + "]", (Throwable)ex);
        }
        if (ex instanceof PessimisticLockException) {
            jdbcEx = (PessimisticLockException)ex;
            return new PessimisticLockingFailureException(ex.getMessage() + "; SQL [" + jdbcEx.getSQL() + "]", (Throwable)ex);
        }
        if (ex instanceof ConstraintViolationException) {
            jdbcEx = (ConstraintViolationException)ex;
            return new DataIntegrityViolationException(ex.getMessage() + "; SQL [" + jdbcEx.getSQL() + "]; constraint [" + jdbcEx.getConstraintName() + "]", (Throwable)ex);
        }
        if (ex instanceof DataException) {
            jdbcEx = (DataException)ex;
            return new DataIntegrityViolationException(ex.getMessage() + "; SQL [" + jdbcEx.getSQL() + "]", (Throwable)ex);
        }
        if (ex instanceof QueryException) {
            return new InvalidDataAccessResourceUsageException(ex.getMessage(), (Throwable)ex);
        }
        if (ex instanceof NonUniqueResultException) {
            return new IncorrectResultSizeDataAccessException(ex.getMessage(), 1, (Throwable)ex);
        }
        if (ex instanceof NonUniqueObjectException) {
            return new DuplicateKeyException(ex.getMessage(), (Throwable)ex);
        }
        if (ex instanceof PropertyValueException) {
            return new DataIntegrityViolationException(ex.getMessage(), (Throwable)ex);
        }
        if (ex instanceof PersistentObjectException) {
            return new InvalidDataAccessApiUsageException(ex.getMessage(), (Throwable)ex);
        }
        if (ex instanceof TransientObjectException) {
            return new InvalidDataAccessApiUsageException(ex.getMessage(), (Throwable)ex);
        }
        if (ex instanceof ObjectDeletedException) {
            return new InvalidDataAccessApiUsageException(ex.getMessage(), (Throwable)ex);
        }
        if (ex instanceof UnresolvableObjectException) {
            hibEx = (UnresolvableObjectException)ex;
            return new ObjectRetrievalFailureException(hibEx.getEntityName(), (Object)hibEx.getIdentifier(), ex.getMessage(), (Throwable)ex);
        }
        if (ex instanceof WrongClassException) {
            hibEx = (WrongClassException)ex;
            return new ObjectRetrievalFailureException(hibEx.getEntityName(), (Object)hibEx.getIdentifier(), ex.getMessage(), (Throwable)ex);
        }
        if (ex instanceof StaleObjectStateException) {
            hibEx = (StaleObjectStateException)ex;
            return new ObjectOptimisticLockingFailureException(hibEx.getEntityName(), (Object)hibEx.getIdentifier(), (Throwable)ex);
        }
        if (ex instanceof StaleStateException) {
            return new ObjectOptimisticLockingFailureException(ex.getMessage(), (Throwable)ex);
        }
        if (ex instanceof OptimisticEntityLockException) {
            return new ObjectOptimisticLockingFailureException(ex.getMessage(), (Throwable)ex);
        }
        if (ex instanceof PessimisticEntityLockException) {
            if (ex.getCause() instanceof LockAcquisitionException) {
                return new CannotAcquireLockException(ex.getMessage(), ex.getCause());
            }
            return new PessimisticLockingFailureException(ex.getMessage(), (Throwable)ex);
        }
        return new JpaSystemException((RuntimeException)ex);
    }

    protected SessionImplementor getSession(EntityManager entityManager) {
        return (SessionImplementor)entityManager.unwrap(SessionImplementor.class);
    }

    private static class HibernateConnectionHandle
    implements ConnectionHandle {
        private final SessionImplementor session;

        public HibernateConnectionHandle(SessionImplementor session) {
            this.session = session;
        }

        public Connection getConnection() {
            return this.session.connection();
        }
    }

    private static class SessionTransactionData {
        private final SessionImplementor session;
        @Nullable
        private final FlushMode previousFlushMode;
        private final boolean needsConnectionReset;
        @Nullable
        private final Integer previousIsolationLevel;
        private final boolean readOnly;

        public SessionTransactionData(SessionImplementor session, @Nullable FlushMode previousFlushMode, boolean connectionPrepared, @Nullable Integer previousIsolationLevel, boolean readOnly) {
            this.session = session;
            this.previousFlushMode = previousFlushMode;
            this.needsConnectionReset = connectionPrepared;
            this.previousIsolationLevel = previousIsolationLevel;
            this.readOnly = readOnly;
        }

        public void resetSessionState() {
            if (this.previousFlushMode != null) {
                this.session.setHibernateFlushMode(this.previousFlushMode);
            }
            if (this.needsConnectionReset && this.session.getJdbcCoordinator().getLogicalConnection().isPhysicallyConnected()) {
                Connection conToReset = this.session.connection();
                DataSourceUtils.resetConnectionAfterTransaction((Connection)conToReset, (Integer)this.previousIsolationLevel, (boolean)this.readOnly);
            }
        }
    }
}

