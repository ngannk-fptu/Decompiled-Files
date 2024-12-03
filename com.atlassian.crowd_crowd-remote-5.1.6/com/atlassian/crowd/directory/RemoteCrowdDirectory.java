/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.AttributeValuesHolder
 *  com.atlassian.crowd.directory.DirectoryMembershipsIterable
 *  com.atlassian.crowd.directory.RemoteDirectory
 *  com.atlassian.crowd.embedded.api.Group
 *  com.atlassian.crowd.embedded.api.GroupWithAttributes
 *  com.atlassian.crowd.embedded.api.PasswordCredential
 *  com.atlassian.crowd.embedded.api.User
 *  com.atlassian.crowd.embedded.api.UserWithAttributes
 *  com.atlassian.crowd.event.EventTokenExpiredException
 *  com.atlassian.crowd.event.Events
 *  com.atlassian.crowd.event.IncrementalSynchronisationNotAvailableException
 *  com.atlassian.crowd.exception.ApplicationPermissionException
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
 *  com.atlassian.crowd.exception.OperationNotSupportedException
 *  com.atlassian.crowd.exception.UnsupportedCrowdApiException
 *  com.atlassian.crowd.exception.UserNotFoundException
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.group.GroupTemplate
 *  com.atlassian.crowd.model.group.GroupTemplateWithAttributes
 *  com.atlassian.crowd.model.group.GroupType
 *  com.atlassian.crowd.model.group.GroupWithAttributes
 *  com.atlassian.crowd.model.group.Membership
 *  com.atlassian.crowd.model.user.User
 *  com.atlassian.crowd.model.user.UserTemplate
 *  com.atlassian.crowd.model.user.UserTemplateWithAttributes
 *  com.atlassian.crowd.model.user.UserWithAttributes
 *  com.atlassian.crowd.search.Entity
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 *  com.atlassian.crowd.search.query.membership.MembershipQuery
 *  com.atlassian.crowd.service.client.ClientProperties
 *  com.atlassian.crowd.service.client.ClientPropertiesImpl
 *  com.atlassian.crowd.service.client.CrowdClient
 *  com.atlassian.crowd.service.factory.CrowdClientFactory
 *  com.atlassian.crowd.util.BoundedCount
 *  com.atlassian.util.concurrent.LazyReference
 *  com.atlassian.util.concurrent.LazyReference$InitializationException
 *  org.apache.commons.lang3.Validate
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.directory;

import com.atlassian.crowd.directory.AttributeValuesHolder;
import com.atlassian.crowd.directory.DirectoryMembershipsIterable;
import com.atlassian.crowd.directory.RemoteDirectory;
import com.atlassian.crowd.embedded.api.Group;
import com.atlassian.crowd.embedded.api.GroupWithAttributes;
import com.atlassian.crowd.embedded.api.PasswordCredential;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.crowd.embedded.api.UserWithAttributes;
import com.atlassian.crowd.event.EventTokenExpiredException;
import com.atlassian.crowd.event.Events;
import com.atlassian.crowd.event.IncrementalSynchronisationNotAvailableException;
import com.atlassian.crowd.exception.ApplicationPermissionException;
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
import com.atlassian.crowd.exception.OperationNotSupportedException;
import com.atlassian.crowd.exception.UnsupportedCrowdApiException;
import com.atlassian.crowd.exception.UserNotFoundException;
import com.atlassian.crowd.model.group.GroupTemplate;
import com.atlassian.crowd.model.group.GroupTemplateWithAttributes;
import com.atlassian.crowd.model.group.GroupType;
import com.atlassian.crowd.model.group.Membership;
import com.atlassian.crowd.model.user.UserTemplate;
import com.atlassian.crowd.model.user.UserTemplateWithAttributes;
import com.atlassian.crowd.search.Entity;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import com.atlassian.crowd.search.query.membership.MembershipQuery;
import com.atlassian.crowd.service.client.ClientProperties;
import com.atlassian.crowd.service.client.ClientPropertiesImpl;
import com.atlassian.crowd.service.client.CrowdClient;
import com.atlassian.crowd.service.factory.CrowdClientFactory;
import com.atlassian.crowd.util.BoundedCount;
import com.atlassian.util.concurrent.LazyReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoteCrowdDirectory
implements RemoteDirectory {
    private static final Logger logger = LoggerFactory.getLogger(RemoteCrowdDirectory.class);
    public static final String DESCRIPTIVE_NAME = "Remote Crowd Directory";
    public static final String APPLICATION_NAME = "application.name";
    public static final String APPLICATION_PASSWORD = "application.password";
    public static final String CROWD_SERVER_URL = "crowd.server.url";
    public static final String AUTHENTICATION_METHOD_ATTRIBUTE = "authentication.method";
    public static final String CROWD_HTTP_TIMEOUT = "crowd.server.http.timeout";
    public static final String CROWD_HTTP_MAX_CONNECTIONS = "crowd.server.http.max.connections";
    public static final String CROWD_HTTP_PROXY_HOST = "crowd.server.http.proxy.host";
    public static final String CROWD_HTTP_PROXY_PORT = "crowd.server.http.proxy.port";
    public static final String CROWD_HTTP_PROXY_USERNAME = "crowd.server.http.proxy.username";
    public static final String CROWD_HTTP_PROXY_PASSWORD = "crowd.server.http.proxy.password";
    private final CrowdClientFactory crowdClientFactory;
    private long directoryId;
    protected AttributeValuesHolder attributes;
    private LazyReference<CrowdClient> crowdClientRef;

    public RemoteCrowdDirectory(CrowdClientFactory crowdClientFactory) {
        this.crowdClientFactory = crowdClientFactory;
    }

    public long getDirectoryId() {
        return this.directoryId;
    }

    public void setDirectoryId(long directoryId) {
        this.directoryId = directoryId;
    }

    public com.atlassian.crowd.model.user.User findUserByName(String name) throws UserNotFoundException, OperationFailedException {
        try {
            return this.buildUserWithDirectoryId(this.getCrowdClient().getUser(name));
        }
        catch (ApplicationPermissionException | InvalidAuthenticationException e) {
            throw new OperationFailedException(e);
        }
    }

    public com.atlassian.crowd.model.user.UserWithAttributes findUserWithAttributesByName(String name) throws UserNotFoundException, OperationFailedException {
        try {
            return this.buildUserWithDirectoryId(this.getCrowdClient().getUserWithAttributes(name));
        }
        catch (ApplicationPermissionException | InvalidAuthenticationException e) {
            throw new OperationFailedException(e);
        }
    }

    public com.atlassian.crowd.model.user.User findUserByExternalId(String externalId) throws UserNotFoundException, OperationFailedException {
        throw new UnsupportedOperationException("Not implemented yet for Crowd servers.");
    }

    public com.atlassian.crowd.model.user.User authenticate(String username, PasswordCredential credential) throws UserNotFoundException, InactiveAccountException, InvalidAuthenticationException, ExpiredCredentialException, OperationFailedException {
        if (credential.isEncryptedCredential()) {
            throw InvalidAuthenticationException.newInstanceWithName((String)username);
        }
        try {
            return this.buildUserWithDirectoryId(this.getCrowdClient().authenticateUser(username, credential.getCredential()));
        }
        catch (ApplicationPermissionException e) {
            throw new OperationFailedException((Throwable)e);
        }
    }

    public com.atlassian.crowd.model.user.User addUser(UserTemplate user, PasswordCredential credential) throws InvalidUserException, InvalidCredentialException, OperationFailedException {
        return this.addUser(UserTemplateWithAttributes.toUserWithNoAttributes((com.atlassian.crowd.model.user.User)user), credential);
    }

    public com.atlassian.crowd.model.user.UserWithAttributes addUser(UserTemplateWithAttributes user, PasswordCredential credential) throws InvalidUserException, InvalidCredentialException, OperationFailedException {
        Validate.notNull((Object)user, (String)"user cannot be null", (Object[])new Object[0]);
        Validate.notNull((Object)user.getName(), (String)"user.name cannot be null", (Object[])new Object[0]);
        UserTemplateWithAttributes userTemplate = new UserTemplateWithAttributes((com.atlassian.crowd.model.user.UserWithAttributes)user);
        userTemplate.setDirectoryId(-1L);
        try {
            this.getCrowdClient().addUser((com.atlassian.crowd.model.user.UserWithAttributes)userTemplate, credential);
        }
        catch (ApplicationPermissionException | InvalidAuthenticationException e) {
            throw new OperationFailedException(e);
        }
        try {
            return this.findUserWithAttributesByName(user.getName());
        }
        catch (UserNotFoundException e) {
            throw new OperationFailedException((Throwable)e);
        }
    }

    public void expireAllPasswords() throws OperationFailedException {
        try {
            this.getCrowdClient().expireAllPasswords();
        }
        catch (ApplicationPermissionException | InvalidAuthenticationException e) {
            throw new OperationFailedException(e);
        }
    }

    public com.atlassian.crowd.model.user.User updateUser(UserTemplate user) throws InvalidUserException, UserNotFoundException, OperationFailedException {
        Validate.notNull((Object)user, (String)"user cannot be null", (Object[])new Object[0]);
        Validate.notNull((Object)user.getName(), (String)"user.name cannot be null", (Object[])new Object[0]);
        UserTemplate userTemplate = new UserTemplate((com.atlassian.crowd.model.user.User)user);
        userTemplate.setDirectoryId(-1L);
        try {
            this.getCrowdClient().updateUser((com.atlassian.crowd.model.user.User)userTemplate);
        }
        catch (ApplicationPermissionException | InvalidAuthenticationException e) {
            throw new OperationFailedException(e);
        }
        return this.findUserByName(user.getName());
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public void updateUserCredential(String username, PasswordCredential credential) throws UserNotFoundException, InvalidCredentialException, OperationFailedException {
        String unencryptedPassword;
        if (credential.isEncryptedCredential()) {
            if (!PasswordCredential.NONE.equals((Object)credential)) throw new InvalidCredentialException("Updating a user's credential to an encrypted value is not supported");
            unencryptedPassword = null;
        } else {
            unencryptedPassword = credential.getCredential();
        }
        try {
            this.getCrowdClient().updateUserCredential(username, unencryptedPassword);
            return;
        }
        catch (ApplicationPermissionException | InvalidAuthenticationException e) {
            throw new OperationFailedException(e);
        }
    }

    public com.atlassian.crowd.model.user.User renameUser(String oldName, String newName) throws UserNotFoundException, InvalidUserException, OperationFailedException {
        try {
            return this.getCrowdClient().renameUser(oldName, newName);
        }
        catch (ApplicationPermissionException | InvalidAuthenticationException e) {
            throw new OperationFailedException(e);
        }
    }

    public void storeUserAttributes(String username, Map<String, Set<String>> attributes) throws UserNotFoundException, OperationFailedException {
        try {
            this.getCrowdClient().storeUserAttributes(username, attributes);
        }
        catch (ApplicationPermissionException | InvalidAuthenticationException e) {
            throw new OperationFailedException(e);
        }
    }

    public void removeUserAttributes(String username, String attributeName) throws UserNotFoundException, OperationFailedException {
        try {
            this.getCrowdClient().removeUserAttributes(username, attributeName);
        }
        catch (ApplicationPermissionException | InvalidAuthenticationException e) {
            throw new OperationFailedException(e);
        }
    }

    public void removeUser(String username) throws UserNotFoundException, OperationFailedException {
        try {
            this.getCrowdClient().removeUser(username);
        }
        catch (ApplicationPermissionException | InvalidAuthenticationException e) {
            throw new OperationFailedException(e);
        }
    }

    public <T> List<T> searchUsers(EntityQuery<T> query) throws OperationFailedException {
        if (query.getEntityDescriptor().getEntityType() != Entity.USER) {
            throw new IllegalArgumentException("Query is not a user query.");
        }
        try {
            Class returnType = query.getReturnType();
            if (String.class.equals((Object)returnType)) {
                return this.getCrowdClient().searchUserNames(query.getSearchRestriction(), query.getStartIndex(), query.getMaxResults());
            }
            if (com.atlassian.crowd.model.user.UserWithAttributes.class.isAssignableFrom(returnType) || UserWithAttributes.class.isAssignableFrom(returnType)) {
                List users = this.getCrowdClient().searchUsersWithAttributes(query.getSearchRestriction(), query.getStartIndex(), query.getMaxResults());
                return this.buildUserListWithDirectoryId(users);
            }
            if (User.class.isAssignableFrom(returnType)) {
                List users = this.getCrowdClient().searchUsers(query.getSearchRestriction(), query.getStartIndex(), query.getMaxResults());
                return this.buildUserListWithDirectoryId(users);
            }
            throw new IllegalArgumentException("Unknown return type for query: " + returnType.getName());
        }
        catch (ApplicationPermissionException | InvalidAuthenticationException e) {
            throw new OperationFailedException(e);
        }
    }

    public com.atlassian.crowd.model.group.Group findGroupByName(String name) throws GroupNotFoundException, OperationFailedException {
        try {
            return this.buildGroupWithDirectoryId(this.getCrowdClient().getGroup(name));
        }
        catch (ApplicationPermissionException | InvalidAuthenticationException e) {
            throw new OperationFailedException(e);
        }
    }

    public com.atlassian.crowd.model.group.GroupWithAttributes findGroupWithAttributesByName(String name) throws GroupNotFoundException, OperationFailedException {
        try {
            return this.buildGroupWithDirectoryId(this.getCrowdClient().getGroupWithAttributes(name));
        }
        catch (ApplicationPermissionException | InvalidAuthenticationException e) {
            throw new OperationFailedException(e);
        }
    }

    public com.atlassian.crowd.model.group.Group addGroup(GroupTemplate group) throws InvalidGroupException, OperationFailedException {
        Validate.notNull((Object)group, (String)"group cannot be null", (Object[])new Object[0]);
        Validate.notNull((Object)group.getName(), (String)"group.name cannot be null", (Object[])new Object[0]);
        GroupTemplate groupTemplate = new GroupTemplate((com.atlassian.crowd.model.group.Group)group);
        groupTemplate.setDirectoryId(-1L);
        try {
            this.getCrowdClient().addGroup((com.atlassian.crowd.model.group.Group)groupTemplate);
        }
        catch (ApplicationPermissionException e) {
            throw new OperationFailedException((Throwable)e);
        }
        catch (InvalidAuthenticationException e) {
            throw new OperationFailedException((Throwable)e);
        }
        try {
            return this.findGroupByName(group.getName());
        }
        catch (GroupNotFoundException e) {
            throw new OperationFailedException((Throwable)e);
        }
    }

    public com.atlassian.crowd.model.group.Group updateGroup(GroupTemplate group) throws InvalidGroupException, GroupNotFoundException, OperationFailedException {
        Validate.notNull((Object)group, (String)"group cannot be null", (Object[])new Object[0]);
        Validate.notNull((Object)group.getName(), (String)"group.name cannot be null", (Object[])new Object[0]);
        GroupTemplate groupTemplate = new GroupTemplate((com.atlassian.crowd.model.group.Group)group);
        groupTemplate.setDirectoryId(-1L);
        try {
            this.getCrowdClient().updateGroup((com.atlassian.crowd.model.group.Group)groupTemplate);
        }
        catch (ApplicationPermissionException | InvalidAuthenticationException e) {
            throw new OperationFailedException(e);
        }
        return this.findGroupByName(group.getName());
    }

    public com.atlassian.crowd.model.group.Group renameGroup(String oldName, String newName) throws GroupNotFoundException, InvalidGroupException, OperationFailedException {
        throw new OperationNotSupportedException("Renaming of groups is not supported");
    }

    public void storeGroupAttributes(String groupName, Map<String, Set<String>> attributes) throws GroupNotFoundException, OperationFailedException {
        try {
            this.getCrowdClient().storeGroupAttributes(groupName, attributes);
        }
        catch (ApplicationPermissionException | InvalidAuthenticationException e) {
            throw new OperationFailedException(e);
        }
    }

    public void removeGroupAttributes(String groupName, String attributeName) throws GroupNotFoundException, OperationFailedException {
        try {
            this.getCrowdClient().removeGroupAttributes(groupName, attributeName);
        }
        catch (ApplicationPermissionException | InvalidAuthenticationException e) {
            throw new OperationFailedException(e);
        }
    }

    public void removeGroup(String groupname) throws GroupNotFoundException, OperationFailedException {
        try {
            this.getCrowdClient().removeGroup(groupname);
        }
        catch (ApplicationPermissionException | InvalidAuthenticationException e) {
            throw new OperationFailedException(e);
        }
    }

    public <T> List<T> searchGroups(EntityQuery<T> query) throws OperationFailedException {
        if (query.getEntityDescriptor().getEntityType() != Entity.GROUP || query.getEntityDescriptor().getGroupType() == GroupType.LEGACY_ROLE) {
            throw new IllegalArgumentException("Query is not a group query.");
        }
        try {
            Class returnType = query.getReturnType();
            if (String.class.isAssignableFrom(returnType)) {
                return this.getCrowdClient().searchGroupNames(query.getSearchRestriction(), query.getStartIndex(), query.getMaxResults());
            }
            if (com.atlassian.crowd.model.group.GroupWithAttributes.class.isAssignableFrom(returnType) || GroupWithAttributes.class.isAssignableFrom(returnType)) {
                List groups = this.getCrowdClient().searchGroupsWithAttributes(query.getSearchRestriction(), query.getStartIndex(), query.getMaxResults());
                return this.buildGroupListWithDirectoryId(groups);
            }
            if (com.atlassian.crowd.model.group.Group.class.isAssignableFrom(returnType) || Group.class.isAssignableFrom(returnType)) {
                List groups = this.getCrowdClient().searchGroups(query.getSearchRestriction(), query.getStartIndex(), query.getMaxResults());
                return this.buildGroupListWithDirectoryId(groups);
            }
            throw new IllegalArgumentException("Unknown return type for query: " + returnType.getName());
        }
        catch (ApplicationPermissionException | InvalidAuthenticationException e) {
            throw new OperationFailedException(e);
        }
    }

    public boolean isUserDirectGroupMember(String username, String groupName) throws OperationFailedException {
        try {
            return this.getCrowdClient().isUserDirectGroupMember(username, groupName);
        }
        catch (ApplicationPermissionException | InvalidAuthenticationException e) {
            throw new OperationFailedException(e);
        }
    }

    public boolean isGroupDirectGroupMember(String childGroup, String parentGroup) throws OperationFailedException {
        try {
            return this.getCrowdClient().isGroupDirectGroupMember(childGroup, parentGroup);
        }
        catch (ApplicationPermissionException | InvalidAuthenticationException e) {
            throw new OperationFailedException(e);
        }
    }

    public BoundedCount countDirectMembersOfGroup(String groupName, int querySizeHint) throws OperationFailedException {
        try {
            long directMembersOfGroup = this.getCrowdClient().getNamesOfUsersOfGroup(groupName, 0, querySizeHint).size();
            return BoundedCount.fromCountedItemsAndLimit((long)directMembersOfGroup, (long)querySizeHint);
        }
        catch (ApplicationPermissionException | InvalidAuthenticationException e) {
            throw new OperationFailedException(e);
        }
        catch (GroupNotFoundException e) {
            return BoundedCount.exactly((long)0L);
        }
    }

    public void addUserToGroup(String username, String groupName) throws GroupNotFoundException, UserNotFoundException, OperationFailedException, MembershipAlreadyExistsException {
        try {
            this.getCrowdClient().addUserToGroup(username, groupName);
        }
        catch (ApplicationPermissionException | InvalidAuthenticationException e) {
            throw new OperationFailedException(e);
        }
    }

    public void addGroupToGroup(String childGroup, String parentGroup) throws GroupNotFoundException, InvalidMembershipException, OperationFailedException, MembershipAlreadyExistsException {
        try {
            this.getCrowdClient().addGroupToGroup(childGroup, parentGroup);
        }
        catch (UserNotFoundException e) {
            throw new OperationFailedException((Throwable)e);
        }
        catch (ApplicationPermissionException | InvalidAuthenticationException e) {
            throw new OperationFailedException(e);
        }
    }

    public void removeUserFromGroup(String username, String groupName) throws GroupNotFoundException, UserNotFoundException, MembershipNotFoundException, OperationFailedException {
        try {
            this.getCrowdClient().removeUserFromGroup(username, groupName);
        }
        catch (ApplicationPermissionException | InvalidAuthenticationException e) {
            throw new OperationFailedException(e);
        }
    }

    public void removeGroupFromGroup(String childGroup, String parentGroup) throws GroupNotFoundException, InvalidMembershipException, MembershipNotFoundException, OperationFailedException {
        try {
            this.getCrowdClient().removeGroupFromGroup(childGroup, parentGroup);
        }
        catch (ApplicationPermissionException | InvalidAuthenticationException e) {
            throw new OperationFailedException(e);
        }
    }

    public <T> List<T> searchGroupRelationships(MembershipQuery<T> query) throws OperationFailedException {
        try {
            if (query.isFindChildren()) {
                if (query.getEntityToReturn().getEntityType() == Entity.USER) {
                    if (query.getReturnType() == String.class) {
                        return this.getCrowdClient().getNamesOfUsersOfGroup(query.getEntityNameToMatch(), query.getStartIndex(), query.getMaxResults());
                    }
                    List users = this.getCrowdClient().getUsersOfGroup(query.getEntityNameToMatch(), query.getStartIndex(), query.getMaxResults());
                    return this.buildUserListWithDirectoryId(users);
                }
                if (query.getEntityToReturn().getEntityType() == Entity.GROUP) {
                    if (query.getReturnType() == String.class) {
                        return this.getCrowdClient().getNamesOfChildGroupsOfGroup(query.getEntityNameToMatch(), query.getStartIndex(), query.getMaxResults());
                    }
                    List groups = this.getCrowdClient().getChildGroupsOfGroup(query.getEntityNameToMatch(), query.getStartIndex(), query.getMaxResults());
                    return this.buildGroupListWithDirectoryId(groups);
                }
                throw new IllegalArgumentException("Query is not a group or user membership query.");
            }
            if (query.getEntityToMatch().getEntityType() == Entity.USER) {
                if (query.getReturnType() == String.class) {
                    return this.getCrowdClient().getNamesOfGroupsForUser(query.getEntityNameToMatch(), query.getStartIndex(), query.getMaxResults());
                }
                List groups = this.getCrowdClient().getGroupsForUser(query.getEntityNameToMatch(), query.getStartIndex(), query.getMaxResults());
                return this.buildGroupListWithDirectoryId(groups);
            }
            if (query.getEntityToReturn().getEntityType() == Entity.GROUP) {
                if (query.getReturnType() == String.class) {
                    return this.getCrowdClient().getNamesOfParentGroupsForGroup(query.getEntityNameToMatch(), query.getStartIndex(), query.getMaxResults());
                }
                List groups = this.getCrowdClient().getParentGroupsForGroup(query.getEntityNameToMatch(), query.getStartIndex(), query.getMaxResults());
                return this.buildGroupListWithDirectoryId(groups);
            }
            throw new IllegalArgumentException("Query is not a group or user membership query.");
        }
        catch (InvalidAuthenticationException e) {
            throw new OperationFailedException((Throwable)e);
        }
        catch (GroupNotFoundException e) {
            return Collections.emptyList();
        }
        catch (ApplicationPermissionException | UserNotFoundException e) {
            return Collections.emptyList();
        }
    }

    public String getCurrentEventToken() throws OperationFailedException, IncrementalSynchronisationNotAvailableException {
        try {
            return this.getCrowdClient().getCurrentEventToken();
        }
        catch (ApplicationPermissionException | InvalidAuthenticationException e) {
            throw new OperationFailedException(e);
        }
    }

    public Events getNewEvents(String eventToken) throws EventTokenExpiredException, OperationFailedException {
        try {
            return this.getCrowdClient().getNewEvents(eventToken);
        }
        catch (ApplicationPermissionException | InvalidAuthenticationException e) {
            throw new OperationFailedException(e);
        }
    }

    public void testConnection() throws OperationFailedException {
        try {
            this.getCrowdClient().testConnection();
        }
        catch (ApplicationPermissionException | InvalidAuthenticationException e) {
            throw new OperationFailedException(e);
        }
        catch (Exception e) {
            throw new OperationFailedException(e.getMessage(), (Throwable)e);
        }
    }

    public boolean supportsInactiveAccounts() {
        return true;
    }

    public boolean supportsNestedGroups() {
        return this.attributes.getAttributeAsBoolean("useNestedGroups", false);
    }

    public boolean supportsPasswordExpiration() {
        return true;
    }

    public boolean supportsSettingEncryptedCredential() {
        return false;
    }

    public boolean isRolesDisabled() {
        return true;
    }

    public String getDescriptiveName() {
        return DESCRIPTIVE_NAME;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = new AttributeValuesHolder(attributes);
        this.createCrowdClientLazily();
    }

    public Set<String> getValues(String name) {
        return this.attributes.getValues(name);
    }

    public String getValue(String name) {
        return this.attributes.getValue(name);
    }

    public Set<String> getKeys() {
        return this.attributes.getKeys();
    }

    public boolean isEmpty() {
        return this.attributes.isEmpty();
    }

    public RemoteDirectory getAuthoritativeDirectory() {
        return this;
    }

    private void createCrowdClientLazily() {
        final ClientProperties properties = this.getClientProperties();
        this.crowdClientRef = new LazyReference<CrowdClient>(){

            protected CrowdClient create() throws Exception {
                return RemoteCrowdDirectory.this.crowdClientFactory.newInstance(properties);
            }
        };
    }

    protected ClientProperties getClientProperties() {
        Properties properties = new Properties();
        RemoteCrowdDirectory.setProperty(properties, CROWD_SERVER_URL, this.getValue(CROWD_SERVER_URL));
        RemoteCrowdDirectory.setProperty(properties, APPLICATION_NAME, this.getValue(APPLICATION_NAME));
        RemoteCrowdDirectory.setProperty(properties, APPLICATION_PASSWORD, this.getValue(APPLICATION_PASSWORD));
        RemoteCrowdDirectory.setProperty(properties, AUTHENTICATION_METHOD_ATTRIBUTE, this.getValue(AUTHENTICATION_METHOD_ATTRIBUTE));
        RemoteCrowdDirectory.setProperty(properties, "http.timeout", this.getValue(CROWD_HTTP_TIMEOUT));
        RemoteCrowdDirectory.setProperty(properties, "http.max.connections", this.getValue(CROWD_HTTP_MAX_CONNECTIONS));
        RemoteCrowdDirectory.setProperty(properties, "http.proxy.host", this.getValue(CROWD_HTTP_PROXY_HOST));
        RemoteCrowdDirectory.setProperty(properties, "http.proxy.port", this.getValue(CROWD_HTTP_PROXY_PORT));
        RemoteCrowdDirectory.setProperty(properties, "http.proxy.username", this.getValue(CROWD_HTTP_PROXY_USERNAME));
        RemoteCrowdDirectory.setProperty(properties, "http.proxy.password", this.getValue(CROWD_HTTP_PROXY_PASSWORD));
        return ClientPropertiesImpl.newInstanceFromProperties((Properties)properties);
    }

    private static void setProperty(Properties properties, String key, String value) {
        if (value != null) {
            properties.setProperty(key, value);
        }
    }

    public CrowdClient getCrowdClient() throws OperationFailedException {
        try {
            return (CrowdClient)this.crowdClientRef.get();
        }
        catch (LazyReference.InitializationException ie) {
            throw new OperationFailedException("Failed to create remote crowd client", ie.getCause());
        }
    }

    private <T extends com.atlassian.crowd.model.user.User> T buildUserWithDirectoryId(T user) {
        UserTemplateWithAttributes userTemplateWithAttributes = user instanceof com.atlassian.crowd.model.user.UserWithAttributes ? new UserTemplateWithAttributes((com.atlassian.crowd.model.user.UserWithAttributes)user) : UserTemplateWithAttributes.toUserWithNoAttributes(user);
        userTemplateWithAttributes.setDirectoryId(this.directoryId);
        return (T)userTemplateWithAttributes;
    }

    private <T extends com.atlassian.crowd.model.user.User> List<T> buildUserListWithDirectoryId(List<T> users) {
        ArrayList<com.atlassian.crowd.model.user.User> newUsers = new ArrayList<com.atlassian.crowd.model.user.User>();
        for (com.atlassian.crowd.model.user.User user : users) {
            newUsers.add(this.buildUserWithDirectoryId(user));
        }
        return newUsers;
    }

    private <T extends com.atlassian.crowd.model.group.Group> T buildGroupWithDirectoryId(T group) {
        GroupTemplateWithAttributes groupTemplateWithAttributes = group instanceof com.atlassian.crowd.model.group.GroupWithAttributes ? new GroupTemplateWithAttributes((com.atlassian.crowd.model.group.GroupWithAttributes)group) : GroupTemplateWithAttributes.ofGroupWithNoAttributes(group);
        groupTemplateWithAttributes.setDirectoryId(this.directoryId);
        return (T)groupTemplateWithAttributes;
    }

    private <T extends com.atlassian.crowd.model.group.Group> List<T> buildGroupListWithDirectoryId(List<T> groups) {
        ArrayList<com.atlassian.crowd.model.group.Group> newGroups = new ArrayList<com.atlassian.crowd.model.group.Group>();
        for (com.atlassian.crowd.model.group.Group group : groups) {
            newGroups.add(this.buildGroupWithDirectoryId(group));
        }
        return newGroups;
    }

    public Iterable<Membership> getMemberships() throws OperationFailedException {
        try {
            return ((CrowdClient)this.crowdClientRef.get()).getMemberships();
        }
        catch (UnsupportedCrowdApiException unsupported) {
            logger.info("Using separate requests to retrieve membership data. " + unsupported.getMessage());
            return new DirectoryMembershipsIterable((RemoteDirectory)this);
        }
        catch (ApplicationPermissionException | InvalidAuthenticationException e) {
            throw new OperationFailedException(e);
        }
    }

    public com.atlassian.crowd.model.user.User userAuthenticated(String username) throws OperationFailedException, UserNotFoundException, InactiveAccountException {
        try {
            logger.debug("Notifying remote Crowd about user authentication for user '{}'", (Object)username);
            return this.buildUserWithDirectoryId(this.getCrowdClient().userAuthenticated(username));
        }
        catch (UnsupportedCrowdApiException e) {
            logger.debug("Remote Crowd doesn't support remote authentication notification, ignoring", (Throwable)e);
            return this.findUserByName(username);
        }
        catch (ApplicationPermissionException | InvalidAuthenticationException e) {
            throw new OperationFailedException(e);
        }
    }
}

