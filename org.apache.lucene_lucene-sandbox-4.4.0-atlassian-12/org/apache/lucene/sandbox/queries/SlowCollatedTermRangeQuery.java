/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.Terms
 *  org.apache.lucene.index.TermsEnum
 *  org.apache.lucene.search.MultiTermQuery
 *  org.apache.lucene.util.AttributeSource
 *  org.apache.lucene.util.ToStringUtils
 */
package org.apache.lucene.sandbox.queries;

import java.io.IOException;
import java.text.Collator;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.sandbox.queries.SlowCollatedTermRangeTermsEnum;
import org.apache.lucene.search.MultiTermQuery;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.util.ToStringUtils;

@Deprecated
public class SlowCollatedTermRangeQuery
extends MultiTermQuery {
    private String lowerTerm;
    private String upperTerm;
    private boolean includeLower;
    private boolean includeUpper;
    private Collator collator;

    public SlowCollatedTermRangeQuery(String field, String lowerTerm, String upperTerm, boolean includeLower, boolean includeUpper, Collator collator) {
        super(field);
        this.lowerTerm = lowerTerm;
        this.upperTerm = upperTerm;
        this.includeLower = includeLower;
        this.includeUpper = includeUpper;
        this.collator = collator;
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

    protected TermsEnum getTermsEnum(Terms terms, AttributeSource atts) throws IOException {
        if (this.lowerTerm != null && this.upperTerm != null && this.collator.compare(this.lowerTerm, this.upperTerm) > 0) {
            return TermsEnum.EMPTY;
        }
        TermsEnum tenum = terms.iterator(null);
        if (this.lowerTerm == null && this.upperTerm == null) {
            return tenum;
        }
        return new SlowCollatedTermRangeTermsEnum(tenum, this.lowerTerm, this.upperTerm, this.includeLower, this.includeUpper, this.collator);
    }

    @Deprecated
    public String field() {
        return this.getField();
    }

    public String toString(String field) {
        StringBuilder buffer = new StringBuilder();
        if (!this.getField().equals(field)) {
            buffer.append(this.getField());
            buffer.append(":");
        }
        buffer.append(this.includeLower ? (char)'[' : '{');
        buffer.append(this.lowerTerm != null ? this.lowerTerm : "*");
        buffer.append(" TO ");
        buffer.append(this.upperTerm != null ? this.upperTerm : "*");
        buffer.append(this.includeUpper ? (char)']' : '}');
        buffer.append(ToStringUtils.boost((float)this.getBoost()));
        return buffer.toString();
    }

    public int hashCode() {
        int prime = 31;
        int result = super.hashCode();
        result = 31 * result + (this.collator == null ? 0 : this.collator.hashCode());
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
        if (((Object)((Object)this)).getClass() != obj.getClass()) {
            return false;
        }
        SlowCollatedTermRangeQuery other = (SlowCollatedTermRangeQuery)((Object)obj);
        if (this.collator == null ? other.collator != null : !this.collator.equals(other.collator)) {
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

