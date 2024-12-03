/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.cql.spi.v2searchhelpers.V2FieldHandlerHelper
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.query.SiteTextSearchQuery
 *  com.atlassian.querylang.fields.BaseFieldHandler
 *  com.atlassian.querylang.fields.TextFieldHandler
 *  com.atlassian.querylang.fields.expressiondata.ExpressionData
 *  com.atlassian.querylang.fields.expressiondata.TextExpressionData
 *  com.atlassian.querylang.fields.expressiondata.TextExpressionData$Operator
 *  com.atlassian.querylang.query.SearchQuery
 *  com.google.common.collect.Sets
 */
package com.atlassian.confluence.plugins.search.query;

import com.atlassian.confluence.plugins.cql.spi.v2searchhelpers.V2FieldHandlerHelper;
import com.atlassian.confluence.search.v2.query.SiteTextSearchQuery;
import com.atlassian.querylang.fields.BaseFieldHandler;
import com.atlassian.querylang.fields.TextFieldHandler;
import com.atlassian.querylang.fields.expressiondata.ExpressionData;
import com.atlassian.querylang.fields.expressiondata.TextExpressionData;
import com.atlassian.querylang.query.SearchQuery;
import com.google.common.collect.Sets;

public class SiteSearchFieldHandler
extends BaseFieldHandler
implements TextFieldHandler {
    private static final String CQL_FIELD_NAME = "siteSearch";

    protected SiteSearchFieldHandler() {
        super(CQL_FIELD_NAME, false);
    }

    public SearchQuery build(TextExpressionData expressionData, String value) {
        this.validateSupportedOp((Enum)((TextExpressionData.Operator)expressionData.getOperator()), Sets.newHashSet((Object[])new TextExpressionData.Operator[]{TextExpressionData.Operator.CONTAINS, TextExpressionData.Operator.NOT_CONTAINS}));
        SiteTextSearchQuery query = new SiteTextSearchQuery(value);
        return V2FieldHandlerHelper.wrapV2Search((com.atlassian.confluence.search.v2.SearchQuery)query, (ExpressionData)expressionData);
    }
}

