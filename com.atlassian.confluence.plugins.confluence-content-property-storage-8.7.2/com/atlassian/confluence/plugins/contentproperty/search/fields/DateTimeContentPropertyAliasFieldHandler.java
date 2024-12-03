/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.cql.spi.fields.AbstractDateRangeFieldHandler
 *  com.atlassian.confluence.search.v2.SearchSort
 *  com.atlassian.confluence.search.v2.SearchSort$Order
 *  com.atlassian.confluence.search.v2.SearchSort$Type
 *  com.atlassian.confluence.search.v2.query.DateRangeQuery
 *  com.atlassian.confluence.search.v2.query.DateRangeQuery$Builder
 *  com.atlassian.querylang.fields.FieldMetaData
 */
package com.atlassian.confluence.plugins.contentproperty.search.fields;

import com.atlassian.confluence.plugins.contentproperty.index.schema.ContentPropertySchemaField;
import com.atlassian.confluence.plugins.contentproperty.search.query.ContentPropertySearchQueryFactory;
import com.atlassian.confluence.plugins.cql.spi.fields.AbstractDateRangeFieldHandler;
import com.atlassian.confluence.search.v2.SearchSort;
import com.atlassian.confluence.search.v2.query.DateRangeQuery;
import com.atlassian.querylang.fields.FieldMetaData;

public class DateTimeContentPropertyAliasFieldHandler
extends AbstractDateRangeFieldHandler {
    private final ContentPropertySchemaField schemaField;
    private final ContentPropertySearchQueryFactory contentPropertySearchQueryFactory;
    public static final String KEY = "content-property-field-sort-mapper";

    protected DateTimeContentPropertyAliasFieldHandler(String aliasName, ContentPropertySchemaField schemaField) {
        super(aliasName, FieldMetaData.builder().isAlias(true).uiSupport(schemaField.getUiSupport()).build());
        this.schemaField = schemaField;
        this.contentPropertySearchQueryFactory = new ContentPropertySearchQueryFactory();
    }

    protected DateRangeQuery.Builder newDateRangeBuilder() {
        return DateRangeQuery.newDateRangeQuery((String)this.schemaField.getFieldName());
    }

    protected SearchSort getSearchSort(SearchSort.Order order) {
        return this.contentPropertySearchQueryFactory.buildContentPropertySearchSort(this.schemaField, SearchSort.Type.LONG, order);
    }
}

