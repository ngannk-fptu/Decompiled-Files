/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.cql.spi.v2searchhelpers.V2FieldHandlerHelper
 *  com.atlassian.confluence.plugins.cql.spi.v2searchhelpers.V2SearchQueryWrapper
 *  com.atlassian.confluence.search.service.ContentTypeEnum
 *  com.atlassian.confluence.search.service.PredefinedSearchBuilder
 *  com.atlassian.confluence.search.service.SearchQueryParameters
 *  com.atlassian.confluence.search.v2.ISearch
 *  com.atlassian.confluence.search.v2.InvalidSearchException
 *  com.atlassian.confluence.search.v2.SearchFieldNames
 *  com.atlassian.confluence.search.v2.SearchManager
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.SearchResult
 *  com.atlassian.confluence.search.v2.SearchResults
 *  com.atlassian.confluence.search.v2.SearchSort
 *  com.atlassian.confluence.search.v2.query.InSpaceQuery
 *  com.atlassian.confluence.search.v2.query.MatchNoDocsQuery
 *  com.atlassian.confluence.search.v2.sort.TitleSort
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.querylang.exceptions.GenericQueryException
 *  com.atlassian.querylang.fields.BaseFieldHandler
 *  com.atlassian.querylang.fields.TextFieldHandler
 *  com.atlassian.querylang.fields.expressiondata.ExpressionData
 *  com.atlassian.querylang.fields.expressiondata.TextExpressionData
 *  com.atlassian.querylang.fields.expressiondata.TextExpressionData$Operator
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Sets
 */
package com.atlassian.confluence.plugins.cql.fields;

import com.atlassian.confluence.plugins.cql.fields.CQLFieldDefaults;
import com.atlassian.confluence.plugins.cql.spi.v2searchhelpers.V2FieldHandlerHelper;
import com.atlassian.confluence.plugins.cql.spi.v2searchhelpers.V2SearchQueryWrapper;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.search.service.PredefinedSearchBuilder;
import com.atlassian.confluence.search.service.SearchQueryParameters;
import com.atlassian.confluence.search.v2.ISearch;
import com.atlassian.confluence.search.v2.InvalidSearchException;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchManager;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.SearchResult;
import com.atlassian.confluence.search.v2.SearchResults;
import com.atlassian.confluence.search.v2.SearchSort;
import com.atlassian.confluence.search.v2.query.InSpaceQuery;
import com.atlassian.confluence.search.v2.query.MatchNoDocsQuery;
import com.atlassian.confluence.search.v2.sort.TitleSort;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.querylang.exceptions.GenericQueryException;
import com.atlassian.querylang.fields.BaseFieldHandler;
import com.atlassian.querylang.fields.TextFieldHandler;
import com.atlassian.querylang.fields.expressiondata.ExpressionData;
import com.atlassian.querylang.fields.expressiondata.TextExpressionData;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class SpaceTitleFieldHandler
extends BaseFieldHandler
implements TextFieldHandler<V2SearchQueryWrapper> {
    private final PredefinedSearchBuilder predefinedSearchBuilder;
    private final SearchManager searchManager;
    private static final ImmutableSet<String> SPACE_KEY_FIELD = ImmutableSet.of((Object)SearchFieldNames.SPACE_KEY);

    protected SpaceTitleFieldHandler(@ComponentImport PredefinedSearchBuilder predefinedSearchBuilder, @ComponentImport SearchManager searchManager) {
        super("space.title");
        this.predefinedSearchBuilder = predefinedSearchBuilder;
        this.searchManager = searchManager;
    }

    private Set<String> findSpaceKeysForSpaceName(String value) {
        SearchQueryParameters params = new SearchQueryParameters(value);
        params.setContentTypes((Set)Sets.newHashSet((Object[])new ContentTypeEnum[]{ContentTypeEnum.SPACE_DESCRIPTION, ContentTypeEnum.PERSONAL_SPACE_DESCRIPTION}));
        params.setSort((SearchSort)TitleSort.ASCENDING);
        ISearch search = this.predefinedSearchBuilder.buildSiteSearch(params, 0, CQLFieldDefaults.MAX_INTERIM_FIELD_RESULTS);
        try {
            SearchResults searchResults = this.searchManager.search(search, SPACE_KEY_FIELD);
            return Collections.unmodifiableSet(StreamSupport.stream(searchResults.spliterator(), false).map(SearchResult::getSpaceKey).collect(Collectors.toSet()));
        }
        catch (InvalidSearchException ex) {
            throw new GenericQueryException("Could not perform space search for value : " + value, (Throwable)ex);
        }
    }

    public V2SearchQueryWrapper build(TextExpressionData expressionData, String value) {
        this.validateSupportedOp((Enum)((TextExpressionData.Operator)expressionData.getOperator()), Sets.newHashSet((Object[])new TextExpressionData.Operator[]{TextExpressionData.Operator.CONTAINS, TextExpressionData.Operator.NOT_CONTAINS}));
        Set<String> keys = this.findSpaceKeysForSpaceName(value);
        if (Iterables.isEmpty(keys)) {
            return new V2SearchQueryWrapper((SearchQuery)MatchNoDocsQuery.getInstance());
        }
        InSpaceQuery query = new InSpaceQuery((Set)Sets.newHashSet(keys));
        return V2FieldHandlerHelper.wrapV2Search((SearchQuery)query, (ExpressionData)expressionData);
    }
}

