/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.index.Term;
import com.atlassian.lucene36.search.MultiTermQueryWrapperFilter;
import com.atlassian.lucene36.search.PrefixQuery;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class PrefixFilter
extends MultiTermQueryWrapperFilter<PrefixQuery> {
    public PrefixFilter(Term prefix) {
        super(new PrefixQuery(prefix));
    }

    public Term getPrefix() {
        return ((PrefixQuery)this.query).getPrefix();
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("PrefixFilter(");
        buffer.append(this.getPrefix().toString());
        buffer.append(")");
        return buffer.toString();
    }
}

