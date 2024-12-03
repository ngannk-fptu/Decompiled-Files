/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.Maps
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.querydsl.sql.dml;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.querydsl.core.DefaultQueryMetadata;
import com.querydsl.core.FilteredClause;
import com.querydsl.core.JoinType;
import com.querydsl.core.QueryFlag;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.dml.StoreClause;
import com.querydsl.core.support.FetchableQueryBase;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.NullExpression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.util.ResultSetAdapter;
import com.querydsl.sql.ColumnMetadata;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLBindings;
import com.querydsl.sql.SQLListener;
import com.querydsl.sql.SQLNoCloseListener;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.SQLSerializer;
import com.querydsl.sql.SQLTemplates;
import com.querydsl.sql.dml.AbstractSQLClause;
import com.querydsl.sql.dml.EmptyResultSet;
import com.querydsl.sql.dml.SQLInsertClause;
import com.querydsl.sql.dml.SQLMergeBatch;
import com.querydsl.sql.dml.SQLUpdateClause;
import com.querydsl.sql.types.Null;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import javax.annotation.Nullable;
import javax.inject.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SQLMergeClause
extends AbstractSQLClause<SQLMergeClause>
implements StoreClause<SQLMergeClause> {
    private static final Logger logger = LoggerFactory.getLogger(SQLMergeClause.class);
    private final List<Path<?>> columns = new ArrayList();
    private final RelationalPath<?> entity;
    private final QueryMetadata metadata = new DefaultQueryMetadata();
    private final List<Path<?>> keys = new ArrayList();
    @Nullable
    private SubQueryExpression<?> subQuery;
    private final List<SQLMergeBatch> batches = new ArrayList<SQLMergeBatch>();
    private final List<Expression<?>> values = new ArrayList();
    private transient String queryString;
    private transient List<Object> constants;

    public SQLMergeClause(Connection connection, SQLTemplates templates, RelationalPath<?> entity) {
        this(connection, new Configuration(templates), entity);
    }

    public SQLMergeClause(Connection connection, Configuration configuration, RelationalPath<?> entity) {
        super(configuration, connection);
        this.entity = entity;
        this.metadata.addJoin(JoinType.DEFAULT, entity);
    }

    public SQLMergeClause(Provider<Connection> connection, Configuration configuration, RelationalPath<?> entity) {
        super(configuration, connection);
        this.entity = entity;
        this.metadata.addJoin(JoinType.DEFAULT, entity);
    }

    public SQLMergeClause addFlag(QueryFlag.Position position, String flag) {
        this.metadata.addFlag(new QueryFlag(position, flag));
        return this;
    }

    public SQLMergeClause addFlag(QueryFlag.Position position, Expression<?> flag) {
        this.metadata.addFlag(new QueryFlag(position, flag));
        return this;
    }

    private List<? extends Path<?>> getKeys() {
        if (!this.keys.isEmpty()) {
            return this.keys;
        }
        if (this.entity.getPrimaryKey() != null) {
            return this.entity.getPrimaryKey().getLocalColumns();
        }
        throw new IllegalStateException("No keys were defined, invoke keys(..) to add keys");
    }

    public SQLMergeClause addBatch() {
        if (!this.configuration.getTemplates().isNativeMerge()) {
            throw new IllegalStateException("batch only supported for databases that support native merge");
        }
        this.batches.add(new SQLMergeBatch(this.keys, this.columns, this.values, this.subQuery));
        this.columns.clear();
        this.values.clear();
        this.keys.clear();
        this.subQuery = null;
        return this;
    }

    @Override
    public void clear() {
        this.batches.clear();
        this.columns.clear();
        this.values.clear();
        this.keys.clear();
        this.subQuery = null;
    }

    public SQLMergeClause columns(Path<?> ... columns) {
        this.columns.addAll(Arrays.asList(columns));
        return this;
    }

    @Nullable
    public <T> T executeWithKey(Path<T> path) {
        return this.executeWithKey(path.getType(), path);
    }

    public <T> T executeWithKey(Class<T> type) {
        return this.executeWithKey(type, null);
    }

    private <T> T executeWithKey(Class<T> type, @Nullable Path<T> path) {
        ResultSet rs = this.executeWithKeys();
        try {
            if (rs.next()) {
                T t = this.configuration.get(rs, path, 1, type);
                return t;
            }
            T t = null;
            return t;
        }
        catch (SQLException e) {
            throw this.configuration.translate(e);
        }
        finally {
            this.close(rs);
        }
    }

    public <T> List<T> executeWithKeys(Path<T> path) {
        return this.executeWithKeys(path.getType(), path);
    }

    public <T> List<T> executeWithKeys(Class<T> type) {
        return this.executeWithKeys(type, null);
    }

    private <T> List<T> executeWithKeys(Class<T> type, @Nullable Path<T> path) {
        ResultSet rs = null;
        try {
            rs = this.executeWithKeys();
            ArrayList<T> rv = new ArrayList<T>();
            while (rs.next()) {
                rv.add(this.configuration.get(rs, path, 1, type));
            }
            ArrayList<T> arrayList = rv;
            return arrayList;
        }
        catch (SQLException e) {
            throw this.configuration.translate(e);
        }
        finally {
            if (rs != null) {
                this.close(rs);
            }
            this.reset();
        }
    }

    public ResultSet executeWithKeys() {
        this.context = this.startContext(this.connection(), this.metadata, this.entity);
        try {
            if (this.configuration.getTemplates().isNativeMerge()) {
                PreparedStatement stmt = null;
                if (this.batches.isEmpty()) {
                    stmt = this.createStatement(true);
                    this.listeners.notifyMerge(this.entity, this.metadata, this.keys, this.columns, this.values, this.subQuery);
                    this.listeners.preExecute(this.context);
                    stmt.executeUpdate();
                    this.listeners.executed(this.context);
                } else {
                    Collection<PreparedStatement> stmts = this.createStatements(true);
                    if (stmts != null && stmts.size() > 1) {
                        throw new IllegalStateException("executeWithKeys called with batch statement and multiple SQL strings");
                    }
                    stmt = stmts.iterator().next();
                    this.listeners.notifyMerges(this.entity, this.metadata, this.batches);
                    this.listeners.preExecute(this.context);
                    stmt.executeBatch();
                    this.listeners.executed(this.context);
                }
                final PreparedStatement stmt2 = stmt;
                ResultSet rs = stmt.getGeneratedKeys();
                return new ResultSetAdapter(rs){

                    @Override
                    public void close() throws SQLException {
                        try {
                            super.close();
                        }
                        finally {
                            stmt2.close();
                            SQLMergeClause.this.reset();
                            SQLMergeClause.this.endContext(SQLMergeClause.this.context);
                        }
                    }
                };
            }
            if (this.hasRow()) {
                SQLUpdateClause update = new SQLUpdateClause(this.connection(), this.configuration, this.entity);
                update.addListener(this.listeners);
                this.populate(update);
                this.addKeyConditions(update);
                this.reset();
                this.endContext(this.context);
                return EmptyResultSet.DEFAULT;
            }
            SQLInsertClause insert = new SQLInsertClause(this.connection(), this.configuration, this.entity);
            insert.addListener(this.listeners);
            this.populate(insert);
            return insert.executeWithKeys();
        }
        catch (SQLException e) {
            this.onException(this.context, e);
            this.reset();
            this.endContext(this.context);
            throw this.configuration.translate(this.queryString, this.constants, e);
        }
    }

    @Override
    public long execute() {
        if (this.configuration.getTemplates().isNativeMerge()) {
            return this.executeNativeMerge();
        }
        return this.executeCompositeMerge();
    }

    @Override
    public List<SQLBindings> getSQL() {
        if (this.batches.isEmpty()) {
            SQLSerializer serializer = this.createSerializer();
            serializer.serializeMerge(this.metadata, this.entity, this.keys, this.columns, this.values, this.subQuery);
            return ImmutableList.of((Object)this.createBindings(this.metadata, serializer));
        }
        ImmutableList.Builder builder = ImmutableList.builder();
        for (SQLMergeBatch batch : this.batches) {
            SQLSerializer serializer = this.createSerializer();
            serializer.serializeMerge(this.metadata, this.entity, batch.getKeys(), batch.getColumns(), batch.getValues(), batch.getSubQuery());
            builder.add((Object)this.createBindings(this.metadata, serializer));
        }
        return builder.build();
    }

    private boolean hasRow() {
        SQLQuery query = (SQLQuery)new SQLQuery(this.connection(), this.configuration).from((Expression<?>)this.entity);
        for (SQLListener sQLListener : this.listeners.getListeners()) {
            query.addListener(sQLListener);
        }
        query.addListener(SQLNoCloseListener.DEFAULT);
        this.addKeyConditions(query);
        return ((FetchableQueryBase)((Object)query.select(Expressions.ONE))).fetchFirst() != null;
    }

    private void addKeyConditions(FilteredClause<?> query) {
        List<Path<?>> keys = this.getKeys();
        for (int i = 0; i < this.columns.size(); ++i) {
            if (!keys.contains(this.columns.get(i))) continue;
            if (this.values.get(i) instanceof NullExpression) {
                query.where(ExpressionUtils.isNull((Expression)this.columns.get(i)));
                continue;
            }
            query.where(ExpressionUtils.eq((Expression)this.columns.get(i), this.values.get(i)));
        }
    }

    private long executeCompositeMerge() {
        if (this.hasRow()) {
            SQLUpdateClause update = new SQLUpdateClause(this.connection(), this.configuration, this.entity);
            this.populate(update);
            this.addListeners(update);
            this.addKeyConditions(update);
            return update.execute();
        }
        SQLInsertClause insert = new SQLInsertClause(this.connection(), this.configuration, this.entity);
        this.addListeners(insert);
        this.populate(insert);
        return insert.execute();
    }

    private void addListeners(AbstractSQLClause<?> clause) {
        for (SQLListener sQLListener : this.listeners.getListeners()) {
            clause.addListener(sQLListener);
        }
    }

    private void populate(StoreClause<?> clause) {
        for (int i = 0; i < this.columns.size(); ++i) {
            clause.set(this.columns.get(i), this.values.get(i));
        }
    }

    private PreparedStatement createStatement(boolean withKeys) throws SQLException {
        boolean addBatches = !this.configuration.getUseLiterals();
        this.listeners.preRender(this.context);
        SQLSerializer serializer = this.createSerializer();
        PreparedStatement stmt = null;
        if (this.batches.isEmpty()) {
            serializer.serializeMerge(this.metadata, this.entity, this.keys, this.columns, this.values, this.subQuery);
            this.context.addSQL(serializer.toString());
            this.listeners.rendered(this.context);
            this.listeners.prePrepare(this.context);
            stmt = this.prepareStatementAndSetParameters(serializer, withKeys);
            this.context.addPreparedStatement(stmt);
            this.listeners.prepared(this.context);
        } else {
            serializer.serializeMerge(this.metadata, this.entity, this.batches.get(0).getKeys(), this.batches.get(0).getColumns(), this.batches.get(0).getValues(), this.batches.get(0).getSubQuery());
            this.context.addSQL(serializer.toString());
            this.listeners.rendered(this.context);
            stmt = this.prepareStatementAndSetParameters(serializer, withKeys);
            if (addBatches) {
                stmt.addBatch();
            }
            for (int i = 1; i < this.batches.size(); ++i) {
                SQLMergeBatch batch = this.batches.get(i);
                this.listeners.preRender(this.context);
                serializer = this.createSerializer();
                serializer.serializeMerge(this.metadata, this.entity, batch.getKeys(), batch.getColumns(), batch.getValues(), batch.getSubQuery());
                this.context.addSQL(serializer.toString());
                this.listeners.rendered(this.context);
                this.setParameters(stmt, serializer.getConstants(), serializer.getConstantPaths(), this.metadata.getParams());
                if (!addBatches) continue;
                stmt.addBatch();
            }
        }
        return stmt;
    }

    private Collection<PreparedStatement> createStatements(boolean withKeys) throws SQLException {
        boolean addBatches = !this.configuration.getUseLiterals();
        HashMap stmts = Maps.newHashMap();
        this.listeners.preRender(this.context);
        SQLSerializer serializer = this.createSerializer();
        serializer.serializeMerge(this.metadata, this.entity, this.batches.get(0).getKeys(), this.batches.get(0).getColumns(), this.batches.get(0).getValues(), this.batches.get(0).getSubQuery());
        this.context.addSQL(serializer.toString());
        this.listeners.rendered(this.context);
        PreparedStatement stmt = this.prepareStatementAndSetParameters(serializer, withKeys);
        stmts.put(serializer.toString(), stmt);
        if (addBatches) {
            stmt.addBatch();
        }
        for (int i = 1; i < this.batches.size(); ++i) {
            SQLMergeBatch batch = this.batches.get(i);
            serializer = this.createSerializer();
            serializer.serializeMerge(this.metadata, this.entity, batch.getKeys(), batch.getColumns(), batch.getValues(), batch.getSubQuery());
            stmt = (PreparedStatement)stmts.get(serializer.toString());
            if (stmt == null) {
                stmt = this.prepareStatementAndSetParameters(serializer, withKeys);
                stmts.put(serializer.toString(), stmt);
            } else {
                this.setParameters(stmt, serializer.getConstants(), serializer.getConstantPaths(), this.metadata.getParams());
            }
            if (!addBatches) continue;
            stmt.addBatch();
        }
        return stmts.values();
    }

    private PreparedStatement prepareStatementAndSetParameters(SQLSerializer serializer, boolean withKeys) throws SQLException {
        PreparedStatement stmt;
        this.listeners.prePrepare(this.context);
        this.queryString = serializer.toString();
        this.constants = serializer.getConstants();
        this.logQuery(logger, this.queryString, this.constants);
        if (withKeys) {
            String[] target = new String[this.keys.size()];
            for (int i = 0; i < target.length; ++i) {
                target[i] = ColumnMetadata.getName(this.getKeys().get(i));
            }
            stmt = this.connection().prepareStatement(this.queryString, target);
        } else {
            stmt = this.connection().prepareStatement(this.queryString);
        }
        this.setParameters(stmt, serializer.getConstants(), serializer.getConstantPaths(), this.metadata.getParams());
        this.context.addPreparedStatement(stmt);
        this.listeners.prepared(this.context);
        return stmt;
    }

    private long executeNativeMerge() {
        long l;
        Collection<PreparedStatement> stmts;
        block11: {
            PreparedStatement stmt;
            block9: {
                long l2;
                block10: {
                    this.context = this.startContext(this.connection(), this.metadata, this.entity);
                    stmt = null;
                    stmts = null;
                    if (!this.batches.isEmpty()) break block9;
                    stmt = this.createStatement(false);
                    this.listeners.notifyMerge(this.entity, this.metadata, this.keys, this.columns, this.values, this.subQuery);
                    this.listeners.preExecute(this.context);
                    int rc = stmt.executeUpdate();
                    this.listeners.executed(this.context);
                    l2 = rc;
                    if (stmt == null) break block10;
                    this.close(stmt);
                }
                if (stmts != null) {
                    this.close(stmts);
                }
                this.reset();
                this.endContext(this.context);
                return l2;
            }
            try {
                stmts = this.createStatements(false);
                this.listeners.notifyMerges(this.entity, this.metadata, this.batches);
                this.listeners.preExecute(this.context);
                long rc = this.executeBatch(stmts);
                this.listeners.executed(this.context);
                l = rc;
                if (stmt == null) break block11;
            }
            catch (SQLException e) {
                try {
                    this.onException(this.context, e);
                    throw this.configuration.translate(this.queryString, this.constants, e);
                }
                catch (Throwable throwable) {
                    if (stmt != null) {
                        this.close(stmt);
                    }
                    if (stmts != null) {
                        this.close(stmts);
                    }
                    this.reset();
                    this.endContext(this.context);
                    throw throwable;
                }
            }
            this.close(stmt);
        }
        if (stmts != null) {
            this.close(stmts);
        }
        this.reset();
        this.endContext(this.context);
        return l;
    }

    public SQLMergeClause keys(Path<?> ... paths) {
        this.keys.addAll(Arrays.asList(paths));
        return this;
    }

    public SQLMergeClause select(SubQueryExpression<?> subQuery) {
        this.subQuery = subQuery;
        return this;
    }

    @Override
    public <T> SQLMergeClause set(Path<T> path, @Nullable T value) {
        this.columns.add(path);
        if (value != null) {
            this.values.add(ConstantImpl.create(value));
        } else {
            this.values.add(Null.CONSTANT);
        }
        return this;
    }

    @Override
    public <T> SQLMergeClause set(Path<T> path, Expression<? extends T> expression) {
        this.columns.add(path);
        this.values.add(expression);
        return this;
    }

    @Override
    public <T> SQLMergeClause setNull(Path<T> path) {
        this.columns.add(path);
        this.values.add(Null.CONSTANT);
        return this;
    }

    public String toString() {
        SQLSerializer serializer = this.createSerializer();
        serializer.serializeMerge(this.metadata, this.entity, this.keys, this.columns, this.values, this.subQuery);
        return serializer.toString();
    }

    public SQLMergeClause values(Object ... v) {
        for (Object value : v) {
            if (value instanceof Expression) {
                this.values.add((Expression)value);
                continue;
            }
            if (value != null) {
                this.values.add(ConstantImpl.create(value));
                continue;
            }
            this.values.add(Null.CONSTANT);
        }
        return this;
    }

    @Override
    public boolean isEmpty() {
        return this.values.isEmpty() && this.batches.isEmpty();
    }

    @Override
    public int getBatchCount() {
        return this.batches.size();
    }
}

