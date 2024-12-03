/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.cql.spi.fields.AbstractDoubleFieldHandler
 *  com.atlassian.confluence.search.v2.Range
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.SearchSort
 *  com.atlassian.confluence.search.v2.SearchSort$Order
 *  com.atlassian.confluence.search.v2.SearchSort$Type
 *  com.atlassian.querylang.fields.FieldMetaData
 *  com.atlassian.querylang.fields.NumericFieldHandler
 *  com.atlassian.querylang.query.SearchQuery
 */
package com.atlassian.confluence.plugins.contentproperty.search.fields;

import com.atlassian.confluence.plugins.contentproperty.index.schema.ContentPropertySchemaField;
import com.atlassian.confluence.plugins.contentproperty.search.query.ContentPropertySearchQueryFactory;
import com.atlassian.confluence.plugins.cql.spi.fields.AbstractDoubleFieldHandler;
import com.atlassian.confluence.search.v2.Range;
import com.atlassian.confluence.search.v2.SearchSort;
import com.atlassian.querylang.fields.FieldMetaData;
import com.atlassian.querylang.fields.NumericFieldHandler;
import com.atlassian.querylang.query.SearchQuery;

public class NumericContentPropertyAliasFieldHandler
extends AbstractDoubleFieldHandler
implements NumericFieldHandler<SearchQuery> {
    private final ContentPropertySchemaField schemaField;
    private final ContentPropertySearchQueryFactory contentPropertySearchQueryFactory;

    public NumericContentPropertyAliasFieldHandler(String aliasName, ContentPropertySchemaField schemaField) {
        super(aliasName, FieldMetaData.builder().isAlias(true).uiSupport(schemaField.getUiSupport()).build());
        this.schemaField = schemaField;
        this.contentPropertySearchQueryFactory = new ContentPropertySearchQueryFactory();
    }

    protected SearchSort getSearchSort(SearchSort.Order order) {
        return this.contentPropertySearchQueryFactory.buildContentPropertySearchSort(this.schemaField, SearchSort.Type.DOUBLE, order);
    }

    protected com.atlassian.confluence.search.v2.SearchQuery createQuery(Double from, Double to, boolean includesFrom, boolean includesTo) {
        return this.contentPropertySearchQueryFactory.buildNumberRangeQuery(this.schemaField, (Range<Double>)new Range((Object)from, (Object)to, includesFrom, includesTo));
    }
}

