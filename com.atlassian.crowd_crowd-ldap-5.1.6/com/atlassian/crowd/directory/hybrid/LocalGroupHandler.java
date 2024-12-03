/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.InternalRemoteDirectory
 *  com.atlassian.crowd.exception.DirectoryNotFoundException
 *  com.atlassian.crowd.exception.GroupNotFoundException
 *  com.atlassian.crowd.exception.InvalidGroupException
 *  com.atlassian.crowd.exception.InvalidMembershipException
 *  com.atlassian.crowd.exception.MembershipAlreadyExistsException
 *  com.atlassian.crowd.exception.MembershipNotFoundException
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.exception.ReadOnlyGroupException
 *  com.atlassian.crowd.exception.UserNotFoundException
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.group.GroupTemplate
 *  com.atlassian.crowd.model.group.InternalDirectoryGroup
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.directory.hybrid;

import com.atlassian.crowd.directory.InternalRemoteDirectory;
import com.atlassian.crowd.directory.hybrid.InternalGroupHandler;
import com.atlassian.crowd.exception.DirectoryNotFoundException;
import com.atlassian.crowd.exception.GroupNotFoundException;
import com.atlassian.crowd.exception.InvalidGroupException;
import com.atlassian.crowd.exception.InvalidMembershipException;
import com.atlassian.crowd.exception.MembershipAlreadyExistsException;
import com.atlassian.crowd.exception.MembershipNotFoundException;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.exception.ReadOnlyGroupException;
import com.atlassian.crowd.exception.UserNotFoundException;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.GroupTemplate;
import com.atlassian.crowd.model.group.InternalDirectoryGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalGroupHandler
extends InternalGroupHandler {
    private static final Logger log = LoggerFactory.getLogger(LocalGroupHandler.class);

    public LocalGroupHandler(InternalRemoteDirectory internalDirectory) {
        super(internalDirectory);
    }

    public Group findLocalGroup(String groupName) throws GroupNotFoundException, ReadOnlyGroupException {
        InternalDirectoryGroup group = this.getInternalDirectory().findGroupByName(groupName);
        if (group.isLocal()) {
            return group;
        }
        throw new ReadOnlyGroupException(groupName);
    }

    public Group createLocalGroup(GroupTemplate groupTemplate) throws InvalidGroupException, OperationFailedException, DirectoryNotFoundException {
        return this.getInternalDirectory().addLocalGroup(groupTemplate);
    }

    public Group updateLocalGroup(GroupTemplate groupTemplate) throws OperationFailedException, GroupNotFoundException, ReadOnlyGroupException, InvalidGroupException {
        this.findLocalGroup(groupTemplate.getName());
        return this.getInternalDirectory().updateGroup(groupTemplate);
    }

    public void addUserToLocalGroup(String username, String groupName) throws OperationFailedException, GroupNotFoundException, ReadOnlyGroupException, UserNotFoundException, MembershipAlreadyExistsException {
        this.findLocalGroup(groupName);
        this.getInternalDirectory().addUserToGroup(username, groupName);
    }

    public void removeUserFromLocalGroup(String username, String groupName) throws OperationFailedException, GroupNotFoundException, MembershipNotFoundException, ReadOnlyGroupException, UserNotFoundException {
        this.findLocalGroup(groupName);
        this.getInternalDirectory().removeUserFromGroup(username, groupName);
    }

    public void addGroupToGroup(String parentGroup, String childGroup) throws GroupNotFoundException, OperationFailedException, ReadOnlyGroupException, MembershipAlreadyExistsException, InvalidMembershipException {
        this.findLocalGroup(parentGroup);
        try {
            this.getInternalDirectory().addGroupToGroup(childGroup, parentGroup);
        }
        catch (MembershipAlreadyExistsException e) {
            log.debug("Group '{}' is already a member of group '{}'.", (Object)childGroup, (Object)parentGroup);
        }
    }

    public void removeGroupFromGroup(String childGroup, String parentGroup) throws GroupNotFoundException, OperationFailedException, ReadOnlyGroupException, MembershipNotFoundException, InvalidMembershipException {
        this.findLocalGroup(parentGroup);
        this.getInternalDirectory().removeGroupFromGroup(childGroup, parentGroup);
    }
}

