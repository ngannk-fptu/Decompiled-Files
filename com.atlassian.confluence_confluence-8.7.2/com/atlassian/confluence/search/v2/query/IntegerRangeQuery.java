/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2.query;

import com.atlassian.confluence.search.v2.Range;
import com.atlassian.confluence.search.v2.SearchPrimitive;
import com.atlassian.confluence.search.v2.SearchQuery;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@SearchPrimitive
public class IntegerRangeQuery
implements SearchQuery {
    public static final String KEY = "intRange";
    private final String fieldName;
    private final Range<Integer> range;

    public IntegerRangeQuery(String fieldName, Range<Integer> range) {
        this.fieldName = fieldName;
        this.range = range;
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public List getParameters() {
        return Arrays.asList(this.fieldName, this.range);
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public Range<Integer> getRange() {
        return this.range;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof IntegerRangeQuery)) {
            return false;
        }
        IntegerRangeQuery that = (IntegerRangeQuery)o;
        return Objects.equals(this.getFieldName(), that.getFieldName()) && Objects.equals(this.getRange(), that.getRange());
    }

    public int hashCode() {
        return Objects.hash(this.getFieldName(), this.getRange());
    }
}

