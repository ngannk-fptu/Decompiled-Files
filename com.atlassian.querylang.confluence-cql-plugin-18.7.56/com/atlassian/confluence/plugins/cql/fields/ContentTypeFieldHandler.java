/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.cql.spi.v2searchhelpers.V2FieldHandlerHelper
 *  com.atlassian.confluence.plugins.cql.spi.v2searchhelpers.V2SearchQueryWrapper
 *  com.atlassian.confluence.plugins.cql.spi.v2searchhelpers.V2SearchSortWrapper
 *  com.atlassian.confluence.search.service.ContentTypeEnum
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.SearchSort
 *  com.atlassian.confluence.search.v2.query.BooleanQuery
 *  com.atlassian.confluence.search.v2.query.ContentTypeQuery
 *  com.atlassian.confluence.search.v2.query.CustomContentTypeQuery
 *  com.atlassian.querylang.exceptions.MissingValueQueryException
 *  com.atlassian.querylang.fields.BaseFieldHandler
 *  com.atlassian.querylang.fields.EqualityFieldHandler
 *  com.atlassian.querylang.fields.expressiondata.EqualityExpressionData
 *  com.atlassian.querylang.fields.expressiondata.EqualityExpressionData$Operator
 *  com.atlassian.querylang.fields.expressiondata.ExpressionData
 *  com.atlassian.querylang.fields.expressiondata.SetExpressionData
 *  com.atlassian.querylang.fields.expressiondata.SetExpressionData$Operator
 *  com.atlassian.querylang.query.FieldOrder
 *  com.atlassian.querylang.query.OrderDirection
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Lists
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.cql.fields;

import com.atlassian.confluence.plugins.cql.impl.SearchTypeManager;
import com.atlassian.confluence.plugins.cql.spi.v2searchhelpers.V2FieldHandlerHelper;
import com.atlassian.confluence.plugins.cql.spi.v2searchhelpers.V2SearchQueryWrapper;
import com.atlassian.confluence.plugins.cql.spi.v2searchhelpers.V2SearchSortWrapper;
import com.atlassian.confluence.plugins.cql.v2search.sort.ContentTypeSort;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.SearchSort;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.ContentTypeQuery;
import com.atlassian.confluence.search.v2.query.CustomContentTypeQuery;
import com.atlassian.querylang.exceptions.MissingValueQueryException;
import com.atlassian.querylang.fields.BaseFieldHandler;
import com.atlassian.querylang.fields.EqualityFieldHandler;
import com.atlassian.querylang.fields.expressiondata.EqualityExpressionData;
import com.atlassian.querylang.fields.expressiondata.ExpressionData;
import com.atlassian.querylang.fields.expressiondata.SetExpressionData;
import com.atlassian.querylang.query.FieldOrder;
import com.atlassian.querylang.query.OrderDirection;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContentTypeFieldHandler
extends BaseFieldHandler
implements EqualityFieldHandler<String, V2SearchQueryWrapper> {
    private static final Logger log = LoggerFactory.getLogger(ContentTypeFieldHandler.class);
    private static final String FIELD_NAME = "type";
    private static final ImmutableSet<SetExpressionData.Operator> SET_OPERATORS = ImmutableSet.of((Object)SetExpressionData.Operator.IN, (Object)SetExpressionData.Operator.NOT_IN);
    private static final ImmutableSet<EqualityExpressionData.Operator> EQUALS_OPERATORS = ImmutableSet.of((Object)EqualityExpressionData.Operator.EQUALS, (Object)EqualityExpressionData.Operator.NOT_EQUALS);
    private final SearchTypeManager searchTypeManager;
    private Function<String, Stream<ContentTypeEnum>> builtInTypeFromValues = value -> {
        String lowerCase = value.toLowerCase();
        this.checkTypeExists(lowerCase);
        ContentTypeEnum typeEnum = ContentTypeEnum.getByRepresentation((String)lowerCase);
        if (typeEnum != null) {
            if (typeEnum.equals((Object)ContentTypeEnum.SPACE)) {
                return Stream.of(ContentTypeEnum.SPACE_DESCRIPTION, ContentTypeEnum.PERSONAL_SPACE_DESCRIPTION);
            }
            return Stream.of(typeEnum);
        }
        if (value.equals("user")) {
            return Stream.of(ContentTypeEnum.PERSONAL_INFORMATION);
        }
        return Stream.of(new ContentTypeEnum[0]);
    };
    private Function<String, Stream<String>> customTypesFromValues = value -> {
        String lowerCase = value.toLowerCase();
        this.checkTypeExists(lowerCase);
        Set typeEnum = this.builtInTypeFromValues.apply(lowerCase).collect(Collectors.toSet());
        if (typeEnum.isEmpty()) {
            return Stream.of(value);
        }
        return Stream.of(new String[0]);
    };

    public ContentTypeFieldHandler(SearchTypeManager searchTypeManager) {
        super(FIELD_NAME, true);
        this.searchTypeManager = searchTypeManager;
    }

    public V2SearchQueryWrapper build(EqualityExpressionData expressionData, String value) {
        this.validateSupportedOp((Enum)((EqualityExpressionData.Operator)expressionData.getOperator()), (Set)EQUALS_OPERATORS);
        SearchQuery query = this.getV2SearchQueryFromValues(Optional.ofNullable(value).map(Collections::singletonList).orElse(Collections.emptyList()));
        return V2FieldHandlerHelper.wrapV2Search((SearchQuery)query, (ExpressionData)expressionData);
    }

    public V2SearchQueryWrapper build(SetExpressionData expressionData, Iterable<String> values) {
        this.validateSupportedOp((Enum)((SetExpressionData.Operator)expressionData.getOperator()), (Set)SET_OPERATORS);
        SearchQuery query = this.getV2SearchQueryFromValues(values);
        return V2FieldHandlerHelper.wrapV2Search((SearchQuery)query, (ExpressionData)expressionData);
    }

    private SearchQuery getV2SearchQueryFromValues(Iterable<String> values) {
        List customTypes;
        List builtInTypes = StreamSupport.stream(values.spliterator(), false).flatMap(this.builtInTypeFromValues).collect(Collectors.toList());
        Object query = null;
        if (!builtInTypes.isEmpty()) {
            query = new ContentTypeQuery((Collection)Lists.newArrayList(builtInTypes));
        }
        if (!(customTypes = StreamSupport.stream(values.spliterator(), false).flatMap(this.customTypesFromValues).collect(Collectors.toList())).isEmpty()) {
            CustomContentTypeQuery customTypeQuery = new CustomContentTypeQuery(customTypes);
            query = query == null ? customTypeQuery : BooleanQuery.orQuery((SearchQuery[])new SearchQuery[]{query, customTypeQuery});
        }
        return query;
    }

    private void checkTypeExists(String lowerCaseType) {
        if (!this.searchTypeManager.hasType(lowerCaseType)) {
            log.debug("Unsupported type : " + lowerCaseType);
            throw new MissingValueQueryException(String.format("Unsupported value for type, got : %s, expected one of : %s", lowerCaseType, this.searchTypeManager.getTypes().keySet()));
        }
    }

    public FieldOrder buildOrder(OrderDirection direction) {
        return new V2SearchSortWrapper((SearchSort)ContentTypeSort.forDirection(direction));
    }
}

