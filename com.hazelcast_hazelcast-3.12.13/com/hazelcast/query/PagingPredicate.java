/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.BinaryInterface;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.query.IndexAwarePredicate;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.VisitablePredicate;
import com.hazelcast.query.impl.Indexes;
import com.hazelcast.query.impl.QueryContext;
import com.hazelcast.query.impl.QueryableEntry;
import com.hazelcast.query.impl.predicates.Visitor;
import com.hazelcast.util.IterationType;
import com.hazelcast.util.SortingUtil;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@BinaryInterface
public class PagingPredicate<K, V>
implements IndexAwarePredicate<K, V>,
VisitablePredicate,
IdentifiedDataSerializable {
    private static final Map.Entry<Integer, Map.Entry> NULL_ANCHOR = new AbstractMap.SimpleImmutableEntry<Integer, Object>(-1, null);
    private List<Map.Entry<Integer, Map.Entry<K, V>>> anchorList;
    private Predicate<K, V> predicate;
    private Comparator<Map.Entry<K, V>> comparator;
    private int pageSize;
    private int page;
    private IterationType iterationType;

    public PagingPredicate() {
    }

    public PagingPredicate(int pageSize) {
        if (pageSize <= 0) {
            throw new IllegalArgumentException("pageSize should be greater than 0!");
        }
        this.pageSize = pageSize;
        this.anchorList = new ArrayList<Map.Entry<Integer, Map.Entry<K, V>>>();
    }

    public PagingPredicate(Predicate predicate, int pageSize) {
        this(pageSize);
        super.setInnerPredicate(predicate);
    }

    public PagingPredicate(Comparator<Map.Entry<K, V>> comparator, int pageSize) {
        this(pageSize);
        this.comparator = comparator;
    }

    public PagingPredicate(Predicate<K, V> predicate, Comparator<Map.Entry<K, V>> comparator, int pageSize) {
        this(pageSize);
        super.setInnerPredicate(predicate);
        this.comparator = comparator;
    }

    private PagingPredicate(PagingPredicate originalPagingPredicate, Predicate predicateReplacement) {
        this.anchorList = originalPagingPredicate.anchorList;
        this.comparator = originalPagingPredicate.comparator;
        this.pageSize = originalPagingPredicate.pageSize;
        this.page = originalPagingPredicate.page;
        this.iterationType = originalPagingPredicate.iterationType;
        this.setInnerPredicate(predicateReplacement);
    }

    @Override
    public Predicate accept(Visitor visitor, Indexes indexes) {
        if (this.predicate instanceof VisitablePredicate) {
            Predicate transformed = ((VisitablePredicate)((Object)this.predicate)).accept(visitor, indexes);
            return transformed == this.predicate ? this : new PagingPredicate<K, V>(this, transformed);
        }
        return this;
    }

    private void setInnerPredicate(Predicate<K, V> predicate) {
        if (predicate instanceof PagingPredicate) {
            throw new IllegalArgumentException("Nested PagingPredicate is not supported!");
        }
        this.predicate = predicate;
    }

    @Override
    public Set<QueryableEntry<K, V>> filter(QueryContext queryContext) {
        if (!(this.predicate instanceof IndexAwarePredicate)) {
            return null;
        }
        Set set = ((IndexAwarePredicate)this.predicate).filter(queryContext);
        if (set == null || set.isEmpty()) {
            return set;
        }
        ArrayList<QueryableEntry> resultList = new ArrayList<QueryableEntry>();
        Map.Entry<Integer, Map.Entry> nearestAnchorEntry = this.getNearestAnchorEntry();
        for (QueryableEntry queryableEntry : set) {
            if (!SortingUtil.compareAnchor(this, queryableEntry, nearestAnchorEntry)) continue;
            resultList.add(queryableEntry);
        }
        List<QueryableEntry> sortedSubList = SortingUtil.getSortedSubList(resultList, this, nearestAnchorEntry);
        return new LinkedHashSet<QueryableEntry<K, V>>(sortedSubList);
    }

    @Override
    public boolean isIndexed(QueryContext queryContext) {
        if (this.predicate instanceof IndexAwarePredicate) {
            return ((IndexAwarePredicate)this.predicate).isIndexed(queryContext);
        }
        return false;
    }

    @Override
    public boolean apply(Map.Entry mapEntry) {
        if (this.predicate != null) {
            return this.predicate.apply(mapEntry);
        }
        return true;
    }

    public void reset() {
        this.iterationType = null;
        this.anchorList.clear();
        this.page = 0;
    }

    public void nextPage() {
        ++this.page;
    }

    public void previousPage() {
        if (this.page != 0) {
            --this.page;
        }
    }

    public IterationType getIterationType() {
        return this.iterationType;
    }

    public void setIterationType(IterationType iterationType) {
        this.iterationType = iterationType;
    }

    public int getPage() {
        return this.page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public Predicate<K, V> getPredicate() {
        return this.predicate;
    }

    public Comparator<Map.Entry<K, V>> getComparator() {
        return this.comparator;
    }

    public Map.Entry<K, V> getAnchor() {
        Map.Entry<Integer, Map.Entry<K, V>> anchorEntry = this.anchorList.get(this.page);
        return anchorEntry == null ? null : anchorEntry.getValue();
    }

    void setAnchor(int page, Map.Entry anchor) {
        AbstractMap.SimpleImmutableEntry<Integer, Map.Entry> anchorEntry = new AbstractMap.SimpleImmutableEntry<Integer, Map.Entry>(page, anchor);
        int anchorCount = this.anchorList.size();
        if (page < anchorCount) {
            this.anchorList.set(page, anchorEntry);
        } else if (page == anchorCount) {
            this.anchorList.add(anchorEntry);
        } else {
            throw new IllegalArgumentException("Anchor index is not correct, expected: " + page + " found: " + anchorCount);
        }
    }

    Map.Entry<Integer, Map.Entry> getNearestAnchorEntry() {
        int anchorCount = this.anchorList.size();
        if (this.page == 0 || anchorCount == 0) {
            return NULL_ANCHOR;
        }
        Map.Entry<Integer, Map.Entry> anchoredEntry = this.page < anchorCount ? this.anchorList.get(this.page - 1) : this.anchorList.get(anchorCount - 1);
        return anchoredEntry;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeObject(this.predicate);
        out.writeObject(this.comparator);
        out.writeInt(this.page);
        out.writeInt(this.pageSize);
        out.writeUTF(this.iterationType.name());
        out.writeInt(this.anchorList.size());
        for (Map.Entry<Integer, Map.Entry<K, V>> anchor : this.anchorList) {
            out.writeInt(anchor.getKey());
            Map.Entry<K, V> anchorEntry = anchor.getValue();
            out.writeObject(anchorEntry.getKey());
            out.writeObject(anchorEntry.getValue());
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.predicate = (Predicate)in.readObject();
        this.comparator = (Comparator)in.readObject();
        this.page = in.readInt();
        this.pageSize = in.readInt();
        this.iterationType = IterationType.valueOf(in.readUTF());
        int size = in.readInt();
        this.anchorList = new ArrayList<Map.Entry<Integer, Map.Entry<K, V>>>(size);
        for (int i = 0; i < size; ++i) {
            int anchorPage = in.readInt();
            Object anchorKey = in.readObject();
            Object anchorValue = in.readObject();
            AbstractMap.SimpleImmutableEntry anchorEntry = new AbstractMap.SimpleImmutableEntry(anchorKey, anchorValue);
            this.anchorList.add(new AbstractMap.SimpleImmutableEntry(anchorPage, anchorEntry));
        }
    }

    @Override
    public int getFactoryId() {
        return -32;
    }

    @Override
    public int getId() {
        return 15;
    }
}

