/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.confluence.search.v2.query;

import com.atlassian.confluence.search.v2.Range;
import com.atlassian.confluence.search.v2.SearchPrimitive;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Objects;

@SearchPrimitive
public class LongRangeQuery
implements SearchQuery {
    public static final String KEY = "longRange";
    private final String fieldName;
    private final Range<Long> range;

    public LongRangeQuery(String fieldName, Range<Long> range) {
        this.fieldName = fieldName;
        this.range = range;
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public List getParameters() {
        return ImmutableList.of((Object)this.fieldName, this.range);
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public Range<Long> getRange() {
        return this.range;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LongRangeQuery)) {
            return false;
        }
        LongRangeQuery that = (LongRangeQuery)o;
        return Objects.equals(this.getFieldName(), that.getFieldName()) && Objects.equals(this.getRange(), that.getRange());
    }

    public int hashCode() {
        return Objects.hash(this.getFieldName(), this.getRange());
    }
}

