/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.search.MultiTermQuery
 *  org.apache.lucene.search.MultiTermQueryWrapperFilter
 */
package org.apache.lucene.sandbox.queries;

import java.text.Collator;
import org.apache.lucene.sandbox.queries.SlowCollatedTermRangeQuery;
import org.apache.lucene.search.MultiTermQuery;
import org.apache.lucene.search.MultiTermQueryWrapperFilter;

@Deprecated
public class SlowCollatedTermRangeFilter
extends MultiTermQueryWrapperFilter<SlowCollatedTermRangeQuery> {
    public SlowCollatedTermRangeFilter(String fieldName, String lowerTerm, String upperTerm, boolean includeLower, boolean includeUpper, Collator collator) {
        super((MultiTermQuery)new SlowCollatedTermRangeQuery(fieldName, lowerTerm, upperTerm, includeLower, includeUpper, collator));
    }

    public String getLowerTerm() {
        return ((SlowCollatedTermRangeQuery)this.query).getLowerTerm();
    }

    public String getUpperTerm() {
        return ((SlowCollatedTermRangeQuery)this.query).getUpperTerm();
    }

    public boolean includesLower() {
        return ((SlowCollatedTermRangeQuery)this.query).includesLower();
    }

    public boolean includesUpper() {
        return ((SlowCollatedTermRangeQuery)this.query).includesUpper();
    }

    public Collator getCollator() {
        return ((SlowCollatedTermRangeQuery)this.query).getCollator();
    }
}

