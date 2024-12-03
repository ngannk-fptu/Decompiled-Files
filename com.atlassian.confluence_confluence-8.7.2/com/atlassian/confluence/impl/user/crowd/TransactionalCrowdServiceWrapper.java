/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Group
 *  com.atlassian.crowd.embedded.api.GroupWithAttributes
 *  com.atlassian.crowd.embedded.api.Query
 *  com.atlassian.crowd.embedded.api.UnfilteredCrowdService
 *  com.atlassian.crowd.embedded.api.User
 *  com.atlassian.crowd.embedded.api.UserCapabilities
 *  com.atlassian.crowd.embedded.api.UserWithAttributes
 *  com.atlassian.crowd.exception.FailedAuthenticationException
 *  com.atlassian.crowd.exception.InactiveAccountException
 *  com.atlassian.crowd.exception.InvalidCredentialException
 *  com.atlassian.crowd.exception.InvalidMembershipException
 *  com.atlassian.crowd.exception.InvalidUserException
 *  com.atlassian.crowd.exception.OperationNotPermittedException
 *  com.atlassian.crowd.exception.embedded.InvalidGroupException
 *  com.atlassian.crowd.exception.runtime.GroupNotFoundException
 *  com.atlassian.crowd.exception.runtime.OperationFailedException
 *  com.atlassian.crowd.exception.runtime.UserNotFoundException
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.impl.user.crowd;

import com.atlassian.crowd.embedded.api.Group;
import com.atlassian.crowd.embedded.api.GroupWithAttributes;
import com.atlassian.crowd.embedded.api.Query;
import com.atlassian.crowd.embedded.api.UnfilteredCrowdService;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.crowd.embedded.api.UserCapabilities;
import com.atlassian.crowd.embedded.api.UserWithAttributes;
import com.atlassian.crowd.exception.FailedAuthenticationException;
import com.atlassian.crowd.exception.InactiveAccountException;
import com.atlassian.crowd.exception.InvalidCredentialException;
import com.atlassian.crowd.exception.InvalidMembershipException;
import com.atlassian.crowd.exception.InvalidUserException;
import com.atlassian.crowd.exception.OperationNotPermittedException;
import com.atlassian.crowd.exception.embedded.InvalidGroupException;
import com.atlassian.crowd.exception.runtime.GroupNotFoundException;
import com.atlassian.crowd.exception.runtime.OperationFailedException;
import com.atlassian.crowd.exception.runtime.UserNotFoundException;
import java.util.Objects;
import java.util.Set;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public final class TransactionalCrowdServiceWrapper
implements UnfilteredCrowdService {
    private final UnfilteredCrowdService crowdService;

    public TransactionalCrowdServiceWrapper(UnfilteredCrowdService crowdService) {
        this.crowdService = Objects.requireNonNull(crowdService);
    }

    public User authenticate(String name, String credential) throws FailedAuthenticationException, OperationFailedException {
        return this.crowdService.authenticate(name, credential);
    }

    @Transactional(readOnly=true)
    public User getUser(String name) {
        return this.crowdService.getUser(name);
    }

    @Transactional(readOnly=true)
    public User getRemoteUser(String name) {
        return this.crowdService.getRemoteUser(name);
    }

    public User userAuthenticated(String name) throws UserNotFoundException, OperationFailedException, InactiveAccountException {
        return this.crowdService.userAuthenticated(name);
    }

    @Transactional(readOnly=true)
    public UserWithAttributes getUserWithAttributes(String name) {
        return this.crowdService.getUserWithAttributes(name);
    }

    @Transactional(readOnly=true)
    public Group getGroup(String name) {
        return this.crowdService.getGroup(name);
    }

    @Transactional(readOnly=true)
    public GroupWithAttributes getGroupWithAttributes(String name) {
        return this.crowdService.getGroupWithAttributes(name);
    }

    @Transactional(readOnly=true)
    public <T> Iterable<T> search(Query<T> query) {
        return this.crowdService.search(query);
    }

    @Transactional(readOnly=true)
    public boolean isUserMemberOfGroup(String userName, String groupName) {
        return this.crowdService.isUserMemberOfGroup(userName, groupName);
    }

    @Transactional(readOnly=true)
    public boolean isUserMemberOfGroup(User user, Group group) {
        return this.crowdService.isUserMemberOfGroup(user, group);
    }

    @Transactional(readOnly=true)
    public boolean isGroupMemberOfGroup(String childGroupName, String parentGroupName) {
        return this.crowdService.isGroupMemberOfGroup(childGroupName, parentGroupName);
    }

    @Transactional(readOnly=true)
    public boolean isGroupMemberOfGroup(Group childGroup, Group parentGroup) {
        return this.crowdService.isGroupMemberOfGroup(childGroup, parentGroup);
    }

    public User addUser(User user, String credential) throws InvalidUserException, InvalidCredentialException, OperationNotPermittedException, OperationFailedException {
        return this.crowdService.addUser(user, credential);
    }

    public UserWithAttributes addUser(UserWithAttributes user, String credential) throws InvalidUserException, InvalidCredentialException, OperationNotPermittedException, OperationFailedException {
        return this.crowdService.addUser(user, credential);
    }

    public User updateUser(User user) throws UserNotFoundException, InvalidUserException, OperationNotPermittedException, OperationFailedException {
        return this.crowdService.updateUser(user);
    }

    public User renameUser(User user, String newUsername) throws UserNotFoundException, InvalidUserException, OperationNotPermittedException, OperationFailedException {
        return this.crowdService.renameUser(user, newUsername);
    }

    public void updateUserCredential(User user, String credential) throws UserNotFoundException, InvalidCredentialException, OperationNotPermittedException, OperationFailedException {
        this.crowdService.updateUserCredential(user, credential);
    }

    public void setUserAttribute(User user, String attributeName, String attributeValue) throws UserNotFoundException, OperationNotPermittedException, OperationFailedException {
        this.crowdService.setUserAttribute(user, attributeName, attributeValue);
    }

    public void setUserAttribute(User user, String attributeName, Set<String> attributeValues) throws UserNotFoundException, OperationNotPermittedException, OperationFailedException {
        this.crowdService.setUserAttribute(user, attributeName, attributeValues);
    }

    public void removeUserAttribute(User user, String attributeName) throws UserNotFoundException, OperationNotPermittedException, OperationFailedException {
        this.crowdService.removeUserAttribute(user, attributeName);
    }

    public void removeAllUserAttributes(User user) throws UserNotFoundException, OperationNotPermittedException, OperationFailedException {
        this.crowdService.removeAllUserAttributes(user);
    }

    public boolean removeUser(User user) throws OperationNotPermittedException, OperationFailedException {
        return this.crowdService.removeUser(user);
    }

    public Group addGroup(Group group) throws InvalidGroupException, OperationNotPermittedException, OperationFailedException {
        return this.crowdService.addGroup(group);
    }

    public Group updateGroup(Group group) throws GroupNotFoundException, InvalidGroupException, OperationNotPermittedException, OperationFailedException {
        return this.crowdService.updateGroup(group);
    }

    public void setGroupAttribute(Group group, String attributeName, String attributeValue) throws GroupNotFoundException, OperationNotPermittedException, OperationFailedException {
        this.crowdService.setGroupAttribute(group, attributeName, attributeValue);
    }

    public void setGroupAttribute(Group group, String attributeName, Set<String> attributeValues) throws GroupNotFoundException, OperationNotPermittedException, OperationFailedException {
        this.crowdService.setGroupAttribute(group, attributeName, attributeValues);
    }

    public void removeGroupAttribute(Group group, String attributeName) throws GroupNotFoundException, OperationNotPermittedException, OperationFailedException {
        this.crowdService.removeGroupAttribute(group, attributeName);
    }

    public void removeAllGroupAttributes(Group group) throws GroupNotFoundException, OperationNotPermittedException, OperationFailedException {
        this.crowdService.removeAllGroupAttributes(group);
    }

    public boolean removeGroup(Group group) throws OperationNotPermittedException, OperationFailedException {
        return this.crowdService.removeGroup(group);
    }

    public boolean addUserToGroup(User user, Group group) throws GroupNotFoundException, UserNotFoundException, OperationNotPermittedException, OperationFailedException {
        return this.crowdService.addUserToGroup(user, group);
    }

    public boolean addGroupToGroup(Group childGroup, Group parentGroup) throws GroupNotFoundException, OperationNotPermittedException, InvalidMembershipException, OperationFailedException {
        return this.crowdService.addGroupToGroup(childGroup, parentGroup);
    }

    public boolean removeUserFromGroup(User user, Group group) throws GroupNotFoundException, UserNotFoundException, OperationNotPermittedException, OperationFailedException {
        return this.crowdService.removeUserFromGroup(user, group);
    }

    public boolean removeGroupFromGroup(Group childGroup, Group parentGroup) throws GroupNotFoundException, OperationNotPermittedException, OperationFailedException {
        return this.crowdService.removeGroupFromGroup(childGroup, parentGroup);
    }

    @Transactional(readOnly=true)
    public boolean isUserDirectGroupMember(User user, Group group) throws OperationFailedException {
        return this.crowdService.isUserDirectGroupMember(user, group);
    }

    @Transactional(readOnly=true)
    public boolean isGroupDirectGroupMember(Group childGroup, Group parentGroup) throws OperationFailedException {
        return this.crowdService.isGroupDirectGroupMember(childGroup, parentGroup);
    }

    @Transactional(readOnly=true)
    public UserCapabilities getCapabilitiesForNewUsers() {
        return this.crowdService.getCapabilitiesForNewUsers();
    }
}

