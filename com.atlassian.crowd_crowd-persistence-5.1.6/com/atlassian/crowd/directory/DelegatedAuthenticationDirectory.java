/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.InternalRemoteDirectory
 *  com.atlassian.crowd.directory.MultiValuesQueriesSupport
 *  com.atlassian.crowd.directory.RemoteDirectory
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.PasswordCredential
 *  com.atlassian.crowd.embedded.impl.IdentifierMap
 *  com.atlassian.crowd.embedded.impl.IdentifierSet
 *  com.atlassian.crowd.embedded.impl.IdentifierUtils
 *  com.atlassian.crowd.embedded.spi.DirectoryDao
 *  com.atlassian.crowd.event.group.AutoGroupCreatedEvent
 *  com.atlassian.crowd.event.group.AutoGroupMembershipCreatedEvent
 *  com.atlassian.crowd.event.group.AutoGroupMembershipDeletedEvent
 *  com.atlassian.crowd.event.user.AutoUserCreatedEvent
 *  com.atlassian.crowd.event.user.AutoUserUpdatedEvent
 *  com.atlassian.crowd.event.user.UserRenamedEvent
 *  com.atlassian.crowd.exception.DirectoryNotFoundException
 *  com.atlassian.crowd.exception.ExpiredCredentialException
 *  com.atlassian.crowd.exception.GroupNotFoundException
 *  com.atlassian.crowd.exception.InactiveAccountException
 *  com.atlassian.crowd.exception.InvalidAuthenticationException
 *  com.atlassian.crowd.exception.InvalidCredentialException
 *  com.atlassian.crowd.exception.InvalidGroupException
 *  com.atlassian.crowd.exception.InvalidUserException
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.exception.OperationNotSupportedException
 *  com.atlassian.crowd.exception.UserAlreadyExistsException
 *  com.atlassian.crowd.exception.UserNotFoundException
 *  com.atlassian.crowd.model.DirectoryEntities
 *  com.atlassian.crowd.model.DirectoryEntity
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.group.GroupTemplate
 *  com.atlassian.crowd.model.group.InternalDirectoryGroup
 *  com.atlassian.crowd.model.membership.MembershipType
 *  com.atlassian.crowd.model.user.ImmutableUser
 *  com.atlassian.crowd.model.user.TimestampedUser
 *  com.atlassian.crowd.model.user.User
 *  com.atlassian.crowd.model.user.UserTemplate
 *  com.atlassian.crowd.model.user.UserTemplateWithAttributes
 *  com.atlassian.crowd.model.user.UserWithAttributes
 *  com.atlassian.crowd.search.EntityDescriptor
 *  com.atlassian.crowd.search.builder.QueryBuilder
 *  com.atlassian.crowd.search.builder.Restriction
 *  com.atlassian.crowd.search.query.entity.restriction.Property
 *  com.atlassian.crowd.search.query.entity.restriction.constants.GroupTermKeys
 *  com.atlassian.crowd.search.query.membership.MembershipQuery
 *  com.atlassian.crowd.util.BatchResult
 *  com.atlassian.event.api.EventPublisher
 *  com.google.common.base.Function
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ListMultimap
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.directory;

import com.atlassian.crowd.directory.AbstractForwardingDirectory;
import com.atlassian.crowd.directory.InternalRemoteDirectory;
import com.atlassian.crowd.directory.MultiValuesQueriesSupport;
import com.atlassian.crowd.directory.RemoteDirectory;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.PasswordCredential;
import com.atlassian.crowd.embedded.impl.IdentifierMap;
import com.atlassian.crowd.embedded.impl.IdentifierSet;
import com.atlassian.crowd.embedded.impl.IdentifierUtils;
import com.atlassian.crowd.embedded.spi.DirectoryDao;
import com.atlassian.crowd.event.group.AutoGroupCreatedEvent;
import com.atlassian.crowd.event.group.AutoGroupMembershipCreatedEvent;
import com.atlassian.crowd.event.group.AutoGroupMembershipDeletedEvent;
import com.atlassian.crowd.event.user.AutoUserCreatedEvent;
import com.atlassian.crowd.event.user.AutoUserUpdatedEvent;
import com.atlassian.crowd.event.user.UserRenamedEvent;
import com.atlassian.crowd.exception.DirectoryNotFoundException;
import com.atlassian.crowd.exception.ExpiredCredentialException;
import com.atlassian.crowd.exception.GroupNotFoundException;
import com.atlassian.crowd.exception.InactiveAccountException;
import com.atlassian.crowd.exception.InvalidAuthenticationException;
import com.atlassian.crowd.exception.InvalidCredentialException;
import com.atlassian.crowd.exception.InvalidGroupException;
import com.atlassian.crowd.exception.InvalidUserException;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.exception.OperationNotSupportedException;
import com.atlassian.crowd.exception.UserAlreadyExistsException;
import com.atlassian.crowd.exception.UserNotFoundException;
import com.atlassian.crowd.model.DirectoryEntities;
import com.atlassian.crowd.model.DirectoryEntity;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.GroupTemplate;
import com.atlassian.crowd.model.group.InternalDirectoryGroup;
import com.atlassian.crowd.model.membership.MembershipType;
import com.atlassian.crowd.model.user.ImmutableUser;
import com.atlassian.crowd.model.user.TimestampedUser;
import com.atlassian.crowd.model.user.User;
import com.atlassian.crowd.model.user.UserTemplate;
import com.atlassian.crowd.model.user.UserTemplateWithAttributes;
import com.atlassian.crowd.model.user.UserWithAttributes;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.builder.QueryBuilder;
import com.atlassian.crowd.search.builder.Restriction;
import com.atlassian.crowd.search.query.entity.restriction.Property;
import com.atlassian.crowd.search.query.entity.restriction.constants.GroupTermKeys;
import com.atlassian.crowd.search.query.membership.MembershipQuery;
import com.atlassian.crowd.util.BatchResult;
import com.atlassian.event.api.EventPublisher;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DelegatedAuthenticationDirectory
extends AbstractForwardingDirectory
implements RemoteDirectory,
MultiValuesQueriesSupport {
    private static final Logger logger = LoggerFactory.getLogger(DelegatedAuthenticationDirectory.class);
    public static final String ATTRIBUTE_CREATE_USER_ON_AUTH = "crowd.delegated.directory.auto.create.user";
    public static final String ATTRIBUTE_UPDATE_USER_ON_AUTH = "crowd.delegated.directory.auto.update.user";
    public static final String ATTRIBUTE_LDAP_DIRECTORY_CLASS = "crowd.delegated.directory.type";
    public static final String ATTRIBUTE_KEY_IMPORT_GROUPS = "crowd.delegated.directory.importGroups";
    private final RemoteDirectory ldapDirectory;
    private final InternalRemoteDirectory internalDirectory;
    private final EventPublisher eventPublisher;
    private final DirectoryDao directoryDao;

    public DelegatedAuthenticationDirectory(RemoteDirectory ldapDirectory, InternalRemoteDirectory internalDirectory, EventPublisher eventPublisher, DirectoryDao directoryDao) {
        this.ldapDirectory = ldapDirectory;
        this.internalDirectory = internalDirectory;
        this.eventPublisher = eventPublisher;
        this.directoryDao = directoryDao;
    }

    @Override
    public void setDirectoryId(long directoryId) {
        throw new UnsupportedOperationException("You cannot mutate the directoryID of " + this.getClass().getName());
    }

    @Override
    public String getDescriptiveName() {
        return "Delegated authentication directory";
    }

    @Override
    public void setAttributes(Map<String, String> attributes) {
        throw new UnsupportedOperationException("You cannot mutate the attributes of " + this.getClass().getName());
    }

    @Override
    public User authenticate(String name, PasswordCredential credential) throws UserNotFoundException, InactiveAccountException, InvalidAuthenticationException, ExpiredCredentialException, OperationFailedException {
        User internalUser;
        if (this.isUserCreateOnAuthEnabled() || this.isUserUpdateOnAuthEnabled()) {
            internalUser = this.authenticateAndUpdateOrCreate(name, credential);
        } else {
            internalUser = this.findUserByName(name);
            if (internalUser.isActive()) {
                User ldapUser = this.ldapDirectory.authenticate(name, credential);
                if (this.isImportGroupsEnabled()) {
                    this.updateGroups(ldapUser, internalUser);
                }
            } else {
                throw new InactiveAccountException(internalUser.getName());
            }
        }
        this.updateAttributesAfterAuth(internalUser);
        return internalUser;
    }

    public User userAuthenticated(String username) throws OperationFailedException, UserNotFoundException, InactiveAccountException {
        User authenticated = super.userAuthenticated(username);
        this.updateAttributesAfterAuth(authenticated);
        return authenticated;
    }

    private void updateAttributesAfterAuth(User internalUser) throws OperationFailedException, UserNotFoundException {
        HashMap<String, Set<String>> attributesToUpdate = new HashMap<String, Set<String>>();
        attributesToUpdate.put("lastAuthenticated", Collections.singleton(Long.toString(System.currentTimeMillis())));
        this.internalDirectory.storeUserAttributes(internalUser.getName(), attributesToUpdate);
    }

    private User authenticateAndUpdateOrCreate(String name, PasswordCredential credential) throws InactiveAccountException, ExpiredCredentialException, OperationFailedException, InvalidAuthenticationException, UserNotFoundException {
        User ldapUser = this.ldapDirectory.authenticate(name, credential);
        User internalUser = this.updateUserFromRemoteDirectory(ldapUser);
        if (!internalUser.isActive()) {
            throw new InactiveAccountException(name);
        }
        return internalUser;
    }

    @Override
    public User updateUserFromRemoteDirectory(@Nonnull User ldapUser) throws OperationFailedException, UserNotFoundException {
        User internalUser;
        boolean updateUserAfterAuth;
        block16: {
            Directory directory;
            updateUserAfterAuth = true;
            internalUser = this.findLocalUserByExternalId(ldapUser.getExternalId());
            if (internalUser != null && !IdentifierUtils.equalsInLowerCase((String)internalUser.getName(), (String)ldapUser.getName())) {
                if (this.isUserUpdateOnAuthEnabled()) {
                    try {
                        directory = this.directoryDao.findById(ldapUser.getDirectoryId());
                        String oldName = internalUser.getName();
                        internalUser = this.internalDirectory.forceRenameUser(internalUser, ldapUser.getName());
                        this.eventPublisher.publish((Object)new UserRenamedEvent((Object)this, directory, internalUser, oldName));
                    }
                    catch (UserNotFoundException e) {
                        throw new ConcurrentModificationException("Unable to rename '" + internalUser.getName() + "' to new name '" + ldapUser.getName() + "' during login.");
                    }
                    catch (DirectoryNotFoundException e) {
                        throw new OperationFailedException("Invalid directory: directory " + ldapUser.getDirectoryId() + " not found", (Throwable)e);
                    }
                } else {
                    internalUser = null;
                }
            }
            if (internalUser == null) {
                try {
                    internalUser = this.internalDirectory.findUserByName(ldapUser.getName());
                    if (!StringUtils.isNotBlank((CharSequence)internalUser.getExternalId()) || internalUser.getExternalId().equals(ldapUser.getExternalId()) || !this.isUserUpdateOnAuthEnabled() || !this.isUserCreateOnAuthEnabled()) break block16;
                    try {
                        directory = this.directoryDao.findById(ldapUser.getDirectoryId());
                        User movedLdapUser = this.ldapDirectory.findUserByExternalId(internalUser.getExternalId());
                        String oldName = internalUser.getName();
                        User renamedUser = this.internalDirectory.forceRenameUser(internalUser, movedLdapUser.getName());
                        this.eventPublisher.publish((Object)new UserRenamedEvent((Object)this, directory, renamedUser, oldName));
                        internalUser = this.createLdapUserInLocalCache(ldapUser.getName(), ldapUser);
                        updateUserAfterAuth = false;
                    }
                    catch (UserNotFoundException directory2) {
                    }
                    catch (DirectoryNotFoundException ex) {
                        throw new OperationFailedException("Invalid directory: directory " + ldapUser.getDirectoryId() + " not found", (Throwable)ex);
                    }
                }
                catch (UserNotFoundException ex) {
                    if (this.isUserCreateOnAuthEnabled()) {
                        internalUser = this.createLdapUserInLocalCache(ldapUser.getName(), ldapUser);
                        updateUserAfterAuth = false;
                    }
                    throw ex;
                }
            }
        }
        if (updateUserAfterAuth) {
            if (this.isUserUpdateOnAuthEnabled()) {
                internalUser = this.updateLocalUserDetails(ldapUser, internalUser);
            }
            if (this.isImportGroupsEnabled()) {
                this.updateGroups(ldapUser, internalUser);
            }
        }
        return internalUser;
    }

    private User createLdapUserInLocalCache(String name, User ldapUser) throws OperationFailedException {
        try {
            return this.addLdapUser(ldapUser);
        }
        catch (InvalidUserException e1) {
            throw new OperationFailedException("Failed to clone LDAP user <" + name + "> to internal directory", (Throwable)e1);
        }
        catch (UserAlreadyExistsException e1) {
            User user;
            logger.info("User '{}' could not be found initially, but when cloning the user internally, user exists", (Object)name);
            try {
                user = this.findUserByName(name);
            }
            catch (UserNotFoundException e) {
                throw new ConcurrentModificationException("User '" + name + "' no longer exists.");
            }
            return user;
        }
    }

    private User findLocalUserByExternalId(String externalId) {
        try {
            return StringUtils.isNotBlank((CharSequence)externalId) ? this.internalDirectory.findUserByExternalId(externalId) : null;
        }
        catch (UserNotFoundException unf) {
            return null;
        }
    }

    private void preventExternalIdDuplication(User ldapUser, User internalUser) throws OperationFailedException, InvalidUserException, DirectoryNotFoundException {
        if (StringUtils.isBlank((CharSequence)ldapUser.getExternalId()) || ldapUser.getExternalId().equals(internalUser.getExternalId())) {
            return;
        }
        try {
            TimestampedUser internalUserByExternalId = this.internalDirectory.findUserByExternalId(ldapUser.getExternalId());
            if (internalUserByExternalId != null) {
                this.removeExternalId((User)internalUserByExternalId);
                logger.warn("Possible user unique id duplication, removing unique id: {} for user '{}'", (Object)internalUser.getExternalId(), (Object)internalUser.getName());
            }
        }
        catch (UserNotFoundException userNotFoundException) {
            // empty catch block
        }
    }

    public User addOrUpdateLdapUser(String name) throws UserNotFoundException, OperationFailedException {
        User ldapUser = this.ldapDirectory.findUserByName(name);
        try {
            TimestampedUser internalUser = this.internalDirectory.findUserByName(name);
            User updatedUser = this.updateLocalUserDetails(ldapUser, (User)internalUser);
            if (this.isImportGroupsEnabled()) {
                this.updateGroups(ldapUser, (User)internalUser);
            }
            return updatedUser;
        }
        catch (UserNotFoundException internalUser) {
            try {
                return this.addLdapUser(ldapUser);
            }
            catch (UserAlreadyExistsException e) {
                logger.info("User was added during the internal cloning process. Returning found user.");
                return this.findUserByName(name);
            }
            catch (InvalidUserException e) {
                throw new OperationFailedException(name, (Throwable)e);
            }
        }
    }

    private User addLdapUser(User user) throws OperationFailedException, InvalidUserException, UserAlreadyExistsException {
        try {
            UserWithAttributes createdUser = this.addUser(UserTemplateWithAttributes.toUserWithNoAttributes((User)user), null);
            Directory dir = this.directoryDao.findById(createdUser.getDirectoryId());
            this.eventPublisher.publish((Object)new AutoUserCreatedEvent((Object)this, dir, (User)createdUser));
            if (this.isImportGroupsEnabled()) {
                List<String> ldapGroups = this.getGroups(user, this.ldapDirectory, String.class);
                this.importGroupsAndMemberships(user, dir, ldapGroups);
                if (this.supportsNestedGroups()) {
                    this.importGroupHierarchy(ldapGroups, dir);
                }
            }
            return createdUser;
        }
        catch (InvalidCredentialException e) {
            throw new OperationFailedException("Could not create authenticated user <" + user.getName() + "> in underlying InternalDirectory: " + e.getMessage(), (Throwable)e);
        }
        catch (DirectoryNotFoundException e) {
            throw new ConcurrentModificationException("Directory mapping was removed while cloning a user: " + e.getMessage());
        }
    }

    private void importGroupsAndMemberships(User user, Directory dir, Collection<String> groupNames) throws OperationFailedException {
        IdentifierMap internalGroupsByName = new IdentifierMap((Map)Maps.uniqueIndex(this.getInternalGroups(groupNames), DirectoryEntity::getName));
        HashSet<Object> membershipsToAdd = new HashSet<Object>();
        HashSet<String> groupsToAdd = new HashSet<String>();
        for (String groupName : groupNames) {
            InternalDirectoryGroup existingGroup = (InternalDirectoryGroup)internalGroupsByName.get((Object)groupName);
            if (existingGroup == null) {
                groupsToAdd.add(groupName);
                continue;
            }
            if (existingGroup.isLocal()) {
                logger.info("Remote group \"{}\" in directory \"{}\" is shadowed by a local group of the same name and will not be imported.", (Object)existingGroup.getName(), (Object)this.getDescriptiveName());
                continue;
            }
            membershipsToAdd.add(existingGroup);
        }
        if (!groupsToAdd.isEmpty()) {
            membershipsToAdd.addAll(this.importGroups(groupsToAdd, dir));
        }
        if (!membershipsToAdd.isEmpty()) {
            this.importMemberships(user.getName(), membershipsToAdd.stream().map(DirectoryEntity::getName).collect(Collectors.toSet()), dir);
        }
    }

    private Set<InternalDirectoryGroup> getInternalGroups(Collection<String> groupNames) throws OperationFailedException {
        if (groupNames.isEmpty()) {
            return ImmutableSet.of();
        }
        return Sets.newHashSet((Iterable)this.internalDirectory.searchGroups(QueryBuilder.queryFor(InternalDirectoryGroup.class, (EntityDescriptor)EntityDescriptor.group()).with(Restriction.on((Property)GroupTermKeys.NAME).exactlyMatchingAny(groupNames)).returningAtMost(-1)));
    }

    private List<Group> importGroups(Set<String> groupNames, Directory dir) {
        HashSet groupTemplates = new HashSet();
        groupNames.forEach(groupName -> {
            GroupTemplate groupTemplate = new GroupTemplate(groupName, this.internalDirectory.getDirectoryId());
            groupTemplate.setLocal(false);
            groupTemplates.add(groupTemplate);
        });
        BatchResult groupBatchResult = this.internalDirectory.addAllGroups(groupTemplates);
        groupBatchResult.getSuccessfulEntities().forEach(entity -> {
            this.logRemoteGroupImported(entity.getName());
            this.eventPublisher.publish((Object)new AutoGroupCreatedEvent((Object)this, dir, entity));
        });
        groupBatchResult.getFailedEntities().forEach(entity -> this.logCouldNotImportRemoteGroup(entity.getName(), null));
        return groupBatchResult.getSuccessfulEntities();
    }

    private Group importGroup(String groupName, Directory dir) {
        try {
            GroupTemplate groupTemplate = new GroupTemplate(groupName, this.internalDirectory.getDirectoryId());
            groupTemplate.setLocal(false);
            Group createdGroup = this.internalDirectory.addGroup(groupTemplate);
            this.logRemoteGroupImported(groupName);
            this.eventPublisher.publish((Object)new AutoGroupCreatedEvent((Object)this, dir, createdGroup));
            return createdGroup;
        }
        catch (Exception e) {
            this.logCouldNotImportRemoteGroup(groupName, e);
            return null;
        }
    }

    private void logRemoteGroupImported(String groupName) {
        logger.info("Imported remote group \"{}\" to directory \"{}\".", (Object)groupName, (Object)this.getDescriptiveName());
    }

    private void logCouldNotImportRemoteGroup(String groupName, Throwable exception) {
        logger.error("Could not import remote group \"{}\" to directory \"{}\".", new Object[]{groupName, this.getDescriptiveName(), exception});
    }

    private void importMemberships(String username, Set<String> groupNames, Directory dir) throws OperationFailedException {
        BatchResult internalMembershipBatchResult;
        try {
            internalMembershipBatchResult = this.internalDirectory.addUserToGroups(username, groupNames);
        }
        catch (UserNotFoundException e) {
            throw new OperationFailedException((Throwable)e);
        }
        internalMembershipBatchResult.getSuccessfulEntities().forEach(groupName -> {
            logger.info("Imported user \"{}\"'s membership of remote group \"{}\" to directory \"{}\".", new Object[]{username, groupName, this.getDescriptiveName()});
            this.eventPublisher.publish((Object)new AutoGroupMembershipCreatedEvent((Object)this, dir, username, groupName, MembershipType.GROUP_USER));
        });
        internalMembershipBatchResult.getFailedEntities().forEach(groupName -> logger.error("Could not import user \"{}\"'s membership of remote group \"{}\" to directory \"{}\".", new Object[]{username, groupName, this.getDescriptiveName()}));
    }

    private void importGroupMembership(String childGroupName, String parentGroupName, Directory dir) {
        try {
            this.addGroupToGroup(childGroupName, parentGroupName);
            logger.info("Imported group \"{}\"'s membership of remote group \"{}\" to directory \"{}\".", new Object[]{childGroupName, parentGroupName, this.getDescriptiveName()});
            this.eventPublisher.publish((Object)new AutoGroupMembershipCreatedEvent((Object)this, dir, childGroupName, parentGroupName, MembershipType.GROUP_GROUP));
        }
        catch (Exception exception) {
            logger.error("Could not import group \"{}\"'s membership of remote group \"{}\" to directory \"{}\".", new Object[]{childGroupName, parentGroupName, this.getDescriptiveName(), exception});
        }
    }

    private void removeGroupMembership(String childGroupName, String parentGroupName, Directory dir) {
        try {
            this.removeGroupFromGroup(childGroupName, parentGroupName);
            logger.info("Removed group \"{}\"'s membership of remote group \"{}\" in directory \"{}\".", new Object[]{childGroupName, parentGroupName, this.getDescriptiveName()});
            this.eventPublisher.publish((Object)new AutoGroupMembershipDeletedEvent((Object)this, dir, childGroupName, parentGroupName, MembershipType.GROUP_GROUP));
        }
        catch (Exception exception) {
            logger.error("Could not remove group \"{}\"'s membership of remote group \"{}\" in directory \"{}\".", new Object[]{childGroupName, parentGroupName, this.getDescriptiveName(), exception});
        }
    }

    private User updateLocalUserDetails(User ldapUser, User internalUser) throws OperationFailedException {
        try {
            Directory directory = this.directoryDao.findById(ldapUser.getDirectoryId());
            UserTemplate template = new UserTemplate(ldapUser);
            template.setActive(internalUser.isActive());
            if (!ldapUser.getName().equals(internalUser.getName())) {
                try {
                    String oldName = internalUser.getName();
                    User renamedUser = this.renameUser(internalUser.getName(), ldapUser.getName());
                    this.eventPublisher.publish((Object)new UserRenamedEvent((Object)this, directory, renamedUser, oldName));
                }
                catch (UserAlreadyExistsException e) {
                    template.setName(internalUser.getName());
                    logger.warn("Remote username '{}' casing differs from local username '{}', but the username cannot be updated", (Object)ldapUser.getName(), (Object)internalUser.getName());
                }
            }
            this.preventExternalIdDuplication(ldapUser, internalUser);
            ImmutableUser originalUser = ImmutableUser.from((User)internalUser);
            User updatedUser = this.updateUser(template);
            this.eventPublisher.publish((Object)new AutoUserUpdatedEvent((Object)this, directory, updatedUser, (User)originalUser));
            return updatedUser;
        }
        catch (UserNotFoundException e) {
            throw new ConcurrentModificationException("User was removed during cloning process: " + e.getMessage());
        }
        catch (DirectoryNotFoundException e) {
            throw new ConcurrentModificationException("Directory mapping was removed while cloning a user: " + e.getMessage());
        }
        catch (InvalidUserException e) {
            throw new OperationFailedException("Invalid user: unable to update user: '" + ldapUser.getName() + "' with data from LDAP", (Throwable)e);
        }
    }

    private void removeExternalId(User user) throws UserNotFoundException, InvalidUserException, OperationFailedException, DirectoryNotFoundException {
        UserTemplate userTemplate = new UserTemplate(user);
        userTemplate.setExternalId(null);
        Directory dir = this.directoryDao.findById(user.getDirectoryId());
        this.updateUser(userTemplate);
        this.eventPublisher.publish((Object)new AutoUserUpdatedEvent((Object)this, dir, (User)userTemplate, user));
    }

    private void updateGroups(User ldapUser, User internalUser) {
        try {
            Directory dir = this.directoryDao.findById(ldapUser.getDirectoryId());
            HashSet ldapGroupNames = Sets.newHashSet(this.getGroups(ldapUser, this.ldapDirectory, String.class));
            ImmutableMap internalGroupsMap = Maps.uniqueIndex(this.getGroups(internalUser, (RemoteDirectory)this.internalDirectory, InternalDirectoryGroup.class), (Function)DirectoryEntities.NAME_FUNCTION);
            Set internalGroupNames = internalGroupsMap.keySet();
            for (String groupName : IdentifierSet.differenceWithOriginalCasing(internalGroupNames, (Collection)ldapGroupNames)) {
                if (((InternalDirectoryGroup)internalGroupsMap.get(groupName)).isLocal()) continue;
                try {
                    this.removeUserFromGroup(internalUser.getName(), groupName);
                    this.eventPublisher.publish((Object)new AutoGroupMembershipDeletedEvent((Object)this, dir, internalUser.getName(), groupName, MembershipType.GROUP_USER));
                    logger.info("Deleted user \"{}\"'s imported membership of remote group \"{}\" to directory \"{}\".", new Object[]{internalUser.getName(), groupName, this.getDescriptiveName()});
                }
                catch (Exception exception) {
                    logger.error("Could not delete user \"{}\"'s imported membership of remote group \"{}\" to directory \"{}\".", new Object[]{internalUser.getName(), groupName, this.getDescriptiveName(), exception});
                }
            }
            this.importGroupsAndMemberships(internalUser, dir, IdentifierSet.differenceWithOriginalCasing((Collection)ldapGroupNames, internalGroupNames));
            if (this.supportsNestedGroups()) {
                this.importGroupHierarchy(ldapGroupNames, dir);
            }
        }
        catch (DirectoryNotFoundException e) {
            throw new ConcurrentModificationException("Directory mapping was removed while updating the groups of a user " + e.getMessage());
        }
        catch (Exception exception) {
            logger.error("Could not update remote group imported memberships of user \"{}\" in directory \"{}\".", new Object[]{internalUser.getName(), this.getDescriptiveName(), exception});
        }
    }

    private void importGroupHierarchy(Collection<String> ldapGroupNames, Directory dir) throws OperationFailedException {
        this.importGroupHierarchy(ldapGroupNames, dir, Collections.emptySet());
    }

    private void importGroupHierarchy(Collection<String> ldapGroupNames, Directory dir, Set<String> alreadySyncGroups) throws OperationFailedException {
        for (String ldapGroupName : ldapGroupNames) {
            if (alreadySyncGroups.contains(ldapGroupName)) continue;
            ImmutableSet newAlreadySyncGroups = ImmutableSet.builder().addAll(alreadySyncGroups).add((Object)ldapGroupName).build();
            MembershipQuery<String> directParentGroupsQuery = this.getDirectParentGroupsQuery(ldapGroupName);
            ImmutableSet ldapDirectParentGroups = ImmutableSet.copyOf((Collection)this.ldapDirectory.searchGroupRelationships(directParentGroupsQuery));
            ImmutableSet internalDirectParentGroups = ImmutableSet.copyOf((Collection)this.internalDirectory.searchGroupRelationships(directParentGroupsQuery));
            for (String directParentGroupInLdapButNotInInternal : Sets.difference((Set)ldapDirectParentGroups, (Set)internalDirectParentGroups)) {
                if (!newAlreadySyncGroups.contains(directParentGroupInLdapButNotInInternal)) {
                    try {
                        InternalDirectoryGroup internalParentGroup = this.internalDirectory.findGroupByName(directParentGroupInLdapButNotInInternal);
                        if (internalParentGroup.isLocal()) {
                            logger.info("Remote group \"{}\" in directory \"{}\" is shadowed by a local group of the same name and will not be imported.", (Object)internalParentGroup.getName(), (Object)this.getDescriptiveName());
                            continue;
                        }
                        logger.debug("Remote group \"{}\" in directory \"{}\" has already been imported.", (Object)internalParentGroup.getName(), (Object)this.getDescriptiveName());
                        this.importGroupMembership(ldapGroupName, directParentGroupInLdapButNotInInternal, dir);
                    }
                    catch (GroupNotFoundException e) {
                        this.importGroup(directParentGroupInLdapButNotInInternal, dir);
                        this.importGroupMembership(ldapGroupName, directParentGroupInLdapButNotInInternal, dir);
                    }
                    catch (Exception e) {
                        logger.error("Could not import group \"{}\"'s membership of remote group \"{}\" to directory \"{}\".", new Object[]{ldapGroupName, directParentGroupInLdapButNotInInternal, this.getDescriptiveName(), e});
                    }
                    continue;
                }
                logger.error("Importing remote group \"{}\"'s membership of remote group \"{}\" to directory \"{}\" would introduce a loop in the group hierarchy.", new Object[]{ldapGroupName, directParentGroupInLdapButNotInInternal, this.getDescriptiveName()});
            }
            for (String directParentGroupInInternalButNotInLdap : Sets.difference((Set)internalDirectParentGroups, (Set)ldapDirectParentGroups)) {
                this.removeGroupMembership(ldapGroupName, directParentGroupInInternalButNotInLdap, dir);
            }
            this.importGroupHierarchy((Collection<String>)ldapDirectParentGroups, dir, (Set<String>)newAlreadySyncGroups);
        }
    }

    private MembershipQuery<String> getDirectParentGroupsQuery(String ldapGroupName) {
        return QueryBuilder.queryFor(String.class, (EntityDescriptor)EntityDescriptor.group()).parentsOf(EntityDescriptor.group()).withName(ldapGroupName).returningAtMost(-1);
    }

    private <T> List<T> getGroups(User user, RemoteDirectory directory, Class<T> returnType) throws OperationFailedException {
        return directory.searchGroupRelationships(QueryBuilder.queryFor(returnType, (EntityDescriptor)EntityDescriptor.group()).parentsOf(EntityDescriptor.user()).withName(user.getName()).returningAtMost(-1));
    }

    @Override
    public void updateUserCredential(String username, PasswordCredential credential) throws UserNotFoundException, InvalidCredentialException, OperationFailedException {
        throw new OperationNotSupportedException("Passwords are stored in LDAP and are read-only for delegated authentication directory");
    }

    @Override
    public Group addGroup(GroupTemplate group) throws InvalidGroupException, OperationFailedException {
        group.setLocal(true);
        return super.addGroup(group);
    }

    @Override
    public void testConnection() throws OperationFailedException {
        this.ldapDirectory.testConnection();
    }

    @Override
    public boolean supportsNestedGroups() {
        return this.ldapDirectory.supportsNestedGroups();
    }

    @Override
    public boolean supportsPasswordExpiration() {
        return this.ldapDirectory.supportsPasswordExpiration();
    }

    @Override
    public boolean supportsSettingEncryptedCredential() {
        return false;
    }

    @Override
    public boolean isRolesDisabled() {
        return true;
    }

    @Override
    public RemoteDirectory getAuthoritativeDirectory() {
        return this.ldapDirectory;
    }

    protected InternalRemoteDirectory getDelegate() {
        return this.internalDirectory;
    }

    private boolean isUserCreateOnAuthEnabled() {
        return Boolean.parseBoolean(this.getValue(ATTRIBUTE_CREATE_USER_ON_AUTH));
    }

    private boolean isUserUpdateOnAuthEnabled() {
        return Boolean.parseBoolean(this.getValue(ATTRIBUTE_UPDATE_USER_ON_AUTH));
    }

    private boolean isImportGroupsEnabled() {
        return Boolean.parseBoolean(this.getValue(ATTRIBUTE_KEY_IMPORT_GROUPS));
    }

    public <T> ListMultimap<String, T> searchGroupRelationshipsGroupedByName(MembershipQuery<T> query) {
        return this.internalDirectory.searchGroupRelationshipsGroupedByName(query);
    }
}

