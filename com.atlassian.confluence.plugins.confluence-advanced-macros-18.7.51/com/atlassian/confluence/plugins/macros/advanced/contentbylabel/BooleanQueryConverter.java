/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.labels.ParsedLabelName
 *  com.atlassian.confluence.search.service.SpaceCategoryEnum
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.query.AllQuery
 *  com.atlassian.confluence.search.v2.query.BooleanQuery
 *  com.atlassian.confluence.search.v2.query.ContentTypeQuery
 *  com.atlassian.confluence.search.v2.query.CustomContentTypeQuery
 *  com.atlassian.confluence.search.v2.query.InSpaceQuery
 *  com.atlassian.confluence.search.v2.query.LabelQuery
 *  com.atlassian.confluence.search.v2.query.SpaceCategoryQuery
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.google.common.collect.Maps
 */
package com.atlassian.confluence.plugins.macros.advanced.contentbylabel;

import com.atlassian.confluence.labels.ParsedLabelName;
import com.atlassian.confluence.plugins.macros.advanced.contentbylabel.CompositeQueryExpression;
import com.atlassian.confluence.plugins.macros.advanced.contentbylabel.QueryExpression;
import com.atlassian.confluence.plugins.macros.advanced.contentbylabel.SimpleQueryExpression;
import com.atlassian.confluence.search.service.SpaceCategoryEnum;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.query.AllQuery;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.ContentTypeQuery;
import com.atlassian.confluence.search.v2.query.CustomContentTypeQuery;
import com.atlassian.confluence.search.v2.query.InSpaceQuery;
import com.atlassian.confluence.search.v2.query.LabelQuery;
import com.atlassian.confluence.search.v2.query.SpaceCategoryQuery;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BooleanQueryConverter {
    private final I18NBean i18n;

    protected BooleanQueryConverter(I18NBean i18n) {
        this.i18n = i18n;
    }

    public QueryExpression convertToExpression(BooleanQuery query) {
        CompositeQueryExpression.Builder builder = CompositeQueryExpression.builder(CompositeQueryExpression.BooleanOperator.AND);
        builder.add(this.getExpression(CompositeQueryExpression.BooleanOperator.AND, SimpleQueryExpression.InclusionOperator.INCLUDES, query.getMustQueries()));
        builder.add(this.getExpression(CompositeQueryExpression.BooleanOperator.OR, SimpleQueryExpression.InclusionOperator.INCLUDES, query.getShouldQueries()));
        builder.add(this.getExpression(CompositeQueryExpression.BooleanOperator.AND, SimpleQueryExpression.InclusionOperator.EXCLUDES, query.getMustNotQueries()));
        return builder.build();
    }

    private QueryExpression getExpression(CompositeQueryExpression.BooleanOperator booleanOperator, SimpleQueryExpression.InclusionOperator inclusionOperator, Set<SearchQuery> queries) {
        if (queries.isEmpty()) {
            return null;
        }
        Map<String, List<String>> queryMap = this.buildQueryMap(queries);
        CompositeQueryExpression.Builder builder = CompositeQueryExpression.builder(booleanOperator);
        for (Map.Entry<String, List<String>> entry : queryMap.entrySet()) {
            QueryExpression expression;
            String key = entry.getKey();
            List<String> values = entry.getValue();
            if (booleanOperator == CompositeQueryExpression.BooleanOperator.AND && inclusionOperator == SimpleQueryExpression.InclusionOperator.INCLUDES) {
                CompositeQueryExpression.Builder subBuilder = CompositeQueryExpression.builder(CompositeQueryExpression.BooleanOperator.AND);
                for (String value : values) {
                    subBuilder.add(SimpleQueryExpression.of(key, SimpleQueryExpression.InclusionOperator.INCLUDES, value));
                }
                expression = subBuilder.build();
            } else {
                expression = SimpleQueryExpression.of(key, inclusionOperator, values);
            }
            builder.add(expression);
        }
        return builder.build();
    }

    private Map<String, List<String>> buildQueryMap(Set<SearchQuery> queries) {
        HashMap queryMap = Maps.newHashMap();
        for (SearchQuery searchQuery : queries) {
            Set contentTypes;
            ContentTypeQuery typeQuery;
            if (searchQuery instanceof LabelQuery) {
                String labelAsString = this.getLabelValue((LabelQuery)searchQuery);
                this.addQueryToMap(queryMap, "label", labelAsString);
                continue;
            }
            if (searchQuery instanceof ContentTypeQuery) {
                typeQuery = (ContentTypeQuery)searchQuery;
                contentTypes = typeQuery.getContentTypes();
                for (Object contentType : contentTypes) {
                    this.addQueryToMap(queryMap, "type", contentType.getRepresentation());
                }
                continue;
            }
            if (searchQuery instanceof CustomContentTypeQuery) {
                typeQuery = (CustomContentTypeQuery)searchQuery;
                contentTypes = typeQuery.getPluginKeys();
                for (Object contentType : contentTypes) {
                    this.addQueryToMap(queryMap, "type", (String)contentType);
                }
                continue;
            }
            if (searchQuery instanceof InSpaceQuery) {
                String spaceKey = (String)searchQuery.getParameters().get(0);
                this.addQueryToMap(queryMap, "space", spaceKey);
                continue;
            }
            if (searchQuery instanceof SpaceCategoryQuery) {
                SpaceCategoryQuery categoryQuery = (SpaceCategoryQuery)searchQuery;
                Set spaceCategories = categoryQuery.getSpaceCategories();
                for (SpaceCategoryEnum spaceCategory : spaceCategories) {
                    String spaceType = this.getSpaceType(spaceCategory);
                    if (spaceType == null) continue;
                    this.addQueryToMap(queryMap, "space.type", spaceType);
                }
                continue;
            }
            if (searchQuery instanceof AllQuery) continue;
            throw new IllegalArgumentException("Unknown query class: " + searchQuery.getClass());
        }
        return queryMap;
    }

    private String getLabelValue(LabelQuery labelQuery) {
        ParsedLabelName parsedLabel = (ParsedLabelName)labelQuery.getParameters().get(0);
        return parsedLabel.getPrefix().equals("global") ? parsedLabel.getName() : labelQuery.getLabelAsString();
    }

    private String getSpaceType(SpaceCategoryEnum category) {
        switch (category) {
            case ALL: {
                return null;
            }
            case GLOBAL: {
                return "global";
            }
            case PERSONAL: {
                return "personal";
            }
            case FAVOURITES: {
                return "favourite";
            }
        }
        throw new IllegalArgumentException(this.i18n.getText("contentbylabel.error.invalid-space", (Object[])new String[]{category.getRepresentation()}));
    }

    private void addQueryToMap(Map<String, List<String>> map, String key, String value) {
        List queries = map.computeIfAbsent(key, k -> new ArrayList());
        queries.add(value);
    }
}

