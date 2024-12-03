/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import org.apache.lucene.search.MultiTermQueryWrapperFilter;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.util.BytesRef;

public class TermRangeFilter
extends MultiTermQueryWrapperFilter<TermRangeQuery> {
    public TermRangeFilter(String fieldName, BytesRef lowerTerm, BytesRef upperTerm, boolean includeLower, boolean includeUpper) {
        super(new TermRangeQuery(fieldName, lowerTerm, upperTerm, includeLower, includeUpper));
    }

    public static TermRangeFilter newStringRange(String field, String lowerTerm, String upperTerm, boolean includeLower, boolean includeUpper) {
        BytesRef lower = lowerTerm == null ? null : new BytesRef(lowerTerm);
        BytesRef upper = upperTerm == null ? null : new BytesRef(upperTerm);
        return new TermRangeFilter(field, lower, upper, includeLower, includeUpper);
    }

    public static TermRangeFilter Less(String fieldName, BytesRef upperTerm) {
        return new TermRangeFilter(fieldName, null, upperTerm, false, true);
    }

    public static TermRangeFilter More(String fieldName, BytesRef lowerTerm) {
        return new TermRangeFilter(fieldName, lowerTerm, null, true, false);
    }

    public BytesRef getLowerTerm() {
        return ((TermRangeQuery)this.query).getLowerTerm();
    }

    public BytesRef getUpperTerm() {
        return ((TermRangeQuery)this.query).getUpperTerm();
    }

    public boolean includesLower() {
        return ((TermRangeQuery)this.query).includesLower();
    }

    public boolean includesUpper() {
        return ((TermRangeQuery)this.query).includesUpper();
    }
}

