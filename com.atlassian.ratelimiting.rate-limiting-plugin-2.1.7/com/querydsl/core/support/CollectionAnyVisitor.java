/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.support;

import com.querydsl.core.support.Context;
import com.querydsl.core.types.CollectionExpression;
import com.querydsl.core.types.Constant;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.FactoryExpression;
import com.querydsl.core.types.Operation;
import com.querydsl.core.types.ParamExpression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.PathMetadataFactory;
import com.querydsl.core.types.PathType;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.PredicateOperation;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.TemplateExpression;
import com.querydsl.core.types.Visitor;
import com.querydsl.core.types.dsl.BooleanTemplate;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.SimplePath;

public class CollectionAnyVisitor
implements Visitor<Expression<?>, Context> {
    private int replacedCounter;

    private static <T> Path<T> replaceParent(Path<T> path, Path<?> parent) {
        PathMetadata metadata = new PathMetadata(parent, path.getMetadata().getElement(), path.getMetadata().getPathType());
        if (path instanceof CollectionExpression) {
            CollectionExpression col = (CollectionExpression)((Object)path);
            return Expressions.listPath(col.getParameter(0), SimplePath.class, metadata);
        }
        return ExpressionUtils.path(path.getType(), metadata);
    }

    @Override
    public Expression<?> visit(Constant<?> expr, Context context) {
        return expr;
    }

    @Override
    public Expression<?> visit(TemplateExpression<?> expr, Context context) {
        Object[] args = new Object[expr.getArgs().size()];
        for (int i = 0; i < args.length; ++i) {
            Context c = new Context();
            args[i] = expr.getArg(i) instanceof Expression ? ((Expression)expr.getArg(i)).accept(this, c) : expr.getArg(i);
            context.add(c);
        }
        if (context.replace) {
            if (expr.getType().equals(Boolean.class)) {
                BooleanTemplate predicate = Expressions.booleanTemplate(expr.getTemplate(), args);
                return !context.paths.isEmpty() ? this.exists(context, predicate) : predicate;
            }
            return ExpressionUtils.template(expr.getType(), expr.getTemplate(), args);
        }
        return expr;
    }

    @Override
    public Expression<?> visit(FactoryExpression<?> expr, Context context) {
        return expr;
    }

    @Override
    public Expression<?> visit(Operation<?> expr, Context context) {
        Expression[] args = new Expression[expr.getArgs().size()];
        for (int i = 0; i < args.length; ++i) {
            Context c = new Context();
            args[i] = (Expression)expr.getArg(i).accept(this, c);
            context.add(c);
        }
        if (context.replace) {
            if (expr.getType().equals(Boolean.class)) {
                PredicateOperation predicate = ExpressionUtils.predicate(expr.getOperator(), args);
                return !context.paths.isEmpty() ? this.exists(context, predicate) : predicate;
            }
            return ExpressionUtils.operation(expr.getType(), expr.getOperator(), args);
        }
        return expr;
    }

    protected Predicate exists(Context c, Predicate condition) {
        return condition;
    }

    @Override
    public Expression<?> visit(Path<?> expr, Context context) {
        if (expr.getMetadata().getPathType() == PathType.COLLECTION_ANY) {
            Path parent = (Path)expr.getMetadata().getParent().accept(this, context);
            expr = ExpressionUtils.path(expr.getType(), PathMetadataFactory.forCollectionAny(parent));
            EntityPathBase replacement = new EntityPathBase(expr.getType(), ExpressionUtils.createRootVariable(expr, this.replacedCounter++));
            context.add(expr, replacement);
            return replacement;
        }
        if (expr.getMetadata().getParent() != null) {
            Context c = new Context();
            Path parent = (Path)expr.getMetadata().getParent().accept(this, c);
            if (c.replace) {
                context.add(c);
                return CollectionAnyVisitor.replaceParent(expr, parent);
            }
        }
        return expr;
    }

    @Override
    public Expression<?> visit(SubQueryExpression<?> expr, Context context) {
        return expr;
    }

    @Override
    public Expression<?> visit(ParamExpression<?> expr, Context context) {
        return expr;
    }
}

