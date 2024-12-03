/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.loader.entity.plan;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import org.hibernate.LockOptions;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.loader.Loader;
import org.hibernate.loader.entity.UniqueEntityLoader;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.pretty.MessageHelper;
import org.hibernate.type.Type;
import org.jboss.logging.Logger;

public abstract class BatchingEntityLoader
implements UniqueEntityLoader {
    private static final Logger log = Logger.getLogger(BatchingEntityLoader.class);
    private final EntityPersister persister;

    public BatchingEntityLoader(EntityPersister persister) {
        this.persister = persister;
    }

    public EntityPersister persister() {
        return this.persister;
    }

    @Override
    public Object load(Serializable id, Object optionalObject, SharedSessionContractImplementor session) {
        return this.load(id, optionalObject, session, LockOptions.NONE, null);
    }

    protected QueryParameters buildQueryParameters(Serializable id, Serializable[] ids, Object optionalObject, LockOptions lockOptions) {
        Object[] types = new Type[ids.length];
        Arrays.fill(types, this.persister().getIdentifierType());
        QueryParameters qp = new QueryParameters();
        qp.setPositionalParameterTypes((Type[])types);
        qp.setPositionalParameterValues(ids);
        qp.setOptionalObject(optionalObject);
        qp.setOptionalEntityName(this.persister().getEntityName());
        qp.setOptionalId(id);
        qp.setLockOptions(lockOptions);
        return qp;
    }

    protected Object getObjectFromList(List results, Serializable id, SharedSessionContractImplementor session) {
        for (Object obj : results) {
            boolean equal = this.persister.getIdentifierType().isEqual(id, session.getContextEntityIdentifier(obj), session.getFactory());
            if (!equal) continue;
            return obj;
        }
        return null;
    }

    protected Object doBatchLoad(Serializable id, Loader loaderToUse, SharedSessionContractImplementor session, Serializable[] ids, Object optionalObject, LockOptions lockOptions) {
        if (log.isDebugEnabled()) {
            log.debugf("Batch loading entity: %s", (Object)MessageHelper.infoString(this.persister, ids, session.getFactory()));
        }
        QueryParameters qp = this.buildQueryParameters(id, ids, optionalObject, lockOptions);
        try {
            List results = loaderToUse.doQueryAndInitializeNonLazyCollections(session, qp, false);
            log.debug((Object)"Done entity batch load");
            return this.getObjectFromList(results, id, session);
        }
        catch (SQLException sqle) {
            throw session.getJdbcServices().getSqlExceptionHelper().convert(sqle, "could not load an entity batch: " + MessageHelper.infoString(this.persister(), ids, session.getFactory()), loaderToUse.getSQLString());
        }
    }
}

