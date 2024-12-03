/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 */
package com.querydsl.sql;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.FactoryExpression;
import com.querydsl.core.types.Operator;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.PathMetadataFactory;
import com.querydsl.core.types.dsl.BeanPath;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.BooleanOperation;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.sql.ColumnMetadata;
import com.querydsl.sql.ForeignKey;
import com.querydsl.sql.PrimaryKey;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.RelationalPathUtils;
import com.querydsl.sql.SchemaAndTable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public class RelationalPathBase<T>
extends BeanPath<T>
implements RelationalPath<T> {
    private static final long serialVersionUID = -7031357250283629202L;
    @Nullable
    private PrimaryKey<T> primaryKey;
    private final Map<Path<?>, ColumnMetadata> columnMetadata = Maps.newLinkedHashMap();
    private final List<ForeignKey<?>> foreignKeys = Lists.newArrayList();
    private final List<ForeignKey<?>> inverseForeignKeys = Lists.newArrayList();
    private final String schema;
    private final String table;
    private final SchemaAndTable schemaAndTable;
    private transient FactoryExpression<T> projection;
    private transient NumberExpression<Long> count;
    private transient NumberExpression<Long> countDistinct;

    public RelationalPathBase(Class<? extends T> type, String variable, String schema, String table) {
        this(type, PathMetadataFactory.forVariable(variable), schema, table);
    }

    public RelationalPathBase(Class<? extends T> type, PathMetadata metadata, String schema, String table) {
        super(type, metadata);
        this.schema = schema;
        this.table = table;
        this.schemaAndTable = new SchemaAndTable(schema, table);
    }

    protected PrimaryKey<T> createPrimaryKey(Path<?> ... columns) {
        this.primaryKey = new PrimaryKey(this, columns);
        return this.primaryKey;
    }

    protected <F> ForeignKey<F> createForeignKey(Path<?> local, String foreign) {
        ForeignKey foreignKey = new ForeignKey(this, local, foreign);
        this.foreignKeys.add(foreignKey);
        return foreignKey;
    }

    protected <F> ForeignKey<F> createForeignKey(List<? extends Path<?>> local, List<String> foreign) {
        ForeignKey foreignKey = new ForeignKey(this, ImmutableList.copyOf(local), (ImmutableList<String>)ImmutableList.copyOf(foreign));
        this.foreignKeys.add(foreignKey);
        return foreignKey;
    }

    protected <F> ForeignKey<F> createInvForeignKey(Path<?> local, String foreign) {
        ForeignKey foreignKey = new ForeignKey(this, local, foreign);
        this.inverseForeignKeys.add(foreignKey);
        return foreignKey;
    }

    protected <F> ForeignKey<F> createInvForeignKey(List<? extends Path<?>> local, List<String> foreign) {
        ForeignKey foreignKey = new ForeignKey(this, ImmutableList.copyOf(local), (ImmutableList<String>)ImmutableList.copyOf(foreign));
        this.inverseForeignKeys.add(foreignKey);
        return foreignKey;
    }

    protected <P extends Path<?>> P addMetadata(P path, ColumnMetadata metadata) {
        this.columnMetadata.put(path, metadata);
        return path;
    }

    @Override
    public NumberExpression<Long> count() {
        if (this.count == null) {
            if (this.primaryKey != null) {
                this.count = Expressions.numberOperation(Long.class, Ops.AggOps.COUNT_AGG, this.primaryKey.getLocalColumns().get(0));
            } else {
                throw new IllegalStateException("No count expression can be created");
            }
        }
        return this.count;
    }

    @Override
    public NumberExpression<Long> countDistinct() {
        if (this.countDistinct == null) {
            if (this.primaryKey != null) {
                this.countDistinct = Expressions.numberOperation(Long.class, Ops.AggOps.COUNT_DISTINCT_AGG, this.primaryKey.getLocalColumns().get(0));
            } else {
                throw new IllegalStateException("No count distinct expression can be created");
            }
        }
        return this.countDistinct;
    }

    @Override
    public BooleanExpression eq(T right) {
        if (right instanceof RelationalPath) {
            return this.primaryKeyOperation(Ops.EQ, this.primaryKey, ((RelationalPath)right).getPrimaryKey());
        }
        return super.eq(right);
    }

    @Override
    public BooleanExpression eq(Expression<? super T> right) {
        if (right instanceof RelationalPath) {
            return this.primaryKeyOperation(Ops.EQ, this.primaryKey, ((RelationalPath)right).getPrimaryKey());
        }
        return super.eq(right);
    }

    @Override
    public BooleanExpression ne(T right) {
        if (right instanceof RelationalPath) {
            return this.primaryKeyOperation(Ops.NE, this.primaryKey, ((RelationalPath)right).getPrimaryKey());
        }
        return super.ne(right);
    }

    @Override
    public BooleanExpression ne(Expression<? super T> right) {
        if (right instanceof RelationalPath) {
            return this.primaryKeyOperation(Ops.NE, this.primaryKey, ((RelationalPath)right).getPrimaryKey());
        }
        return super.ne(right);
    }

    private BooleanExpression primaryKeyOperation(Operator op, PrimaryKey<?> pk1, PrimaryKey<?> pk2) {
        if (pk1 == null || pk2 == null) {
            throw new UnsupportedOperationException("No primary keys available");
        }
        if (pk1.getLocalColumns().size() != pk2.getLocalColumns().size()) {
            throw new UnsupportedOperationException("Size mismatch for primary key columns");
        }
        BooleanOperation rv = null;
        for (int i = 0; i < pk1.getLocalColumns().size(); ++i) {
            BooleanOperation pred = Expressions.booleanOperation(op, pk1.getLocalColumns().get(i), pk2.getLocalColumns().get(i));
            rv = rv != null ? rv.and(pred) : pred;
        }
        return rv;
    }

    @Override
    public FactoryExpression<T> getProjection() {
        if (this.projection == null) {
            this.projection = RelationalPathUtils.createProjection(this);
        }
        return this.projection;
    }

    public Path<?>[] all() {
        Path[] all = new Path[this.columnMetadata.size()];
        this.columnMetadata.keySet().toArray(all);
        return all;
    }

    @Override
    protected <P extends Path<?>> P add(P path) {
        return path;
    }

    @Override
    public List<Path<?>> getColumns() {
        return Lists.newArrayList(this.columnMetadata.keySet());
    }

    @Override
    public Collection<ForeignKey<?>> getForeignKeys() {
        return this.foreignKeys;
    }

    @Override
    public Collection<ForeignKey<?>> getInverseForeignKeys() {
        return this.inverseForeignKeys;
    }

    @Override
    public PrimaryKey<T> getPrimaryKey() {
        return this.primaryKey;
    }

    @Override
    public SchemaAndTable getSchemaAndTable() {
        return this.schemaAndTable;
    }

    @Override
    public String getSchemaName() {
        return this.schema;
    }

    @Override
    public String getTableName() {
        return this.table;
    }

    @Override
    public ColumnMetadata getMetadata(Path<?> column) {
        return this.columnMetadata.get(column);
    }
}

