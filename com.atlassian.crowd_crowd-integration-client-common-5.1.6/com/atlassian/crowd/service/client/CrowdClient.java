/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.crowd.embedded.api.PasswordCredential
 *  com.atlassian.crowd.embedded.api.SearchRestriction
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
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.group.GroupWithAttributes
 *  com.atlassian.crowd.model.group.Membership
 *  com.atlassian.crowd.model.user.User
 *  com.atlassian.crowd.model.user.UserWithAttributes
 *  javax.annotation.Nullable
 */
package com.atlassian.crowd.service.client;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.crowd.embedded.api.PasswordCredential;
import com.atlassian.crowd.embedded.api.SearchRestriction;
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
import com.atlassian.crowd.model.authentication.CookieConfiguration;
import com.atlassian.crowd.model.authentication.Session;
import com.atlassian.crowd.model.authentication.UserAuthenticationContext;
import com.atlassian.crowd.model.authentication.ValidationFactor;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.GroupWithAttributes;
import com.atlassian.crowd.model.group.Membership;
import com.atlassian.crowd.model.user.User;
import com.atlassian.crowd.model.user.UserWithAttributes;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public interface CrowdClient {
    public User getUser(String var1) throws UserNotFoundException, OperationFailedException, ApplicationPermissionException, InvalidAuthenticationException;

    public UserWithAttributes getUserWithAttributes(String var1) throws UserNotFoundException, OperationFailedException, ApplicationPermissionException, InvalidAuthenticationException;

    public User getUserByKey(String var1) throws UserNotFoundException, OperationFailedException, ApplicationPermissionException, InvalidAuthenticationException;

    public User authenticateUser(String var1, String var2) throws UserNotFoundException, InactiveAccountException, ExpiredCredentialException, ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException;

    @ExperimentalApi
    public User userAuthenticated(String var1) throws ApplicationPermissionException, OperationFailedException, InvalidAuthenticationException, UnsupportedCrowdApiException, UserNotFoundException, InactiveAccountException;

    @Nullable
    public UserWithAttributes addUser(UserWithAttributes var1, PasswordCredential var2) throws InvalidUserException, InvalidCredentialException, OperationFailedException, InvalidAuthenticationException, ApplicationPermissionException;

    @Nullable
    @Deprecated
    public UserWithAttributes addUser(User var1, PasswordCredential var2) throws InvalidUserException, InvalidCredentialException, OperationFailedException, InvalidAuthenticationException, ApplicationPermissionException;

    public void updateUser(User var1) throws InvalidUserException, UserNotFoundException, OperationFailedException, ApplicationPermissionException, InvalidAuthenticationException;

    public User renameUser(String var1, String var2) throws InvalidUserException, UserNotFoundException, OperationFailedException, ApplicationPermissionException, InvalidAuthenticationException;

    public void updateUserCredential(String var1, @Nullable String var2) throws UserNotFoundException, InvalidCredentialException, OperationFailedException, ApplicationPermissionException, InvalidAuthenticationException;

    public void storeUserAttributes(String var1, Map<String, Set<String>> var2) throws UserNotFoundException, OperationFailedException, ApplicationPermissionException, InvalidAuthenticationException;

    public void removeUserAttributes(String var1, String var2) throws UserNotFoundException, OperationFailedException, InvalidAuthenticationException, ApplicationPermissionException;

    public void removeUser(String var1) throws UserNotFoundException, OperationFailedException, InvalidAuthenticationException, ApplicationPermissionException;

    public void requestPasswordReset(String var1) throws UserNotFoundException, InvalidEmailAddressException, OperationFailedException, InvalidAuthenticationException, ApplicationPermissionException;

    public void requestUsernames(String var1) throws InvalidEmailAddressException, OperationFailedException, InvalidAuthenticationException, ApplicationPermissionException;

    public Group getGroup(String var1) throws GroupNotFoundException, OperationFailedException, InvalidAuthenticationException, ApplicationPermissionException;

    public GroupWithAttributes getGroupWithAttributes(String var1) throws GroupNotFoundException, OperationFailedException, InvalidAuthenticationException, ApplicationPermissionException;

    public void addGroup(Group var1) throws InvalidGroupException, ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException;

    public void updateGroup(Group var1) throws InvalidGroupException, GroupNotFoundException, OperationFailedException, ApplicationPermissionException, InvalidAuthenticationException;

    public void storeGroupAttributes(String var1, Map<String, Set<String>> var2) throws GroupNotFoundException, OperationFailedException, ApplicationPermissionException, InvalidAuthenticationException;

    public void removeGroupAttributes(String var1, String var2) throws GroupNotFoundException, OperationFailedException, ApplicationPermissionException, InvalidAuthenticationException;

    public void removeGroup(String var1) throws GroupNotFoundException, OperationFailedException, ApplicationPermissionException, InvalidAuthenticationException;

    public boolean isUserDirectGroupMember(String var1, String var2) throws OperationFailedException, ApplicationPermissionException, InvalidAuthenticationException;

    public boolean isUserNestedGroupMember(String var1, String var2) throws OperationFailedException, ApplicationPermissionException, InvalidAuthenticationException;

    public boolean isGroupDirectGroupMember(String var1, String var2) throws OperationFailedException, InvalidAuthenticationException, ApplicationPermissionException;

    public void addUserToGroup(String var1, String var2) throws GroupNotFoundException, UserNotFoundException, OperationFailedException, MembershipAlreadyExistsException, InvalidAuthenticationException, ApplicationPermissionException;

    public void addGroupToGroup(String var1, String var2) throws GroupNotFoundException, UserNotFoundException, OperationFailedException, InvalidAuthenticationException, ApplicationPermissionException, MembershipAlreadyExistsException;

    public void removeUserFromGroup(String var1, String var2) throws MembershipNotFoundException, GroupNotFoundException, UserNotFoundException, ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException;

    public void removeGroupFromGroup(String var1, String var2) throws MembershipNotFoundException, GroupNotFoundException, OperationFailedException, InvalidAuthenticationException, ApplicationPermissionException;

    public void testConnection() throws OperationFailedException, InvalidAuthenticationException, ApplicationPermissionException;

    public List<User> searchUsers(SearchRestriction var1, int var2, int var3) throws OperationFailedException, InvalidAuthenticationException, ApplicationPermissionException;

    public List<UserWithAttributes> searchUsersWithAttributes(SearchRestriction var1, int var2, int var3) throws OperationFailedException, InvalidAuthenticationException, ApplicationPermissionException;

    public List<String> searchUserNames(SearchRestriction var1, int var2, int var3) throws OperationFailedException, InvalidAuthenticationException, ApplicationPermissionException;

    public List<Group> searchGroups(SearchRestriction var1, int var2, int var3) throws OperationFailedException, InvalidAuthenticationException, ApplicationPermissionException;

    public List<GroupWithAttributes> searchGroupsWithAttributes(SearchRestriction var1, int var2, int var3) throws OperationFailedException, InvalidAuthenticationException, ApplicationPermissionException;

    public List<String> searchGroupNames(SearchRestriction var1, int var2, int var3) throws OperationFailedException, InvalidAuthenticationException, ApplicationPermissionException;

    public List<User> getUsersOfGroup(String var1, int var2, int var3) throws GroupNotFoundException, ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException;

    public List<String> getNamesOfUsersOfGroup(String var1, int var2, int var3) throws GroupNotFoundException, ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException;

    public List<Group> getChildGroupsOfGroup(String var1, int var2, int var3) throws GroupNotFoundException, ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException;

    public List<String> getNamesOfChildGroupsOfGroup(String var1, int var2, int var3) throws OperationFailedException, GroupNotFoundException, InvalidAuthenticationException, ApplicationPermissionException;

    public List<Group> getGroupsForUser(String var1, int var2, int var3) throws OperationFailedException, InvalidAuthenticationException, ApplicationPermissionException, UserNotFoundException;

    public List<GroupWithAttributes> getGroupsWithAttributesForUser(String var1, int var2, int var3) throws UserNotFoundException, ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException;

    public List<String> getNamesOfGroupsForUser(String var1, int var2, int var3) throws OperationFailedException, InvalidAuthenticationException, ApplicationPermissionException, UserNotFoundException;

    public List<Group> getParentGroupsForGroup(String var1, int var2, int var3) throws OperationFailedException, InvalidAuthenticationException, ApplicationPermissionException, GroupNotFoundException;

    public List<String> getNamesOfParentGroupsForGroup(String var1, int var2, int var3) throws OperationFailedException, InvalidAuthenticationException, ApplicationPermissionException, GroupNotFoundException;

    public List<User> getNestedUsersOfGroup(String var1, int var2, int var3) throws OperationFailedException, InvalidAuthenticationException, ApplicationPermissionException, GroupNotFoundException;

    public List<String> getNamesOfNestedUsersOfGroup(String var1, int var2, int var3) throws OperationFailedException, InvalidAuthenticationException, ApplicationPermissionException, GroupNotFoundException;

    public List<Group> getNestedChildGroupsOfGroup(String var1, int var2, int var3) throws OperationFailedException, InvalidAuthenticationException, ApplicationPermissionException, GroupNotFoundException;

    public List<String> getNamesOfNestedChildGroupsOfGroup(String var1, int var2, int var3) throws OperationFailedException, InvalidAuthenticationException, ApplicationPermissionException, GroupNotFoundException;

    public List<Group> getGroupsForNestedUser(String var1, int var2, int var3) throws OperationFailedException, InvalidAuthenticationException, ApplicationPermissionException, UserNotFoundException;

    public List<String> getNamesOfGroupsForNestedUser(String var1, int var2, int var3) throws OperationFailedException, InvalidAuthenticationException, ApplicationPermissionException, UserNotFoundException;

    public List<Group> getParentGroupsForNestedGroup(String var1, int var2, int var3) throws OperationFailedException, InvalidAuthenticationException, ApplicationPermissionException, GroupNotFoundException;

    public List<String> getNamesOfParentGroupsForNestedGroup(String var1, int var2, int var3) throws OperationFailedException, InvalidAuthenticationException, ApplicationPermissionException, GroupNotFoundException;

    public Iterable<Membership> getMemberships() throws OperationFailedException, ApplicationPermissionException, InvalidAuthenticationException, UnsupportedCrowdApiException;

    public User findUserFromSSOToken(String var1) throws OperationFailedException, InvalidAuthenticationException, ApplicationPermissionException, InvalidTokenException;

    public String authenticateSSOUser(UserAuthenticationContext var1) throws ApplicationAccessDeniedException, InactiveAccountException, ExpiredCredentialException, ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException;

    public String authenticateSSOUser(UserAuthenticationContext var1, long var2) throws ApplicationAccessDeniedException, InactiveAccountException, ExpiredCredentialException, ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException;

    public String authenticateSSOUserWithoutValidatingPassword(UserAuthenticationContext var1) throws ApplicationPermissionException, InactiveAccountException, ApplicationAccessDeniedException, InvalidAuthenticationException, OperationFailedException;

    public void validateSSOAuthentication(String var1, List<ValidationFactor> var2) throws OperationFailedException, InvalidAuthenticationException, ApplicationPermissionException, InvalidTokenException;

    public Session validateSSOAuthenticationAndGetSession(String var1, List<ValidationFactor> var2) throws InvalidTokenException, ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException;

    public void invalidateSSOToken(String var1) throws OperationFailedException, InvalidAuthenticationException, ApplicationPermissionException;

    public void invalidateSSOTokensForUser(String var1) throws OperationFailedException, ApplicationPermissionException, InvalidAuthenticationException;

    public void invalidateSSOTokensForUser(String var1, String var2) throws OperationFailedException, ApplicationPermissionException, InvalidAuthenticationException;

    public CookieConfiguration getCookieConfiguration() throws OperationFailedException, InvalidAuthenticationException, ApplicationPermissionException;

    public void shutdown();

    public String getCurrentEventToken() throws ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException, IncrementalSynchronisationNotAvailableException;

    public Events getNewEvents(String var1) throws EventTokenExpiredException, ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException;

    public String getWebhook(long var1) throws InvalidAuthenticationException, ApplicationPermissionException, OperationFailedException, WebhookNotFoundException;

    public long registerWebhook(String var1, @Nullable String var2) throws InvalidAuthenticationException, ApplicationPermissionException, OperationFailedException;

    public void unregisterWebhook(long var1) throws ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException, WebhookNotFoundException;

    public void expireAllPasswords() throws ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException;
}

