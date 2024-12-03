/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.PasswordCredential
 *  com.atlassian.crowd.embedded.api.UserCapabilities
 *  com.atlassian.crowd.event.EventTokenExpiredException
 *  com.atlassian.crowd.event.Events
 *  com.atlassian.crowd.event.IncrementalSynchronisationNotAvailableException
 *  com.atlassian.crowd.exception.ApplicationPermissionException
 *  com.atlassian.crowd.exception.BulkAddFailedException
 *  com.atlassian.crowd.exception.DirectoryNotFoundException
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
 *  com.atlassian.crowd.exception.UserNotFoundException
 *  com.atlassian.crowd.exception.WebhookNotFoundException
 *  com.atlassian.crowd.manager.application.ApplicationService
 *  com.atlassian.crowd.manager.application.ApplicationService$MembershipsIterable
 *  com.atlassian.crowd.manager.application.PagedSearcher
 *  com.atlassian.crowd.manager.application.PagingNotSupportedException
 *  com.atlassian.crowd.manager.avatar.AvatarReference
 *  com.atlassian.crowd.manager.webhook.InvalidWebhookEndpointException
 *  com.atlassian.crowd.model.application.Application
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.group.GroupTemplate
 *  com.atlassian.crowd.model.group.GroupWithAttributes
 *  com.atlassian.crowd.model.user.User
 *  com.atlassian.crowd.model.user.UserTemplate
 *  com.atlassian.crowd.model.user.UserTemplateWithAttributes
 *  com.atlassian.crowd.model.user.UserTemplateWithCredentialAndAttributes
 *  com.atlassian.crowd.model.user.UserWithAttributes
 *  com.atlassian.crowd.model.webhook.Webhook
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 *  com.atlassian.crowd.search.query.membership.MembershipQuery
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nullable
 */
package com.atlassian.crowd.manager.application;

import com.atlassian.crowd.embedded.api.PasswordCredential;
import com.atlassian.crowd.embedded.api.UserCapabilities;
import com.atlassian.crowd.event.EventTokenExpiredException;
import com.atlassian.crowd.event.Events;
import com.atlassian.crowd.event.IncrementalSynchronisationNotAvailableException;
import com.atlassian.crowd.exception.ApplicationPermissionException;
import com.atlassian.crowd.exception.BulkAddFailedException;
import com.atlassian.crowd.exception.DirectoryNotFoundException;
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
import com.atlassian.crowd.exception.UserNotFoundException;
import com.atlassian.crowd.exception.WebhookNotFoundException;
import com.atlassian.crowd.manager.application.ApplicationService;
import com.atlassian.crowd.manager.application.PagedSearcher;
import com.atlassian.crowd.manager.application.PagingNotSupportedException;
import com.atlassian.crowd.manager.avatar.AvatarReference;
import com.atlassian.crowd.manager.webhook.InvalidWebhookEndpointException;
import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.GroupTemplate;
import com.atlassian.crowd.model.group.GroupWithAttributes;
import com.atlassian.crowd.model.user.User;
import com.atlassian.crowd.model.user.UserTemplate;
import com.atlassian.crowd.model.user.UserTemplateWithAttributes;
import com.atlassian.crowd.model.user.UserTemplateWithCredentialAndAttributes;
import com.atlassian.crowd.model.user.UserWithAttributes;
import com.atlassian.crowd.model.webhook.Webhook;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import com.atlassian.crowd.search.query.membership.MembershipQuery;
import com.google.common.base.Preconditions;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public abstract class AbstractDelegatingApplicationService
implements ApplicationService {
    private ApplicationService applicationService;

    public AbstractDelegatingApplicationService(ApplicationService applicationService) {
        this.applicationService = (ApplicationService)Preconditions.checkNotNull((Object)applicationService);
    }

    protected ApplicationService getApplicationService() {
        return this.applicationService;
    }

    public User authenticateUser(Application application, String username, PasswordCredential passwordCredential) throws OperationFailedException, InactiveAccountException, InvalidAuthenticationException, ExpiredCredentialException, UserNotFoundException {
        return this.applicationService.authenticateUser(application, username, passwordCredential);
    }

    public boolean isUserAuthorised(Application application, String username) {
        return this.applicationService.isUserAuthorised(application, username);
    }

    public void addAllUsers(Application application, Collection<UserTemplateWithCredentialAndAttributes> users) throws ApplicationPermissionException, OperationFailedException, BulkAddFailedException {
        this.applicationService.addAllUsers(application, users);
    }

    public User findUserByName(Application application, String name) throws UserNotFoundException {
        return this.applicationService.findUserByName(application, name);
    }

    public User findRemoteUserByName(Application application, String username) throws UserNotFoundException {
        return this.applicationService.findRemoteUserByName(application, username);
    }

    public UserWithAttributes findUserWithAttributesByName(Application application, String name) throws UserNotFoundException {
        return this.applicationService.findUserWithAttributesByName(application, name);
    }

    public User findUserByKey(Application application, String key) throws UserNotFoundException {
        return this.applicationService.findUserByKey(application, key);
    }

    public UserWithAttributes findUserWithAttributesByKey(Application application, String key) throws UserNotFoundException {
        return this.applicationService.findUserWithAttributesByKey(application, key);
    }

    public User addUser(Application application, UserTemplate user, PasswordCredential credential) throws InvalidUserException, OperationFailedException, InvalidCredentialException, ApplicationPermissionException {
        return this.applicationService.addUser(application, UserTemplateWithAttributes.toUserWithNoAttributes((User)user), credential);
    }

    public UserWithAttributes addUser(Application application, UserTemplateWithAttributes userWithAttributes, PasswordCredential credential) throws InvalidUserException, OperationFailedException, InvalidCredentialException, ApplicationPermissionException {
        return this.applicationService.addUser(application, userWithAttributes, credential);
    }

    public User updateUser(Application application, UserTemplate user) throws InvalidUserException, OperationFailedException, ApplicationPermissionException, UserNotFoundException {
        return this.applicationService.updateUser(application, user);
    }

    public User renameUser(Application application, String oldUserName, String newUsername) throws UserNotFoundException, OperationFailedException, ApplicationPermissionException, InvalidUserException {
        return this.applicationService.renameUser(application, oldUserName, newUsername);
    }

    public void updateUserCredential(Application application, String username, PasswordCredential credential) throws OperationFailedException, UserNotFoundException, InvalidCredentialException, ApplicationPermissionException {
        this.applicationService.updateUserCredential(application, username, credential);
    }

    public void storeUserAttributes(Application application, String username, Map<String, Set<String>> attributes) throws OperationFailedException, ApplicationPermissionException, UserNotFoundException {
        this.applicationService.storeUserAttributes(application, username, attributes);
    }

    public void removeUserAttributes(Application application, String username, String attributeName) throws OperationFailedException, ApplicationPermissionException, UserNotFoundException {
        this.applicationService.removeUserAttributes(application, username, attributeName);
    }

    public void removeUser(Application application, String user) throws OperationFailedException, UserNotFoundException, ApplicationPermissionException {
        this.applicationService.removeUser(application, user);
    }

    public <T> List<T> searchUsers(Application application, EntityQuery<T> query) {
        return this.applicationService.searchUsers(application, query);
    }

    public Group findGroupByName(Application application, String name) throws GroupNotFoundException {
        return this.applicationService.findGroupByName(application, name);
    }

    public GroupWithAttributes findGroupWithAttributesByName(Application application, String name) throws GroupNotFoundException {
        return this.applicationService.findGroupWithAttributesByName(application, name);
    }

    public Group addGroup(Application application, GroupTemplate group) throws InvalidGroupException, OperationFailedException, ApplicationPermissionException {
        return this.applicationService.addGroup(application, group);
    }

    public Group updateGroup(Application application, GroupTemplate group) throws InvalidGroupException, OperationFailedException, ApplicationPermissionException, GroupNotFoundException {
        return this.applicationService.updateGroup(application, group);
    }

    public void storeGroupAttributes(Application application, String groupname, Map<String, Set<String>> attributes) throws OperationFailedException, ApplicationPermissionException, GroupNotFoundException {
        this.applicationService.storeGroupAttributes(application, groupname, attributes);
    }

    public void removeGroupAttributes(Application application, String groupname, String attributeName) throws OperationFailedException, ApplicationPermissionException, GroupNotFoundException {
        this.applicationService.removeGroupAttributes(application, groupname, attributeName);
    }

    public void removeGroup(Application application, String group) throws OperationFailedException, GroupNotFoundException, ApplicationPermissionException {
        this.applicationService.removeGroup(application, group);
    }

    public <T> List<T> searchGroups(Application application, EntityQuery<T> query) {
        return this.applicationService.searchGroups(application, query);
    }

    public void addUserToGroup(Application application, String username, String groupName) throws OperationFailedException, UserNotFoundException, GroupNotFoundException, ApplicationPermissionException, MembershipAlreadyExistsException {
        this.applicationService.addUserToGroup(application, username, groupName);
    }

    public void addGroupToGroup(Application application, String childGroupName, String parentGroupName) throws OperationFailedException, GroupNotFoundException, ApplicationPermissionException, InvalidMembershipException, MembershipAlreadyExistsException {
        this.applicationService.addGroupToGroup(application, childGroupName, parentGroupName);
    }

    public void removeUserFromGroup(Application application, String username, String groupName) throws OperationFailedException, GroupNotFoundException, UserNotFoundException, ApplicationPermissionException, MembershipNotFoundException {
        this.applicationService.removeUserFromGroup(application, username, groupName);
    }

    public void removeGroupFromGroup(Application application, String childGroup, String parentGroup) throws OperationFailedException, GroupNotFoundException, ApplicationPermissionException, MembershipNotFoundException {
        this.applicationService.removeGroupFromGroup(application, childGroup, parentGroup);
    }

    public boolean isUserDirectGroupMember(Application application, String username, String groupName) {
        return this.applicationService.isUserDirectGroupMember(application, username, groupName);
    }

    public boolean isGroupDirectGroupMember(Application application, String childGroup, String parentGroup) {
        return this.applicationService.isGroupDirectGroupMember(application, childGroup, parentGroup);
    }

    public boolean isUserNestedGroupMember(Application application, String username, String groupName) {
        return this.applicationService.isUserNestedGroupMember(application, username, groupName);
    }

    public boolean isGroupNestedGroupMember(Application application, String childGroup, String parentGroup) {
        return this.applicationService.isGroupNestedGroupMember(application, childGroup, parentGroup);
    }

    public <T> List<T> searchDirectGroupRelationships(Application application, MembershipQuery<T> query) {
        return this.applicationService.searchDirectGroupRelationships(application, query);
    }

    public <T> List<T> searchNestedGroupRelationships(Application application, MembershipQuery<T> query) {
        return this.applicationService.searchNestedGroupRelationships(application, query);
    }

    public String getCurrentEventToken(Application application) throws IncrementalSynchronisationNotAvailableException {
        return this.applicationService.getCurrentEventToken(application);
    }

    public Events getNewEvents(Application application, String eventToken) throws EventTokenExpiredException, OperationFailedException {
        return this.applicationService.getNewEvents(application, eventToken);
    }

    public Webhook findWebhookById(Application application, long webhookId) throws WebhookNotFoundException, ApplicationPermissionException {
        return this.applicationService.findWebhookById(application, webhookId);
    }

    public Webhook registerWebhook(Application application, String endpointUrl, @Nullable String token) throws InvalidWebhookEndpointException {
        return this.applicationService.registerWebhook(application, endpointUrl, token);
    }

    public void unregisterWebhook(Application application, long webhookId) throws ApplicationPermissionException, WebhookNotFoundException {
        this.applicationService.unregisterWebhook(application, webhookId);
    }

    public UserCapabilities getCapabilitiesForNewUsers(Application application) {
        return this.applicationService.getCapabilitiesForNewUsers(application);
    }

    @Nullable
    public URI getUserAvatarLink(Application application, String username, int sizeHint) throws UserNotFoundException, DirectoryNotFoundException, OperationFailedException {
        return this.applicationService.getUserAvatarLink(application, username, sizeHint);
    }

    @Nullable
    public AvatarReference getUserAvatar(Application application, String username, int sizeHint) throws UserNotFoundException, DirectoryNotFoundException, OperationFailedException {
        return this.applicationService.getUserAvatar(application, username, sizeHint);
    }

    public void expireAllPasswords(Application application) throws OperationFailedException {
        this.applicationService.expireAllPasswords(application);
    }

    public User userAuthenticated(Application application, String username) throws UserNotFoundException, OperationFailedException, InactiveAccountException {
        return this.applicationService.userAuthenticated(application, username);
    }

    public ApplicationService.MembershipsIterable getMemberships(Application application) {
        return this.applicationService.getMemberships(application);
    }

    public boolean isUserAuthorised(Application application, User user) {
        return this.applicationService.isUserAuthorised(application, user);
    }

    public <T> PagedSearcher<T> createPagedUserSearcher(Application application, EntityQuery<T> query) throws PagingNotSupportedException {
        return this.applicationService.createPagedUserSearcher(application, query);
    }

    public <T> PagedSearcher<T> createPagedGroupSearcher(Application application, EntityQuery<T> query) throws PagingNotSupportedException {
        return this.applicationService.createPagedGroupSearcher(application, query);
    }
}

