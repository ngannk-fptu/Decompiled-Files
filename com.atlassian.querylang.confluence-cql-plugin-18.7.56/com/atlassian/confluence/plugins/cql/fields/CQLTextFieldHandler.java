/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.cql.spi.v2searchhelpers.V2FieldHandlerHelper
 *  com.atlassian.confluence.plugins.cql.spi.v2searchhelpers.V2SearchQueryWrapper
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.query.MultiTextFieldQuery
 *  com.atlassian.confluence.search.v2.query.TextQuery
 *  com.atlassian.querylang.fields.BaseFieldHandler
 *  com.atlassian.querylang.fields.TextFieldHandler
 *  com.atlassian.querylang.fields.expressiondata.ExpressionData
 *  com.atlassian.querylang.fields.expressiondata.TextExpressionData
 *  com.atlassian.querylang.fields.expressiondata.TextExpressionData$Operator
 *  com.google.common.collect.Sets
 */
package com.atlassian.confluence.plugins.cql.fields;

import com.atlassian.confluence.plugins.cql.spi.v2searchhelpers.V2FieldHandlerHelper;
import com.atlassian.confluence.plugins.cql.spi.v2searchhelpers.V2SearchQueryWrapper;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.query.MultiTextFieldQuery;
import com.atlassian.confluence.search.v2.query.TextQuery;
import com.atlassian.querylang.fields.BaseFieldHandler;
import com.atlassian.querylang.fields.TextFieldHandler;
import com.atlassian.querylang.fields.expressiondata.ExpressionData;
import com.atlassian.querylang.fields.expressiondata.TextExpressionData;
import com.google.common.collect.Sets;

public class CQLTextFieldHandler
extends BaseFieldHandler
implements TextFieldHandler<V2SearchQueryWrapper> {
    private static final String FIELD_NAME = "text";

    public CQLTextFieldHandler() {
        super(FIELD_NAME);
    }

    private MultiTextFieldQuery createQuery(String query) {
        return new TextQuery(query);
    }

    public V2SearchQueryWrapper build(TextExpressionData expressionData, String value) {
        this.validateSupportedOp((Enum)((TextExpressionData.Operator)expressionData.getOperator()), Sets.newHashSet((Object[])new TextExpressionData.Operator[]{TextExpressionData.Operator.CONTAINS, TextExpressionData.Operator.NOT_CONTAINS}));
        return V2FieldHandlerHelper.wrapV2Search((SearchQuery)this.createQuery(value), (ExpressionData)expressionData);
    }
}

