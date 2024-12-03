/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.persister.collection;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
import org.hibernate.internal.StaticFilterAliasGenerator;
import org.hibernate.internal.util.collections.ArrayHelper;
import org.hibernate.jdbc.Expectation;
import org.hibernate.jdbc.Expectations;
import org.hibernate.loader.collection.BatchingCollectionInitializerBuilder;
import org.hibernate.loader.collection.CollectionInitializer;
import org.hibernate.loader.collection.SubselectCollectionLoader;
import org.hibernate.mapping.Collection;
import org.hibernate.persister.collection.AbstractCollectionPersister;
import org.hibernate.persister.entity.Joinable;
import org.hibernate.persister.spi.PersisterCreationContext;
import org.hibernate.pretty.MessageHelper;
import org.hibernate.sql.Delete;
import org.hibernate.sql.Insert;
import org.hibernate.sql.SelectFragment;
import org.hibernate.sql.Update;
import org.hibernate.type.AssociationType;

public class BasicCollectionPersister
extends AbstractCollectionPersister {
    private BasicBatchKey updateBatchKey;

    @Override
    public boolean isCascadeDeleteEnabled() {
        return false;
    }

    public BasicCollectionPersister(Collection collectionBinding, CollectionDataAccess cacheAccessStrategy, PersisterCreationContext creationContext) throws MappingException, CacheException {
        super(collectionBinding, cacheAccessStrategy, creationContext);
    }

    @Override
    protected String generateDeleteString() {
        Delete delete = this.createDelete().setTableName(this.qualifiedTableName).addPrimaryKeyColumns(this.keyColumnNames);
        if (this.hasWhere) {
            delete.setWhere(this.sqlWhereString);
        }
        if (this.getFactory().getSessionFactoryOptions().isCommentsEnabled()) {
            delete.setComment("delete collection " + this.getRole());
        }
        return delete.toStatementString();
    }

    @Override
    protected String generateInsertRowString() {
        Insert insert = this.createInsert().setTableName(this.qualifiedTableName).addColumns(this.keyColumnNames);
        if (this.hasIdentifier) {
            insert.addColumn(this.identifierColumnName);
        }
        if (this.hasIndex) {
            insert.addColumns(this.indexColumnNames, this.indexColumnIsSettable);
        }
        if (this.getFactory().getSessionFactoryOptions().isCommentsEnabled()) {
            insert.setComment("insert collection row " + this.getRole());
        }
        insert.addColumns(this.elementColumnNames, this.elementColumnIsSettable, this.elementColumnWriters);
        return insert.toStatementString();
    }

    @Override
    protected String generateUpdateRowString() {
        Update update = this.createUpdate().setTableName(this.qualifiedTableName);
        update.addColumns(this.elementColumnNames, this.elementColumnIsSettable, this.elementColumnWriters);
        if (this.hasIdentifier) {
            update.addPrimaryKeyColumns(new String[]{this.identifierColumnName});
        } else if (this.hasIndex && !this.indexContainsFormula) {
            update.addPrimaryKeyColumns(ArrayHelper.join(this.keyColumnNames, this.indexColumnNames));
        } else {
            update.addPrimaryKeyColumns(this.keyColumnNames);
            update.addPrimaryKeyColumns(this.elementColumnNames, this.elementColumnIsInPrimaryKey, this.elementColumnWriters);
        }
        if (this.getFactory().getSessionFactoryOptions().isCommentsEnabled()) {
            update.setComment("update collection row " + this.getRole());
        }
        return update.toStatementString();
    }

    @Override
    protected void doProcessQueuedOps(PersistentCollection collection, Serializable id, SharedSessionContractImplementor session) {
    }

    @Override
    protected String generateDeleteRowString() {
        Delete delete = this.createDelete().setTableName(this.qualifiedTableName);
        if (this.hasIdentifier) {
            delete.addPrimaryKeyColumns(new String[]{this.identifierColumnName});
        } else if (this.hasIndex && !this.indexContainsFormula) {
            delete.addPrimaryKeyColumns(ArrayHelper.join(this.keyColumnNames, this.indexColumnNames));
        } else {
            delete.addPrimaryKeyColumns(this.keyColumnNames);
            delete.addPrimaryKeyColumns(this.elementColumnNames, this.elementColumnIsInPrimaryKey, this.elementColumnWriters);
        }
        if (this.getFactory().getSessionFactoryOptions().isCommentsEnabled()) {
            delete.setComment("delete collection row " + this.getRole());
        }
        return delete.toStatementString();
    }

    @Override
    public boolean consumesEntityAlias() {
        return false;
    }

    @Override
    public boolean consumesCollectionAlias() {
        return true;
    }

    @Override
    public boolean isOneToMany() {
        return false;
    }

    @Override
    public boolean isManyToMany() {
        return this.elementType.isEntityType();
    }

    @Override
    protected int doUpdateRows(Serializable id, PersistentCollection collection, SharedSessionContractImplementor session) throws HibernateException {
        if (ArrayHelper.isAllFalse(this.elementColumnIsSettable)) {
            return 0;
        }
        try {
            Expectation expectation = Expectations.appropriateExpectation(this.getUpdateCheckStyle());
            boolean callable = this.isUpdateCallable();
            int jdbcBatchSizeToUse = session.getConfiguredJdbcBatchSize();
            boolean useBatch = expectation.canBeBatched() && jdbcBatchSizeToUse > 1;
            Iterator entries = collection.entries(this);
            ArrayList elements = new ArrayList();
            while (entries.hasNext()) {
                elements.add(entries.next());
            }
            String sql = this.getSQLUpdateRowString();
            int count = 0;
            if (collection.isElementRemoved()) {
                for (int i = elements.size() - 1; i >= 0; --i) {
                    count = this.doUpdateRow(id, collection, session, expectation, callable, useBatch, elements, sql, count, i);
                }
            } else {
                for (int i = 0; i < elements.size(); ++i) {
                    count = this.doUpdateRow(id, collection, session, expectation, callable, useBatch, elements, sql, count, i);
                }
            }
            return count;
        }
        catch (SQLException sqle) {
            throw session.getJdbcServices().getSqlExceptionHelper().convert(sqle, "could not update collection rows: " + MessageHelper.collectionInfoString(this, collection, id, session), this.getSQLUpdateRowString());
        }
    }

    private int doUpdateRow(Serializable id, PersistentCollection collection, SharedSessionContractImplementor session, Expectation expectation, boolean callable, boolean useBatch, List elements, String sql, int count, int i) throws SQLException {
        Object entry = elements.get(i);
        if (collection.needsUpdating(entry, i, this.elementType)) {
            PreparedStatement st;
            int offset = 1;
            if (useBatch) {
                if (this.updateBatchKey == null) {
                    this.updateBatchKey = new BasicBatchKey(this.getRole() + "#UPDATE", expectation);
                }
                st = session.getJdbcCoordinator().getBatch(this.updateBatchKey).getBatchStatement(sql, callable);
            } else {
                st = session.getJdbcCoordinator().getStatementPreparer().prepareStatement(sql, callable);
            }
            try {
                int loc = this.writeElement(st, collection.getElement(entry), offset += expectation.prepare(st), session);
                if (this.hasIdentifier) {
                    this.writeIdentifier(st, collection.getIdentifier(entry, i), loc, session);
                } else {
                    loc = this.writeKey(st, id, loc, session);
                    if (this.hasIndex && !this.indexContainsFormula) {
                        this.writeIndexToWhere(st, collection.getIndex(entry, i, this), loc, session);
                    } else {
                        this.writeElementToWhere(st, collection.getSnapshotElement(entry, i), loc, session);
                    }
                }
                if (useBatch) {
                    session.getJdbcCoordinator().getBatch(this.updateBatchKey).addToBatch();
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
                    session.getJdbcCoordinator().getLogicalConnection().getResourceRegistry().release(st);
                    session.getJdbcCoordinator().afterStatementExecution();
                }
            }
            ++count;
        }
        return count;
    }

    @Override
    public String selectFragment(Joinable rhs, String rhsAlias, String lhsAlias, String entitySuffix, String collectionSuffix, boolean includeCollectionColumns) {
        AssociationType elementType;
        if (rhs != null && this.isManyToMany() && !rhs.isCollection() && rhs.equals((elementType = (AssociationType)this.getElementType()).getAssociatedJoinable(this.getFactory()))) {
            return this.manyToManySelectFragment(rhs, rhsAlias, lhsAlias, collectionSuffix);
        }
        return includeCollectionColumns ? this.selectFragment(lhsAlias, collectionSuffix) : "";
    }

    private String manyToManySelectFragment(Joinable rhs, String rhsAlias, String lhsAlias, String collectionSuffix) {
        SelectFragment frag = this.generateSelectFragment(lhsAlias, collectionSuffix);
        String[] elementColumnNames = rhs.getKeyColumnNames();
        frag.addColumns(rhsAlias, elementColumnNames, this.elementColumnAliases);
        this.appendIndexColumns(frag, lhsAlias);
        this.appendIdentifierColumns(frag, lhsAlias);
        return frag.toFragmentString().substring(2);
    }

    @Override
    protected CollectionInitializer createCollectionInitializer(LoadQueryInfluencers loadQueryInfluencers) throws MappingException {
        return BatchingCollectionInitializerBuilder.getBuilder(this.getFactory()).createBatchingCollectionInitializer(this, this.batchSize, this.getFactory(), loadQueryInfluencers);
    }

    @Override
    public String fromJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses) {
        return "";
    }

    @Override
    public String fromJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses, Set<String> treatAsDeclarations) {
        return "";
    }

    @Override
    public String whereJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses) {
        return "";
    }

    @Override
    public String whereJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses, Set<String> treatAsDeclarations) {
        return "";
    }

    @Override
    protected CollectionInitializer createSubselectInitializer(SubselectFetch subselect, SharedSessionContractImplementor session) {
        return new SubselectCollectionLoader(this, subselect.toSubselectString(this.getCollectionType().getLHSPropertyName()), subselect.getResult(), subselect.getQueryParameters(), subselect.getNamedParameterLocMap(), session.getFactory(), session.getLoadQueryInfluencers());
    }

    @Override
    public FilterAliasGenerator getFilterAliasGenerator(String rootAlias) {
        return new StaticFilterAliasGenerator(rootAlias);
    }
}

