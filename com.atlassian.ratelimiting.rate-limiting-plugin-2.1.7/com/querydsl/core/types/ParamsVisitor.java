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
import com.querydsl.core.types.ParamExpression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.TemplateExpression;
import com.querydsl.core.types.Visitor;
import java.util.List;
import java.util.Map;

public final class ParamsVisitor
implements Visitor<Void, QueryMetadata> {
    public static final ParamsVisitor DEFAULT = new ParamsVisitor();

    private ParamsVisitor() {
    }

    @Override
    public Void visit(Constant<?> expr, QueryMetadata context) {
        return null;
    }

    @Override
    public Void visit(FactoryExpression<?> expr, QueryMetadata context) {
        this.visit(expr.getArgs(), context);
        return null;
    }

    @Override
    public Void visit(Operation<?> expr, QueryMetadata context) {
        this.visit(expr.getArgs(), context);
        return null;
    }

    @Override
    public Void visit(ParamExpression<?> expr, QueryMetadata context) {
        return null;
    }

    @Override
    public Void visit(Path<?> expr, QueryMetadata context) {
        return null;
    }

    @Override
    public Void visit(SubQueryExpression<?> expr, QueryMetadata context) {
        QueryMetadata md = expr.getMetadata();
        for (Map.Entry<ParamExpression<?>, Object> entry : md.getParams().entrySet()) {
            context.setParam(entry.getKey(), entry.getValue());
        }
        this.visit(md.getGroupBy(), context);
        this.visit(md.getHaving(), context);
        for (JoinExpression join : md.getJoins()) {
            this.visit(join.getTarget(), context);
            this.visit(join.getCondition(), context);
        }
        this.visit(md.getProjection(), context);
        this.visit(md.getWhere(), context);
        return null;
    }

    @Override
    public Void visit(TemplateExpression<?> expr, QueryMetadata context) {
        for (Object arg : expr.getArgs()) {
            if (!(arg instanceof Expression)) continue;
            ((Expression)arg).accept(this, context);
        }
        return null;
    }

    @Override
    private void visit(Expression<?> expr, QueryMetadata context) {
        if (expr != null) {
            expr.accept(this, context);
        }
    }

    @Override
    private void visit(List<Expression<?>> exprs, QueryMetadata context) {
        for (Expression<?> arg : exprs) {
            arg.accept(this, context);
        }
    }
}

