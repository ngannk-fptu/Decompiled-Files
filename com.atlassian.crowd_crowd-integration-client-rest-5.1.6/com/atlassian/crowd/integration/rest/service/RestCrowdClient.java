/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Attributes
 *  com.atlassian.crowd.embedded.api.PasswordCredential
 *  com.atlassian.crowd.embedded.api.SearchRestriction
 *  com.atlassian.crowd.embedded.api.User
 *  com.atlassian.crowd.event.EventTokenExpiredException
 *  com.atlassian.crowd.event.Events
 *  com.atlassian.crowd.event.IncrementalSynchronisationNotAvailableException
 *  com.atlassian.crowd.exception.ApplicationAccessDeniedException
 *  com.atlassian.crowd.exception.ApplicationPermissionException
 *  com.atlassian.crowd.exception.ExpiredCredentialException
 *  com.atlassian.crowd.exception.GroupNotFoundException
 *  com.atlassian.crowd.exception.InactiveAccountException
 *  com.atlassian.crowd.exception.InvalidAuthenticationException
 *  com.atlassian.crowd.exception.InvalidCredentialException
 *  com.atlassian.crowd.exception.InvalidEmailAddressException
 *  com.atlassian.crowd.exception.InvalidGroupException
 *  com.atlassian.crowd.exception.InvalidTokenException
 *  com.atlassian.crowd.exception.InvalidUserException
 *  com.atlassian.crowd.exception.MembershipAlreadyExistsException
 *  com.atlassian.crowd.exception.MembershipNotFoundException
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.exception.UnsupportedCrowdApiException
 *  com.atlassian.crowd.exception.UserNotFoundException
 *  com.atlassian.crowd.exception.WebhookNotFoundException
 *  com.atlassian.crowd.model.authentication.CookieConfiguration
 *  com.atlassian.crowd.model.authentication.Session
 *  com.atlassian.crowd.model.authentication.UserAuthenticationContext
 *  com.atlassian.crowd.model.authentication.ValidationFactor
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.group.GroupWithAttributes
 *  com.atlassian.crowd.model.group.Membership
 *  com.atlassian.crowd.model.user.User
 *  com.atlassian.crowd.model.user.UserWithAttributes
 *  com.atlassian.crowd.search.builder.Restriction
 *  com.atlassian.crowd.search.query.entity.restriction.Property
 *  com.atlassian.crowd.search.query.entity.restriction.constants.UserTermKeys
 *  com.atlassian.crowd.service.client.ClientProperties
 *  com.atlassian.crowd.service.client.CrowdClient
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.Validate
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.integration.rest.service;

import com.atlassian.crowd.embedded.api.Attributes;
import com.atlassian.crowd.embedded.api.PasswordCredential;
import com.atlassian.crowd.embedded.api.SearchRestriction;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.crowd.event.EventTokenExpiredException;
import com.atlassian.crowd.event.Events;
import com.atlassian.crowd.event.IncrementalSynchronisationNotAvailableException;
import com.atlassian.crowd.exception.ApplicationAccessDeniedException;
import com.atlassian.crowd.exception.ApplicationPermissionException;
import com.atlassian.crowd.exception.ExpiredCredentialException;
import com.atlassian.crowd.exception.GroupNotFoundException;
import com.atlassian.crowd.exception.InactiveAccountException;
import com.atlassian.crowd.exception.InvalidAuthenticationException;
import com.atlassian.crowd.exception.InvalidCredentialException;
import com.atlassian.crowd.exception.InvalidEmailAddressException;
import com.atlassian.crowd.exception.InvalidGroupException;
import com.atlassian.crowd.exception.InvalidTokenException;
import com.atlassian.crowd.exception.InvalidUserException;
import com.atlassian.crowd.exception.MembershipAlreadyExistsException;
import com.atlassian.crowd.exception.MembershipNotFoundException;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.exception.UnsupportedCrowdApiException;
import com.atlassian.crowd.exception.UserNotFoundException;
import com.atlassian.crowd.exception.WebhookNotFoundException;
import com.atlassian.crowd.integration.rest.entity.AuthenticationContextEntity;
import com.atlassian.crowd.integration.rest.entity.CookieConfigEntity;
import com.atlassian.crowd.integration.rest.entity.ErrorEntity;
import com.atlassian.crowd.integration.rest.entity.EventEntityList;
import com.atlassian.crowd.integration.rest.entity.GroupEntity;
import com.atlassian.crowd.integration.rest.entity.GroupEntityList;
import com.atlassian.crowd.integration.rest.entity.MembershipsEntity;
import com.atlassian.crowd.integration.rest.entity.MultiValuedAttributeEntityList;
import com.atlassian.crowd.integration.rest.entity.PasswordEntity;
import com.atlassian.crowd.integration.rest.entity.RenameEntity;
import com.atlassian.crowd.integration.rest.entity.SearchRestrictionEntity;
import com.atlassian.crowd.integration.rest.entity.SessionEntity;
import com.atlassian.crowd.integration.rest.entity.UserEntity;
import com.atlassian.crowd.integration.rest.entity.UserEntityList;
import com.atlassian.crowd.integration.rest.entity.ValidationFactorEntityList;
import com.atlassian.crowd.integration.rest.entity.WebhookEntity;
import com.atlassian.crowd.integration.rest.service.BasicAuthRestExecutor;
import com.atlassian.crowd.integration.rest.service.CrowdRestException;
import com.atlassian.crowd.integration.rest.service.DefaultHttpClientProvider;
import com.atlassian.crowd.integration.rest.service.RestExecutor;
import com.atlassian.crowd.integration.rest.util.EntityTranslator;
import com.atlassian.crowd.integration.rest.util.SearchRestrictionEntityTranslator;
import com.atlassian.crowd.model.authentication.CookieConfiguration;
import com.atlassian.crowd.model.authentication.Session;
import com.atlassian.crowd.model.authentication.UserAuthenticationContext;
import com.atlassian.crowd.model.authentication.ValidationFactor;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.GroupWithAttributes;
import com.atlassian.crowd.model.group.Membership;
import com.atlassian.crowd.model.user.UserWithAttributes;
import com.atlassian.crowd.search.builder.Restriction;
import com.atlassian.crowd.search.query.entity.restriction.Property;
import com.atlassian.crowd.search.query.entity.restriction.constants.UserTermKeys;
import com.atlassian.crowd.service.client.ClientProperties;
import com.atlassian.crowd.service.client.CrowdClient;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestCrowdClient
implements CrowdClient {
    private static final Logger logger = LoggerFactory.getLogger(RestCrowdClient.class);
    private static final String USER_NULL_ERROR_MSG = "User must not be null";
    private static final String USERNAME_NULL_ERROR_MSG = "Username must not be null";
    private final RestExecutor executor;

    @Deprecated
    public RestCrowdClient(ClientProperties clientProperties) {
        this(BasicAuthRestExecutor.createFrom(clientProperties, new DefaultHttpClientProvider().getClient(clientProperties)));
    }

    public RestCrowdClient(RestExecutor executor) {
        this.executor = executor;
    }

    public com.atlassian.crowd.model.user.User getUser(String name) throws UserNotFoundException, ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException {
        try {
            return (com.atlassian.crowd.model.user.User)this.executor.get("/user?username=%s", name).andReceive(UserEntity.class);
        }
        catch (CrowdRestException e) {
            RestCrowdClient.handleUserNotFound(e.getErrorEntity(), name);
            throw RestCrowdClient.handleCommonExceptions(e);
        }
    }

    public UserWithAttributes getUserWithAttributes(String name) throws UserNotFoundException, ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException {
        try {
            return this.executor.get("/user?username=%s&expand=attributes", name).andReceive(UserEntity.class);
        }
        catch (CrowdRestException e) {
            RestCrowdClient.handleUserNotFound(e.getErrorEntity(), name);
            throw RestCrowdClient.handleCommonExceptions(e);
        }
    }

    public com.atlassian.crowd.model.user.User getUserByKey(String key) throws UserNotFoundException, OperationFailedException, ApplicationPermissionException, InvalidAuthenticationException {
        try {
            return (com.atlassian.crowd.model.user.User)this.executor.get("/user?key=%s", key).andReceive(UserEntity.class);
        }
        catch (CrowdRestException e) {
            if (e.getErrorEntity().getReason() == ErrorEntity.ErrorReason.USER_NOT_FOUND) {
                UserNotFoundException.throwNotFoundByExternalId((String)key);
            }
            throw RestCrowdClient.handleCommonExceptions(e);
        }
    }

    public com.atlassian.crowd.model.user.User authenticateUser(String username, String password) throws UserNotFoundException, InactiveAccountException, ExpiredCredentialException, ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException {
        try {
            return (com.atlassian.crowd.model.user.User)this.executor.post(new PasswordEntity(password), "/authentication?username=%s", username).andReceive(UserEntity.class);
        }
        catch (CrowdRestException e) {
            RestCrowdClient.handleUserNotFound(e.getErrorEntity(), username);
            RestCrowdClient.handleInactiveUserAccount(e.getErrorEntity(), username);
            RestCrowdClient.handleExpiredUserCredential(e.getErrorEntity());
            RestCrowdClient.handleInvalidUserAuthentication(e.getErrorEntity(), username);
            throw RestCrowdClient.handleCommonExceptions(e);
        }
    }

    public com.atlassian.crowd.model.user.User userAuthenticated(String username) throws ApplicationPermissionException, OperationFailedException, InvalidAuthenticationException, UserNotFoundException, InactiveAccountException {
        try {
            return (com.atlassian.crowd.model.user.User)this.executor.post(new PasswordEntity(null), "/authentication/notify?username=%s", username).andReceive(UserEntity.class);
        }
        catch (CrowdRestException e) {
            if (e.getStatusCode() == 404) {
                throw new UnsupportedCrowdApiException("3.3.0", "for updating user during remote authentication");
            }
            RestCrowdClient.handleUserNotFound(e.getErrorEntity(), username);
            RestCrowdClient.handleInactiveUserAccount(e.getErrorEntity(), username);
            throw RestCrowdClient.handleCommonExceptions(e);
        }
    }

    public UserWithAttributes addUser(UserWithAttributes user, PasswordCredential passwordCredential) throws ApplicationPermissionException, InvalidUserException, InvalidCredentialException, InvalidAuthenticationException, OperationFailedException {
        Validate.notNull((Object)user, (String)USER_NULL_ERROR_MSG, (Object[])new Object[0]);
        Validate.notNull((Object)user.getName(), (String)USERNAME_NULL_ERROR_MSG, (Object[])new Object[0]);
        UserEntity userEntity = EntityTranslator.toUserEntity((com.atlassian.crowd.model.user.User)user, passwordCredential);
        userEntity.setAttributes(EntityTranslator.toMultiValuedAttributeEntityList((Attributes)user));
        try {
            UserEntity returnedUser = this.executor.post(userEntity, "/user", new Object[0]).andOptionallyReceive(UserEntity.class);
            if (returnedUser != null && returnedUser.getKeys().containsAll(user.getKeys())) {
                return returnedUser;
            }
            return null;
        }
        catch (CrowdRestException e) {
            RestCrowdClient.handleInvalidUser(e.getErrorEntity(), (com.atlassian.crowd.model.user.User)user);
            RestCrowdClient.handleInvalidCredential(e.getErrorEntity());
            throw RestCrowdClient.handleCommonExceptions(e);
        }
    }

    public UserWithAttributes addUser(com.atlassian.crowd.model.user.User user, PasswordCredential passwordCredential) throws ApplicationPermissionException, InvalidUserException, InvalidCredentialException, InvalidAuthenticationException, OperationFailedException {
        return this.addUser(EntityTranslator.toUserEntity(user), passwordCredential);
    }

    public void updateUser(com.atlassian.crowd.model.user.User user) throws InvalidUserException, UserNotFoundException, ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException {
        Validate.notNull((Object)user, (String)USER_NULL_ERROR_MSG, (Object[])new Object[0]);
        Validate.notNull((Object)user.getName(), (String)USERNAME_NULL_ERROR_MSG, (Object[])new Object[0]);
        UserEntity restUser = EntityTranslator.toUserEntity(user);
        try {
            this.executor.put(restUser, "/user?username=%s", user.getName()).andCheckResponse();
        }
        catch (CrowdRestException e) {
            RestCrowdClient.handleInvalidUser(e.getErrorEntity(), user);
            RestCrowdClient.handleUserNotFound(e.getErrorEntity(), user.getName());
            throw RestCrowdClient.handleCommonExceptions(e);
        }
    }

    public com.atlassian.crowd.model.user.User renameUser(String oldUsername, String newUsername) throws InvalidUserException, UserNotFoundException, OperationFailedException, ApplicationPermissionException, InvalidAuthenticationException {
        Preconditions.checkNotNull((Object)oldUsername, (Object)"Old username must not be null");
        Preconditions.checkNotNull((Object)newUsername, (Object)"New username must not be null");
        RenameEntity newName = new RenameEntity(newUsername);
        try {
            return (com.atlassian.crowd.model.user.User)this.executor.post(newName, "/user/rename?username=%s", oldUsername).andReceive(UserEntity.class);
        }
        catch (CrowdRestException e) {
            if (e.getStatusCode() == 404) {
                throw new UnsupportedCrowdApiException("2.8.4", "for renaming users remotely");
            }
            RestCrowdClient.handleInvalidUser(e.getErrorEntity(), this.getUser(oldUsername));
            RestCrowdClient.handleUserNotFound(e.getErrorEntity(), oldUsername);
            throw RestCrowdClient.handleCommonExceptions(e);
        }
    }

    public void updateUserCredential(String username, String password) throws InvalidCredentialException, UserNotFoundException, ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException {
        try {
            if (password == null) {
                this.executor.delete("/user/password?username=%s", username).andCheckResponse();
            } else {
                this.executor.put(new PasswordEntity(password), "/user/password?username=%s", username).andCheckResponse();
            }
        }
        catch (CrowdRestException e) {
            RestCrowdClient.handleUserNotFound(e.getErrorEntity(), username);
            RestCrowdClient.handleInvalidCredential(e.getErrorEntity());
            throw RestCrowdClient.handleCommonExceptions(e);
        }
    }

    public void requestPasswordReset(String username) throws UserNotFoundException, InvalidEmailAddressException, ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException {
        try {
            this.executor.postEmpty("/user/mail/password?username=%s", username).andCheckResponse();
        }
        catch (CrowdRestException e) {
            RestCrowdClient.handleUserNotFound(e.getErrorEntity(), username);
            RestCrowdClient.handleInvalidEmail(e.getErrorEntity());
            throw RestCrowdClient.handleCommonExceptions(e);
        }
    }

    public void requestUsernames(String email) throws InvalidEmailAddressException, ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException {
        try {
            this.executor.postEmpty("/user/mail/usernames?email=%s", email).andCheckResponse();
        }
        catch (CrowdRestException e) {
            RestCrowdClient.handleInvalidEmail(e.getErrorEntity());
            throw RestCrowdClient.handleCommonExceptions(e);
        }
    }

    public void storeUserAttributes(String username, Map<String, Set<String>> attributes) throws UserNotFoundException, ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException {
        MultiValuedAttributeEntityList restAttributes = EntityTranslator.toMultiValuedAttributeEntityList(attributes);
        this.storeUserAttributes(username, restAttributes);
    }

    private void storeUserAttributes(String username, MultiValuedAttributeEntityList restAttributes) throws UserNotFoundException, ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException {
        try {
            this.executor.post(restAttributes, "/user/attribute?username=%s", username).andCheckResponse();
        }
        catch (CrowdRestException e) {
            RestCrowdClient.handleUserNotFound(e.getErrorEntity(), username);
            throw RestCrowdClient.handleCommonExceptions(e);
        }
    }

    public void removeUserAttributes(String username, String attributeName) throws UserNotFoundException, ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException {
        try {
            this.executor.delete("/user/attribute?username=%s&attributename=%s", username, attributeName).andCheckResponse();
        }
        catch (CrowdRestException e) {
            RestCrowdClient.handleUserNotFound(e.getErrorEntity(), username);
            throw RestCrowdClient.handleCommonExceptions(e);
        }
    }

    public void removeUser(String username) throws UserNotFoundException, ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException {
        try {
            this.executor.delete("/user?username=%s", username).andCheckResponse();
        }
        catch (CrowdRestException e) {
            RestCrowdClient.handleUserNotFound(e.getErrorEntity(), username);
            throw RestCrowdClient.handleCommonExceptions(e);
        }
    }

    public Group getGroup(String name) throws GroupNotFoundException, ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException {
        try {
            return (Group)this.executor.get("/group?groupname=%s", name).andReceive(GroupEntity.class);
        }
        catch (CrowdRestException e) {
            RestCrowdClient.handleGroupNotFound(e.getErrorEntity(), name);
            throw RestCrowdClient.handleCommonExceptions(e);
        }
    }

    public GroupWithAttributes getGroupWithAttributes(String name) throws GroupNotFoundException, ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException {
        try {
            return this.executor.get("/group?groupname=%s&expand=attributes", name).andReceive(GroupEntity.class);
        }
        catch (CrowdRestException e) {
            RestCrowdClient.handleGroupNotFound(e.getErrorEntity(), name);
            throw RestCrowdClient.handleCommonExceptions(e);
        }
    }

    public void addGroup(Group group) throws InvalidGroupException, ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException {
        GroupEntity restGroup = EntityTranslator.toGroupEntity(group);
        try {
            this.executor.post(restGroup, "/group", new Object[0]).andCheckResponse();
        }
        catch (CrowdRestException e) {
            RestCrowdClient.handleInvalidGroup(e.getErrorEntity(), group);
            throw RestCrowdClient.handleCommonExceptions(e);
        }
    }

    public void updateGroup(Group group) throws InvalidGroupException, GroupNotFoundException, ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException {
        Validate.notNull((Object)group);
        Validate.notNull((Object)group.getName());
        GroupEntity restGroup = EntityTranslator.toGroupEntity(group);
        try {
            this.executor.put(restGroup, "/group?groupname=%s", group.getName()).andCheckResponse();
        }
        catch (CrowdRestException e) {
            RestCrowdClient.handleInvalidGroup(e.getErrorEntity(), group);
            RestCrowdClient.handleGroupNotFound(e.getErrorEntity(), group.getName());
            throw RestCrowdClient.handleCommonExceptions(e);
        }
    }

    public void storeGroupAttributes(String groupName, Map<String, Set<String>> attributes) throws GroupNotFoundException, ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException {
        MultiValuedAttributeEntityList restAttributes = EntityTranslator.toMultiValuedAttributeEntityList(attributes);
        try {
            this.executor.post(restAttributes, "/group/attribute?groupname=%s", groupName).andCheckResponse();
        }
        catch (CrowdRestException e) {
            RestCrowdClient.handleGroupNotFound(e.getErrorEntity(), groupName);
            throw RestCrowdClient.handleCommonExceptions(e);
        }
    }

    public void removeGroupAttributes(String groupName, String attributeName) throws GroupNotFoundException, ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException {
        try {
            this.executor.delete("/group/attribute?groupname=%s&attributename=%s", groupName, attributeName).andCheckResponse();
        }
        catch (CrowdRestException e) {
            RestCrowdClient.handleGroupNotFound(e.getErrorEntity(), groupName);
            throw RestCrowdClient.handleCommonExceptions(e);
        }
    }

    public void removeGroup(String groupName) throws GroupNotFoundException, ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException {
        try {
            this.executor.delete("/group?groupname=%s", groupName).andCheckResponse();
        }
        catch (CrowdRestException e) {
            RestCrowdClient.handleGroupNotFound(e.getErrorEntity(), groupName);
            throw RestCrowdClient.handleCommonExceptions(e);
        }
    }

    public boolean isUserDirectGroupMember(String username, String groupName) throws ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException {
        try {
            return this.executor.get("/group/user/direct?groupname=%s&username=%s", groupName, username).doesExist();
        }
        catch (CrowdRestException e) {
            throw RestCrowdClient.handleCommonExceptions(e);
        }
    }

    public boolean isUserNestedGroupMember(String username, String groupName) throws ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException {
        try {
            return this.executor.get("/group/user/nested?groupname=%s&username=%s", groupName, username).doesExist();
        }
        catch (CrowdRestException e) {
            throw RestCrowdClient.handleCommonExceptions(e);
        }
    }

    public boolean isGroupDirectGroupMember(String childName, String parentName) throws ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException {
        try {
            return this.executor.get("/group/child-group/direct?groupname=%s&child-groupname=%s", parentName, childName).doesExist();
        }
        catch (CrowdRestException e) {
            throw RestCrowdClient.handleCommonExceptions(e);
        }
    }

    public void addUserToGroup(String username, String groupName) throws GroupNotFoundException, UserNotFoundException, ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException, MembershipAlreadyExistsException {
        UserEntity user = UserEntity.newMinimalInstance(username);
        try {
            this.executor.post(user, "/group/user/direct?groupname=%s", groupName).andCheckResponse();
        }
        catch (CrowdRestException e) {
            RestCrowdClient.handleMembershipAlreadyExists(e.getErrorEntity(), username, groupName);
            RestCrowdClient.handleUserNotFound(e.getErrorEntity(), username);
            RestCrowdClient.handleGroupNotFound(e.getErrorEntity(), groupName);
            throw RestCrowdClient.handleCommonExceptions(e);
        }
    }

    public void addGroupToGroup(String childGroup, String parentGroup) throws GroupNotFoundException, ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException, MembershipAlreadyExistsException {
        GroupEntity group = GroupEntity.newMinimalInstance(childGroup);
        try {
            this.executor.post(group, "/group/child-group/direct?groupname=%s", parentGroup).andCheckResponse();
        }
        catch (CrowdRestException e) {
            RestCrowdClient.handleMembershipAlreadyExists(e.getErrorEntity(), childGroup, parentGroup);
            switch (e.getStatusCode()) {
                case 404: {
                    RestCrowdClient.handleGroupNotFound(e.getErrorEntity(), parentGroup);
                    break;
                }
                case 400: {
                    RestCrowdClient.handleGroupNotFound(e.getErrorEntity(), childGroup);
                }
            }
            throw RestCrowdClient.handleCommonExceptions(e);
        }
    }

    public void removeUserFromGroup(String username, String groupName) throws MembershipNotFoundException, GroupNotFoundException, UserNotFoundException, ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException {
        try {
            this.executor.delete("/group/user/direct?groupname=%s&username=%s", groupName, username).andCheckResponse();
        }
        catch (CrowdRestException e) {
            RestCrowdClient.handleMembershipNotFound(e.getErrorEntity(), username, groupName);
            RestCrowdClient.handleGroupNotFound(e.getErrorEntity(), groupName);
            RestCrowdClient.handleUserNotFound(e.getErrorEntity(), username);
            throw RestCrowdClient.handleCommonExceptions(e);
        }
    }

    public void removeGroupFromGroup(String childGroup, String parentGroup) throws MembershipNotFoundException, GroupNotFoundException, ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException {
        try {
            this.executor.delete("/group/child-group/direct?groupname=%s&child-groupname=%s", parentGroup, childGroup).andCheckResponse();
        }
        catch (CrowdRestException e) {
            RestCrowdClient.handleMembershipNotFound(e.getErrorEntity(), childGroup, parentGroup);
            this.getGroup(childGroup);
            this.getGroup(parentGroup);
            throw RestCrowdClient.handleCommonExceptions(e);
        }
    }

    public void testConnection() throws ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException {
        this.searchUsers((SearchRestriction)Restriction.on((Property)UserTermKeys.USERNAME).isNull(), 0, 1);
    }

    public List<com.atlassian.crowd.model.user.User> searchUsers(SearchRestriction searchRestriction, int startIndex, int maxResults) throws ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException {
        UserEntityList userEntityList;
        SearchRestrictionEntity searchRestrictionEntity = SearchRestrictionEntityTranslator.toSearchRestrictionEntity(searchRestriction);
        try {
            userEntityList = this.executor.post(searchRestrictionEntity, "/search?entity-type=user&start-index=%d&max-results=%d&expand=user", startIndex, maxResults).andReceive(UserEntityList.class);
        }
        catch (CrowdRestException e) {
            throw RestCrowdClient.handleCommonExceptions(e);
        }
        return EntityTranslator.toUserList(userEntityList);
    }

    public List<UserWithAttributes> searchUsersWithAttributes(SearchRestriction searchRestriction, int startIndex, int maxResults) throws ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException {
        UserEntityList userEntityList;
        SearchRestrictionEntity searchRestrictionEntity = SearchRestrictionEntityTranslator.toSearchRestrictionEntity(searchRestriction);
        try {
            userEntityList = this.executor.post(searchRestrictionEntity, "/search?entity-type=user&start-index=%d&max-results=%d&expand=user,attributes", startIndex, maxResults).andReceive(UserEntityList.class);
        }
        catch (CrowdRestException e) {
            throw RestCrowdClient.handleCommonExceptions(e);
        }
        return EntityTranslator.toUserWithAttributesList(userEntityList);
    }

    public List<String> searchUserNames(SearchRestriction searchRestriction, int startIndex, int maxResults) throws ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException {
        UserEntityList userEntityList;
        SearchRestrictionEntity searchRestrictionEntity = SearchRestrictionEntityTranslator.toSearchRestrictionEntity(searchRestriction);
        try {
            userEntityList = this.executor.post(searchRestrictionEntity, "/search?entity-type=user&start-index=%d&max-results=%d", startIndex, maxResults).andReceive(UserEntityList.class);
        }
        catch (CrowdRestException e) {
            throw RestCrowdClient.handleCommonExceptions(e);
        }
        return EntityTranslator.toNameList(userEntityList);
    }

    public List<Group> searchGroups(SearchRestriction searchRestriction, int startIndex, int maxResults) throws ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException {
        GroupEntityList groupEntityList;
        SearchRestrictionEntity searchRestrictionEntity = SearchRestrictionEntityTranslator.toSearchRestrictionEntity(searchRestriction);
        try {
            groupEntityList = this.executor.post(searchRestrictionEntity, "/search?entity-type=group&start-index=%d&max-results=%d&expand=group", startIndex, maxResults).andReceive(GroupEntityList.class);
        }
        catch (CrowdRestException e) {
            throw RestCrowdClient.handleCommonExceptions(e);
        }
        return EntityTranslator.toGroupList(groupEntityList);
    }

    public List<GroupWithAttributes> searchGroupsWithAttributes(SearchRestriction searchRestriction, int startIndex, int maxResults) throws ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException {
        GroupEntityList groupEntityList;
        SearchRestrictionEntity searchRestrictionEntity = SearchRestrictionEntityTranslator.toSearchRestrictionEntity(searchRestriction);
        try {
            groupEntityList = this.executor.post(searchRestrictionEntity, "/search?entity-type=group&start-index=%d&max-results=%d&expand=group,attributes", startIndex, maxResults).andReceive(GroupEntityList.class);
        }
        catch (CrowdRestException e) {
            throw RestCrowdClient.handleCommonExceptions(e);
        }
        return EntityTranslator.toGroupWithAttributesList(groupEntityList);
    }

    public List<String> searchGroupNames(SearchRestriction searchRestriction, int startIndex, int maxResults) throws ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException {
        GroupEntityList groupEntityList;
        SearchRestrictionEntity searchRestrictionEntity = SearchRestrictionEntityTranslator.toSearchRestrictionEntity(searchRestriction);
        try {
            groupEntityList = this.executor.post(searchRestrictionEntity, "/search?entity-type=group&start-index=%d&max-results=%d", startIndex, maxResults).andReceive(GroupEntityList.class);
        }
        catch (CrowdRestException e) {
            throw RestCrowdClient.handleCommonExceptions(e);
        }
        return EntityTranslator.toNameList(groupEntityList);
    }

    public List<com.atlassian.crowd.model.user.User> getUsersOfGroup(String groupName, int startIndex, int maxResults) throws GroupNotFoundException, ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException {
        UserEntityList restUsers;
        try {
            restUsers = this.executor.get("/group/user/direct?groupname=%s&start-index=%d&max-results=%d&expand=user", groupName, startIndex, maxResults).andReceive(UserEntityList.class);
        }
        catch (CrowdRestException e) {
            RestCrowdClient.handleGroupNotFound(e.getErrorEntity(), groupName);
            throw RestCrowdClient.handleCommonExceptions(e);
        }
        return EntityTranslator.toUserList(restUsers);
    }

    public List<String> getNamesOfUsersOfGroup(String groupName, int startIndex, int maxResults) throws GroupNotFoundException, ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException {
        UserEntityList restUsers;
        try {
            restUsers = this.executor.get("/group/user/direct?groupname=%s&start-index=%d&max-results=%d", groupName, startIndex, maxResults).andReceive(UserEntityList.class);
        }
        catch (CrowdRestException e) {
            RestCrowdClient.handleGroupNotFound(e.getErrorEntity(), groupName);
            throw RestCrowdClient.handleCommonExceptions(e);
        }
        return EntityTranslator.toNameList(restUsers);
    }

    public List<Group> getChildGroupsOfGroup(String groupName, int startIndex, int maxResults) throws GroupNotFoundException, ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException {
        GroupEntityList restGroups;
        try {
            restGroups = this.executor.get("/group/child-group/direct?groupname=%s&start-index=%d&max-results=%d&expand=group", groupName, startIndex, maxResults).andReceive(GroupEntityList.class);
        }
        catch (CrowdRestException e) {
            RestCrowdClient.handleGroupNotFound(e.getErrorEntity(), groupName);
            throw RestCrowdClient.handleCommonExceptions(e);
        }
        return EntityTranslator.toGroupList(restGroups);
    }

    public List<String> getNamesOfChildGroupsOfGroup(String groupName, int startIndex, int maxResults) throws GroupNotFoundException, ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException {
        GroupEntityList restGroups;
        try {
            restGroups = this.executor.get("/group/child-group/direct?groupname=%s&start-index=%d&max-results=%d", groupName, startIndex, maxResults).andReceive(GroupEntityList.class);
        }
        catch (CrowdRestException e) {
            RestCrowdClient.handleGroupNotFound(e.getErrorEntity(), groupName);
            throw RestCrowdClient.handleCommonExceptions(e);
        }
        return EntityTranslator.toNameList(restGroups);
    }

    public List<Group> getGroupsForUser(String userName, int startIndex, int maxResults) throws UserNotFoundException, ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException {
        GroupEntityList restGroups;
        try {
            restGroups = this.executor.get("/user/group/direct?username=%s&start-index=%d&max-results=%d&expand=group", userName, startIndex, maxResults).andReceive(GroupEntityList.class);
        }
        catch (CrowdRestException e) {
            RestCrowdClient.handleUserNotFound(e.getErrorEntity(), userName);
            throw RestCrowdClient.handleCommonExceptions(e);
        }
        return EntityTranslator.toGroupList(restGroups);
    }

    public List<GroupWithAttributes> getGroupsWithAttributesForUser(String userName, int startIndex, int maxResults) throws UserNotFoundException, ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException {
        GroupEntityList restGroups;
        try {
            restGroups = this.executor.get("/user/group/direct?username=%s&start-index=%d&max-results=%d&expand=group,attributes", userName, startIndex, maxResults).andReceive(GroupEntityList.class);
        }
        catch (CrowdRestException e) {
            RestCrowdClient.handleUserNotFound(e.getErrorEntity(), userName);
            throw RestCrowdClient.handleCommonExceptions(e);
        }
        return EntityTranslator.toGroupWithAttributesList(restGroups);
    }

    public List<String> getNamesOfGroupsForUser(String userName, int startIndex, int maxResults) throws UserNotFoundException, ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException {
        GroupEntityList restGroups;
        try {
            restGroups = this.executor.get("/user/group/direct?username=%s&start-index=%d&max-results=%d", userName, startIndex, maxResults).andReceive(GroupEntityList.class);
        }
        catch (CrowdRestException e) {
            RestCrowdClient.handleUserNotFound(e.getErrorEntity(), userName);
            throw RestCrowdClient.handleCommonExceptions(e);
        }
        return EntityTranslator.toNameList(restGroups);
    }

    public List<Group> getParentGroupsForGroup(String groupName, int startIndex, int maxResults) throws GroupNotFoundException, ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException {
        GroupEntityList restGroups;
        try {
            restGroups = this.executor.get("/group/parent-group/direct?groupname=%s&start-index=%d&max-results=%d&expand=group", groupName, startIndex, maxResults).andReceive(GroupEntityList.class);
        }
        catch (CrowdRestException e) {
            RestCrowdClient.handleGroupNotFound(e.getErrorEntity(), groupName);
            throw RestCrowdClient.handleCommonExceptions(e);
        }
        return EntityTranslator.toGroupList(restGroups);
    }

    public List<String> getNamesOfParentGroupsForGroup(String groupName, int startIndex, int maxResults) throws GroupNotFoundException, ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException {
        GroupEntityList restGroups;
        try {
            restGroups = this.executor.get("/group/parent-group/direct?groupname=%s&start-index=%d&max-results=%d", groupName, startIndex, maxResults).andReceive(GroupEntityList.class);
        }
        catch (CrowdRestException e) {
            RestCrowdClient.handleGroupNotFound(e.getErrorEntity(), groupName);
            throw RestCrowdClient.handleCommonExceptions(e);
        }
        return EntityTranslator.toNameList(restGroups);
    }

    public List<com.atlassian.crowd.model.user.User> getNestedUsersOfGroup(String groupName, int startIndex, int maxResults) throws GroupNotFoundException, ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException {
        UserEntityList restUsers;
        try {
            restUsers = this.executor.get("/group/user/nested?groupname=%s&start-index=%d&max-results=%d&expand=user", groupName, startIndex, maxResults).andReceive(UserEntityList.class);
        }
        catch (CrowdRestException e) {
            RestCrowdClient.handleGroupNotFound(e.getErrorEntity(), groupName);
            throw RestCrowdClient.handleCommonExceptions(e);
        }
        return EntityTranslator.toUserList(restUsers);
    }

    public List<String> getNamesOfNestedUsersOfGroup(String groupName, int startIndex, int maxResults) throws GroupNotFoundException, ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException {
        UserEntityList restUsers;
        try {
            restUsers = this.executor.get("/group/user/nested?groupname=%s&start-index=%d&max-results=%d", groupName, startIndex, maxResults).andReceive(UserEntityList.class);
        }
        catch (CrowdRestException e) {
            RestCrowdClient.handleGroupNotFound(e.getErrorEntity(), groupName);
            throw RestCrowdClient.handleCommonExceptions(e);
        }
        return EntityTranslator.toNameList(restUsers);
    }

    public List<Group> getNestedChildGroupsOfGroup(String groupName, int startIndex, int maxResults) throws GroupNotFoundException, ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException {
        GroupEntityList restGroups;
        try {
            restGroups = this.executor.get("/group/child-group/nested?groupname=%s&start-index=%d&max-results=%d&expand=group", groupName, startIndex, maxResults).andReceive(GroupEntityList.class);
        }
        catch (CrowdRestException e) {
            RestCrowdClient.handleGroupNotFound(e.getErrorEntity(), groupName);
            throw RestCrowdClient.handleCommonExceptions(e);
        }
        return EntityTranslator.toGroupList(restGroups);
    }

    public List<String> getNamesOfNestedChildGroupsOfGroup(String groupName, int startIndex, int maxResults) throws GroupNotFoundException, ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException {
        GroupEntityList restGroups;
        try {
            restGroups = this.executor.get("/group/child-group/nested?groupname=%s&start-index=%d&max-results=%d", groupName, startIndex, maxResults).andReceive(GroupEntityList.class);
        }
        catch (CrowdRestException e) {
            RestCrowdClient.handleGroupNotFound(e.getErrorEntity(), groupName);
            throw RestCrowdClient.handleCommonExceptions(e);
        }
        return EntityTranslator.toNameList(restGroups);
    }

    public List<Group> getGroupsForNestedUser(String userName, int startIndex, int maxResults) throws UserNotFoundException, ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException {
        GroupEntityList restGroups;
        try {
            restGroups = this.executor.get("/user/group/nested?username=%s&start-index=%d&max-results=%d&expand=group", userName, startIndex, maxResults).andReceive(GroupEntityList.class);
        }
        catch (CrowdRestException e) {
            RestCrowdClient.handleUserNotFound(e.getErrorEntity(), userName);
            throw RestCrowdClient.handleCommonExceptions(e);
        }
        return EntityTranslator.toGroupList(restGroups);
    }

    public List<String> getNamesOfGroupsForNestedUser(String userName, int startIndex, int maxResults) throws UserNotFoundException, ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException {
        GroupEntityList groupEntityList;
        try {
            groupEntityList = this.executor.get("/user/group/nested?username=%s&start-index=%d&max-results=%d", userName, startIndex, maxResults).andReceive(GroupEntityList.class);
        }
        catch (CrowdRestException e) {
            RestCrowdClient.handleUserNotFound(e.getErrorEntity(), userName);
            throw RestCrowdClient.handleCommonExceptions(e);
        }
        return EntityTranslator.toNameList(groupEntityList);
    }

    public List<Group> getParentGroupsForNestedGroup(String groupName, int startIndex, int maxResults) throws GroupNotFoundException, ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException {
        GroupEntityList restGroups;
        try {
            restGroups = this.executor.get("/group/parent-group/nested?groupname=%s&start-index=%d&max-results=%d&expand=group", groupName, startIndex, maxResults).andReceive(GroupEntityList.class);
        }
        catch (CrowdRestException e) {
            RestCrowdClient.handleGroupNotFound(e.getErrorEntity(), groupName);
            throw RestCrowdClient.handleCommonExceptions(e);
        }
        return EntityTranslator.toGroupList(restGroups);
    }

    public List<String> getNamesOfParentGroupsForNestedGroup(String groupName, int startIndex, int maxResults) throws GroupNotFoundException, ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException {
        GroupEntityList restGroups;
        try {
            restGroups = this.executor.get("/group/parent-group/nested?groupname=%s&start-index=%d&max-results=%d", groupName, startIndex, maxResults).andReceive(GroupEntityList.class);
        }
        catch (CrowdRestException e) {
            RestCrowdClient.handleGroupNotFound(e.getErrorEntity(), groupName);
            throw RestCrowdClient.handleCommonExceptions(e);
        }
        return EntityTranslator.toNameList(restGroups);
    }

    public Iterable<Membership> getMemberships() throws OperationFailedException, ApplicationPermissionException, InvalidAuthenticationException, UnsupportedCrowdApiException {
        MembershipsEntity memberships;
        try {
            memberships = this.executor.get("/group/membership", new Object[0]).ignoreErrorEntityForStatusCode(404).andReceive(MembershipsEntity.class);
        }
        catch (CrowdRestException e) {
            if (e.getStatusCode() == 404) {
                throw new UnsupportedCrowdApiException("1.1", "to retrieve membership data with a single request");
            }
            throw RestCrowdClient.handleCommonExceptions(e);
        }
        return Collections.unmodifiableList(memberships.getList());
    }

    public com.atlassian.crowd.model.user.User findUserFromSSOToken(String token) throws InvalidTokenException, ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException {
        try {
            return this.executor.get("/session/%s?expand=user", token).andReceive(SessionEntity.class).getUser();
        }
        catch (CrowdRestException e) {
            RestCrowdClient.handleInvalidSsoToken(e.getErrorEntity());
            throw RestCrowdClient.handleCommonExceptions(e);
        }
    }

    public String authenticateSSOUser(UserAuthenticationContext userAuthenticationContext) throws ApplicationAccessDeniedException, ExpiredCredentialException, InactiveAccountException, ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException {
        Validate.notNull((Object)userAuthenticationContext);
        Validate.notNull((Object)userAuthenticationContext.getName());
        AuthenticationContextEntity authContextEntity = AuthenticationContextEntity.newInstance(userAuthenticationContext);
        try {
            return this.executor.post(authContextEntity, "/session?validate-password=true", new Object[0]).andReceive(SessionEntity.class).getToken();
        }
        catch (CrowdRestException e) {
            ErrorEntity errorEntity = e.getErrorEntity();
            RestCrowdClient.handleInvalidUserAuthentication(errorEntity, userAuthenticationContext.getName());
            RestCrowdClient.handleInactiveUserAccount(errorEntity, userAuthenticationContext.getName());
            RestCrowdClient.handleExpiredUserCredential(errorEntity);
            RestCrowdClient.handleApplicationAccessDenied(errorEntity);
            throw RestCrowdClient.handleCommonExceptions(e);
        }
    }

    public String authenticateSSOUser(UserAuthenticationContext userAuthenticationContext, long duration) throws ApplicationAccessDeniedException, ExpiredCredentialException, InactiveAccountException, ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException {
        Validate.notNull((Object)userAuthenticationContext);
        Validate.notNull((Object)userAuthenticationContext.getName());
        AuthenticationContextEntity authContextEntity = AuthenticationContextEntity.newInstance(userAuthenticationContext);
        try {
            return this.executor.post(authContextEntity, "/session?validate-password=true&duration=%d", duration).andReceive(SessionEntity.class).getToken();
        }
        catch (CrowdRestException e) {
            ErrorEntity errorEntity = e.getErrorEntity();
            RestCrowdClient.handleInvalidUserAuthentication(errorEntity, userAuthenticationContext.getName());
            RestCrowdClient.handleInactiveUserAccount(errorEntity, userAuthenticationContext.getName());
            RestCrowdClient.handleExpiredUserCredential(errorEntity);
            RestCrowdClient.handleApplicationAccessDenied(errorEntity);
            throw RestCrowdClient.handleCommonExceptions(e);
        }
    }

    public String authenticateSSOUserWithoutValidatingPassword(UserAuthenticationContext userAuthenticationContext) throws ApplicationAccessDeniedException, InactiveAccountException, ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException {
        AuthenticationContextEntity authContextEntity = AuthenticationContextEntity.newInstance(userAuthenticationContext);
        try {
            return this.executor.post(authContextEntity, "/session?validate-password=false", new Object[0]).andReceive(SessionEntity.class).getToken();
        }
        catch (CrowdRestException e) {
            ErrorEntity errorEntity = e.getErrorEntity();
            RestCrowdClient.handleInvalidUserAuthentication(errorEntity, userAuthenticationContext.getName());
            RestCrowdClient.handleInactiveUserAccount(errorEntity, userAuthenticationContext.getName());
            RestCrowdClient.handleApplicationAccessDenied(errorEntity);
            throw RestCrowdClient.handleCommonExceptions(e);
        }
    }

    public void validateSSOAuthentication(String token, List<ValidationFactor> validationFactors) throws InvalidTokenException, ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException {
        ValidationFactorEntityList validationFactorEntityList = ValidationFactorEntityList.newInstance(validationFactors);
        try {
            this.executor.post(validationFactorEntityList, "/session/%s", token).andCheckResponse();
        }
        catch (CrowdRestException e) {
            RestCrowdClient.handleInvalidSsoToken(e.getErrorEntity());
            throw RestCrowdClient.handleCommonExceptions(e);
        }
    }

    public Session validateSSOAuthenticationAndGetSession(String token, List<ValidationFactor> validationFactors) throws InvalidTokenException, ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException {
        ValidationFactorEntityList validationFactorEntityList = ValidationFactorEntityList.newInstance(validationFactors);
        try {
            return this.executor.post(validationFactorEntityList, "/session/%s", token).andReceive(SessionEntity.class);
        }
        catch (CrowdRestException e) {
            RestCrowdClient.handleInvalidSsoToken(e.getErrorEntity());
            throw RestCrowdClient.handleCommonExceptions(e);
        }
    }

    public void invalidateSSOToken(String token) throws ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException {
        Preconditions.checkArgument((!token.isEmpty() ? 1 : 0) != 0, (Object)"The empty string cannot be invalidated as an SSO token");
        try {
            this.executor.delete("/session/%s", token).andCheckResponse();
        }
        catch (CrowdRestException e) {
            throw RestCrowdClient.handleCommonExceptions(e);
        }
    }

    public void invalidateSSOTokensForUser(String username) throws OperationFailedException, ApplicationPermissionException, InvalidAuthenticationException, UnsupportedCrowdApiException {
        try {
            this.executor.delete("/session?username=%s", username).ignoreErrorEntityForStatusCode(405).andCheckResponse();
        }
        catch (CrowdRestException e) {
            if (e.getStatusCode() == 405) {
                throw new UnsupportedCrowdApiException("1.3", "for bulk session invalidation");
            }
            throw RestCrowdClient.handleCommonExceptions(e);
        }
    }

    public void invalidateSSOTokensForUser(String username, String exclude) throws OperationFailedException, ApplicationPermissionException, InvalidAuthenticationException, UnsupportedCrowdApiException {
        try {
            this.executor.delete("/session?username=%s&exclude=%s", username, exclude).ignoreErrorEntityForStatusCode(405).andCheckResponse();
        }
        catch (CrowdRestException e) {
            if (e.getStatusCode() == 405) {
                throw new UnsupportedCrowdApiException("1.3", "for bulk session invalidation");
            }
            throw RestCrowdClient.handleCommonExceptions(e);
        }
    }

    public CookieConfiguration getCookieConfiguration() throws ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException {
        CookieConfigEntity cookieConfig;
        try {
            cookieConfig = this.executor.get("/config/cookie", new Object[0]).andReceive(CookieConfigEntity.class);
        }
        catch (CrowdRestException e) {
            throw RestCrowdClient.handleCommonExceptions(e);
        }
        return new CookieConfiguration(cookieConfig.getDomain(), cookieConfig.isSecure(), cookieConfig.getName());
    }

    public void shutdown() {
        try {
            this.executor.close();
        }
        catch (IOException e) {
            logger.error("Failed to close the REST executor", (Throwable)e);
        }
    }

    public String getCurrentEventToken() throws ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException, IncrementalSynchronisationNotAvailableException {
        try {
            EventEntityList eventEntityList = this.executor.get("/event", new Object[0]).ignoreErrorEntityForStatusCode(404).andReceive(EventEntityList.class);
            if (eventEntityList.isIncrementalSynchronisationAvailable() == null) {
                throw new IncrementalSynchronisationNotAvailableException("Incremental synchronisation is not guaranteed to be available");
            }
            if (!eventEntityList.isIncrementalSynchronisationAvailable().booleanValue()) {
                throw new IncrementalSynchronisationNotAvailableException();
            }
            return eventEntityList.getNewEventToken();
        }
        catch (CrowdRestException e) {
            if (e.getStatusCode() == 404) {
                throw new UnsupportedCrowdApiException("1.2", "for event-based synchronisation");
            }
            throw RestCrowdClient.handleCommonExceptions(e);
        }
    }

    public Events getNewEvents(String eventToken) throws EventTokenExpiredException, ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException {
        try {
            return EntityTranslator.toEvents(this.executor.get("/event/%s", eventToken).ignoreErrorEntityForStatusCode(404).andReceive(EventEntityList.class));
        }
        catch (CrowdRestException e) {
            if (e.getStatusCode() == 404) {
                throw new UnsupportedCrowdApiException("1.2", "for event-based synchronisation");
            }
            RestCrowdClient.handleEventTokenExpiredException(e.getErrorEntity());
            throw RestCrowdClient.handleCommonExceptions(e);
        }
    }

    public String getWebhook(long webhookId) throws InvalidAuthenticationException, ApplicationPermissionException, OperationFailedException, WebhookNotFoundException {
        try {
            WebhookEntity webhookEntity = this.executor.get("/webhook/%d", webhookId).andReceive(WebhookEntity.class);
            return webhookEntity.getEndpointUrl();
        }
        catch (CrowdRestException e) {
            RestCrowdClient.handleWebhookNotFound(e.getErrorEntity(), webhookId);
            throw RestCrowdClient.handleCommonExceptions(e);
        }
    }

    public long registerWebhook(String endpointUrl, @Nullable String token) throws ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException {
        try {
            WebhookEntity webhookEntity = new WebhookEntity(endpointUrl, token);
            WebhookEntity returnedWebhookEntity = this.executor.post(webhookEntity, "/webhook", new Object[0]).ignoreErrorEntityForStatusCode(404).andReceive(WebhookEntity.class);
            return returnedWebhookEntity.getId();
        }
        catch (CrowdRestException e) {
            if (e.getStatusCode() == 404) {
                throw new UnsupportedCrowdApiException("1.4", "for web hook synchronisation");
            }
            throw RestCrowdClient.handleCommonExceptions(e);
        }
    }

    public void unregisterWebhook(long webhookId) throws ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException, WebhookNotFoundException {
        try {
            this.executor.delete("/webhook/%d", webhookId).andCheckResponse();
        }
        catch (CrowdRestException e) {
            RestCrowdClient.handleWebhookNotFound(e.getErrorEntity(), webhookId);
            throw RestCrowdClient.handleCommonExceptions(e);
        }
    }

    public void expireAllPasswords() throws ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException {
        try {
            this.executor.postEmpty("/user/expire-all-passwords?confirm=true", new Object[0]).andCheckResponse();
        }
        catch (CrowdRestException e) {
            throw RestCrowdClient.handleCommonExceptions(e);
        }
    }

    private static void handleUserNotFound(ErrorEntity errorEntity, String userName) throws UserNotFoundException {
        if (errorEntity.getReason() == ErrorEntity.ErrorReason.USER_NOT_FOUND) {
            throw new UserNotFoundException(userName);
        }
    }

    private static void handleInvalidUserAuthentication(ErrorEntity errorEntity, String userName) throws InvalidAuthenticationException {
        if (errorEntity.getReason() == ErrorEntity.ErrorReason.INVALID_USER_AUTHENTICATION) {
            throw InvalidAuthenticationException.newInstanceWithName((String)userName);
        }
    }

    private static void handleGroupNotFound(ErrorEntity errorEntity, String groupName) throws GroupNotFoundException {
        if (errorEntity.getReason() == ErrorEntity.ErrorReason.GROUP_NOT_FOUND) {
            throw new GroupNotFoundException(groupName);
        }
    }

    private static void handleInvalidUser(ErrorEntity errorEntity, com.atlassian.crowd.model.user.User user) throws InvalidUserException {
        if (errorEntity.getReason() == ErrorEntity.ErrorReason.INVALID_USER) {
            throw new InvalidUserException((User)user, errorEntity.getMessage());
        }
    }

    private static void handleInvalidCredential(ErrorEntity errorEntity) throws InvalidCredentialException {
        if (errorEntity.getReason() == ErrorEntity.ErrorReason.INVALID_CREDENTIAL) {
            throw new InvalidCredentialException(errorEntity.getMessage());
        }
    }

    private static void handleInvalidGroup(ErrorEntity errorEntity, Group group) throws InvalidGroupException {
        if (errorEntity.getReason() == ErrorEntity.ErrorReason.INVALID_GROUP) {
            throw new InvalidGroupException(group, errorEntity.getMessage());
        }
    }

    private static void handleMembershipNotFound(ErrorEntity errorEntity, String childName, String parentName) throws MembershipNotFoundException {
        if (errorEntity.getReason() == ErrorEntity.ErrorReason.MEMBERSHIP_NOT_FOUND) {
            throw new MembershipNotFoundException(childName, parentName);
        }
    }

    private static void handleMembershipAlreadyExists(ErrorEntity errorEntity, String childEntity, String parentEntity) throws MembershipAlreadyExistsException {
        if (errorEntity.getReason() == ErrorEntity.ErrorReason.MEMBERSHIP_ALREADY_EXISTS) {
            throw new MembershipAlreadyExistsException(childEntity, parentEntity);
        }
    }

    private static void handleInvalidSsoToken(ErrorEntity errorEntity) throws InvalidTokenException {
        if (errorEntity.getReason() == ErrorEntity.ErrorReason.INVALID_SSO_TOKEN) {
            throw new InvalidTokenException(errorEntity.getMessage());
        }
    }

    private static void handleInactiveUserAccount(ErrorEntity errorEntity, String userName) throws InactiveAccountException {
        if (errorEntity.getReason() == ErrorEntity.ErrorReason.INACTIVE_ACCOUNT) {
            throw new InactiveAccountException(userName);
        }
    }

    private static void handleExpiredUserCredential(ErrorEntity errorEntity) throws ExpiredCredentialException {
        if (errorEntity.getReason() == ErrorEntity.ErrorReason.EXPIRED_CREDENTIAL) {
            throw new ExpiredCredentialException(errorEntity.getMessage());
        }
    }

    private static void handleInvalidEmail(ErrorEntity errorEntity) throws InvalidEmailAddressException {
        if (errorEntity.getReason() == ErrorEntity.ErrorReason.INVALID_EMAIL) {
            throw new InvalidEmailAddressException(errorEntity.getMessage());
        }
    }

    private static void handleApplicationAccessDenied(ErrorEntity errorEntity) throws ApplicationAccessDeniedException {
        if (errorEntity.getReason() == ErrorEntity.ErrorReason.APPLICATION_ACCESS_DENIED) {
            throw new ApplicationAccessDeniedException(errorEntity.getMessage());
        }
    }

    private static void handleEventTokenExpiredException(ErrorEntity errorEntity) throws EventTokenExpiredException {
        if (errorEntity.getReason() == ErrorEntity.ErrorReason.EVENT_TOKEN_EXPIRED) {
            throw new EventTokenExpiredException(errorEntity.getMessage());
        }
    }

    private static void handleWebhookNotFound(ErrorEntity errorEntity, long webhookId) throws WebhookNotFoundException {
        if (errorEntity.getReason() == ErrorEntity.ErrorReason.WEBHOOK_NOT_FOUND) {
            throw new WebhookNotFoundException(webhookId);
        }
    }

    private static OperationFailedException handleCommonExceptions(CrowdRestException e) throws ApplicationPermissionException, OperationFailedException {
        if (e.getErrorEntity().getReason() == ErrorEntity.ErrorReason.APPLICATION_PERMISSION_DENIED) {
            throw new ApplicationPermissionException(e.getErrorEntity().getMessage(), (Throwable)e);
        }
        throw new OperationFailedException("Error from Crowd server propagated to here via REST API (check the Crowd server logs for details): " + e.getMessage(), (Throwable)e);
    }
}

