/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.querylang.antlrgen.AqlParser$MapExprValueContext
 *  com.google.common.collect.Lists
 */
package com.atlassian.confluence.plugins.cql.fields.dynamic;

import com.atlassian.confluence.plugins.cql.fields.dynamic.ValueExpressionEvaluator;
import com.atlassian.confluence.plugins.cql.impl.CQLIterableStringValueParseTreeVisitor;
import com.atlassian.querylang.antlrgen.AqlParser;
import com.google.common.collect.Lists;
import java.util.List;

public class StringValueEvaluator
implements ValueExpressionEvaluator {
    private final CQLIterableStringValueParseTreeVisitor visitor;

    public StringValueEvaluator(CQLIterableStringValueParseTreeVisitor visitor) {
        this.visitor = visitor;
    }

    @Override
    public List<String> evaluate(AqlParser.MapExprValueContext valueContext) {
        if (valueContext.value() != null) {
            return Lists.newArrayList((Object[])new String[]{valueContext.value().getText()});
        }
        return Lists.newArrayList((Iterable)((Iterable)this.visitor.visitSetOperand(valueContext.setOperand())));
    }
}

