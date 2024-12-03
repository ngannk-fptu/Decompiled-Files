/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.fugue.Option
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.pocketknife.internal.querydsl.schema;

import com.atlassian.pocketknife.internal.querydsl.util.Unit;
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
import com.querydsl.sql.RelationalPath;
import io.atlassian.fugue.Option;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RelationPathsInQueryMetadata {
    private static final Logger log = LoggerFactory.getLogger(RelationPathsInQueryMetadata.class);
    private static final Runnable NOOP = () -> {};

    public Set<RelationalPath<?>> capture(QueryMetadata metadata) {
        return new CaptureVisitor().traverse(metadata, new CaptureCtx()).getRelationalPaths();
    }

    static class CaptureVisitor
    implements Visitor<Unit, CaptureCtx> {
        CaptureVisitor() {
        }

        private CaptureCtx traverse(QueryMetadata metadata, CaptureCtx context) {
            Option select = Option.option(metadata.getProjection());
            Option joins = Option.option(metadata.getJoins());
            Option where = Option.option((Object)metadata.getWhere());
            Option groupBys = Option.option(metadata.getGroupBy());
            Option having = Option.option((Object)metadata.getHaving());
            Option orderBys = Option.option(metadata.getOrderBy());
            select.forEach(e -> this.handle((Expression<?>)e, context));
            joins.forEach(list -> list.forEach(join -> this.handle(join.getTarget(), context)));
            where.forEach(e -> this.handle((Expression<?>)e, context));
            groupBys.forEach(list -> list.forEach(e -> this.handle((Expression<?>)e, context)));
            having.forEach(e -> this.handle((Expression<?>)e, context));
            orderBys.forEach(list -> list.forEach(ob -> this.handle(ob.getTarget(), context)));
            return context;
        }

        private void handle(Expression<?> expr, CaptureCtx context) {
            if (expr != null) {
                expr.accept(this, context);
            }
        }

        private void acceptExpressions(List<Expression<?>> args, CaptureCtx context) {
            for (Expression<?> arg : args) {
                arg.accept(this, context);
            }
        }

        private Unit enter(String place, Expression<?> expr, Runnable runnable) {
            if (log.isDebugEnabled()) {
                log.debug("Entering %s - type %s", (Object)place, (Object)expr.getClass().getName());
            }
            runnable.run();
            if (log.isDebugEnabled()) {
                log.debug("\tExiting %s - type %s", (Object)place, (Object)expr.getClass().getName());
            }
            return Unit.VALUE;
        }

        @Override
        public Unit visit(Path<?> expr, CaptureCtx context) {
            return this.enter("Path", expr, () -> {
                if (expr instanceof RelationalPath) {
                    RelationalPath relationalPath = (RelationalPath)expr;
                    context.getRelationalPaths().add(relationalPath);
                }
                Option parent = Option.option((Object)expr.getMetadata()).flatMap(md -> Option.option(md.getParent()));
                parent.forEach(parentPath -> {
                    if (parentPath != expr) {
                        this.visit((Path<?>)parentPath, context);
                    }
                });
            });
        }

        @Override
        public Unit visit(FactoryExpression<?> expr, CaptureCtx context) {
            return this.enter("FactoryExpression", expr, () -> {
                List<Expression<?>> args = expr.getArgs();
                this.acceptExpressions(args, context);
            });
        }

        @Override
        public Unit visit(Operation<?> expr, CaptureCtx context) {
            return this.enter("Operation", expr, () -> {
                List<Expression<?>> args = expr.getArgs();
                this.acceptExpressions(args, context);
            });
        }

        @Override
        public Unit visit(SubQueryExpression<?> expr, CaptureCtx context) {
            return this.enter("SubQueryExpression", expr, () -> {
                QueryMetadata metadata = expr.getMetadata();
                this.traverse(metadata, context);
            });
        }

        @Override
        public Unit visit(Constant<?> expr, CaptureCtx context) {
            return this.enter("Constant", expr, NOOP);
        }

        @Override
        public Unit visit(ParamExpression<?> expr, CaptureCtx context) {
            return this.enter("ParamExpression", expr, NOOP);
        }

        @Override
        public Unit visit(TemplateExpression<?> expr, CaptureCtx context) {
            return this.enter("TemplateExpression", expr, NOOP);
        }
    }

    static class CaptureCtx {
        private final Set<RelationalPath<?>> relationalPaths = new HashSet();

        CaptureCtx() {
        }

        public Set<RelationalPath<?>> getRelationalPaths() {
            return this.relationalPaths;
        }
    }
}

