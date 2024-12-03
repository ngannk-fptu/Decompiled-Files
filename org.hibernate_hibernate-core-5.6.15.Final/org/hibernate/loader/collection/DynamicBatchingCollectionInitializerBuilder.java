/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.collection;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.dialect.pagination.LimitHelper;
import org.hibernate.engine.spi.LoadQueryInfluencers;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.RowSelection;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.internal.util.collections.ArrayHelper;
import org.hibernate.loader.JoinWalker;
import org.hibernate.loader.Loader;
import org.hibernate.loader.collection.BasicCollectionJoinWalker;
import org.hibernate.loader.collection.BasicCollectionLoader;
import org.hibernate.loader.collection.BatchingCollectionInitializer;
import org.hibernate.loader.collection.BatchingCollectionInitializerBuilder;
import org.hibernate.loader.collection.CollectionInitializer;
import org.hibernate.loader.collection.CollectionLoader;
import org.hibernate.loader.collection.OneToManyJoinWalker;
import org.hibernate.loader.collection.OneToManyLoader;
import org.hibernate.loader.spi.AfterLoadAction;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.collection.QueryableCollection;
import org.hibernate.pretty.MessageHelper;
import org.hibernate.type.Type;

public class DynamicBatchingCollectionInitializerBuilder
extends BatchingCollectionInitializerBuilder {
    public static final DynamicBatchingCollectionInitializerBuilder INSTANCE = new DynamicBatchingCollectionInitializerBuilder();

    @Override
    protected CollectionInitializer createRealBatchingCollectionInitializer(QueryableCollection persister, int maxBatchSize, SessionFactoryImplementor factory, LoadQueryInfluencers influencers) {
        return new DynamicBatchingCollectionInitializer(persister, maxBatchSize, factory, influencers);
    }

    @Override
    protected CollectionInitializer createRealBatchingOneToManyInitializer(QueryableCollection persister, int maxBatchSize, SessionFactoryImplementor factory, LoadQueryInfluencers influencers) {
        return new DynamicBatchingCollectionInitializer(persister, maxBatchSize, factory, influencers);
    }

    private static class DynamicBatchingCollectionLoader
    extends CollectionLoader {
        private final String sqlTemplate;
        private final String alias;

        public DynamicBatchingCollectionLoader(QueryableCollection collectionPersister, SessionFactoryImplementor factory, LoadQueryInfluencers influencers) {
            super(collectionPersister, factory, influencers);
            JoinWalker walker = this.buildJoinWalker(collectionPersister, factory, influencers);
            this.initFromWalker(walker);
            this.sqlTemplate = walker.getSQLString();
            this.alias = StringHelper.generateAlias(collectionPersister.getRole(), 0);
            this.postInstantiate();
            if (LOG.isDebugEnabled()) {
                LOG.debugf("SQL-template for dynamic collection [%s] batch-fetching : %s", collectionPersister.getRole(), this.sqlTemplate);
            }
        }

        private JoinWalker buildJoinWalker(QueryableCollection collectionPersister, SessionFactoryImplementor factory, LoadQueryInfluencers influencers) {
            if (collectionPersister.isOneToMany()) {
                return new OneToManyJoinWalker(collectionPersister, -1, null, factory, influencers){

                    @Override
                    protected StringBuilder whereString(String alias, String[] columnNames, String subselect, int batchSize) {
                        if (subselect != null) {
                            return super.whereString(alias, columnNames, subselect, batchSize);
                        }
                        return StringHelper.buildBatchFetchRestrictionFragment(alias, columnNames, this.getFactory().getDialect());
                    }
                };
            }
            return new BasicCollectionJoinWalker(collectionPersister, -1, null, factory, influencers){

                @Override
                protected StringBuilder whereString(String alias, String[] columnNames, String subselect, int batchSize) {
                    if (subselect != null) {
                        return super.whereString(alias, columnNames, subselect, batchSize);
                    }
                    return StringHelper.buildBatchFetchRestrictionFragment(alias, columnNames, this.getFactory().getDialect());
                }
            };
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public final void doBatchedCollectionLoad(SharedSessionContractImplementor session, Serializable[] ids, Type type) throws HibernateException {
            if (LOG.isDebugEnabled()) {
                LOG.debugf("Batch loading collection: %s", MessageHelper.collectionInfoString(this.getCollectionPersisters()[0], ids, this.getFactory()));
            }
            Object[] idTypes = new Type[ids.length];
            Arrays.fill(idTypes, type);
            QueryParameters queryParameters = new QueryParameters((Type[])idTypes, ids, ids);
            String sql = StringHelper.expandBatchIdPlaceholder(this.sqlTemplate, ids, this.alias, this.collectionPersister().getKeyColumnNames(), session.getJdbcServices().getJdbcEnvironment().getDialect());
            try {
                PersistenceContext persistenceContext = session.getPersistenceContextInternal();
                boolean defaultReadOnlyOrig = persistenceContext.isDefaultReadOnly();
                if (queryParameters.isReadOnlyInitialized()) {
                    persistenceContext.setDefaultReadOnly(queryParameters.isReadOnly());
                } else {
                    queryParameters.setReadOnly(persistenceContext.isDefaultReadOnly());
                }
                persistenceContext.beforeLoad();
                try {
                    try {
                        this.doTheLoad(sql, queryParameters, session);
                    }
                    finally {
                        persistenceContext.afterLoad();
                    }
                    persistenceContext.initializeNonLazyCollections();
                }
                finally {
                    persistenceContext.setDefaultReadOnly(defaultReadOnlyOrig);
                }
            }
            catch (SQLException e) {
                throw session.getJdbcServices().getSqlExceptionHelper().convert(e, "could not initialize a collection batch: " + MessageHelper.collectionInfoString((CollectionPersister)this.collectionPersister(), ids, this.getFactory()), sql);
            }
            LOG.debug("Done batch load");
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void doTheLoad(String sql, QueryParameters queryParameters, SharedSessionContractImplementor session) throws SQLException {
            RowSelection selection = queryParameters.getRowSelection();
            int maxRows = LimitHelper.hasMaxRows(selection) ? selection.getMaxRows() : Integer.MAX_VALUE;
            List<AfterLoadAction> afterLoadActions = Collections.emptyList();
            Loader.SqlStatementWrapper wrapper = this.executeQueryStatement(sql, queryParameters, false, afterLoadActions, session);
            ResultSet rs = wrapper.getResultSet();
            Statement st = wrapper.getStatement();
            try {
                this.processResultSet(rs, queryParameters, session, true, null, maxRows, afterLoadActions);
            }
            finally {
                session.getJdbcCoordinator().getLogicalConnection().getResourceRegistry().release(st);
                session.getJdbcCoordinator().afterStatementExecution();
            }
        }
    }

    public static class DynamicBatchingCollectionInitializer
    extends BatchingCollectionInitializer {
        private final int maxBatchSize;
        private final Loader singleKeyLoader;
        private final DynamicBatchingCollectionLoader batchLoader;

        public DynamicBatchingCollectionInitializer(QueryableCollection collectionPersister, int maxBatchSize, SessionFactoryImplementor factory, LoadQueryInfluencers influencers) {
            super(collectionPersister);
            this.maxBatchSize = maxBatchSize;
            this.singleKeyLoader = collectionPersister.isOneToMany() ? new OneToManyLoader(collectionPersister, 1, factory, influencers) : new BasicCollectionLoader(collectionPersister, 1, factory, influencers);
            this.batchLoader = new DynamicBatchingCollectionLoader(collectionPersister, factory, influencers);
        }

        @Override
        public void initialize(Serializable id, SharedSessionContractImplementor session) throws HibernateException {
            Serializable[] batch = session.getPersistenceContextInternal().getBatchFetchQueue().getCollectionBatch(this.collectionPersister(), id, this.maxBatchSize);
            int numberOfIds = ArrayHelper.countNonNull(batch);
            if (numberOfIds <= 1) {
                this.singleKeyLoader.loadCollection(session, id, this.collectionPersister().getKeyType());
                return;
            }
            Serializable[] idsToLoad = new Serializable[numberOfIds];
            System.arraycopy(batch, 0, idsToLoad, 0, numberOfIds);
            this.batchLoader.doBatchedCollectionLoad(session, idsToLoad, this.collectionPersister().getKeyType());
        }
    }
}

