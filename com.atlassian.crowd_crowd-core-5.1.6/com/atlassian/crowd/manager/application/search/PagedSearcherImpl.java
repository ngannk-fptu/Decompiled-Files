/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.SearchRestriction
 *  com.atlassian.crowd.manager.application.PagedSearcher
 *  com.atlassian.crowd.model.NameComparator
 *  com.atlassian.crowd.search.Entity
 *  com.atlassian.crowd.search.builder.Combine
 *  com.atlassian.crowd.search.builder.Restriction
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 *  com.atlassian.crowd.search.query.entity.restriction.Property
 *  com.atlassian.crowd.search.query.entity.restriction.PropertyRestriction
 *  com.atlassian.crowd.search.query.entity.restriction.constants.GroupTermKeys
 *  com.atlassian.crowd.search.query.entity.restriction.constants.UserTermKeys
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  org.apache.commons.lang3.tuple.Pair
 */
package com.atlassian.crowd.manager.application.search;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.SearchRestriction;
import com.atlassian.crowd.manager.application.PagedSearcher;
import com.atlassian.crowd.manager.application.search.DirectoryManagerSearchWrapper;
import com.atlassian.crowd.model.NameComparator;
import com.atlassian.crowd.search.Entity;
import com.atlassian.crowd.search.builder.Combine;
import com.atlassian.crowd.search.builder.Restriction;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import com.atlassian.crowd.search.query.entity.restriction.Property;
import com.atlassian.crowd.search.query.entity.restriction.PropertyRestriction;
import com.atlassian.crowd.search.query.entity.restriction.constants.GroupTermKeys;
import com.atlassian.crowd.search.query.entity.restriction.constants.UserTermKeys;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.function.Function;
import org.apache.commons.lang3.tuple.Pair;

public class PagedSearcherImpl<T>
implements PagedSearcher<T> {
    private final DirectoryManagerSearchWrapper directoryManagerSearchWrapper;
    private final EntityQuery<T> originalQuery;
    private final EntityQuery<T> baseQuery;
    private final PriorityQueue<DirectoryResults> queue = new PriorityQueue<DirectoryResults>(Comparator.comparing(d -> (String)d.results.getFirst().getLeft()).thenComparingInt(d -> d.dirIndex));
    private final List<DirectoryResults> pending = new ArrayList<DirectoryResults>();
    private final Function<T, String> idGetter;
    private String lastResult;
    private final boolean mergeEntities;
    private final Property<String> nameProperty;
    private int currentIndex = 0;
    private final int maxIndex;

    public PagedSearcherImpl(List<Directory> directories, DirectoryManagerSearchWrapper directoryManagerSearchWrapper, boolean mergeEntities, EntityQuery<T> query) {
        this.directoryManagerSearchWrapper = directoryManagerSearchWrapper;
        this.baseQuery = query.withAllResults();
        this.originalQuery = query;
        this.idGetter = NameComparator.normaliserOf((Class)query.getReturnType());
        this.mergeEntities = mergeEntities;
        for (int i = 0; i < directories.size(); ++i) {
            this.pending.add(new DirectoryResults(directories.get(i), i));
        }
        this.nameProperty = query.getEntityDescriptor().getEntityType() == Entity.USER ? UserTermKeys.USERNAME : GroupTermKeys.NAME;
        this.maxIndex = EntityQuery.addToMaxResults((int)query.getMaxResults(), (int)query.getStartIndex());
    }

    public static <T> PagedSearcher<T> emptySearcher() {
        return size -> ImmutableList.of();
    }

    public List<T> fetchNextBatch(int batchSize) {
        ArrayList results = new ArrayList();
        while (results.size() < batchSize && (this.maxIndex == -1 || this.currentIndex < this.maxIndex)) {
            this.queryPending(batchSize);
            DirectoryResults directoryResults = this.queue.poll();
            if (directoryResults == null) break;
            this.consumeResult(directoryResults, results);
        }
        return results;
    }

    private void consumeResult(DirectoryResults directoryResults, List<T> results) {
        Pair result = directoryResults.results.removeFirst();
        if (this.isCanonical((String)result.getLeft())) {
            if (this.currentIndex++ >= this.originalQuery.getStartIndex()) {
                results.add(result.getRight());
            }
            this.lastResult = (String)result.getLeft();
        }
        if (directoryResults.results.isEmpty()) {
            this.pending.add(directoryResults);
        } else {
            this.queue.add(directoryResults);
        }
    }

    boolean isCanonical(String id) {
        return !this.mergeEntities || this.lastResult == null || id.compareTo(this.lastResult) != 0;
    }

    private void queryPending(int batchSize) {
        for (DirectoryResults directoryResults : this.pending) {
            if (!directoryResults.fetch(batchSize)) continue;
            this.queue.add(directoryResults);
        }
        this.pending.clear();
    }

    private class DirectoryResults {
        final Directory directory;
        final int dirIndex;
        final LinkedList<Pair<String, T>> results = new LinkedList();
        boolean hasMore = true;
        String lastResult = null;

        public DirectoryResults(Directory directory, int dirIndex) {
            this.directory = directory;
            this.dirIndex = dirIndex;
        }

        boolean fetch(int batchSize) {
            Preconditions.checkState((boolean)this.results.isEmpty());
            if (this.hasMore) {
                EntityQuery query = this.createNextPageQuery(batchSize);
                for (Object result : PagedSearcherImpl.this.directoryManagerSearchWrapper.search(this.directory.getId(), query)) {
                    this.results.add(Pair.of(PagedSearcherImpl.this.idGetter.apply(result), result));
                }
                if (!this.results.isEmpty()) {
                    this.lastResult = (String)this.results.getLast().getLeft();
                }
                this.hasMore = this.results.size() >= batchSize;
            }
            return !this.results.isEmpty();
        }

        private EntityQuery<T> createNextPageQuery(int batchSize) {
            SearchRestriction restriction = PagedSearcherImpl.this.baseQuery.getSearchRestriction();
            if (this.lastResult != null) {
                PropertyRestriction nameRestriction = Restriction.on((Property)PagedSearcherImpl.this.nameProperty).greaterThan((Object)this.lastResult);
                restriction = Combine.optionalAllOf((SearchRestriction[])new SearchRestriction[]{restriction, nameRestriction});
            }
            return PagedSearcherImpl.this.baseQuery.withStartIndexAndMaxResults(0, batchSize).withSearchRestriction(restriction);
        }
    }
}

