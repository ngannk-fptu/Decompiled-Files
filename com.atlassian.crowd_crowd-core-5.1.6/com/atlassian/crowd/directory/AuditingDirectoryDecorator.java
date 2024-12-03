/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.crowd.audit.AuditLogChangeset
 *  com.atlassian.crowd.audit.AuditLogEntityType
 *  com.atlassian.crowd.audit.AuditLogEntry
 *  com.atlassian.crowd.audit.AuditLogEventType
 *  com.atlassian.crowd.audit.ImmutableAuditLogChangeset
 *  com.atlassian.crowd.audit.ImmutableAuditLogChangeset$Builder
 *  com.atlassian.crowd.audit.ImmutableAuditLogEntity
 *  com.atlassian.crowd.audit.ImmutableAuditLogEntity$Builder
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
 *  com.atlassian.crowd.manager.audit.AuditService
 *  com.atlassian.crowd.manager.audit.mapper.AuditLogGroupMapper
 *  com.atlassian.crowd.manager.audit.mapper.AuditLogUserMapper
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
 */
package com.atlassian.crowd.directory;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.crowd.audit.AuditLogChangeset;
import com.atlassian.crowd.audit.AuditLogEntityType;
import com.atlassian.crowd.audit.AuditLogEntry;
import com.atlassian.crowd.audit.AuditLogEventType;
import com.atlassian.crowd.audit.ImmutableAuditLogChangeset;
import com.atlassian.crowd.audit.ImmutableAuditLogEntity;
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
import com.atlassian.crowd.manager.audit.AuditService;
import com.atlassian.crowd.manager.audit.mapper.AuditLogGroupMapper;
import com.atlassian.crowd.manager.audit.mapper.AuditLogUserMapper;
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

public class AuditingDirectoryDecorator
implements RemoteDirectory {
    private final RemoteDirectory remoteDirectory;
    private final AuditService auditService;
    private final ImmutableAuditLogEntity directoryEntity;
    private final AuditLogUserMapper auditLogUserMapper;
    private final AuditLogGroupMapper auditLogGroupMapper;

    public AuditingDirectoryDecorator(RemoteDirectory remoteDirectory, AuditService auditService, AuditLogUserMapper auditLogUserMapper, AuditLogGroupMapper auditLogGroupMapper, String directoryName) {
        this.remoteDirectory = remoteDirectory;
        this.auditService = auditService;
        this.auditLogUserMapper = auditLogUserMapper;
        this.auditLogGroupMapper = auditLogGroupMapper;
        this.directoryEntity = new ImmutableAuditLogEntity.Builder().setEntityId(Long.valueOf(remoteDirectory.getDirectoryId())).setEntityName(directoryName).setEntityType(AuditLogEntityType.DIRECTORY).build();
    }

    public void addUserToGroup(String username, String groupName) throws GroupNotFoundException, UserNotFoundException, ReadOnlyGroupException, OperationFailedException, MembershipAlreadyExistsException {
        this.remoteDirectory.addUserToGroup(username, groupName);
        this.auditMembershipEvent(AuditLogEventType.ADDED_TO_GROUP, groupName, username, AuditLogEntityType.USER);
    }

    public void addGroupToGroup(String childGroup, String parentGroup) throws GroupNotFoundException, InvalidMembershipException, ReadOnlyGroupException, OperationFailedException, MembershipAlreadyExistsException {
        this.remoteDirectory.addGroupToGroup(childGroup, parentGroup);
        this.auditMembershipEvent(AuditLogEventType.ADDED_TO_GROUP, parentGroup, childGroup, AuditLogEntityType.GROUP);
    }

    public void removeUserFromGroup(String username, String groupName) throws GroupNotFoundException, UserNotFoundException, MembershipNotFoundException, ReadOnlyGroupException, OperationFailedException {
        this.remoteDirectory.removeUserFromGroup(username, groupName);
        this.auditMembershipEvent(AuditLogEventType.REMOVED_FROM_GROUP, groupName, username, AuditLogEntityType.USER);
    }

    public void removeGroupFromGroup(String childGroup, String parentGroup) throws GroupNotFoundException, InvalidMembershipException, MembershipNotFoundException, ReadOnlyGroupException, OperationFailedException {
        this.remoteDirectory.removeGroupFromGroup(childGroup, parentGroup);
        this.auditMembershipEvent(AuditLogEventType.REMOVED_FROM_GROUP, parentGroup, childGroup, AuditLogEntityType.GROUP);
    }

    @Nonnull
    public <T> List<T> searchGroupRelationships(MembershipQuery<T> query) throws OperationFailedException {
        return this.remoteDirectory.searchGroupRelationships(query);
    }

    public void testConnection() throws OperationFailedException {
        this.remoteDirectory.testConnection();
    }

    public boolean supportsInactiveAccounts() {
        return this.remoteDirectory.supportsInactiveAccounts();
    }

    public boolean supportsNestedGroups() {
        return this.remoteDirectory.supportsNestedGroups();
    }

    public boolean supportsPasswordExpiration() {
        return this.remoteDirectory.supportsPasswordExpiration();
    }

    public boolean supportsSettingEncryptedCredential() {
        return this.remoteDirectory.supportsSettingEncryptedCredential();
    }

    public boolean isRolesDisabled() {
        return this.remoteDirectory.isRolesDisabled();
    }

    @Nonnull
    public Iterable<Membership> getMemberships() throws OperationFailedException {
        return this.remoteDirectory.getMemberships();
    }

    @Nonnull
    public RemoteDirectory getAuthoritativeDirectory() {
        return this.remoteDirectory.getAuthoritativeDirectory();
    }

    public void expireAllPasswords() throws OperationFailedException {
        this.remoteDirectory.expireAllPasswords();
    }

    @Nullable
    public AvatarReference getUserAvatarByName(String username, int sizeHint) throws UserNotFoundException, OperationFailedException {
        return this.remoteDirectory.getUserAvatarByName(username, sizeHint);
    }

    @ExperimentalApi
    public User updateUserFromRemoteDirectory(User remoteUser) throws OperationFailedException, UserNotFoundException {
        return this.remoteDirectory.updateUserFromRemoteDirectory(remoteUser);
    }

    @Nullable
    public Set<String> getValues(String key) {
        return this.remoteDirectory.getValues(key);
    }

    @Nullable
    public String getValue(String key) {
        return this.remoteDirectory.getValue(key);
    }

    public Set<String> getKeys() {
        return this.remoteDirectory.getKeys();
    }

    public boolean isEmpty() {
        return this.remoteDirectory.isEmpty();
    }

    @Nonnull
    public User addUser(UserTemplate user, PasswordCredential credential) throws InvalidUserException, InvalidCredentialException, UserAlreadyExistsException, OperationFailedException {
        User addedUser = this.remoteDirectory.addUser(user, credential);
        this.auditUserEvent(AuditLogEventType.USER_CREATED, user.getName(), this.auditLogUserMapper.calculateDifference(AuditLogEventType.USER_CREATED, null, (User)user));
        return addedUser;
    }

    public UserWithAttributes addUser(UserTemplateWithAttributes user, PasswordCredential credential) throws InvalidUserException, InvalidCredentialException, UserAlreadyExistsException, OperationFailedException {
        UserWithAttributes addedUser = this.remoteDirectory.addUser(user, credential);
        this.auditUserEvent(AuditLogEventType.USER_CREATED, user.getName(), this.auditLogUserMapper.calculateDifference(AuditLogEventType.USER_CREATED, null, (User)user));
        return addedUser;
    }

    @Nonnull
    public User updateUser(UserTemplate user) throws InvalidUserException, UserNotFoundException, OperationFailedException {
        User oldUser = this.findUserByName(user.getName());
        User updatedUser = this.remoteDirectory.updateUser(user);
        this.auditUserEvent(AuditLogEventType.USER_UPDATED, user.getName(), this.auditLogUserMapper.calculateDifference(AuditLogEventType.USER_UPDATED, oldUser, (User)user));
        return updatedUser;
    }

    @Nonnull
    public User renameUser(String oldName, String newName) throws UserNotFoundException, InvalidUserException, UserAlreadyExistsException, OperationFailedException {
        User userToRename = this.findUserByName(oldName);
        User renamedUser = this.remoteDirectory.renameUser(oldName, newName);
        this.auditUserEvent(AuditLogEventType.USER_UPDATED, newName, this.auditLogUserMapper.calculateDifference(AuditLogEventType.USER_DELETED, userToRename, renamedUser));
        return renamedUser;
    }

    public void storeUserAttributes(String username, Map<String, Set<String>> attributes) throws UserNotFoundException, OperationFailedException {
        this.remoteDirectory.storeUserAttributes(username, attributes);
    }

    public void removeUserAttributes(String username, String attributeName) throws UserNotFoundException, OperationFailedException {
        this.remoteDirectory.removeUserAttributes(username, attributeName);
    }

    public void removeUser(String name) throws UserNotFoundException, OperationFailedException {
        User userToRemove = this.findUserByName(name);
        this.remoteDirectory.removeUser(name);
        this.auditUserEvent(AuditLogEventType.USER_DELETED, name, this.auditLogUserMapper.calculateDifference(AuditLogEventType.USER_DELETED, userToRemove, null));
    }

    @Nonnull
    public <T> List<T> searchUsers(EntityQuery<T> query) throws OperationFailedException {
        return this.remoteDirectory.searchUsers(query);
    }

    @Nonnull
    public Group findGroupByName(String name) throws GroupNotFoundException, OperationFailedException {
        return this.remoteDirectory.findGroupByName(name);
    }

    @Nonnull
    public GroupWithAttributes findGroupWithAttributesByName(String name) throws GroupNotFoundException, OperationFailedException {
        return this.remoteDirectory.findGroupWithAttributesByName(name);
    }

    public void updateUserCredential(String username, PasswordCredential credential) throws UserNotFoundException, InvalidCredentialException, OperationFailedException {
        this.remoteDirectory.updateUserCredential(username, credential);
        this.auditLogUpdateUserCredential(username);
    }

    @Nonnull
    public Group addGroup(GroupTemplate group) throws InvalidGroupException, OperationFailedException {
        Group addedGroup = this.remoteDirectory.addGroup(group);
        this.auditLogGroupOperation(group.getName(), this.auditLogGroupMapper.calculateDifference(null, addedGroup), AuditLogEventType.GROUP_CREATED);
        return addedGroup;
    }

    public void removeGroup(String name) throws GroupNotFoundException, ReadOnlyGroupException, OperationFailedException {
        Group groupToRemove = this.remoteDirectory.findGroupByName(name);
        this.remoteDirectory.removeGroup(name);
        this.auditLogGroupOperation(name, this.auditLogGroupMapper.calculateDifference(groupToRemove, null), AuditLogEventType.GROUP_DELETED);
    }

    @Nonnull
    public <T> List<T> searchGroups(EntityQuery<T> query) throws OperationFailedException {
        return this.remoteDirectory.searchGroups(query);
    }

    public boolean isUserDirectGroupMember(String username, String groupName) throws OperationFailedException {
        return this.remoteDirectory.isUserDirectGroupMember(username, groupName);
    }

    public boolean isGroupDirectGroupMember(String childGroup, String parentGroup) throws OperationFailedException {
        return this.remoteDirectory.isGroupDirectGroupMember(childGroup, parentGroup);
    }

    @Nonnull
    public BoundedCount countDirectMembersOfGroup(String groupName, int querySizeHint) throws OperationFailedException {
        return this.remoteDirectory.countDirectMembersOfGroup(groupName, querySizeHint);
    }

    @Nonnull
    public Group updateGroup(GroupTemplate group) throws InvalidGroupException, GroupNotFoundException, ReadOnlyGroupException, OperationFailedException {
        Group groupBeforeUpdate = this.remoteDirectory.findGroupByName(group.getName());
        Group updatedGroup = this.remoteDirectory.updateGroup(group);
        this.auditLogGroupOperation(group.getName(), this.auditLogGroupMapper.calculateDifference(groupBeforeUpdate, updatedGroup), AuditLogEventType.GROUP_UPDATED);
        return updatedGroup;
    }

    @Nonnull
    public Group renameGroup(String oldName, String newName) throws GroupNotFoundException, InvalidGroupException, OperationFailedException {
        return this.remoteDirectory.renameGroup(oldName, newName);
    }

    public void storeGroupAttributes(String groupName, Map<String, Set<String>> attributes) throws GroupNotFoundException, OperationFailedException {
        this.remoteDirectory.storeGroupAttributes(groupName, attributes);
    }

    public void removeGroupAttributes(String groupName, String attributeName) throws GroupNotFoundException, OperationFailedException {
        this.remoteDirectory.removeGroupAttributes(groupName, attributeName);
    }

    public long getDirectoryId() {
        return this.remoteDirectory.getDirectoryId();
    }

    public void setDirectoryId(long directoryId) {
        this.remoteDirectory.setDirectoryId(directoryId);
    }

    @Nonnull
    public String getDescriptiveName() {
        return this.remoteDirectory.getDescriptiveName();
    }

    public void setAttributes(Map<String, String> attributes) {
        this.remoteDirectory.setAttributes(attributes);
    }

    @Nonnull
    public User findUserByName(String name) throws UserNotFoundException, OperationFailedException {
        return this.remoteDirectory.findUserByName(name);
    }

    @Nonnull
    public UserWithAttributes findUserWithAttributesByName(String name) throws UserNotFoundException, OperationFailedException {
        return this.remoteDirectory.findUserWithAttributesByName(name);
    }

    @Nonnull
    public User findUserByExternalId(String externalId) throws UserNotFoundException, OperationFailedException {
        return this.remoteDirectory.findUserByExternalId(externalId);
    }

    @Nonnull
    public User authenticate(String name, PasswordCredential credential) throws UserNotFoundException, InactiveAccountException, InvalidAuthenticationException, ExpiredCredentialException, OperationFailedException {
        return this.remoteDirectory.authenticate(name, credential);
    }

    private void auditLogUpdateUserCredential(String username) {
        ImmutableAuditLogEntity primaryUserEntity = new ImmutableAuditLogEntity.Builder().setPrimary().setEntityName(username).setEntityType(AuditLogEntityType.USER).build();
        ImmutableAuditLogChangeset auditLogChangeset = new ImmutableAuditLogChangeset.Builder().setEventType(AuditLogEventType.PASSWORD_CHANGED).addEntity(primaryUserEntity).addEntity(this.directoryEntity).addEntry(this.auditLogUserMapper.calculatePasswordDiff()).build();
        this.auditService.saveAudit((AuditLogChangeset)auditLogChangeset);
    }

    private void auditMembershipEvent(AuditLogEventType eventType, String parentName, String childName, AuditLogEntityType childType) {
        ImmutableAuditLogEntity parentGroupEntity = new ImmutableAuditLogEntity.Builder().setEntityName(parentName).setEntityType(AuditLogEntityType.GROUP).setPrimary().build();
        ImmutableAuditLogEntity childEntity = new ImmutableAuditLogEntity.Builder().setEntityName(childName).setEntityType(childType).build();
        this.auditService.saveAudit((AuditLogChangeset)new ImmutableAuditLogChangeset.Builder().setEventType(eventType).addEntity(parentGroupEntity).addEntity(childEntity).addEntity(this.directoryEntity).build());
    }

    private void auditUserEvent(AuditLogEventType eventType, String name, List<AuditLogEntry> entries) {
        if (entries.size() == 0) {
            return;
        }
        ImmutableAuditLogEntity userEntity = new ImmutableAuditLogEntity.Builder().setEntityName(name).setEntityType(AuditLogEntityType.USER).setPrimary().build();
        this.auditService.saveAudit((AuditLogChangeset)new ImmutableAuditLogChangeset.Builder().setEventType(eventType).addEntity(userEntity).addEntity(this.directoryEntity).addEntries(entries).build());
    }

    private void auditLogGroupOperation(String groupName, List<AuditLogEntry> diffEntries, AuditLogEventType eventType) {
        if (diffEntries.size() == 0) {
            return;
        }
        ImmutableAuditLogEntity primaryGroupEntity = new ImmutableAuditLogEntity.Builder().setEntityName(groupName).setEntityType(AuditLogEntityType.GROUP).setPrimary().build();
        ImmutableAuditLogChangeset auditLogChangeset = new ImmutableAuditLogChangeset.Builder().addEntity(primaryGroupEntity).addEntity(this.directoryEntity).setEventType(eventType).addEntries(diffEntries).build();
        this.auditService.saveAudit((AuditLogChangeset)auditLogChangeset);
    }
}

