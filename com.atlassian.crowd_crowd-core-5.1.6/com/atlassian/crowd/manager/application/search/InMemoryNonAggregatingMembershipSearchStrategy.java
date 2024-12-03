/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.impl.IdentifierUtils
 *  com.atlassian.crowd.manager.directory.DirectoryManager
 *  com.atlassian.crowd.search.query.membership.MembershipQuery
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.SetMultimap
 */
package com.atlassian.crowd.manager.application.search;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.impl.IdentifierUtils;
import com.atlassian.crowd.manager.application.canonicality.CanonicalityChecker;
import com.atlassian.crowd.manager.application.canonicality.SimpleCanonicalityChecker;
import com.atlassian.crowd.manager.application.filtering.AccessFilter;
import com.atlassian.crowd.manager.application.search.AbstractInMemoryMembershipSearchStrategy;
import com.atlassian.crowd.manager.directory.DirectoryManager;
import com.atlassian.crowd.search.query.membership.MembershipQuery;
import com.google.common.base.Preconditions;
import com.google.common.collect.SetMultimap;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class InMemoryNonAggregatingMembershipSearchStrategy
extends AbstractInMemoryMembershipSearchStrategy {
    private final CanonicalityChecker canonicalityChecker;

    public InMemoryNonAggregatingMembershipSearchStrategy(DirectoryManager directoryManager, List<Directory> directories, CanonicalityChecker canonicalityChecker, AccessFilter accessFilter) {
        super(directoryManager, directories, accessFilter);
        this.canonicalityChecker = canonicalityChecker == null ? new SimpleCanonicalityChecker(directoryManager, directories) : canonicalityChecker;
        Preconditions.checkArgument((canonicalityChecker == null || canonicalityChecker.getDirectories().equals(this.directories) ? 1 : 0) != 0);
    }

    @Override
    protected CanonicalityChecker getCanonicalityCheckerIfNeeded(MembershipQuery<?> query) {
        return this.directories.size() > 1 && query.isFindChildren() ? this.canonicalityChecker : null;
    }

    @Override
    protected <T> BiFunction<Directory, MembershipQuery<T>, MembershipQuery<T>> getQueryTransformer(MembershipQuery<T> original) {
        return this.directories.size() == 1 || original.isFindChildren() ? (directory, query) -> query : this.filterByCanonical(original);
    }

    private <T> BiFunction<Directory, MembershipQuery<T>, MembershipQuery<T>> filterByCanonical(MembershipQuery<T> original) {
        SetMultimap<Long, String> canonical = this.canonicalityChecker.groupByCanonicalId(original.getEntityNamesToMatch(), original.getEntityToMatch());
        return (directory, query) -> this.filterEntityNamesToMatch((MembershipQuery)query, canonical.get((Object)directory.getId()));
    }

    private <T> MembershipQuery<T> filterEntityNamesToMatch(MembershipQuery<T> original, Set<String> filter) {
        List filteredNames = original.getEntityNamesToMatch().stream().filter(IdentifierUtils.containsIdentifierPredicate(filter)).collect(Collectors.toList());
        return original.withEntityNames(filteredNames);
    }
}

