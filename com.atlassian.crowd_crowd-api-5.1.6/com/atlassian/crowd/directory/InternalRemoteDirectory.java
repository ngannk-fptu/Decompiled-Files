/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.crowd.exception.GroupNotFoundException
 *  com.atlassian.crowd.exception.InvalidGroupException
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.exception.UserNotFoundException
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.group.InternalDirectoryGroup
 *  com.atlassian.crowd.model.user.TimestampedUser
 *  com.atlassian.crowd.model.user.User
 *  javax.annotation.Nonnull
 */
package com.atlassian.crowd.directory;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.crowd.directory.FastEntityCountProvider;
import com.atlassian.crowd.directory.MultiValuesQueriesSupport;
import com.atlassian.crowd.directory.RemoteDirectory;
import com.atlassian.crowd.exception.GroupNotFoundException;
import com.atlassian.crowd.exception.InvalidGroupException;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.exception.UserNotFoundException;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.GroupTemplate;
import com.atlassian.crowd.model.group.InternalDirectoryGroup;
import com.atlassian.crowd.model.user.TimestampedUser;
import com.atlassian.crowd.model.user.User;
import com.atlassian.crowd.model.user.UserTemplateWithCredentialAndAttributes;
import com.atlassian.crowd.util.BatchResult;
import java.util.Collection;
import java.util.Set;
import javax.annotation.Nonnull;

public interface InternalRemoteDirectory
extends RemoteDirectory,
FastEntityCountProvider,
MultiValuesQueriesSupport {
    public TimestampedUser findUserByName(String var1) throws UserNotFoundException;

    public TimestampedUser findUserByExternalId(String var1) throws UserNotFoundException;

    public InternalDirectoryGroup findGroupByName(String var1) throws GroupNotFoundException;

    public Group addLocalGroup(GroupTemplate var1) throws InvalidGroupException, OperationFailedException;

    public BatchResult<User> addAllUsers(Set<UserTemplateWithCredentialAndAttributes> var1);

    public BatchResult<Group> addAllGroups(Set<GroupTemplate> var1);

    public BatchResult<String> addAllUsersToGroup(Set<String> var1, String var2) throws GroupNotFoundException;

    public BatchResult<String> removeAllUsers(Set<String> var1);

    public BatchResult<String> removeAllGroups(Set<String> var1);

    public boolean isLocalUserStatusEnabled();

    public User forceRenameUser(@Nonnull User var1, @Nonnull String var2) throws UserNotFoundException;

    @Nonnull
    public Set<String> getAllUserExternalIds() throws OperationFailedException;

    public BatchResult<String> addUserToGroups(String var1, Set<String> var2) throws UserNotFoundException;

    @ExperimentalApi
    public BatchResult<String> removeUsersFromGroup(Set<String> var1, String var2) throws GroupNotFoundException, OperationFailedException;

    @ExperimentalApi
    public BatchResult<String> addAllGroupsToGroup(Collection<String> var1, String var2) throws GroupNotFoundException;

    @ExperimentalApi
    public BatchResult<String> removeGroupsFromGroup(Collection<String> var1, String var2) throws GroupNotFoundException, OperationFailedException;
}

