/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.cql.spi.v2searchhelpers.V2FieldHandlerHelper
 *  com.atlassian.confluence.plugins.cql.spi.v2searchhelpers.V2SearchQueryWrapper
 *  com.atlassian.confluence.search.v2.SearchFieldNames
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.query.TermQuery
 *  com.atlassian.querylang.fields.BaseFieldHandler
 *  com.atlassian.querylang.fields.EqualityFieldHandler
 *  com.atlassian.querylang.fields.expressiondata.EqualityExpressionData
 *  com.atlassian.querylang.fields.expressiondata.EqualityExpressionData$Operator
 *  com.atlassian.querylang.fields.expressiondata.ExpressionData
 *  com.atlassian.querylang.fields.expressiondata.SetExpressionData
 *  com.atlassian.querylang.fields.expressiondata.SetExpressionData$Operator
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.confluence.plugins.retentionrules.impl;

import com.atlassian.confluence.plugins.cql.spi.v2searchhelpers.V2FieldHandlerHelper;
import com.atlassian.confluence.plugins.cql.spi.v2searchhelpers.V2SearchQueryWrapper;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.query.TermQuery;
import com.atlassian.querylang.fields.BaseFieldHandler;
import com.atlassian.querylang.fields.EqualityFieldHandler;
import com.atlassian.querylang.fields.expressiondata.EqualityExpressionData;
import com.atlassian.querylang.fields.expressiondata.ExpressionData;
import com.atlassian.querylang.fields.expressiondata.SetExpressionData;
import com.google.common.collect.ImmutableSet;
import java.util.Collections;
import java.util.Set;

public class RetentionPolicyFieldHandler
extends BaseFieldHandler
implements EqualityFieldHandler<String, V2SearchQueryWrapper> {
    public static final String CQL_FIELD_NAME = "retentionPolicy";

    public RetentionPolicyFieldHandler() {
        super(CQL_FIELD_NAME);
    }

    public V2SearchQueryWrapper build(SetExpressionData expressionData, Iterable<String> values) {
        this.validateSupportedOp((Enum)((SetExpressionData.Operator)expressionData.getOperator()), Collections.emptySet());
        return null;
    }

    public V2SearchQueryWrapper build(EqualityExpressionData equalityExpressionData, String value) {
        this.validateSupportedOp((Enum)((EqualityExpressionData.Operator)equalityExpressionData.getOperator()), (Set)ImmutableSet.of((Object)EqualityExpressionData.Operator.EQUALS, (Object)EqualityExpressionData.Operator.NOT_EQUALS));
        return V2FieldHandlerHelper.wrapV2Search((SearchQuery)this.createEqualityQuery(value), (ExpressionData)equalityExpressionData);
    }

    private TermQuery createEqualityQuery(String value) {
        return new TermQuery(SearchFieldNames.RETENTION_POLICY, value);
    }
}

