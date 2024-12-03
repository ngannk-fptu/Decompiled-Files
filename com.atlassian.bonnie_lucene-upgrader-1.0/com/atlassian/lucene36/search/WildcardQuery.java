/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.index.Term;
import com.atlassian.lucene36.search.FilteredTermEnum;
import com.atlassian.lucene36.search.MultiTermQuery;
import com.atlassian.lucene36.search.PrefixTermEnum;
import com.atlassian.lucene36.search.SingleTermEnum;
import com.atlassian.lucene36.search.WildcardTermEnum;
import com.atlassian.lucene36.util.ToStringUtils;
import java.io.IOException;

public class WildcardQuery
extends MultiTermQuery {
    private boolean termContainsWildcard;
    private boolean termIsPrefix;
    protected Term term;

    public WildcardQuery(Term term) {
        this.term = term;
        String text = term.text();
        this.termContainsWildcard = text.indexOf(42) != -1 || text.indexOf(63) != -1;
        this.termIsPrefix = this.termContainsWildcard && text.indexOf(63) == -1 && text.indexOf(42) == text.length() - 1;
    }

    protected FilteredTermEnum getEnum(IndexReader reader) throws IOException {
        if (this.termIsPrefix) {
            return new PrefixTermEnum(reader, this.term.createTerm(this.term.text().substring(0, this.term.text().indexOf(42))));
        }
        if (this.termContainsWildcard) {
            return new WildcardTermEnum(reader, this.getTerm());
        }
        return new SingleTermEnum(reader, this.getTerm());
    }

    public Term getTerm() {
        return this.term;
    }

    public String toString(String field) {
        StringBuilder buffer = new StringBuilder();
        if (!this.term.field().equals(field)) {
            buffer.append(this.term.field());
            buffer.append(":");
        }
        buffer.append(this.term.text());
        buffer.append(ToStringUtils.boost(this.getBoost()));
        return buffer.toString();
    }

    public int hashCode() {
        int prime = 31;
        int result = super.hashCode();
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
        WildcardQuery other = (WildcardQuery)obj;
        return !(this.term == null ? other.term != null : !this.term.equals(other.term));
    }
}

