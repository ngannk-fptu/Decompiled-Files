/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.RemoteDirectory
 *  com.atlassian.crowd.embedded.api.PasswordCredential
 *  com.atlassian.crowd.embedded.impl.IdentifierUtils
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
 *  com.atlassian.crowd.search.EntityDescriptor
 *  com.atlassian.crowd.search.builder.QueryBuilder
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 *  com.atlassian.crowd.search.query.membership.MembershipQuery
 *  com.atlassian.crowd.util.BoundedCount
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableMap
 */
package com.atlassian.crowd.manager.recovery;

import com.atlassian.crowd.directory.RemoteDirectory;
import com.atlassian.crowd.embedded.api.PasswordCredential;
import com.atlassian.crowd.embedded.impl.IdentifierUtils;
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
import com.atlassian.crowd.manager.recovery.RecoveryModeDirectory;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.GroupTemplate;
import com.atlassian.crowd.model.group.GroupWithAttributes;
import com.atlassian.crowd.model.group.Membership;
import com.atlassian.crowd.model.user.User;
import com.atlassian.crowd.model.user.UserTemplate;
import com.atlassian.crowd.model.user.UserTemplateWithAttributes;
import com.atlassian.crowd.model.user.UserWithAttributes;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.builder.QueryBuilder;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import com.atlassian.crowd.search.query.membership.MembershipQuery;
import com.atlassian.crowd.util.BoundedCount;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RecoveryModeRemoteDirectory
implements RemoteDirectory {
    private final long id;
    private final Map<String, String> attributes;
    private final String username;
    private final String password;

    public RecoveryModeRemoteDirectory(RecoveryModeDirectory directory) {
        Preconditions.checkNotNull((Object)directory, (Object)"directory");
        this.id = (Long)Preconditions.checkNotNull((Object)directory.getId(), (Object)"id");
        this.attributes = ImmutableMap.copyOf(directory.getAttributes());
        this.username = directory.getRecoveryUsername();
        this.password = directory.getRecoveryPassword();
    }

    public long getDirectoryId() {
        return this.id;
    }

    public void setDirectoryId(long directoryId) {
        throw new UnsupportedOperationException("Modifying ID is not supported");
    }

    public String getDescriptiveName() {
        return "Recovery Mode Remote Directory";
    }

    public void setAttributes(Map<String, String> attributes) {
        throw new UnsupportedOperationException("Modifying attributes is not supported");
    }

    public User authenticate(String name, PasswordCredential credential) throws UserNotFoundException, InactiveAccountException, InvalidAuthenticationException, ExpiredCredentialException, OperationFailedException {
        if (credential.isEncryptedCredential()) {
            throw InvalidAuthenticationException.newInstanceWithName((String)name);
        }
        if (IdentifierUtils.equalsInLowerCase((String)this.username, (String)name)) {
            if (this.password.equals(credential.getCredential())) {
                return this.findUserWithAttributesByName(name);
            }
            throw new InvalidAuthenticationException("Invalid password credential");
        }
        throw new UserNotFoundException(name);
    }

    public User findUserByName(String name) throws UserNotFoundException, OperationFailedException {
        return this.findUserWithAttributesByName(name);
    }

    public UserWithAttributes findUserWithAttributesByName(String name) throws UserNotFoundException, OperationFailedException {
        if (IdentifierUtils.equalsInLowerCase((String)this.username, (String)name)) {
            return this.createRecoveryUser();
        }
        throw new UserNotFoundException(name);
    }

    public User findUserByExternalId(String externalId) throws UserNotFoundException, OperationFailedException {
        throw new OperationFailedException("Not supported");
    }

    public User addUser(UserTemplate user, PasswordCredential credential) throws InvalidUserException, InvalidCredentialException, UserAlreadyExistsException, OperationFailedException {
        throw new OperationFailedException("This is an immutable directory");
    }

    public UserWithAttributes addUser(UserTemplateWithAttributes user, PasswordCredential credential) throws InvalidUserException, InvalidCredentialException, UserAlreadyExistsException, OperationFailedException {
        throw new OperationFailedException("This is an immutable directory");
    }

    public User updateUser(UserTemplate user) throws InvalidUserException, UserNotFoundException, OperationFailedException {
        throw new OperationFailedException("This is an immutable directory");
    }

    public void updateUserCredential(String username, PasswordCredential credential) throws UserNotFoundException, InvalidCredentialException, OperationFailedException {
        throw new OperationFailedException("This is an immutable directory");
    }

    public User renameUser(String oldName, String newName) throws UserNotFoundException, InvalidUserException, UserAlreadyExistsException, OperationFailedException {
        throw new OperationFailedException("This is an immutable directory");
    }

    public void storeUserAttributes(String username, Map<String, Set<String>> attributes) throws UserNotFoundException, OperationFailedException {
        throw new OperationFailedException("This is an immutable directory");
    }

    public void removeUserAttributes(String username, String attributeName) throws UserNotFoundException, OperationFailedException {
        throw new OperationFailedException("This is an immutable directory");
    }

    public void removeUser(String name) throws UserNotFoundException, OperationFailedException {
        throw new OperationFailedException("This is an immutable directory");
    }

    public <T> List<T> searchUsers(EntityQuery<T> query) throws OperationFailedException {
        return Collections.emptyList();
    }

    public Group findGroupByName(String name) throws GroupNotFoundException, OperationFailedException {
        throw new GroupNotFoundException(name);
    }

    public GroupWithAttributes findGroupWithAttributesByName(String name) throws GroupNotFoundException, OperationFailedException {
        throw new GroupNotFoundException(name);
    }

    public Group addGroup(GroupTemplate group) throws InvalidGroupException, OperationFailedException {
        throw new OperationFailedException("This is an immutable directory");
    }

    public Group updateGroup(GroupTemplate group) throws InvalidGroupException, GroupNotFoundException, ReadOnlyGroupException, OperationFailedException {
        throw new OperationFailedException("This is an immutable directory");
    }

    public Group renameGroup(String oldName, String newName) throws GroupNotFoundException, InvalidGroupException, OperationFailedException {
        throw new OperationFailedException("This is an immutable directory");
    }

    public void storeGroupAttributes(String groupName, Map<String, Set<String>> attributes) throws GroupNotFoundException, OperationFailedException {
        throw new OperationFailedException("This is an immutable directory");
    }

    public void removeGroupAttributes(String groupName, String attributeName) throws GroupNotFoundException, OperationFailedException {
        throw new OperationFailedException("This is an immutable directory");
    }

    public void removeGroup(String name) throws GroupNotFoundException, ReadOnlyGroupException, OperationFailedException {
        throw new OperationFailedException("This is an immutable directory");
    }

    public <T> List<T> searchGroups(EntityQuery<T> query) throws OperationFailedException {
        return Collections.emptyList();
    }

    public boolean isUserDirectGroupMember(String username, String groupName) throws OperationFailedException {
        return false;
    }

    public boolean isGroupDirectGroupMember(String childGroup, String parentGroup) throws OperationFailedException {
        return false;
    }

    public BoundedCount countDirectMembersOfGroup(String groupName, int querySizeHint) throws OperationFailedException {
        MembershipQuery membershipQuery = QueryBuilder.queryFor(String.class, (EntityDescriptor)EntityDescriptor.user()).childrenOf(EntityDescriptor.group()).withName(groupName).startingAt(0).returningAtMost(querySizeHint);
        return BoundedCount.fromCountedItemsAndLimit((long)this.searchGroupRelationships(membershipQuery).size(), (long)querySizeHint);
    }

    public void addUserToGroup(String username, String groupName) throws GroupNotFoundException, UserNotFoundException, ReadOnlyGroupException, OperationFailedException, MembershipAlreadyExistsException {
        throw new OperationFailedException("This is an immutable directory");
    }

    public void addGroupToGroup(String childGroup, String parentGroup) throws GroupNotFoundException, InvalidMembershipException, ReadOnlyGroupException, OperationFailedException, MembershipAlreadyExistsException {
        throw new OperationFailedException("This is an immutable directory");
    }

    public void removeUserFromGroup(String username, String groupName) throws GroupNotFoundException, UserNotFoundException, MembershipNotFoundException, ReadOnlyGroupException, OperationFailedException {
        throw new OperationFailedException("This is an immutable directory");
    }

    public void removeGroupFromGroup(String childGroup, String parentGroup) throws GroupNotFoundException, InvalidMembershipException, MembershipNotFoundException, ReadOnlyGroupException, OperationFailedException {
        throw new OperationFailedException("This is an immutable directory");
    }

    public void expireAllPasswords() throws OperationFailedException {
        throw new OperationFailedException("This is an immutable directory");
    }

    public <T> List<T> searchGroupRelationships(MembershipQuery<T> query) throws OperationFailedException {
        return Collections.emptyList();
    }

    public void testConnection() throws OperationFailedException {
    }

    public boolean supportsInactiveAccounts() {
        return false;
    }

    public boolean supportsNestedGroups() {
        return false;
    }

    public boolean supportsPasswordExpiration() {
        return false;
    }

    public boolean supportsSettingEncryptedCredential() {
        return false;
    }

    public boolean isRolesDisabled() {
        return true;
    }

    public Iterable<Membership> getMemberships() throws OperationFailedException {
        return Collections.emptyList();
    }

    public RemoteDirectory getAuthoritativeDirectory() {
        return this;
    }

    public Set<String> getValues(String key) {
        return this.attributes.containsKey(key) ? Collections.singleton(this.attributes.get(key)) : null;
    }

    public String getValue(String key) {
        return this.attributes.get(key);
    }

    public Set<String> getKeys() {
        return this.attributes.keySet();
    }

    public boolean isEmpty() {
        return this.attributes.isEmpty();
    }

    private UserWithAttributes createRecoveryUser() {
        UserTemplateWithAttributes recoveryUser = new UserTemplateWithAttributes(this.username, this.id);
        recoveryUser.setActive(true);
        recoveryUser.setDisplayName("Recovery Admin User");
        recoveryUser.setEmailAddress("@");
        return recoveryUser;
    }

    public AvatarReference getUserAvatarByName(String username, int sizeHint) throws OperationFailedException {
        return null;
    }
}

