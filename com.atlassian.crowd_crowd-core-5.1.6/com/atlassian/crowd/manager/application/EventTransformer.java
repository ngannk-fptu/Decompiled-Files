/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directories
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.impl.IdentifierSet
 *  com.atlassian.crowd.exception.DirectoryNotFoundException
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.manager.directory.DirectoryManager
 *  com.atlassian.crowd.model.application.Application
 *  com.atlassian.crowd.model.application.Applications
 *  com.atlassian.crowd.model.event.GroupEvent
 *  com.atlassian.crowd.model.event.GroupMembershipEvent
 *  com.atlassian.crowd.model.event.Operation
 *  com.atlassian.crowd.model.event.OperationEvent
 *  com.atlassian.crowd.model.event.UserEvent
 *  com.atlassian.crowd.model.event.UserMembershipEvent
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.group.GroupType
 *  com.atlassian.crowd.model.user.User
 *  com.atlassian.crowd.search.EntityDescriptor
 *  com.atlassian.crowd.search.builder.QueryBuilder
 *  com.google.common.base.Predicate
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 */
package com.atlassian.crowd.manager.application;

import com.atlassian.crowd.embedded.api.Directories;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.impl.IdentifierSet;
import com.atlassian.crowd.exception.DirectoryNotFoundException;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.manager.application.canonicality.CanonicalEntityByNameFinder;
import com.atlassian.crowd.manager.directory.DirectoryManager;
import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.model.application.Applications;
import com.atlassian.crowd.model.event.GroupEvent;
import com.atlassian.crowd.model.event.GroupMembershipEvent;
import com.atlassian.crowd.model.event.Operation;
import com.atlassian.crowd.model.event.OperationEvent;
import com.atlassian.crowd.model.event.UserEvent;
import com.atlassian.crowd.model.event.UserMembershipEvent;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.GroupType;
import com.atlassian.crowd.model.user.User;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.builder.QueryBuilder;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EventTransformer {
    private static final int NON_EXISTING_DIRECTORY_ID = -1;
    private final DirectoryManager directoryManager;
    private final List<Directory> activeDirectories;

    public EventTransformer(DirectoryManager directoryManager, Application application) {
        this.directoryManager = directoryManager;
        this.activeDirectories = Applications.getActiveDirectories((Application)application);
    }

    public List<OperationEvent> transformEvents(Iterable<OperationEvent> events) throws OperationFailedException {
        ArrayList<OperationEvent> transformed = new ArrayList<OperationEvent>();
        for (OperationEvent event : events) {
            if (event.getDirectoryId() == null) {
                transformed.add(event);
                continue;
            }
            int eventDirectoryIndex = Iterables.indexOf(this.activeDirectories, (Predicate)Directories.directoryWithIdPredicate((long)event.getDirectoryId()));
            boolean isDirectoryActive = eventDirectoryIndex != -1;
            if (!isDirectoryActive) continue;
            if (event instanceof UserEvent) {
                UserEvent userEvent = (UserEvent)event;
                transformed.addAll(this.processUserEvent(eventDirectoryIndex, userEvent));
                continue;
            }
            if (event instanceof GroupEvent) {
                GroupEvent groupEvent = (GroupEvent)event;
                transformed.addAll(this.processGroupEvent(eventDirectoryIndex, groupEvent));
                continue;
            }
            if (event instanceof UserMembershipEvent) {
                UserMembershipEvent userMembershipEvent = (UserMembershipEvent)event;
                transformed.addAll(this.processUserMembershipEvent(userMembershipEvent));
                continue;
            }
            if (event instanceof GroupMembershipEvent) {
                GroupMembershipEvent groupMembershipEvent = (GroupMembershipEvent)event;
                transformed.addAll(this.processGroupMembershipEvent(groupMembershipEvent));
                continue;
            }
            throw new IllegalArgumentException("Event type " + event.getClass() + " not supported.");
        }
        return transformed;
    }

    private List<? extends OperationEvent> processUserEvent(int eventDirectoryIndex, UserEvent event) throws OperationFailedException {
        ImmutableList events;
        String username = event.getUser().getName();
        List<Directory> earlierDirectories = this.activeDirectories.subList(0, eventDirectoryIndex);
        List<Directory> laterDirectories = this.activeDirectories.subList(eventDirectoryIndex + 1, this.activeDirectories.size());
        if (this.findUser(earlierDirectories, username) != null) {
            if (event.getOperation() == Operation.DELETED) {
                Set<String> parentGroupNames = this.getParentGroupNames(EntityDescriptor.user(), username);
                events = ImmutableList.of((Object)new UserMembershipEvent(Operation.UPDATED, null, username, parentGroupNames));
            } else {
                events = ImmutableList.of();
            }
        } else if (event.getOperation() == Operation.CREATED) {
            events = this.findUser(laterDirectories, username) != null ? ImmutableList.of((Object)new UserEvent(Operation.UPDATED, null, event.getUser(), event.getStoredAttributes(), event.getDeletedAttributes())) : ImmutableList.of((Object)event);
        } else if (event.getOperation() == Operation.DELETED) {
            User laterUser = this.findUser(laterDirectories, username);
            if (laterUser != null) {
                Set<String> parentGroupNames = this.getParentGroupNames(EntityDescriptor.user(), username);
                UserEvent userEvent = new UserEvent(Operation.UPDATED, null, laterUser, null, null);
                UserMembershipEvent membershipEvent = new UserMembershipEvent(Operation.UPDATED, null, username, parentGroupNames);
                events = ImmutableList.of((Object)userEvent, (Object)membershipEvent);
            } else {
                events = ImmutableList.of((Object)event);
            }
        } else {
            events = ImmutableList.of((Object)event);
        }
        return events;
    }

    private List<? extends OperationEvent> processGroupEvent(int eventDirectoryIndex, GroupEvent event) throws OperationFailedException {
        ImmutableList events;
        String groupName = event.getGroup().getName();
        List<Directory> earlierDirectories = this.activeDirectories.subList(0, eventDirectoryIndex);
        if (this.findGroup(earlierDirectories, groupName) != null) {
            if (event.getOperation() == Operation.DELETED) {
                Set<String> parentGroupNames = this.getParentGroupNames(EntityDescriptor.group((GroupType)GroupType.GROUP), groupName);
                Set<String> childGroupNames = this.getChildGroupNames(groupName);
                events = ImmutableList.of((Object)new GroupMembershipEvent(Operation.UPDATED, null, groupName, parentGroupNames, childGroupNames));
            } else {
                events = ImmutableList.of();
            }
        } else if (event.getOperation() == Operation.CREATED) {
            List<Directory> laterDirectories = this.activeDirectories.subList(eventDirectoryIndex + 1, this.activeDirectories.size());
            events = this.findGroup(laterDirectories, groupName) != null ? ImmutableList.of((Object)new GroupEvent(Operation.UPDATED, null, event.getGroup(), event.getStoredAttributes(), event.getDeletedAttributes())) : ImmutableList.of((Object)event);
        } else if (event.getOperation() == Operation.DELETED) {
            List<Directory> laterDirectories = this.activeDirectories.subList(eventDirectoryIndex + 1, this.activeDirectories.size());
            Group laterGroup = this.findGroup(laterDirectories, groupName);
            if (laterGroup != null) {
                GroupEvent groupEvent = new GroupEvent(Operation.UPDATED, null, laterGroup, null, null);
                Set<String> parentGroupNames = this.getParentGroupNames(EntityDescriptor.group((GroupType)GroupType.GROUP), groupName);
                Set<String> childGroupNames = this.getChildGroupNames(groupName);
                GroupMembershipEvent membershipEvent = new GroupMembershipEvent(Operation.UPDATED, null, groupName, parentGroupNames, childGroupNames);
                events = ImmutableList.of((Object)groupEvent, (Object)membershipEvent);
            } else {
                events = ImmutableList.of((Object)event);
            }
        } else {
            events = ImmutableList.of((Object)event);
        }
        return events;
    }

    private List<OperationEvent> processUserMembershipEvent(UserMembershipEvent event) throws OperationFailedException {
        UserMembershipEvent applicationEvent;
        if (event.getOperation() == Operation.DELETED) {
            String username = event.getChildUsername();
            IdentifierSet disappearedParentGroupNames = IdentifierSet.difference((Collection)event.getParentGroupNames(), this.getParentGroupNames(EntityDescriptor.user(), username));
            applicationEvent = new UserMembershipEvent(Operation.DELETED, event.getDirectoryId(), username, (Set)disappearedParentGroupNames);
        } else {
            applicationEvent = event;
        }
        return ImmutableList.of((Object)applicationEvent);
    }

    private List<OperationEvent> processGroupMembershipEvent(GroupMembershipEvent event) throws OperationFailedException {
        GroupMembershipEvent applicationEvent;
        if (event.getOperation() == Operation.DELETED) {
            String groupName = event.getGroupName();
            IdentifierSet parentGroupNames = IdentifierSet.difference((Collection)event.getParentGroupNames(), this.getParentGroupNames(EntityDescriptor.group(), groupName));
            IdentifierSet childGroupNames = IdentifierSet.difference((Collection)event.getChildGroupNames(), this.getChildGroupNames(groupName));
            applicationEvent = new GroupMembershipEvent(Operation.DELETED, event.getDirectoryId(), groupName, (Set)parentGroupNames, (Set)childGroupNames);
        } else {
            applicationEvent = event;
        }
        return ImmutableList.of((Object)applicationEvent);
    }

    private Set<String> getParentGroupNames(EntityDescriptor entityDescriptor, String name) throws OperationFailedException {
        HashSet<String> parentGroupNames = new HashSet<String>();
        for (Directory directory : this.activeDirectories) {
            try {
                parentGroupNames.addAll(this.directoryManager.searchDirectGroupRelationships(directory.getId().longValue(), QueryBuilder.queryFor(String.class, (EntityDescriptor)EntityDescriptor.group((GroupType)GroupType.GROUP)).parentsOf(entityDescriptor).withName(name).returningAtMost(-1)));
            }
            catch (DirectoryNotFoundException e) {
                throw new OperationFailedException("Directory has been removed", (Throwable)e);
            }
        }
        return parentGroupNames;
    }

    private Set<String> getChildGroupNames(String groupName) throws OperationFailedException {
        HashSet<String> childGroupNames = new HashSet<String>();
        for (Directory directory : this.activeDirectories) {
            try {
                childGroupNames.addAll(this.directoryManager.searchDirectGroupRelationships(directory.getId().longValue(), QueryBuilder.queryFor(String.class, (EntityDescriptor)EntityDescriptor.group((GroupType)GroupType.GROUP)).childrenOf(EntityDescriptor.group((GroupType)GroupType.GROUP)).withName(groupName).returningAtMost(-1)));
            }
            catch (DirectoryNotFoundException e) {
                throw new OperationFailedException("Directory has been removed", (Throwable)e);
            }
        }
        return childGroupNames;
    }

    private User findUser(Iterable<Directory> directories, String username) throws OperationFailedException {
        return new CanonicalEntityByNameFinder(this.directoryManager, directories).fastFailingFindOptionalUserByName(username).orElse(null);
    }

    private Group findGroup(Iterable<Directory> directories, String groupName) throws OperationFailedException {
        return new CanonicalEntityByNameFinder(this.directoryManager, directories).fastFailingFindOptionalGroupByName(groupName).orElse(null);
    }
}

