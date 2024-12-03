/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2.query;

import com.atlassian.confluence.search.v2.SearchPrimitive;
import com.atlassian.confluence.search.v2.SearchQuery;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

@SearchPrimitive
public class TermQuery
implements SearchQuery {
    public static final String KEY = "term";
    private final String fieldName;
    private final String value;

    public TermQuery(String fieldName, String value) {
        this.fieldName = fieldName;
        this.value = value;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public String getValue() {
        return this.value;
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public List<?> getParameters() {
        return Arrays.asList(this.fieldName, this.value);
    }

    public static BiFunction<String, String, SearchQuery> builder() {
        return TermQuery::new;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TermQuery)) {
            return false;
        }
        TermQuery termQuery = (TermQuery)o;
        return Objects.equals(this.getFieldName(), termQuery.getFieldName()) && Objects.equals(this.getValue(), termQuery.getValue());
    }

    public int hashCode() {
        return Objects.hash(this.getFieldName(), this.getValue());
    }

    public String toString() {
        return "TermQuery{fieldName='" + this.fieldName + "', value='" + this.value + "'}";
    }
}

