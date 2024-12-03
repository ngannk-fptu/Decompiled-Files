/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.RemoteCrowdDirectory
 *  com.atlassian.crowd.directory.RemoteDirectory
 *  com.atlassian.crowd.directory.synchronisation.CacheSynchronisationResult
 *  com.atlassian.crowd.directory.synchronisation.PartialSynchronisationResult
 *  com.atlassian.crowd.directory.synchronisation.cache.AbstractCacheRefresher
 *  com.atlassian.crowd.directory.synchronisation.cache.CacheRefresher
 *  com.atlassian.crowd.directory.synchronisation.cache.DirectoryCache
 *  com.atlassian.crowd.event.EventTokenExpiredException
 *  com.atlassian.crowd.event.Events
 *  com.atlassian.crowd.event.IncrementalSynchronisationNotAvailableException
 *  com.atlassian.crowd.exception.CrowdException
 *  com.atlassian.crowd.exception.GroupNotFoundException
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.exception.UnsupportedCrowdApiException
 *  com.atlassian.crowd.exception.UserNotFoundException
 *  com.atlassian.crowd.model.event.GroupEvent
 *  com.atlassian.crowd.model.event.GroupMembershipEvent
 *  com.atlassian.crowd.model.event.Operation
 *  com.atlassian.crowd.model.event.OperationEvent
 *  com.atlassian.crowd.model.event.UserEvent
 *  com.atlassian.crowd.model.event.UserMembershipEvent
 *  com.atlassian.crowd.model.group.GroupWithAttributes
 *  com.atlassian.crowd.model.user.UserWithAttributes
 *  com.google.common.base.Strings
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.directory.ldap.cache;

import com.atlassian.crowd.directory.RemoteCrowdDirectory;
import com.atlassian.crowd.directory.RemoteDirectory;
import com.atlassian.crowd.directory.synchronisation.CacheSynchronisationResult;
import com.atlassian.crowd.directory.synchronisation.PartialSynchronisationResult;
import com.atlassian.crowd.directory.synchronisation.cache.AbstractCacheRefresher;
import com.atlassian.crowd.directory.synchronisation.cache.CacheRefresher;
import com.atlassian.crowd.directory.synchronisation.cache.DirectoryCache;
import com.atlassian.crowd.event.EventTokenExpiredException;
import com.atlassian.crowd.event.Events;
import com.atlassian.crowd.event.IncrementalSynchronisationNotAvailableException;
import com.atlassian.crowd.exception.CrowdException;
import com.atlassian.crowd.exception.GroupNotFoundException;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.exception.UnsupportedCrowdApiException;
import com.atlassian.crowd.exception.UserNotFoundException;
import com.atlassian.crowd.model.event.GroupEvent;
import com.atlassian.crowd.model.event.GroupMembershipEvent;
import com.atlassian.crowd.model.event.Operation;
import com.atlassian.crowd.model.event.OperationEvent;
import com.atlassian.crowd.model.event.UserEvent;
import com.atlassian.crowd.model.event.UserMembershipEvent;
import com.atlassian.crowd.model.group.GroupWithAttributes;
import com.atlassian.crowd.model.user.UserWithAttributes;
import com.google.common.base.Strings;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventTokenChangedCacheRefresher
extends AbstractCacheRefresher {
    private static final Logger log = LoggerFactory.getLogger(EventTokenChangedCacheRefresher.class);
    private final CacheRefresher fullSyncCacheRefresher;
    private final RemoteCrowdDirectory crowdDirectory;

    public EventTokenChangedCacheRefresher(RemoteCrowdDirectory crowdDirectory, CacheRefresher fullSyncCacheRefresher) {
        super((RemoteDirectory)crowdDirectory);
        this.crowdDirectory = crowdDirectory;
        this.fullSyncCacheRefresher = fullSyncCacheRefresher;
    }

    public CacheSynchronisationResult synchroniseAll(DirectoryCache directoryCache) throws OperationFailedException {
        String initialEventToken = null;
        if (this.isIncrementalSyncEnabled()) {
            try {
                initialEventToken = this.crowdDirectory.getCurrentEventToken();
            }
            catch (UnsupportedCrowdApiException e) {
                log.debug("Remote server does not support event based sync.");
            }
            catch (OperationFailedException e) {
                log.warn("Could not update event token.", (Throwable)e);
            }
            catch (IncrementalSynchronisationNotAvailableException e) {
                log.warn("Incremental synchronisation is not available. Falling back to full synchronisation", (Throwable)e);
            }
        }
        CacheSynchronisationResult result = this.fullSyncCacheRefresher.synchroniseAll(directoryCache);
        String postEventToken = null;
        try {
            postEventToken = this.crowdDirectory.getCurrentEventToken();
            if (initialEventToken != null && !initialEventToken.equals(postEventToken)) {
                log.warn("Possible events during full synchronisation");
            }
        }
        catch (CrowdException e) {
            log.debug("Failed to retrieve event token after full synchronisation", (Throwable)e);
        }
        if (result.isSuccess()) {
            return new CacheSynchronisationResult(true, postEventToken);
        }
        return CacheSynchronisationResult.FAILURE;
    }

    public CacheSynchronisationResult synchroniseChanges(DirectoryCache directoryCache, @Nullable String eventToken) throws OperationFailedException {
        Events events;
        if (!this.isIncrementalSyncEnabled()) {
            log.debug("Incremental synchronisation is not enabled");
            return CacheSynchronisationResult.FAILURE;
        }
        if (Strings.emptyToNull((String)eventToken) == null) {
            log.debug("A full synchronisation is needed to obtain the current token event");
            return CacheSynchronisationResult.FAILURE;
        }
        try {
            events = this.crowdDirectory.getNewEvents(eventToken);
        }
        catch (EventTokenExpiredException e) {
            if (e.getMessage() != null) {
                log.error("Incremental synchronisation failed: {}", (Object)e.getMessage());
            }
            return CacheSynchronisationResult.FAILURE;
        }
        for (OperationEvent event : events.getEvents()) {
            UserMembershipEvent membershipEvent;
            if (event instanceof UserEvent) {
                UserEvent userEvent = (UserEvent)event;
                if (event.getOperation() == Operation.CREATED || event.getOperation() == Operation.UPDATED) {
                    directoryCache.addOrUpdateCachedUser(userEvent.getUser());
                    if (!this.isUserAttributeSynchronisationEnabled()) continue;
                    try {
                        directoryCache.applySyncingUserAttributes(userEvent.getUser().getName(), userEvent.getDeletedAttributes(), userEvent.getStoredAttributes());
                        continue;
                    }
                    catch (UserNotFoundException e) {
                        throw new OperationFailedException("Failed to synchronize directory user attributes for missing user: " + userEvent.getUser().getName());
                    }
                }
                if (event.getOperation() != Operation.DELETED) continue;
                directoryCache.deleteCachedUser(userEvent.getUser().getName());
                continue;
            }
            if (event instanceof GroupEvent) {
                GroupEvent groupEvent = (GroupEvent)event;
                if (event.getOperation() == Operation.CREATED || event.getOperation() == Operation.UPDATED) {
                    directoryCache.addOrUpdateCachedGroup(groupEvent.getGroup());
                    try {
                        directoryCache.applySyncingGroupAttributes(groupEvent.getGroup().getName(), groupEvent.getDeletedAttributes(), groupEvent.getStoredAttributes());
                        continue;
                    }
                    catch (GroupNotFoundException e) {
                        throw new OperationFailedException("Failed to synchronize directory group attributes for missing group: " + groupEvent.getGroup().getName());
                    }
                }
                if (event.getOperation() != Operation.DELETED) continue;
                directoryCache.deleteCachedGroup(groupEvent.getGroup().getName());
                continue;
            }
            if (event instanceof UserMembershipEvent) {
                membershipEvent = (UserMembershipEvent)event;
                if (event.getOperation() == Operation.CREATED) {
                    for (String parentGroupName : membershipEvent.getParentGroupNames()) {
                        directoryCache.addUserToGroup(membershipEvent.getChildUsername(), parentGroupName);
                    }
                    continue;
                }
                if (event.getOperation() == Operation.DELETED) {
                    for (String parentGroupName : membershipEvent.getParentGroupNames()) {
                        directoryCache.removeUserFromGroup(membershipEvent.getChildUsername(), parentGroupName);
                    }
                    continue;
                }
                if (event.getOperation() != Operation.UPDATED) continue;
                directoryCache.syncGroupMembershipsForUser(membershipEvent.getChildUsername(), membershipEvent.getParentGroupNames());
                continue;
            }
            if (event instanceof GroupMembershipEvent) {
                membershipEvent = (GroupMembershipEvent)event;
                if (event.getOperation() == Operation.CREATED) {
                    for (String parentGroupName : membershipEvent.getParentGroupNames()) {
                        directoryCache.addGroupToGroup(membershipEvent.getGroupName(), parentGroupName);
                    }
                    continue;
                }
                if (event.getOperation() == Operation.DELETED) {
                    for (String parentGroupName : membershipEvent.getParentGroupNames()) {
                        directoryCache.removeGroupFromGroup(membershipEvent.getGroupName(), parentGroupName);
                    }
                    continue;
                }
                if (event.getOperation() != Operation.UPDATED) continue;
                directoryCache.syncGroupMembershipsAndMembersForGroup(membershipEvent.getGroupName(), membershipEvent.getParentGroupNames(), membershipEvent.getChildGroupNames());
                continue;
            }
            throw new RuntimeException("Unsupported event " + event);
        }
        return new CacheSynchronisationResult(true, events.getNewEventToken());
    }

    protected PartialSynchronisationResult<? extends UserWithAttributes> synchroniseAllUsers(DirectoryCache directoryCache) throws OperationFailedException {
        throw new UnsupportedOperationException();
    }

    protected PartialSynchronisationResult<? extends GroupWithAttributes> synchroniseAllGroups(DirectoryCache directoryCache) throws OperationFailedException {
        throw new UnsupportedOperationException();
    }
}

