/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.Query
 *  com.atlassian.crowd.embedded.impl.IdentifierMap
 *  com.atlassian.crowd.model.NameComparator
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.user.User
 *  com.atlassian.crowd.search.EntityDescriptor
 *  com.atlassian.crowd.search.query.QueryUtils
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 *  com.atlassian.crowd.search.query.membership.MembershipQuery
 *  com.atlassian.crowd.search.util.ResultsAggregator
 *  com.atlassian.crowd.search.util.ResultsAggregators
 *  com.atlassian.crowd.search.util.SearchResultsUtil
 *  com.google.common.base.Function
 *  com.google.common.collect.ArrayListMultimap
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.LinkedListMultimap
 *  com.google.common.collect.ListMultimap
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Multimap
 *  com.google.common.collect.Multimaps
 *  org.apache.commons.lang3.tuple.Pair
 */
package com.atlassian.crowd.manager.application.search;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.Query;
import com.atlassian.crowd.embedded.impl.IdentifierMap;
import com.atlassian.crowd.manager.application.canonicality.CanonicalityChecker;
import com.atlassian.crowd.manager.application.search.DirectoryManagerSearchWrapper;
import com.atlassian.crowd.manager.application.search.DirectoryQueryWithFilter;
import com.atlassian.crowd.model.NameComparator;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.user.User;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.query.QueryUtils;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import com.atlassian.crowd.search.query.membership.MembershipQuery;
import com.atlassian.crowd.search.util.ResultsAggregator;
import com.atlassian.crowd.search.util.ResultsAggregators;
import com.atlassian.crowd.search.util.SearchResultsUtil;
import com.google.common.base.Function;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;

class InMemoryQueryRunner<T, Q extends Query<T>> {
    private static final int MIN_OPTIMISTIC_QUERY_MARGIN = 10;
    private final DirectoryManagerSearchWrapper directoryManagerSearchWrapper;
    private final List<Directory> directories;
    private final EntityDescriptor entityDescriptor;
    private final CanonicalityChecker canonicalityChecker;
    private final boolean mergeEntities;
    private final boolean nested;
    private final Q query;
    private final Q splitQuery;
    private final Q optimisticSplitQuery;
    private final Q withAllResults;
    private final Directory firstDir;
    private final BiFunction<Directory, Q, Optional<DirectoryQueryWithFilter<T>>> queryProvider;

    private InMemoryQueryRunner(DirectoryManagerSearchWrapper directoryManagerSearchWrapper, List<Directory> directories, CanonicalityChecker canonicalityChecker, boolean mergeEntities, boolean nested, BiFunction<Directory, Q, Optional<DirectoryQueryWithFilter<T>>> queryProvider, EntityDescriptor entityDescriptor, Q query, Q splitQuery, Q optimisticSplitQuery, Q withAllResults) {
        this.directoryManagerSearchWrapper = directoryManagerSearchWrapper;
        this.directories = directories;
        this.canonicalityChecker = canonicalityChecker;
        this.mergeEntities = mergeEntities;
        this.nested = nested;
        this.firstDir = directories.get(0);
        this.queryProvider = queryProvider;
        this.entityDescriptor = entityDescriptor;
        this.query = query;
        this.splitQuery = splitQuery;
        this.optimisticSplitQuery = optimisticSplitQuery;
        this.withAllResults = withAllResults;
    }

    public List<T> search() {
        QueryUtils.checkAssignableFrom((Class)this.query.getReturnType(), (Class[])new Class[]{String.class, Group.class, User.class});
        if (this.canonicalityChecker == null) {
            List<DirectoryQueryWithFilter<T>> queries = this.prepareValidQueries(true);
            return this.merge(this::execute, queries);
        }
        return this.searchWithCanonicalityCheck();
    }

    private List<T> searchWithCanonicalityCheck() {
        LinkedListMultimap resultsByDirectory = LinkedListMultimap.create();
        ArrayList<Pair<DirectoryQueryWithFilter<T>, T>> rerunCandidates = new ArrayList<Pair<DirectoryQueryWithFilter<T>, T>>();
        List<DirectoryQueryWithFilter<T>> queries = this.prepareValidQueries(true);
        for (DirectoryQueryWithFilter<T> validQuery : queries) {
            List<T> dirResults = this.execute(validQuery, rerunCandidates);
            resultsByDirectory.putAll((Object)validQuery.getDirectory().getId(), dirResults);
        }
        List<T> result = this.removeNonCanonicalEntitiesIfNeeded((ListMultimap<Long, T>)resultsByDirectory, queries);
        boolean rerun = this.rerunIfNeeded((List<Pair<DirectoryQueryWithFilter<T>, T>>)rerunCandidates, (ListMultimap<Long, T>)resultsByDirectory, result);
        return rerun ? this.removeNonCanonicalEntitiesIfNeeded((ListMultimap<Long, T>)resultsByDirectory, queries) : result;
    }

    private List<T> execute(DirectoryQueryWithFilter<T> validQuery) {
        return validQuery.filterResults(this.executeUnfiltered(validQuery));
    }

    private List<T> execute(DirectoryQueryWithFilter<T> validQuery, List<Pair<DirectoryQueryWithFilter<T>, T>> rerunCandidates) {
        List<T> unfiltered = this.executeUnfiltered(validQuery);
        if (!validQuery.getDirectory().getId().equals(this.firstDir.getId()) && this.query.getMaxResults() != -1 && validQuery.getQuery().getMaxResults() != -1 && unfiltered.size() >= validQuery.getQuery().getMaxResults()) {
            rerunCandidates.add(Pair.of(validQuery, unfiltered.get(unfiltered.size() - 1)));
        }
        return validQuery.filterResults(unfiltered);
    }

    private boolean rerunIfNeeded(List<Pair<DirectoryQueryWithFilter<T>, T>> rerunCandidates, ListMultimap<Long, T> resultsByDirectory, List<T> result) {
        if (rerunCandidates.isEmpty()) {
            return false;
        }
        Object last = result.size() < this.query.getMaxResults() ? null : (Object)result.get(result.size() - 1);
        Comparator cmp = NameComparator.of((Class)this.query.getReturnType());
        boolean rerun = false;
        for (Pair<DirectoryQueryWithFilter<T>, T> candidate : rerunCandidates) {
            Long directoryId = ((DirectoryQueryWithFilter)candidate.getLeft()).getDirectory().getId();
            if (resultsByDirectory.get((Object)directoryId).size() >= this.splitQuery.getMaxResults() || last != null && cmp.compare(candidate.getRight(), last) >= 0) continue;
            resultsByDirectory.replaceValues((Object)directoryId, this.execute(((DirectoryQueryWithFilter)candidate.getLeft()).getDirectory(), this.withAllResults));
            rerun = true;
        }
        return rerun;
    }

    private List<T> execute(Directory directory, Q query) {
        return this.queryProvider.apply(directory, query).map(dirQuery -> dirQuery.filterResults(this.executeUnfiltered((DirectoryQueryWithFilter<T>)dirQuery))).orElse((List)ImmutableList.of());
    }

    private List<T> executeUnfiltered(DirectoryQueryWithFilter<T> directoryQueryWithFilter) {
        Long directoryId = directoryQueryWithFilter.getDirectory().getId();
        if (directoryQueryWithFilter.getQuery() instanceof MembershipQuery) {
            return this.nested ? this.directoryManagerSearchWrapper.searchNestedGroupRelationships(directoryId, directoryQueryWithFilter.getMembershipQuery()) : this.directoryManagerSearchWrapper.searchDirectGroupRelationships(directoryId, directoryQueryWithFilter.getMembershipQuery());
        }
        return this.directoryManagerSearchWrapper.search(directoryId, (EntityQuery)directoryQueryWithFilter.getQuery());
    }

    private List<T> removeNonCanonicalEntitiesIfNeeded(ListMultimap<Long, T> results, List<DirectoryQueryWithFilter<T>> queries) {
        if (this.canonicalityChecker != null) {
            this.canonicalityChecker.removeNonCanonicalEntities((Multimap<Long, String>)Multimaps.transformValues(results, (Function)NameComparator.nameGetter((Class)this.query.getReturnType())), this.entityDescriptor);
        }
        return this.merge(helper -> results.get((Object)helper.getDirectory().getId()), queries);
    }

    private List<T> merge(java.util.function.Function<DirectoryQueryWithFilter<T>, List<T>> provider, List<DirectoryQueryWithFilter<T>> queries) {
        Query<T> effectiveQuery;
        if (this.directories.size() == 1 && queries.size() == 1 && (effectiveQuery = queries.get(0).getQuery()).getStartIndex() == this.query.getStartIndex()) {
            return SearchResultsUtil.constrainResults(provider.apply(queries.get(0)), (int)0, (int)this.query.getMaxResults());
        }
        ResultsAggregator aggregator = ResultsAggregators.with(this.query, (boolean)this.mergeEntities);
        queries.forEach(directoryQuery -> aggregator.addAll((Iterable)provider.apply((DirectoryQueryWithFilter)directoryQuery)));
        return aggregator.constrainResults();
    }

    public ListMultimap<String, T> searchGroupedByName() {
        QueryUtils.checkAssignableFrom((Class)this.query.getReturnType(), (Class[])new Class[]{String.class, Group.class, User.class});
        MembershipQuery membershipQuery = (MembershipQuery)this.query;
        IdentifierMap perGroup = new IdentifierMap((Map)Maps.toMap((Iterable)membershipQuery.getEntityNamesToMatch(), name -> LinkedListMultimap.create()));
        List<DirectoryQueryWithFilter<T>> queries = this.prepareValidQueries(false);
        for (DirectoryQueryWithFilter<T> validQuery : queries) {
            Long directoryId = validQuery.getDirectory().getId();
            ListMultimap<String, T> unfiltered = this.directoryManagerSearchWrapper.searchDirectGroupRelationshipsGroupedByName(directoryId, validQuery.getMembershipQuery());
            for (String name2 : unfiltered.keySet()) {
                List<T> filtered = validQuery.filterResults(unfiltered.get((Object)name2));
                ((ListMultimap)perGroup.get((Object)name2)).putAll((Object)directoryId, filtered);
            }
        }
        ArrayListMultimap result = ArrayListMultimap.create();
        for (String name3 : membershipQuery.getEntityNamesToMatch()) {
            result.putAll((Object)name3, this.removeNonCanonicalEntitiesIfNeeded((ListMultimap)perGroup.get((Object)name3), queries));
        }
        return result;
    }

    private List<DirectoryQueryWithFilter<T>> prepareValidQueries(boolean allowReRun) {
        Q furtherDirectories = this.canonicalityChecker == null ? this.splitQuery : (allowReRun && !this.nested ? this.optimisticSplitQuery : this.withAllResults);
        ArrayList queries = new ArrayList(this.directories.size());
        this.queryProvider.apply(this.firstDir, this.directories.size() == 1 ? this.query : this.splitQuery).ifPresent(queries::add);
        for (Directory directory : this.directories.subList(1, this.directories.size())) {
            this.queryProvider.apply(directory, furtherDirectories).ifPresent(queries::add);
        }
        return queries.stream().filter(this::isValid).collect(Collectors.toList());
    }

    private boolean isValid(DirectoryQueryWithFilter<T> directoryQueryWithFilter) {
        if (directoryQueryWithFilter.getQuery() instanceof MembershipQuery) {
            return !directoryQueryWithFilter.getMembershipQuery().getEntityNamesToMatch().isEmpty();
        }
        return true;
    }

    static <T> InMemoryQueryRunner<T, EntityQuery<T>> createEntityQueryRunner(DirectoryManagerSearchWrapper directoryManagerSearchWrapper, List<Directory> directories, CanonicalityChecker canonicalityChecker, boolean mergeEntities, BiFunction<Directory, EntityQuery<T>, Optional<DirectoryQueryWithFilter<T>>> queryProvider, EntityQuery<T> query) {
        EntityQuery splitQuery = query.baseSplitQuery();
        EntityQuery optimisticSplitQuery = splitQuery.addToMaxResults(Math.max(splitQuery.getMaxResults(), 10));
        return new InMemoryQueryRunner<T, EntityQuery>(directoryManagerSearchWrapper, directories, canonicalityChecker, mergeEntities, false, queryProvider, query.getEntityDescriptor(), query, splitQuery, optimisticSplitQuery, query.withAllResults());
    }

    static <T> InMemoryQueryRunner<T, MembershipQuery<T>> createMembershipQueryRunner(DirectoryManagerSearchWrapper directoryManagerSearchWrapper, List<Directory> directories, CanonicalityChecker canonicalityChecker, boolean mergeEntities, boolean nested, BiFunction<Directory, MembershipQuery<T>, Optional<DirectoryQueryWithFilter<T>>> queryProvider, MembershipQuery<T> query) {
        MembershipQuery splitQuery = query.baseSplitQuery();
        MembershipQuery optimisticSplitQuery = splitQuery.addToMaxResults(Math.max(splitQuery.getMaxResults(), 10));
        return new InMemoryQueryRunner<T, MembershipQuery>(directoryManagerSearchWrapper, directories, canonicalityChecker, mergeEntities, nested, queryProvider, query.getEntityToReturn(), query, splitQuery, optimisticSplitQuery, query.withAllResults());
    }
}

