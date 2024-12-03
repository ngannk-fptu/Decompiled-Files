/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.AtomicReader
 *  org.apache.lucene.index.AtomicReaderContext
 *  org.apache.lucene.search.DocIdSet
 *  org.apache.lucene.search.Filter
 *  org.apache.lucene.util.Bits
 *  org.apache.lucene.util.OpenBitSet
 *  org.apache.lucene.util.OpenBitSetDISI
 */
package com.atlassian.confluence.impl.search.v2.lucene.filter;

import java.io.IOException;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.Filter;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.OpenBitSet;
import org.apache.lucene.util.OpenBitSetDISI;

public class SpacePermissionsFilter
extends Filter {
    private final Filter spaceLessFilter;
    private final Filter permittedSpacesFilter;

    protected SpacePermissionsFilter(Filter spaceLessFilter, Filter permittedSpacesFilter) {
        this.spaceLessFilter = spaceLessFilter;
        this.permittedSpacesFilter = permittedSpacesFilter;
    }

    public DocIdSet getDocIdSet(AtomicReaderContext context, Bits acceptDocs) throws IOException {
        AtomicReader reader = context.reader();
        DocIdSet spaceFilterResult = this.permittedSpacesFilter.getDocIdSet(context, acceptDocs);
        DocIdSet spaceLessFilterResult = this.spaceLessFilter.getDocIdSet(context, acceptDocs);
        if (spaceFilterResult instanceof OpenBitSet && spaceLessFilterResult instanceof OpenBitSet) {
            OpenBitSet spaceFilterOpenBitSetResult = (OpenBitSet)spaceFilterResult;
            OpenBitSet spaceLessFilterOpenBitSetResult = (OpenBitSet)spaceLessFilterResult;
            spaceFilterOpenBitSetResult.or(spaceLessFilterOpenBitSetResult);
            return spaceFilterOpenBitSetResult;
        }
        OpenBitSetDISI spaceFilterResultDISI = new OpenBitSetDISI(spaceFilterResult.iterator(), reader.maxDoc());
        spaceFilterResultDISI.inPlaceOr(spaceLessFilterResult.iterator());
        return spaceFilterResultDISI;
    }
}

