/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 */
package com.querydsl.sql;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.querydsl.core.JoinExpression;
import com.querydsl.core.JoinFlag;
import com.querydsl.core.QueryFlag;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.support.SerializerBase;
import com.querydsl.core.types.Constant;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.FactoryExpression;
import com.querydsl.core.types.Operation;
import com.querydsl.core.types.Operator;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.ParamExpression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.Template;
import com.querydsl.core.types.TemplateExpression;
import com.querydsl.core.types.TemplateFactory;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.sql.ColumnMetadata;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.RelationalFunctionCall;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLOps;
import com.querydsl.sql.SQLTemplates;
import com.querydsl.sql.SchemaAndTable;
import com.querydsl.sql.dml.SQLInsertBatch;
import com.querydsl.sql.types.Null;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public class SQLSerializer
extends SerializerBase<SQLSerializer> {
    private static final Expression<?> Q = Expressions.template(Object.class, "?", new Object[0]);
    private static final String COMMA = ", ";
    private final LinkedList<Path<?>> constantPaths = new LinkedList();
    private final List<Object> constants = new ArrayList<Object>();
    private final Set<Path<?>> withAliases = Sets.newHashSet();
    private final boolean dml;
    protected Stage stage = Stage.SELECT;
    private boolean skipParent;
    private boolean dmlWithSchema;
    private RelationalPath<?> entity;
    private final Configuration configuration;
    private final SQLTemplates templates;
    private boolean inUnion = false;
    private boolean inJoin = false;
    private boolean inSubquery = false;
    private boolean useLiterals = false;

    public SQLSerializer(Configuration conf) {
        this(conf, false);
    }

    public SQLSerializer(Configuration conf, boolean dml) {
        super(conf.getTemplates());
        this.configuration = conf;
        this.templates = conf.getTemplates();
        this.dml = dml;
    }

    protected void appendAsColumnName(Path<?> path, boolean precededByDot) {
        String column = ColumnMetadata.getName(path);
        if (path.getMetadata().getParent() instanceof RelationalPath) {
            RelationalPath parent = (RelationalPath)path.getMetadata().getParent();
            column = this.configuration.getColumnOverride(parent.getSchemaAndTable(), column);
        }
        this.append(this.templates.quoteIdentifier(column, precededByDot));
    }

    private SchemaAndTable getSchemaAndTable(RelationalPath<?> path) {
        return this.configuration.getOverride(path.getSchemaAndTable());
    }

    protected void appendSchemaName(String schema) {
        this.append(this.templates.quoteIdentifier(schema));
    }

    protected void appendTableName(String table, boolean precededByDot) {
        this.append(this.templates.quoteIdentifier(table, precededByDot));
    }

    public List<Object> getConstants() {
        return this.constants;
    }

    public List<Path<?>> getConstantPaths() {
        return this.constantPaths;
    }

    private List<Expression<?>> getIdentifierColumns(List<JoinExpression> joins, boolean alias) {
        if (joins.size() == 1) {
            JoinExpression join = joins.get(0);
            if (join.getTarget() instanceof RelationalPath) {
                return ((RelationalPath)join.getTarget()).getColumns();
            }
            return Collections.emptyList();
        }
        ArrayList rv = Lists.newArrayList();
        int counter = 0;
        for (JoinExpression join : joins) {
            if (join.getTarget() instanceof RelationalPath) {
                RelationalPath path = (RelationalPath)join.getTarget();
                List<Path<?>> columns = path.getPrimaryKey() != null ? path.getPrimaryKey().getLocalColumns() : path.getColumns();
                if (alias) {
                    for (Expression expression : columns) {
                        rv.add(ExpressionUtils.as(expression, "col" + ++counter));
                    }
                    continue;
                }
                rv.addAll(columns);
                continue;
            }
            return Collections.emptyList();
        }
        return rv;
    }

    protected SQLTemplates getTemplates() {
        return this.templates;
    }

    public void handle(String template, Object ... args) {
        this.handleTemplate(TemplateFactory.DEFAULT.create(template), Arrays.asList(args));
    }

    public final SQLSerializer handleSelect(String sep, List<? extends Expression<?>> expressions) {
        if (this.inSubquery) {
            HashSet names = Sets.newHashSet();
            ArrayList replacements = Lists.newArrayList();
            for (Expression<?> expr : expressions) {
                String name;
                if (expr instanceof Path && !names.add((name = ColumnMetadata.getName((Path)expr)).toLowerCase())) {
                    expr = ExpressionUtils.as(expr, "col__" + name + replacements.size());
                }
                replacements.add(expr);
            }
            return (SQLSerializer)this.handle(sep, replacements);
        }
        return (SQLSerializer)this.handle(sep, expressions);
    }

    protected void handleJoinTarget(JoinExpression je) {
        RelationalPath pe;
        if (je.getTarget() instanceof RelationalPath && this.templates.isSupportsAlias() && (pe = (RelationalPath)je.getTarget()).getMetadata().getParent() == null) {
            if (this.withAliases.contains(pe)) {
                this.appendTableName(pe.getMetadata().getName(), false);
                this.append(this.templates.getTableAlias());
            } else {
                boolean precededByDot;
                SchemaAndTable schemaAndTable = this.getSchemaAndTable(pe);
                if (this.templates.isPrintSchema()) {
                    this.appendSchemaName(schemaAndTable.getSchema());
                    this.append(".");
                    precededByDot = true;
                } else {
                    precededByDot = false;
                }
                this.appendTableName(schemaAndTable.getTable(), precededByDot);
                this.append(this.templates.getTableAlias());
            }
        }
        this.inJoin = true;
        this.handle(je.getTarget());
        this.inJoin = false;
    }

    public void serialize(QueryMetadata metadata, boolean forCountRow) {
        this.templates.serialize(metadata, forCountRow, this);
    }

    void serializeForQuery(QueryMetadata metadata, boolean forCountRow) {
        boolean oldInSubquery = this.inSubquery;
        this.inSubquery = this.inSubquery || this.getLength() > 0;
        boolean oldSkipParent = this.skipParent;
        this.skipParent = false;
        Expression<?> select = metadata.getProjection();
        List<JoinExpression> joins = metadata.getJoins();
        Predicate where = metadata.getWhere();
        List<Expression<?>> groupBy = metadata.getGroupBy();
        Predicate having = metadata.getHaving();
        List<OrderSpecifier<?>> orderBy = metadata.getOrderBy();
        Set<QueryFlag> flags = metadata.getFlags();
        boolean hasFlags = !flags.isEmpty();
        String suffix = null;
        ImmutableList sqlSelect = select instanceof FactoryExpression ? ((FactoryExpression)select).getArgs() : (select != null ? ImmutableList.of(select) : ImmutableList.of());
        if (hasFlags) {
            ArrayList withFlags = Lists.newArrayList();
            boolean recursive = false;
            for (QueryFlag flag : flags) {
                if (flag.getPosition() != QueryFlag.Position.WITH) continue;
                if (flag.getFlag() == SQLTemplates.RECURSIVE) {
                    recursive = true;
                    continue;
                }
                withFlags.add(flag.getFlag());
            }
            if (!withFlags.isEmpty()) {
                if (recursive) {
                    this.append(this.templates.getWithRecursive());
                } else {
                    this.append(this.templates.getWith());
                }
                this.handle(COMMA, withFlags);
                this.append("\n");
            }
        }
        if (hasFlags) {
            this.serialize(QueryFlag.Position.START, flags);
        }
        Stage oldStage = this.stage;
        this.stage = Stage.SELECT;
        if (forCountRow) {
            this.append(this.templates.getSelect());
            if (hasFlags) {
                this.serialize(QueryFlag.Position.AFTER_SELECT, flags);
            }
            if (!metadata.isDistinct()) {
                this.append(this.templates.getCountStar());
                if (!groupBy.isEmpty()) {
                    this.append(this.templates.getFrom());
                    this.append("(");
                    this.append(this.templates.getSelect());
                    this.append("1 as one ");
                    suffix = ") internal";
                }
            } else {
                List<Expression<Object>> columns = sqlSelect.isEmpty() ? this.getIdentifierColumns(joins, !this.templates.isCountDistinctMultipleColumns()) : sqlSelect;
                if (!groupBy.isEmpty()) {
                    this.append(this.templates.getCountStar());
                    this.append(this.templates.getFrom());
                    this.append("(");
                    this.append(this.templates.getSelectDistinct());
                    this.handleSelect(COMMA, columns);
                    suffix = ") internal";
                } else if (columns.size() == 1) {
                    this.append(this.templates.getDistinctCountStart());
                    this.handle((Expression)columns.get(0));
                    this.append(this.templates.getDistinctCountEnd());
                } else if (this.templates.isCountDistinctMultipleColumns()) {
                    this.append(this.templates.getDistinctCountStart());
                    ((SQLSerializer)this.append("(")).handleSelect(COMMA, columns).append(")");
                    this.append(this.templates.getDistinctCountEnd());
                } else {
                    this.append(this.templates.getCountStar());
                    this.append(this.templates.getFrom());
                    this.append("(");
                    this.append(this.templates.getSelectDistinct());
                    this.handleSelect(COMMA, columns);
                    suffix = ") internal";
                }
            }
        } else if (!sqlSelect.isEmpty()) {
            if (!metadata.isDistinct()) {
                this.append(this.templates.getSelect());
            } else {
                this.append(this.templates.getSelectDistinct());
            }
            if (hasFlags) {
                this.serialize(QueryFlag.Position.AFTER_SELECT, flags);
            }
            this.handleSelect(COMMA, (List<? extends Expression<?>>)sqlSelect);
        }
        if (hasFlags) {
            this.serialize(QueryFlag.Position.AFTER_PROJECTION, flags);
        }
        this.stage = Stage.FROM;
        this.serializeSources(joins);
        if (hasFlags) {
            this.serialize(QueryFlag.Position.BEFORE_FILTERS, flags);
        }
        if (where != null) {
            this.stage = Stage.WHERE;
            ((SQLSerializer)this.append(this.templates.getWhere())).handle(where);
        }
        if (hasFlags) {
            this.serialize(QueryFlag.Position.AFTER_FILTERS, flags);
        }
        if (hasFlags) {
            this.serialize(QueryFlag.Position.BEFORE_GROUP_BY, flags);
        }
        if (!groupBy.isEmpty()) {
            this.stage = Stage.GROUP_BY;
            ((SQLSerializer)this.append(this.templates.getGroupBy())).handle(COMMA, groupBy);
        }
        if (hasFlags) {
            this.serialize(QueryFlag.Position.AFTER_GROUP_BY, flags);
        }
        if (hasFlags) {
            this.serialize(QueryFlag.Position.BEFORE_HAVING, flags);
        }
        if (having != null) {
            this.stage = Stage.HAVING;
            ((SQLSerializer)this.append(this.templates.getHaving())).handle(having);
        }
        if (hasFlags) {
            this.serialize(QueryFlag.Position.AFTER_HAVING, flags);
        }
        if (hasFlags) {
            this.serialize(QueryFlag.Position.BEFORE_ORDER, flags);
        }
        if (!orderBy.isEmpty() && !forCountRow) {
            this.stage = Stage.ORDER_BY;
            this.append(this.templates.getOrderBy());
            this.handleOrderBy(orderBy);
        }
        if (hasFlags) {
            this.serialize(QueryFlag.Position.AFTER_ORDER, flags);
        }
        if (!forCountRow && metadata.getModifiers().isRestricting() && !joins.isEmpty()) {
            this.stage = Stage.MODIFIERS;
            this.templates.serializeModifiers(metadata, this);
        }
        if (suffix != null) {
            this.append(suffix);
        }
        this.stage = oldStage;
        this.skipParent = oldSkipParent;
        this.inSubquery = oldInSubquery;
    }

    protected void handleOrderBy(List<OrderSpecifier<?>> orderBy) {
        boolean first = true;
        for (OrderSpecifier<?> os : orderBy) {
            String order;
            if (!first) {
                this.append(COMMA);
            }
            String string = order = os.getOrder() == Order.ASC ? this.templates.getAsc() : this.templates.getDesc();
            if (os.getNullHandling() == OrderSpecifier.NullHandling.NullsFirst) {
                if (this.templates.getNullsFirst() != null) {
                    this.handle(os.getTarget());
                    this.append(order);
                    this.append(this.templates.getNullsFirst());
                } else {
                    this.append("(case when ");
                    this.handle(os.getTarget());
                    this.append(" is null then 0 else 1 end), ");
                    this.handle(os.getTarget());
                    this.append(order);
                }
            } else if (os.getNullHandling() == OrderSpecifier.NullHandling.NullsLast) {
                if (this.templates.getNullsLast() != null) {
                    this.handle(os.getTarget());
                    this.append(order);
                    this.append(this.templates.getNullsLast());
                } else {
                    this.append("(case when ");
                    this.handle(os.getTarget());
                    this.append(" is null then 1 else 0 end), ");
                    this.handle(os.getTarget());
                    this.append(order);
                }
            } else {
                this.handle(os.getTarget());
                this.append(order);
            }
            first = false;
        }
    }

    public void serializeDelete(QueryMetadata metadata, RelationalPath<?> entity) {
        this.entity = entity;
        this.templates.serializeDelete(metadata, entity, this);
    }

    void serializeForDelete(QueryMetadata metadata, RelationalPath<?> entity) {
        this.serialize(QueryFlag.Position.START, metadata.getFlags());
        if (!this.serialize(QueryFlag.Position.START_OVERRIDE, metadata.getFlags())) {
            this.append(this.templates.getDelete());
        }
        this.serialize(QueryFlag.Position.AFTER_SELECT, metadata.getFlags());
        this.append("from ");
        this.dmlWithSchema = true;
        this.handle(entity);
        this.dmlWithSchema = false;
        if (metadata.getWhere() != null) {
            ((SQLSerializer)this.append(this.templates.getWhere())).handle(metadata.getWhere());
        }
    }

    public void serializeMerge(QueryMetadata metadata, RelationalPath<?> entity, List<Path<?>> keys, List<Path<?>> columns, List<Expression<?>> values, @Nullable SubQueryExpression<?> subQuery) {
        this.entity = entity;
        this.templates.serializeMerge(metadata, entity, keys, columns, values, subQuery, this);
    }

    void serializeForMerge(QueryMetadata metadata, RelationalPath<?> entity, List<Path<?>> keys, List<Path<?>> columns, List<Expression<?>> values, @Nullable SubQueryExpression<?> subQuery) {
        this.serialize(QueryFlag.Position.START, metadata.getFlags());
        if (!this.serialize(QueryFlag.Position.START_OVERRIDE, metadata.getFlags())) {
            this.append(this.templates.getMergeInto());
        }
        this.serialize(QueryFlag.Position.AFTER_SELECT, metadata.getFlags());
        this.dmlWithSchema = true;
        this.handle(entity);
        this.dmlWithSchema = false;
        this.append(" ");
        if (!columns.isEmpty()) {
            this.skipParent = true;
            ((SQLSerializer)((SQLSerializer)this.append("(")).handle(COMMA, columns)).append(") ");
            this.skipParent = false;
        }
        if (!keys.isEmpty()) {
            this.append(this.templates.getKey());
            this.skipParent = true;
            ((SQLSerializer)((SQLSerializer)this.append("(")).handle(COMMA, keys)).append(") ");
            this.skipParent = false;
        }
        if (subQuery != null) {
            this.append("\n");
            this.serialize(subQuery.getMetadata(), false);
        } else {
            if (!this.useLiterals) {
                for (int i = 0; i < columns.size(); ++i) {
                    if (!(values.get(i) instanceof Constant)) continue;
                    this.constantPaths.add(columns.get(i));
                }
            }
            this.append(this.templates.getValues());
            ((SQLSerializer)((SQLSerializer)this.append("(")).handle(COMMA, values)).append(") ");
        }
    }

    public void serializeInsert(QueryMetadata metadata, RelationalPath<?> entity, List<Path<?>> columns, List<Expression<?>> values, @Nullable SubQueryExpression<?> subQuery) {
        this.entity = entity;
        this.templates.serializeInsert(metadata, entity, columns, values, subQuery, this);
    }

    public void serializeInsert(QueryMetadata metadata, RelationalPath<?> entity, List<SQLInsertBatch> batches) {
        this.entity = entity;
        this.templates.serializeInsert(metadata, entity, batches, this);
    }

    void serializeForInsert(QueryMetadata metadata, RelationalPath<?> entity, List<SQLInsertBatch> batches) {
        this.serializeForInsert(metadata, entity, batches.get(0).getColumns(), batches.get(0).getValues(), null);
        for (int i = 1; i < batches.size(); ++i) {
            this.append(COMMA);
            this.append("(");
            this.handle(COMMA, batches.get(i).getValues());
            this.append(")");
        }
    }

    void serializeForInsert(QueryMetadata metadata, RelationalPath<?> entity, List<Path<?>> columns, List<Expression<?>> values, @Nullable SubQueryExpression<?> subQuery) {
        this.serialize(QueryFlag.Position.START, metadata.getFlags());
        if (!this.serialize(QueryFlag.Position.START_OVERRIDE, metadata.getFlags())) {
            this.append(this.templates.getInsertInto());
        }
        this.serialize(QueryFlag.Position.AFTER_SELECT, metadata.getFlags());
        this.dmlWithSchema = true;
        this.handle(entity);
        this.dmlWithSchema = false;
        if (!columns.isEmpty()) {
            this.append(" (");
            this.skipParent = true;
            this.handle(COMMA, columns);
            this.skipParent = false;
            this.append(")");
        }
        if (subQuery != null) {
            this.append("\n");
            this.serialize(subQuery.getMetadata(), false);
        } else {
            if (!this.useLiterals) {
                for (int i = 0; i < columns.size(); ++i) {
                    if (!(values.get(i) instanceof Constant)) continue;
                    this.constantPaths.add(columns.get(i));
                }
            }
            if (!values.isEmpty()) {
                this.append(this.templates.getValues());
                this.append("(");
                this.handle(COMMA, values);
                this.append(")");
            } else {
                this.append(this.templates.getDefaultValues());
            }
        }
    }

    public void serializeUpdate(QueryMetadata metadata, RelationalPath<?> entity, Map<Path<?>, Expression<?>> updates) {
        this.templates.serializeUpdate(metadata, entity, updates, this);
    }

    void serializeForUpdate(QueryMetadata metadata, RelationalPath<?> entity, Map<Path<?>, Expression<?>> updates) {
        this.entity = entity;
        this.serialize(QueryFlag.Position.START, metadata.getFlags());
        if (!this.serialize(QueryFlag.Position.START_OVERRIDE, metadata.getFlags())) {
            this.append(this.templates.getUpdate());
        }
        this.serialize(QueryFlag.Position.AFTER_SELECT, metadata.getFlags());
        this.dmlWithSchema = true;
        this.handle(entity);
        this.dmlWithSchema = false;
        this.append("\n");
        this.append(this.templates.getSet());
        boolean first = true;
        this.skipParent = true;
        for (Map.Entry<Path<?>, Expression<?>> update : updates.entrySet()) {
            if (!first) {
                this.append(COMMA);
            }
            this.handle((Expression)update.getKey());
            this.append(" = ");
            if (!this.useLiterals && update.getValue() instanceof Constant) {
                this.constantPaths.add(update.getKey());
            }
            this.handle(update.getValue());
            first = false;
        }
        this.skipParent = false;
        if (metadata.getWhere() != null) {
            ((SQLSerializer)this.append(this.templates.getWhere())).handle(metadata.getWhere());
        }
    }

    private void serializeSources(List<JoinExpression> joins) {
        if (joins.isEmpty()) {
            String dummyTable = this.templates.getDummyTable();
            if (!Strings.isNullOrEmpty((String)dummyTable)) {
                this.append(this.templates.getFrom());
                this.append(dummyTable);
            }
        } else {
            this.append(this.templates.getFrom());
            for (int i = 0; i < joins.size(); ++i) {
                JoinExpression je = joins.get(i);
                if (je.getFlags().isEmpty()) {
                    if (i > 0) {
                        this.append(this.templates.getJoinSymbol(je.getType()));
                    }
                    this.handleJoinTarget(je);
                    if (je.getCondition() == null) continue;
                    ((SQLSerializer)this.append(this.templates.getOn())).handle(je.getCondition());
                    continue;
                }
                this.serialize(JoinFlag.Position.START, je.getFlags());
                if (!this.serialize(JoinFlag.Position.OVERRIDE, je.getFlags()) && i > 0) {
                    this.append(this.templates.getJoinSymbol(je.getType()));
                }
                this.serialize(JoinFlag.Position.BEFORE_TARGET, je.getFlags());
                this.handleJoinTarget(je);
                this.serialize(JoinFlag.Position.BEFORE_CONDITION, je.getFlags());
                if (je.getCondition() != null) {
                    ((SQLSerializer)this.append(this.templates.getOn())).handle(je.getCondition());
                }
                this.serialize(JoinFlag.Position.END, je.getFlags());
            }
        }
    }

    public void serializeUnion(Expression<?> union, QueryMetadata metadata, boolean unionAll) {
        boolean hasFlags;
        List<Expression<?>> groupBy = metadata.getGroupBy();
        Predicate having = metadata.getHaving();
        List<OrderSpecifier<?>> orderBy = metadata.getOrderBy();
        Set<QueryFlag> flags = metadata.getFlags();
        boolean bl = hasFlags = !flags.isEmpty();
        if (hasFlags) {
            boolean handled = false;
            boolean recursive = false;
            for (QueryFlag flag : flags) {
                if (flag.getPosition() != QueryFlag.Position.WITH) continue;
                if (flag.getFlag() == SQLTemplates.RECURSIVE) {
                    recursive = true;
                    continue;
                }
                if (handled) {
                    this.append(COMMA);
                }
                this.handle(flag.getFlag());
                handled = true;
            }
            if (handled) {
                if (recursive) {
                    this.prepend(this.templates.getWithRecursive());
                } else {
                    this.prepend(this.templates.getWith());
                }
                this.append("\n");
            }
        }
        Stage oldStage = this.stage;
        this.handle(union);
        if (hasFlags) {
            this.serialize(QueryFlag.Position.BEFORE_GROUP_BY, flags);
        }
        if (!groupBy.isEmpty()) {
            this.stage = Stage.GROUP_BY;
            ((SQLSerializer)this.append(this.templates.getGroupBy())).handle(COMMA, groupBy);
        }
        if (hasFlags) {
            this.serialize(QueryFlag.Position.AFTER_GROUP_BY, flags);
        }
        if (hasFlags) {
            this.serialize(QueryFlag.Position.BEFORE_HAVING, flags);
        }
        if (having != null) {
            this.stage = Stage.HAVING;
            ((SQLSerializer)this.append(this.templates.getHaving())).handle(having);
        }
        if (hasFlags) {
            this.serialize(QueryFlag.Position.AFTER_HAVING, flags);
        }
        if (hasFlags) {
            this.serialize(QueryFlag.Position.BEFORE_ORDER, flags);
        }
        if (!orderBy.isEmpty()) {
            this.stage = Stage.ORDER_BY;
            this.append(this.templates.getOrderBy());
            this.skipParent = true;
            this.handleOrderBy(orderBy);
            this.skipParent = false;
        }
        if (hasFlags) {
            this.serialize(QueryFlag.Position.AFTER_ORDER, flags);
        }
        if (hasFlags) {
            this.serialize(QueryFlag.Position.END, flags);
        }
        this.stage = oldStage;
    }

    @Override
    public void visitConstant(Object constant) {
        if (this.useLiterals) {
            if (constant instanceof Collection) {
                this.append("(");
                boolean first = true;
                for (Object o : (Collection)constant) {
                    if (!first) {
                        this.append(COMMA);
                    }
                    this.append(this.configuration.asLiteral(o));
                    first = false;
                }
                this.append(")");
            } else {
                this.append(this.configuration.asLiteral(constant));
            }
        } else if (constant instanceof Collection) {
            this.append("(");
            boolean first = true;
            for (Object o : (Collection)constant) {
                if (!first) {
                    this.append(COMMA);
                }
                this.append("?");
                this.constants.add(o);
                if (first && this.constantPaths.size() < this.constants.size()) {
                    this.constantPaths.add(null);
                }
                first = false;
            }
            this.append(")");
            int size = ((Collection)constant).size() - 1;
            Path<?> lastPath = this.constantPaths.peekLast();
            for (int i = 0; i < size; ++i) {
                this.constantPaths.add(lastPath);
            }
        } else {
            if (this.stage == Stage.SELECT && !Null.class.isInstance(constant) && this.configuration.getTemplates().isWrapSelectParameters()) {
                String typeName = this.configuration.getTypeNameForCast(constant.getClass());
                Expression<String> type = Expressions.constant(typeName);
                super.visitOperation(constant.getClass(), SQLOps.CAST, (List<? extends Expression<?>>)ImmutableList.of(Q, type));
            } else {
                this.append("?");
            }
            this.constants.add(constant);
            if (this.constantPaths.size() < this.constants.size()) {
                this.constantPaths.add(null);
            }
        }
    }

    @Override
    public Void visit(ParamExpression<?> param, Void context) {
        this.append("?");
        this.constants.add(param);
        if (this.constantPaths.size() < this.constants.size()) {
            this.constantPaths.add(null);
        }
        return null;
    }

    @Override
    public Void visit(Path<?> path, Void context) {
        boolean precededByDot;
        PathMetadata metadata;
        if (this.dml) {
            if (path.equals(this.entity) && path instanceof RelationalPath) {
                boolean precededByDot2;
                SchemaAndTable schemaAndTable = this.getSchemaAndTable((RelationalPath)path);
                if (this.dmlWithSchema && this.templates.isPrintSchema()) {
                    this.appendSchemaName(schemaAndTable.getSchema());
                    this.append(".");
                    precededByDot2 = true;
                } else {
                    precededByDot2 = false;
                }
                this.appendTableName(schemaAndTable.getTable(), precededByDot2);
                return null;
            }
            if (this.entity.equals(path.getMetadata().getParent()) && this.skipParent) {
                this.appendAsColumnName(path, false);
                return null;
            }
        }
        if ((metadata = path.getMetadata()).getParent() != null && (!this.skipParent || this.dml)) {
            this.visit(metadata.getParent(), context);
            this.append(".");
            precededByDot = true;
        } else {
            precededByDot = false;
        }
        this.appendAsColumnName(path, precededByDot);
        return null;
    }

    @Override
    public Void visit(SubQueryExpression<?> query, Void context) {
        boolean oldInSubsuery = this.inSubquery;
        this.inSubquery = true;
        if (this.inUnion && !this.templates.isUnionsWrapped()) {
            this.serialize(query.getMetadata(), false);
        } else {
            this.append("(");
            this.serialize(query.getMetadata(), false);
            this.append(")");
        }
        this.inSubquery = oldInSubsuery;
        return null;
    }

    @Override
    public Void visit(TemplateExpression<?> expr, Void context) {
        if (expr.equals(Expressions.TRUE)) {
            this.append(this.templates.serialize("1", 16));
        } else if (expr.equals(Expressions.FALSE)) {
            this.append(this.templates.serialize("0", 16));
        } else if (this.inJoin && expr instanceof RelationalFunctionCall && this.templates.isFunctionJoinsWrapped()) {
            this.append("table(");
            super.visit(expr, context);
            this.append(")");
        } else {
            super.visit(expr, context);
        }
        return null;
    }

    @Override
    protected void visitOperation(Class<?> type, Operator operator, List<? extends Expression<?>> args) {
        Object constant;
        boolean pathAdded = false;
        if (!(args.size() != 2 || this.useLiterals || !(args.get(0) instanceof Path) || !(args.get(1) instanceof Constant) || operator == Ops.NUMCAST || Collection.class.isInstance(constant = ((Constant)args.get(1)).getConstant()) && ((Collection)constant).isEmpty())) {
            for (Template.Element element : this.templates.getTemplate(operator).getElements()) {
                if (!(element instanceof Template.ByIndex) || ((Template.ByIndex)element).getIndex() != 1) continue;
                this.constantPaths.add((Path)args.get(0));
                pathAdded = true;
                break;
            }
        }
        if (operator == Ops.SET && args.get(0) instanceof SubQueryExpression) {
            boolean oldUnion = this.inUnion;
            this.inUnion = true;
            super.visitOperation(type, SQLOps.UNION, (List<? extends Expression<?>>)args);
            this.inUnion = oldUnion;
        } else if (operator == SQLOps.UNION || operator == SQLOps.UNION_ALL) {
            boolean oldUnion = this.inUnion;
            this.inUnion = true;
            super.visitOperation(type, operator, (List<? extends Expression<?>>)args);
            this.inUnion = oldUnion;
        } else if (operator == Ops.LIKE && args.get(1) instanceof Constant) {
            String escape = String.valueOf(this.templates.getEscapeChar());
            String escaped = args.get(1).toString().replace(escape, escape + escape);
            super.visitOperation(String.class, Ops.LIKE, (List<? extends Expression<?>>)ImmutableList.of(args.get(0), ConstantImpl.create(escaped)));
        } else if (operator == Ops.STRING_CAST) {
            String typeName = this.configuration.getTypeNameForCast(String.class);
            super.visitOperation(String.class, SQLOps.CAST, (List<? extends Expression<?>>)ImmutableList.of(args.get(0), ConstantImpl.create(typeName)));
        } else if (operator == Ops.NUMCAST) {
            Constant expectedConstant = (Constant)args.get(1);
            Class targetType = (Class)expectedConstant.getConstant();
            String typeName = this.configuration.getTypeNameForCast(targetType);
            super.visitOperation(targetType, SQLOps.CAST, (List<? extends Expression<?>>)ImmutableList.of(args.get(0), ConstantImpl.create(typeName)));
        } else if (operator == Ops.ALIAS) {
            if (this.stage == Stage.SELECT || this.stage == Stage.FROM) {
                if (args.get(1) instanceof Path && !((Path)args.get(1)).getMetadata().isRoot()) {
                    Path path = (Path)args.get(1);
                    args = ImmutableList.of(args.get(0), ExpressionUtils.path(path.getType(), path.getMetadata().getName()));
                }
                super.visitOperation(type, operator, (List<? extends Expression<?>>)args);
            } else {
                this.handle(args.get(1));
            }
        } else if ((operator == Ops.IN || operator == Ops.NOT_IN) && args.get(0) instanceof Path && args.get(1) instanceof Constant) {
            Collection coll = (Collection)((Constant)args.get(1)).getConstant();
            if (coll.isEmpty()) {
                super.visitOperation(type, operator == Ops.IN ? Ops.EQ : Ops.NE, (List<? extends Expression<?>>)ImmutableList.of(Expressions.ONE, Expressions.TWO));
            } else if (this.templates.getListMaxSize() == 0 || coll.size() <= this.templates.getListMaxSize()) {
                super.visitOperation(type, operator, (List<? extends Expression<?>>)args);
            } else {
                Expression path = (Expression)args.get(0);
                if (pathAdded) {
                    this.constantPaths.removeLast();
                }
                Iterable partitioned = Iterables.partition((Iterable)coll, (int)this.templates.getListMaxSize());
                Predicate result = operator == Ops.IN ? ExpressionUtils.inAny(path, partitioned) : ExpressionUtils.notInAny(path, partitioned);
                this.append("(");
                result.accept(this, null);
                this.append(")");
            }
        } else if (operator == SQLOps.WITH_COLUMNS) {
            boolean oldSkipParent = this.skipParent;
            this.skipParent = true;
            super.visitOperation(type, operator, (List<? extends Expression<?>>)args);
            this.skipParent = oldSkipParent;
        } else if (operator == Ops.ORDER) {
            List order = (List)((Constant)args.get(0)).getConstant();
            this.handleOrderBy(order);
        } else {
            super.visitOperation(type, operator, (List<? extends Expression<?>>)args);
        }
        if (operator == SQLOps.WITH_ALIAS || operator == SQLOps.WITH_COLUMNS) {
            if (args.get(0) instanceof Path) {
                this.withAliases.add((Path)args.get(0));
            } else {
                this.withAliases.add((Path)((Operation)args.get(0)).getArg(0));
            }
        }
    }

    public void setUseLiterals(boolean useLiterals) {
        this.useLiterals = useLiterals;
    }

    protected void setSkipParent(boolean b) {
        this.skipParent = b;
    }

    protected void setDmlWithSchema(boolean b) {
        this.dmlWithSchema = b;
    }

    protected static enum Stage {
        SELECT,
        FROM,
        WHERE,
        GROUP_BY,
        HAVING,
        ORDER_BY,
        MODIFIERS;

    }
}

