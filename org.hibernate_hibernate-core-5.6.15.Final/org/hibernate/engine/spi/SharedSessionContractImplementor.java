/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.FlushModeType
 *  javax.persistence.TransactionRequiredException
 *  javax.persistence.criteria.Selection
 */
package org.hibernate.engine.spi;

import java.io.Serializable;
import java.sql.Connection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import javax.persistence.FlushModeType;
import javax.persistence.TransactionRequiredException;
import javax.persistence.criteria.Selection;
import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.ScrollMode;
import org.hibernate.SharedSessionContract;
import org.hibernate.Transaction;
import org.hibernate.cache.spi.CacheTransactionSynchronization;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.jdbc.LobCreationContext;
import org.hibernate.engine.jdbc.spi.JdbcCoordinator;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.query.spi.sql.NativeSQLQuerySpecification;
import org.hibernate.engine.spi.EntityKey;
import org.hibernate.engine.spi.ExceptionConverter;
import org.hibernate.engine.spi.LoadQueryInfluencers;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.SessionEventListenerManager;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.jpa.spi.HibernateEntityManagerImplementor;
import org.hibernate.loader.custom.CustomQuery;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.query.spi.QueryImplementor;
import org.hibernate.query.spi.QueryProducerImplementor;
import org.hibernate.query.spi.ScrollableResultsImplementor;
import org.hibernate.resource.jdbc.spi.JdbcSessionOwner;
import org.hibernate.resource.transaction.spi.TransactionCoordinatorBuilder;
import org.hibernate.type.descriptor.WrapperOptions;

public interface SharedSessionContractImplementor
extends SharedSessionContract,
JdbcSessionOwner,
TransactionCoordinatorBuilder.Options,
LobCreationContext,
WrapperOptions,
QueryProducerImplementor {
    @Override
    public SessionFactoryImplementor getFactory();

    public SessionEventListenerManager getEventListenerManager();

    public PersistenceContext getPersistenceContext();

    public JdbcCoordinator getJdbcCoordinator();

    public JdbcServices getJdbcServices();

    @Override
    public String getTenantIdentifier();

    public UUID getSessionIdentifier();

    public boolean isClosed();

    default public boolean isOpenOrWaitingForAutoClose() {
        return !this.isClosed();
    }

    default public void checkOpen() {
        this.checkOpen(true);
    }

    public void checkOpen(boolean var1);

    public void markForRollbackOnly();

    @Deprecated
    public long getTransactionStartTimestamp();

    @Deprecated
    default public long getTimestamp() {
        return this.getTransactionStartTimestamp();
    }

    public CacheTransactionSynchronization getCacheTransactionSynchronization();

    public boolean isTransactionInProgress();

    default public void checkTransactionNeededForUpdateOperation(String exceptionMessage) {
        if (!this.isTransactionInProgress()) {
            throw new TransactionRequiredException(exceptionMessage);
        }
    }

    public Transaction accessTransaction();

    public EntityKey generateEntityKey(Serializable var1, EntityPersister var2);

    public Interceptor getInterceptor();

    public void setAutoClear(boolean var1);

    public void initializeCollection(PersistentCollection var1, boolean var2) throws HibernateException;

    public Object internalLoad(String var1, Serializable var2, boolean var3, boolean var4) throws HibernateException;

    public Object immediateLoad(String var1, Serializable var2) throws HibernateException;

    public List list(String var1, QueryParameters var2) throws HibernateException;

    public Iterator iterate(String var1, QueryParameters var2) throws HibernateException;

    public ScrollableResultsImplementor scroll(String var1, QueryParameters var2) throws HibernateException;

    public ScrollableResultsImplementor scroll(Criteria var1, ScrollMode var2);

    public List list(Criteria var1);

    public List listFilter(Object var1, String var2, QueryParameters var3) throws HibernateException;

    public Iterator iterateFilter(Object var1, String var2, QueryParameters var3) throws HibernateException;

    public EntityPersister getEntityPersister(String var1, Object var2) throws HibernateException;

    public Object getEntityUsingInterceptor(EntityKey var1) throws HibernateException;

    public Serializable getContextEntityIdentifier(Object var1);

    public String bestGuessEntityName(Object var1);

    public String guessEntityName(Object var1) throws HibernateException;

    public Object instantiate(String var1, Serializable var2) throws HibernateException;

    public Object instantiate(EntityPersister var1, Serializable var2) throws HibernateException;

    public List listCustomQuery(CustomQuery var1, QueryParameters var2) throws HibernateException;

    public ScrollableResultsImplementor scrollCustomQuery(CustomQuery var1, QueryParameters var2) throws HibernateException;

    public List list(NativeSQLQuerySpecification var1, QueryParameters var2) throws HibernateException;

    public ScrollableResultsImplementor scroll(NativeSQLQuerySpecification var1, QueryParameters var2);

    public int getDontFlushFromFind();

    public int executeUpdate(String var1, QueryParameters var2) throws HibernateException;

    public int executeNativeUpdate(NativeSQLQuerySpecification var1, QueryParameters var2) throws HibernateException;

    @Override
    public CacheMode getCacheMode();

    public void setCacheMode(CacheMode var1);

    @Deprecated
    public void setFlushMode(FlushMode var1);

    public FlushModeType getFlushMode();

    public void setHibernateFlushMode(FlushMode var1);

    @Override
    public FlushMode getHibernateFlushMode();

    public Connection connection();

    public void flush();

    public boolean isEventSource();

    public void afterScrollOperation();

    public boolean shouldAutoClose();

    public boolean isAutoCloseSessionEnabled();

    default public boolean isQueryParametersValidationEnabled() {
        return this.getFactory().getSessionFactoryOptions().isQueryParametersValidationEnabled();
    }

    public LoadQueryInfluencers getLoadQueryInfluencers();

    public ExceptionConverter getExceptionConverter();

    default public Integer getConfiguredJdbcBatchSize() {
        Integer sessionJdbcBatchSize = this.getJdbcBatchSize();
        return sessionJdbcBatchSize == null ? ConfigurationHelper.getInt("hibernate.jdbc.batch_size", this.getFactory().getProperties(), 1) : sessionJdbcBatchSize;
    }

    @Deprecated
    public <T> QueryImplementor<T> createQuery(String var1, Class<T> var2, Selection var3, HibernateEntityManagerImplementor.QueryOptions var4);

    public PersistenceContext getPersistenceContextInternal();

    default public boolean isEnforcingFetchGraph() {
        return false;
    }

    default public void setEnforcingFetchGraph(boolean enforcingFetchGraph) {
    }
}

