/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.support;

import com.querydsl.core.types.Constant;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.FactoryExpression;
import com.querydsl.core.types.Operation;
import com.querydsl.core.types.ParamExpression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.TemplateExpression;
import com.querydsl.core.types.Visitor;
import java.util.Collection;
import java.util.List;

public final class PathsExtractor
implements Visitor<Void, List<Path<?>>> {
    public static final PathsExtractor DEFAULT = new PathsExtractor();

    private PathsExtractor() {
    }

    @Override
    public Void visit(Constant<?> expr, List<Path<?>> paths) {
        return null;
    }

    @Override
    public Void visit(FactoryExpression<?> expr, List<Path<?>> paths) {
        this.visit(expr.getArgs(), paths);
        return null;
    }

    @Override
    public Void visit(Operation<?> expr, List<Path<?>> paths) {
        this.visit(expr.getArgs(), paths);
        return null;
    }

    @Override
    public Void visit(ParamExpression<?> expr, List<Path<?>> paths) {
        return null;
    }

    @Override
    public Void visit(Path<?> expr, List<Path<?>> paths) {
        paths.add(expr);
        return null;
    }

    @Override
    public Void visit(SubQueryExpression<?> expr, List<Path<?>> paths) {
        return null;
    }

    @Override
    public Void visit(TemplateExpression<?> expr, List<Path<?>> paths) {
        this.visit(expr.getArgs(), paths);
        return null;
    }

    @Override
    public Path<?> visit(Collection<?> exprs, List<Path<?>> paths) {
        for (Object e : exprs) {
            if (!(e instanceof Expression)) continue;
            ((Expression)e).accept(this, paths);
        }
        return null;
    }
}

