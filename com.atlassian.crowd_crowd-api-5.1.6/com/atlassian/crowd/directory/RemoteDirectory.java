/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.crowd.embedded.api.Attributes
 *  com.atlassian.crowd.embedded.api.PasswordCredential
 *  com.atlassian.crowd.exception.ExpiredCredentialException
 *  com.atlassian.crowd.exception.GroupNotFoundException
 *  com.atlassian.crowd.exception.InactiveAccountException
 *  com.atlassian.crowd.exception.InvalidAuthenticationException
 *  com.atlassian.crowd.exception.InvalidCredentialException
 *  com.atlassian.crowd.exception.InvalidGroupException
 *  com.atlassian.crowd.exception.InvalidMembershipException
 *  com.atlassian.crowd.exception.InvalidUserException
 *  com.atlassian.crowd.exception.MembershipAlreadyExistsException
 *  com.atlassian.crowd.exception.MembershipNotFoundException
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.exception.ReadOnlyGroupException
 *  com.atlassian.crowd.exception.UserAlreadyExistsException
 *  com.atlassian.crowd.exception.UserNotFoundException
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.group.GroupWithAttributes
 *  com.atlassian.crowd.model.group.Membership
 *  com.atlassian.crowd.model.user.User
 *  com.atlassian.crowd.model.user.UserWithAttributes
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.crowd.directory;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.crowd.embedded.api.Attributes;
import com.atlassian.crowd.embedded.api.PasswordCredential;
import com.atlassian.crowd.exception.ExpiredCredentialException;
import com.atlassian.crowd.exception.GroupNotFoundException;
import com.atlassian.crowd.exception.InactiveAccountException;
import com.atlassian.crowd.exception.InvalidAuthenticationException;
import com.atlassian.crowd.exception.InvalidCredentialException;
import com.atlassian.crowd.exception.InvalidGroupException;
import com.atlassian.crowd.exception.InvalidMembershipException;
import com.atlassian.crowd.exception.InvalidUserException;
import com.atlassian.crowd.exception.MembershipAlreadyExistsException;
import com.atlassian.crowd.exception.MembershipNotFoundException;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.exception.ReadOnlyGroupException;
import com.atlassian.crowd.exception.UserAlreadyExistsException;
import com.atlassian.crowd.exception.UserNotFoundException;
import com.atlassian.crowd.manager.avatar.AvatarReference;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.GroupTemplate;
import com.atlassian.crowd.model.group.GroupWithAttributes;
import com.atlassian.crowd.model.group.Membership;
import com.atlassian.crowd.model.user.User;
import com.atlassian.crowd.model.user.UserTemplate;
import com.atlassian.crowd.model.user.UserTemplateWithAttributes;
import com.atlassian.crowd.model.user.UserWithAttributes;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import com.atlassian.crowd.search.query.membership.MembershipQuery;
import com.atlassian.crowd.util.BoundedCount;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface RemoteDirectory
extends Attributes {
    public long getDirectoryId();

    public void setDirectoryId(long var1);

    @Nonnull
    public String getDescriptiveName();

    public void setAttributes(Map<String, String> var1);

    @Nonnull
    public User findUserByName(String var1) throws UserNotFoundException, OperationFailedException;

    @Nonnull
    public UserWithAttributes findUserWithAttributesByName(String var1) throws UserNotFoundException, OperationFailedException;

    @Nonnull
    public User findUserByExternalId(String var1) throws UserNotFoundException, OperationFailedException;

    @Nonnull
    public User authenticate(String var1, PasswordCredential var2) throws UserNotFoundException, InactiveAccountException, InvalidAuthenticationException, ExpiredCredentialException, OperationFailedException;

    @Nonnull
    @Deprecated
    public User addUser(UserTemplate var1, PasswordCredential var2) throws InvalidUserException, InvalidCredentialException, UserAlreadyExistsException, OperationFailedException;

    public UserWithAttributes addUser(UserTemplateWithAttributes var1, PasswordCredential var2) throws InvalidUserException, InvalidCredentialException, UserAlreadyExistsException, OperationFailedException;

    @Nonnull
    public User updateUser(UserTemplate var1) throws InvalidUserException, UserNotFoundException, OperationFailedException;

    public void updateUserCredential(String var1, PasswordCredential var2) throws UserNotFoundException, InvalidCredentialException, OperationFailedException;

    @Nonnull
    public User renameUser(String var1, String var2) throws UserNotFoundException, InvalidUserException, UserAlreadyExistsException, OperationFailedException;

    public void storeUserAttributes(String var1, Map<String, Set<String>> var2) throws UserNotFoundException, OperationFailedException;

    public void removeUserAttributes(String var1, String var2) throws UserNotFoundException, OperationFailedException;

    public void removeUser(String var1) throws UserNotFoundException, OperationFailedException;

    @Nonnull
    public <T> List<T> searchUsers(EntityQuery<T> var1) throws OperationFailedException;

    @Nonnull
    public Group findGroupByName(String var1) throws GroupNotFoundException, OperationFailedException;

    @Nonnull
    public GroupWithAttributes findGroupWithAttributesByName(String var1) throws GroupNotFoundException, OperationFailedException;

    @Nonnull
    public Group addGroup(GroupTemplate var1) throws InvalidGroupException, OperationFailedException;

    @Nonnull
    public Group updateGroup(GroupTemplate var1) throws InvalidGroupException, GroupNotFoundException, ReadOnlyGroupException, OperationFailedException;

    @Nonnull
    public Group renameGroup(String var1, String var2) throws GroupNotFoundException, InvalidGroupException, OperationFailedException;

    public void storeGroupAttributes(String var1, Map<String, Set<String>> var2) throws GroupNotFoundException, OperationFailedException;

    public void removeGroupAttributes(String var1, String var2) throws GroupNotFoundException, OperationFailedException;

    public void removeGroup(String var1) throws GroupNotFoundException, ReadOnlyGroupException, OperationFailedException;

    @Nonnull
    public <T> List<T> searchGroups(EntityQuery<T> var1) throws OperationFailedException;

    public boolean isUserDirectGroupMember(String var1, String var2) throws OperationFailedException;

    public boolean isGroupDirectGroupMember(String var1, String var2) throws OperationFailedException;

    @Nonnull
    public BoundedCount countDirectMembersOfGroup(String var1, int var2) throws OperationFailedException;

    public void addUserToGroup(String var1, String var2) throws GroupNotFoundException, UserNotFoundException, ReadOnlyGroupException, OperationFailedException, MembershipAlreadyExistsException;

    public void addGroupToGroup(String var1, String var2) throws GroupNotFoundException, InvalidMembershipException, ReadOnlyGroupException, OperationFailedException, MembershipAlreadyExistsException;

    public void removeUserFromGroup(String var1, String var2) throws GroupNotFoundException, UserNotFoundException, MembershipNotFoundException, ReadOnlyGroupException, OperationFailedException;

    public void removeGroupFromGroup(String var1, String var2) throws GroupNotFoundException, InvalidMembershipException, MembershipNotFoundException, ReadOnlyGroupException, OperationFailedException;

    @Nonnull
    public <T> List<T> searchGroupRelationships(MembershipQuery<T> var1) throws OperationFailedException;

    public void testConnection() throws OperationFailedException;

    public boolean supportsInactiveAccounts();

    public boolean supportsNestedGroups();

    public boolean supportsPasswordExpiration();

    public boolean supportsSettingEncryptedCredential();

    @Deprecated
    public boolean isRolesDisabled();

    @Nonnull
    public Iterable<Membership> getMemberships() throws OperationFailedException;

    @Nonnull
    public RemoteDirectory getAuthoritativeDirectory();

    public void expireAllPasswords() throws OperationFailedException;

    @Nullable
    default public AvatarReference getUserAvatarByName(String username, int sizeHint) throws UserNotFoundException, OperationFailedException {
        return null;
    }

    @ExperimentalApi
    default public User updateUserFromRemoteDirectory(User remoteUser) throws OperationFailedException, UserNotFoundException {
        return remoteUser;
    }

    @ExperimentalApi
    default public User userAuthenticated(String username) throws OperationFailedException, UserNotFoundException, InactiveAccountException {
        RemoteDirectory authoritativeDirectory = this.getAuthoritativeDirectory();
        User remoteUserAfterAuth = authoritativeDirectory != this ? authoritativeDirectory.userAuthenticated(username) : this.findUserByName(username);
        User user = this.updateUserFromRemoteDirectory(remoteUserAfterAuth);
        if (!user.isActive()) {
            throw new InactiveAccountException(user.getName());
        }
        return user;
    }

    @ExperimentalApi
    default public Optional<Set<String>> getLocallyFilteredGroupNames() {
        return Optional.empty();
    }
}

