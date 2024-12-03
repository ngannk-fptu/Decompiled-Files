/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.FilteredTermsEnum
 *  org.apache.lucene.index.Term
 *  org.apache.lucene.index.Terms
 *  org.apache.lucene.search.MultiTermQuery
 *  org.apache.lucene.util.AttributeSource
 *  org.apache.lucene.util.ToStringUtils
 */
package org.apache.lucene.sandbox.queries.regex;

import java.io.IOException;
import org.apache.lucene.index.FilteredTermsEnum;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.sandbox.queries.regex.JavaUtilRegexCapabilities;
import org.apache.lucene.sandbox.queries.regex.RegexCapabilities;
import org.apache.lucene.sandbox.queries.regex.RegexQueryCapable;
import org.apache.lucene.sandbox.queries.regex.RegexTermsEnum;
import org.apache.lucene.search.MultiTermQuery;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.util.ToStringUtils;

public class RegexQuery
extends MultiTermQuery
implements RegexQueryCapable {
    private RegexCapabilities regexImpl = new JavaUtilRegexCapabilities();
    private Term term;

    public RegexQuery(Term term) {
        super(term.field());
        this.term = term;
    }

    public Term getTerm() {
        return this.term;
    }

    @Override
    public void setRegexImplementation(RegexCapabilities impl) {
        this.regexImpl = impl;
    }

    @Override
    public RegexCapabilities getRegexImplementation() {
        return this.regexImpl;
    }

    protected FilteredTermsEnum getTermsEnum(Terms terms, AttributeSource atts) throws IOException {
        return new RegexTermsEnum(terms.iterator(null), this.term, this.regexImpl);
    }

    public String toString(String field) {
        StringBuilder buffer = new StringBuilder();
        if (!this.term.field().equals(field)) {
            buffer.append(this.term.field());
            buffer.append(":");
        }
        buffer.append(this.term.text());
        buffer.append(ToStringUtils.boost((float)this.getBoost()));
        return buffer.toString();
    }

    public int hashCode() {
        int prime = 31;
        int result = super.hashCode();
        result = 31 * result + (this.regexImpl == null ? 0 : this.regexImpl.hashCode());
        result = 31 * result + (this.term == null ? 0 : this.term.hashCode());
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
        RegexQuery other = (RegexQuery)obj;
        if (this.regexImpl == null ? other.regexImpl != null : !this.regexImpl.equals(other.regexImpl)) {
            return false;
        }
        return !(this.term == null ? other.term != null : !this.term.equals((Object)other.term));
    }
}

