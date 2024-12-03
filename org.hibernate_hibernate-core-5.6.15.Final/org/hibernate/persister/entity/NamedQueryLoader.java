/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.persister.entity;

import java.io.Serializable;
import org.hibernate.FlushMode;
import org.hibernate.LockOptions;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.loader.entity.UniqueEntityLoader;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.query.internal.AbstractProducedQuery;

public final class NamedQueryLoader
implements UniqueEntityLoader {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(NamedQueryLoader.class);
    private final String queryName;
    private final EntityPersister persister;
    private final int position;

    public NamedQueryLoader(String queryName, EntityPersister persister) {
        this.queryName = queryName;
        this.persister = persister;
        this.position = persister.getFactory().getSessionFactoryOptions().jdbcStyleParamsZeroBased() ? 0 : 1;
    }

    @Override
    public Object load(Serializable id, Object optionalObject, SharedSessionContractImplementor session, LockOptions lockOptions) {
        return this.load(id, optionalObject, session, (Boolean)null);
    }

    @Override
    public Object load(Serializable id, Object optionalObject, SharedSessionContractImplementor session, LockOptions lockOptions, Boolean readOnly) {
        if (lockOptions != null) {
            LOG.debug("Ignoring lock-options passed to named query loader");
        }
        return this.load(id, optionalObject, session, readOnly);
    }

    @Override
    public Object load(Serializable id, Object optionalObject, SharedSessionContractImplementor session) {
        return this.load(id, optionalObject, session, (Boolean)null);
    }

    @Override
    public Object load(Serializable id, Object optionalObject, SharedSessionContractImplementor session, Boolean readOnly) {
        LOG.debugf("Loading entity: %s using named query: %s", this.persister.getEntityName(), this.queryName);
        AbstractProducedQuery query = (AbstractProducedQuery)session.getNamedQuery(this.queryName);
        if (query.getParameterMetadata().hasNamedParameters()) {
            query.setParameter(query.getNamedParameters()[0], (Object)id, this.persister.getIdentifierType());
        } else {
            query.setParameter(this.position, (Object)id, this.persister.getIdentifierType());
        }
        query.setOptionalId(id);
        query.setOptionalEntityName(this.persister.getEntityName());
        query.setOptionalObject(optionalObject);
        query.setFlushMode(FlushMode.MANUAL);
        if (readOnly != null) {
            query.setReadOnly(readOnly);
        }
        query.list();
        return session.getPersistenceContextInternal().getEntity(session.generateEntityKey(id, this.persister));
    }
}

