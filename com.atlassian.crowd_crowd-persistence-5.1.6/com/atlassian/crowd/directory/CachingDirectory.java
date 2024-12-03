/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.PasswordCredential
 *  com.atlassian.crowd.embedded.api.User
 *  com.atlassian.crowd.embedded.spi.DirectoryDao
 *  com.atlassian.crowd.embedded.spi.GroupDao
 *  com.atlassian.crowd.embedded.spi.MembershipDao
 *  com.atlassian.crowd.embedded.spi.UserDao
 *  com.atlassian.crowd.exception.DirectoryNotFoundException
 *  com.atlassian.crowd.exception.GroupNotFoundException
 *  com.atlassian.crowd.exception.InvalidCredentialException
 *  com.atlassian.crowd.exception.InvalidGroupException
 *  com.atlassian.crowd.exception.InvalidUserException
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.exception.UserAlreadyExistsException
 *  com.atlassian.crowd.exception.UserNotFoundException
 *  com.atlassian.crowd.model.DirectoryEntity
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.group.GroupTemplate
 *  com.atlassian.crowd.model.user.User
 *  com.atlassian.crowd.model.user.UserTemplate
 *  com.atlassian.crowd.model.user.UserTemplateWithAttributes
 *  com.atlassian.crowd.model.user.UserTemplateWithCredentialAndAttributes
 *  com.atlassian.crowd.model.user.UserWithAttributes
 *  com.atlassian.crowd.password.factory.PasswordEncoderFactory
 *  com.atlassian.crowd.util.BatchResult
 *  org.apache.commons.lang3.Validate
 */
package com.atlassian.crowd.directory;

import com.atlassian.crowd.dao.tombstone.TombstoneDao;
import com.atlassian.crowd.directory.AbstractInternalDirectory;
import com.atlassian.crowd.directory.InternalDirectoryUtils;
import com.atlassian.crowd.directory.PasswordConstraintsLoader;
import com.atlassian.crowd.embedded.api.PasswordCredential;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.crowd.embedded.spi.DirectoryDao;
import com.atlassian.crowd.embedded.spi.GroupDao;
import com.atlassian.crowd.embedded.spi.MembershipDao;
import com.atlassian.crowd.embedded.spi.UserDao;
import com.atlassian.crowd.exception.DirectoryNotFoundException;
import com.atlassian.crowd.exception.GroupNotFoundException;
import com.atlassian.crowd.exception.InvalidCredentialException;
import com.atlassian.crowd.exception.InvalidGroupException;
import com.atlassian.crowd.exception.InvalidUserException;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.exception.UserAlreadyExistsException;
import com.atlassian.crowd.exception.UserNotFoundException;
import com.atlassian.crowd.model.DirectoryEntity;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.GroupTemplate;
import com.atlassian.crowd.model.user.UserTemplate;
import com.atlassian.crowd.model.user.UserTemplateWithAttributes;
import com.atlassian.crowd.model.user.UserTemplateWithCredentialAndAttributes;
import com.atlassian.crowd.model.user.UserWithAttributes;
import com.atlassian.crowd.password.factory.PasswordEncoderFactory;
import com.atlassian.crowd.util.BatchResult;
import java.util.Collection;
import java.util.Set;
import org.apache.commons.lang3.Validate;

public class CachingDirectory
extends AbstractInternalDirectory {
    public CachingDirectory(InternalDirectoryUtils internalDirectoryUtils, PasswordEncoderFactory passwordEncoderFactory, DirectoryDao directoryDao, UserDao userDao, GroupDao groupDao, MembershipDao membershipDao, TombstoneDao tombstoneDao, PasswordConstraintsLoader passwordConstraints) {
        super(internalDirectoryUtils, passwordEncoderFactory, directoryDao, userDao, groupDao, membershipDao, tombstoneDao, passwordConstraints);
    }

    public com.atlassian.crowd.model.user.User addUser(UserTemplate user, PasswordCredential credential) throws InvalidCredentialException, InvalidUserException, UserAlreadyExistsException, OperationFailedException {
        return this.addUser(UserTemplateWithAttributes.toUserWithNoAttributes((com.atlassian.crowd.model.user.User)user), credential);
    }

    @Override
    public UserWithAttributes addUser(UserTemplateWithAttributes user, PasswordCredential credential) throws InvalidCredentialException, InvalidUserException, UserAlreadyExistsException, OperationFailedException {
        this.internalDirectoryUtils.validateDirectoryForEntity((DirectoryEntity)user, this.directoryId);
        try {
            this.userDao.add((com.atlassian.crowd.model.user.User)user, credential);
        }
        catch (IllegalArgumentException e) {
            throw new InvalidUserException((User)user, e.getMessage(), (Throwable)e);
        }
        catch (DirectoryNotFoundException e) {
            throw new OperationFailedException((Throwable)e);
        }
        try {
            this.storeUserAttributes(user.getName(), user.getAttributes());
            return this.findUserWithAttributesByName(user.getName());
        }
        catch (UserNotFoundException e) {
            throw new OperationFailedException((Throwable)e);
        }
    }

    @Override
    public Group addLocalGroup(GroupTemplate group) throws InvalidGroupException, OperationFailedException {
        this.internalDirectoryUtils.validateDirectoryForEntity((DirectoryEntity)group, this.directoryId);
        this.internalDirectoryUtils.validateGroupName((Group)group, group.getName());
        try {
            return this.groupDao.addLocal((Group)group);
        }
        catch (IllegalArgumentException e) {
            throw new InvalidGroupException((Group)group, e.getMessage(), (Throwable)e);
        }
        catch (DirectoryNotFoundException e) {
            throw new OperationFailedException(e.getMessage(), (Throwable)e);
        }
    }

    public BatchResult<com.atlassian.crowd.model.user.User> addAllUsers(Set<UserTemplateWithCredentialAndAttributes> users) {
        for (UserTemplateWithCredentialAndAttributes user : users) {
            this.internalDirectoryUtils.validateDirectoryForEntity((DirectoryEntity)user, this.directoryId);
        }
        return this.userDao.addAll(users);
    }

    public BatchResult<Group> addAllGroups(Set<GroupTemplate> groups) {
        for (GroupTemplate group : groups) {
            this.internalDirectoryUtils.validateDirectoryForEntity((DirectoryEntity)group, this.directoryId);
            this.internalDirectoryUtils.validateGroupName((Group)group, group.getName());
        }
        try {
            return this.groupDao.addAll(groups);
        }
        catch (DirectoryNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public BatchResult<String> addAllUsersToGroup(Set<String> userNames, String groupName) throws GroupNotFoundException {
        Validate.notNull(userNames, (String)"userNames cannot be null", (Object[])new Object[0]);
        Validate.notEmpty((CharSequence)groupName, (String)"groupName cannot be null or empty", (Object[])new Object[0]);
        return this.membershipDao.addAllUsersToGroup(this.getDirectoryId(), userNames, groupName);
    }

    @Override
    public BatchResult<String> removeUsersFromGroup(Set<String> usernames, String groupName) throws GroupNotFoundException {
        Validate.notNull(usernames, (String)"usernames cannot be null", (Object[])new Object[0]);
        Validate.notEmpty((CharSequence)groupName, (String)"groupName cannot be null or empty", (Object[])new Object[0]);
        return this.membershipDao.removeUsersFromGroup(this.getDirectoryId(), usernames, groupName);
    }

    @Override
    public BatchResult<String> removeGroupsFromGroup(Collection<String> childGroupNames, String groupName) throws GroupNotFoundException {
        Validate.notNull(childGroupNames, (String)"childGroupNames cannot be null", (Object[])new Object[0]);
        Validate.notEmpty((CharSequence)groupName, (String)"groupName cannot be null or empty", (Object[])new Object[0]);
        return this.membershipDao.removeGroupsFromGroup(this.getDirectoryId(), childGroupNames, groupName);
    }

    public com.atlassian.crowd.model.user.User updateUser(UserTemplate user) throws InvalidUserException, UserNotFoundException {
        this.internalDirectoryUtils.validateDirectoryForEntity((DirectoryEntity)user, this.directoryId);
        try {
            return this.userDao.update((com.atlassian.crowd.model.user.User)user);
        }
        catch (IllegalArgumentException e) {
            throw new InvalidUserException((User)user, e.getMessage(), (Throwable)e);
        }
    }

    public boolean isLocalUserStatusEnabled() {
        return Boolean.valueOf(this.getValue("localUserStatusEnabled"));
    }
}

