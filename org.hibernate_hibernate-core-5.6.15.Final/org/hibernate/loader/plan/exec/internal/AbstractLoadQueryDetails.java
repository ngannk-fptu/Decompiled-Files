/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.exec.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.loader.MultipleBagFetchException;
import org.hibernate.loader.plan.build.spi.LoadPlanTreePrinter;
import org.hibernate.loader.plan.exec.internal.AliasResolutionContextImpl;
import org.hibernate.loader.plan.exec.internal.FetchStats;
import org.hibernate.loader.plan.exec.internal.LoadQueryJoinAndFetchProcessor;
import org.hibernate.loader.plan.exec.process.spi.CollectionReferenceInitializer;
import org.hibernate.loader.plan.exec.process.spi.EntityReferenceInitializer;
import org.hibernate.loader.plan.exec.process.spi.ReaderCollector;
import org.hibernate.loader.plan.exec.process.spi.ResultSetProcessor;
import org.hibernate.loader.plan.exec.process.spi.ResultSetProcessorResolver;
import org.hibernate.loader.plan.exec.query.internal.SelectStatementBuilder;
import org.hibernate.loader.plan.exec.query.spi.QueryBuildingParameters;
import org.hibernate.loader.plan.exec.spi.AliasResolutionContext;
import org.hibernate.loader.plan.exec.spi.LoadQueryDetails;
import org.hibernate.loader.plan.spi.CollectionAttributeFetch;
import org.hibernate.loader.plan.spi.CollectionReturn;
import org.hibernate.loader.plan.spi.FetchSource;
import org.hibernate.loader.plan.spi.LoadPlan;
import org.hibernate.loader.plan.spi.QuerySpace;
import org.hibernate.loader.plan.spi.Return;
import org.hibernate.sql.ConditionFragment;
import org.hibernate.sql.DisjunctionFragment;
import org.hibernate.sql.InFragment;

public abstract class AbstractLoadQueryDetails
implements LoadQueryDetails {
    private final LoadPlan loadPlan;
    private final String[] keyColumnNames;
    private final Return rootReturn;
    private final LoadQueryJoinAndFetchProcessor queryProcessor;
    private String sqlStatement;
    private ResultSetProcessor resultSetProcessor;

    protected AbstractLoadQueryDetails(LoadPlan loadPlan, AliasResolutionContextImpl aliasResolutionContext, QueryBuildingParameters buildingParameters, String[] keyColumnNames, Return rootReturn, SessionFactoryImplementor factory) {
        this.keyColumnNames = keyColumnNames;
        this.rootReturn = rootReturn;
        this.loadPlan = loadPlan;
        this.queryProcessor = new LoadQueryJoinAndFetchProcessor(aliasResolutionContext, buildingParameters, factory);
    }

    protected QuerySpace getQuerySpace(String querySpaceUid) {
        return this.loadPlan.getQuerySpaces().getQuerySpaceByUid(querySpaceUid);
    }

    @Override
    public String getSqlStatement() {
        return this.sqlStatement;
    }

    @Override
    public ResultSetProcessor getResultSetProcessor() {
        return this.resultSetProcessor;
    }

    protected final Return getRootReturn() {
        return this.rootReturn;
    }

    protected final AliasResolutionContext getAliasResolutionContext() {
        return this.queryProcessor.getAliasResolutionContext();
    }

    protected final QueryBuildingParameters getQueryBuildingParameters() {
        return this.queryProcessor.getQueryBuildingParameters();
    }

    protected final SessionFactoryImplementor getSessionFactory() {
        return this.queryProcessor.getSessionFactory();
    }

    protected LoadPlan getLoadPlan() {
        return this.loadPlan;
    }

    protected String[] getKeyColumnNames() {
        return this.keyColumnNames;
    }

    protected void generate() {
        this.generate(ResultSetProcessorResolver.DEFAULT);
    }

    protected void generate(ResultSetProcessorResolver resultSetProcessorResolver) {
        CollectionReturn collectionReturn;
        SelectStatementBuilder select = new SelectStatementBuilder(this.queryProcessor.getSessionFactory().getDialect());
        this.applyRootReturnTableFragments(select);
        if (this.shouldApplyRootReturnFilterBeforeKeyRestriction()) {
            this.applyRootReturnFilterRestrictions(select);
            AbstractLoadQueryDetails.applyKeyRestriction(select, this.getRootTableAlias(), this.keyColumnNames, this.getQueryBuildingParameters().getBatchSize());
        } else {
            AbstractLoadQueryDetails.applyKeyRestriction(select, this.getRootTableAlias(), this.keyColumnNames, this.getQueryBuildingParameters().getBatchSize());
            this.applyRootReturnFilterRestrictions(select);
        }
        this.applyRootReturnWhereJoinRestrictions(select);
        this.applyRootReturnOrderByFragments(select);
        this.applyRootReturnSelectFragments(select);
        this.queryProcessor.processQuerySpaceJoins(this.getRootQuerySpace(), select);
        FetchStats fetchStats = null;
        if (FetchSource.class.isInstance(this.rootReturn)) {
            fetchStats = this.queryProcessor.processFetches((FetchSource)((Object)this.rootReturn), select, this.getReaderCollector());
        } else if (CollectionReturn.class.isInstance(this.rootReturn) && (collectionReturn = (CollectionReturn)this.rootReturn).getElementGraph() != null) {
            fetchStats = this.queryProcessor.processFetches(collectionReturn.getElementGraph(), select, this.getReaderCollector());
        }
        if (fetchStats != null && fetchStats.getJoinedBagAttributeFetches().size() > 1) {
            ArrayList<String> bagRoles = new ArrayList<String>();
            for (CollectionAttributeFetch bagFetch : fetchStats.getJoinedBagAttributeFetches()) {
                bagRoles.add(bagFetch.getCollectionPersister().getRole());
            }
            throw new MultipleBagFetchException(bagRoles);
        }
        LoadPlanTreePrinter.INSTANCE.logTree(this.loadPlan, this.queryProcessor.getAliasResolutionContext());
        this.sqlStatement = select.toStatementString();
        this.resultSetProcessor = resultSetProcessorResolver.resolveResultSetProcessor(this.loadPlan, this.queryProcessor.getAliasResolutionContext(), this.getReaderCollector(), this.shouldUseOptionalEntityInstance(), this.isSubselectLoadingEnabled(fetchStats));
    }

    protected abstract boolean isSubselectLoadingEnabled(FetchStats var1);

    protected abstract boolean shouldUseOptionalEntityInstance();

    protected abstract ReaderCollector getReaderCollector();

    protected abstract QuerySpace getRootQuerySpace();

    protected abstract String getRootTableAlias();

    protected abstract boolean shouldApplyRootReturnFilterBeforeKeyRestriction();

    protected abstract void applyRootReturnSelectFragments(SelectStatementBuilder var1);

    protected abstract void applyRootReturnTableFragments(SelectStatementBuilder var1);

    protected abstract void applyRootReturnFilterRestrictions(SelectStatementBuilder var1);

    protected abstract void applyRootReturnWhereJoinRestrictions(SelectStatementBuilder var1);

    protected abstract void applyRootReturnOrderByFragments(SelectStatementBuilder var1);

    private static void applyKeyRestriction(SelectStatementBuilder select, String alias, String[] keyColumnNames, int batchSize) {
        if (keyColumnNames.length == 1) {
            InFragment in = new InFragment().setColumn(alias, keyColumnNames[0]);
            for (int i = 0; i < batchSize; ++i) {
                in.addValue("?");
            }
            select.appendRestrictions(in.toFragmentString());
        } else {
            ConditionFragment keyRestrictionBuilder = new ConditionFragment().setTableAlias(alias).setCondition(keyColumnNames, "?");
            String keyRestrictionFragment = keyRestrictionBuilder.toFragmentString();
            StringBuilder restrictions = new StringBuilder();
            if (batchSize == 1) {
                restrictions.append(keyRestrictionFragment);
            } else {
                restrictions.append('(');
                DisjunctionFragment df = new DisjunctionFragment();
                for (int i = 0; i < batchSize; ++i) {
                    df.addCondition(keyRestrictionFragment);
                }
                restrictions.append(df.toFragmentString());
                restrictions.append(')');
            }
            select.appendRestrictions(restrictions.toString());
        }
    }

    protected static abstract class ReaderCollectorImpl
    implements ReaderCollector {
        private List<EntityReferenceInitializer> entityReferenceInitializers;
        private List<CollectionReferenceInitializer> arrayReferenceInitializers;
        private List<CollectionReferenceInitializer> collectionReferenceInitializers;

        protected ReaderCollectorImpl() {
        }

        @Override
        public void add(CollectionReferenceInitializer collectionReferenceInitializer) {
            if (collectionReferenceInitializer.getCollectionReference().getCollectionPersister().isArray()) {
                this.arrayReferenceInitializers = ReaderCollectorImpl.addTo(this.arrayReferenceInitializers, collectionReferenceInitializer);
            } else {
                this.collectionReferenceInitializers = ReaderCollectorImpl.addTo(this.collectionReferenceInitializers, collectionReferenceInitializer);
            }
        }

        private static <V> List<V> addTo(List<V> host, V element) {
            List<V> output = host;
            if (output == null) {
                output = Collections.singletonList(element);
            } else if (output.size() == 1) {
                output = new ArrayList<V>(output);
                output.add(element);
            } else {
                output.add(element);
            }
            return output;
        }

        @Override
        public void add(EntityReferenceInitializer entityReferenceInitializer) {
            this.entityReferenceInitializers = ReaderCollectorImpl.addTo(this.entityReferenceInitializers, entityReferenceInitializer);
        }

        @Override
        public final List<EntityReferenceInitializer> getEntityReferenceInitializers() {
            return this.entityReferenceInitializers == null ? Collections.EMPTY_LIST : this.entityReferenceInitializers;
        }

        @Override
        public List<CollectionReferenceInitializer> getArrayReferenceInitializers() {
            return this.arrayReferenceInitializers == null ? Collections.EMPTY_LIST : this.arrayReferenceInitializers;
        }

        @Override
        public List<CollectionReferenceInitializer> getNonArrayCollectionReferenceInitializers() {
            return this.collectionReferenceInitializers == null ? Collections.EMPTY_LIST : this.collectionReferenceInitializers;
        }
    }
}

