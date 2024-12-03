/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 */
package com.querydsl.core.support;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.querydsl.core.DefaultQueryMetadata;
import com.querydsl.core.JoinExpression;
import com.querydsl.core.JoinFlag;
import com.querydsl.core.QueryFlag;
import com.querydsl.core.types.Constant;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.FactoryExpression;
import com.querydsl.core.types.FactoryExpressionUtils;
import com.querydsl.core.types.Operation;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.ParamExpression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.SubQueryExpressionImpl;
import com.querydsl.core.types.TemplateExpression;
import com.querydsl.core.types.Visitor;
import com.querydsl.core.types.dsl.Expressions;
import java.util.List;
import java.util.Map;

public class ReplaceVisitor<C>
implements Visitor<Expression<?>, C> {
    @Override
    public Expression<?> visit(Constant<?> expr, C context) {
        return expr;
    }

    @Override
    public Expression<?> visit(FactoryExpression<?> expr, C context) {
        ImmutableList<Expression<?>> args = this.visit(expr.getArgs(), context);
        if (args.equals(expr.getArgs())) {
            return expr;
        }
        return FactoryExpressionUtils.wrap(expr, args);
    }

    @Override
    public Expression<?> visit(Operation<?> expr, C context) {
        ImmutableList<Expression<?>> args = this.visit(expr.getArgs(), context);
        if (args.equals(expr.getArgs())) {
            return expr;
        }
        if (expr instanceof Predicate) {
            return ExpressionUtils.predicate(expr.getOperator(), args);
        }
        return ExpressionUtils.operation(expr.getType(), expr.getOperator(), args);
    }

    @Override
    public Expression<?> visit(ParamExpression<?> expr, C context) {
        return expr;
    }

    @Override
    public Expression<?> visit(Path<?> expr, C context) {
        if (expr.getMetadata().isRoot()) {
            return expr;
        }
        PathMetadata metadata = expr.getMetadata();
        Path parent = (Path)metadata.getParent().accept(this, context);
        Object element = metadata.getElement();
        if (element instanceof Expression) {
            element = ((Expression)element).accept(this, context);
        }
        if (parent.equals(metadata.getParent()) && Objects.equal((Object)element, (Object)metadata.getElement())) {
            return expr;
        }
        metadata = new PathMetadata(parent, element, metadata.getPathType());
        return ExpressionUtils.path(expr.getType(), metadata);
    }

    @Override
    public Expression<?> visit(SubQueryExpression<?> expr, C context) {
        Predicate predicate;
        DefaultQueryMetadata md = new DefaultQueryMetadata();
        md.setValidate(false);
        md.setDistinct(expr.getMetadata().isDistinct());
        md.setModifiers(expr.getMetadata().getModifiers());
        md.setUnique(expr.getMetadata().isUnique());
        for (QueryFlag queryFlag : expr.getMetadata().getFlags()) {
            md.addFlag(new QueryFlag(queryFlag.getPosition(), (Expression)queryFlag.getFlag().accept(this, context)));
        }
        for (Expression expression : expr.getMetadata().getGroupBy()) {
            md.addGroupBy((Expression)expression.accept(this, context));
        }
        Predicate having = expr.getMetadata().getHaving();
        if (having != null) {
            md.addHaving((Predicate)having.accept(this, context));
        }
        for (JoinExpression joinExpression : expr.getMetadata().getJoins()) {
            md.addJoin(joinExpression.getType(), (Expression)joinExpression.getTarget().accept(this, context));
            if (joinExpression.getCondition() != null) {
                md.addJoinCondition((Predicate)joinExpression.getCondition().accept(this, context));
            }
            for (JoinFlag jf : joinExpression.getFlags()) {
                md.addJoinFlag(new JoinFlag((Expression)jf.getFlag().accept(this, context), jf.getPosition()));
            }
        }
        for (OrderSpecifier<?> orderSpecifier : expr.getMetadata().getOrderBy()) {
            OrderSpecifier os2 = new OrderSpecifier(orderSpecifier.getOrder(), (Expression)orderSpecifier.getTarget().accept(this, context), orderSpecifier.getNullHandling());
            md.addOrderBy(os2);
        }
        for (Map.Entry<ParamExpression<?>, Object> entry : expr.getMetadata().getParams().entrySet()) {
            md.setParam((ParamExpression)entry.getKey().accept(this, context), entry.getValue());
        }
        if (expr.getMetadata().getProjection() != null) {
            md.setProjection((Expression)expr.getMetadata().getProjection().accept(this, context));
        }
        if ((predicate = expr.getMetadata().getWhere()) != null) {
            md.addWhere((Predicate)predicate.accept(this, context));
        }
        if (expr.getMetadata().equals(md)) {
            return expr;
        }
        return new SubQueryExpressionImpl(expr.getType(), md);
    }

    @Override
    public Expression<?> visit(TemplateExpression<?> expr, C context) {
        ImmutableList.Builder builder = ImmutableList.builder();
        for (Object arg : expr.getArgs()) {
            if (arg instanceof Expression) {
                builder.add(((Expression)arg).accept(this, context));
                continue;
            }
            builder.add(arg);
        }
        ImmutableList args = builder.build();
        if (args.equals(expr.getArgs())) {
            return expr;
        }
        if (expr instanceof Predicate) {
            return Expressions.booleanTemplate(expr.getTemplate(), args);
        }
        return ExpressionUtils.template(expr.getType(), expr.getTemplate(), args);
    }

    @Override
    private ImmutableList<Expression<?>> visit(List<Expression<?>> args, C context) {
        ImmutableList.Builder builder = ImmutableList.builder();
        for (Expression<?> arg : args) {
            builder.add(arg.accept(this, context));
        }
        return builder.build();
    }
}

