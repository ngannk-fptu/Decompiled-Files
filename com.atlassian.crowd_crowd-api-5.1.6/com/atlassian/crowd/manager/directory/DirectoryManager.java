/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.DirectorySynchronisationInformation
 *  com.atlassian.crowd.embedded.api.PasswordCredential
 *  com.atlassian.crowd.exception.DirectoryCurrentlySynchronisingException
 *  com.atlassian.crowd.exception.DirectoryInstantiationException
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
 *  com.atlassian.crowd.exception.NestedGroupsNotSupportedException
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.exception.ReadOnlyGroupException
 *  com.atlassian.crowd.exception.UserAlreadyExistsException
 *  com.atlassian.crowd.exception.UserNotFoundException
 *  com.atlassian.crowd.model.application.Application
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.group.GroupWithAttributes
 *  com.atlassian.crowd.model.user.User
 *  com.atlassian.crowd.model.user.UserWithAttributes
 *  com.google.common.collect.ListMultimap
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.crowd.manager.directory;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.DirectorySynchronisationInformation;
import com.atlassian.crowd.embedded.api.PasswordCredential;
import com.atlassian.crowd.exception.DirectoryCurrentlySynchronisingException;
import com.atlassian.crowd.exception.DirectoryInstantiationException;
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
import com.atlassian.crowd.exception.NestedGroupsNotSupportedException;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.exception.ReadOnlyGroupException;
import com.atlassian.crowd.exception.UserAlreadyExistsException;
import com.atlassian.crowd.exception.UserNotFoundException;
import com.atlassian.crowd.manager.avatar.AvatarReference;
import com.atlassian.crowd.manager.directory.BulkAddResult;
import com.atlassian.crowd.manager.directory.DirectoryPermissionException;
import com.atlassian.crowd.manager.directory.SynchronisationMode;
import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.GroupTemplate;
import com.atlassian.crowd.model.group.GroupWithAttributes;
import com.atlassian.crowd.model.user.User;
import com.atlassian.crowd.model.user.UserTemplate;
import com.atlassian.crowd.model.user.UserTemplateWithAttributes;
import com.atlassian.crowd.model.user.UserTemplateWithCredentialAndAttributes;
import com.atlassian.crowd.model.user.UserWithAttributes;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import com.atlassian.crowd.search.query.membership.MembershipQuery;
import com.atlassian.crowd.util.BoundedCount;
import com.google.common.collect.ListMultimap;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface DirectoryManager {
    public Directory addDirectory(Directory var1) throws DirectoryInstantiationException;

    public Directory findDirectoryById(long var1) throws DirectoryNotFoundException;

    @Deprecated
    public List<Directory> findAllDirectories();

    public List<Directory> searchDirectories(EntityQuery<Directory> var1);

    public Directory findDirectoryByName(String var1) throws DirectoryNotFoundException;

    public Directory updateDirectory(Directory var1) throws DirectoryNotFoundException;

    public void removeDirectory(Directory var1) throws DirectoryNotFoundException, DirectoryCurrentlySynchronisingException;

    public User authenticateUser(long var1, String var3, PasswordCredential var4) throws OperationFailedException, InactiveAccountException, InvalidAuthenticationException, ExpiredCredentialException, DirectoryNotFoundException, UserNotFoundException;

    public User findUserByName(long var1, String var3) throws DirectoryNotFoundException, UserNotFoundException, OperationFailedException;

    @Nonnull
    public User findRemoteUserByName(Long var1, String var2) throws OperationFailedException, DirectoryNotFoundException, UserNotFoundException;

    public UserWithAttributes findUserWithAttributesByName(long var1, String var3) throws DirectoryNotFoundException, UserNotFoundException, OperationFailedException;

    public User findUserByExternalId(long var1, String var3) throws DirectoryNotFoundException, UserNotFoundException, OperationFailedException;

    public UserWithAttributes findUserWithAttributesByExternalId(long var1, String var3) throws DirectoryNotFoundException, UserNotFoundException, OperationFailedException;

    public <T> List<T> searchUsers(long var1, EntityQuery<T> var3) throws DirectoryNotFoundException, OperationFailedException;

    public UserWithAttributes addUser(long var1, UserTemplateWithAttributes var3, PasswordCredential var4) throws InvalidCredentialException, InvalidUserException, DirectoryPermissionException, DirectoryNotFoundException, OperationFailedException, UserAlreadyExistsException;

    @Deprecated
    public User addUser(long var1, UserTemplate var3, PasswordCredential var4) throws InvalidCredentialException, InvalidUserException, DirectoryPermissionException, DirectoryNotFoundException, OperationFailedException, UserAlreadyExistsException;

    public User updateUser(long var1, UserTemplate var3) throws DirectoryNotFoundException, UserNotFoundException, DirectoryPermissionException, InvalidUserException, OperationFailedException;

    public User renameUser(long var1, String var3, String var4) throws DirectoryNotFoundException, UserNotFoundException, OperationFailedException, DirectoryPermissionException, InvalidUserException, UserAlreadyExistsException;

    public void storeUserAttributes(long var1, String var3, Map<String, Set<String>> var4) throws DirectoryPermissionException, DirectoryNotFoundException, UserNotFoundException, OperationFailedException;

    public void removeUserAttributes(long var1, String var3, String var4) throws DirectoryPermissionException, DirectoryNotFoundException, UserNotFoundException, OperationFailedException;

    public void updateUserCredential(long var1, String var3, PasswordCredential var4) throws DirectoryPermissionException, InvalidCredentialException, DirectoryNotFoundException, UserNotFoundException, OperationFailedException;

    public void removeUser(long var1, String var3) throws DirectoryNotFoundException, UserNotFoundException, DirectoryPermissionException, OperationFailedException;

    public Group findGroupByName(long var1, String var3) throws GroupNotFoundException, DirectoryNotFoundException, OperationFailedException;

    public GroupWithAttributes findGroupWithAttributesByName(long var1, String var3) throws GroupNotFoundException, DirectoryNotFoundException, OperationFailedException;

    public <T> List<T> searchGroups(long var1, EntityQuery<T> var3) throws DirectoryNotFoundException, OperationFailedException;

    public Group addGroup(long var1, GroupTemplate var3) throws InvalidGroupException, DirectoryPermissionException, DirectoryNotFoundException, OperationFailedException;

    public Group updateGroup(long var1, GroupTemplate var3) throws GroupNotFoundException, DirectoryNotFoundException, DirectoryPermissionException, InvalidGroupException, OperationFailedException, ReadOnlyGroupException;

    public Group renameGroup(long var1, String var3, String var4) throws GroupNotFoundException, DirectoryNotFoundException, DirectoryPermissionException, InvalidGroupException, OperationFailedException;

    public void storeGroupAttributes(long var1, String var3, Map<String, Set<String>> var4) throws DirectoryPermissionException, GroupNotFoundException, DirectoryNotFoundException, OperationFailedException;

    public void removeGroupAttributes(long var1, String var3, String var4) throws DirectoryPermissionException, GroupNotFoundException, DirectoryNotFoundException, OperationFailedException;

    public void removeGroup(long var1, String var3) throws GroupNotFoundException, DirectoryNotFoundException, DirectoryPermissionException, OperationFailedException, ReadOnlyGroupException;

    public boolean isUserDirectGroupMember(long var1, String var3, String var4) throws DirectoryNotFoundException, OperationFailedException;

    public boolean isGroupDirectGroupMember(long var1, String var3, String var4) throws DirectoryNotFoundException, OperationFailedException;

    public void addUserToGroup(long var1, String var3, String var4) throws DirectoryPermissionException, DirectoryNotFoundException, UserNotFoundException, GroupNotFoundException, OperationFailedException, ReadOnlyGroupException, MembershipAlreadyExistsException;

    public void addGroupToGroup(long var1, String var3, String var4) throws DirectoryPermissionException, DirectoryNotFoundException, GroupNotFoundException, InvalidMembershipException, NestedGroupsNotSupportedException, OperationFailedException, ReadOnlyGroupException, MembershipAlreadyExistsException;

    public void removeUserFromGroup(long var1, String var3, String var4) throws DirectoryPermissionException, DirectoryNotFoundException, UserNotFoundException, GroupNotFoundException, MembershipNotFoundException, OperationFailedException, ReadOnlyGroupException;

    public void removeGroupFromGroup(long var1, String var3, String var4) throws DirectoryPermissionException, GroupNotFoundException, DirectoryNotFoundException, InvalidMembershipException, MembershipNotFoundException, OperationFailedException, ReadOnlyGroupException;

    public <T> List<T> searchDirectGroupRelationships(long var1, MembershipQuery<T> var3) throws DirectoryNotFoundException, OperationFailedException;

    public <T> ListMultimap<String, T> searchDirectGroupRelationshipsGroupedByName(long var1, MembershipQuery<T> var3) throws OperationFailedException, DirectoryNotFoundException;

    public boolean isUserNestedGroupMember(long var1, String var3, String var4) throws DirectoryNotFoundException, OperationFailedException;

    public boolean isUserNestedGroupMember(long var1, String var3, Set<String> var4) throws DirectoryNotFoundException, OperationFailedException;

    public Set<String> filterNestedUserMembersOfGroups(long var1, Set<String> var3, Set<String> var4) throws OperationFailedException, DirectoryNotFoundException;

    public boolean isGroupNestedGroupMember(long var1, String var3, String var4) throws DirectoryNotFoundException, OperationFailedException;

    public BoundedCount countDirectMembersOfGroup(long var1, String var3, int var4) throws DirectoryNotFoundException, OperationFailedException;

    public <T> List<T> searchNestedGroupRelationships(long var1, MembershipQuery<T> var3) throws DirectoryNotFoundException, OperationFailedException;

    public BulkAddResult<User> addAllUsers(long var1, Collection<UserTemplateWithCredentialAndAttributes> var3, boolean var4) throws DirectoryPermissionException, DirectoryNotFoundException, OperationFailedException;

    public BulkAddResult<Group> addAllGroups(long var1, Collection<GroupTemplate> var3, boolean var4) throws DirectoryPermissionException, DirectoryNotFoundException, OperationFailedException, InvalidGroupException;

    public BulkAddResult<String> addAllUsersToGroup(long var1, Collection<String> var3, String var4) throws DirectoryPermissionException, DirectoryNotFoundException, GroupNotFoundException, OperationFailedException;

    public boolean supportsNestedGroups(long var1) throws DirectoryInstantiationException, DirectoryNotFoundException;

    public boolean isSynchronisable(long var1) throws DirectoryInstantiationException, DirectoryNotFoundException;

    @Nullable
    public SynchronisationMode getSynchronisationMode(long var1) throws DirectoryInstantiationException, DirectoryNotFoundException;

    public void synchroniseCache(long var1, SynchronisationMode var3) throws OperationFailedException, DirectoryNotFoundException;

    public void synchroniseCache(long var1, SynchronisationMode var3, boolean var4) throws OperationFailedException, DirectoryNotFoundException;

    public boolean isSynchronising(long var1) throws DirectoryInstantiationException, DirectoryNotFoundException;

    public DirectorySynchronisationInformation getDirectorySynchronisationInformation(long var1) throws DirectoryInstantiationException, DirectoryNotFoundException;

    public boolean supportsExpireAllPasswords(long var1) throws DirectoryInstantiationException, DirectoryNotFoundException;

    public void expireAllPasswords(long var1) throws OperationFailedException, DirectoryNotFoundException;

    public AvatarReference getUserAvatarByName(long var1, String var3, int var4) throws UserNotFoundException, OperationFailedException, DirectoryNotFoundException;

    @ExperimentalApi
    public User updateUserFromRemoteDirectory(@Nonnull User var1) throws OperationFailedException, DirectoryNotFoundException, UserNotFoundException;

    @ExperimentalApi
    public User userAuthenticated(long var1, String var3) throws OperationFailedException, DirectoryNotFoundException, UserNotFoundException, InactiveAccountException;

    @ExperimentalApi
    public List<Application> findAuthorisedApplications(long var1, List<String> var3);
}

