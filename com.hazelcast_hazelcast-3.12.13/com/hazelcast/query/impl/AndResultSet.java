/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl;

import com.hazelcast.query.Predicate;
import com.hazelcast.query.impl.QueryableEntry;
import com.hazelcast.util.Preconditions;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public class AndResultSet
extends AbstractSet<QueryableEntry> {
    private static final int SIZE_UNINITIALIZED = -1;
    private final Set<QueryableEntry> setSmallest;
    private final List<Set<QueryableEntry>> otherIndexedResults;
    private final List<Predicate> lsNoIndexPredicates;
    private int cachedSize;

    public AndResultSet(Set<QueryableEntry> setSmallest, List<Set<QueryableEntry>> otherIndexedResults, List<Predicate> lsNoIndexPredicates) {
        this.setSmallest = Preconditions.isNotNull(setSmallest, "setSmallest");
        this.otherIndexedResults = otherIndexedResults;
        this.lsNoIndexPredicates = lsNoIndexPredicates;
        this.cachedSize = -1;
    }

    @Override
    public boolean contains(Object o) {
        if (!this.setSmallest.contains(o)) {
            return false;
        }
        if (this.otherIndexedResults != null) {
            for (Set set : this.otherIndexedResults) {
                if (set.contains(o)) continue;
                return false;
            }
        }
        if (this.lsNoIndexPredicates != null) {
            for (Predicate predicate : this.lsNoIndexPredicates) {
                if (predicate.apply((Map.Entry)o)) continue;
                return false;
            }
        }
        return true;
    }

    @Override
    public Iterator<QueryableEntry> iterator() {
        return new It();
    }

    @Override
    public int size() {
        if (this.cachedSize == -1) {
            int calculatedSize = 0;
            Iterator<QueryableEntry> it = this.iterator();
            while (it.hasNext()) {
                ++calculatedSize;
                it.next();
            }
            this.cachedSize = calculatedSize;
        }
        return this.cachedSize;
    }

    public int estimatedSize() {
        if (this.cachedSize == -1) {
            if (this.setSmallest == null) {
                return 0;
            }
            return this.setSmallest.size();
        }
        return this.cachedSize;
    }

    class It
    implements Iterator<QueryableEntry> {
        QueryableEntry currentEntry;
        final Iterator<QueryableEntry> it;

        It() {
            this.it = AndResultSet.this.setSmallest.iterator();
        }

        @Override
        public boolean hasNext() {
            if (this.currentEntry != null) {
                return true;
            }
            while (this.it.hasNext()) {
                QueryableEntry entry = this.it.next();
                if (!this.checkOtherIndexedResults(entry) || !this.checkNoIndexPredicates(entry)) continue;
                this.currentEntry = entry;
                return true;
            }
            return false;
        }

        private boolean checkNoIndexPredicates(QueryableEntry currentEntry) {
            if (AndResultSet.this.lsNoIndexPredicates == null) {
                return true;
            }
            for (Predicate noIndexPredicate : AndResultSet.this.lsNoIndexPredicates) {
                if (noIndexPredicate.apply(currentEntry)) continue;
                return false;
            }
            return true;
        }

        private boolean checkOtherIndexedResults(QueryableEntry currentEntry) {
            if (AndResultSet.this.otherIndexedResults == null) {
                return true;
            }
            for (Set otherIndexedResult : AndResultSet.this.otherIndexedResults) {
                if (otherIndexedResult.contains(currentEntry)) continue;
                return false;
            }
            return true;
        }

        @Override
        public QueryableEntry next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            QueryableEntry result = this.currentEntry;
            this.currentEntry = null;
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}

