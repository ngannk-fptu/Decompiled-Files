/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.querylang.antlrgen.AqlParser$MapExprValueContext
 *  com.atlassian.querylang.fields.expressiondata.EqualityExpressionData$Operator
 *  com.atlassian.querylang.fields.expressiondata.RangeExpressionData$Operator
 *  com.atlassian.querylang.fields.expressiondata.SetExpressionData$Operator
 *  com.atlassian.querylang.fields.expressiondata.TextExpressionData$Operator
 *  com.atlassian.querylang.lib.fields.expressiondata.ExpressionDataFactory
 *  com.google.common.collect.Sets
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.contentproperty.search.fields;

import com.atlassian.confluence.plugins.contentproperty.index.schema.SchemaFieldType;
import com.atlassian.querylang.antlrgen.AqlParser;
import com.atlassian.querylang.fields.expressiondata.EqualityExpressionData;
import com.atlassian.querylang.fields.expressiondata.RangeExpressionData;
import com.atlassian.querylang.fields.expressiondata.SetExpressionData;
import com.atlassian.querylang.fields.expressiondata.TextExpressionData;
import com.atlassian.querylang.lib.fields.expressiondata.ExpressionDataFactory;
import com.google.common.collect.Sets;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContentPropertyValueExpressionValidator {
    private static final Logger log = LoggerFactory.getLogger(ContentPropertyValueExpressionValidator.class);
    private ExpressionDataFactory expressionDataFactory = new ExpressionDataFactory();

    public boolean isOperatorValidForFieldType(SchemaFieldType fieldType, AqlParser.MapExprValueContext mapExprContext) {
        switch (fieldType) {
            case TEXT: {
                return this.isTextOpValid(mapExprContext);
            }
            case STRING: {
                return this.isStringOpValid(mapExprContext);
            }
            case DATE: 
            case NUMBER: {
                return this.isRangeOpValid(mapExprContext);
            }
        }
        log.warn("Unrecognised index schema field type '{}'", (Object)fieldType);
        return false;
    }

    private boolean isTextOpValid(AqlParser.MapExprValueContext mapExprContext) {
        if (mapExprContext.textOp() != null) {
            TextExpressionData.Operator operator = (TextExpressionData.Operator)this.expressionDataFactory.create("", mapExprContext.textOp()).getOperator();
            return this.validateSupportedOp(operator, Sets.newHashSet((Object[])new TextExpressionData.Operator[]{TextExpressionData.Operator.CONTAINS, TextExpressionData.Operator.NOT_CONTAINS}));
        }
        return false;
    }

    private boolean isStringOpValid(AqlParser.MapExprValueContext mapExprContext) {
        if (mapExprContext.setOp() != null) {
            SetExpressionData.Operator operator = (SetExpressionData.Operator)this.expressionDataFactory.create("", mapExprContext.setOp()).getOperator();
            return this.validateSupportedOp(operator, Sets.newHashSet((Object[])new SetExpressionData.Operator[]{SetExpressionData.Operator.IN, SetExpressionData.Operator.NOT_IN}));
        }
        return this.isEqualityOpValid(mapExprContext);
    }

    private boolean isRangeOpValid(AqlParser.MapExprValueContext mapExprContext) {
        if (mapExprContext.rangeOp() != null) {
            RangeExpressionData.Operator operator = (RangeExpressionData.Operator)this.expressionDataFactory.create("", mapExprContext.rangeOp()).getOperator();
            return this.validateSupportedOp(operator, Sets.newHashSet((Object[])new RangeExpressionData.Operator[]{RangeExpressionData.Operator.EQUALS, RangeExpressionData.Operator.NOT_EQUALS, RangeExpressionData.Operator.GREATER, RangeExpressionData.Operator.LESS, RangeExpressionData.Operator.GREATER_OR_EQUALS, RangeExpressionData.Operator.LESS_OR_EQUALS}));
        }
        return this.isEqualityOpValid(mapExprContext);
    }

    private boolean isEqualityOpValid(AqlParser.MapExprValueContext mapExprValueContext) {
        if (mapExprValueContext.eqOp() != null) {
            EqualityExpressionData.Operator operator = (EqualityExpressionData.Operator)this.expressionDataFactory.create("", mapExprValueContext.eqOp()).getOperator();
            return this.validateSupportedOp(operator, Sets.newHashSet((Object[])new EqualityExpressionData.Operator[]{EqualityExpressionData.Operator.EQUALS, EqualityExpressionData.Operator.NOT_EQUALS}));
        }
        return false;
    }

    protected <T extends Enum> boolean validateSupportedOp(T operator, Set<T> operators) {
        return operators.contains(operator);
    }
}

