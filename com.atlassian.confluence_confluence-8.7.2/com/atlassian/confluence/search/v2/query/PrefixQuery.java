/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2.query;

import com.atlassian.confluence.search.v2.SearchPrimitive;
import com.atlassian.confluence.search.v2.SearchQuery;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@SearchPrimitive
public class PrefixQuery
implements SearchQuery {
    public static final String KEY = "prefix";
    private final String fieldName;
    private final String prefix;

    public PrefixQuery(String fieldName, String prefix) {
        this.fieldName = fieldName;
        this.prefix = prefix;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public String getPrefix() {
        return this.prefix;
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public List<?> getParameters() {
        return Arrays.asList(this.fieldName, this.prefix);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PrefixQuery)) {
            return false;
        }
        PrefixQuery that = (PrefixQuery)o;
        return Objects.equals(this.getFieldName(), that.getFieldName()) && Objects.equals(this.getPrefix(), that.getPrefix());
    }

    public int hashCode() {
        return Objects.hash(this.getFieldName(), this.getPrefix());
    }

    public String toString() {
        return "PrefixQuery{fieldName='" + this.fieldName + "', prefix='" + this.prefix + "'}";
    }
}

