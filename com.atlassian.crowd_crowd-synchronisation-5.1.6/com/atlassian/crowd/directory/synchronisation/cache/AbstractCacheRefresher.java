/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.attribute.AttributePredicates
 *  com.atlassian.crowd.directory.RemoteDirectory
 *  com.atlassian.crowd.embedded.api.Attributes
 *  com.atlassian.crowd.embedded.impl.IdentifierMap
 *  com.atlassian.crowd.exception.GroupNotFoundException
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.exception.UserNotFoundException
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.group.GroupWithAttributes
 *  com.atlassian.crowd.model.group.Membership
 *  com.atlassian.crowd.model.user.UserWithAttributes
 *  com.atlassian.crowd.util.TimedOperation
 *  com.atlassian.crowd.util.TimedProgressOperation
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.directory.synchronisation.cache;

import com.atlassian.crowd.attribute.AttributePredicates;
import com.atlassian.crowd.directory.RemoteDirectory;
import com.atlassian.crowd.directory.synchronisation.CacheSynchronisationResult;
import com.atlassian.crowd.directory.synchronisation.PartialSynchronisationResult;
import com.atlassian.crowd.directory.synchronisation.cache.CacheRefresher;
import com.atlassian.crowd.directory.synchronisation.cache.DirectoryCache;
import com.atlassian.crowd.embedded.api.Attributes;
import com.atlassian.crowd.embedded.impl.IdentifierMap;
import com.atlassian.crowd.exception.GroupNotFoundException;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.exception.UserNotFoundException;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.GroupWithAttributes;
import com.atlassian.crowd.model.group.Membership;
import com.atlassian.crowd.model.user.UserWithAttributes;
import com.atlassian.crowd.util.TimedOperation;
import com.atlassian.crowd.util.TimedProgressOperation;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractCacheRefresher<G extends GroupWithAttributes>
implements CacheRefresher {
    private static final Logger log = LoggerFactory.getLogger(AbstractCacheRefresher.class);
    protected final RemoteDirectory remoteDirectory;

    public AbstractCacheRefresher(RemoteDirectory remoteDirectory) {
        this.remoteDirectory = remoteDirectory;
    }

    @Override
    public CacheSynchronisationResult synchroniseAll(DirectoryCache directoryCache) throws OperationFailedException {
        PartialSynchronisationResult<UserWithAttributes> allUsers = this.synchroniseAllUsers(directoryCache);
        if (this.isUserAttributeSynchronisationEnabled()) {
            this.synchroniseAllUserAttributes(allUsers.getResults(), directoryCache);
        }
        PartialSynchronisationResult<G> allGroups = this.synchroniseAllGroups(directoryCache);
        if (this.isGroupAttributeSynchronisationEnabled()) {
            this.synchroniseAllGroupAttributes(allGroups.getResults(), directoryCache);
        }
        this.synchroniseMemberships(allGroups.getResults(), directoryCache, true);
        return new CacheSynchronisationResult(true, null);
    }

    protected abstract PartialSynchronisationResult<? extends UserWithAttributes> synchroniseAllUsers(DirectoryCache var1) throws OperationFailedException;

    protected abstract PartialSynchronisationResult<G> synchroniseAllGroups(DirectoryCache var1) throws OperationFailedException;

    protected Iterable<Membership> getMemberships(Collection<G> groups, boolean isFullSync) throws OperationFailedException {
        return this.remoteDirectory.getMemberships();
    }

    protected boolean isUserAttributeSynchronisationEnabled() {
        return Boolean.parseBoolean(this.remoteDirectory.getValue("userAttributesSyncEnabled"));
    }

    protected boolean isGroupAttributeSynchronisationEnabled() {
        return Boolean.parseBoolean(this.remoteDirectory.getValue("groupAttributesSyncEnabled"));
    }

    protected void synchroniseAllUserAttributes(Collection<? extends UserWithAttributes> remoteUsers, DirectoryCache directoryCache) throws OperationFailedException {
        TimedOperation userAttributeSyncOperation = new TimedOperation();
        int failureCount = 0;
        for (UserWithAttributes userWithAttributes : remoteUsers) {
            try {
                UserWithAttributes internalUserWithAttributes = directoryCache.findUserWithAttributesByName(userWithAttributes.getName());
                Set<String> attributesToDelete = AbstractCacheRefresher.getAttributesToDelete((Attributes)userWithAttributes, (Attributes)internalUserWithAttributes);
                Map<String, Set<String>> attributesToStore = AbstractCacheRefresher.getAttributesToStore((Attributes)userWithAttributes, (Attributes)internalUserWithAttributes);
                directoryCache.applySyncingUserAttributes(userWithAttributes.getName(), attributesToDelete, attributesToStore);
            }
            catch (UserNotFoundException e) {
                ++failureCount;
                log.debug("Could not synchronize user attributes for user '{}'. User was not found in the cache.", (Object)userWithAttributes.getName());
            }
        }
        log.info(userAttributeSyncOperation.complete("finished user attribute sync with " + failureCount + " failures"));
    }

    protected void synchroniseAllGroupAttributes(Collection<G> remoteGroups, DirectoryCache directoryCache) throws OperationFailedException {
        TimedOperation groupAttributeSyncOperation = new TimedOperation();
        int failureCount = 0;
        for (GroupWithAttributes remoteGroup : remoteGroups) {
            try {
                GroupWithAttributes internalGroupWithAttributes = directoryCache.findGroupWithAttributesByName(remoteGroup.getName());
                Set<String> attributesToDelete = AbstractCacheRefresher.getAttributesToDelete((Attributes)remoteGroup, (Attributes)internalGroupWithAttributes);
                Map<String, Set<String>> attributesToStore = AbstractCacheRefresher.getAttributesToStore((Attributes)remoteGroup, (Attributes)internalGroupWithAttributes);
                directoryCache.applySyncingGroupAttributes(remoteGroup.getName(), attributesToDelete, attributesToStore);
            }
            catch (GroupNotFoundException e) {
                ++failureCount;
                log.debug("Could not synchronize group attributes for group '{}'. Group was not found in the cache.", (Object)remoteGroup.getName());
            }
        }
        log.info(groupAttributeSyncOperation.complete("finished group attribute sync with " + failureCount + " failures"));
    }

    static Set<String> getAttributesToDelete(Attributes newAttributes, Attributes oldAttributes) {
        Set newAttributeNames = newAttributes.getKeys();
        return oldAttributes.getKeys().stream().filter(AttributePredicates.SYNCING_ATTRIBUTE).filter(attrName -> !newAttributeNames.contains(attrName)).collect(Collectors.toSet());
    }

    static Map<String, Set<String>> getAttributesToStore(Attributes newAttributes, Attributes oldAttributes) {
        return newAttributes.getKeys().stream().filter(AttributePredicates.SYNCING_ATTRIBUTE).filter(attrName -> !Objects.equals(newAttributes.getValues(attrName), oldAttributes.getValues(attrName))).collect(Collectors.toMap(attrName -> attrName, arg_0 -> ((Attributes)newAttributes).getValues(arg_0)));
    }

    protected void synchroniseMemberships(Collection<G> remoteGroups, DirectoryCache directoryCache, boolean isFullSync) throws OperationFailedException {
        if (log.isDebugEnabled()) {
            log.debug("Updating memberships for " + remoteGroups.size() + " groups from " + this.directoryDescription());
        }
        int total = remoteGroups.size();
        IdentifierMap groupsByName = new IdentifierMap();
        for (GroupWithAttributes g : remoteGroups) {
            String name = g.getName();
            if (null == groupsByName.put(name, g)) continue;
            throw new OperationFailedException("Unable to synchronise directory: duplicate groups with name '" + name + "'");
        }
        TimedProgressOperation operation = new TimedProgressOperation("migrated memberships for group", total, log);
        TimedOperation getMembershipsOperation = new TimedOperation();
        Collection values = groupsByName.values();
        log.debug(getMembershipsOperation.complete("Got remote memberships"));
        Iterator<Membership> iter = this.getMemberships(values, isFullSync).iterator();
        TimedOperation applyMembershipsOperation = new TimedOperation();
        while (iter.hasNext()) {
            long start = System.currentTimeMillis();
            Membership membership = iter.next();
            long finish = System.currentTimeMillis();
            long duration = finish - start;
            log.debug("found [ " + membership.getUserNames().size() + " ] remote user-group memberships, [ " + membership.getChildGroupNames().size() + " ] remote group-group memberships in [ " + duration + "ms ]");
            Group g = (Group)groupsByName.get(membership.getGroupName());
            if (g == null) {
                log.debug("Unexpected group in response: " + membership.getGroupName());
                continue;
            }
            directoryCache.syncUserMembersForGroup(g, membership.getUserNames());
            if (this.remoteDirectory.supportsNestedGroups()) {
                directoryCache.syncGroupMembersForGroup(g, membership.getChildGroupNames());
            }
            operation.incrementedProgress();
        }
        log.debug(applyMembershipsOperation.complete("Applied remote memberships"));
    }

    protected boolean isIncrementalSyncEnabled() {
        return Boolean.parseBoolean(this.remoteDirectory.getValue("crowd.sync.incremental.enabled"));
    }

    protected String directoryDescription() {
        return this.remoteDirectory.getDescriptiveName() + " Directory " + this.remoteDirectory.getDirectoryId();
    }
}

