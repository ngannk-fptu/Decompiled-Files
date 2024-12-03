/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.loader.plan.exec.internal;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.AssertionFailure;
import org.hibernate.engine.FetchStyle;
import org.hibernate.engine.FetchTiming;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.loader.plan.exec.internal.AliasResolutionContextImpl;
import org.hibernate.loader.plan.exec.internal.FetchStats;
import org.hibernate.loader.plan.exec.process.internal.CollectionReferenceInitializerImpl;
import org.hibernate.loader.plan.exec.process.internal.EntityReferenceInitializerImpl;
import org.hibernate.loader.plan.exec.process.spi.ReaderCollector;
import org.hibernate.loader.plan.exec.query.internal.SelectStatementBuilder;
import org.hibernate.loader.plan.exec.query.spi.QueryBuildingParameters;
import org.hibernate.loader.plan.exec.spi.AliasResolutionContext;
import org.hibernate.loader.plan.exec.spi.CollectionReferenceAliases;
import org.hibernate.loader.plan.exec.spi.EntityReferenceAliases;
import org.hibernate.loader.plan.spi.CollectionAttributeFetch;
import org.hibernate.loader.plan.spi.CollectionQuerySpace;
import org.hibernate.loader.plan.spi.CompositeQuerySpace;
import org.hibernate.loader.plan.spi.EntityFetch;
import org.hibernate.loader.plan.spi.EntityQuerySpace;
import org.hibernate.loader.plan.spi.EntityReference;
import org.hibernate.loader.plan.spi.Fetch;
import org.hibernate.loader.plan.spi.FetchSource;
import org.hibernate.loader.plan.spi.Join;
import org.hibernate.loader.plan.spi.JoinDefinedByMetadata;
import org.hibernate.loader.plan.spi.QuerySpace;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.collection.QueryableCollection;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.Joinable;
import org.hibernate.persister.entity.OuterJoinLoadable;
import org.hibernate.persister.walking.internal.FetchStrategyHelper;
import org.hibernate.sql.JoinFragment;
import org.hibernate.sql.JoinType;
import org.hibernate.type.AssociationType;
import org.hibernate.type.BagType;
import org.hibernate.type.Type;
import org.jboss.logging.Logger;

public class LoadQueryJoinAndFetchProcessor {
    private static final Logger LOG = CoreLogging.logger(LoadQueryJoinAndFetchProcessor.class);
    private final AliasResolutionContextImpl aliasResolutionContext;
    private final QueryBuildingParameters buildingParameters;
    private final SessionFactoryImplementor factory;

    public LoadQueryJoinAndFetchProcessor(AliasResolutionContextImpl aliasResolutionContext, QueryBuildingParameters buildingParameters, SessionFactoryImplementor factory) {
        this.aliasResolutionContext = aliasResolutionContext;
        this.buildingParameters = buildingParameters;
        this.factory = factory;
    }

    public AliasResolutionContext getAliasResolutionContext() {
        return this.aliasResolutionContext;
    }

    public QueryBuildingParameters getQueryBuildingParameters() {
        return this.buildingParameters;
    }

    public SessionFactoryImplementor getSessionFactory() {
        return this.factory;
    }

    public void processQuerySpaceJoins(QuerySpace querySpace, SelectStatementBuilder selectStatementBuilder) {
        LOG.debug((Object)("processing queryspace " + querySpace.getUid()));
        JoinFragment joinFragment = this.factory.getDialect().createOuterJoinFragment();
        this.processQuerySpaceJoins(querySpace, joinFragment);
        selectStatementBuilder.setOuterJoins(joinFragment.toFromFragmentString(), joinFragment.toWhereFragmentString());
    }

    private void processQuerySpaceJoins(QuerySpace querySpace, JoinFragment joinFragment) {
        for (Join join : querySpace.getJoins()) {
            this.processQuerySpaceJoin(join, joinFragment);
        }
    }

    private void processQuerySpaceJoin(Join join, JoinFragment joinFragment) {
        this.renderJoin(join, joinFragment);
        this.processQuerySpaceJoins(join.getRightHandSide(), joinFragment);
    }

    private void renderJoin(Join join, JoinFragment joinFragment) {
        if (CompositeQuerySpace.class.isInstance(join.getRightHandSide())) {
            this.handleCompositeJoin(join, joinFragment);
        } else if (EntityQuerySpace.class.isInstance(join.getRightHandSide())) {
            if (join.getLeftHandSide().getDisposition() == QuerySpace.Disposition.COLLECTION) {
                if (((CollectionQuerySpace)CollectionQuerySpace.class.cast(join.getLeftHandSide())).getCollectionPersister().isManyToMany()) {
                    this.renderManyToManyJoin(join, joinFragment);
                } else if (JoinDefinedByMetadata.class.isInstance(join) && "indices".equals(((JoinDefinedByMetadata)JoinDefinedByMetadata.class.cast(join)).getJoinedPropertyName())) {
                    this.renderManyToManyJoin(join, joinFragment);
                }
            } else {
                this.renderEntityJoin(join, joinFragment);
            }
        } else if (CollectionQuerySpace.class.isInstance(join.getRightHandSide())) {
            this.renderCollectionJoin(join, joinFragment);
        }
    }

    private void handleCompositeJoin(Join join, JoinFragment joinFragment) {
        String leftHandSideUid = join.getLeftHandSide().getUid();
        String rightHandSideUid = join.getRightHandSide().getUid();
        String leftHandSideTableAlias = this.aliasResolutionContext.resolveSqlTableAliasFromQuerySpaceUid(leftHandSideUid);
        if (leftHandSideTableAlias == null) {
            throw new IllegalStateException("QuerySpace with that UID was not yet registered in the AliasResolutionContext");
        }
        this.aliasResolutionContext.registerCompositeQuerySpaceUidResolution(rightHandSideUid, leftHandSideTableAlias);
    }

    private void renderEntityJoin(Join join, JoinFragment joinFragment) {
        EntityQuerySpace rightHandSide = (EntityQuerySpace)join.getRightHandSide();
        EntityReferenceAliases aliases = this.aliasResolutionContext.resolveEntityReferenceAliases(rightHandSide.getUid());
        if (aliases == null) {
            this.aliasResolutionContext.generateEntityReferenceAliases(rightHandSide.getUid(), rightHandSide.getEntityPersister());
        }
        Joinable joinable = (Joinable)((Object)rightHandSide.getEntityPersister());
        this.addJoins(join, joinFragment, joinable, null);
    }

    private AssociationType getJoinedAssociationTypeOrNull(Join join) {
        if (!JoinDefinedByMetadata.class.isInstance(join)) {
            return null;
        }
        Type joinedType = ((JoinDefinedByMetadata)join).getJoinedPropertyType();
        return joinedType.isAssociationType() ? (AssociationType)joinedType : null;
    }

    private String resolveAdditionalJoinCondition(String rhsTableAlias, String withClause, Joinable joinable, AssociationType associationType) {
        String filter;
        String string = filter = associationType != null ? associationType.getOnCondition(rhsTableAlias, this.factory, this.buildingParameters.getQueryInfluencers().getEnabledFilters()) : joinable.filterFragment(rhsTableAlias, this.buildingParameters.getQueryInfluencers().getEnabledFilters());
        if (StringHelper.isEmpty(withClause) && StringHelper.isEmpty(filter)) {
            return "";
        }
        if (StringHelper.isNotEmpty(withClause) && StringHelper.isNotEmpty(filter)) {
            return filter + " and " + withClause;
        }
        return StringHelper.isNotEmpty(filter) ? filter : withClause;
    }

    private void addJoins(Join join, JoinFragment joinFragment, Joinable joinable, String joinConditions) {
        String rhsTableAlias = this.aliasResolutionContext.resolveSqlTableAliasFromQuerySpaceUid(join.getRightHandSide().getUid());
        if (StringHelper.isEmpty(rhsTableAlias)) {
            throw new IllegalStateException("Join's RHS table alias cannot be empty");
        }
        String lhsTableAlias = this.aliasResolutionContext.resolveSqlTableAliasFromQuerySpaceUid(join.getLeftHandSide().getUid());
        if (lhsTableAlias == null) {
            throw new IllegalStateException("QuerySpace with that UID was not yet registered in the AliasResolutionContext");
        }
        String otherConditions = join.getAnyAdditionalJoinConditions(rhsTableAlias);
        if (!StringHelper.isEmpty(otherConditions) && !StringHelper.isEmpty(joinConditions)) {
            otherConditions = otherConditions + " and " + joinConditions;
        } else if (!StringHelper.isEmpty(joinConditions)) {
            otherConditions = joinConditions;
        }
        String additionalJoinConditions = this.resolveAdditionalJoinCondition(rhsTableAlias, otherConditions, joinable, this.getJoinedAssociationTypeOrNull(join));
        String[] joinColumns = join.resolveAliasedLeftHandSideJoinConditionColumns(lhsTableAlias);
        QuerySpace lhsQuerySpace = join.getLeftHandSide();
        if (joinColumns.length == 0 && lhsQuerySpace instanceof EntityQuerySpace) {
            EntityQuerySpace entityQuerySpace = (EntityQuerySpace)lhsQuerySpace;
            AbstractEntityPersister persister = (AbstractEntityPersister)entityQuerySpace.getEntityPersister();
            String[][] polyJoinColumns = persister.getPolymorphicJoinColumns(lhsTableAlias, ((JoinDefinedByMetadata)join).getJoinedPropertyName());
            joinFragment.addJoin(joinable.getTableName(), rhsTableAlias, polyJoinColumns, join.resolveNonAliasedRightHandSideJoinConditionColumns(), join.isRightHandSideRequired() ? JoinType.INNER_JOIN : JoinType.LEFT_OUTER_JOIN, additionalJoinConditions);
        } else {
            joinFragment.addJoin(joinable.getTableName(), rhsTableAlias, joinColumns, join.resolveNonAliasedRightHandSideJoinConditionColumns(), join.isRightHandSideRequired() ? JoinType.INNER_JOIN : JoinType.LEFT_OUTER_JOIN, additionalJoinConditions);
        }
        joinFragment.addJoins(joinable.fromJoinFragment(rhsTableAlias, false, true), joinable.whereJoinFragment(rhsTableAlias, false, true));
    }

    private void renderCollectionJoin(Join join, JoinFragment joinFragment) {
        CollectionQuerySpace rightHandSide = (CollectionQuerySpace)join.getRightHandSide();
        Join collectionElementJoin = null;
        JoinDefinedByMetadata collectionIndexJoin = null;
        for (Join collectionJoin : rightHandSide.getJoins()) {
            if (!JoinDefinedByMetadata.class.isInstance(collectionJoin)) continue;
            JoinDefinedByMetadata collectionJoinDefinedByMetadata = (JoinDefinedByMetadata)collectionJoin;
            if ("elements".equals(collectionJoinDefinedByMetadata.getJoinedPropertyName())) {
                if (collectionElementJoin != null) {
                    throw new AssertionFailure(String.format("More than one element join defined for: %s", rightHandSide.getCollectionPersister().getRole()));
                }
                collectionElementJoin = collectionJoinDefinedByMetadata;
            }
            if (!"indices".equals(collectionJoinDefinedByMetadata.getJoinedPropertyName())) continue;
            if (collectionIndexJoin != null) {
                throw new AssertionFailure(String.format("More than one index join defined for: %s", rightHandSide.getCollectionPersister().getRole()));
            }
            collectionIndexJoin = collectionJoinDefinedByMetadata;
        }
        if (rightHandSide.getCollectionPersister().isOneToMany() || rightHandSide.getCollectionPersister().isManyToMany()) {
            if (collectionElementJoin == null) {
                throw new IllegalStateException(String.format("Could not locate collection element join within collection join [%s : %s]", rightHandSide.getUid(), rightHandSide.getCollectionPersister()));
            }
            this.aliasResolutionContext.generateCollectionReferenceAliases(rightHandSide.getUid(), rightHandSide.getCollectionPersister(), collectionElementJoin.getRightHandSide().getUid());
        } else {
            this.aliasResolutionContext.generateCollectionReferenceAliases(rightHandSide.getUid(), rightHandSide.getCollectionPersister(), null);
        }
        if (rightHandSide.getCollectionPersister().hasIndex() && rightHandSide.getCollectionPersister().getIndexType().isEntityType()) {
            if (collectionIndexJoin == null) {
                throw new IllegalStateException(String.format("Could not locate collection index join within collection join [%s : %s]", rightHandSide.getUid(), rightHandSide.getCollectionPersister()));
            }
            this.aliasResolutionContext.generateEntityReferenceAliases(collectionIndexJoin.getRightHandSide().getUid(), rightHandSide.getCollectionPersister().getIndexDefinition().toEntityDefinition().getEntityPersister());
        }
        this.addJoins(join, joinFragment, (Joinable)((Object)rightHandSide.getCollectionPersister()), null);
    }

    private void renderManyToManyJoin(Join join, JoinFragment joinFragment) {
        String manyToManyFilter;
        EntityPersister entityPersister = ((EntityQuerySpace)join.getRightHandSide()).getEntityPersister();
        String entityTableAlias = this.aliasResolutionContext.resolveSqlTableAliasFromQuerySpaceUid(join.getRightHandSide().getUid());
        if (StringHelper.isEmpty(entityTableAlias)) {
            throw new IllegalStateException("Collection element (many-to-many) table alias cannot be empty");
        }
        if (JoinDefinedByMetadata.class.isInstance(join) && "elements".equals(((JoinDefinedByMetadata)join).getJoinedPropertyName())) {
            CollectionQuerySpace leftHandSide = (CollectionQuerySpace)join.getLeftHandSide();
            CollectionPersister persister = leftHandSide.getCollectionPersister();
            manyToManyFilter = persister.getManyToManyFilterFragment(entityTableAlias, this.buildingParameters.getQueryInfluencers().getEnabledFilters());
        } else {
            manyToManyFilter = null;
        }
        this.addJoins(join, joinFragment, (Joinable)((Object)entityPersister), manyToManyFilter);
    }

    public FetchStats processFetches(FetchSource fetchSource, SelectStatementBuilder selectStatementBuilder, ReaderCollector readerCollector) {
        EntityReference fetchOwnerAsEntityReference;
        FetchStatsImpl fetchStats = new FetchStatsImpl();
        if (EntityReference.class.isInstance(fetchSource) && (fetchOwnerAsEntityReference = (EntityReference)fetchSource).getIdentifierDescription().hasFetches()) {
            FetchSource entityIdentifierAsFetchSource = (FetchSource)((Object)fetchOwnerAsEntityReference.getIdentifierDescription());
            for (Fetch fetch : entityIdentifierAsFetchSource.getFetches()) {
                this.processFetch(selectStatementBuilder, fetchSource, fetch, readerCollector, fetchStats);
            }
        }
        this.processFetches(fetchSource, selectStatementBuilder, readerCollector, fetchStats);
        return fetchStats;
    }

    private void processFetches(FetchSource fetchSource, SelectStatementBuilder selectStatementBuilder, ReaderCollector readerCollector, FetchStatsImpl fetchStats) {
        for (Fetch fetch : fetchSource.getFetches()) {
            this.processFetch(selectStatementBuilder, fetchSource, fetch, readerCollector, fetchStats);
        }
    }

    private void processFetch(SelectStatementBuilder selectStatementBuilder, FetchSource fetchSource, Fetch fetch, ReaderCollector readerCollector, FetchStatsImpl fetchStats) {
        if (EntityFetch.class.isInstance(fetch)) {
            EntityFetch entityFetch = (EntityFetch)fetch;
            this.processEntityFetch(selectStatementBuilder, fetchSource, entityFetch, readerCollector, fetchStats);
        } else if (CollectionAttributeFetch.class.isInstance(fetch)) {
            CollectionAttributeFetch collectionFetch = (CollectionAttributeFetch)fetch;
            this.processCollectionFetch(selectStatementBuilder, fetchSource, collectionFetch, readerCollector, fetchStats);
        } else if (FetchSource.class.isInstance(fetch)) {
            this.processFetches((FetchSource)((Object)fetch), selectStatementBuilder, readerCollector, fetchStats);
        }
    }

    private void processEntityFetch(SelectStatementBuilder selectStatementBuilder, FetchSource fetchSource, EntityFetch fetch, ReaderCollector readerCollector, FetchStatsImpl fetchStats) {
        fetchStats.processingFetch(fetch);
        if (!FetchStrategyHelper.isJoinFetched(fetch.getFetchStrategy())) {
            return;
        }
        Joinable joinable = (Joinable)((Object)fetch.getEntityPersister());
        EntityReferenceAliases aliases = this.aliasResolutionContext.resolveEntityReferenceAliases(fetch.getQuerySpaceUid());
        selectStatementBuilder.appendSelectClauseFragment(joinable.selectFragment(null, null, aliases.getTableAlias(), aliases.getColumnAliases().getSuffix(), null, true));
        if (fetch.getIdentifierDescription().hasFetches()) {
            FetchSource entityIdentifierAsFetchSource = (FetchSource)((Object)fetch.getIdentifierDescription());
            for (Fetch identifierFetch : entityIdentifierAsFetchSource.getFetches()) {
                this.processFetch(selectStatementBuilder, fetch, identifierFetch, readerCollector, fetchStats);
            }
        }
        readerCollector.add(new EntityReferenceInitializerImpl(fetch, aliases));
        this.processFetches(fetch, selectStatementBuilder, readerCollector, fetchStats);
    }

    private void processCollectionFetch(SelectStatementBuilder selectStatementBuilder, FetchSource fetchSource, CollectionAttributeFetch fetch, ReaderCollector readerCollector, FetchStatsImpl fetchStats) {
        fetchStats.processingFetch(fetch);
        if (!FetchStrategyHelper.isJoinFetched(fetch.getFetchStrategy())) {
            return;
        }
        CollectionReferenceAliases aliases = this.aliasResolutionContext.resolveCollectionReferenceAliases(fetch.getQuerySpaceUid());
        QueryableCollection queryableCollection = (QueryableCollection)fetch.getCollectionPersister();
        Joinable joinableCollection = (Joinable)((Object)fetch.getCollectionPersister());
        if (fetch.getCollectionPersister().isManyToMany()) {
            String ordering;
            String ownerTableAlias = this.aliasResolutionContext.resolveSqlTableAliasFromQuerySpaceUid(fetchSource.getQuerySpaceUid());
            String collectionTableAlias = aliases.getCollectionTableAlias();
            String elementTableAlias = aliases.getElementTableAlias();
            selectStatementBuilder.appendSelectClauseFragment(joinableCollection.selectFragment((Joinable)((Object)queryableCollection.getElementPersister()), elementTableAlias, collectionTableAlias, aliases.getEntityElementAliases().getColumnAliases().getSuffix(), aliases.getCollectionColumnAliases().getSuffix(), true));
            OuterJoinLoadable elementPersister = (OuterJoinLoadable)queryableCollection.getElementPersister();
            selectStatementBuilder.appendSelectClauseFragment(elementPersister.selectFragment(elementTableAlias, aliases.getEntityElementAliases().getColumnAliases().getSuffix()));
            String manyToManyOrdering = queryableCollection.getManyToManyOrderByString(elementTableAlias);
            if (StringHelper.isNotEmpty(manyToManyOrdering)) {
                selectStatementBuilder.appendOrderByFragment(manyToManyOrdering);
            }
            if (StringHelper.isNotEmpty(ordering = queryableCollection.getSQLOrderByString(collectionTableAlias))) {
                selectStatementBuilder.appendOrderByFragment(ordering);
            }
            readerCollector.add(new EntityReferenceInitializerImpl((EntityReference)((Object)fetch.getElementGraph()), this.aliasResolutionContext.resolveEntityReferenceAliases(fetch.getElementGraph().getQuerySpaceUid())));
        } else {
            String ordering;
            selectStatementBuilder.appendSelectClauseFragment(queryableCollection.selectFragment(aliases.getElementTableAlias(), aliases.getCollectionColumnAliases().getSuffix()));
            if (fetch.getCollectionPersister().isOneToMany()) {
                OuterJoinLoadable elementPersister = (OuterJoinLoadable)queryableCollection.getElementPersister();
                selectStatementBuilder.appendSelectClauseFragment(elementPersister.selectFragment(aliases.getElementTableAlias(), aliases.getEntityElementAliases().getColumnAliases().getSuffix()));
                readerCollector.add(new EntityReferenceInitializerImpl((EntityReference)((Object)fetch.getElementGraph()), this.aliasResolutionContext.resolveEntityReferenceAliases(fetch.getElementGraph().getQuerySpaceUid())));
            }
            if (StringHelper.isNotEmpty(ordering = queryableCollection.getSQLOrderByString(aliases.getElementTableAlias()))) {
                selectStatementBuilder.appendOrderByFragment(ordering);
            }
        }
        if (fetch.getElementGraph() != null) {
            this.processFetches(fetch.getElementGraph(), selectStatementBuilder, readerCollector);
        }
        readerCollector.add(new CollectionReferenceInitializerImpl(fetch, aliases));
    }

    private static class FetchStatsImpl
    implements FetchStats {
        private boolean hasSubselectFetch;
        private Set<CollectionAttributeFetch> joinedBagAttributeFetches;

        private FetchStatsImpl() {
        }

        public void processingFetch(Fetch fetch) {
            if (!this.hasSubselectFetch && fetch.getFetchStrategy().getStyle() == FetchStyle.SUBSELECT && fetch.getFetchStrategy().getTiming() != FetchTiming.IMMEDIATE) {
                this.hasSubselectFetch = true;
            }
            if (this.isJoinFetchedBag(fetch)) {
                if (this.joinedBagAttributeFetches == null) {
                    this.joinedBagAttributeFetches = new HashSet<CollectionAttributeFetch>();
                }
                this.joinedBagAttributeFetches.add((CollectionAttributeFetch)fetch);
            }
        }

        @Override
        public boolean hasSubselectFetches() {
            return this.hasSubselectFetch;
        }

        @Override
        public Set<CollectionAttributeFetch> getJoinedBagAttributeFetches() {
            return this.joinedBagAttributeFetches == null ? Collections.emptySet() : this.joinedBagAttributeFetches;
        }

        private boolean isJoinFetchedBag(Fetch fetch) {
            if (FetchStrategyHelper.isJoinFetched(fetch.getFetchStrategy()) && CollectionAttributeFetch.class.isInstance(fetch)) {
                CollectionAttributeFetch collectionAttributeFetch = (CollectionAttributeFetch)fetch;
                return collectionAttributeFetch.getFetchedType().getClass().isAssignableFrom(BagType.class);
            }
            return false;
        }
    }
}

