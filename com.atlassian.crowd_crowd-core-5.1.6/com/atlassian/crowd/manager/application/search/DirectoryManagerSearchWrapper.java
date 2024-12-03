/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.common.properties.SystemProperties
 *  com.atlassian.crowd.common.util.ProxyUtil
 *  com.atlassian.crowd.exception.DirectoryNotFoundException
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.exception.runtime.OperationFailedException
 *  com.atlassian.crowd.manager.directory.DirectoryManager
 *  com.atlassian.crowd.search.Entity
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 *  com.atlassian.crowd.search.query.membership.MembershipQuery
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableListMultimap
 *  com.google.common.collect.ListMultimap
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.manager.application.search;

import com.atlassian.crowd.common.properties.SystemProperties;
import com.atlassian.crowd.common.util.ProxyUtil;
import com.atlassian.crowd.exception.DirectoryNotFoundException;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.manager.directory.DirectoryManager;
import com.atlassian.crowd.search.Entity;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import com.atlassian.crowd.search.query.membership.MembershipQuery;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;
import java.util.ConcurrentModificationException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DirectoryManagerSearchWrapper {
    private static final Logger log = LoggerFactory.getLogger(DirectoryManagerSearchWrapper.class);
    private final DirectoryManager directoryManager;

    public DirectoryManagerSearchWrapper(DirectoryManager directoryManager) {
        this.directoryManager = (DirectoryManager)ProxyUtil.runWithContextClassLoader((ClassLoader)directoryManager.getClass().getClassLoader(), (Object)directoryManager);
    }

    public <T> List<T> search(long directoryId, EntityQuery<T> query) {
        Entity entityType = query.getEntityDescriptor().getEntityType();
        switch (entityType) {
            case USER: {
                return this.searchUsers(directoryId, query);
            }
            case GROUP: {
                return this.searchGroups(directoryId, query);
            }
        }
        throw new IllegalArgumentException("Can't query for " + entityType);
    }

    public <T> List<T> searchUsers(long directoryId, EntityQuery<T> query) {
        return (List)this.handle(() -> this.directoryManager.searchUsers(directoryId, query), ImmutableList.of());
    }

    public <T> List<T> searchGroups(long directoryId, EntityQuery<T> query) {
        return (List)this.handle(() -> this.directoryManager.searchGroups(directoryId, query), ImmutableList.of());
    }

    public <T> List<T> searchDirectGroupRelationships(long directoryId, MembershipQuery<T> query) {
        return (List)this.handle(() -> this.directoryManager.searchDirectGroupRelationships(directoryId, query), ImmutableList.of());
    }

    public <T> List<T> searchNestedGroupRelationships(long directoryId, MembershipQuery<T> query) {
        return (List)this.handle(() -> this.directoryManager.searchNestedGroupRelationships(directoryId, query), ImmutableList.of());
    }

    public <T> ListMultimap<String, T> searchDirectGroupRelationshipsGroupedByName(long directoryId, MembershipQuery<T> query) {
        return (ListMultimap)this.handle(() -> this.directoryManager.searchDirectGroupRelationshipsGroupedByName(directoryId, query), ImmutableListMultimap.of());
    }

    private <T> T handle(Operation<T> operation, T defaultValue) {
        try {
            return operation.execute();
        }
        catch (DirectoryNotFoundException e) {
            throw new ConcurrentModificationException("Directory mapping was removed while iterating through directories", e);
        }
        catch (OperationFailedException e) {
            if (!((Boolean)SystemProperties.SWALLOW_EXCEPTIONS_IN_DIRECTORY_SEARCH.getValue()).booleanValue()) {
                throw new com.atlassian.crowd.exception.runtime.OperationFailedException((Throwable)e);
            }
            log.error("Failed to search underlying directory", (Throwable)e);
            return defaultValue;
        }
    }

    private static interface Operation<T> {
        public T execute() throws OperationFailedException, DirectoryNotFoundException;
    }
}

