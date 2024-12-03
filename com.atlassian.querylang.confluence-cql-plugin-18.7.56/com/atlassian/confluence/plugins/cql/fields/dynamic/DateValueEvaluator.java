/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.querylang.antlrgen.AqlParser$MapExprValueContext
 *  com.atlassian.querylang.fields.DateTimePrecision
 *  com.atlassian.querylang.literals.DateLiteralHelper
 */
package com.atlassian.confluence.plugins.cql.fields.dynamic;

import com.atlassian.confluence.plugins.cql.fields.dynamic.ValueExpressionEvaluator;
import com.atlassian.confluence.plugins.cql.impl.CQLStringValueParseTreeVisitor;
import com.atlassian.querylang.antlrgen.AqlParser;
import com.atlassian.querylang.fields.DateTimePrecision;
import com.atlassian.querylang.literals.DateLiteralHelper;

public class DateValueEvaluator
implements ValueExpressionEvaluator {
    private final CQLStringValueParseTreeVisitor visitor;

    public DateValueEvaluator(CQLStringValueParseTreeVisitor visitor) {
        this.visitor = visitor;
    }

    public DateTimePrecision evaluate(AqlParser.MapExprValueContext valueContext) {
        return DateLiteralHelper.create((String)this.getDateTimeAsAString(valueContext));
    }

    private String getDateTimeAsAString(AqlParser.MapExprValueContext valueContext) {
        if (valueContext.dateTimeValue() != null) {
            return (String)this.visitor.visitDateTimeValue(valueContext.dateTimeValue());
        }
        return valueContext.value().getText();
    }
}

