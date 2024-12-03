/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.internal;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import org.hibernate.HibernateException;
import org.hibernate.LockOptions;
import org.hibernate.WrongClassException;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.internal.AbstractLockUpgradeEventListener;
import org.hibernate.event.spi.EventSource;
import org.hibernate.event.spi.ResolveNaturalIdEvent;
import org.hibernate.event.spi.ResolveNaturalIdEventListener;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.UniqueKeyLoadable;
import org.hibernate.pretty.MessageHelper;
import org.hibernate.stat.spi.StatisticsImplementor;

public class DefaultResolveNaturalIdEventListener
extends AbstractLockUpgradeEventListener
implements ResolveNaturalIdEventListener {
    public static final Object REMOVED_ENTITY_MARKER = new Object();
    public static final Object INCONSISTENT_RTN_CLASS_MARKER = new Object();
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(DefaultResolveNaturalIdEventListener.class);

    @Override
    public void onResolveNaturalId(ResolveNaturalIdEvent event) throws HibernateException {
        Serializable entityId = this.resolveNaturalId(event);
        event.setEntityId(entityId);
    }

    protected Serializable resolveNaturalId(ResolveNaturalIdEvent event) {
        Serializable entityId;
        EntityPersister persister = event.getEntityPersister();
        if (LOG.isTraceEnabled()) {
            LOG.tracev("Attempting to resolve: {0}#{1}", MessageHelper.infoString(persister), event.getNaturalIdValues());
        }
        if ((entityId = this.resolveFromCache(event)) != null) {
            if (LOG.isTraceEnabled()) {
                LOG.tracev("Resolved object in cache: {0}#{1}", MessageHelper.infoString(persister), event.getNaturalIdValues());
            }
            return entityId;
        }
        if (LOG.isTraceEnabled()) {
            LOG.tracev("Object not resolved in any cache: {0}#{1}", MessageHelper.infoString(persister), event.getNaturalIdValues());
        }
        return this.loadFromDatasource(event);
    }

    protected Serializable resolveFromCache(ResolveNaturalIdEvent event) {
        return event.getSession().getPersistenceContextInternal().getNaturalIdHelper().findCachedNaturalIdResolution(event.getEntityPersister(), event.getOrderedNaturalIdValues());
    }

    protected Serializable loadFromDatasource(ResolveNaturalIdEvent event) {
        Serializable pk;
        EventSource session = event.getSession();
        SessionFactoryImplementor factory = session.getFactory();
        StatisticsImplementor statistics = factory.getStatistics();
        boolean stats = statistics.isStatisticsEnabled();
        long startTime = 0L;
        if (stats) {
            startTime = System.nanoTime();
        }
        Object[] naturalIdValues = event.getOrderedNaturalIdValues();
        EntityPersister persister = event.getEntityPersister();
        LockOptions lockOptions = event.getLockOptions();
        if (persister instanceof UniqueKeyLoadable) {
            UniqueKeyLoadable rootPersister = (UniqueKeyLoadable)persister.getFactory().getMetamodel().entityPersister(persister.getRootEntityName());
            Object entity = rootPersister.loadByNaturalId(naturalIdValues, lockOptions, session);
            if (entity == null) {
                pk = null;
            } else {
                if (!persister.isInstance(entity)) {
                    throw new WrongClassException("loaded object was of wrong class " + entity.getClass(), (Serializable)naturalIdValues, persister.getEntityName());
                }
                pk = persister.getIdentifier(entity, session);
            }
        } else {
            pk = persister.loadEntityIdByNaturalId(naturalIdValues, lockOptions, session);
        }
        if (stats) {
            long endTime = System.nanoTime();
            long milliseconds = TimeUnit.MILLISECONDS.convert(endTime - startTime, TimeUnit.NANOSECONDS);
            statistics.naturalIdQueryExecuted(persister.getRootEntityName(), milliseconds);
        }
        if (pk != null) {
            session.getPersistenceContextInternal().getNaturalIdHelper().cacheNaturalIdCrossReferenceFromLoad(persister, pk, naturalIdValues);
        }
        return pk;
    }
}

