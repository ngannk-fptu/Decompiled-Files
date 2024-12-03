/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 */
package com.atlassian.crowd.embedded.api;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.crowd.embedded.api.Group;
import com.atlassian.crowd.embedded.api.GroupWithAttributes;
import com.atlassian.crowd.embedded.api.Query;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.crowd.embedded.api.UserCapabilities;
import com.atlassian.crowd.embedded.api.UserWithAttributes;
import com.atlassian.crowd.exception.FailedAuthenticationException;
import com.atlassian.crowd.exception.InactiveAccountException;
import com.atlassian.crowd.exception.InvalidCredentialException;
import com.atlassian.crowd.exception.InvalidMembershipException;
import com.atlassian.crowd.exception.InvalidUserException;
import com.atlassian.crowd.exception.OperationNotPermittedException;
import com.atlassian.crowd.exception.embedded.InvalidGroupException;
import com.atlassian.crowd.exception.runtime.GroupNotFoundException;
import com.atlassian.crowd.exception.runtime.OperationFailedException;
import com.atlassian.crowd.exception.runtime.UserNotFoundException;
import java.util.Set;

public interface CrowdService {
    public User authenticate(String var1, String var2) throws FailedAuthenticationException, OperationFailedException;

    public User getUser(String var1);

    public User getRemoteUser(String var1);

    @ExperimentalApi
    public User userAuthenticated(String var1) throws UserNotFoundException, OperationFailedException, InactiveAccountException;

    public UserWithAttributes getUserWithAttributes(String var1);

    public Group getGroup(String var1);

    public GroupWithAttributes getGroupWithAttributes(String var1);

    public <T> Iterable<T> search(Query<T> var1);

    public boolean isUserMemberOfGroup(String var1, String var2);

    public boolean isUserMemberOfGroup(User var1, Group var2);

    public boolean isGroupMemberOfGroup(String var1, String var2);

    public boolean isGroupMemberOfGroup(Group var1, Group var2);

    @Deprecated
    public User addUser(User var1, String var2) throws InvalidUserException, InvalidCredentialException, OperationNotPermittedException, OperationFailedException;

    public UserWithAttributes addUser(UserWithAttributes var1, String var2) throws InvalidUserException, InvalidCredentialException, OperationNotPermittedException, OperationFailedException;

    public User updateUser(User var1) throws UserNotFoundException, InvalidUserException, OperationNotPermittedException, OperationFailedException;

    public User renameUser(User var1, String var2) throws UserNotFoundException, InvalidUserException, OperationNotPermittedException, OperationFailedException;

    public void updateUserCredential(User var1, String var2) throws UserNotFoundException, InvalidCredentialException, OperationNotPermittedException, OperationFailedException;

    public void setUserAttribute(User var1, String var2, String var3) throws UserNotFoundException, OperationNotPermittedException, OperationFailedException;

    public void setUserAttribute(User var1, String var2, Set<String> var3) throws UserNotFoundException, OperationNotPermittedException, OperationFailedException;

    public void removeUserAttribute(User var1, String var2) throws UserNotFoundException, OperationNotPermittedException, OperationFailedException;

    public void removeAllUserAttributes(User var1) throws UserNotFoundException, OperationNotPermittedException, OperationFailedException;

    public boolean removeUser(User var1) throws OperationNotPermittedException, OperationFailedException;

    public Group addGroup(Group var1) throws InvalidGroupException, OperationNotPermittedException, OperationFailedException;

    public Group updateGroup(Group var1) throws GroupNotFoundException, InvalidGroupException, OperationNotPermittedException, OperationFailedException;

    public void setGroupAttribute(Group var1, String var2, String var3) throws GroupNotFoundException, OperationNotPermittedException, OperationFailedException;

    public void setGroupAttribute(Group var1, String var2, Set<String> var3) throws GroupNotFoundException, OperationNotPermittedException, OperationFailedException;

    public void removeGroupAttribute(Group var1, String var2) throws GroupNotFoundException, OperationNotPermittedException, OperationFailedException;

    public void removeAllGroupAttributes(Group var1) throws GroupNotFoundException, OperationNotPermittedException, OperationFailedException;

    public boolean removeGroup(Group var1) throws OperationNotPermittedException, OperationFailedException;

    public boolean addUserToGroup(User var1, Group var2) throws GroupNotFoundException, UserNotFoundException, OperationNotPermittedException, OperationFailedException;

    public boolean addGroupToGroup(Group var1, Group var2) throws GroupNotFoundException, OperationNotPermittedException, InvalidMembershipException, OperationFailedException;

    public boolean removeUserFromGroup(User var1, Group var2) throws GroupNotFoundException, UserNotFoundException, OperationNotPermittedException, OperationFailedException;

    public boolean removeGroupFromGroup(Group var1, Group var2) throws GroupNotFoundException, OperationNotPermittedException, OperationFailedException;

    public boolean isUserDirectGroupMember(User var1, Group var2) throws OperationFailedException;

    public boolean isGroupDirectGroupMember(Group var1, Group var2) throws OperationFailedException;

    public UserCapabilities getCapabilitiesForNewUsers();
}

