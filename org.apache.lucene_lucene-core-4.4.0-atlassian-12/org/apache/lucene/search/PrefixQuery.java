/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import java.io.IOException;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.MultiTermQuery;
import org.apache.lucene.search.PrefixTermsEnum;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.util.ToStringUtils;

public class PrefixQuery
extends MultiTermQuery {
    private Term prefix;

    public PrefixQuery(Term prefix) {
        super(prefix.field());
        this.prefix = prefix;
    }

    public Term getPrefix() {
        return this.prefix;
    }

    @Override
    protected TermsEnum getTermsEnum(Terms terms, AttributeSource atts) throws IOException {
        TermsEnum tenum = terms.iterator(null);
        if (this.prefix.bytes().length == 0) {
            return tenum;
        }
        return new PrefixTermsEnum(tenum, this.prefix.bytes());
    }

    @Override
    public String toString(String field) {
        StringBuilder buffer = new StringBuilder();
        if (!this.getField().equals(field)) {
            buffer.append(this.getField());
            buffer.append(":");
        }
        buffer.append(this.prefix.text());
        buffer.append('*');
        buffer.append(ToStringUtils.boost(this.getBoost()));
        return buffer.toString();
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = super.hashCode();
        result = 31 * result + (this.prefix == null ? 0 : this.prefix.hashCode());
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
        PrefixQuery other = (PrefixQuery)obj;
        return !(this.prefix == null ? other.prefix != null : !this.prefix.equals(other.prefix));
    }
}

