/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.User
 *  com.atlassian.crowd.embedded.impl.IdentifierSet
 *  com.atlassian.crowd.exception.DirectoryNotFoundException
 *  com.atlassian.crowd.exception.GroupNotFoundException
 *  com.atlassian.crowd.exception.InvalidGroupException
 *  com.atlassian.crowd.exception.MembershipAlreadyExistsException
 *  com.atlassian.crowd.exception.MembershipNotFoundException
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.exception.ReadOnlyGroupException
 *  com.atlassian.crowd.exception.UserNotFoundException
 *  com.atlassian.crowd.exception.runtime.GroupNotFoundException
 *  com.atlassian.crowd.exception.runtime.UserNotFoundException
 *  com.atlassian.crowd.manager.directory.DirectoryManager
 *  com.atlassian.crowd.manager.directory.DirectoryPermissionException
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.group.GroupTemplate
 *  com.atlassian.crowd.search.EntityDescriptor
 *  com.atlassian.crowd.search.builder.QueryBuilder
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  javax.inject.Inject
 *  javax.inject.Named
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.authentication.impl.web.usercontext.impl.jit;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.crowd.embedded.impl.IdentifierSet;
import com.atlassian.crowd.exception.DirectoryNotFoundException;
import com.atlassian.crowd.exception.InvalidGroupException;
import com.atlassian.crowd.exception.MembershipAlreadyExistsException;
import com.atlassian.crowd.exception.MembershipNotFoundException;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.exception.ReadOnlyGroupException;
import com.atlassian.crowd.exception.UserNotFoundException;
import com.atlassian.crowd.exception.runtime.GroupNotFoundException;
import com.atlassian.crowd.manager.directory.DirectoryManager;
import com.atlassian.crowd.manager.directory.DirectoryPermissionException;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.GroupTemplate;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.builder.QueryBuilder;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.authentication.impl.web.usercontext.impl.jit.JitCrowdUser;
import com.atlassian.plugins.authentication.impl.web.usercontext.impl.jit.JitException;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
public class GroupProvisioningService {
    private static final Logger log = LoggerFactory.getLogger(GroupProvisioningService.class);
    private final DirectoryManager directoryManager;

    @Inject
    public GroupProvisioningService(@ComponentImport DirectoryManager directoryManager) {
        this.directoryManager = directoryManager;
    }

    public void updateUserGroups(JitCrowdUser user, IdentifierSet newGroupNames, Directory jitDirectory) {
        IdentifierSet currentUserGroups = this.getUserGroups(user);
        IdentifierSet groupsToRemoveFrom = IdentifierSet.difference((Collection)currentUserGroups, (Collection)newGroupNames);
        IdentifierSet groupsToAddTo = IdentifierSet.difference((Collection)newGroupNames, (Collection)currentUserGroups);
        log.debug("Updating groups for JIT user [{}]: removing from [{}], adding to [{}]", new Object[]{user.getName(), groupsToRemoveFrom, groupsToAddTo});
        this.removeUserFromGroups((User)user, groupsToRemoveFrom);
        this.addUserToGroups((User)user, groupsToAddTo, jitDirectory);
    }

    private IdentifierSet getUserGroups(JitCrowdUser username) {
        try {
            List result = this.directoryManager.searchDirectGroupRelationships(username.getDirectoryId(), QueryBuilder.queryFor(String.class, (EntityDescriptor)EntityDescriptor.group()).parentsOf(EntityDescriptor.user()).withName(username.getName()).returningAtMost(-1));
            return new IdentifierSet((Collection)result);
        }
        catch (DirectoryNotFoundException e) {
            throw new ConcurrentModificationException(e);
        }
        catch (OperationFailedException e) {
            throw new JitException(e);
        }
    }

    private void removeUserFromGroups(User user, IdentifierSet groups) {
        groups.forEach(group -> {
            try {
                log.debug("Removing user [{}] from group [{}]", (Object)user.getName(), group);
                this.directoryManager.removeUserFromGroup(user.getDirectoryId(), user.getName(), group);
            }
            catch (OperationFailedException | ReadOnlyGroupException | DirectoryPermissionException e) {
                log.error("Removing user [{}] from group [{}] failed", new Object[]{user.getName(), group, e});
                throw new JitException(e);
            }
            catch (DirectoryNotFoundException e) {
                log.error("Removing user [{}] from group [{}] failed as the directory does not exist", new Object[]{user, group, e});
                throw new ConcurrentModificationException(e);
            }
            catch (com.atlassian.crowd.exception.GroupNotFoundException e) {
                throw new GroupNotFoundException(e.getGroupName(), e.getCause());
            }
            catch (MembershipNotFoundException e) {
                log.debug("Cannot remove user [{}] from group [{}] as user is not a member of that group", new Object[]{user.getName(), group, e});
            }
            catch (UserNotFoundException e) {
                throw new com.atlassian.crowd.exception.runtime.UserNotFoundException(user.getName(), e.getCause());
            }
        });
    }

    private void addUserToGroups(User user, IdentifierSet groupNames, Directory jitDirectory) {
        groupNames.forEach(groupName -> {
            Group group = this.getGroup(user.getDirectoryId(), (String)groupName);
            if (group == null) {
                group = this.provisionGroup((String)groupName, jitDirectory);
            }
            try {
                log.debug("Adding user [{}] to group [{}]", (Object)user.getName(), (Object)group.getName());
                this.directoryManager.addUserToGroup(user.getDirectoryId(), user.getName(), group.getName());
            }
            catch (MembershipAlreadyExistsException e) {
                log.info("User [{}] is already a member of group [{}]", (Object)user.getName(), groupName);
            }
            catch (OperationFailedException | ReadOnlyGroupException | DirectoryPermissionException e) {
                log.error("Adding user [{}] to group [{}] failed", new Object[]{user, groupName, e});
                throw new JitException(e);
            }
            catch (DirectoryNotFoundException e) {
                log.error("Adding user [{}] to group [{}] failed as the directory does not exist", new Object[]{user, groupName, e});
                throw new ConcurrentModificationException(e);
            }
            catch (com.atlassian.crowd.exception.GroupNotFoundException e) {
                throw new GroupNotFoundException(groupName, e.getCause());
            }
            catch (UserNotFoundException e) {
                throw new com.atlassian.crowd.exception.runtime.UserNotFoundException(user.getName(), e.getCause());
            }
        });
    }

    @Nullable
    private Group getGroup(long directoryId, String groupName) {
        try {
            return this.directoryManager.findGroupByName(directoryId, groupName);
        }
        catch (com.atlassian.crowd.exception.GroupNotFoundException e) {
            return null;
        }
        catch (DirectoryNotFoundException e) {
            log.error("Could not find directory [{}] in which group [{}] should be created", (Object)directoryId, (Object)groupName);
            throw new ConcurrentModificationException(e);
        }
        catch (OperationFailedException e) {
            log.error("Creating group [{}] failed", (Object)groupName);
            throw new JitException(e);
        }
    }

    private Group provisionGroup(String name, Directory jitDirectory) {
        try {
            log.debug("JIT provisioning group [{}]", (Object)name);
            return this.directoryManager.addGroup(jitDirectory.getId().longValue(), new GroupTemplate(name, jitDirectory.getId().longValue()));
        }
        catch (DirectoryNotFoundException e) {
            log.error("Adding group [{}] failed as the directory does not exist", (Object)name, (Object)e);
            throw new ConcurrentModificationException(e);
        }
        catch (InvalidGroupException | OperationFailedException | DirectoryPermissionException e) {
            log.error("Adding group [{}] failed", (Object)name, (Object)e);
            throw new JitException(e);
        }
    }
}

