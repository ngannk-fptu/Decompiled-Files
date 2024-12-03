/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.cql.spi.functions.CQLEvaluationContext
 *  com.atlassian.querylang.antlrgen.AqlParser$MultiValueFunctionOperandContext
 *  com.atlassian.querylang.antlrgen.AqlParser$ValueContext
 *  com.atlassian.querylang.functions.EvaluationContext
 *  com.atlassian.querylang.lib.functions.FunctionRegistry
 *  com.atlassian.querylang.lib.visitor.AqlBaseVisitorToIterable
 *  com.google.common.collect.Lists
 *  org.antlr.v4.runtime.misc.NotNull
 */
package com.atlassian.confluence.plugins.cql.impl;

import com.atlassian.confluence.plugins.cql.impl.CQLStringValueParseTreeVisitor;
import com.atlassian.confluence.plugins.cql.spi.functions.CQLEvaluationContext;
import com.atlassian.querylang.antlrgen.AqlParser;
import com.atlassian.querylang.functions.EvaluationContext;
import com.atlassian.querylang.lib.functions.FunctionRegistry;
import com.atlassian.querylang.lib.visitor.AqlBaseVisitorToIterable;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.antlr.v4.runtime.misc.NotNull;

public class CQLIterableStringValueParseTreeVisitor
extends AqlBaseVisitorToIterable<String> {
    private final FunctionRegistry functionRegistry;
    private final CQLEvaluationContext evaluationContext;
    private final CQLStringValueParseTreeVisitor stringValueParseTreeVisitor;

    public CQLIterableStringValueParseTreeVisitor(FunctionRegistry functionRegistry, CQLEvaluationContext evaluationContext, CQLStringValueParseTreeVisitor stringValueParseTreeVisitor) {
        this.functionRegistry = functionRegistry;
        this.evaluationContext = evaluationContext;
        this.stringValueParseTreeVisitor = stringValueParseTreeVisitor;
    }

    public Iterable<String> visitMultiValueFunctionOperand(@NotNull AqlParser.MultiValueFunctionOperandContext ctx) {
        if (this.evaluationContext != null) {
            return Lists.newArrayList((Iterable)this.functionRegistry.getRegisteredMultiValueFunction(ctx.funcName.getText(), ctx.params.size()).invoke(this.extractParameterValues(ctx.params), (EvaluationContext)this.evaluationContext));
        }
        return new ArrayList<String>();
    }

    public Iterable<String> visitValue(@NotNull AqlParser.ValueContext ctx) {
        return Lists.newArrayList((Object[])new String[]{(String)this.stringValueParseTreeVisitor.visitValue(ctx)});
    }

    private List<String> extractParameterValues(List<AqlParser.ValueContext> parameters) {
        return parameters.stream().map(arg_0 -> ((CQLStringValueParseTreeVisitor)this.stringValueParseTreeVisitor).visitValue(arg_0)).collect(Collectors.toList());
    }
}

