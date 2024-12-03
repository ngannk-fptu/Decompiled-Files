/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.types;

import com.querydsl.core.types.Constant;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.FactoryExpression;
import com.querydsl.core.types.Operation;
import com.querydsl.core.types.ParamExpression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.TemplateExpression;
import com.querydsl.core.types.Visitor;

final class ExtractorVisitor
implements Visitor<Expression<?>, Void> {
    public static final ExtractorVisitor DEFAULT = new ExtractorVisitor();

    private ExtractorVisitor() {
    }

    @Override
    public Expression<?> visit(Constant<?> expr, Void context) {
        return expr;
    }

    @Override
    public Expression<?> visit(FactoryExpression<?> expr, Void context) {
        return expr;
    }

    @Override
    public Expression<?> visit(Operation<?> expr, Void context) {
        return expr;
    }

    @Override
    public Expression<?> visit(ParamExpression<?> expr, Void context) {
        return expr;
    }

    @Override
    public Expression<?> visit(Path<?> expr, Void context) {
        return expr;
    }

    @Override
    public Expression<?> visit(SubQueryExpression<?> expr, Void context) {
        return expr;
    }

    @Override
    public Expression<?> visit(TemplateExpression<?> expr, Void context) {
        return expr;
    }
}

