/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.cql.spi.v2searchhelpers.V2FieldHandlerHelper
 *  com.atlassian.confluence.plugins.cql.spi.v2searchhelpers.V2SearchQueryWrapper
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.query.LabelQuery
 *  com.atlassian.querylang.fields.BaseFieldHandler
 *  com.atlassian.querylang.fields.EqualityFieldHandler
 *  com.atlassian.querylang.fields.expressiondata.EqualityExpressionData
 *  com.atlassian.querylang.fields.expressiondata.EqualityExpressionData$Operator
 *  com.atlassian.querylang.fields.expressiondata.ExpressionData
 *  com.atlassian.querylang.fields.expressiondata.SetExpressionData
 *  com.atlassian.querylang.fields.expressiondata.SetExpressionData$Operator
 *  com.google.common.collect.Sets
 */
package com.atlassian.confluence.plugins.cql.fields;

import com.atlassian.confluence.plugins.cql.spi.v2searchhelpers.V2FieldHandlerHelper;
import com.atlassian.confluence.plugins.cql.spi.v2searchhelpers.V2SearchQueryWrapper;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.query.LabelQuery;
import com.atlassian.querylang.fields.BaseFieldHandler;
import com.atlassian.querylang.fields.EqualityFieldHandler;
import com.atlassian.querylang.fields.expressiondata.EqualityExpressionData;
import com.atlassian.querylang.fields.expressiondata.ExpressionData;
import com.atlassian.querylang.fields.expressiondata.SetExpressionData;
import com.google.common.collect.Sets;

public class LabelFieldHandler
extends BaseFieldHandler
implements EqualityFieldHandler<String, V2SearchQueryWrapper> {
    private static final String FIELD_NAME = "label";

    public LabelFieldHandler() {
        super(FIELD_NAME);
    }

    public V2SearchQueryWrapper build(SetExpressionData expressionData, Iterable<String> values) {
        this.validateSupportedOp((Enum)((SetExpressionData.Operator)expressionData.getOperator()), Sets.newHashSet((Object[])new SetExpressionData.Operator[]{SetExpressionData.Operator.IN, SetExpressionData.Operator.NOT_IN}));
        SearchQuery query = V2FieldHandlerHelper.joinSingleValueSearchQueries(values, LabelQuery::new);
        return V2FieldHandlerHelper.wrapV2Search((SearchQuery)query, (ExpressionData)expressionData);
    }

    public V2SearchQueryWrapper build(EqualityExpressionData expressionData, String value) {
        this.validateSupportedOp((Enum)((EqualityExpressionData.Operator)expressionData.getOperator()), Sets.newHashSet((Object[])new EqualityExpressionData.Operator[]{EqualityExpressionData.Operator.EQUALS, EqualityExpressionData.Operator.NOT_EQUALS}));
        return V2FieldHandlerHelper.wrapV2Search((SearchQuery)new LabelQuery(value), (ExpressionData)expressionData);
    }
}

