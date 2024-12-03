/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.exec.internal;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.EntityKey;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.internal.util.collections.CollectionHelper;
import org.hibernate.loader.plan.exec.internal.AbstractLoadQueryDetails;
import org.hibernate.loader.plan.exec.internal.AliasResolutionContextImpl;
import org.hibernate.loader.plan.exec.internal.FetchStats;
import org.hibernate.loader.plan.exec.process.internal.AbstractRowReader;
import org.hibernate.loader.plan.exec.process.internal.EntityReferenceInitializerImpl;
import org.hibernate.loader.plan.exec.process.internal.EntityReturnReader;
import org.hibernate.loader.plan.exec.process.internal.ResultSetProcessingContextImpl;
import org.hibernate.loader.plan.exec.process.spi.EntityReferenceInitializer;
import org.hibernate.loader.plan.exec.process.spi.ReaderCollector;
import org.hibernate.loader.plan.exec.process.spi.ResultSetProcessingContext;
import org.hibernate.loader.plan.exec.process.spi.ResultSetProcessorResolver;
import org.hibernate.loader.plan.exec.process.spi.RowReader;
import org.hibernate.loader.plan.exec.query.internal.SelectStatementBuilder;
import org.hibernate.loader.plan.exec.query.spi.QueryBuildingParameters;
import org.hibernate.loader.plan.exec.spi.EntityReferenceAliases;
import org.hibernate.loader.plan.spi.EntityReturn;
import org.hibernate.loader.plan.spi.LoadPlan;
import org.hibernate.loader.plan.spi.QuerySpace;
import org.hibernate.persister.entity.OuterJoinLoadable;
import org.hibernate.persister.entity.Queryable;

public class EntityLoadQueryDetails
extends AbstractLoadQueryDetails {
    private final EntityReferenceAliases entityReferenceAliases;
    private final ReaderCollector readerCollector;

    protected EntityLoadQueryDetails(LoadPlan loadPlan, String[] keyColumnNames, AliasResolutionContextImpl aliasResolutionContext, EntityReturn rootReturn, QueryBuildingParameters buildingParameters, SessionFactoryImplementor factory, ResultSetProcessorResolver resultSetProcessorResolver) {
        super(loadPlan, aliasResolutionContext, buildingParameters, keyColumnNames, rootReturn, factory);
        this.entityReferenceAliases = aliasResolutionContext.generateEntityReferenceAliases(rootReturn.getQuerySpaceUid(), rootReturn.getEntityPersister());
        this.readerCollector = new EntityLoaderReaderCollectorImpl(new EntityReturnReader(rootReturn), new EntityReferenceInitializerImpl(rootReturn, this.entityReferenceAliases, true));
        this.generate(resultSetProcessorResolver);
    }

    protected EntityLoadQueryDetails(LoadPlan loadPlan, String[] keyColumnNames, AliasResolutionContextImpl aliasResolutionContext, EntityReturn rootReturn, QueryBuildingParameters buildingParameters, SessionFactoryImplementor factory) {
        this(loadPlan, keyColumnNames, aliasResolutionContext, rootReturn, buildingParameters, factory, ResultSetProcessorResolver.DEFAULT);
    }

    protected EntityLoadQueryDetails(EntityLoadQueryDetails initialEntityLoadQueryDetails, QueryBuildingParameters buildingParameters, ResultSetProcessorResolver resultSetProcessorResolver) {
        this(initialEntityLoadQueryDetails.getLoadPlan(), initialEntityLoadQueryDetails.getKeyColumnNames(), new AliasResolutionContextImpl(initialEntityLoadQueryDetails.getSessionFactory()), (EntityReturn)initialEntityLoadQueryDetails.getRootReturn(), buildingParameters, initialEntityLoadQueryDetails.getSessionFactory(), resultSetProcessorResolver);
    }

    protected EntityLoadQueryDetails(EntityLoadQueryDetails initialEntityLoadQueryDetails, QueryBuildingParameters buildingParameters) {
        this(initialEntityLoadQueryDetails, buildingParameters, ResultSetProcessorResolver.DEFAULT);
    }

    public boolean hasCollectionInitializers() {
        return CollectionHelper.isNotEmpty(this.readerCollector.getArrayReferenceInitializers()) || CollectionHelper.isNotEmpty(this.readerCollector.getNonArrayCollectionReferenceInitializers());
    }

    private EntityReturn getRootEntityReturn() {
        return (EntityReturn)this.getRootReturn();
    }

    @Override
    protected void applyRootReturnTableFragments(SelectStatementBuilder select) {
        String fromTableFragment;
        String rootAlias = this.entityReferenceAliases.getTableAlias();
        OuterJoinLoadable outerJoinLoadable = (OuterJoinLoadable)this.getRootEntityReturn().getEntityPersister();
        Dialect dialect = this.getSessionFactory().getJdbcServices().getJdbcEnvironment().getDialect();
        if (this.getQueryBuildingParameters().getLockOptions() != null) {
            fromTableFragment = dialect.appendLockHint(this.getQueryBuildingParameters().getLockOptions(), outerJoinLoadable.fromTableFragment(rootAlias));
            select.setLockOptions(this.getQueryBuildingParameters().getLockOptions());
        } else if (this.getQueryBuildingParameters().getLockMode() != null) {
            fromTableFragment = dialect.appendLockHint(this.getQueryBuildingParameters().getLockMode(), outerJoinLoadable.fromTableFragment(rootAlias));
            select.setLockMode(this.getQueryBuildingParameters().getLockMode());
        } else {
            fromTableFragment = outerJoinLoadable.fromTableFragment(rootAlias);
        }
        select.appendFromClauseFragment(fromTableFragment + outerJoinLoadable.fromJoinFragment(rootAlias, true, true));
    }

    @Override
    protected void applyRootReturnFilterRestrictions(SelectStatementBuilder selectStatementBuilder) {
        Queryable rootQueryable = (Queryable)this.getRootEntityReturn().getEntityPersister();
        selectStatementBuilder.appendRestrictions(rootQueryable.filterFragment(this.entityReferenceAliases.getTableAlias(), Collections.emptyMap()));
    }

    @Override
    protected void applyRootReturnWhereJoinRestrictions(SelectStatementBuilder selectStatementBuilder) {
        OuterJoinLoadable joinable = (OuterJoinLoadable)this.getRootEntityReturn().getEntityPersister();
        selectStatementBuilder.appendRestrictions(joinable.whereJoinFragment(this.entityReferenceAliases.getTableAlias(), true, true));
    }

    @Override
    protected void applyRootReturnOrderByFragments(SelectStatementBuilder selectStatementBuilder) {
    }

    @Override
    protected boolean isSubselectLoadingEnabled(FetchStats fetchStats) {
        return this.getQueryBuildingParameters().getBatchSize() > 1 && fetchStats != null && fetchStats.hasSubselectFetches();
    }

    @Override
    protected boolean shouldUseOptionalEntityInstance() {
        return this.getQueryBuildingParameters().getBatchSize() < 2;
    }

    @Override
    protected ReaderCollector getReaderCollector() {
        return this.readerCollector;
    }

    @Override
    protected QuerySpace getRootQuerySpace() {
        return this.getQuerySpace(this.getRootEntityReturn().getQuerySpaceUid());
    }

    @Override
    protected String getRootTableAlias() {
        return this.entityReferenceAliases.getTableAlias();
    }

    @Override
    protected boolean shouldApplyRootReturnFilterBeforeKeyRestriction() {
        return false;
    }

    @Override
    protected void applyRootReturnSelectFragments(SelectStatementBuilder selectStatementBuilder) {
        OuterJoinLoadable outerJoinLoadable = (OuterJoinLoadable)this.getRootEntityReturn().getEntityPersister();
        selectStatementBuilder.appendSelectClauseFragment(outerJoinLoadable.selectFragment(this.entityReferenceAliases.getTableAlias(), this.entityReferenceAliases.getColumnAliases().getSuffix()));
    }

    private static class EntityLoaderRowReader
    extends AbstractRowReader {
        private final EntityReturnReader rootReturnReader;

        public EntityLoaderRowReader(EntityLoaderReaderCollectorImpl entityLoaderReaderCollector) {
            super(entityLoaderReaderCollector);
            this.rootReturnReader = entityLoaderReaderCollector.getReturnReader();
        }

        @Override
        public Object readRow(ResultSet resultSet, ResultSetProcessingContextImpl context) throws SQLException {
            ResultSetProcessingContext.EntityReferenceProcessingState processingState = this.rootReturnReader.getIdentifierResolutionContext(context);
            if (context.shouldUseOptionalEntityInformation() && context.getQueryParameters().getOptionalId() != null) {
                EntityKey entityKey = context.getSession().generateEntityKey(context.getQueryParameters().getOptionalId(), processingState.getEntityReference().getEntityPersister());
                processingState.registerIdentifierHydratedForm(entityKey.getIdentifier());
                processingState.registerEntityKey(entityKey);
            }
            return super.readRow(resultSet, context);
        }

        @Override
        protected Object readLogicalRow(ResultSet resultSet, ResultSetProcessingContextImpl context) throws SQLException {
            return this.rootReturnReader.read(resultSet, context);
        }
    }

    private static class EntityLoaderReaderCollectorImpl
    extends AbstractLoadQueryDetails.ReaderCollectorImpl {
        private final EntityReturnReader entityReturnReader;

        public EntityLoaderReaderCollectorImpl(EntityReturnReader entityReturnReader, EntityReferenceInitializer entityReferenceInitializer) {
            this.entityReturnReader = entityReturnReader;
            this.add(entityReferenceInitializer);
        }

        @Override
        public RowReader buildRowReader() {
            return new EntityLoaderRowReader(this);
        }

        @Override
        public EntityReturnReader getReturnReader() {
            return this.entityReturnReader;
        }
    }
}

