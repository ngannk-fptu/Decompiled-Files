/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.search.BooleanClause$Occur
 *  org.apache.lucene.search.Filter
 */
package org.apache.lucene.queries;

import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.Filter;

public final class FilterClause {
    private final BooleanClause.Occur occur;
    private final Filter filter;

    public FilterClause(Filter filter, BooleanClause.Occur occur) {
        this.occur = occur;
        this.filter = filter;
    }

    public Filter getFilter() {
        return this.filter;
    }

    public BooleanClause.Occur getOccur() {
        return this.occur;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || !(o instanceof FilterClause)) {
            return false;
        }
        FilterClause other = (FilterClause)o;
        return this.filter.equals(other.filter) && this.occur == other.occur;
    }

    public int hashCode() {
        return this.filter.hashCode() ^ this.occur.hashCode();
    }

    public String toString() {
        return this.occur.toString() + this.filter.toString();
    }
}

