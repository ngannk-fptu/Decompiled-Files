/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.cql.spi.functions.CQLEvaluationContext
 *  com.atlassian.querylang.antlrgen.AqlBaseVisitor
 *  com.atlassian.querylang.antlrgen.AqlParser$DateTimeOperandContext
 *  com.atlassian.querylang.antlrgen.AqlParser$NumericOperandContext
 *  com.atlassian.querylang.antlrgen.AqlParser$SingleValueFunctionOperandContext
 *  com.atlassian.querylang.antlrgen.AqlParser$TextOperandContext
 *  com.atlassian.querylang.antlrgen.AqlParser$ValueContext
 *  com.atlassian.querylang.functions.EvaluationContext
 *  com.atlassian.querylang.lib.functions.FunctionRegistry
 *  com.atlassian.querylang.literals.StringLiteralHelper
 *  org.antlr.v4.runtime.misc.NotNull
 */
package com.atlassian.confluence.plugins.cql.impl;

import com.atlassian.confluence.plugins.cql.spi.functions.CQLEvaluationContext;
import com.atlassian.querylang.antlrgen.AqlBaseVisitor;
import com.atlassian.querylang.antlrgen.AqlParser;
import com.atlassian.querylang.functions.EvaluationContext;
import com.atlassian.querylang.lib.functions.FunctionRegistry;
import com.atlassian.querylang.literals.StringLiteralHelper;
import java.util.List;
import java.util.stream.Collectors;
import org.antlr.v4.runtime.misc.NotNull;

public class CQLStringValueParseTreeVisitor
extends AqlBaseVisitor<String> {
    private final FunctionRegistry functionRegistry;
    private final CQLEvaluationContext evaluationContext;

    public CQLStringValueParseTreeVisitor(FunctionRegistry functionRegistry, CQLEvaluationContext evaluationContext) {
        this.functionRegistry = functionRegistry;
        this.evaluationContext = evaluationContext;
    }

    public String visitTextOperand(@NotNull AqlParser.TextOperandContext ctx) {
        return StringLiteralHelper.stripQuotes((String)ctx.getText());
    }

    public String visitSingleValueFunctionOperand(@NotNull AqlParser.SingleValueFunctionOperandContext ctx) {
        if (this.evaluationContext != null) {
            return this.functionRegistry.getRegisteredSingleValueFunction(ctx.funcName.getText(), ctx.params.size()).invoke(this.extractParameterValues(ctx.params), (EvaluationContext)this.evaluationContext);
        }
        return null;
    }

    public String visitNumericOperand(@NotNull AqlParser.NumericOperandContext ctx) {
        return ctx.getText();
    }

    private List<String> extractParameterValues(List<AqlParser.ValueContext> parameters) {
        return parameters.stream().map(arg_0 -> ((CQLStringValueParseTreeVisitor)this).visitValue(arg_0)).collect(Collectors.toList());
    }

    public String visitDateTimeOperand(@NotNull AqlParser.DateTimeOperandContext ctx) {
        return StringLiteralHelper.stripQuotes((String)ctx.getText());
    }
}

