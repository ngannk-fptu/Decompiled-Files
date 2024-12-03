/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.people.Person
 *  com.atlassian.confluence.api.model.people.User
 *  com.atlassian.core.exception.InfrastructureException
 *  com.atlassian.core.user.preferences.UserPreferences
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.EntityException
 *  com.atlassian.user.Group
 *  com.atlassian.user.User
 *  com.atlassian.user.search.SearchResult
 *  com.atlassian.user.search.page.Pager
 *  com.atlassian.user.search.query.Query
 *  com.atlassian.user.search.query.QueryContext
 *  com.atlassian.user.security.password.Credential
 *  com.opensymphony.module.propertyset.PropertySet
 *  javax.annotation.Nonnull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.user;

import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.people.Person;
import com.atlassian.confluence.api.model.people.User;
import com.atlassian.confluence.internal.user.UserAccessorInternal;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.ConfluenceUserPreferences;
import com.atlassian.confluence.user.actions.ProfilePictureInfo;
import com.atlassian.core.exception.InfrastructureException;
import com.atlassian.core.user.preferences.UserPreferences;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.EntityException;
import com.atlassian.user.Group;
import com.atlassian.user.search.SearchResult;
import com.atlassian.user.search.page.Pager;
import com.atlassian.user.search.query.Query;
import com.atlassian.user.search.query.QueryContext;
import com.atlassian.user.security.password.Credential;
import com.opensymphony.module.propertyset.PropertySet;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ReadOnlyUserAccessor
implements UserAccessorInternal {
    private final UserAccessorInternal delegate;

    public ReadOnlyUserAccessor(UserAccessorInternal delegate) {
        this.delegate = delegate;
    }

    public com.atlassian.user.User addUser(String username, String password, String email, String fullname, String[] groups) {
        throw new UnsupportedOperationException();
    }

    public com.atlassian.user.User addUser(String username, String password, String email, String fullname) {
        throw new UnsupportedOperationException();
    }

    public Pager<String> getUserNames() {
        return this.delegate.getUserNames();
    }

    public Pager<com.atlassian.user.User> getUsers() {
        return this.delegate.getUsers();
    }

    @Override
    public Group getGroup(String name) {
        return this.delegate.getGroup(name);
    }

    public Pager<Group> getGroups() {
        return this.delegate.getGroups();
    }

    public Group addGroup(String name) {
        throw new UnsupportedOperationException();
    }

    public void removeGroup(Group group) {
        throw new UnsupportedOperationException();
    }

    public void removeUser(com.atlassian.user.User user) throws InfrastructureException {
        throw new UnsupportedOperationException();
    }

    public Group getGroupCreateIfNecessary(String name) {
        throw new UnsupportedOperationException();
    }

    public UserPreferences getUserPreferences(com.atlassian.user.User user) {
        return this.delegate.getUserPreferences(user);
    }

    public void saveUser(com.atlassian.user.User user) {
        throw new UnsupportedOperationException();
    }

    public SearchResult getUsersByEmail(String email) {
        return this.delegate.getUsersByEmail(email);
    }

    public void deactivateUser(com.atlassian.user.User user) {
        throw new UnsupportedOperationException();
    }

    public void reactivateUser(com.atlassian.user.User user) {
        throw new UnsupportedOperationException();
    }

    public boolean isUserRemovable(com.atlassian.user.User user) throws EntityException {
        return this.delegate.isUserRemovable(user);
    }

    public Pager<Group> getGroups(com.atlassian.user.User user) {
        return this.delegate.getGroups(user);
    }

    public boolean hasMembership(Group group, com.atlassian.user.User user) {
        return this.delegate.hasMembership(group, user);
    }

    public boolean hasMembership(String groupName, String username) {
        return this.delegate.hasMembership(groupName, username);
    }

    public void addMembership(Group group, com.atlassian.user.User user) {
        throw new UnsupportedOperationException();
    }

    public void addMembership(String groupname, String username) {
        throw new UnsupportedOperationException();
    }

    public boolean removeMembership(Group group, com.atlassian.user.User user) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Pager<String> getMemberNames(Group group) {
        return this.delegate.getMemberNames(group);
    }

    public Group createGroup(String groupname) {
        throw new UnsupportedOperationException();
    }

    public boolean authenticate(String username, String password) {
        return this.delegate.authenticate(username, password);
    }

    public SearchResult<com.atlassian.user.User> findUsers(Query<com.atlassian.user.User> search) throws EntityException {
        return this.delegate.findUsers(search);
    }

    public SearchResult<Group> findGroups(Query<Group> query) throws EntityException {
        return this.delegate.findGroups(query);
    }

    public SearchResult<com.atlassian.user.User> findUsers(Query<com.atlassian.user.User> query, QueryContext context) throws EntityException {
        return this.delegate.findUsers(query, context);
    }

    public SearchResult<Group> findGroups(Query<Group> query, QueryContext context) throws EntityException {
        return this.delegate.findGroups(query, context);
    }

    public void alterPassword(com.atlassian.user.User user, String plainTextPassword) throws EntityException {
        throw new UnsupportedOperationException();
    }

    public boolean removeMembership(String groupname, String username) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Nonnull
    public PageResponse<ConfluenceUser> getUsers(LimitedRequest limitedRequest) {
        return this.delegate.getUsers(limitedRequest);
    }

    @Override
    public @Nullable ConfluenceUser getExistingUserByPerson(Person person) {
        return this.delegate.getExistingUserByPerson(person);
    }

    @Override
    public Optional<ConfluenceUser> getExistingByApiUser(User user) {
        return this.delegate.getExistingByApiUser(user);
    }

    @Override
    public boolean isDeletedUser(ConfluenceUser user) {
        return this.delegate.isDeletedUser(user);
    }

    @Override
    public boolean isUnsyncedUser(ConfluenceUser user) {
        return this.delegate.isUnsyncedUser(user);
    }

    @Override
    public boolean isCrowdManaged(ConfluenceUser user) {
        return this.delegate.isCrowdManaged(user);
    }

    @Override
    public Pager<ConfluenceUser> searchUnsyncedUsers(String searchParam) {
        return this.delegate.searchUnsyncedUsers(searchParam);
    }

    @Override
    public com.atlassian.user.User getUser(String name) {
        return this.delegate.getUser(name);
    }

    @Override
    public @Nullable ConfluenceUser getUserByName(String name) {
        return this.delegate.getUserByName(name);
    }

    @Override
    public @Nullable ConfluenceUser getUserByKey(UserKey key) {
        return this.delegate.getUserByKey(key);
    }

    @Override
    public @Nullable ConfluenceUser getExistingUserByKey(UserKey key) {
        return this.delegate.getExistingUserByKey(key);
    }

    @Override
    public boolean exists(String name) {
        return this.delegate.exists(name);
    }

    @Override
    public ConfluenceUser createUser(com.atlassian.user.User userTemplate, Credential password) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isReadOnly(com.atlassian.user.User user) {
        return this.delegate.isReadOnly(user);
    }

    @Override
    public boolean isReadOnly(Group group) {
        return this.delegate.isReadOnly(group);
    }

    @Override
    public ProfilePictureInfo getUserProfilePicture(@Nullable com.atlassian.user.User user) {
        return this.delegate.getUserProfilePicture(user);
    }

    @Override
    public ConfluenceUserPreferences getConfluenceUserPreferences(@Nullable com.atlassian.user.User user) {
        return this.delegate.getConfluenceUserPreferences(user);
    }

    @Override
    public boolean isSuperUser(com.atlassian.user.User user) {
        return this.delegate.isSuperUser(user);
    }

    @Override
    public List<Group> getGroupsAsList() {
        return this.delegate.getGroupsAsList();
    }

    @Override
    public List<String> getMemberNamesAsList(Group group) {
        return this.delegate.getMemberNamesAsList(group);
    }

    @Override
    public Iterable<ConfluenceUser> getMembers(Group group) {
        return this.delegate.getMembers(group);
    }

    @Override
    public List<String> getGroupNames(com.atlassian.user.User user) {
        return this.delegate.getGroupNames(user);
    }

    @Override
    public List<String> getGroupNamesForUserName(String userName) {
        return this.delegate.getGroupNamesForUserName(userName);
    }

    @Override
    public List<Group> getWriteableGroups() {
        return this.delegate.getWriteableGroups();
    }

    @Override
    public PropertySet getPropertySet(com.atlassian.user.User user) {
        return this.delegate.getPropertySet(user);
    }

    @Override
    public PropertySet getPropertySet(ConfluenceUser user) {
        return this.delegate.getPropertySet(user);
    }

    @Override
    public List<String> getUserNamesWithConfluenceAccess() {
        return this.delegate.getUserNamesWithConfluenceAccess();
    }

    @Override
    public int countLicenseConsumingUsers() {
        return this.delegate.countLicenseConsumingUsers();
    }

    @Override
    public List<com.atlassian.user.User> findUsersAsList(Query<com.atlassian.user.User> search) throws EntityException {
        return this.delegate.findUsersAsList(search);
    }

    @Override
    public void setUserProfilePicture(com.atlassian.user.User user, Attachment attachment) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setUserProfilePicture(com.atlassian.user.User targetUser, String imagePath) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<String> getAllDefaultGroupNames() {
        return this.delegate.getAllDefaultGroupNames();
    }

    @Override
    public String getNewUserDefaultGroupName() {
        return this.delegate.getNewUserDefaultGroupName();
    }

    @Override
    public ConfluenceUser renameUser(ConfluenceUser user, String newUsername) throws EntityException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<ConfluenceUser> getUsersByUserKeys(List<UserKey> userKeys) {
        return this.delegate.getUsersByUserKeys(userKeys);
    }

    @Override
    public List<Group> getGroupsByGroupNames(List<String> groupNames) {
        return this.delegate.getGroupsByGroupNames(groupNames);
    }

    @Override
    public int countUnsyncedUsers() {
        return this.delegate.countUnsyncedUsers();
    }

    @Override
    public boolean isLicensedToAddMoreUsers() {
        return this.delegate.isLicensedToAddMoreUsers();
    }

    @Override
    public boolean isDeactivated(com.atlassian.user.User user) {
        return this.delegate.isDeactivated(user);
    }

    @Override
    public boolean isDeactivated(String username) {
        return this.delegate.isDeactivated(username);
    }

    @Override
    public void alterPassword(com.atlassian.user.User user, String plainTextPassword, String token) throws EntityException {
        throw new UnsupportedOperationException();
    }
}

