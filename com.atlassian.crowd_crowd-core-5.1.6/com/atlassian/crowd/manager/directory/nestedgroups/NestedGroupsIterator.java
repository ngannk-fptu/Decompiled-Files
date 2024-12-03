/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.model.DirectoryEntity
 *  com.atlassian.crowd.model.group.Group
 *  com.google.common.collect.Collections2
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.crowd.manager.directory.nestedgroups;

import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.manager.directory.nestedgroups.NestedGroupsProvider;
import com.atlassian.crowd.model.DirectoryEntity;
import com.atlassian.crowd.model.group.Group;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.function.Function;

public class NestedGroupsIterator<T> {
    private final Queue<String> groupsToVisit = new LinkedList<String>();
    private final Set<String> processed = new HashSet<String>();
    private final Queue<T> toReturn;
    private final NestedGroupsProvider provider;
    private final Function<Group, T> toReturnTransformer;

    private NestedGroupsIterator(Collection<String> toVisit, Collection<? extends T> initialToReturn, NestedGroupsProvider provider, Function<Group, T> toReturnTransformer) {
        this.groupsToVisit.addAll(toVisit);
        this.processed.addAll(Collections2.transform(toVisit, provider::normalizeIdentifier));
        this.toReturn = new LinkedList<T>(initialToReturn);
        this.provider = provider;
        this.toReturnTransformer = toReturnTransformer;
    }

    private void fetchRelatedGroups(List<String> groupNames) throws OperationFailedException {
        for (Group member : this.provider.getDirectlyRelatedGroups(groupNames)) {
            String name = this.provider.getIdentifierForSubGroupsQuery(member);
            if (!this.processed.add(this.provider.normalizeIdentifier(name))) continue;
            this.groupsToVisit.add(name);
            this.toReturn.add(this.toReturnTransformer.apply(member));
        }
    }

    public boolean hasNext() throws OperationFailedException {
        this.fetchNextIfNeeded();
        return !this.toReturn.isEmpty();
    }

    public T next() throws OperationFailedException {
        this.fetchNextIfNeeded();
        return this.toReturn.remove();
    }

    private void fetchNextIfNeeded() throws OperationFailedException {
        while (this.toReturn.isEmpty() && !this.groupsToVisit.isEmpty()) {
            ArrayList<String> batch = new ArrayList<String>();
            while (batch.size() < this.provider.getMaxBatchSize() && !this.groupsToVisit.isEmpty()) {
                batch.add(this.groupsToVisit.remove());
            }
            this.fetchRelatedGroups(batch);
        }
    }

    public List<T> toList() throws OperationFailedException {
        return this.nextBatch(Integer.MAX_VALUE);
    }

    public List<T> nextBatch(int maxBatchSize) throws OperationFailedException {
        ArrayList<T> batch = new ArrayList<T>();
        while (batch.size() < maxBatchSize && this.hasNext()) {
            batch.add(this.next());
        }
        return batch;
    }

    public boolean anyMatch(PredicateWithException<T> consumer) throws OperationFailedException {
        while (this.hasNext()) {
            if (!consumer.test(this.next())) continue;
            return true;
        }
        return false;
    }

    public void visitAll() throws OperationFailedException {
        while (this.hasNext()) {
            this.next();
        }
    }

    public static NestedGroupsIterator<Group> groupsIterator(Collection<? extends Group> groups, boolean includeOriginal, NestedGroupsProvider provider) {
        return new NestedGroupsIterator<Group>(Collections2.transform(groups, DirectoryEntity::getName), (Collection<Group>)(includeOriginal ? groups : ImmutableList.of()), provider, Function.identity());
    }

    public static NestedGroupsIterator<String> namesIterator(Collection<String> groups, boolean includeOriginal, NestedGroupsProvider provider) {
        return new NestedGroupsIterator<String>((Collection<String>)groups, (Collection<String>)(includeOriginal ? groups : ImmutableList.of()), provider, DirectoryEntity::getName);
    }

    public static NestedGroupsIterator<Group> groupsIterator(Collection<String> groups, NestedGroupsProvider provider) {
        return new NestedGroupsIterator<Group>(groups, (Collection<Group>)ImmutableList.of(), provider, Function.identity());
    }

    public static interface PredicateWithException<T> {
        public boolean test(T var1) throws OperationFailedException;
    }
}

