/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.types;

import com.querydsl.core.JoinExpression;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.types.Constant;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.FactoryExpression;
import com.querydsl.core.types.Operation;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.ParamExpression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.TemplateExpression;
import com.querydsl.core.types.Visitor;
import com.querydsl.core.util.CollectionUtils;
import java.io.Serializable;
import java.util.Set;

public final class ValidatingVisitor
implements Visitor<Set<Expression<?>>, Set<Expression<?>>>,
Serializable {
    private static final long serialVersionUID = 691350069621050872L;
    public static final ValidatingVisitor DEFAULT = new ValidatingVisitor();
    private final String errorTemplate;

    public ValidatingVisitor() {
        this.errorTemplate = "Undeclared path '%s'. Add this path as a source to the query to be able to reference it.";
    }

    public ValidatingVisitor(String errorTemplate) {
        this.errorTemplate = errorTemplate;
    }

    @Override
    public Set<Expression<?>> visit(Constant<?> expr, Set<Expression<?>> known) {
        return known;
    }

    @Override
    public Set<Expression<?>> visit(FactoryExpression<?> expr, Set<Expression<?>> known) {
        for (Expression<?> arg : expr.getArgs()) {
            known = (Set)arg.accept(this, known);
        }
        return known;
    }

    @Override
    public Set<Expression<?>> visit(Operation<?> expr, Set<Expression<?>> known) {
        if (expr.getOperator() == Ops.ALIAS) {
            known = CollectionUtils.add(known, expr.getArg(1));
        }
        for (Expression<?> arg : expr.getArgs()) {
            known = (Set)arg.accept(this, known);
        }
        return known;
    }

    @Override
    public Set<Expression<?>> visit(ParamExpression<?> expr, Set<Expression<?>> known) {
        return known;
    }

    @Override
    public Set<Expression<?>> visit(Path<?> expr, Set<Expression<?>> known) {
        if (!known.contains(expr.getRoot())) {
            throw new IllegalArgumentException(String.format(this.errorTemplate, expr.getRoot()));
        }
        return known;
    }

    @Override
    public Set<Expression<?>> visit(SubQueryExpression<?> expr, Set<Expression<?>> known) {
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
            known = (Set)md.getWhere().accept(this, known);
        }
        return old;
    }

    @Override
    public Set<Expression<?>> visit(TemplateExpression<?> expr, Set<Expression<?>> known) {
        for (Object arg : expr.getArgs()) {
            if (!(arg instanceof Expression)) continue;
            known = (Set)((Expression)arg).accept(this, known);
        }
        return known;
    }

    private Set<Expression<?>> visitJoins(Iterable<JoinExpression> joins, Set<Expression<?>> known) {
        for (JoinExpression j : joins) {
            Expression<?> expr = j.getTarget();
            known = expr instanceof Path && ((Path)expr).getMetadata().isRoot() ? CollectionUtils.add(known, expr) : (Set)expr.accept(this, known);
            if (j.getCondition() == null) continue;
            known = (Set)j.getCondition().accept(this, known);
        }
        return known;
    }
}

