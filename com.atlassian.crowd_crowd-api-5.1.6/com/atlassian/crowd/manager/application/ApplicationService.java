/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
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
 *  com.atlassian.crowd.model.application.Application
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.group.GroupWithAttributes
 *  com.atlassian.crowd.model.group.Membership
 *  com.atlassian.crowd.model.user.User
 *  com.atlassian.crowd.model.user.UserWithAttributes
 *  com.atlassian.crowd.model.webhook.Webhook
 *  javax.annotation.Nullable
 */
package com.atlassian.crowd.manager.application;

import com.atlassian.annotations.ExperimentalApi;
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
import com.atlassian.crowd.manager.application.PagedSearcher;
import com.atlassian.crowd.manager.application.PagingNotSupportedException;
import com.atlassian.crowd.manager.avatar.AvatarReference;
import com.atlassian.crowd.manager.webhook.InvalidWebhookEndpointException;
import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.GroupTemplate;
import com.atlassian.crowd.model.group.GroupWithAttributes;
import com.atlassian.crowd.model.group.Membership;
import com.atlassian.crowd.model.user.User;
import com.atlassian.crowd.model.user.UserTemplate;
import com.atlassian.crowd.model.user.UserTemplateWithAttributes;
import com.atlassian.crowd.model.user.UserTemplateWithCredentialAndAttributes;
import com.atlassian.crowd.model.user.UserWithAttributes;
import com.atlassian.crowd.model.webhook.Webhook;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import com.atlassian.crowd.search.query.membership.MembershipQuery;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public interface ApplicationService {
    public User authenticateUser(Application var1, String var2, PasswordCredential var3) throws OperationFailedException, InactiveAccountException, InvalidAuthenticationException, ExpiredCredentialException, UserNotFoundException;

    public boolean isUserAuthorised(Application var1, String var2);

    @Deprecated
    public boolean isUserAuthorised(Application var1, User var2);

    public void addAllUsers(Application var1, Collection<UserTemplateWithCredentialAndAttributes> var2) throws ApplicationPermissionException, OperationFailedException, BulkAddFailedException;

    public User findUserByName(Application var1, String var2) throws UserNotFoundException;

    public User findRemoteUserByName(Application var1, String var2) throws UserNotFoundException;

    public UserWithAttributes findUserWithAttributesByName(Application var1, String var2) throws UserNotFoundException;

    public User findUserByKey(Application var1, String var2) throws UserNotFoundException;

    public UserWithAttributes findUserWithAttributesByKey(Application var1, String var2) throws UserNotFoundException;

    @Deprecated
    public User addUser(Application var1, UserTemplate var2, PasswordCredential var3) throws InvalidUserException, OperationFailedException, InvalidCredentialException, ApplicationPermissionException;

    public UserWithAttributes addUser(Application var1, UserTemplateWithAttributes var2, PasswordCredential var3) throws InvalidUserException, OperationFailedException, InvalidCredentialException, ApplicationPermissionException;

    public User updateUser(Application var1, UserTemplate var2) throws InvalidUserException, OperationFailedException, ApplicationPermissionException, UserNotFoundException;

    public User renameUser(Application var1, String var2, String var3) throws UserNotFoundException, OperationFailedException, ApplicationPermissionException, InvalidUserException;

    public void updateUserCredential(Application var1, String var2, PasswordCredential var3) throws OperationFailedException, UserNotFoundException, InvalidCredentialException, ApplicationPermissionException;

    public void storeUserAttributes(Application var1, String var2, Map<String, Set<String>> var3) throws OperationFailedException, ApplicationPermissionException, UserNotFoundException;

    public void removeUserAttributes(Application var1, String var2, String var3) throws OperationFailedException, ApplicationPermissionException, UserNotFoundException;

    public void removeUser(Application var1, String var2) throws OperationFailedException, UserNotFoundException, ApplicationPermissionException;

    public <T> List<T> searchUsers(Application var1, EntityQuery<T> var2);

    public Group findGroupByName(Application var1, String var2) throws GroupNotFoundException;

    public GroupWithAttributes findGroupWithAttributesByName(Application var1, String var2) throws GroupNotFoundException;

    public Group addGroup(Application var1, GroupTemplate var2) throws InvalidGroupException, OperationFailedException, ApplicationPermissionException;

    public Group updateGroup(Application var1, GroupTemplate var2) throws InvalidGroupException, OperationFailedException, ApplicationPermissionException, GroupNotFoundException;

    public void storeGroupAttributes(Application var1, String var2, Map<String, Set<String>> var3) throws OperationFailedException, ApplicationPermissionException, GroupNotFoundException;

    public void removeGroupAttributes(Application var1, String var2, String var3) throws OperationFailedException, ApplicationPermissionException, GroupNotFoundException;

    public void removeGroup(Application var1, String var2) throws OperationFailedException, GroupNotFoundException, ApplicationPermissionException;

    public <T> List<T> searchGroups(Application var1, EntityQuery<T> var2);

    public void addUserToGroup(Application var1, String var2, String var3) throws OperationFailedException, UserNotFoundException, GroupNotFoundException, ApplicationPermissionException, MembershipAlreadyExistsException;

    public void addGroupToGroup(Application var1, String var2, String var3) throws OperationFailedException, GroupNotFoundException, ApplicationPermissionException, InvalidMembershipException, MembershipAlreadyExistsException;

    public void removeUserFromGroup(Application var1, String var2, String var3) throws OperationFailedException, GroupNotFoundException, UserNotFoundException, ApplicationPermissionException, MembershipNotFoundException;

    public void removeGroupFromGroup(Application var1, String var2, String var3) throws OperationFailedException, GroupNotFoundException, ApplicationPermissionException, MembershipNotFoundException;

    public boolean isUserDirectGroupMember(Application var1, String var2, String var3);

    public boolean isGroupDirectGroupMember(Application var1, String var2, String var3);

    public boolean isUserNestedGroupMember(Application var1, String var2, String var3);

    public boolean isGroupNestedGroupMember(Application var1, String var2, String var3);

    public <T> List<T> searchDirectGroupRelationships(Application var1, MembershipQuery<T> var2);

    public <T> List<T> searchNestedGroupRelationships(Application var1, MembershipQuery<T> var2);

    public String getCurrentEventToken(Application var1) throws IncrementalSynchronisationNotAvailableException;

    public Events getNewEvents(Application var1, String var2) throws EventTokenExpiredException, OperationFailedException;

    public Webhook findWebhookById(Application var1, long var2) throws WebhookNotFoundException, ApplicationPermissionException;

    public Webhook registerWebhook(Application var1, String var2, @Nullable String var3) throws InvalidWebhookEndpointException;

    public void unregisterWebhook(Application var1, long var2) throws ApplicationPermissionException, WebhookNotFoundException;

    public UserCapabilities getCapabilitiesForNewUsers(Application var1);

    @Nullable
    public URI getUserAvatarLink(Application var1, String var2, int var3) throws UserNotFoundException, DirectoryNotFoundException, OperationFailedException;

    @Nullable
    public AvatarReference getUserAvatar(Application var1, String var2, int var3) throws UserNotFoundException, DirectoryNotFoundException, OperationFailedException;

    public void expireAllPasswords(Application var1) throws OperationFailedException;

    @ExperimentalApi
    public User userAuthenticated(Application var1, String var2) throws UserNotFoundException, OperationFailedException, InactiveAccountException;

    @ExperimentalApi
    public MembershipsIterable getMemberships(Application var1);

    @ExperimentalApi
    public <T> PagedSearcher<T> createPagedUserSearcher(Application var1, EntityQuery<T> var2) throws PagingNotSupportedException;

    @ExperimentalApi
    public <T> PagedSearcher<T> createPagedGroupSearcher(Application var1, EntityQuery<T> var2) throws PagingNotSupportedException;

    @ExperimentalApi
    public static interface MembershipsIterable
    extends Iterable<Membership> {
        public int groupCount();
    }
}

