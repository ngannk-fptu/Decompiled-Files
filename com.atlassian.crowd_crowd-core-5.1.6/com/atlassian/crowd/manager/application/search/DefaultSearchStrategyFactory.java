/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.manager.directory.DirectoryManager
 */
package com.atlassian.crowd.manager.application.search;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.manager.application.canonicality.CanonicalityChecker;
import com.atlassian.crowd.manager.application.filtering.AccessFilter;
import com.atlassian.crowd.manager.application.search.GroupSearchStrategy;
import com.atlassian.crowd.manager.application.search.InMemoryAggregatingMembershipSearchStrategy;
import com.atlassian.crowd.manager.application.search.InMemoryEntitySearchStrategy;
import com.atlassian.crowd.manager.application.search.InMemoryNonAggregatingMembershipSearchStrategy;
import com.atlassian.crowd.manager.application.search.MembershipSearchStrategy;
import com.atlassian.crowd.manager.application.search.NoDirectorySearchStrategy;
import com.atlassian.crowd.manager.application.search.SearchStrategyFactory;
import com.atlassian.crowd.manager.application.search.UserSearchStrategy;
import com.atlassian.crowd.manager.directory.DirectoryManager;
import java.util.List;
import java.util.Objects;

public class DefaultSearchStrategyFactory
implements SearchStrategyFactory {
    private final DirectoryManager directoryManager;

    public DefaultSearchStrategyFactory(DirectoryManager directoryManager) {
        this.directoryManager = Objects.requireNonNull(directoryManager, "directoryManager");
    }

    @Override
    public MembershipSearchStrategy createMembershipSearchStrategy(boolean aggregateMemberships, List<Directory> directories, CanonicalityChecker canonicalityChecker, AccessFilter filter) {
        if (directories.isEmpty()) {
            return NoDirectorySearchStrategy.INSTANCE;
        }
        return aggregateMemberships ? new InMemoryAggregatingMembershipSearchStrategy(this.directoryManager, directories, filter) : new InMemoryNonAggregatingMembershipSearchStrategy(this.directoryManager, directories, canonicalityChecker, filter);
    }

    @Override
    public UserSearchStrategy createUserSearchStrategy(boolean mergeUsers, List<Directory> directories, AccessFilter filter) {
        if (directories.isEmpty()) {
            return NoDirectorySearchStrategy.INSTANCE;
        }
        return new InMemoryEntitySearchStrategy(this.directoryManager, directories, mergeUsers, filter);
    }

    @Override
    public GroupSearchStrategy createGroupSearchStrategy(boolean mergeGroups, List<Directory> directories, AccessFilter filter) {
        if (directories.isEmpty()) {
            return NoDirectorySearchStrategy.INSTANCE;
        }
        return new InMemoryEntitySearchStrategy(this.directoryManager, directories, mergeGroups, filter);
    }
}

