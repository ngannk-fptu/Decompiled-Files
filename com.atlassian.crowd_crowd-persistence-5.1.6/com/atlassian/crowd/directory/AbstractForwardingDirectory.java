/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.RemoteDirectory
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
 *  com.atlassian.crowd.manager.avatar.AvatarReference
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.group.GroupTemplate
 *  com.atlassian.crowd.model.group.GroupWithAttributes
 *  com.atlassian.crowd.model.group.Membership
 *  com.atlassian.crowd.model.user.User
 *  com.atlassian.crowd.model.user.UserTemplate
 *  com.atlassian.crowd.model.user.UserTemplateWithAttributes
 *  com.atlassian.crowd.model.user.UserWithAttributes
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 *  com.atlassian.crowd.search.query.membership.MembershipQuery
 *  com.atlassian.crowd.util.BoundedCount
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.crowd.directory;

import com.atlassian.crowd.directory.RemoteDirectory;
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
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public abstract class AbstractForwardingDirectory
implements RemoteDirectory {
    public long getDirectoryId() {
        return this.getDelegate().getDirectoryId();
    }

    public void setDirectoryId(long directoryId) {
        this.getDelegate().setDirectoryId(directoryId);
    }

    @Nonnull
    public String getDescriptiveName() {
        return this.getDelegate().getDescriptiveName();
    }

    public void setAttributes(Map<String, String> attributes) {
        this.getDelegate().setAttributes(attributes);
    }

    @Nonnull
    public User findUserByName(String name) throws UserNotFoundException, OperationFailedException {
        return this.getDelegate().findUserByName(name);
    }

    @Nonnull
    public UserWithAttributes findUserWithAttributesByName(String name) throws UserNotFoundException, OperationFailedException {
        return this.getDelegate().findUserWithAttributesByName(name);
    }

    @Nonnull
    public User findUserByExternalId(String externalId) throws UserNotFoundException, OperationFailedException {
        return this.getDelegate().findUserByExternalId(externalId);
    }

    @Nonnull
    public User authenticate(String name, PasswordCredential credential) throws UserNotFoundException, InactiveAccountException, InvalidAuthenticationException, ExpiredCredentialException, OperationFailedException {
        return this.getDelegate().authenticate(name, credential);
    }

    @Nonnull
    public User addUser(UserTemplate user, PasswordCredential credential) throws InvalidUserException, InvalidCredentialException, UserAlreadyExistsException, OperationFailedException {
        return this.addUser(UserTemplateWithAttributes.toUserWithNoAttributes((User)user), credential);
    }

    public UserWithAttributes addUser(UserTemplateWithAttributes user, PasswordCredential credential) throws InvalidUserException, InvalidCredentialException, UserAlreadyExistsException, OperationFailedException {
        return this.getDelegate().addUser(user, credential);
    }

    @Nonnull
    public User updateUser(UserTemplate user) throws InvalidUserException, UserNotFoundException, OperationFailedException {
        return this.getDelegate().updateUser(user);
    }

    public void updateUserCredential(String username, PasswordCredential credential) throws UserNotFoundException, InvalidCredentialException, OperationFailedException {
        this.getDelegate().updateUserCredential(username, credential);
    }

    @Nonnull
    public User renameUser(String oldName, String newName) throws UserNotFoundException, InvalidUserException, UserAlreadyExistsException, OperationFailedException {
        return this.getDelegate().renameUser(oldName, newName);
    }

    public void storeUserAttributes(String username, Map<String, Set<String>> attributes) throws UserNotFoundException, OperationFailedException {
        this.getDelegate().storeUserAttributes(username, attributes);
    }

    public void removeUserAttributes(String username, String attributeName) throws UserNotFoundException, OperationFailedException {
        this.getDelegate().removeUserAttributes(username, attributeName);
    }

    public void removeUser(String name) throws UserNotFoundException, OperationFailedException {
        this.getDelegate().removeUser(name);
    }

    @Nonnull
    public <T> List<T> searchUsers(EntityQuery<T> query) throws OperationFailedException {
        return this.getDelegate().searchUsers(query);
    }

    @Nonnull
    public Group findGroupByName(String name) throws GroupNotFoundException, OperationFailedException {
        return this.getDelegate().findGroupByName(name);
    }

    @Nonnull
    public GroupWithAttributes findGroupWithAttributesByName(String name) throws GroupNotFoundException, OperationFailedException {
        return this.getDelegate().findGroupWithAttributesByName(name);
    }

    @Nonnull
    public Group addGroup(GroupTemplate group) throws InvalidGroupException, OperationFailedException {
        return this.getDelegate().addGroup(group);
    }

    @Nonnull
    public Group updateGroup(GroupTemplate group) throws InvalidGroupException, GroupNotFoundException, ReadOnlyGroupException, OperationFailedException {
        return this.getDelegate().updateGroup(group);
    }

    @Nonnull
    public Group renameGroup(String oldName, String newName) throws GroupNotFoundException, InvalidGroupException, OperationFailedException {
        return this.getDelegate().renameGroup(oldName, newName);
    }

    public void storeGroupAttributes(String groupName, Map<String, Set<String>> attributes) throws GroupNotFoundException, OperationFailedException {
        this.getDelegate().storeGroupAttributes(groupName, attributes);
    }

    public void removeGroupAttributes(String groupName, String attributeName) throws GroupNotFoundException, OperationFailedException {
        this.getDelegate().removeGroupAttributes(groupName, attributeName);
    }

    public void removeGroup(String name) throws GroupNotFoundException, ReadOnlyGroupException, OperationFailedException {
        this.getDelegate().removeGroup(name);
    }

    @Nonnull
    public <T> List<T> searchGroups(EntityQuery<T> query) throws OperationFailedException {
        return this.getDelegate().searchGroups(query);
    }

    public boolean isUserDirectGroupMember(String username, String groupName) throws OperationFailedException {
        return this.getDelegate().isUserDirectGroupMember(username, groupName);
    }

    public boolean isGroupDirectGroupMember(String childGroup, String parentGroup) throws OperationFailedException {
        return this.getDelegate().isGroupDirectGroupMember(childGroup, parentGroup);
    }

    @Nonnull
    public BoundedCount countDirectMembersOfGroup(String groupName, int querySizeHint) throws OperationFailedException {
        return this.getDelegate().countDirectMembersOfGroup(groupName, querySizeHint);
    }

    public void addUserToGroup(String username, String groupName) throws GroupNotFoundException, UserNotFoundException, ReadOnlyGroupException, OperationFailedException, MembershipAlreadyExistsException {
        this.getDelegate().addUserToGroup(username, groupName);
    }

    public void addGroupToGroup(String childGroup, String parentGroup) throws GroupNotFoundException, InvalidMembershipException, ReadOnlyGroupException, OperationFailedException, MembershipAlreadyExistsException {
        this.getDelegate().addGroupToGroup(childGroup, parentGroup);
    }

    public void removeUserFromGroup(String username, String groupName) throws GroupNotFoundException, UserNotFoundException, MembershipNotFoundException, ReadOnlyGroupException, OperationFailedException {
        this.getDelegate().removeUserFromGroup(username, groupName);
    }

    public void removeGroupFromGroup(String childGroup, String parentGroup) throws GroupNotFoundException, InvalidMembershipException, MembershipNotFoundException, ReadOnlyGroupException, OperationFailedException {
        this.getDelegate().removeGroupFromGroup(childGroup, parentGroup);
    }

    @Nonnull
    public <T> List<T> searchGroupRelationships(MembershipQuery<T> query) throws OperationFailedException {
        return this.getDelegate().searchGroupRelationships(query);
    }

    public void testConnection() throws OperationFailedException {
        this.getDelegate().testConnection();
    }

    public boolean supportsInactiveAccounts() {
        return this.getDelegate().supportsInactiveAccounts();
    }

    public boolean supportsNestedGroups() {
        return this.getDelegate().supportsNestedGroups();
    }

    public boolean supportsPasswordExpiration() {
        return this.getDelegate().supportsPasswordExpiration();
    }

    public boolean supportsSettingEncryptedCredential() {
        return this.getDelegate().supportsSettingEncryptedCredential();
    }

    public boolean isRolesDisabled() {
        return this.getDelegate().isRolesDisabled();
    }

    @Nonnull
    public Iterable<Membership> getMemberships() throws OperationFailedException {
        return this.getDelegate().getMemberships();
    }

    @Nonnull
    public RemoteDirectory getAuthoritativeDirectory() {
        return this.getDelegate().getAuthoritativeDirectory();
    }

    public void expireAllPasswords() throws OperationFailedException {
        this.getDelegate().expireAllPasswords();
    }

    @Nullable
    public Set<String> getValues(String key) {
        return this.getDelegate().getValues(key);
    }

    public String getValue(String key) {
        return this.getDelegate().getValue(key);
    }

    public Set<String> getKeys() {
        return this.getDelegate().getKeys();
    }

    public boolean isEmpty() {
        return this.getDelegate().isEmpty();
    }

    public AvatarReference getUserAvatarByName(String username, int sizeHint) throws UserNotFoundException, OperationFailedException {
        return this.getDelegate().getUserAvatarByName(username, sizeHint);
    }

    public User updateUserFromRemoteDirectory(User remoteUser) throws OperationFailedException, UserNotFoundException {
        return this.getDelegate().updateUserFromRemoteDirectory(remoteUser);
    }

    protected abstract RemoteDirectory getDelegate();
}

