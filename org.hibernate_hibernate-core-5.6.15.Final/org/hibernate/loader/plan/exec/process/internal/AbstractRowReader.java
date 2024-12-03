/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.loader.plan.exec.process.internal;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.engine.internal.TwoPhaseLoad;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.event.spi.EventSource;
import org.hibernate.event.spi.PostLoadEvent;
import org.hibernate.event.spi.PreLoadEvent;
import org.hibernate.internal.CoreLogging;
import org.hibernate.loader.plan.exec.process.internal.HydratedEntityRegistration;
import org.hibernate.loader.plan.exec.process.internal.ResultSetProcessingContextImpl;
import org.hibernate.loader.plan.exec.process.spi.CollectionReferenceInitializer;
import org.hibernate.loader.plan.exec.process.spi.EntityReferenceInitializer;
import org.hibernate.loader.plan.exec.process.spi.ReaderCollector;
import org.hibernate.loader.plan.exec.process.spi.RowReader;
import org.hibernate.loader.plan.spi.BidirectionalEntityReference;
import org.hibernate.loader.plan.spi.CompositeFetch;
import org.hibernate.loader.plan.spi.EntityFetch;
import org.hibernate.loader.plan.spi.EntityIdentifierDescription;
import org.hibernate.loader.plan.spi.EntityReference;
import org.hibernate.loader.plan.spi.Fetch;
import org.hibernate.loader.plan.spi.FetchSource;
import org.hibernate.loader.spi.AfterLoadAction;
import org.hibernate.persister.entity.Loadable;
import org.jboss.logging.Logger;

public abstract class AbstractRowReader
implements RowReader {
    private static final Logger log = CoreLogging.logger(AbstractRowReader.class);
    private static final EntityReferenceInitializer[] EMPTY_REFERENCE_INITIALIZERS = new EntityReferenceInitializer[0];
    private final EntityReferenceInitializer[] entityReferenceInitializers;
    private final List<CollectionReferenceInitializer> arrayReferenceInitializers;
    private final List<CollectionReferenceInitializer> collectionReferenceInitializers;
    private Map<EntityReference, EntityReferenceInitializer> entityInitializerByEntityReference;

    public AbstractRowReader(ReaderCollector readerCollector) {
        this.entityReferenceInitializers = readerCollector.getEntityReferenceInitializers().toArray(EMPTY_REFERENCE_INITIALIZERS);
        this.arrayReferenceInitializers = readerCollector.getArrayReferenceInitializers();
        this.collectionReferenceInitializers = readerCollector.getNonArrayCollectionReferenceInitializers();
    }

    protected abstract Object readLogicalRow(ResultSet var1, ResultSetProcessingContextImpl var2) throws SQLException;

    @Override
    public Object readRow(ResultSet resultSet, ResultSetProcessingContextImpl context) throws SQLException {
        for (EntityReferenceInitializer entityReferenceInitializer : this.entityReferenceInitializers) {
            entityReferenceInitializer.hydrateIdentifier(resultSet, context);
        }
        for (EntityReferenceInitializer entityReferenceInitializer : this.entityReferenceInitializers) {
            this.resolveEntityKey(resultSet, context, entityReferenceInitializer);
        }
        for (EntityReferenceInitializer entityReferenceInitializer : this.entityReferenceInitializers) {
            entityReferenceInitializer.hydrateEntityState(resultSet, context);
        }
        Object logicalRow = this.readLogicalRow(resultSet, context);
        for (EntityReferenceInitializer entityReferenceInitializer : this.entityReferenceInitializers) {
            entityReferenceInitializer.finishUpRow(resultSet, context);
        }
        for (CollectionReferenceInitializer collectionReferenceInitializer : this.collectionReferenceInitializers) {
            collectionReferenceInitializer.finishUpRow(resultSet, context);
        }
        for (CollectionReferenceInitializer arrayReferenceInitializer : this.arrayReferenceInitializers) {
            arrayReferenceInitializer.finishUpRow(resultSet, context);
        }
        return logicalRow;
    }

    private void resolveEntityKey(ResultSet resultSet, ResultSetProcessingContextImpl context, EntityReferenceInitializer entityReferenceInitializer) throws SQLException {
        EntityReference entityReference = entityReferenceInitializer.getEntityReference();
        EntityIdentifierDescription identifierDescription = entityReference.getIdentifierDescription();
        if (identifierDescription.hasFetches() || identifierDescription.hasBidirectionalEntityReferences()) {
            this.resolveEntityKey(resultSet, context, (FetchSource)((Object)identifierDescription));
        }
        entityReferenceInitializer.resolveEntityKey(resultSet, context);
    }

    private void resolveEntityKey(ResultSet resultSet, ResultSetProcessingContextImpl context, FetchSource fetchSource) throws SQLException {
        for (BidirectionalEntityReference bidirectionalEntityReference : fetchSource.getBidirectionalEntityReferences()) {
            EntityReferenceInitializer targetEntityReferenceInitializer = this.getInitializerByEntityReference(bidirectionalEntityReference.getTargetEntityReference());
            this.resolveEntityKey(resultSet, context, targetEntityReferenceInitializer);
            targetEntityReferenceInitializer.hydrateEntityState(resultSet, context);
        }
        for (Fetch fetch : fetchSource.getFetches()) {
            if (EntityFetch.class.isInstance(fetch)) {
                EntityFetch entityFetch = (EntityFetch)fetch;
                EntityReferenceInitializer entityReferenceInitializer = this.getInitializerByEntityReference(entityFetch);
                if (entityReferenceInitializer == null) continue;
                this.resolveEntityKey(resultSet, context, entityReferenceInitializer);
                entityReferenceInitializer.hydrateEntityState(resultSet, context);
                continue;
            }
            if (!CompositeFetch.class.isInstance(fetch)) continue;
            this.resolveEntityKey(resultSet, context, (CompositeFetch)fetch);
        }
    }

    private EntityReferenceInitializer getInitializerByEntityReference(EntityReference targetEntityReference) {
        if (this.entityInitializerByEntityReference == null) {
            this.entityInitializerByEntityReference = new HashMap<EntityReference, EntityReferenceInitializer>(this.entityReferenceInitializers.length);
            for (EntityReferenceInitializer entityReferenceInitializer : this.entityReferenceInitializers) {
                this.entityInitializerByEntityReference.put(entityReferenceInitializer.getEntityReference(), entityReferenceInitializer);
            }
        }
        return this.entityInitializerByEntityReference.get(targetEntityReference);
    }

    @Override
    public void finishUp(ResultSetProcessingContextImpl context, List<AfterLoadAction> afterLoadActionList) {
        PostLoadEvent postLoadEvent;
        PreLoadEvent preLoadEvent;
        List<HydratedEntityRegistration> hydratedEntityRegistrations = context.getHydratedEntityRegistrationList();
        this.finishLoadingArrays(context);
        if (context.getSession().isEventSource()) {
            preLoadEvent = new PreLoadEvent((EventSource)context.getSession());
            postLoadEvent = new PostLoadEvent((EventSource)context.getSession());
        } else {
            preLoadEvent = null;
            postLoadEvent = null;
        }
        this.performTwoPhaseLoad(preLoadEvent, context, hydratedEntityRegistrations);
        this.finishLoadingCollections(context);
        this.afterInitialize(context, hydratedEntityRegistrations);
        this.postLoad(postLoadEvent, context, hydratedEntityRegistrations, afterLoadActionList);
    }

    protected void finishLoadingArrays(ResultSetProcessingContextImpl context) {
        for (CollectionReferenceInitializer arrayReferenceInitializer : this.arrayReferenceInitializers) {
            arrayReferenceInitializer.endLoading(context);
        }
    }

    private void performTwoPhaseLoad(PreLoadEvent preLoadEvent, ResultSetProcessingContextImpl context, List<HydratedEntityRegistration> hydratedEntityRegistrations) {
        int numberOfHydratedObjects = hydratedEntityRegistrations == null ? 0 : hydratedEntityRegistrations.size();
        log.tracev("Total objects hydrated: {0}", (Object)numberOfHydratedObjects);
        if (numberOfHydratedObjects == 0) {
            return;
        }
        SharedSessionContractImplementor session = context.getSession();
        for (HydratedEntityRegistration registration : hydratedEntityRegistrations) {
            TwoPhaseLoad.initializeEntity(registration.getInstance(), context.isReadOnly(), session, preLoadEvent);
        }
    }

    protected void finishLoadingCollections(ResultSetProcessingContextImpl context) {
        for (CollectionReferenceInitializer collectionReferenceInitializer : this.collectionReferenceInitializers) {
            collectionReferenceInitializer.endLoading(context);
        }
    }

    protected void afterInitialize(ResultSetProcessingContextImpl context, List<HydratedEntityRegistration> hydratedEntityRegistrations) {
        if (hydratedEntityRegistrations == null) {
            return;
        }
        for (HydratedEntityRegistration registration : hydratedEntityRegistrations) {
            TwoPhaseLoad.afterInitialize(registration.getInstance(), context.getSession());
        }
    }

    protected void postLoad(PostLoadEvent postLoadEvent, ResultSetProcessingContextImpl context, List<HydratedEntityRegistration> hydratedEntityRegistrations, List<AfterLoadAction> afterLoadActionList) {
        if (hydratedEntityRegistrations == null) {
            return;
        }
        SharedSessionContractImplementor session = context.getSession();
        for (HydratedEntityRegistration registration : hydratedEntityRegistrations) {
            TwoPhaseLoad.postLoad(registration.getInstance(), session, postLoadEvent);
            if (afterLoadActionList == null) continue;
            for (AfterLoadAction afterLoadAction : afterLoadActionList) {
                afterLoadAction.afterLoad(session, registration.getInstance(), (Loadable)registration.getEntityReference().getEntityPersister());
            }
        }
    }
}

