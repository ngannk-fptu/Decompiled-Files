/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.cql.spi.v2searchhelpers.V2FieldHandlerHelper
 *  com.atlassian.confluence.plugins.cql.spi.v2searchhelpers.V2SearchQueryWrapper
 *  com.atlassian.confluence.plugins.cql.spi.v2searchhelpers.V2SearchSortWrapper
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.SearchSort$Order
 *  com.atlassian.confluence.search.v2.SearchSort$Type
 *  com.atlassian.querylang.fields.BaseFieldHandler
 *  com.atlassian.querylang.fields.EqualityFieldHandler
 *  com.atlassian.querylang.fields.FieldMetaData
 *  com.atlassian.querylang.fields.expressiondata.EqualityExpressionData
 *  com.atlassian.querylang.fields.expressiondata.EqualityExpressionData$Operator
 *  com.atlassian.querylang.fields.expressiondata.ExpressionData
 *  com.atlassian.querylang.fields.expressiondata.SetExpressionData
 *  com.atlassian.querylang.fields.expressiondata.SetExpressionData$Operator
 *  com.atlassian.querylang.query.FieldOrder
 *  com.atlassian.querylang.query.OrderDirection
 *  com.google.common.collect.Sets
 *  com.google.common.collect.Streams
 */
package com.atlassian.confluence.plugins.contentproperty.search.fields;

import com.atlassian.confluence.plugins.contentproperty.index.schema.ContentPropertySchemaField;
import com.atlassian.confluence.plugins.contentproperty.search.query.ContentPropertySearchQueryFactory;
import com.atlassian.confluence.plugins.cql.spi.v2searchhelpers.V2FieldHandlerHelper;
import com.atlassian.confluence.plugins.cql.spi.v2searchhelpers.V2SearchQueryWrapper;
import com.atlassian.confluence.plugins.cql.spi.v2searchhelpers.V2SearchSortWrapper;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.SearchSort;
import com.atlassian.querylang.fields.BaseFieldHandler;
import com.atlassian.querylang.fields.EqualityFieldHandler;
import com.atlassian.querylang.fields.FieldMetaData;
import com.atlassian.querylang.fields.expressiondata.EqualityExpressionData;
import com.atlassian.querylang.fields.expressiondata.ExpressionData;
import com.atlassian.querylang.fields.expressiondata.SetExpressionData;
import com.atlassian.querylang.query.FieldOrder;
import com.atlassian.querylang.query.OrderDirection;
import com.google.common.collect.Sets;
import com.google.common.collect.Streams;

public class StringContentPropertyAliasFieldHandler
extends BaseFieldHandler
implements EqualityFieldHandler<String, V2SearchQueryWrapper> {
    private final ContentPropertySchemaField schemaField;
    private final ContentPropertySearchQueryFactory contentPropertySearchQueryFactory;
    public static final String KEY = "content-property-field-sort-mapper";

    public StringContentPropertyAliasFieldHandler(String aliasName, ContentPropertySchemaField schemaField, boolean orderSupported) {
        super(aliasName, FieldMetaData.builder().isAlias(true).uiSupport(schemaField.getUiSupport()).build(), orderSupported);
        this.schemaField = schemaField;
        this.contentPropertySearchQueryFactory = new ContentPropertySearchQueryFactory();
    }

    public V2SearchQueryWrapper build(SetExpressionData setExpressionData, Iterable<String> value) {
        this.validateSupportedOp((Enum)((SetExpressionData.Operator)setExpressionData.getOperator()), Sets.newHashSet((Object[])new SetExpressionData.Operator[]{SetExpressionData.Operator.IN, SetExpressionData.Operator.IN}));
        return V2FieldHandlerHelper.wrapV2Search((SearchQuery)this.contentPropertySearchQueryFactory.buildStringEqualityQuery(this.schemaField, (String[])Streams.stream(value).toArray(String[]::new)), (ExpressionData)setExpressionData);
    }

    public V2SearchQueryWrapper build(EqualityExpressionData equalityExpressionData, String value) {
        this.validateSupportedOp((Enum)((EqualityExpressionData.Operator)equalityExpressionData.getOperator()), Sets.newHashSet((Object[])new EqualityExpressionData.Operator[]{EqualityExpressionData.Operator.EQUALS, EqualityExpressionData.Operator.NOT_EQUALS}));
        return V2FieldHandlerHelper.wrapV2Search((SearchQuery)this.contentPropertySearchQueryFactory.buildStringEqualityQuery(this.schemaField, value), (ExpressionData)equalityExpressionData);
    }

    public FieldOrder buildOrder(OrderDirection direction) {
        SearchSort.Order order = direction == OrderDirection.ASC ? SearchSort.Order.ASCENDING : SearchSort.Order.DESCENDING;
        return new V2SearchSortWrapper(this.contentPropertySearchQueryFactory.buildContentPropertySearchSort(this.schemaField, SearchSort.Type.STRING, order));
    }
}

