/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.AtomicReaderContext
 *  org.apache.lucene.index.Term
 *  org.apache.lucene.search.DocIdSet
 *  org.apache.lucene.search.Filter
 *  org.apache.lucene.util.Bits
 */
package com.atlassian.confluence.impl.search.v2.lucene.filter;

import com.atlassian.confluence.impl.search.v2.lucene.filter.MultiTermFilter;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.Filter;
import org.apache.lucene.util.Bits;

public class SpaceFilter
extends Filter {
    private final Filter spaceFilter;

    public SpaceFilter(Collection<String> spaceKeys, boolean negate) {
        this.spaceFilter = SpaceFilter.createFilter(spaceKeys, negate);
    }

    public SpaceFilter(Collection<String> spaceKeys) {
        this(spaceKeys, false);
    }

    public SpaceFilter(String spaceKey) {
        this(Arrays.asList(spaceKey));
    }

    public SpaceFilter(String spaceKey, boolean negate) {
        this(Arrays.asList(spaceKey), negate);
    }

    public static Filter createFilter(Collection<String> spaceKeys, boolean negate) {
        MultiTermFilter spaceFilter = new MultiTermFilter(negate);
        for (String spaceKey : spaceKeys) {
            spaceFilter.addTerm(new Term(SearchFieldNames.SPACE_KEY, spaceKey));
        }
        return spaceFilter;
    }

    public DocIdSet getDocIdSet(AtomicReaderContext context, Bits acceptDocs) throws IOException {
        return this.spaceFilter.getDocIdSet(context, acceptDocs);
    }
}

