/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.search.FilteredTermEnum;
import com.atlassian.lucene36.search.MultiTermQuery;
import com.atlassian.lucene36.search.TermRangeTermEnum;
import com.atlassian.lucene36.util.ToStringUtils;
import java.io.IOException;
import java.text.Collator;

public class TermRangeQuery
extends MultiTermQuery {
    private String lowerTerm;
    private String upperTerm;
    private Collator collator;
    private String field;
    private boolean includeLower;
    private boolean includeUpper;

    public TermRangeQuery(String field, String lowerTerm, String upperTerm, boolean includeLower, boolean includeUpper) {
        this(field, lowerTerm, upperTerm, includeLower, includeUpper, null);
    }

    public TermRangeQuery(String field, String lowerTerm, String upperTerm, boolean includeLower, boolean includeUpper, Collator collator) {
        this.field = field;
        this.lowerTerm = lowerTerm;
        this.upperTerm = upperTerm;
        this.includeLower = includeLower;
        this.includeUpper = includeUpper;
        this.collator = collator;
    }

    public String getField() {
        return this.field;
    }

    public String getLowerTerm() {
        return this.lowerTerm;
    }

    public String getUpperTerm() {
        return this.upperTerm;
    }

    public boolean includesLower() {
        return this.includeLower;
    }

    public boolean includesUpper() {
        return this.includeUpper;
    }

    public Collator getCollator() {
        return this.collator;
    }

    protected FilteredTermEnum getEnum(IndexReader reader) throws IOException {
        return new TermRangeTermEnum(reader, this.field, this.lowerTerm, this.upperTerm, this.includeLower, this.includeUpper, this.collator);
    }

    public String toString(String field) {
        StringBuilder buffer = new StringBuilder();
        if (!this.getField().equals(field)) {
            buffer.append(this.getField());
            buffer.append(":");
        }
        buffer.append(this.includeLower ? (char)'[' : '{');
        buffer.append(this.lowerTerm != null ? ("*".equals(this.lowerTerm) ? "\\*" : this.lowerTerm) : "*");
        buffer.append(" TO ");
        buffer.append(this.upperTerm != null ? ("*".equals(this.upperTerm) ? "\\*" : this.upperTerm) : "*");
        buffer.append(this.includeUpper ? (char)']' : '}');
        buffer.append(ToStringUtils.boost(this.getBoost()));
        return buffer.toString();
    }

    public int hashCode() {
        int prime = 31;
        int result = super.hashCode();
        result = 31 * result + (this.collator == null ? 0 : this.collator.hashCode());
        result = 31 * result + (this.field == null ? 0 : this.field.hashCode());
        result = 31 * result + (this.includeLower ? 1231 : 1237);
        result = 31 * result + (this.includeUpper ? 1231 : 1237);
        result = 31 * result + (this.lowerTerm == null ? 0 : this.lowerTerm.hashCode());
        result = 31 * result + (this.upperTerm == null ? 0 : this.upperTerm.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        TermRangeQuery other = (TermRangeQuery)obj;
        if (this.collator == null ? other.collator != null : !this.collator.equals(other.collator)) {
            return false;
        }
        if (this.field == null ? other.field != null : !this.field.equals(other.field)) {
            return false;
        }
        if (this.includeLower != other.includeLower) {
            return false;
        }
        if (this.includeUpper != other.includeUpper) {
            return false;
        }
        if (this.lowerTerm == null ? other.lowerTerm != null : !this.lowerTerm.equals(other.lowerTerm)) {
            return false;
        }
        return !(this.upperTerm == null ? other.upperTerm != null : !this.upperTerm.equals(other.upperTerm));
    }
}

