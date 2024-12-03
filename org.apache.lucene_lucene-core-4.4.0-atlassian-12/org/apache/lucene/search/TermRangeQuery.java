/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import java.io.IOException;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.MultiTermQuery;
import org.apache.lucene.search.TermRangeTermsEnum;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.ToStringUtils;

public class TermRangeQuery
extends MultiTermQuery {
    private BytesRef lowerTerm;
    private BytesRef upperTerm;
    private boolean includeLower;
    private boolean includeUpper;

    public TermRangeQuery(String field, BytesRef lowerTerm, BytesRef upperTerm, boolean includeLower, boolean includeUpper) {
        super(field);
        this.lowerTerm = lowerTerm;
        this.upperTerm = upperTerm;
        this.includeLower = includeLower;
        this.includeUpper = includeUpper;
    }

    public static TermRangeQuery newStringRange(String field, String lowerTerm, String upperTerm, boolean includeLower, boolean includeUpper) {
        BytesRef lower = lowerTerm == null ? null : new BytesRef(lowerTerm);
        BytesRef upper = upperTerm == null ? null : new BytesRef(upperTerm);
        return new TermRangeQuery(field, lower, upper, includeLower, includeUpper);
    }

    public BytesRef getLowerTerm() {
        return this.lowerTerm;
    }

    public BytesRef getUpperTerm() {
        return this.upperTerm;
    }

    public boolean includesLower() {
        return this.includeLower;
    }

    public boolean includesUpper() {
        return this.includeUpper;
    }

    @Override
    protected TermsEnum getTermsEnum(Terms terms, AttributeSource atts) throws IOException {
        if (this.lowerTerm != null && this.upperTerm != null && this.lowerTerm.compareTo(this.upperTerm) > 0) {
            return TermsEnum.EMPTY;
        }
        TermsEnum tenum = terms.iterator(null);
        if ((this.lowerTerm == null || this.includeLower && this.lowerTerm.length == 0) && this.upperTerm == null) {
            return tenum;
        }
        return new TermRangeTermsEnum(tenum, this.lowerTerm, this.upperTerm, this.includeLower, this.includeUpper);
    }

    @Override
    public String toString(String field) {
        StringBuilder buffer = new StringBuilder();
        if (!this.getField().equals(field)) {
            buffer.append(this.getField());
            buffer.append(":");
        }
        buffer.append(this.includeLower ? (char)'[' : '{');
        buffer.append(this.lowerTerm != null ? ("*".equals(Term.toString(this.lowerTerm)) ? "\\*" : Term.toString(this.lowerTerm)) : "*");
        buffer.append(" TO ");
        buffer.append(this.upperTerm != null ? ("*".equals(Term.toString(this.upperTerm)) ? "\\*" : Term.toString(this.upperTerm)) : "*");
        buffer.append(this.includeUpper ? (char)']' : '}');
        buffer.append(ToStringUtils.boost(this.getBoost()));
        return buffer.toString();
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = super.hashCode();
        result = 31 * result + (this.includeLower ? 1231 : 1237);
        result = 31 * result + (this.includeUpper ? 1231 : 1237);
        result = 31 * result + (this.lowerTerm == null ? 0 : this.lowerTerm.hashCode());
        result = 31 * result + (this.upperTerm == null ? 0 : this.upperTerm.hashCode());
        return result;
    }

    @Override
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

