/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.DirectoryProperties
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.manager.application.PagedSearcher
 *  com.atlassian.crowd.manager.application.PagingNotSupportedException
 *  com.atlassian.crowd.manager.directory.DirectoryManager
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.user.User
 *  com.atlassian.crowd.search.Entity
 *  com.atlassian.crowd.search.query.QueryUtils
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 *  com.google.common.base.Preconditions
 */
package com.atlassian.crowd.manager.application.search;

import com.atlassian.crowd.directory.DirectoryProperties;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.manager.application.PagedSearcher;
import com.atlassian.crowd.manager.application.PagingNotSupportedException;
import com.atlassian.crowd.manager.application.canonicality.CanonicalityChecker;
import com.atlassian.crowd.manager.application.canonicality.SimpleCanonicalityChecker;
import com.atlassian.crowd.manager.application.filtering.AccessFilter;
import com.atlassian.crowd.manager.application.search.DirectoryManagerSearchWrapper;
import com.atlassian.crowd.manager.application.search.GroupSearchStrategy;
import com.atlassian.crowd.manager.application.search.InMemoryQueryRunner;
import com.atlassian.crowd.manager.application.search.PagedSearcherImpl;
import com.atlassian.crowd.manager.application.search.UserSearchStrategy;
import com.atlassian.crowd.manager.directory.DirectoryManager;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.user.User;
import com.atlassian.crowd.search.Entity;
import com.atlassian.crowd.search.query.QueryUtils;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import com.google.common.base.Preconditions;
import java.util.List;

public class InMemoryEntitySearchStrategy
implements UserSearchStrategy,
GroupSearchStrategy {
    protected final DirectoryManagerSearchWrapper directoryManagerSearchWrapper;
    protected final List<Directory> directories;
    private final boolean mergeEntities;
    private final AccessFilter accessFilter;
    private final CanonicalityChecker canonicalityChecker;

    public InMemoryEntitySearchStrategy(DirectoryManager directoryManager, List<Directory> directories, boolean mergeEntities, AccessFilter accessFilter) {
        this.directoryManagerSearchWrapper = new DirectoryManagerSearchWrapper(directoryManager);
        this.directories = directories;
        this.mergeEntities = mergeEntities;
        this.accessFilter = accessFilter;
        this.canonicalityChecker = new SimpleCanonicalityChecker(directoryManager, directories);
    }

    @Override
    public <T> List<T> searchUsers(EntityQuery<T> query) {
        QueryUtils.checkAssignableFrom((Class)query.getReturnType(), (Class[])new Class[]{String.class, User.class});
        Preconditions.checkArgument((query.getEntityDescriptor().getEntityType() == Entity.USER ? 1 : 0) != 0);
        return this.search(query);
    }

    @Override
    public <T> List<T> searchGroups(EntityQuery<T> query) {
        QueryUtils.checkAssignableFrom((Class)query.getReturnType(), (Class[])new Class[]{String.class, Group.class});
        Preconditions.checkArgument((query.getEntityDescriptor().getEntityType() == Entity.GROUP ? 1 : 0) != 0);
        return this.search(query);
    }

    protected <T> List<T> search(EntityQuery<T> query) {
        return InMemoryQueryRunner.createEntityQueryRunner(this.directoryManagerSearchWrapper, this.directories, this.requiresCanonicalityFiltering(query) ? this.canonicalityChecker : null, this.mergeEntities, this.accessFilter::getDirectoryQueryWithFilter, query).search();
    }

    private boolean requiresCanonicalityFiltering(EntityQuery<?> query) {
        return this.mergeEntities && query.getEntityDescriptor().getEntityType() == Entity.USER && this.accessFilter.requiresFiltering(Entity.USER);
    }

    @Override
    public <T> PagedSearcher<T> createPagedUserSearcher(EntityQuery<T> query) throws PagingNotSupportedException {
        QueryUtils.checkAssignableFrom((Class)query.getReturnType(), (Class[])new Class[]{String.class, User.class});
        Preconditions.checkArgument((query.getEntityDescriptor().getEntityType() == Entity.USER ? 1 : 0) != 0);
        this.checkPaging(query);
        return new PagedSearcherImpl<T>(this.directories, this.directoryManagerSearchWrapper, this.mergeEntities, query);
    }

    @Override
    public <T> PagedSearcher<T> createPagedGroupSearcher(EntityQuery<T> query) throws PagingNotSupportedException {
        QueryUtils.checkAssignableFrom((Class)query.getReturnType(), (Class[])new Class[]{String.class, Group.class});
        Preconditions.checkArgument((query.getEntityDescriptor().getEntityType() == Entity.GROUP ? 1 : 0) != 0);
        this.checkPaging(query);
        return new PagedSearcherImpl<T>(this.directories, this.directoryManagerSearchWrapper, this.mergeEntities, query);
    }

    private void checkPaging(EntityQuery<?> query) throws PagingNotSupportedException {
        if (this.requiresCanonicalityFiltering(query)) {
            throw new PagingNotSupportedException("Paged queries are not supported when canonicality filtering is required");
        }
        if (this.accessFilter.requiresFiltering(query.getEntityDescriptor().getEntityType())) {
            throw new PagingNotSupportedException("Paged queries are not supported when access filtering is required");
        }
        if (!this.directories.stream().allMatch(DirectoryProperties::cachesAnyUsers)) {
            throw new PagingNotSupportedException("Paged queries are not supported for uncached directories");
        }
    }
}

