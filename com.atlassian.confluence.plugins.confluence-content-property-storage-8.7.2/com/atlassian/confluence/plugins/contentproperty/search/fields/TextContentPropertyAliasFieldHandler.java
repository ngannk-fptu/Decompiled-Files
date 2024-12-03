/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.cql.spi.v2searchhelpers.V2FieldHandlerHelper
 *  com.atlassian.confluence.plugins.cql.spi.v2searchhelpers.V2SearchSortWrapper
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.SearchSort$Order
 *  com.atlassian.confluence.search.v2.SearchSort$Type
 *  com.atlassian.querylang.fields.BaseFieldHandler
 *  com.atlassian.querylang.fields.FieldMetaData
 *  com.atlassian.querylang.fields.TextFieldHandler
 *  com.atlassian.querylang.fields.expressiondata.ExpressionData
 *  com.atlassian.querylang.fields.expressiondata.TextExpressionData
 *  com.atlassian.querylang.fields.expressiondata.TextExpressionData$Operator
 *  com.atlassian.querylang.query.FieldOrder
 *  com.atlassian.querylang.query.OrderDirection
 *  com.atlassian.querylang.query.SearchQuery
 *  com.google.common.collect.Sets
 */
package com.atlassian.confluence.plugins.contentproperty.search.fields;

import com.atlassian.confluence.plugins.contentproperty.index.schema.ContentPropertySchemaField;
import com.atlassian.confluence.plugins.contentproperty.search.query.ContentPropertySearchQueryFactory;
import com.atlassian.confluence.plugins.cql.spi.v2searchhelpers.V2FieldHandlerHelper;
import com.atlassian.confluence.plugins.cql.spi.v2searchhelpers.V2SearchSortWrapper;
import com.atlassian.confluence.search.v2.SearchSort;
import com.atlassian.querylang.fields.BaseFieldHandler;
import com.atlassian.querylang.fields.FieldMetaData;
import com.atlassian.querylang.fields.TextFieldHandler;
import com.atlassian.querylang.fields.expressiondata.ExpressionData;
import com.atlassian.querylang.fields.expressiondata.TextExpressionData;
import com.atlassian.querylang.query.FieldOrder;
import com.atlassian.querylang.query.OrderDirection;
import com.atlassian.querylang.query.SearchQuery;
import com.google.common.collect.Sets;

public class TextContentPropertyAliasFieldHandler
extends BaseFieldHandler
implements TextFieldHandler<SearchQuery> {
    private final ContentPropertySearchQueryFactory contentPropertySearchQueryFactory;
    private final ContentPropertySchemaField schemaField;

    protected TextContentPropertyAliasFieldHandler(String aliasName, ContentPropertySchemaField schemaField, boolean orderSupported) {
        super(aliasName, FieldMetaData.builder().isAlias(true).uiSupport(schemaField.getUiSupport()).build(), orderSupported);
        this.schemaField = schemaField;
        this.contentPropertySearchQueryFactory = new ContentPropertySearchQueryFactory();
    }

    public SearchQuery build(TextExpressionData textExpressionData, String s) {
        this.validateSupportedOp((Enum)((TextExpressionData.Operator)textExpressionData.getOperator()), Sets.newHashSet((Object[])new TextExpressionData.Operator[]{TextExpressionData.Operator.CONTAINS, TextExpressionData.Operator.NOT_CONTAINS}));
        return V2FieldHandlerHelper.wrapV2Search((com.atlassian.confluence.search.v2.SearchQuery)this.contentPropertySearchQueryFactory.buildTextQuery(this.schemaField, s), (ExpressionData)textExpressionData);
    }

    public FieldOrder buildOrder(OrderDirection direction) {
        SearchSort.Order order = direction == OrderDirection.ASC ? SearchSort.Order.ASCENDING : SearchSort.Order.DESCENDING;
        return new V2SearchSortWrapper(this.contentPropertySearchQueryFactory.buildContentPropertySearchSort(this.schemaField, SearchSort.Type.STRING, order));
    }
}

