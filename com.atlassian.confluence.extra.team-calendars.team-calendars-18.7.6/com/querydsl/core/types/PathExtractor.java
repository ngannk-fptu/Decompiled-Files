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
import java.util.List;

public final class PathExtractor
implements Visitor<Path<?>, Void> {
    public static final PathExtractor DEFAULT = new PathExtractor();

    private PathExtractor() {
    }

    @Override
    public Path<?> visit(Constant<?> expr, Void context) {
        return null;
    }

    @Override
    public Path<?> visit(FactoryExpression<?> expr, Void context) {
        return this.visit(expr.getArgs());
    }

    @Override
    public Path<?> visit(Operation<?> expr, Void context) {
        return this.visit(expr.getArgs());
    }

    @Override
    public Path<?> visit(ParamExpression<?> expr, Void context) {
        return null;
    }

    @Override
    public Path<?> visit(Path<?> expr, Void context) {
        return expr;
    }

    @Override
    public Path<?> visit(SubQueryExpression<?> expr, Void context) {
        return null;
    }

    @Override
    public Path<?> visit(TemplateExpression<?> expr, Void context) {
        return this.visit(expr.getArgs());
    }

    private Path<?> visit(List<?> exprs) {
        for (Object e : exprs) {
            Path path;
            if (!(e instanceof Expression) || (path = (Path)((Expression)e).accept(this, null)) == null) continue;
            return path;
        }
        return null;
    }
}

