/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.querylang.antlrgen.AqlParser$MultiValueFunctionOperandContext
 *  com.atlassian.querylang.antlrgen.AqlParser$SingleValueFunctionOperandContext
 *  com.atlassian.querylang.antlrgen.AqlParser$ValueContext
 *  com.atlassian.querylang.lib.visitor.AqlBaseVisitorToIterable
 *  com.atlassian.querylang.literals.StringLiteralHelper
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  org.antlr.v4.runtime.misc.NotNull
 */
package com.atlassian.confluence.plugins.cql.impl;

import com.atlassian.confluence.plugins.cql.rest.model.FunctionValue;
import com.atlassian.querylang.antlrgen.AqlParser;
import com.atlassian.querylang.lib.visitor.AqlBaseVisitorToIterable;
import com.atlassian.querylang.literals.StringLiteralHelper;
import com.google.common.collect.ImmutableList;
import java.util.Collections;
import java.util.List;
import org.antlr.v4.runtime.misc.NotNull;

public class CQLQueryFunctionValueParseTreeVisitor
extends AqlBaseVisitorToIterable<FunctionValue> {
    public Iterable<FunctionValue> visitSingleValueFunctionOperand(@NotNull AqlParser.SingleValueFunctionOperandContext ctx) {
        return this.visitFunctionOperand(ctx.params, ctx.funcName.getText());
    }

    public Iterable<FunctionValue> visitMultiValueFunctionOperand(@NotNull AqlParser.MultiValueFunctionOperandContext ctx) {
        return this.visitFunctionOperand(ctx.params, ctx.funcName.getText());
    }

    private Iterable<FunctionValue> visitFunctionOperand(List<AqlParser.ValueContext> functionParams, String functionName) {
        ImmutableList.Builder params = ImmutableList.builder();
        for (AqlParser.ValueContext value : functionParams) {
            params.add((Object)StringLiteralHelper.stripQuotes((String)value.getText()));
        }
        return Collections.singletonList(new FunctionValue(functionName, (List<String>)params.build()));
    }
}

