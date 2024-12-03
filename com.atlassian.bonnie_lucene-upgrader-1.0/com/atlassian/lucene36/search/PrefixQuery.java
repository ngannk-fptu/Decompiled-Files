/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.index.Term;
import com.atlassian.lucene36.search.FilteredTermEnum;
import com.atlassian.lucene36.search.MultiTermQuery;
import com.atlassian.lucene36.search.PrefixTermEnum;
import com.atlassian.lucene36.util.ToStringUtils;
import java.io.IOException;

public class PrefixQuery
extends MultiTermQuery {
    private Term prefix;

    public PrefixQuery(Term prefix) {
        this.prefix = prefix;
    }

    public Term getPrefix() {
        return this.prefix;
    }

    protected FilteredTermEnum getEnum(IndexReader reader) throws IOException {
        return new PrefixTermEnum(reader, this.prefix);
    }

    public String toString(String field) {
        StringBuilder buffer = new StringBuilder();
        if (!this.prefix.field().equals(field)) {
            buffer.append(this.prefix.field());
            buffer.append(":");
        }
        buffer.append(this.prefix.text());
        buffer.append('*');
        buffer.append(ToStringUtils.boost(this.getBoost()));
        return buffer.toString();
    }

    public int hashCode() {
        int prime = 31;
        int result = super.hashCode();
        result = 31 * result + (this.prefix == null ? 0 : this.prefix.hashCode());
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
        PrefixQuery other = (PrefixQuery)obj;
        return !(this.prefix == null ? other.prefix != null : !this.prefix.equals(other.prefix));
    }
}

