/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 */
package com.querydsl.sql;

import com.google.common.collect.ImmutableSet;
import com.querydsl.core.JoinExpression;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.types.Constant;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.FactoryExpression;
import com.querydsl.core.types.Operation;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.ParamExpression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.TemplateExpression;
import com.querydsl.core.types.Visitor;
import com.querydsl.core.util.CollectionUtils;
import com.querydsl.sql.RelationalPath;
import java.util.Set;

public final class RelationalPathExtractor
implements Visitor<Set<RelationalPath<?>>, Set<RelationalPath<?>>> {
    public static final RelationalPathExtractor DEFAULT = new RelationalPathExtractor();

    public static Set<RelationalPath<?>> extract(QueryMetadata md) {
        Object known = ImmutableSet.of();
        known = DEFAULT.visitJoins((Iterable<JoinExpression>)md.getJoins(), (Set<RelationalPath<?>>)known);
        if (md.getProjection() != null) {
            known = (Set)md.getProjection().accept(DEFAULT, known);
        }
        for (OrderSpecifier<?> orderSpecifier : md.getOrderBy()) {
            known = (Set)orderSpecifier.getTarget().accept(DEFAULT, known);
        }
        for (Expression expression : md.getGroupBy()) {
            known = (Set)expression.accept(DEFAULT, known);
        }
        if (md.getHaving() != null) {
            known = (Set)md.getHaving().accept(DEFAULT, known);
        }
        if (md.getWhere() != null) {
            known = (Set)md.getWhere().accept(DEFAULT, known);
        }
        return known;
    }

    public static Set<RelationalPath<?>> extract(Expression<?> expr) {
        return (Set)expr.accept(DEFAULT, ImmutableSet.of());
    }

    @Override
    public Set<RelationalPath<?>> visit(Constant<?> expr, Set<RelationalPath<?>> known) {
        return known;
    }

    @Override
    public Set<RelationalPath<?>> visit(FactoryExpression<?> expr, Set<RelationalPath<?>> known) {
        for (Expression<?> arg : expr.getArgs()) {
            known = (Set)arg.accept(this, known);
        }
        return known;
    }

    @Override
    public Set<RelationalPath<?>> visit(Operation<?> expr, Set<RelationalPath<?>> known) {
        for (Expression<?> arg : expr.getArgs()) {
            known = (Set)arg.accept(this, known);
        }
        return known;
    }

    @Override
    public Set<RelationalPath<?>> visit(ParamExpression<?> expr, Set<RelationalPath<?>> known) {
        return known;
    }

    @Override
    public Set<RelationalPath<?>> visit(Path<?> expr, Set<RelationalPath<?>> known) {
        if (expr.getMetadata().isRoot()) {
            if (expr instanceof RelationalPath) {
                known = CollectionUtils.add(known, (RelationalPath)expr);
            }
        } else {
            known = (Set)expr.getMetadata().getParent().accept(this, known);
        }
        return known;
    }

    @Override
    public Set<RelationalPath<?>> visit(SubQueryExpression<?> expr, Set<RelationalPath<?>> known) {
        Set old = known;
        QueryMetadata md = expr.getMetadata();
        known = this.visitJoins(md.getJoins(), known);
        if (md.getProjection() != null) {
            known = (Set)md.getProjection().accept(this, known);
        }
        for (OrderSpecifier<?> orderSpecifier : md.getOrderBy()) {
            known = (Set)orderSpecifier.getTarget().accept(this, known);
        }
        for (Expression expression : md.getGroupBy()) {
            known = (Set)expression.accept(this, known);
        }
        if (md.getHaving() != null) {
            known = (Set)md.getHaving().accept(this, known);
        }
        if (md.getWhere() != null) {
            md.getWhere().accept(this, known);
        }
        return old;
    }

    @Override
    public Set<RelationalPath<?>> visit(TemplateExpression<?> expr, Set<RelationalPath<?>> known) {
        for (Object arg : expr.getArgs()) {
            if (!(arg instanceof Expression)) continue;
            known = (Set)((Expression)arg).accept(this, known);
        }
        return known;
    }

    private Set<RelationalPath<?>> visitJoins(Iterable<JoinExpression> joins, Set<RelationalPath<?>> known) {
        for (JoinExpression j : joins) {
            known = (Set)j.getTarget().accept(this, known);
            if (j.getCondition() == null) continue;
            known = (Set)j.getCondition().accept(this, known);
        }
        return known;
    }

    private RelationalPathExtractor() {
    }
}

