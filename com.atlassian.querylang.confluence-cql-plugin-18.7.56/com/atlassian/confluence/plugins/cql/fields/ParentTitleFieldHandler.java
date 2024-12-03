/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.cql.spi.v2searchhelpers.V2FieldHandlerHelper
 *  com.atlassian.confluence.plugins.cql.spi.v2searchhelpers.V2SearchQueryWrapper
 *  com.atlassian.confluence.plugins.cql.spi.v2searchhelpers.V2SearchSortWrapper
 *  com.atlassian.confluence.search.v2.BooleanOperator
 *  com.atlassian.confluence.search.v2.SearchFieldNames
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.SearchSort
 *  com.atlassian.confluence.search.v2.query.TextFieldQuery
 *  com.atlassian.confluence.search.v2.sort.TitleSort
 *  com.atlassian.querylang.fields.BaseFieldHandler
 *  com.atlassian.querylang.fields.EqualityFieldHandler
 *  com.atlassian.querylang.fields.TextFieldHandler
 *  com.atlassian.querylang.fields.expressiondata.EqualityExpressionData
 *  com.atlassian.querylang.fields.expressiondata.EqualityExpressionData$Operator
 *  com.atlassian.querylang.fields.expressiondata.ExpressionData
 *  com.atlassian.querylang.fields.expressiondata.SetExpressionData
 *  com.atlassian.querylang.fields.expressiondata.SetExpressionData$Operator
 *  com.atlassian.querylang.fields.expressiondata.TextExpressionData
 *  com.atlassian.querylang.fields.expressiondata.TextExpressionData$Operator
 *  com.atlassian.querylang.query.FieldOrder
 *  com.atlassian.querylang.query.OrderDirection
 *  com.google.common.collect.Sets
 */
package com.atlassian.confluence.plugins.cql.fields;

import com.atlassian.confluence.plugins.cql.spi.v2searchhelpers.V2FieldHandlerHelper;
import com.atlassian.confluence.plugins.cql.spi.v2searchhelpers.V2SearchQueryWrapper;
import com.atlassian.confluence.plugins.cql.spi.v2searchhelpers.V2SearchSortWrapper;
import com.atlassian.confluence.search.v2.BooleanOperator;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.SearchSort;
import com.atlassian.confluence.search.v2.query.TextFieldQuery;
import com.atlassian.confluence.search.v2.sort.TitleSort;
import com.atlassian.querylang.fields.BaseFieldHandler;
import com.atlassian.querylang.fields.EqualityFieldHandler;
import com.atlassian.querylang.fields.TextFieldHandler;
import com.atlassian.querylang.fields.expressiondata.EqualityExpressionData;
import com.atlassian.querylang.fields.expressiondata.ExpressionData;
import com.atlassian.querylang.fields.expressiondata.SetExpressionData;
import com.atlassian.querylang.fields.expressiondata.TextExpressionData;
import com.atlassian.querylang.query.FieldOrder;
import com.atlassian.querylang.query.OrderDirection;
import com.google.common.collect.Sets;

public class ParentTitleFieldHandler
extends BaseFieldHandler
implements TextFieldHandler<V2SearchQueryWrapper>,
EqualityFieldHandler<String, V2SearchQueryWrapper> {
    private static final String FIELD_NAME = "parent.title";

    public ParentTitleFieldHandler() {
        super(FIELD_NAME, true);
    }

    public V2SearchQueryWrapper build(TextExpressionData expressionData, String value) {
        this.validateSupportedOp((Enum)((TextExpressionData.Operator)expressionData.getOperator()), Sets.newHashSet((Object[])new TextExpressionData.Operator[]{TextExpressionData.Operator.CONTAINS, TextExpressionData.Operator.NOT_CONTAINS}));
        return V2FieldHandlerHelper.wrapV2Search((SearchQuery)new TextFieldQuery(SearchFieldNames.PARENT_TITLE_UNSTEMMED, value, BooleanOperator.AND), (ExpressionData)expressionData);
    }

    public FieldOrder buildOrder(OrderDirection direction) {
        return new V2SearchSortWrapper((SearchSort)new TitleSort(V2SearchSortWrapper.convertOrder((OrderDirection)direction)));
    }

    public V2SearchQueryWrapper build(SetExpressionData expressionData, Iterable<String> values) {
        this.validateSupportedOp((Enum)((SetExpressionData.Operator)expressionData.getOperator()), Sets.newHashSet((Object[])new SetExpressionData.Operator[]{SetExpressionData.Operator.IN, SetExpressionData.Operator.NOT_IN}));
        SearchQuery query = V2FieldHandlerHelper.joinSingleValueQueries(values, this::createEqualityQuery);
        return V2FieldHandlerHelper.wrapV2Search((SearchQuery)query, (ExpressionData)expressionData);
    }

    public V2SearchQueryWrapper build(EqualityExpressionData expressionData, String value) {
        this.validateSupportedOp((Enum)((EqualityExpressionData.Operator)expressionData.getOperator()), Sets.newHashSet((Object[])new EqualityExpressionData.Operator[]{EqualityExpressionData.Operator.EQUALS, EqualityExpressionData.Operator.NOT_EQUALS}));
        return V2FieldHandlerHelper.wrapV2Search((SearchQuery)this.createEqualityQuery(value), (ExpressionData)expressionData);
    }

    private TextFieldQuery createEqualityQuery(String value) {
        return new TextFieldQuery(SearchFieldNames.PARENT_TITLE_UNSTEMMED, "\"" + value + "\"", BooleanOperator.AND);
    }
}

