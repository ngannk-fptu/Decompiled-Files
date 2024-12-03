/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.manager.directory.DirectoryManager
 *  com.atlassian.crowd.search.query.membership.MembershipQuery
 */
package com.atlassian.crowd.manager.application.search;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.manager.application.canonicality.CanonicalityChecker;
import com.atlassian.crowd.manager.application.filtering.AccessFilter;
import com.atlassian.crowd.manager.application.search.AbstractInMemoryMembershipSearchStrategy;
import com.atlassian.crowd.manager.directory.DirectoryManager;
import com.atlassian.crowd.search.query.membership.MembershipQuery;
import java.util.List;
import java.util.function.BiFunction;

public class InMemoryAggregatingMembershipSearchStrategy
extends AbstractInMemoryMembershipSearchStrategy {
    public InMemoryAggregatingMembershipSearchStrategy(DirectoryManager directoryManager, List<Directory> directories, AccessFilter accessFilter) {
        super(directoryManager, directories, accessFilter);
    }

    @Override
    protected CanonicalityChecker getCanonicalityCheckerIfNeeded(MembershipQuery<?> query) {
        return null;
    }

    @Override
    protected <T> BiFunction<Directory, MembershipQuery<T>, MembershipQuery<T>> getQueryTransformer(MembershipQuery<T> original) {
        return (directoryId, query) -> query;
    }
}

