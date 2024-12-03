/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.impl.IdentifierSet
 *  com.atlassian.crowd.manager.directory.DirectoryManager
 *  com.atlassian.crowd.search.Entity
 *  com.atlassian.crowd.search.query.membership.MembershipQuery
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ListMultimap
 *  com.google.common.collect.Lists
 */
package com.atlassian.crowd.manager.application.search;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.impl.IdentifierSet;
import com.atlassian.crowd.manager.application.canonicality.CanonicalityChecker;
import com.atlassian.crowd.manager.application.filtering.AccessFilter;
import com.atlassian.crowd.manager.application.search.DirectoryManagerSearchWrapper;
import com.atlassian.crowd.manager.application.search.DirectoryQueryWithFilter;
import com.atlassian.crowd.manager.application.search.InMemoryQueryRunner;
import com.atlassian.crowd.manager.application.search.MembershipSearchStrategy;
import com.atlassian.crowd.manager.application.search.NamesUtil;
import com.atlassian.crowd.manager.directory.DirectoryManager;
import com.atlassian.crowd.search.Entity;
import com.atlassian.crowd.search.query.membership.MembershipQuery;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;

public abstract class AbstractInMemoryMembershipSearchStrategy
implements MembershipSearchStrategy {
    protected final DirectoryManagerSearchWrapper directoryManagerSearchWrapper;
    protected final List<Directory> directories;
    protected final List<Long> directoryIds;
    protected final AccessFilter accessFilter;

    public AbstractInMemoryMembershipSearchStrategy(DirectoryManager directoryManager, List<Directory> directories, AccessFilter accessFilter) {
        this.directoryManagerSearchWrapper = new DirectoryManagerSearchWrapper(directoryManager);
        this.directories = ImmutableList.copyOf(directories);
        this.directoryIds = ImmutableList.copyOf((Collection)Lists.transform(directories, Directory::getId));
        this.accessFilter = accessFilter;
    }

    @Override
    public <T> List<T> searchDirectGroupRelationships(MembershipQuery<T> query) {
        return this.searchGroupRelationships(query, false);
    }

    @Override
    public <T> List<T> searchNestedGroupRelationships(MembershipQuery<T> query) {
        return this.searchGroupRelationships(query, true);
    }

    protected <T> List<T> searchGroupRelationships(MembershipQuery<T> query, boolean nested) {
        return this.createSearcher(query, nested).search();
    }

    @Override
    public <T> ListMultimap<String, T> searchDirectGroupRelationshipsGroupedByName(MembershipQuery<T> query) {
        return this.createSearcher(query, false).searchGroupedByName();
    }

    private <T> InMemoryQueryRunner<T, MembershipQuery<T>> createSearcher(MembershipQuery<T> original, boolean nested) {
        BiFunction transformer = this.getQueryTransformer(original);
        return InMemoryQueryRunner.createMembershipQueryRunner(this.directoryManagerSearchWrapper, this.directories, this.getCanonicalityCheckerIfNeeded(original), true, nested, (directory, query) -> this.accessFilter.getDirectoryQueryWithFilter((Directory)directory, (MembershipQuery)transformer.apply((Directory)directory, (MembershipQuery)query)).map(q -> this.filterOriginalResultsIfNeeded(original, nested, (DirectoryQueryWithFilter)q)), original);
    }

    private <T> DirectoryQueryWithFilter<T> filterOriginalResultsIfNeeded(MembershipQuery<T> query, boolean nested, DirectoryQueryWithFilter<T> directoryQueryWithFilter) {
        if (nested && query.getEntityToReturn().getEntityType() == Entity.GROUP && query.getEntityToMatch().getEntityType() == Entity.GROUP) {
            MembershipQuery<T> transformed = directoryQueryWithFilter.getMembershipQuery();
            IdentifierSet diff = IdentifierSet.difference((Collection)query.getEntityNamesToMatch(), (Collection)transformed.getEntityNamesToMatch());
            if (!diff.isEmpty()) {
                return new DirectoryQueryWithFilter(directoryQueryWithFilter.getDirectory(), transformed.baseSplitQuery().addToMaxResults(diff.size()), results -> NamesUtil.filterOutByName(directoryQueryWithFilter.filterResults((List)results), arg_0 -> ((IdentifierSet)diff).contains(arg_0)));
            }
        }
        return directoryQueryWithFilter;
    }

    protected abstract CanonicalityChecker getCanonicalityCheckerIfNeeded(MembershipQuery<?> var1);

    protected abstract <T> BiFunction<Directory, MembershipQuery<T>, MembershipQuery<T>> getQueryTransformer(MembershipQuery<T> var1);
}

