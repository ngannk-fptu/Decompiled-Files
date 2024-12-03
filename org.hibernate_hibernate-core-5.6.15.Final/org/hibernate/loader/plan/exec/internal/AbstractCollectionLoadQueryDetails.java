/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.exec.internal;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.loader.plan.exec.internal.AbstractLoadQueryDetails;
import org.hibernate.loader.plan.exec.internal.AliasResolutionContextImpl;
import org.hibernate.loader.plan.exec.internal.FetchStats;
import org.hibernate.loader.plan.exec.process.internal.AbstractRowReader;
import org.hibernate.loader.plan.exec.process.internal.CollectionReferenceInitializerImpl;
import org.hibernate.loader.plan.exec.process.internal.CollectionReturnReader;
import org.hibernate.loader.plan.exec.process.internal.EntityReferenceInitializerImpl;
import org.hibernate.loader.plan.exec.process.internal.ResultSetProcessingContextImpl;
import org.hibernate.loader.plan.exec.process.spi.CollectionReferenceInitializer;
import org.hibernate.loader.plan.exec.process.spi.ReaderCollector;
import org.hibernate.loader.plan.exec.process.spi.RowReader;
import org.hibernate.loader.plan.exec.query.internal.SelectStatementBuilder;
import org.hibernate.loader.plan.exec.query.spi.QueryBuildingParameters;
import org.hibernate.loader.plan.exec.spi.CollectionReferenceAliases;
import org.hibernate.loader.plan.exec.spi.EntityReferenceAliases;
import org.hibernate.loader.plan.spi.CollectionQuerySpace;
import org.hibernate.loader.plan.spi.CollectionReturn;
import org.hibernate.loader.plan.spi.EntityReference;
import org.hibernate.loader.plan.spi.LoadPlan;
import org.hibernate.persister.collection.QueryableCollection;
import org.hibernate.persister.entity.OuterJoinLoadable;

public abstract class AbstractCollectionLoadQueryDetails
extends AbstractLoadQueryDetails {
    private final CollectionReferenceAliases collectionReferenceAliases;
    private final ReaderCollector readerCollector;

    protected AbstractCollectionLoadQueryDetails(LoadPlan loadPlan, AliasResolutionContextImpl aliasResolutionContext, CollectionReturn rootReturn, QueryBuildingParameters buildingParameters, SessionFactoryImplementor factory) {
        super(loadPlan, aliasResolutionContext, buildingParameters, ((QueryableCollection)rootReturn.getCollectionPersister()).getKeyColumnNames(), rootReturn, factory);
        String elementUid = rootReturn.getCollectionPersister().getElementType().isEntityType() ? rootReturn.getElementGraph().getQuerySpaceUid() : null;
        this.collectionReferenceAliases = aliasResolutionContext.generateCollectionReferenceAliases(rootReturn.getQuerySpaceUid(), rootReturn.getCollectionPersister(), elementUid);
        this.readerCollector = new CollectionLoaderReaderCollectorImpl(new CollectionReturnReader(rootReturn), new CollectionReferenceInitializerImpl(rootReturn, this.collectionReferenceAliases));
        if (rootReturn.allowElementJoin() && rootReturn.getCollectionPersister().getElementType().isEntityType()) {
            EntityReference elementEntityReference = rootReturn.getElementGraph().resolveEntityReference();
            this.readerCollector.add(new EntityReferenceInitializerImpl(elementEntityReference, this.collectionReferenceAliases.getEntityElementAliases()));
        }
        if (rootReturn.allowIndexJoin() && rootReturn.getCollectionPersister().getIndexType().isEntityType()) {
            EntityReference indexEntityReference = rootReturn.getIndexGraph().resolveEntityReference();
            EntityReferenceAliases indexEntityReferenceAliases = aliasResolutionContext.generateEntityReferenceAliases(indexEntityReference.getQuerySpaceUid(), indexEntityReference.getEntityPersister());
            this.readerCollector.add(new EntityReferenceInitializerImpl(indexEntityReference, indexEntityReferenceAliases));
        }
    }

    protected CollectionReturn getRootCollectionReturn() {
        return (CollectionReturn)this.getRootReturn();
    }

    @Override
    protected boolean isSubselectLoadingEnabled(FetchStats fetchStats) {
        return fetchStats != null && fetchStats.hasSubselectFetches();
    }

    @Override
    protected boolean shouldUseOptionalEntityInstance() {
        return false;
    }

    @Override
    protected ReaderCollector getReaderCollector() {
        return this.readerCollector;
    }

    @Override
    protected CollectionQuerySpace getRootQuerySpace() {
        return (CollectionQuerySpace)this.getQuerySpace(this.getRootCollectionReturn().getQuerySpaceUid());
    }

    protected CollectionReferenceAliases getCollectionReferenceAliases() {
        return this.collectionReferenceAliases;
    }

    protected QueryableCollection getQueryableCollection() {
        return (QueryableCollection)this.getRootCollectionReturn().getCollectionPersister();
    }

    @Override
    protected boolean shouldApplyRootReturnFilterBeforeKeyRestriction() {
        return true;
    }

    @Override
    protected void applyRootReturnSelectFragments(SelectStatementBuilder selectStatementBuilder) {
        if (this.getRootCollectionReturn().allowIndexJoin() && this.getQueryableCollection().getIndexType().isEntityType()) {
            EntityReference indexEntityReference = this.getRootCollectionReturn().getIndexGraph().resolveEntityReference();
            EntityReferenceAliases indexEntityReferenceAliases = this.getAliasResolutionContext().resolveEntityReferenceAliases(indexEntityReference.getQuerySpaceUid());
            selectStatementBuilder.appendSelectClauseFragment(((OuterJoinLoadable)indexEntityReference.getEntityPersister()).selectFragment(indexEntityReferenceAliases.getTableAlias(), indexEntityReferenceAliases.getColumnAliases().getSuffix()));
        }
    }

    @Override
    protected void applyRootReturnFilterRestrictions(SelectStatementBuilder selectStatementBuilder) {
        selectStatementBuilder.appendRestrictions(this.getQueryableCollection().filterFragment(this.getRootTableAlias(), this.getQueryBuildingParameters().getQueryInfluencers().getEnabledFilters()));
    }

    @Override
    protected void applyRootReturnWhereJoinRestrictions(SelectStatementBuilder selectStatementBuilder) {
    }

    @Override
    protected void applyRootReturnOrderByFragments(SelectStatementBuilder selectStatementBuilder) {
        String ordering = this.getQueryableCollection().getSQLOrderByString(this.getRootTableAlias());
        if (StringHelper.isNotEmpty(ordering)) {
            selectStatementBuilder.appendOrderByFragment(ordering);
        }
    }

    private static class CollectionLoaderRowReader
    extends AbstractRowReader {
        private final CollectionReturnReader rootReturnReader;

        public CollectionLoaderRowReader(CollectionLoaderReaderCollectorImpl collectionLoaderReaderCollector) {
            super(collectionLoaderReaderCollector);
            this.rootReturnReader = collectionLoaderReaderCollector.getReturnReader();
        }

        @Override
        protected Object readLogicalRow(ResultSet resultSet, ResultSetProcessingContextImpl context) throws SQLException {
            return this.rootReturnReader.read(resultSet, context);
        }
    }

    private static class CollectionLoaderReaderCollectorImpl
    extends AbstractLoadQueryDetails.ReaderCollectorImpl {
        private final CollectionReturnReader collectionReturnReader;

        public CollectionLoaderReaderCollectorImpl(CollectionReturnReader collectionReturnReader, CollectionReferenceInitializer collectionReferenceInitializer) {
            this.collectionReturnReader = collectionReturnReader;
            this.add(collectionReferenceInitializer);
        }

        @Override
        public RowReader buildRowReader() {
            return new CollectionLoaderRowReader(this);
        }

        @Override
        public CollectionReturnReader getReturnReader() {
            return this.collectionReturnReader;
        }
    }
}

