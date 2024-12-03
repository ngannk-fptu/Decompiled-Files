/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.querylang.antlrgen.AqlBaseVisitor
 *  com.atlassian.querylang.antlrgen.AqlParser$AqlStatementContext
 *  com.atlassian.querylang.antlrgen.AqlParser$OrderDirectionContext
 *  com.atlassian.querylang.antlrgen.AqlParser$OrderbyClauseContext
 *  com.atlassian.querylang.antlrgen.AqlParser$OrderbyContext
 *  com.atlassian.querylang.fields.FieldHandler
 *  com.atlassian.querylang.lib.fields.FieldRegistry
 *  com.atlassian.querylang.query.FieldOrder
 *  com.atlassian.querylang.query.OrderDirection
 *  org.antlr.v4.runtime.misc.NotNull
 *  org.antlr.v4.runtime.tree.ParseTree
 */
package com.atlassian.confluence.plugins.cql.impl;

import com.atlassian.querylang.antlrgen.AqlBaseVisitor;
import com.atlassian.querylang.antlrgen.AqlParser;
import com.atlassian.querylang.fields.FieldHandler;
import com.atlassian.querylang.lib.fields.FieldRegistry;
import com.atlassian.querylang.query.FieldOrder;
import com.atlassian.querylang.query.OrderDirection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTree;

public class CQLtoFieldOrderParseTreeVisitor
extends AqlBaseVisitor<Iterable<FieldOrder>> {
    private FieldRegistry fieldRegistry;

    public CQLtoFieldOrderParseTreeVisitor(FieldRegistry fieldRegistry) {
        this.fieldRegistry = fieldRegistry;
    }

    protected Iterable<FieldOrder> defaultResult() {
        return Collections.emptyList();
    }

    public Iterable<FieldOrder> visitAqlStatement(@NotNull AqlParser.AqlStatementContext ctx) {
        if (ctx.orderby() != null) {
            return this.visitOrderby(ctx.orderby());
        }
        return Collections.emptyList();
    }

    public Iterable<FieldOrder> visitOrderby(@NotNull AqlParser.OrderbyContext ctx) {
        ArrayList<FieldOrder> fieldOrders = new ArrayList<FieldOrder>();
        for (AqlParser.OrderbyClauseContext clause : ctx.orderbyClause()) {
            Iterator iterator = this.visitOrderbyClause(clause).iterator();
            while (iterator.hasNext()) {
                FieldOrder order = (FieldOrder)iterator.next();
                fieldOrders.add(order);
            }
        }
        return fieldOrders;
    }

    public Iterable<FieldOrder> visitOrderbyClause(@NotNull AqlParser.OrderbyClauseContext ctx) {
        OrderDirection direction = this.getDirection(ctx.orderDirection());
        String field = ctx.orderbyField().getText();
        if (!this.fieldRegistry.isRegisteredOrderByField(field)) {
            throw new IllegalArgumentException("Cannot order by " + field);
        }
        FieldHandler handler = this.fieldRegistry.getOrderByFieldHandler(field);
        return Collections.singletonList(handler.buildOrder(direction));
    }

    private OrderDirection getDirection(AqlParser.OrderDirectionContext ctx) {
        if (ctx == null || ctx.getChildCount() == 0) {
            return OrderDirection.ASC;
        }
        ParseTree direction = ctx.getChild(0);
        if (direction.equals(ctx.K_ASC())) {
            return OrderDirection.ASC;
        }
        if (direction.equals(ctx.K_DESC())) {
            return OrderDirection.DESC;
        }
        throw new IllegalArgumentException("Could not determine direction from context " + ctx);
    }
}

