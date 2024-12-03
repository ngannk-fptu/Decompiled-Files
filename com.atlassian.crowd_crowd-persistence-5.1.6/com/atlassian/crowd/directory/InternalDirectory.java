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
 *  com.atlassian.crowd.exception.InvalidUserException
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.exception.OperationNotSupportedException
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
 *  com.atlassian.crowd.util.UserUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.Validate
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
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
import com.atlassian.crowd.exception.InvalidUserException;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.exception.OperationNotSupportedException;
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
import com.atlassian.crowd.util.UserUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InternalDirectory
extends AbstractInternalDirectory {
    private static final Logger logger = LoggerFactory.getLogger(InternalDirectory.class);

    public InternalDirectory(InternalDirectoryUtils internalDirectoryUtils, PasswordEncoderFactory passwordEncoderFactory, DirectoryDao directoryDao, UserDao userDao, GroupDao groupDao, MembershipDao membershipDao, TombstoneDao tombstoneDao, PasswordConstraintsLoader passwordConstraints) {
        super(internalDirectoryUtils, passwordEncoderFactory, directoryDao, userDao, groupDao, membershipDao, tombstoneDao, passwordConstraints);
    }

    public com.atlassian.crowd.model.user.User addUser(UserTemplate user, PasswordCredential credential) throws InvalidCredentialException, InvalidUserException, UserAlreadyExistsException, OperationFailedException {
        return this.addUser(UserTemplateWithAttributes.toUserWithNoAttributes((com.atlassian.crowd.model.user.User)user), credential);
    }

    @Override
    public UserWithAttributes addUser(UserTemplateWithAttributes user, PasswordCredential credential) throws InvalidCredentialException, InvalidUserException, UserAlreadyExistsException, OperationFailedException {
        com.atlassian.crowd.model.user.User addedUser;
        PasswordCredential encryptedCredential;
        this.internalDirectoryUtils.validateDirectoryForEntity((DirectoryEntity)user, this.directoryId);
        this.internalDirectoryUtils.validateUsername(user.getName());
        UserTemplateWithAttributes userWithExternalId = new UserTemplateWithAttributes((UserWithAttributes)user);
        if (this.isUserExternalIdReadOnly()) {
            if (StringUtils.isBlank((CharSequence)user.getExternalId())) {
                userWithExternalId.setExternalId(InternalDirectory.generateUniqueIdentifier());
            } else {
                throw new InvalidUserException((User)user, "User externalId cannot be externally managed");
            }
        }
        com.atlassian.crowd.model.user.User prepopulatedUser = UserUtils.populateNames((com.atlassian.crowd.model.user.User)userWithExternalId);
        if (credential != null) {
            this.internalDirectoryUtils.validateCredential((User)prepopulatedUser, credential, this.getPasswordConstraints(), this.getValue("password_complexity_message"));
            encryptedCredential = this.encryptedCredential(credential);
        } else {
            encryptedCredential = null;
        }
        try {
            addedUser = this.userDao.add(prepopulatedUser, encryptedCredential);
        }
        catch (IllegalArgumentException e) {
            throw new InvalidUserException((User)user, e.getMessage(), (Throwable)e);
        }
        catch (DirectoryNotFoundException e) {
            throw new OperationFailedException((Throwable)e);
        }
        try {
            HashMap<String, Set<String>> combinedUserAttributes = new HashMap<String, Set<String>>(user.getAttributes());
            combinedUserAttributes.putAll(InternalDirectory.calculatePostPasswordUpdateAttributes());
            this.userDao.storeAttributes(addedUser, combinedUserAttributes, false);
            return this.findUserWithAttributesByName(addedUser.getName());
        }
        catch (UserNotFoundException e) {
            throw new OperationFailedException((Throwable)e);
        }
    }

    private static String generateUniqueIdentifier() {
        return UUID.randomUUID().toString();
    }

    @Override
    public Group addLocalGroup(GroupTemplate group) throws OperationFailedException {
        throw new OperationNotSupportedException("addLocalGroup() is not supported for InternalDirectory");
    }

    public BatchResult<com.atlassian.crowd.model.user.User> addAllUsers(Set<UserTemplateWithCredentialAndAttributes> users) {
        Validate.notNull(users, (String)"users cannot be null", (Object[])new Object[0]);
        HashSet<UserTemplateWithCredentialAndAttributes> preparedUsers = new HashSet<UserTemplateWithCredentialAndAttributes>();
        ArrayList<UserTemplateWithCredentialAndAttributes> failedUsers = new ArrayList<UserTemplateWithCredentialAndAttributes>();
        for (UserTemplateWithCredentialAndAttributes uncleansedUser : users) {
            UserTemplate userWithExternalId = new UserTemplate((com.atlassian.crowd.model.user.User)uncleansedUser);
            if (this.isUserExternalIdReadOnly()) {
                if (StringUtils.isBlank((CharSequence)uncleansedUser.getExternalId())) {
                    userWithExternalId.setExternalId(InternalDirectory.generateUniqueIdentifier());
                } else {
                    failedUsers.add(uncleansedUser);
                    continue;
                }
            }
            com.atlassian.crowd.model.user.User prepopulatedUser = UserUtils.populateNames((com.atlassian.crowd.model.user.User)userWithExternalId);
            try {
                this.internalDirectoryUtils.validateDirectoryForEntity((DirectoryEntity)prepopulatedUser, this.getDirectoryId());
                this.internalDirectoryUtils.validateUsername(prepopulatedUser.getName());
                this.internalDirectoryUtils.validateCredential((User)prepopulatedUser, uncleansedUser.getCredential(), this.getPasswordConstraints(), this.getValue("password_complexity_message"));
                Map<String, Set<String>> attributesForAdd = InternalDirectory.calculatePostPasswordUpdateAttributes();
                PasswordCredential encryptedCredential = this.encryptedCredential(uncleansedUser.getCredential());
                preparedUsers.add(new UserTemplateWithCredentialAndAttributes(prepopulatedUser, attributesForAdd, encryptedCredential));
            }
            catch (IllegalArgumentException e) {
                failedUsers.add(uncleansedUser);
                logger.error("Cannot add invalid user " + uncleansedUser.getName(), (Throwable)e);
            }
            catch (InvalidCredentialException e) {
                failedUsers.add(uncleansedUser);
                logger.error("Cannot add user with invalid password " + uncleansedUser.getName(), (Throwable)e);
            }
        }
        BatchResult result = this.userDao.addAll(preparedUsers);
        result.addFailures(failedUsers);
        return result;
    }

    public BatchResult<Group> addAllGroups(Set<GroupTemplate> groups) {
        Validate.notNull(groups, (String)"groups cannot be null", (Object[])new Object[0]);
        HashSet<GroupTemplate> preparedGroups = new HashSet<GroupTemplate>();
        ArrayList<GroupTemplate> failedGroups = new ArrayList<GroupTemplate>();
        for (GroupTemplate group : groups) {
            try {
                this.internalDirectoryUtils.validateDirectoryForEntity((DirectoryEntity)group, this.getDirectoryId());
                this.internalDirectoryUtils.validateGroupName((Group)group, group.getName());
                preparedGroups.add(group);
            }
            catch (IllegalArgumentException e) {
                failedGroups.add(group);
                logger.error("Cannot add invalid group " + group.getName(), (Throwable)e);
            }
        }
        try {
            BatchResult result = this.groupDao.addAll(preparedGroups);
            result.addFailures(failedGroups);
            return result;
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

    public com.atlassian.crowd.model.user.User updateUser(UserTemplate user) throws InvalidUserException, UserNotFoundException {
        this.internalDirectoryUtils.validateDirectoryForEntity((DirectoryEntity)user, this.directoryId);
        UserTemplate externalIdPreservingTemplate = new UserTemplate((com.atlassian.crowd.model.user.User)user);
        if (this.isUserExternalIdReadOnly()) {
            String previousExternalId = this.userDao.findByName(user.getDirectoryId(), user.getName()).getExternalId();
            if (user.getExternalId() != null && !user.getExternalId().equals(previousExternalId)) {
                throw new InvalidUserException((User)user, "User externalId cannot be changed");
            }
            externalIdPreservingTemplate.setExternalId(previousExternalId);
        }
        com.atlassian.crowd.model.user.User prepopulatedUser = UserUtils.populateNames((com.atlassian.crowd.model.user.User)externalIdPreservingTemplate);
        try {
            return this.userDao.update(prepopulatedUser);
        }
        catch (IllegalArgumentException e) {
            throw new InvalidUserException((User)user, e.getMessage(), (Throwable)e);
        }
    }

    public boolean isLocalUserStatusEnabled() {
        return false;
    }

    protected boolean isUserExternalIdReadOnly() {
        return true;
    }
}

