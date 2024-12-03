/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.confluence.search.v2.query;

import com.atlassian.confluence.plugins.index.api.AnalyzerDescriptorProvider;
import com.atlassian.confluence.search.v2.BooleanOperator;
import com.atlassian.confluence.search.v2.SearchPrimitive;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@SearchPrimitive
public class QueryStringQuery
implements SearchQuery {
    public static final String KEY = "queryString";
    private final Set<String> fieldNames;
    private final Map<String, ? extends AnalyzerDescriptorProvider> analyzerProviders;
    private final String query;
    private final BooleanOperator operator;
    private final Map<String, Float> fieldsBoost;

    public QueryStringQuery(Collection<String> fieldNames, String query, BooleanOperator operator) {
        this(fieldNames, Collections.emptyMap(), operator, query);
    }

    public QueryStringQuery(Collection<String> fieldNames, Map<String, ? extends AnalyzerDescriptorProvider> analyzerProviders, BooleanOperator operator, String query) {
        this(fieldNames, analyzerProviders, operator, query, Collections.emptyMap());
    }

    public QueryStringQuery(Collection<String> fieldNames, String query, BooleanOperator operator, Map<String, Float> fieldsBoost) {
        this(fieldNames, Collections.emptyMap(), operator, query, fieldsBoost);
    }

    public QueryStringQuery(Collection<String> fieldNames, Map<String, ? extends AnalyzerDescriptorProvider> analyzerProviders, BooleanOperator operator, String query, Map<String, Float> fieldsBoost) {
        this.fieldNames = ImmutableSet.copyOf(fieldNames);
        this.analyzerProviders = analyzerProviders == null ? Collections.emptyMap() : ImmutableMap.copyOf(analyzerProviders);
        this.operator = operator;
        this.query = query;
        this.fieldsBoost = fieldsBoost;
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public List<?> getParameters() {
        return Arrays.asList(new Object[]{this.fieldNames, this.query, this.operator});
    }

    public Set<String> getFieldNames() {
        return this.fieldNames;
    }

    public Map<String, ? extends AnalyzerDescriptorProvider> getAnalyzerProviders() {
        return this.analyzerProviders;
    }

    public String getQuery() {
        return this.query;
    }

    public BooleanOperator getOperator() {
        return this.operator;
    }

    public Map<String, Float> getFieldsBoost() {
        return this.fieldsBoost;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof QueryStringQuery)) {
            return false;
        }
        QueryStringQuery that = (QueryStringQuery)o;
        return Objects.equals(this.getFieldNames(), that.getFieldNames()) && Objects.equals(this.getAnalyzerProviders(), that.getAnalyzerProviders()) && Objects.equals(this.getQuery(), that.getQuery()) && this.getOperator() == that.getOperator() && Objects.equals(this.fieldsBoost, that.fieldsBoost);
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.getFieldNames(), this.getAnalyzerProviders(), this.getQuery(), this.getOperator(), this.getFieldsBoost()});
    }
}

