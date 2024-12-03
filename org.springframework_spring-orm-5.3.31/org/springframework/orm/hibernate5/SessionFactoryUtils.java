/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.PersistenceException
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
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
 *  org.hibernate.SessionFactory
 *  org.hibernate.StaleObjectStateException
 *  org.hibernate.StaleStateException
 *  org.hibernate.TransientObjectException
 *  org.hibernate.UnresolvableObjectException
 *  org.hibernate.WrongClassException
 *  org.hibernate.dialect.lock.OptimisticEntityLockException
 *  org.hibernate.dialect.lock.PessimisticEntityLockException
 *  org.hibernate.engine.jdbc.connections.spi.ConnectionProvider
 *  org.hibernate.engine.spi.SessionFactoryImplementor
 *  org.hibernate.exception.ConstraintViolationException
 *  org.hibernate.exception.DataException
 *  org.hibernate.exception.JDBCConnectionException
 *  org.hibernate.exception.LockAcquisitionException
 *  org.hibernate.exception.SQLGrammarException
 *  org.hibernate.service.UnknownServiceException
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
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.ReflectionUtils
 */
package org.springframework.orm.hibernate5;

import java.lang.reflect.Method;
import java.util.Map;
import javax.persistence.PersistenceException;
import javax.sql.DataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import org.hibernate.SessionFactory;
import org.hibernate.StaleObjectStateException;
import org.hibernate.StaleStateException;
import org.hibernate.TransientObjectException;
import org.hibernate.UnresolvableObjectException;
import org.hibernate.WrongClassException;
import org.hibernate.dialect.lock.OptimisticEntityLockException;
import org.hibernate.dialect.lock.PessimisticEntityLockException;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.DataException;
import org.hibernate.exception.JDBCConnectionException;
import org.hibernate.exception.LockAcquisitionException;
import org.hibernate.exception.SQLGrammarException;
import org.hibernate.service.UnknownServiceException;
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
import org.springframework.lang.Nullable;
import org.springframework.orm.hibernate5.HibernateJdbcException;
import org.springframework.orm.hibernate5.HibernateObjectRetrievalFailureException;
import org.springframework.orm.hibernate5.HibernateOptimisticLockingFailureException;
import org.springframework.orm.hibernate5.HibernateQueryException;
import org.springframework.orm.hibernate5.HibernateSystemException;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

public abstract class SessionFactoryUtils {
    public static final int SESSION_SYNCHRONIZATION_ORDER = 900;
    static final Log logger = LogFactory.getLog(SessionFactoryUtils.class);

    static void flush(Session session, boolean synch) throws DataAccessException {
        if (synch) {
            logger.debug((Object)"Flushing Hibernate Session on transaction synchronization");
        } else {
            logger.debug((Object)"Flushing Hibernate Session on explicit request");
        }
        try {
            session.flush();
        }
        catch (HibernateException ex) {
            throw SessionFactoryUtils.convertHibernateAccessException(ex);
        }
        catch (PersistenceException ex) {
            if (ex.getCause() instanceof HibernateException) {
                throw SessionFactoryUtils.convertHibernateAccessException((HibernateException)ex.getCause());
            }
            throw ex;
        }
    }

    public static void closeSession(@Nullable Session session) {
        if (session != null) {
            try {
                if (session.isOpen()) {
                    session.close();
                }
            }
            catch (Throwable ex) {
                logger.error((Object)"Failed to release Hibernate Session", ex);
            }
        }
    }

    @Nullable
    public static DataSource getDataSource(SessionFactory sessionFactory) {
        block5: {
            Object dataSourceValue;
            Map props;
            Method getProperties = ClassUtils.getMethodIfAvailable(sessionFactory.getClass(), (String)"getProperties", (Class[])new Class[0]);
            if (getProperties != null && (props = (Map)ReflectionUtils.invokeMethod((Method)getProperties, (Object)sessionFactory)) != null && (dataSourceValue = props.get("hibernate.connection.datasource")) instanceof DataSource) {
                return (DataSource)dataSourceValue;
            }
            if (sessionFactory instanceof SessionFactoryImplementor) {
                SessionFactoryImplementor sfi = (SessionFactoryImplementor)sessionFactory;
                try {
                    ConnectionProvider cp = (ConnectionProvider)sfi.getServiceRegistry().getService(ConnectionProvider.class);
                    if (cp != null) {
                        return (DataSource)cp.unwrap(DataSource.class);
                    }
                }
                catch (UnknownServiceException ex) {
                    if (!logger.isDebugEnabled()) break block5;
                    logger.debug((Object)("No ConnectionProvider found - cannot determine DataSource for SessionFactory: " + (Object)((Object)ex)));
                }
            }
        }
        return null;
    }

    public static DataAccessException convertHibernateAccessException(HibernateException ex) {
        if (ex instanceof JDBCConnectionException) {
            return new DataAccessResourceFailureException(ex.getMessage(), (Throwable)ex);
        }
        if (ex instanceof SQLGrammarException) {
            SQLGrammarException jdbcEx = (SQLGrammarException)ex;
            return new InvalidDataAccessResourceUsageException(ex.getMessage() + "; SQL [" + jdbcEx.getSQL() + "]", (Throwable)ex);
        }
        if (ex instanceof org.hibernate.QueryTimeoutException) {
            org.hibernate.QueryTimeoutException jdbcEx = (org.hibernate.QueryTimeoutException)ex;
            return new QueryTimeoutException(ex.getMessage() + "; SQL [" + jdbcEx.getSQL() + "]", (Throwable)ex);
        }
        if (ex instanceof LockAcquisitionException) {
            LockAcquisitionException jdbcEx = (LockAcquisitionException)ex;
            return new CannotAcquireLockException(ex.getMessage() + "; SQL [" + jdbcEx.getSQL() + "]", (Throwable)ex);
        }
        if (ex instanceof PessimisticLockException) {
            PessimisticLockException jdbcEx = (PessimisticLockException)ex;
            return new PessimisticLockingFailureException(ex.getMessage() + "; SQL [" + jdbcEx.getSQL() + "]", (Throwable)ex);
        }
        if (ex instanceof ConstraintViolationException) {
            ConstraintViolationException jdbcEx = (ConstraintViolationException)ex;
            return new DataIntegrityViolationException(ex.getMessage() + "; SQL [" + jdbcEx.getSQL() + "]; constraint [" + jdbcEx.getConstraintName() + "]", (Throwable)ex);
        }
        if (ex instanceof DataException) {
            DataException jdbcEx = (DataException)ex;
            return new DataIntegrityViolationException(ex.getMessage() + "; SQL [" + jdbcEx.getSQL() + "]", (Throwable)ex);
        }
        if (ex instanceof JDBCException) {
            return new HibernateJdbcException((JDBCException)ex);
        }
        if (ex instanceof QueryException) {
            return new HibernateQueryException((QueryException)ex);
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
            return new HibernateObjectRetrievalFailureException((UnresolvableObjectException)ex);
        }
        if (ex instanceof WrongClassException) {
            return new HibernateObjectRetrievalFailureException((WrongClassException)ex);
        }
        if (ex instanceof StaleObjectStateException) {
            return new HibernateOptimisticLockingFailureException((StaleObjectStateException)ex);
        }
        if (ex instanceof StaleStateException) {
            return new HibernateOptimisticLockingFailureException((StaleStateException)ex);
        }
        if (ex instanceof OptimisticEntityLockException) {
            return new HibernateOptimisticLockingFailureException((OptimisticEntityLockException)ex);
        }
        if (ex instanceof PessimisticEntityLockException) {
            if (ex.getCause() instanceof LockAcquisitionException) {
                return new CannotAcquireLockException(ex.getMessage(), ex.getCause());
            }
            return new PessimisticLockingFailureException(ex.getMessage(), (Throwable)ex);
        }
        return new HibernateSystemException(ex);
    }
}

