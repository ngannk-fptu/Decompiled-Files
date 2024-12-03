/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.loader.entity.plan;

import java.io.Serializable;
import java.util.List;
import org.hibernate.LockOptions;
import org.hibernate.engine.internal.BatchFetchQueueHelper;
import org.hibernate.engine.spi.LoadQueryInfluencers;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.internal.util.collections.ArrayHelper;
import org.hibernate.loader.entity.plan.BatchingEntityLoader;
import org.hibernate.loader.entity.plan.EntityLoader;
import org.hibernate.persister.entity.OuterJoinLoadable;
import org.hibernate.pretty.MessageHelper;
import org.jboss.logging.Logger;

public class DynamicBatchingEntityLoader
extends BatchingEntityLoader {
    private static final Logger log = Logger.getLogger(DynamicBatchingEntityLoader.class);
    private final int maxBatchSize;
    private final LoadQueryInfluencers loadQueryInfluencers;

    public DynamicBatchingEntityLoader(OuterJoinLoadable persister, int maxBatchSize, LockOptions lockOptions, SessionFactoryImplementor factory, LoadQueryInfluencers loadQueryInfluencers) {
        super(persister);
        this.maxBatchSize = maxBatchSize;
        this.loadQueryInfluencers = loadQueryInfluencers;
    }

    @Override
    public Object load(Serializable id, Object optionalObject, SharedSessionContractImplementor session, LockOptions lockOptions) {
        return this.load(id, optionalObject, session, lockOptions, null);
    }

    @Override
    public Object load(Serializable id, Object optionalObject, SharedSessionContractImplementor session, LockOptions lockOptions, Boolean readOnly) {
        Serializable[] batch = session.getPersistenceContextInternal().getBatchFetchQueue().getEntityBatch(this.persister(), id, this.maxBatchSize, this.persister().getEntityMode());
        int numberOfIds = ArrayHelper.countNonNull(batch);
        Serializable[] idsToLoad = new Serializable[numberOfIds];
        System.arraycopy(batch, 0, idsToLoad, 0, numberOfIds);
        if (log.isDebugEnabled()) {
            log.debugf("Batch loading entity: %s", (Object)MessageHelper.infoString(this.persister(), idsToLoad, session.getFactory()));
        }
        EntityLoader dynamicLoader = EntityLoader.forEntity((OuterJoinLoadable)this.persister()).withInfluencers(this.loadQueryInfluencers).withLockOptions(lockOptions).withBatchSize(idsToLoad.length).byPrimaryKey();
        List results = dynamicLoader.loadEntityBatch(session, idsToLoad, this.persister().getIdentifierType(), optionalObject, this.persister().getEntityName(), id, this.persister(), lockOptions, readOnly);
        BatchFetchQueueHelper.removeNotFoundBatchLoadableEntityKeys(idsToLoad, results, this.persister(), session);
        return this.getObjectFromList(results, id, session);
    }
}

