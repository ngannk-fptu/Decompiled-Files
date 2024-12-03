/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.search.MultiTermQueryWrapperFilter;
import com.atlassian.lucene36.search.TermRangeQuery;
import java.text.Collator;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class TermRangeFilter
extends MultiTermQueryWrapperFilter<TermRangeQuery> {
    public TermRangeFilter(String fieldName, String lowerTerm, String upperTerm, boolean includeLower, boolean includeUpper) {
        super(new TermRangeQuery(fieldName, lowerTerm, upperTerm, includeLower, includeUpper));
    }

    public TermRangeFilter(String fieldName, String lowerTerm, String upperTerm, boolean includeLower, boolean includeUpper, Collator collator) {
        super(new TermRangeQuery(fieldName, lowerTerm, upperTerm, includeLower, includeUpper, collator));
    }

    public static TermRangeFilter Less(String fieldName, String upperTerm) {
        return new TermRangeFilter(fieldName, null, upperTerm, false, true);
    }

    public static TermRangeFilter More(String fieldName, String lowerTerm) {
        return new TermRangeFilter(fieldName, lowerTerm, null, true, false);
    }

    public String getField() {
        return ((TermRangeQuery)this.query).getField();
    }

    public String getLowerTerm() {
        return ((TermRangeQuery)this.query).getLowerTerm();
    }

    public String getUpperTerm() {
        return ((TermRangeQuery)this.query).getUpperTerm();
    }

    public boolean includesLower() {
        return ((TermRangeQuery)this.query).includesLower();
    }

    public boolean includesUpper() {
        return ((TermRangeQuery)this.query).includesUpper();
    }

    public Collator getCollator() {
        return ((TermRangeQuery)this.query).getCollator();
    }
}

