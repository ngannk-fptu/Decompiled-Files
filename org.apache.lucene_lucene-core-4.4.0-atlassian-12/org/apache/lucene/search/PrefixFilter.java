/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.MultiTermQueryWrapperFilter;
import org.apache.lucene.search.PrefixQuery;

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

