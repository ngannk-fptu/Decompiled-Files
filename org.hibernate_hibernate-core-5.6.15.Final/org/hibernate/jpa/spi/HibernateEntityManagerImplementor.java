/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.LockModeType
 *  javax.persistence.criteria.Selection
 */
package org.hibernate.jpa.spi;

import java.util.List;
import java.util.Map;
import javax.persistence.LockModeType;
import javax.persistence.criteria.Selection;
import org.hibernate.LockOptions;
import org.hibernate.ejb.HibernateEntityManager;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.jpa.spi.HibernateEntityManagerFactoryAware;
import org.hibernate.query.Query;
import org.hibernate.query.criteria.internal.ValueHandlerFactory;
import org.hibernate.type.Type;

@Deprecated
public interface HibernateEntityManagerImplementor
extends HibernateEntityManager,
HibernateEntityManagerFactoryAware {
    @Override
    public SessionImplementor getSession();

    public void checkOpen(boolean var1) throws IllegalStateException;

    public boolean isTransactionInProgress();

    public void markForRollbackOnly();

    @Deprecated
    public LockOptions getLockRequest(LockModeType var1, Map<String, Object> var2);

    default public LockOptions buildLockOptions(LockModeType lockModeType, Map<String, Object> properties) {
        return this.getLockRequest(lockModeType, properties);
    }

    @Deprecated
    public <T> Query<T> createQuery(String var1, Class<T> var2, Selection var3, QueryOptions var4);

    public static interface QueryOptions {
        public ResultMetadataValidator getResultMetadataValidator();

        public List<ValueHandlerFactory.ValueHandler> getValueHandlers();

        public Map<String, Class> getNamedParameterExplicitTypes();

        public static interface ResultMetadataValidator {
            public void validate(Type[] var1);
        }
    }
}

