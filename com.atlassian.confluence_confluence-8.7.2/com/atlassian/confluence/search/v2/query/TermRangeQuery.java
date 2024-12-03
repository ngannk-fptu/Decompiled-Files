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
public class TermRangeQuery
implements SearchQuery {
    public static final String KEY = "termRange";
    private final String fieldName;
    private final String lowerTerm;
    private final String upperTerm;
    private final boolean includeUpper;
    private final boolean includeLower;

    public TermRangeQuery(String fieldName, String lowerTerm, String upperTerm, boolean includeLower, boolean includeUpper) {
        this.fieldName = fieldName;
        this.lowerTerm = lowerTerm;
        this.upperTerm = upperTerm;
        this.includeUpper = includeUpper;
        this.includeLower = includeLower;
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public List getParameters() {
        return Arrays.asList(this.fieldName, this.lowerTerm, this.upperTerm, this.includeLower, this.includeUpper);
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public String getLowerTerm() {
        return this.lowerTerm;
    }

    public String getUpperTerm() {
        return this.upperTerm;
    }

    public boolean isIncludeUpper() {
        return this.includeUpper;
    }

    public boolean isIncludeLower() {
        return this.includeLower;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TermRangeQuery)) {
            return false;
        }
        TermRangeQuery that = (TermRangeQuery)o;
        return this.isIncludeUpper() == that.isIncludeUpper() && this.isIncludeLower() == that.isIncludeLower() && Objects.equals(this.getFieldName(), that.getFieldName()) && Objects.equals(this.getLowerTerm(), that.getLowerTerm()) && Objects.equals(this.getUpperTerm(), that.getUpperTerm());
    }

    public int hashCode() {
        return Objects.hash(this.getFieldName(), this.getLowerTerm(), this.getUpperTerm(), this.isIncludeUpper(), this.isIncludeLower());
    }
}

