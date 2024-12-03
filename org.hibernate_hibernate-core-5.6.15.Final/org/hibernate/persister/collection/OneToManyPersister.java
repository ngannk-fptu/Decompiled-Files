/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.persister.collection;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Set;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.access.CollectionDataAccess;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.jdbc.batch.internal.BasicBatchKey;
import org.hibernate.engine.spi.LoadQueryInfluencers;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.engine.spi.SubselectFetch;
import org.hibernate.internal.FilterAliasGenerator;
import org.hibernate.internal.util.collections.ArrayHelper;
import org.hibernate.jdbc.Expectation;
import org.hibernate.jdbc.Expectations;
import org.hibernate.loader.collection.BatchingCollectionInitializerBuilder;
import org.hibernate.loader.collection.CollectionInitializer;
import org.hibernate.loader.collection.SubselectOneToManyLoader;
import org.hibernate.loader.entity.CollectionElementLoader;
import org.hibernate.mapping.Collection;
import org.hibernate.persister.collection.AbstractCollectionPersister;
import org.hibernate.persister.entity.Joinable;
import org.hibernate.persister.entity.OuterJoinLoadable;
import org.hibernate.persister.spi.PersisterCreationContext;
import org.hibernate.pretty.MessageHelper;
import org.hibernate.sql.Update;

public class OneToManyPersister
extends AbstractCollectionPersister {
    private final boolean cascadeDeleteEnabled;
    private final boolean keyIsNullable;
    private final boolean keyIsUpdateable;
    private BasicBatchKey deleteRowBatchKey;
    private BasicBatchKey insertRowBatchKey;

    @Override
    protected boolean isRowDeleteEnabled() {
        return this.keyIsUpdateable && this.keyIsNullable;
    }

    @Override
    protected boolean isRowInsertEnabled() {
        return this.keyIsUpdateable;
    }

    @Override
    public boolean isCascadeDeleteEnabled() {
        return this.cascadeDeleteEnabled;
    }

    public OneToManyPersister(Collection collectionBinding, CollectionDataAccess cacheAccessStrategy, PersisterCreationContext creationContext) throws MappingException, CacheException {
        super(collectionBinding, cacheAccessStrategy, creationContext);
        this.cascadeDeleteEnabled = collectionBinding.getKey().isCascadeDeleteEnabled() && creationContext.getSessionFactory().getDialect().supportsCascadeDelete();
        this.keyIsNullable = collectionBinding.getKey().isNullable();
        this.keyIsUpdateable = collectionBinding.getKey().isUpdateable();
    }

    @Override
    protected String generateDeleteString() {
        Update update = this.createUpdate().setTableName(this.qualifiedTableName).addColumns(this.keyColumnNames, "null");
        if (this.hasIndex && !this.indexContainsFormula) {
            for (int i = 0; i < this.indexColumnNames.length; ++i) {
                if (!this.indexColumnIsSettable[i]) continue;
                update.addColumn(this.indexColumnNames[i], "null");
            }
        }
        update.addPrimaryKeyColumns(this.keyColumnNames);
        if (this.hasWhere) {
            update.setWhere(this.sqlWhereString);
        }
        if (this.getFactory().getSessionFactoryOptions().isCommentsEnabled()) {
            update.setComment("delete one-to-many " + this.getRole());
        }
        return update.toStatementString();
    }

    @Override
    protected String generateInsertRowString() {
        Update update = this.createUpdate().setTableName(this.qualifiedTableName).addColumns(this.keyColumnNames);
        if (this.hasIndex && !this.indexContainsFormula) {
            for (int i = 0; i < this.indexColumnNames.length; ++i) {
                if (!this.indexColumnIsSettable[i]) continue;
                update.addColumn(this.indexColumnNames[i]);
            }
        }
        if (this.getFactory().getSessionFactoryOptions().isCommentsEnabled()) {
            update.setComment("create one-to-many row " + this.getRole());
        }
        return update.addPrimaryKeyColumns(this.elementColumnNames, this.elementColumnWriters).toStatementString();
    }

    @Override
    protected String generateUpdateRowString() {
        Update update = this.createUpdate().setTableName(this.qualifiedTableName);
        if (this.hasIndex && !this.indexContainsFormula) {
            for (int i = 0; i < this.indexColumnNames.length; ++i) {
                if (!this.indexColumnIsSettable[i]) continue;
                update.addColumn(this.indexColumnNames[i]);
            }
        }
        update.addPrimaryKeyColumns(this.elementColumnNames, this.elementColumnIsSettable, this.elementColumnWriters);
        if (this.hasIdentifier) {
            update.addPrimaryKeyColumns(new String[]{this.identifierColumnName});
        }
        return update.toStatementString();
    }

    @Override
    protected String generateDeleteRowString() {
        Update update = this.createUpdate().setTableName(this.qualifiedTableName).addColumns(this.keyColumnNames, "null");
        if (this.hasIndex && !this.indexContainsFormula) {
            for (int i = 0; i < this.indexColumnNames.length; ++i) {
                if (!this.indexColumnIsSettable[i]) continue;
                update.addColumn(this.indexColumnNames[i], "null");
            }
        }
        if (this.getFactory().getSessionFactoryOptions().isCommentsEnabled()) {
            update.setComment("delete one-to-many row " + this.getRole());
        }
        String[] rowSelectColumnNames = ArrayHelper.join(this.keyColumnNames, this.elementColumnNames);
        return update.addPrimaryKeyColumns(rowSelectColumnNames).toStatementString();
    }

    @Override
    public void recreate(PersistentCollection collection, Serializable id, SharedSessionContractImplementor session) throws HibernateException {
        super.recreate(collection, id, session);
        this.writeIndex(collection, collection.entries(this), id, true, session);
    }

    @Override
    public void insertRows(PersistentCollection collection, Serializable id, SharedSessionContractImplementor session) throws HibernateException {
        super.insertRows(collection, id, session);
        this.writeIndex(collection, collection.entries(this), id, true, session);
    }

    @Override
    protected void doProcessQueuedOps(PersistentCollection collection, Serializable id, SharedSessionContractImplementor session) throws HibernateException {
        this.writeIndex(collection, collection.queuedAdditionIterator(), id, false, session);
    }

    private void writeIndex(PersistentCollection collection, Iterator entries, Serializable id, boolean resetIndex, SharedSessionContractImplementor session) {
        block18: {
            if (this.isInverse && this.hasIndex && !this.indexContainsFormula && ArrayHelper.countTrue(this.indexColumnIsSettable) > 0) {
                try {
                    if (!entries.hasNext()) break block18;
                    int nextIndex = resetIndex ? 0 : this.getSize(id, session);
                    Expectation expectation = Expectations.appropriateExpectation(this.getUpdateCheckStyle());
                    while (entries.hasNext()) {
                        Object entry = entries.next();
                        if (entry != null && collection.entryExists(entry, nextIndex)) {
                            int offset = 1;
                            PreparedStatement st = null;
                            boolean callable = this.isUpdateCallable();
                            boolean useBatch = expectation.canBeBatched();
                            String sql = this.getSQLUpdateRowString();
                            if (useBatch) {
                                if (this.recreateBatchKey == null) {
                                    this.recreateBatchKey = new BasicBatchKey(this.getRole() + "#RECREATE", expectation);
                                }
                                st = session.getJdbcCoordinator().getBatch(this.recreateBatchKey).getBatchStatement(sql, callable);
                            } else {
                                st = session.getJdbcCoordinator().getStatementPreparer().prepareStatement(sql, callable);
                            }
                            try {
                                offset += expectation.prepare(st);
                                if (this.hasIdentifier) {
                                    offset = this.writeIdentifier(st, collection.getIdentifier(entry, nextIndex), offset, session);
                                }
                                offset = this.writeIndex(st, collection.getIndex(entry, nextIndex, this), offset, session);
                                offset = this.writeElement(st, collection.getElement(entry), offset, session);
                                if (useBatch) {
                                    session.getJdbcCoordinator().getBatch(this.recreateBatchKey).addToBatch();
                                } else {
                                    expectation.verifyOutcome(session.getJdbcCoordinator().getResultSetReturn().executeUpdate(st), st, -1, sql);
                                }
                            }
                            catch (SQLException sqle) {
                                if (useBatch) {
                                    session.getJdbcCoordinator().abortBatch();
                                }
                                throw sqle;
                            }
                            finally {
                                if (!useBatch) {
                                    session.getJdbcCoordinator().getResourceRegistry().release(st);
                                    session.getJdbcCoordinator().afterStatementExecution();
                                }
                            }
                        }
                        ++nextIndex;
                    }
                }
                catch (SQLException sqle) {
                    throw this.sqlExceptionHelper.convert(sqle, "could not update collection: " + MessageHelper.collectionInfoString(this, collection, id, session), this.getSQLUpdateRowString());
                }
            }
        }
    }

    @Override
    public boolean consumesEntityAlias() {
        return true;
    }

    @Override
    public boolean consumesCollectionAlias() {
        return true;
    }

    @Override
    public boolean isOneToMany() {
        return true;
    }

    @Override
    public boolean isManyToMany() {
        return false;
    }

    @Override
    protected int doUpdateRows(Serializable id, PersistentCollection collection, SharedSessionContractImplementor session) {
        try {
            Object entry;
            boolean useBatch;
            int count = 0;
            if (this.isRowDeleteEnabled()) {
                Expectation deleteExpectation = Expectations.appropriateExpectation(this.getDeleteCheckStyle());
                useBatch = deleteExpectation.canBeBatched();
                if (useBatch && this.deleteRowBatchKey == null) {
                    this.deleteRowBatchKey = new BasicBatchKey(this.getRole() + "#DELETEROW", deleteExpectation);
                }
                String sql = this.getSQLDeleteRowString();
                PreparedStatement st = null;
                try {
                    int i = 0;
                    Iterator entries = collection.entries(this);
                    int offset = 1;
                    while (entries.hasNext()) {
                        entry = entries.next();
                        if (collection.needsUpdating(entry, i, this.elementType)) {
                            st = useBatch ? session.getJdbcCoordinator().getBatch(this.deleteRowBatchKey).getBatchStatement(sql, this.isDeleteCallable()) : session.getJdbcCoordinator().getStatementPreparer().prepareStatement(sql, this.isDeleteCallable());
                            int loc = this.writeKey(st, id, offset, session);
                            this.writeElementToWhere(st, collection.getSnapshotElement(entry, i), loc, session);
                            if (useBatch) {
                                session.getJdbcCoordinator().getBatch(this.deleteRowBatchKey).addToBatch();
                            } else {
                                deleteExpectation.verifyOutcome(session.getJdbcCoordinator().getResultSetReturn().executeUpdate(st), st, -1, sql);
                            }
                            ++count;
                        }
                        ++i;
                    }
                }
                catch (SQLException e) {
                    if (useBatch) {
                        session.getJdbcCoordinator().abortBatch();
                    }
                    throw e;
                }
                finally {
                    if (!useBatch) {
                        session.getJdbcCoordinator().getResourceRegistry().release(st);
                        session.getJdbcCoordinator().afterStatementExecution();
                    }
                }
            }
            if (this.isRowInsertEnabled()) {
                Expectation insertExpectation = Expectations.appropriateExpectation(this.getInsertCheckStyle());
                useBatch = insertExpectation.canBeBatched();
                boolean callable = this.isInsertCallable();
                if (useBatch && this.insertRowBatchKey == null) {
                    this.insertRowBatchKey = new BasicBatchKey(this.getRole() + "#INSERTROW", insertExpectation);
                }
                String sql = this.getSQLInsertRowString();
                PreparedStatement st = null;
                try {
                    int i = 0;
                    Iterator entries = collection.entries(this);
                    while (entries.hasNext()) {
                        entry = entries.next();
                        int offset = 1;
                        if (collection.needsUpdating(entry, i, this.elementType)) {
                            st = useBatch ? session.getJdbcCoordinator().getBatch(this.insertRowBatchKey).getBatchStatement(sql, callable) : session.getJdbcCoordinator().getStatementPreparer().prepareStatement(sql, callable);
                            int loc = this.writeKey(st, id, offset += insertExpectation.prepare(st), session);
                            if (this.hasIndex && !this.indexContainsFormula) {
                                loc = this.writeIndexToWhere(st, collection.getIndex(entry, i, this), loc, session);
                            }
                            this.writeElementToWhere(st, collection.getElement(entry), loc, session);
                            if (useBatch) {
                                session.getJdbcCoordinator().getBatch(this.insertRowBatchKey).addToBatch();
                            } else {
                                insertExpectation.verifyOutcome(session.getJdbcCoordinator().getResultSetReturn().executeUpdate(st), st, -1, sql);
                            }
                            ++count;
                        }
                        ++i;
                    }
                }
                catch (SQLException sqle) {
                    if (useBatch) {
                        session.getJdbcCoordinator().abortBatch();
                    }
                    throw sqle;
                }
                finally {
                    if (!useBatch) {
                        session.getJdbcCoordinator().getResourceRegistry().release(st);
                        session.getJdbcCoordinator().afterStatementExecution();
                    }
                }
            }
            return count;
        }
        catch (SQLException sqle) {
            throw this.getFactory().getSQLExceptionHelper().convert(sqle, "could not update collection rows: " + MessageHelper.collectionInfoString(this, collection, id, session), this.getSQLInsertRowString());
        }
    }

    @Override
    public String selectFragment(Joinable rhs, String rhsAlias, String lhsAlias, String entitySuffix, String collectionSuffix, boolean includeCollectionColumns) {
        StringBuilder buf = new StringBuilder();
        if (includeCollectionColumns) {
            buf.append(this.selectFragment(lhsAlias, collectionSuffix)).append(", ");
        }
        OuterJoinLoadable ojl = (OuterJoinLoadable)this.getElementPersister();
        return buf.append(ojl.selectFragment(lhsAlias, entitySuffix)).toString();
    }

    @Override
    protected CollectionInitializer createCollectionInitializer(LoadQueryInfluencers loadQueryInfluencers) throws MappingException {
        return BatchingCollectionInitializerBuilder.getBuilder(this.getFactory()).createBatchingOneToManyInitializer(this, this.batchSize, this.getFactory(), loadQueryInfluencers);
    }

    @Override
    public String fromJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses) {
        return ((Joinable)((Object)this.getElementPersister())).fromJoinFragment(alias, innerJoin, includeSubclasses);
    }

    @Override
    public String fromJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses, Set<String> treatAsDeclarations) {
        return ((Joinable)((Object)this.getElementPersister())).fromJoinFragment(alias, innerJoin, includeSubclasses, treatAsDeclarations);
    }

    @Override
    public String whereJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses) {
        return ((Joinable)((Object)this.getElementPersister())).whereJoinFragment(alias, innerJoin, includeSubclasses);
    }

    @Override
    public String whereJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses, Set<String> treatAsDeclarations) {
        return ((Joinable)((Object)this.getElementPersister())).whereJoinFragment(alias, innerJoin, includeSubclasses, treatAsDeclarations);
    }

    @Override
    public String getTableName() {
        return ((Joinable)((Object)this.getElementPersister())).getTableName();
    }

    @Override
    public String filterFragment(String alias) throws MappingException {
        String result = super.filterFragment(alias);
        if (this.getElementPersister() instanceof Joinable) {
            result = result + ((Joinable)((Object)this.getElementPersister())).oneToManyFilterFragment(alias);
        }
        return result;
    }

    @Override
    protected String filterFragment(String alias, Set<String> treatAsDeclarations) throws MappingException {
        String result = super.filterFragment(alias);
        if (this.getElementPersister() instanceof Joinable) {
            result = result + ((Joinable)((Object)this.getElementPersister())).oneToManyFilterFragment(alias, treatAsDeclarations);
        }
        return result;
    }

    @Override
    protected CollectionInitializer createSubselectInitializer(SubselectFetch subselect, SharedSessionContractImplementor session) {
        return new SubselectOneToManyLoader(this, subselect.toSubselectString(this.getCollectionType().getLHSPropertyName()), subselect.getResult(), subselect.getQueryParameters(), subselect.getNamedParameterLocMap(), session.getFactory(), session.getLoadQueryInfluencers());
    }

    @Override
    public Object getElementByIndex(Serializable key, Object index, SharedSessionContractImplementor session, Object owner) {
        return new CollectionElementLoader(this, this.getFactory(), session.getLoadQueryInfluencers()).loadElement(session, key, this.incrementIndexByBase(index));
    }

    @Override
    public FilterAliasGenerator getFilterAliasGenerator(String rootAlias) {
        return this.getElementPersister().getFilterAliasGenerator(rootAlias);
    }
}

