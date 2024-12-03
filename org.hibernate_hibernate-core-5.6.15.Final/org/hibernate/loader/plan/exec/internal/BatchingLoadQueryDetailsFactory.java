/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.exec.internal;

import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.loader.plan.exec.internal.AliasResolutionContextImpl;
import org.hibernate.loader.plan.exec.internal.BasicCollectionLoadQueryDetails;
import org.hibernate.loader.plan.exec.internal.EntityLoadQueryDetails;
import org.hibernate.loader.plan.exec.internal.OneToManyLoadQueryDetails;
import org.hibernate.loader.plan.exec.internal.RootHelper;
import org.hibernate.loader.plan.exec.process.spi.ResultSetProcessorResolver;
import org.hibernate.loader.plan.exec.query.spi.QueryBuildingParameters;
import org.hibernate.loader.plan.exec.spi.LoadQueryDetails;
import org.hibernate.loader.plan.spi.CollectionReturn;
import org.hibernate.loader.plan.spi.EntityReturn;
import org.hibernate.loader.plan.spi.LoadPlan;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.entity.Queryable;

public class BatchingLoadQueryDetailsFactory {
    public static final BatchingLoadQueryDetailsFactory INSTANCE = new BatchingLoadQueryDetailsFactory();

    private BatchingLoadQueryDetailsFactory() {
    }

    public EntityLoadQueryDetails makeEntityLoadQueryDetails(LoadPlan loadPlan, String[] keyColumnNames, QueryBuildingParameters buildingParameters, SessionFactoryImplementor factory, ResultSetProcessorResolver resultSetProcessorResolver) {
        EntityReturn rootReturn = RootHelper.INSTANCE.extractRootReturn(loadPlan, EntityReturn.class);
        String[] keyColumnNamesToUse = keyColumnNames != null ? keyColumnNames : ((Queryable)rootReturn.getEntityPersister()).getIdentifierColumnNames();
        AliasResolutionContextImpl aliasResolutionContext = new AliasResolutionContextImpl(factory);
        return new EntityLoadQueryDetails(loadPlan, keyColumnNamesToUse, aliasResolutionContext, rootReturn, buildingParameters, factory, resultSetProcessorResolver);
    }

    public EntityLoadQueryDetails makeEntityLoadQueryDetails(LoadPlan loadPlan, String[] keyColumnNames, QueryBuildingParameters buildingParameters, SessionFactoryImplementor factory) {
        return this.makeEntityLoadQueryDetails(loadPlan, keyColumnNames, buildingParameters, factory, ResultSetProcessorResolver.DEFAULT);
    }

    public EntityLoadQueryDetails makeEntityLoadQueryDetails(EntityLoadQueryDetails entityLoadQueryDetailsTemplate, QueryBuildingParameters buildingParameters, ResultSetProcessorResolver resultSetProcessorResolver) {
        return new EntityLoadQueryDetails(entityLoadQueryDetailsTemplate, buildingParameters, resultSetProcessorResolver);
    }

    public EntityLoadQueryDetails makeEntityLoadQueryDetails(EntityLoadQueryDetails entityLoadQueryDetailsTemplate, QueryBuildingParameters buildingParameters) {
        return this.makeEntityLoadQueryDetails(entityLoadQueryDetailsTemplate, buildingParameters, ResultSetProcessorResolver.DEFAULT);
    }

    public LoadQueryDetails makeCollectionLoadQueryDetails(CollectionPersister collectionPersister, LoadPlan loadPlan, QueryBuildingParameters buildingParameters) {
        CollectionReturn rootReturn = RootHelper.INSTANCE.extractRootReturn(loadPlan, CollectionReturn.class);
        AliasResolutionContextImpl aliasResolutionContext = new AliasResolutionContextImpl(collectionPersister.getFactory());
        return collectionPersister.isOneToMany() ? new OneToManyLoadQueryDetails(loadPlan, aliasResolutionContext, rootReturn, buildingParameters, collectionPersister.getFactory()) : new BasicCollectionLoadQueryDetails(loadPlan, aliasResolutionContext, rootReturn, buildingParameters, collectionPersister.getFactory());
    }
}

