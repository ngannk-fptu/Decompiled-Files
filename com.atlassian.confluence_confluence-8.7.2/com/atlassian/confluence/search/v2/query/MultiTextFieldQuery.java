/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.search.v2.query;

import com.atlassian.confluence.plugins.index.api.AnalyzerDescriptorProvider;
import com.atlassian.confluence.search.v2.BooleanOperator;
import com.atlassian.confluence.search.v2.QueryUtil;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.query.QueryStringQuery;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

public class MultiTextFieldQuery
implements SearchQuery {
    private static final String KEY = "multiTextField";
    private final String query;
    private final Set<String> fields;
    private final Map<String, ? extends AnalyzerDescriptorProvider> analyzerProviders;
    private final BooleanOperator operator;

    public MultiTextFieldQuery(String query, Set<String> fields, BooleanOperator operator) {
        this(fields, Collections.emptyMap(), operator, query);
    }

    public MultiTextFieldQuery(String query, String ... fields) {
        this(Arrays.asList(fields), Collections.emptyMap(), BooleanOperator.AND, query);
    }

    public MultiTextFieldQuery(Collection<String> fields, Map<String, ? extends AnalyzerDescriptorProvider> analyzerProviders, BooleanOperator operator, String query) {
        if (StringUtils.isBlank((CharSequence)query)) {
            throw new IllegalArgumentException("query parameter is required.");
        }
        if (fields == null || fields.isEmpty()) {
            throw new IllegalArgumentException("fields parameter is required");
        }
        if (operator == null) {
            throw new IllegalArgumentException("operator parameter is required");
        }
        this.fields = ImmutableSet.copyOf(fields);
        this.analyzerProviders = analyzerProviders == null ? Collections.emptyMap() : ImmutableMap.copyOf(analyzerProviders);
        this.operator = operator;
        this.query = query;
    }

    @Override
    public String getKey() {
        return KEY;
    }

    public String getQuery() {
        return QueryUtil.escape(this.query);
    }

    public String getUnescapedQuery() {
        return this.query;
    }

    public Set<String> getFields() {
        return new HashSet<String>(this.fields);
    }

    public Map<String, ? extends AnalyzerDescriptorProvider> getAnalyzerProviders() {
        return this.analyzerProviders;
    }

    public BooleanOperator getOperator() {
        return this.operator;
    }

    @Override
    public List getParameters() {
        return Arrays.asList(this.query, this.fields);
    }

    @Override
    public SearchQuery expand() {
        return new QueryStringQuery(this.fields, this.query, this.operator);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MultiTextFieldQuery)) {
            return false;
        }
        MultiTextFieldQuery that = (MultiTextFieldQuery)o;
        return Objects.equals(this.getQuery(), that.getQuery()) && Objects.equals(this.getFields(), that.getFields()) && Objects.equals(this.getAnalyzerProviders(), that.getAnalyzerProviders()) && this.getOperator() == that.getOperator();
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.getQuery(), this.getFields(), this.getAnalyzerProviders(), this.getOperator()});
    }
}

