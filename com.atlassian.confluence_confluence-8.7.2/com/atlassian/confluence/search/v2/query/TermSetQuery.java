/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2.query;

import com.atlassian.confluence.search.v2.SearchPrimitive;
import com.atlassian.confluence.search.v2.SearchQuery;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;

@SearchPrimitive
public class TermSetQuery
implements SearchQuery {
    public static final String KEY = "termSet";
    private final String fieldName;
    private final Set<String> values;

    public TermSetQuery(String fieldName, Set<String> values) {
        this.fieldName = fieldName;
        this.values = values;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public Set<String> getValues() {
        return this.values;
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public List<?> getParameters() {
        return Arrays.asList(this.fieldName, this.values);
    }

    public static BiFunction<String, Set<String>, SearchQuery> builder() {
        return TermSetQuery::new;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        TermSetQuery that = (TermSetQuery)o;
        return this.fieldName.equals(that.fieldName) && this.values.equals(that.values);
    }

    public int hashCode() {
        return Objects.hash(this.fieldName, this.values);
    }
}

