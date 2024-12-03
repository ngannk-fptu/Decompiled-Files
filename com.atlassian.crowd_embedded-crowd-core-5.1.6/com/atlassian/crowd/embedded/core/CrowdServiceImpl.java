/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.loader.DirectoryInstanceLoader
 *  com.atlassian.crowd.embedded.api.ApplicationFactory
 *  com.atlassian.crowd.embedded.api.Group
 *  com.atlassian.crowd.embedded.api.GroupWithAttributes
 *  com.atlassian.crowd.embedded.api.PasswordCredential
 *  com.atlassian.crowd.embedded.api.Query
 *  com.atlassian.crowd.embedded.api.UnfilteredCrowdService
 *  com.atlassian.crowd.embedded.api.User
 *  com.atlassian.crowd.embedded.api.UserCapabilities
 *  com.atlassian.crowd.embedded.api.UserWithAttributes
 *  com.atlassian.crowd.exception.AccountNotFoundException
 *  com.atlassian.crowd.exception.ApplicationPermissionException
 *  com.atlassian.crowd.exception.FailedAuthenticationException
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
 *  com.atlassian.crowd.exception.OperationNotPermittedException
 *  com.atlassian.crowd.exception.UserNotFoundException
 *  com.atlassian.crowd.exception.embedded.InvalidGroupException
 *  com.atlassian.crowd.exception.runtime.CommunicationException
 *  com.atlassian.crowd.exception.runtime.GroupNotFoundException
 *  com.atlassian.crowd.exception.runtime.MembershipNotFoundException
 *  com.atlassian.crowd.exception.runtime.OperationFailedException
 *  com.atlassian.crowd.exception.runtime.UserNotFoundException
 *  com.atlassian.crowd.manager.application.ApplicationService
 *  com.atlassian.crowd.model.application.Application
 *  com.atlassian.crowd.model.group.GroupTemplate
 *  com.atlassian.crowd.model.user.UserTemplate
 *  com.atlassian.crowd.model.user.UserTemplateWithAttributes
 *  com.atlassian.crowd.search.query.entity.GroupQuery
 *  com.atlassian.crowd.search.query.entity.UserQuery
 *  com.atlassian.crowd.search.query.membership.MembershipQuery
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Sets
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.springframework.ldap.CommunicationException
 */
package com.atlassian.crowd.embedded.core;

import com.atlassian.crowd.directory.loader.DirectoryInstanceLoader;
import com.atlassian.crowd.embedded.api.ApplicationFactory;
import com.atlassian.crowd.embedded.api.Group;
import com.atlassian.crowd.embedded.api.GroupWithAttributes;
import com.atlassian.crowd.embedded.api.PasswordCredential;
import com.atlassian.crowd.embedded.api.Query;
import com.atlassian.crowd.embedded.api.UnfilteredCrowdService;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.crowd.embedded.api.UserCapabilities;
import com.atlassian.crowd.embedded.api.UserWithAttributes;
import com.atlassian.crowd.embedded.core.util.ConversionUtils;
import com.atlassian.crowd.exception.AccountNotFoundException;
import com.atlassian.crowd.exception.ApplicationPermissionException;
import com.atlassian.crowd.exception.FailedAuthenticationException;
import com.atlassian.crowd.exception.InactiveAccountException;
import com.atlassian.crowd.exception.InvalidAuthenticationException;
import com.atlassian.crowd.exception.InvalidCredentialException;
import com.atlassian.crowd.exception.InvalidGroupException;
import com.atlassian.crowd.exception.InvalidMembershipException;
import com.atlassian.crowd.exception.InvalidUserException;
import com.atlassian.crowd.exception.MembershipAlreadyExistsException;
import com.atlassian.crowd.exception.OperationNotPermittedException;
import com.atlassian.crowd.exception.UserNotFoundException;
import com.atlassian.crowd.exception.runtime.GroupNotFoundException;
import com.atlassian.crowd.exception.runtime.MembershipNotFoundException;
import com.atlassian.crowd.exception.runtime.OperationFailedException;
import com.atlassian.crowd.manager.application.ApplicationService;
import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.model.group.GroupTemplate;
import com.atlassian.crowd.model.user.UserTemplate;
import com.atlassian.crowd.model.user.UserTemplateWithAttributes;
import com.atlassian.crowd.search.query.entity.GroupQuery;
import com.atlassian.crowd.search.query.entity.UserQuery;
import com.atlassian.crowd.search.query.membership.MembershipQuery;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.springframework.ldap.CommunicationException;

public class CrowdServiceImpl
implements UnfilteredCrowdService {
    private final ApplicationService applicationService;
    private final ApplicationFactory applicationFactory;

    public CrowdServiceImpl(ApplicationFactory applicationFactory, ApplicationService applicationService, DirectoryInstanceLoader directoryInstanceLoader) {
        this.applicationFactory = (ApplicationFactory)Preconditions.checkNotNull((Object)applicationFactory);
        this.applicationService = (ApplicationService)Preconditions.checkNotNull((Object)applicationService);
    }

    public User authenticate(String name, String credential) throws FailedAuthenticationException, OperationFailedException {
        Application application = this.getApplication();
        if (application == null) {
            throw new FailedAuthenticationException("No application to authenticate user against.");
        }
        try {
            return this.applicationService.authenticateUser(application, name, PasswordCredential.unencrypted((String)credential));
        }
        catch (UserNotFoundException e) {
            throw new AccountNotFoundException(e.getUserName(), (Throwable)e);
        }
        catch (InvalidAuthenticationException e) {
            throw new FailedAuthenticationException((Throwable)e);
        }
        catch (com.atlassian.crowd.exception.OperationFailedException ex) {
            throw this.convertOperationFailedException(ex);
        }
    }

    public User getUser(String name) {
        Application application = this.getApplication();
        if (application == null) {
            return null;
        }
        try {
            return this.applicationService.findUserByName(application, name);
        }
        catch (UserNotFoundException e) {
            return null;
        }
    }

    @Nullable
    public User getRemoteUser(@Nonnull String username) {
        Application application = this.getApplication();
        try {
            return this.applicationService.findRemoteUserByName(application, username);
        }
        catch (UserNotFoundException e) {
            return null;
        }
    }

    public User userAuthenticated(String username) throws com.atlassian.crowd.exception.runtime.UserNotFoundException, OperationFailedException, InactiveAccountException {
        Application application = this.getApplication();
        try {
            return this.applicationService.userAuthenticated(application, username);
        }
        catch (UserNotFoundException e) {
            throw new com.atlassian.crowd.exception.runtime.UserNotFoundException(e.getUserName(), e.getCause());
        }
        catch (com.atlassian.crowd.exception.OperationFailedException e) {
            throw new OperationFailedException(e.getMessage(), e.getCause());
        }
    }

    public UserWithAttributes getUserWithAttributes(String name) {
        Application application = this.getApplication();
        if (application == null) {
            return null;
        }
        try {
            return this.applicationService.findUserWithAttributesByName(application, name);
        }
        catch (UserNotFoundException e) {
            return null;
        }
    }

    public Group getGroup(String name) {
        Application application = this.getApplication();
        if (application == null) {
            return null;
        }
        try {
            return ConversionUtils.toEmbeddedGroup(this.applicationService.findGroupByName(application, name));
        }
        catch (com.atlassian.crowd.exception.GroupNotFoundException e) {
            return null;
        }
    }

    public GroupWithAttributes getGroupWithAttributes(String name) {
        Application application = this.getApplication();
        if (application == null) {
            return null;
        }
        try {
            return ConversionUtils.toEmbeddedGroupWithAttributes(this.applicationService.findGroupWithAttributesByName(application, name));
        }
        catch (com.atlassian.crowd.exception.GroupNotFoundException e) {
            return null;
        }
    }

    public <T> Iterable<T> search(Query<T> query) {
        Preconditions.checkNotNull(query, (Object)"You cannot query with a null query object");
        if (MembershipQuery.class.isInstance(query)) {
            return this.searchNestedGroupRelationships((MembershipQuery)query);
        }
        if (UserQuery.class.isInstance(query)) {
            return this.searchUsers((UserQuery)query);
        }
        if (GroupQuery.class.isInstance(query)) {
            return this.searchGroups((GroupQuery)query);
        }
        throw new IllegalArgumentException("Cannot search with a query of type <" + query.getClass().getName() + ">");
    }

    private <T> List<T> searchUsers(UserQuery<T> query) {
        Application application = this.getApplication();
        if (application == null) {
            return new ArrayList();
        }
        if (String.class.equals((Object)query.getReturnType())) {
            return this.applicationService.searchUsers(application, query);
        }
        if (User.class.equals((Object)query.getReturnType())) {
            List modelUsers = this.applicationService.searchUsers(application, ConversionUtils.toModelUserQuery(query));
            return new ArrayList(modelUsers);
        }
        throw new IllegalArgumentException("User search queries can only be specified to return String or " + User.class.getCanonicalName() + " objects");
    }

    private <T> List<T> searchGroups(GroupQuery<T> query) {
        Application application = this.getApplication();
        if (application == null) {
            return new ArrayList();
        }
        if (String.class.equals((Object)query.getReturnType())) {
            return this.applicationService.searchGroups(application, query);
        }
        if (Group.class.equals((Object)query.getReturnType())) {
            List modelGroups = this.applicationService.searchGroups(application, ConversionUtils.toModelGroupQuery(query));
            return ConversionUtils.toEmbeddedGroups(modelGroups);
        }
        throw new IllegalArgumentException("Group search queries can only be specified to return String or " + Group.class.getCanonicalName() + " objects");
    }

    private <T> List<T> searchNestedGroupRelationships(MembershipQuery<T> query) {
        Application application = this.getApplication();
        if (application == null) {
            return new ArrayList();
        }
        if (String.class.equals((Object)query.getReturnType())) {
            return this.applicationService.searchNestedGroupRelationships(application, query);
        }
        if (User.class.equals((Object)query.getReturnType())) {
            List modelUsers = this.applicationService.searchNestedGroupRelationships(application, ConversionUtils.toModelUserMembershipQuery(query));
            return ConversionUtils.toEmbeddedUsers(modelUsers);
        }
        if (Group.class.equals((Object)query.getReturnType())) {
            List modelGroups = this.applicationService.searchNestedGroupRelationships(application, ConversionUtils.toModelGroupMembershipQuery(query));
            return ConversionUtils.toEmbeddedGroups(modelGroups);
        }
        throw new IllegalArgumentException("Membership search queries can only be specified to return String, " + User.class.getCanonicalName() + ", or " + Group.class.getCanonicalName() + " objects");
    }

    public boolean isUserMemberOfGroup(String userName, String groupName) {
        Application application = this.getApplication();
        if (application == null) {
            return false;
        }
        return this.applicationService.isUserNestedGroupMember(application, userName, groupName);
    }

    public boolean isUserMemberOfGroup(User user, Group group) {
        return this.isUserMemberOfGroup(user.getName(), group.getName());
    }

    public boolean isGroupMemberOfGroup(String childGroupName, String parentGroupName) {
        Application application = this.getApplication();
        if (application == null) {
            return false;
        }
        return this.applicationService.isGroupNestedGroupMember(application, childGroupName, parentGroupName);
    }

    public boolean isGroupMemberOfGroup(Group childGroup, Group parentGroup) {
        return this.isGroupMemberOfGroup(childGroup.getName(), parentGroup.getName());
    }

    public User addUser(User user, String credential) throws InvalidUserException, InvalidCredentialException, OperationNotPermittedException, OperationFailedException {
        return this.addUser((UserWithAttributes)UserTemplateWithAttributes.toUserWithNoAttributes((User)user), credential);
    }

    public UserWithAttributes addUser(UserWithAttributes user, String credential) throws InvalidUserException, InvalidCredentialException, OperationNotPermittedException, OperationFailedException {
        Application application = this.getApplication();
        if (application == null) {
            throw new OperationFailedException("No Crowd Application available.");
        }
        try {
            return this.applicationService.addUser(application, new UserTemplateWithAttributes(user), new PasswordCredential(credential));
        }
        catch (ApplicationPermissionException e) {
            throw new OperationNotPermittedException((Throwable)e);
        }
        catch (com.atlassian.crowd.exception.OperationFailedException e) {
            throw new OperationFailedException(e.getMessage(), e.getCause());
        }
    }

    public User updateUser(User user) throws com.atlassian.crowd.exception.runtime.UserNotFoundException, InvalidUserException, OperationNotPermittedException, OperationFailedException {
        Application application = this.getApplication();
        if (application == null) {
            throw new OperationFailedException("No Crowd Application available.");
        }
        try {
            return this.applicationService.updateUser(application, new UserTemplate(user));
        }
        catch (ApplicationPermissionException e) {
            throw new OperationNotPermittedException((Throwable)e);
        }
        catch (UserNotFoundException e) {
            throw new com.atlassian.crowd.exception.runtime.UserNotFoundException(e.getUserName(), e.getCause());
        }
        catch (com.atlassian.crowd.exception.OperationFailedException e) {
            throw new OperationFailedException(e.getMessage(), e.getCause());
        }
    }

    public User renameUser(User user, String newUsername) throws com.atlassian.crowd.exception.runtime.UserNotFoundException, InvalidUserException, OperationNotPermittedException, OperationFailedException {
        Application application = this.getApplication();
        if (application == null) {
            throw new OperationFailedException("No Crowd Application available.");
        }
        try {
            return this.applicationService.renameUser(application, user.getName(), newUsername);
        }
        catch (ApplicationPermissionException e) {
            throw new OperationNotPermittedException((Throwable)e);
        }
        catch (UserNotFoundException e) {
            throw new com.atlassian.crowd.exception.runtime.UserNotFoundException(e.getUserName(), e.getCause());
        }
        catch (com.atlassian.crowd.exception.OperationFailedException e) {
            throw new OperationFailedException(e.getMessage(), e.getCause());
        }
    }

    public void updateUserCredential(User user, String credential) throws com.atlassian.crowd.exception.runtime.UserNotFoundException, InvalidCredentialException, OperationNotPermittedException, OperationFailedException {
        Application application = this.getApplication();
        if (application == null) {
            throw new OperationFailedException("No Crowd Application available.");
        }
        try {
            this.applicationService.updateUserCredential(application, user.getName(), new PasswordCredential(credential));
        }
        catch (ApplicationPermissionException e) {
            throw new OperationNotPermittedException((Throwable)e);
        }
        catch (UserNotFoundException e) {
            throw new com.atlassian.crowd.exception.runtime.UserNotFoundException(e.getUserName(), e.getCause());
        }
        catch (com.atlassian.crowd.exception.OperationFailedException e) {
            throw new OperationFailedException(e.getMessage(), e.getCause());
        }
    }

    public void setUserAttribute(User user, String attributeName, String attributeValue) throws com.atlassian.crowd.exception.runtime.UserNotFoundException, OperationNotPermittedException, OperationFailedException {
        HashSet attributeValues = Sets.newHashSet((Object[])new String[]{attributeValue});
        this.setUserAttribute(user, attributeName, attributeValues);
    }

    public void setUserAttribute(User user, String attributeName, Set<String> attributeValues) throws com.atlassian.crowd.exception.runtime.UserNotFoundException, OperationNotPermittedException, OperationFailedException {
        Application application = this.getApplication();
        if (application == null) {
            throw new OperationFailedException("No Crowd Application available.");
        }
        try {
            this.applicationService.storeUserAttributes(application, user.getName(), this.buildAttributesAsMap(attributeName, attributeValues));
        }
        catch (ApplicationPermissionException e) {
            throw new OperationNotPermittedException((Throwable)e);
        }
        catch (UserNotFoundException e) {
            throw new com.atlassian.crowd.exception.runtime.UserNotFoundException(e.getUserName(), e.getCause());
        }
        catch (com.atlassian.crowd.exception.OperationFailedException e) {
            throw new OperationFailedException(e.getMessage(), e.getCause());
        }
    }

    private Map<String, Set<String>> buildAttributesAsMap(String attributeName, Set<String> attributeValues) {
        return new ImmutableMap.Builder().put((Object)attributeName, attributeValues).build();
    }

    public void removeUserAttribute(User user, String attributeName) throws com.atlassian.crowd.exception.runtime.UserNotFoundException, OperationNotPermittedException, OperationFailedException {
        Application application = this.getApplication();
        if (application == null) {
            throw new OperationFailedException("No Crowd Application available.");
        }
        try {
            this.applicationService.removeUserAttributes(application, user.getName(), attributeName);
        }
        catch (ApplicationPermissionException e) {
            throw new OperationNotPermittedException((Throwable)e);
        }
        catch (UserNotFoundException e) {
            throw new com.atlassian.crowd.exception.runtime.UserNotFoundException(e.getUserName(), e.getCause());
        }
        catch (com.atlassian.crowd.exception.OperationFailedException e) {
            throw new OperationFailedException(e.getMessage(), e.getCause());
        }
    }

    public void removeAllUserAttributes(User user) throws com.atlassian.crowd.exception.runtime.UserNotFoundException, OperationNotPermittedException, OperationFailedException {
        Application application = this.getApplication();
        if (application == null) {
            throw new OperationFailedException("No Crowd Application available.");
        }
        String userName = user.getName();
        UserWithAttributes userWithAttributes = this.getUserWithAttributes(userName);
        Set attributeNames = userWithAttributes.getKeys();
        try {
            for (String attributeName : attributeNames) {
                this.applicationService.removeUserAttributes(application, userName, attributeName);
            }
        }
        catch (ApplicationPermissionException e) {
            throw new OperationNotPermittedException((Throwable)e);
        }
        catch (UserNotFoundException e) {
            throw new com.atlassian.crowd.exception.runtime.UserNotFoundException(e.getUserName(), e.getCause());
        }
        catch (com.atlassian.crowd.exception.OperationFailedException e) {
            throw new OperationFailedException(e.getMessage(), e.getCause());
        }
    }

    public boolean removeUser(User user) throws OperationNotPermittedException, OperationFailedException {
        Application application = this.getApplication();
        if (application == null) {
            throw new OperationFailedException("No Crowd Application available.");
        }
        try {
            this.applicationService.removeUser(application, user.getName());
            return true;
        }
        catch (UserNotFoundException e) {
            return false;
        }
        catch (ApplicationPermissionException e) {
            throw new OperationNotPermittedException((Throwable)e);
        }
        catch (com.atlassian.crowd.exception.OperationFailedException e) {
            throw new OperationFailedException(e.getMessage(), e.getCause());
        }
    }

    private static GroupTemplate newGroupTemplate(Group group) {
        Preconditions.checkNotNull((Object)group, (Object)"group argument cannot be null");
        return new GroupTemplate(group.getName());
    }

    public Group addGroup(Group group) throws com.atlassian.crowd.exception.embedded.InvalidGroupException, OperationNotPermittedException, OperationFailedException {
        Application application = this.getApplication();
        if (application == null) {
            throw new OperationFailedException("No Crowd Application available.");
        }
        try {
            return ConversionUtils.toEmbeddedGroup(this.applicationService.addGroup(application, CrowdServiceImpl.newGroupTemplate(group)));
        }
        catch (InvalidGroupException ex) {
            throw new com.atlassian.crowd.exception.embedded.InvalidGroupException(ConversionUtils.getEmbeddedGroup(ex), (Throwable)ex);
        }
        catch (ApplicationPermissionException e) {
            throw new OperationNotPermittedException((Throwable)e);
        }
        catch (com.atlassian.crowd.exception.OperationFailedException e) {
            throw new OperationFailedException(e.getMessage(), e.getCause());
        }
    }

    public Group updateGroup(Group group) throws com.atlassian.crowd.exception.embedded.InvalidGroupException, OperationNotPermittedException, GroupNotFoundException, OperationFailedException {
        Application application = this.getApplication();
        if (application == null) {
            throw new OperationFailedException("No Crowd Application available.");
        }
        try {
            return ConversionUtils.toEmbeddedGroup(this.applicationService.updateGroup(application, CrowdServiceImpl.newGroupTemplate(group)));
        }
        catch (InvalidGroupException ex) {
            throw new com.atlassian.crowd.exception.embedded.InvalidGroupException(ConversionUtils.getEmbeddedGroup(ex), (Throwable)ex);
        }
        catch (ApplicationPermissionException e) {
            throw new OperationNotPermittedException((Throwable)e);
        }
        catch (com.atlassian.crowd.exception.GroupNotFoundException e) {
            throw new GroupNotFoundException(e.getGroupName(), e.getCause());
        }
        catch (com.atlassian.crowd.exception.OperationFailedException e) {
            throw new OperationFailedException(e.getMessage(), e.getCause());
        }
    }

    public void setGroupAttribute(Group group, String attributeName, String attributeValue) throws GroupNotFoundException, OperationNotPermittedException, OperationFailedException {
        HashSet attributeValues = Sets.newHashSet((Object[])new String[]{attributeValue});
        this.setGroupAttribute(group, attributeName, attributeValues);
    }

    public void setGroupAttribute(Group group, String attributeName, Set<String> attributeValues) throws GroupNotFoundException, OperationNotPermittedException, OperationFailedException {
        Application application = this.getApplication();
        if (application == null) {
            throw new OperationFailedException("No Crowd Application available.");
        }
        try {
            this.applicationService.storeGroupAttributes(application, group.getName(), this.buildAttributesAsMap(attributeName, attributeValues));
        }
        catch (ApplicationPermissionException e) {
            throw new OperationNotPermittedException((Throwable)e);
        }
        catch (com.atlassian.crowd.exception.GroupNotFoundException e) {
            throw new GroupNotFoundException(e.getGroupName(), e.getCause());
        }
        catch (com.atlassian.crowd.exception.OperationFailedException e) {
            throw new OperationFailedException(e.getMessage(), e.getCause());
        }
    }

    public void removeGroupAttribute(Group group, String attributeName) throws GroupNotFoundException, OperationNotPermittedException, OperationFailedException {
        Application application = this.getApplication();
        if (application == null) {
            throw new OperationFailedException("No Crowd Application available.");
        }
        try {
            this.applicationService.removeGroupAttributes(application, group.getName(), attributeName);
        }
        catch (ApplicationPermissionException e) {
            throw new OperationNotPermittedException((Throwable)e);
        }
        catch (com.atlassian.crowd.exception.GroupNotFoundException e) {
            throw new GroupNotFoundException(e.getGroupName(), e.getCause());
        }
        catch (com.atlassian.crowd.exception.OperationFailedException e) {
            throw new OperationFailedException(e.getMessage(), e.getCause());
        }
    }

    public void removeAllGroupAttributes(Group group) throws GroupNotFoundException, OperationNotPermittedException, OperationFailedException {
        Application application = this.getApplication();
        if (application == null) {
            throw new OperationFailedException("No Crowd Application available.");
        }
        String groupName = group.getName();
        GroupWithAttributes groupWithAttributes = this.getGroupWithAttributes(groupName);
        Set attributeNames = groupWithAttributes.getKeys();
        try {
            for (String attributeName : attributeNames) {
                this.applicationService.removeGroupAttributes(application, groupName, attributeName);
            }
        }
        catch (ApplicationPermissionException e) {
            throw new OperationNotPermittedException((Throwable)e);
        }
        catch (com.atlassian.crowd.exception.GroupNotFoundException e) {
            throw new GroupNotFoundException(e.getGroupName(), e.getCause());
        }
        catch (com.atlassian.crowd.exception.OperationFailedException e) {
            throw new OperationFailedException(e.getMessage(), e.getCause());
        }
    }

    public boolean removeGroup(Group group) throws OperationNotPermittedException, GroupNotFoundException, OperationFailedException {
        Application application = this.getApplication();
        if (application == null) {
            throw new OperationFailedException("No Crowd Application available.");
        }
        try {
            this.applicationService.removeGroup(application, group.getName());
            return true;
        }
        catch (com.atlassian.crowd.exception.GroupNotFoundException e) {
            return false;
        }
        catch (ApplicationPermissionException e) {
            throw new OperationNotPermittedException((Throwable)e);
        }
        catch (com.atlassian.crowd.exception.OperationFailedException e) {
            throw new OperationFailedException(e.getMessage(), e.getCause());
        }
    }

    public boolean addUserToGroup(User user, Group group) throws GroupNotFoundException, com.atlassian.crowd.exception.runtime.UserNotFoundException, OperationNotPermittedException, OperationFailedException {
        Application application = this.getApplication();
        if (application == null) {
            throw new OperationFailedException("No Crowd Application available.");
        }
        try {
            this.applicationService.addUserToGroup(application, user.getName(), group.getName());
            return true;
        }
        catch (MembershipAlreadyExistsException e) {
            return false;
        }
        catch (ApplicationPermissionException e) {
            throw new OperationNotPermittedException((Throwable)e);
        }
        catch (UserNotFoundException e) {
            throw new com.atlassian.crowd.exception.runtime.UserNotFoundException(e.getUserName(), e.getCause());
        }
        catch (com.atlassian.crowd.exception.GroupNotFoundException e) {
            throw new GroupNotFoundException(e.getGroupName(), e.getCause());
        }
        catch (com.atlassian.crowd.exception.OperationFailedException e) {
            throw new OperationFailedException(e.getMessage(), e.getCause());
        }
    }

    public boolean addGroupToGroup(Group childGroup, Group parentGroup) throws GroupNotFoundException, OperationNotPermittedException, InvalidMembershipException, OperationFailedException {
        Application application = this.getApplication();
        if (application == null) {
            throw new OperationFailedException("No Crowd Application available.");
        }
        try {
            this.applicationService.addGroupToGroup(application, childGroup.getName(), parentGroup.getName());
            return true;
        }
        catch (MembershipAlreadyExistsException e) {
            return false;
        }
        catch (ApplicationPermissionException e) {
            throw new OperationNotPermittedException((Throwable)e);
        }
        catch (com.atlassian.crowd.exception.GroupNotFoundException e) {
            throw new GroupNotFoundException(e.getGroupName(), e.getCause());
        }
        catch (com.atlassian.crowd.exception.OperationFailedException e) {
            throw new OperationFailedException(e.getMessage(), e.getCause());
        }
    }

    public boolean removeUserFromGroup(User user, Group group) throws GroupNotFoundException, com.atlassian.crowd.exception.runtime.UserNotFoundException, OperationNotPermittedException, MembershipNotFoundException, OperationFailedException {
        Application application = this.getApplication();
        if (application == null) {
            throw new OperationFailedException("No Crowd Application available.");
        }
        try {
            this.applicationService.removeUserFromGroup(application, user.getName(), group.getName());
            return true;
        }
        catch (com.atlassian.crowd.exception.MembershipNotFoundException e) {
            return false;
        }
        catch (ApplicationPermissionException e) {
            throw new OperationNotPermittedException((Throwable)e);
        }
        catch (UserNotFoundException e) {
            throw new com.atlassian.crowd.exception.runtime.UserNotFoundException(e.getUserName(), e.getCause());
        }
        catch (com.atlassian.crowd.exception.GroupNotFoundException e) {
            throw new GroupNotFoundException(e.getGroupName(), e.getCause());
        }
        catch (com.atlassian.crowd.exception.OperationFailedException e) {
            throw new OperationFailedException(e.getMessage(), e.getCause());
        }
    }

    public boolean removeGroupFromGroup(Group childGroup, Group parentGroup) throws GroupNotFoundException, OperationNotPermittedException, MembershipNotFoundException, OperationFailedException {
        Application application = this.getApplication();
        if (application == null) {
            throw new OperationFailedException("No Crowd Application available.");
        }
        try {
            this.applicationService.removeGroupFromGroup(application, childGroup.getName(), parentGroup.getName());
            return true;
        }
        catch (com.atlassian.crowd.exception.MembershipNotFoundException e) {
            return false;
        }
        catch (ApplicationPermissionException e) {
            throw new OperationNotPermittedException((Throwable)e);
        }
        catch (com.atlassian.crowd.exception.GroupNotFoundException e) {
            throw new GroupNotFoundException(e.getGroupName(), e.getCause());
        }
        catch (com.atlassian.crowd.exception.OperationFailedException e) {
            throw new OperationFailedException(e.getMessage(), e.getCause());
        }
    }

    public boolean isUserDirectGroupMember(User user, Group group) throws OperationFailedException {
        Application application = this.getApplication();
        if (application == null) {
            return false;
        }
        return this.applicationService.isUserDirectGroupMember(application, user.getName(), group.getName());
    }

    public boolean isGroupDirectGroupMember(Group childGroup, Group parentGroup) throws OperationFailedException {
        Application application = this.getApplication();
        if (application == null) {
            return false;
        }
        return this.applicationService.isGroupDirectGroupMember(application, childGroup.getName(), parentGroup.getName());
    }

    public UserCapabilities getCapabilitiesForNewUsers() {
        return this.applicationService.getCapabilitiesForNewUsers(this.getApplication());
    }

    private Application getApplication() {
        return this.applicationFactory.getApplication();
    }

    private OperationFailedException convertOperationFailedException(com.atlassian.crowd.exception.OperationFailedException ex) {
        Throwable cause = ex.getCause();
        if (cause == null) {
            return new OperationFailedException(ex.getMessage(), (Throwable)ex);
        }
        if (cause instanceof CommunicationException) {
            return new com.atlassian.crowd.exception.runtime.CommunicationException(cause);
        }
        if (cause instanceof ConnectException) {
            return new com.atlassian.crowd.exception.runtime.CommunicationException(cause);
        }
        return new OperationFailedException(cause);
    }
}

