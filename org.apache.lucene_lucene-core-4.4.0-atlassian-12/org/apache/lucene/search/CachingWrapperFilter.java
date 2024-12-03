/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.search.BitsFilteredDocIdSet;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.Filter;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.FixedBitSet;
import org.apache.lucene.util.RamUsageEstimator;

public class CachingWrapperFilter
extends Filter {
    private final Filter filter;
    private final Map<Object, DocIdSet> cache = Collections.synchronizedMap(new WeakHashMap());
    int hitCount;
    int missCount;
    protected static final DocIdSet EMPTY_DOCIDSET = new DocIdSet(){

        @Override
        public DocIdSetIterator iterator() {
            return DocIdSetIterator.empty();
        }

        @Override
        public boolean isCacheable() {
            return true;
        }

        @Override
        public Bits bits() {
            return null;
        }
    };

    public CachingWrapperFilter(Filter filter) {
        this.filter = filter;
    }

    protected DocIdSet docIdSetToCache(DocIdSet docIdSet, AtomicReader reader) throws IOException {
        if (docIdSet == null) {
            return EMPTY_DOCIDSET;
        }
        if (docIdSet.isCacheable()) {
            return docIdSet;
        }
        DocIdSetIterator it = docIdSet.iterator();
        if (it == null) {
            return EMPTY_DOCIDSET;
        }
        FixedBitSet bits = new FixedBitSet(reader.maxDoc());
        bits.or(it);
        return bits;
    }

    @Override
    public DocIdSet getDocIdSet(AtomicReaderContext context, Bits acceptDocs) throws IOException {
        AtomicReader reader = context.reader();
        Object key = reader.getCoreCacheKey();
        DocIdSet docIdSet = this.cache.get(key);
        if (docIdSet != null) {
            ++this.hitCount;
        } else {
            ++this.missCount;
            docIdSet = this.docIdSetToCache(this.filter.getDocIdSet(context, null), reader);
            this.cache.put(key, docIdSet);
        }
        return docIdSet == EMPTY_DOCIDSET ? null : BitsFilteredDocIdSet.wrap(docIdSet, acceptDocs);
    }

    public String toString() {
        return "CachingWrapperFilter(" + this.filter + ")";
    }

    public boolean equals(Object o) {
        if (!(o instanceof CachingWrapperFilter)) {
            return false;
        }
        CachingWrapperFilter other = (CachingWrapperFilter)o;
        return this.filter.equals(other.filter);
    }

    public int hashCode() {
        return this.filter.hashCode() ^ 0x1117BF25;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public long sizeInBytes() {
        ArrayList<DocIdSet> docIdSets;
        Map<Object, DocIdSet> map = this.cache;
        synchronized (map) {
            docIdSets = new ArrayList<DocIdSet>(this.cache.values());
        }
        long total = 0L;
        for (DocIdSet dis : docIdSets) {
            total += RamUsageEstimator.sizeOf(dis);
        }
        return total;
    }
}

