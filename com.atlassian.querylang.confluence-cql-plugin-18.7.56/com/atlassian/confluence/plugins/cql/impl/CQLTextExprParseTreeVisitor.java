/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.querylang.antlrgen.AqlParser$TextExprContext
 *  com.atlassian.querylang.lib.visitor.AqlBaseVisitorToIterable
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  org.antlr.v4.runtime.misc.NotNull
 *  org.antlr.v4.runtime.tree.ParseTree
 */
package com.atlassian.confluence.plugins.cql.impl;

import com.atlassian.confluence.plugins.cql.impl.CQLStringValueParseTreeVisitor;
import com.atlassian.querylang.antlrgen.AqlParser;
import com.atlassian.querylang.lib.visitor.AqlBaseVisitorToIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTree;

public class CQLTextExprParseTreeVisitor
extends AqlBaseVisitorToIterable<String> {
    private final CQLStringValueParseTreeVisitor cqlStringValueParseTreeVisitor;

    public CQLTextExprParseTreeVisitor(CQLStringValueParseTreeVisitor stringValueParseTreeVisitor) {
        this.cqlStringValueParseTreeVisitor = stringValueParseTreeVisitor;
    }

    public Iterable<String> visit(@NotNull ParseTree tree) {
        return ImmutableList.builder().addAll((Iterable)super.visit(tree)).build();
    }

    public Iterable<String> visitTextExpr(@NotNull AqlParser.TextExprContext ctx) {
        if (ctx.textOp().OP_LIKE() != null) {
            ArrayList strings = Lists.newArrayList((Object[])new String[]{(String)this.cqlStringValueParseTreeVisitor.visitTextExpr(ctx)});
            return strings;
        }
        return Collections.emptyList();
    }
}

