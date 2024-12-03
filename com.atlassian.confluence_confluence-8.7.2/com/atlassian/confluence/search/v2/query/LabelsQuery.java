/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package com.atlassian.confluence.search.v2.query;

import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.PrefixQuery;
import com.atlassian.confluence.search.v2.query.TermQuery;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class LabelsQuery
implements SearchQuery {
    public static final String KEY = "labels";
    private final Set<String> labels;

    public LabelsQuery(Set<String> labels) {
        if (labels == null) {
            throw new IllegalArgumentException("labels is required.");
        }
        this.labels = labels;
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public List getParameters() {
        return Lists.newArrayList(this.labels);
    }

    public Set<String> getLabels() {
        return Collections.unmodifiableSet(this.labels);
    }

    @Override
    public SearchQuery expand() {
        Collection subQueries = this.labels.stream().filter(v -> v.startsWith("~")).map(v -> new PrefixQuery(SearchFieldNames.LABEL, v.concat(":"))).collect(Collectors.toList());
        if (subQueries.isEmpty()) {
            subQueries = this.labels.stream().map(v -> new TermQuery(SearchFieldNames.LABEL, (String)v)).collect(Collectors.toList());
        }
        return (SearchQuery)BooleanQuery.builder().addShould(subQueries).build();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        LabelsQuery that = (LabelsQuery)o;
        return this.labels.equals(that.labels);
    }

    public int hashCode() {
        return Objects.hash(this.labels);
    }
}

