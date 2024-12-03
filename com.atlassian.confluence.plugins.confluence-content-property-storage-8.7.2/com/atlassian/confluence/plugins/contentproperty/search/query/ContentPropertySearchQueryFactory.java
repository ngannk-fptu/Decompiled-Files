/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.cql.spi.v2searchhelpers.V2FieldHandlerHelper
 *  com.atlassian.confluence.plugins.cql.spi.v2searchhelpers.V2SearchQueryWrapper
 *  com.atlassian.confluence.search.v2.BooleanOperator
 *  com.atlassian.confluence.search.v2.Range
 *  com.atlassian.confluence.search.v2.Range$Builder
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.SearchSort
 *  com.atlassian.confluence.search.v2.SearchSort$Order
 *  com.atlassian.confluence.search.v2.SearchSort$Type
 *  com.atlassian.confluence.search.v2.query.BooleanQuery
 *  com.atlassian.confluence.search.v2.query.DateRangeQuery
 *  com.atlassian.confluence.search.v2.query.DoubleRangeQuery
 *  com.atlassian.confluence.search.v2.query.QueryStringQuery
 *  com.atlassian.confluence.search.v2.query.TermQuery
 *  com.atlassian.confluence.search.v2.sort.FieldSort
 *  com.atlassian.querylang.antlrgen.AqlParser$EqOpContext
 *  com.atlassian.querylang.antlrgen.AqlParser$MapExprValueContext
 *  com.atlassian.querylang.antlrgen.AqlParser$RangeOpContext
 *  com.atlassian.querylang.fields.DateTimePrecision
 *  com.atlassian.querylang.fields.expressiondata.ExpressionData
 *  com.atlassian.querylang.lib.fields.expressiondata.ExpressionDataFactory
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.contentproperty.search.query;

import com.atlassian.confluence.plugins.contentproperty.index.schema.ContentPropertySchemaField;
import com.atlassian.confluence.plugins.cql.spi.v2searchhelpers.V2FieldHandlerHelper;
import com.atlassian.confluence.plugins.cql.spi.v2searchhelpers.V2SearchQueryWrapper;
import com.atlassian.confluence.search.v2.BooleanOperator;
import com.atlassian.confluence.search.v2.Range;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.SearchSort;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.DateRangeQuery;
import com.atlassian.confluence.search.v2.query.DoubleRangeQuery;
import com.atlassian.confluence.search.v2.query.QueryStringQuery;
import com.atlassian.confluence.search.v2.query.TermQuery;
import com.atlassian.confluence.search.v2.sort.FieldSort;
import com.atlassian.querylang.antlrgen.AqlParser;
import com.atlassian.querylang.fields.DateTimePrecision;
import com.atlassian.querylang.fields.expressiondata.ExpressionData;
import com.atlassian.querylang.lib.fields.expressiondata.ExpressionDataFactory;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class ContentPropertySearchQueryFactory {
    private final ExpressionDataFactory factory = new ExpressionDataFactory();

    public V2SearchQueryWrapper create(ContentPropertySchemaField schemaField, Object value, AqlParser.MapExprValueContext valueContext) {
        switch (schemaField.getFieldType()) {
            case STRING: {
                return this.createStringSearchQuery(schemaField, (List)value, valueContext);
            }
            case DATE: {
                return this.createDateSearchQuery(schemaField, (DateTimePrecision)value, valueContext);
            }
            case NUMBER: {
                return this.createNumberRangeSearchQuery(schemaField, Double.valueOf(String.valueOf(value)), valueContext);
            }
            case TEXT: {
                return this.createTextSearchQuery(schemaField, (String)value, valueContext);
            }
        }
        throw new IllegalArgumentException(String.format("Could not create a search query for given schema field type '%s'", new Object[]{schemaField.getFieldType()}));
    }

    public SearchQuery buildStringEqualityQuery(ContentPropertySchemaField schemaField, String ... value) {
        return this.buildMultiTermQuery(schemaField, Arrays.asList(value));
    }

    private V2SearchQueryWrapper createTextSearchQuery(ContentPropertySchemaField schemaField, String value, AqlParser.MapExprValueContext valueContext) {
        return V2FieldHandlerHelper.wrapV2Search((SearchQuery)this.buildTextQuery(schemaField, value), (ExpressionData)this.factory.create(schemaField.getFieldName(), valueContext.textOp()));
    }

    private V2SearchQueryWrapper createStringSearchQuery(ContentPropertySchemaField schemaField, List<String> value, AqlParser.MapExprValueContext valueContext) {
        if (valueContext.value() != null) {
            return V2FieldHandlerHelper.wrapV2Search((SearchQuery)this.buildMultiTermQuery(schemaField, value), (ExpressionData)this.factory.create(schemaField.getFieldName(), valueContext.eqOp()));
        }
        return V2FieldHandlerHelper.wrapV2Search((SearchQuery)this.buildMultiTermQuery(schemaField, value), (ExpressionData)this.factory.create(schemaField.getFieldName(), valueContext.setOp()));
    }

    private V2SearchQueryWrapper createDateSearchQuery(ContentPropertySchemaField schemaField, DateTimePrecision value, AqlParser.MapExprValueContext valueContext) {
        if (valueContext.rangeOp() != null) {
            return new V2SearchQueryWrapper(this.buildDateRangeQuery(schemaField, valueContext.rangeOp(), value));
        }
        return new V2SearchQueryWrapper(this.buildDateRangeQuery(schemaField, valueContext.eqOp(), value));
    }

    private V2SearchQueryWrapper createNumberRangeSearchQuery(ContentPropertySchemaField schemaField, Double value, AqlParser.MapExprValueContext valueContext) {
        if (valueContext.rangeOp() != null) {
            return new V2SearchQueryWrapper(this.buildNumberRangeQuery(schemaField, valueContext.rangeOp(), value));
        }
        return new V2SearchQueryWrapper(this.buildNumberRangeQuery(schemaField, valueContext.eqOp(), value));
    }

    private SearchQuery buildNumberRangeQuery(ContentPropertySchemaField schemaField, AqlParser.RangeOpContext opCtx, Double value) {
        if (opCtx.OP_GT() != null) {
            return this.buildNumberRangeQuery(schemaField, (Range<Double>)Range.Builder.range(Double.class).greaterThan((Object)value));
        }
        if (opCtx.OP_GTEQ() != null) {
            return this.buildNumberRangeQuery(schemaField, (Range<Double>)Range.Builder.range(Double.class).greaterThanEquals((Object)value));
        }
        if (opCtx.OP_LT() != null) {
            return this.buildNumberRangeQuery(schemaField, (Range<Double>)Range.Builder.range(Double.class).lessThan((Object)value));
        }
        if (opCtx.OP_LTEQ() != null) {
            return this.buildNumberRangeQuery(schemaField, (Range<Double>)Range.Builder.range(Double.class).lessThanEquals((Object)value));
        }
        if (opCtx.OP_EQUALS() != null) {
            return this.buildNumberRangeQuery(schemaField, (Range<Double>)Range.Builder.range(Double.class).equalsOp((Object)value));
        }
        if (opCtx.OP_NOT_EQUALS() != null) {
            return V2FieldHandlerHelper.negate((SearchQuery)this.buildNumberRangeQuery(schemaField, (Range<Double>)Range.Builder.range(Double.class).equalsOp((Object)value)));
        }
        throw new IllegalArgumentException("Could not construct a number range query with given operator.");
    }

    private SearchQuery buildNumberRangeQuery(ContentPropertySchemaField schemaField, AqlParser.EqOpContext opCtx, Double value) {
        if (opCtx.OP_EQUALS() != null) {
            return this.buildNumberRangeQuery(schemaField, (Range<Double>)Range.Builder.range(Double.class).equalsOp((Object)value));
        }
        if (opCtx.OP_NOT_EQUALS() != null) {
            return V2FieldHandlerHelper.negate((SearchQuery)this.buildNumberRangeQuery(schemaField, (Range<Double>)Range.Builder.range(Double.class).equalsOp((Object)value)));
        }
        throw new IllegalArgumentException("Could not construct a number range query with given operator.");
    }

    private SearchQuery buildDateRangeQuery(ContentPropertySchemaField schemaField, AqlParser.EqOpContext opCtx, DateTimePrecision value) {
        if (opCtx.OP_EQUALS() != null) {
            return this.buildDateRangeQuery(schemaField, (Range<Date>)Range.Builder.range(Date.class).equalsOp((Object)value.calcStartDateTimeInclusive().toDate(), (Object)value.calcEndDateTimeExclusive().toDate()));
        }
        if (opCtx.OP_NOT_EQUALS() != null) {
            return V2FieldHandlerHelper.negate((SearchQuery)this.buildDateRangeQuery(schemaField, (Range<Date>)Range.Builder.range(Date.class).equalsOp((Object)value.calcStartDateTimeInclusive().toDate(), (Object)value.calcEndDateTimeExclusive().toDate())));
        }
        throw new IllegalArgumentException("Could not construct a date range query with given operator.");
    }

    private SearchQuery buildDateRangeQuery(ContentPropertySchemaField schemaField, AqlParser.RangeOpContext opCtx, DateTimePrecision value) {
        if (opCtx.OP_GT() != null) {
            return this.buildDateRangeQuery(schemaField, (Range<Date>)Range.Builder.range(Date.class).greaterThanEquals((Object)value.calcEndDateTimeExclusive().toDate()));
        }
        if (opCtx.OP_GTEQ() != null) {
            return this.buildDateRangeQuery(schemaField, (Range<Date>)Range.Builder.range(Date.class).greaterThanEquals((Object)value.calcStartDateTimeInclusive().toDate()));
        }
        if (opCtx.OP_LT() != null) {
            return this.buildDateRangeQuery(schemaField, (Range<Date>)Range.Builder.range(Date.class).lessThan((Object)value.calcStartDateTimeInclusive().toDate()));
        }
        if (opCtx.OP_LTEQ() != null) {
            return this.buildDateRangeQuery(schemaField, (Range<Date>)Range.Builder.range(Date.class).lessThan((Object)value.calcEndDateTimeExclusive().toDate()));
        }
        if (opCtx.OP_EQUALS() != null) {
            return this.buildDateRangeQuery(schemaField, (Range<Date>)Range.Builder.range(Date.class).equalsOp((Object)value.calcStartDateTimeInclusive().toDate(), (Object)value.calcEndDateTimeExclusive().toDate()));
        }
        if (opCtx.OP_NOT_EQUALS() != null) {
            return V2FieldHandlerHelper.negate((SearchQuery)this.buildDateRangeQuery(schemaField, (Range<Date>)Range.Builder.range(Date.class).equalsOp((Object)value.calcStartDateTimeInclusive().toDate(), (Object)value.calcEndDateTimeExclusive().toDate())));
        }
        throw new IllegalArgumentException("Could not construct a date range query with given operator.");
    }

    public SearchSort buildContentPropertySearchSort(ContentPropertySchemaField schemaField, SearchSort.Type type, SearchSort.Order order) {
        return new FieldSort(schemaField.getFieldName(), type, order);
    }

    public SearchQuery buildTextQuery(ContentPropertySchemaField schemaField, String rawQuery) {
        return new QueryStringQuery(Collections.singleton(schemaField.getFieldName()), rawQuery, BooleanOperator.OR);
    }

    public SearchQuery buildNumberRangeQuery(ContentPropertySchemaField schemaField, Range<Double> range) {
        return new DoubleRangeQuery(schemaField.getFieldName(), range);
    }

    private SearchQuery buildDateRangeQuery(ContentPropertySchemaField schemaField, Range<Date> range) {
        return new DateRangeQuery((Date)range.getFrom(), (Date)range.getTo(), range.isIncludeFrom(), range.isIncludeTo(), schemaField.getFieldName());
    }

    private SearchQuery buildMultiTermQuery(ContentPropertySchemaField schemaField, Collection<String> values) {
        Set queries = values.stream().map(x -> new TermQuery(schemaField.getFieldName(), x)).collect(Collectors.toSet());
        return (SearchQuery)BooleanQuery.builder().addShould(queries).build();
    }
}

