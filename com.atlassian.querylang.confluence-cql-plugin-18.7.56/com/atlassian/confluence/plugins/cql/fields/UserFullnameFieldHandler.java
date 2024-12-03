/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.confluence.plugins.cql.spi.fields.AbstractUserFieldHandler
 *  com.atlassian.confluence.plugins.cql.spi.v2searchhelpers.V2FieldHandlerHelper
 *  com.atlassian.confluence.plugins.cql.spi.v2searchhelpers.V2SearchQueryWrapper
 *  com.atlassian.confluence.search.service.PredefinedSearchBuilder
 *  com.atlassian.confluence.search.v2.ISearch
 *  com.atlassian.confluence.search.v2.InvalidSearchException
 *  com.atlassian.confluence.search.v2.SearchFieldNames
 *  com.atlassian.confluence.search.v2.SearchManager
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.SearchResult
 *  com.atlassian.confluence.search.v2.SearchResults
 *  com.atlassian.confluence.search.v2.query.BooleanQuery
 *  com.atlassian.confluence.search.v2.query.MatchNoDocsQuery
 *  com.atlassian.confluence.search.v2.sort.UserAttributeSort$UserAttribute
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.querylang.fields.BaseFieldHandler
 *  com.atlassian.querylang.fields.TextFieldHandler
 *  com.atlassian.querylang.fields.expressiondata.ExpressionData
 *  com.atlassian.querylang.fields.expressiondata.TextExpressionData
 *  com.atlassian.querylang.fields.expressiondata.TextExpressionData$Operator
 *  com.atlassian.querylang.query.FieldOrder
 *  com.atlassian.querylang.query.OrderDirection
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Sets
 */
package com.atlassian.confluence.plugins.cql.fields;

import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.plugins.cql.spi.fields.AbstractUserFieldHandler;
import com.atlassian.confluence.plugins.cql.spi.v2searchhelpers.V2FieldHandlerHelper;
import com.atlassian.confluence.plugins.cql.spi.v2searchhelpers.V2SearchQueryWrapper;
import com.atlassian.confluence.search.service.PredefinedSearchBuilder;
import com.atlassian.confluence.search.v2.ISearch;
import com.atlassian.confluence.search.v2.InvalidSearchException;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchManager;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.SearchResult;
import com.atlassian.confluence.search.v2.SearchResults;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.MatchNoDocsQuery;
import com.atlassian.confluence.search.v2.sort.UserAttributeSort;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.querylang.fields.BaseFieldHandler;
import com.atlassian.querylang.fields.TextFieldHandler;
import com.atlassian.querylang.fields.expressiondata.ExpressionData;
import com.atlassian.querylang.fields.expressiondata.TextExpressionData;
import com.atlassian.querylang.query.FieldOrder;
import com.atlassian.querylang.query.OrderDirection;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.Set;

public class UserFullnameFieldHandler
extends BaseFieldHandler
implements TextFieldHandler<V2SearchQueryWrapper> {
    private static final int MAX_RESULTS = 1000;
    public static final String FIELD_NAME = "fullname";
    private final SearchManager searchManager;
    private final PredefinedSearchBuilder searchBuilder;
    private final AbstractUserFieldHandler delegateHandler;

    protected UserFullnameFieldHandler(AbstractUserFieldHandler delegate, @ComponentImport SearchManager searchManager, @ComponentImport PredefinedSearchBuilder searchBuilder) {
        super(FIELD_NAME, true);
        this.delegateHandler = delegate;
        this.searchManager = searchManager;
        this.searchBuilder = searchBuilder;
    }

    public V2SearchQueryWrapper build(TextExpressionData expressionData, String value) {
        this.validateSupportedOp((Enum)((TextExpressionData.Operator)expressionData.getOperator()), Sets.newHashSet((Object[])new TextExpressionData.Operator[]{TextExpressionData.Operator.CONTAINS, TextExpressionData.Operator.NOT_CONTAINS}));
        ISearch search = this.searchBuilder.buildUsersSearch(value, 1000);
        try {
            SearchResults results = this.searchManager.search(search, (Set)ImmutableSet.of((Object)SearchFieldNames.USER_KEY, (Object)"username"));
            if (results.size() == 0) {
                return V2FieldHandlerHelper.wrapV2Search((SearchQuery)MatchNoDocsQuery.getInstance(), (ExpressionData)expressionData);
            }
            HashSet queries = Sets.newHashSet();
            for (SearchResult result : results) {
                queries.add(this.delegateHandler.createUserQuery(new UserKey(result.getField(SearchFieldNames.USER_KEY)), result.getField("username")));
            }
            return V2FieldHandlerHelper.wrapV2Search((SearchQuery)BooleanQuery.composeOrQuery((Set)queries), (ExpressionData)expressionData);
        }
        catch (InvalidSearchException ex) {
            throw new BadRequestException(String.format("Could not perform user search for field %s, using value %s, due to : %s", this.fieldName(), value, ex.getMessage()), (Throwable)ex);
        }
    }

    public FieldOrder buildOrder(OrderDirection direction) {
        return this.delegateHandler.buildOrder(direction, UserAttributeSort.UserAttribute.FULLNAME);
    }
}

