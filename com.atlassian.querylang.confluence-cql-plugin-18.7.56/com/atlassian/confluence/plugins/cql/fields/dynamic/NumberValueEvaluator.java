/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.querylang.antlrgen.AqlParser$MapExprValueContext
 */
package com.atlassian.confluence.plugins.cql.fields.dynamic;

import com.atlassian.confluence.plugins.cql.fields.dynamic.ValueExpressionEvaluator;
import com.atlassian.confluence.plugins.cql.impl.CQLStringValueParseTreeVisitor;
import com.atlassian.querylang.antlrgen.AqlParser;

public class NumberValueEvaluator
implements ValueExpressionEvaluator {
    private final CQLStringValueParseTreeVisitor visitor;

    public NumberValueEvaluator(CQLStringValueParseTreeVisitor visitor) {
        this.visitor = visitor;
    }

    @Override
    public String evaluate(AqlParser.MapExprValueContext valueContext) {
        if (valueContext.numericValue() != null) {
            return (String)this.visitor.visitNumericValue(valueContext.numericValue());
        }
        return valueContext.value().getText();
    }
}

