/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.search.DocIdSet;
import com.atlassian.lucene36.search.DocIdSetIterator;
import com.atlassian.lucene36.search.Filter;
import com.atlassian.lucene36.search.FilteredDocIdSet;
import com.atlassian.lucene36.util.FixedBitSet;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.WeakHashMap;

public class CachingWrapperFilter
extends Filter {
    Filter filter;
    protected final FilterCache<DocIdSet> cache;
    int hitCount;
    int missCount;

    public CachingWrapperFilter(Filter filter) {
        this(filter, DeletesMode.IGNORE);
    }

    public CachingWrapperFilter(Filter filter, DeletesMode deletesMode) {
        this.filter = filter;
        this.cache = new FilterCache<DocIdSet>(deletesMode){

            @Override
            public DocIdSet mergeDeletes(final IndexReader r, DocIdSet docIdSet) {
                return new FilteredDocIdSet(docIdSet){

                    protected boolean match(int docID) {
                        return !r.isDeleted(docID);
                    }
                };
            }
        };
    }

    protected DocIdSet docIdSetToCache(DocIdSet docIdSet, IndexReader reader) throws IOException {
        if (docIdSet == null) {
            return DocIdSet.EMPTY_DOCIDSET;
        }
        if (docIdSet.isCacheable()) {
            return docIdSet;
        }
        DocIdSetIterator it = docIdSet.iterator();
        if (it == null) {
            return DocIdSet.EMPTY_DOCIDSET;
        }
        FixedBitSet bits = new FixedBitSet(reader.maxDoc());
        bits.or(it);
        return bits;
    }

    public DocIdSet getDocIdSet(IndexReader reader) throws IOException {
        Object delCoreKey;
        Object coreKey = reader.getCoreCacheKey();
        DocIdSet docIdSet = this.cache.get(reader, coreKey, delCoreKey = reader.hasDeletions() ? reader.getDeletesCacheKey() : coreKey);
        if (docIdSet != null) {
            ++this.hitCount;
            return docIdSet;
        }
        ++this.missCount;
        docIdSet = this.docIdSetToCache(this.filter.getDocIdSet(reader), reader);
        if (docIdSet != null) {
            this.cache.put(coreKey, delCoreKey, docIdSet);
        }
        return docIdSet;
    }

    public String toString() {
        return "CachingWrapperFilter(" + this.filter + ")";
    }

    public boolean equals(Object o) {
        if (!(o instanceof CachingWrapperFilter)) {
            return false;
        }
        return this.filter.equals(((CachingWrapperFilter)o).filter);
    }

    public int hashCode() {
        return this.filter.hashCode() ^ 0x1117BF25;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static abstract class FilterCache<T>
    implements Serializable {
        transient Map<Object, T> cache;
        private final DeletesMode deletesMode;

        public FilterCache(DeletesMode deletesMode) {
            this.deletesMode = deletesMode;
        }

        public synchronized T get(IndexReader reader, Object coreKey, Object delCoreKey) throws IOException {
            T value;
            if (this.cache == null) {
                this.cache = new WeakHashMap<Object, T>();
            }
            if (this.deletesMode == DeletesMode.IGNORE) {
                value = this.cache.get(coreKey);
            } else if (this.deletesMode == DeletesMode.RECACHE) {
                value = this.cache.get(delCoreKey);
            } else {
                assert (this.deletesMode == DeletesMode.DYNAMIC);
                value = this.cache.get(delCoreKey);
                if (value == null && (value = this.cache.get(coreKey)) != null && reader.hasDeletions()) {
                    value = this.mergeDeletes(reader, value);
                }
            }
            return value;
        }

        protected abstract T mergeDeletes(IndexReader var1, T var2);

        public synchronized void put(Object coreKey, Object delCoreKey, T value) {
            if (this.deletesMode == DeletesMode.IGNORE) {
                this.cache.put(coreKey, value);
            } else if (this.deletesMode == DeletesMode.RECACHE) {
                this.cache.put(delCoreKey, value);
            } else {
                this.cache.put(coreKey, value);
                this.cache.put(delCoreKey, value);
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum DeletesMode {
        IGNORE,
        RECACHE,
        DYNAMIC;

    }
}

