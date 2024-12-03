/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.engine.internal;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import org.hibernate.engine.spi.BatchFetchQueue;
import org.hibernate.engine.spi.EntityKey;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.persister.entity.EntityPersister;
import org.jboss.logging.Logger;

public class BatchFetchQueueHelper {
    private static final CoreMessageLogger LOG = (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)BatchFetchQueueHelper.class.getName());

    private BatchFetchQueueHelper() {
    }

    public static void removeNotFoundBatchLoadableEntityKeys(Serializable[] ids, List<?> results, EntityPersister persister, SharedSessionContractImplementor session) {
        if (!persister.isBatchLoadable()) {
            return;
        }
        if (ids.length == results.size()) {
            return;
        }
        LOG.debug("Not all entities were loaded.");
        HashSet<Serializable> idSet = new HashSet<Serializable>(Arrays.asList(ids));
        for (Object result : results) {
            idSet.remove(session.getContextEntityIdentifier(result));
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Entities of type [" + persister.getEntityName() + "] not found; IDs: " + idSet);
        }
        for (Serializable id : idSet) {
            BatchFetchQueueHelper.removeBatchLoadableEntityKey(id, persister, session);
        }
    }

    public static void removeBatchLoadableEntityKey(Serializable id, EntityPersister persister, SharedSessionContractImplementor session) {
        EntityKey entityKey = session.generateEntityKey(id, persister);
        BatchFetchQueue batchFetchQueue = session.getPersistenceContextInternal().getBatchFetchQueue();
        batchFetchQueue.removeBatchLoadableEntityKey(entityKey);
    }
}

