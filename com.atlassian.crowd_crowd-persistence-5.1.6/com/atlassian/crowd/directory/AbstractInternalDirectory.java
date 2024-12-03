/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.AttributeValuesHolder
 *  com.atlassian.crowd.directory.DirectoryMembershipsIterable
 *  com.atlassian.crowd.directory.InternalRemoteDirectory
 *  com.atlassian.crowd.directory.RemoteDirectory
 *  com.atlassian.crowd.embedded.api.Attributes
 *  com.atlassian.crowd.embedded.api.PasswordConstraint
 *  com.atlassian.crowd.embedded.api.PasswordCredential
 *  com.atlassian.crowd.embedded.api.User
 *  com.atlassian.crowd.embedded.impl.IdentifierUtils
 *  com.atlassian.crowd.embedded.spi.DirectoryDao
 *  com.atlassian.crowd.embedded.spi.GroupDao
 *  com.atlassian.crowd.embedded.spi.MembershipDao
 *  com.atlassian.crowd.embedded.spi.UserDao
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
 *  com.atlassian.crowd.exception.UserAlreadyExistsException
 *  com.atlassian.crowd.exception.UserNotFoundException
 *  com.atlassian.crowd.manager.avatar.AvatarReference
 *  com.atlassian.crowd.model.DirectoryEntity
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.group.GroupTemplate
 *  com.atlassian.crowd.model.group.GroupWithAttributes
 *  com.atlassian.crowd.model.group.InternalDirectoryGroup
 *  com.atlassian.crowd.model.group.Membership
 *  com.atlassian.crowd.model.user.TimestampedUser
 *  com.atlassian.crowd.model.user.User
 *  com.atlassian.crowd.model.user.UserTemplateWithAttributes
 *  com.atlassian.crowd.model.user.UserWithAttributes
 *  com.atlassian.crowd.password.encoder.PasswordEncoder
 *  com.atlassian.crowd.password.encoder.UpgradeablePasswordEncoder
 *  com.atlassian.crowd.password.factory.PasswordEncoderFactory
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 *  com.atlassian.crowd.search.query.membership.MembershipQuery
 *  com.atlassian.crowd.util.BatchResult
 *  com.atlassian.crowd.util.BoundedCount
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ListMultimap
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.Validate
 *  org.apache.commons.lang3.math.NumberUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.directory;

import com.atlassian.crowd.dao.tombstone.TombstoneDao;
import com.atlassian.crowd.directory.AttributeValuesHolder;
import com.atlassian.crowd.directory.DirectoryMembershipsIterable;
import com.atlassian.crowd.directory.InternalDirectory;
import com.atlassian.crowd.directory.InternalDirectoryUtils;
import com.atlassian.crowd.directory.InternalRemoteDirectory;
import com.atlassian.crowd.directory.PasswordConstraintsLoader;
import com.atlassian.crowd.directory.RemoteDirectory;
import com.atlassian.crowd.embedded.api.Attributes;
import com.atlassian.crowd.embedded.api.PasswordConstraint;
import com.atlassian.crowd.embedded.api.PasswordCredential;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.crowd.embedded.impl.IdentifierUtils;
import com.atlassian.crowd.embedded.spi.DirectoryDao;
import com.atlassian.crowd.embedded.spi.GroupDao;
import com.atlassian.crowd.embedded.spi.MembershipDao;
import com.atlassian.crowd.embedded.spi.UserDao;
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
import com.atlassian.crowd.exception.UserAlreadyExistsException;
import com.atlassian.crowd.exception.UserNotFoundException;
import com.atlassian.crowd.manager.avatar.AvatarReference;
import com.atlassian.crowd.model.DirectoryEntity;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.GroupTemplate;
import com.atlassian.crowd.model.group.GroupWithAttributes;
import com.atlassian.crowd.model.group.InternalDirectoryGroup;
import com.atlassian.crowd.model.group.Membership;
import com.atlassian.crowd.model.user.TimestampedUser;
import com.atlassian.crowd.model.user.UserTemplateWithAttributes;
import com.atlassian.crowd.model.user.UserWithAttributes;
import com.atlassian.crowd.password.encoder.PasswordEncoder;
import com.atlassian.crowd.password.encoder.UpgradeablePasswordEncoder;
import com.atlassian.crowd.password.factory.PasswordEncoderFactory;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import com.atlassian.crowd.search.query.membership.MembershipQuery;
import com.atlassian.crowd.util.BatchResult;
import com.atlassian.crowd.util.BoundedCount;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ListMultimap;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractInternalDirectory
implements InternalRemoteDirectory {
    public static final String DESCRIPTIVE_NAME = "Crowd Internal Directory";
    public static final String ATTRIBUTE_PASSWORD_REGEX = "password_regex";
    public static final String ATTRIBUTE_PASSWORD_COMPLEXITY_MESSAGE = "password_complexity_message";
    public static final String ATTRIBUTE_PASSWORD_MAX_ATTEMPTS = "password_max_attempts";
    public static final String ATTRIBUTE_PASSWORD_HISTORY_COUNT = "password_history_count";
    public static final String ATTRIBUTE_USER_ENCRYPTION_METHOD = "user_encryption_method";
    public static final String ATTRIBUTE_PASSWORD_MAX_CHANGE_TIME = "password_max_change_time";
    public static final String ATTRIBUTE_PASSWORD_EXPIRATION_NOTIFICATION_PERIODS = "password_expiration_notification_periods";
    public static final String ATTRIBUTE_PASSWORD_MINIMUM_LENGTH = "password_minimum_length";
    public static final String ATTRIBUTE_PASSWORD_MINIMUM_SCORE = "password_minimum_score";
    private static final Logger logger = LoggerFactory.getLogger(InternalDirectory.class);
    protected long directoryId;
    protected AttributeValuesHolder attributes;
    protected final PasswordEncoderFactory passwordEncoderFactory;
    protected final DirectoryDao directoryDao;
    protected final UserDao userDao;
    protected final GroupDao groupDao;
    protected final MembershipDao membershipDao;
    protected final TombstoneDao tombstoneDao;
    protected final InternalDirectoryUtils internalDirectoryUtils;
    private final PasswordConstraintsLoader passwordConstraints;

    public AbstractInternalDirectory(InternalDirectoryUtils internalDirectoryUtils, PasswordEncoderFactory passwordEncoderFactory, DirectoryDao directoryDao, UserDao userDao, GroupDao groupDao, MembershipDao membershipDao, TombstoneDao tombstoneDao, PasswordConstraintsLoader passwordConstraints) {
        this.internalDirectoryUtils = internalDirectoryUtils;
        this.directoryDao = directoryDao;
        this.passwordEncoderFactory = passwordEncoderFactory;
        this.membershipDao = membershipDao;
        this.groupDao = groupDao;
        this.userDao = userDao;
        this.tombstoneDao = tombstoneDao;
        this.passwordConstraints = passwordConstraints;
    }

    public long getDirectoryId() {
        return this.directoryId;
    }

    public void setDirectoryId(long id) {
        this.directoryId = id;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = new AttributeValuesHolder(attributes);
    }

    public Set<String> getValues(String name) {
        return this.attributes.getValues(name);
    }

    public String getValue(String name) {
        return this.attributes.getValue(name);
    }

    public Set<String> getKeys() {
        return this.attributes.getKeys();
    }

    public boolean isEmpty() {
        return this.attributes.isEmpty();
    }

    public String getDescriptiveName() {
        return DESCRIPTIVE_NAME;
    }

    public TimestampedUser findUserByName(String name) throws UserNotFoundException {
        Validate.notNull((Object)name, (String)"name argument cannot be null", (Object[])new Object[0]);
        return this.userDao.findByName(this.getDirectoryId(), name);
    }

    public TimestampedUser findUserByExternalId(String externalId) throws UserNotFoundException {
        Validate.notNull((Object)externalId, (String)"externalId argument cannot be null", (Object[])new Object[0]);
        return this.userDao.findByExternalId(this.getDirectoryId(), externalId);
    }

    public UserWithAttributes findUserWithAttributesByName(String name) throws UserNotFoundException {
        Validate.notNull((Object)name, (String)"name argument cannot be null", (Object[])new Object[0]);
        return this.userDao.findByNameWithAttributes(this.getDirectoryId(), name);
    }

    public com.atlassian.crowd.model.user.User authenticate(String name, PasswordCredential credential) throws InactiveAccountException, InvalidAuthenticationException, ExpiredCredentialException, UserNotFoundException {
        if (credential.isEncryptedCredential()) {
            throw InvalidAuthenticationException.newInstanceWithName((String)name);
        }
        UserWithAttributes user = this.userDao.findByNameWithAttributes(this.getDirectoryId(), name);
        if (user.isActive()) {
            this.processAuthentication(user, credential);
            return user;
        }
        throw new InactiveAccountException(user.getName());
    }

    public com.atlassian.crowd.model.user.User userAuthenticated(String username) throws OperationFailedException, UserNotFoundException, InactiveAccountException {
        com.atlassian.crowd.model.user.User authenticated = super.userAuthenticated(username);
        this.storeUserAttributes(authenticated.getName(), Collections.singletonMap("lastAuthenticated", Collections.singleton(Long.toString(System.currentTimeMillis()))));
        return authenticated;
    }

    private void processAuthentication(UserWithAttributes user, PasswordCredential credential) throws InvalidAuthenticationException, ExpiredCredentialException, UserNotFoundException {
        long currentInvalidAttempts = this.processPasswordAttempts(user);
        HashMap<String, Set<String>> attributesToUpdate = new HashMap<String, Set<String>>();
        try {
            PasswordCredential currentCredential = this.userDao.getCredential(this.directoryId, user.getName());
            this.authenticate((com.atlassian.crowd.model.user.User)user, credential, currentCredential, this.getValue(ATTRIBUTE_USER_ENCRYPTION_METHOD));
            boolean requiresPasswordChange = this.requiresPasswordChange(user);
            attributesToUpdate.put("requiresPasswordChange", Collections.singleton(Boolean.toString(requiresPasswordChange)));
            attributesToUpdate.put("invalidPasswordAttempts", Collections.singleton(Long.toString(0L)));
            attributesToUpdate.put("lastAuthenticated", Collections.singleton(Long.toString(System.currentTimeMillis())));
            this.userDao.storeAttributes((com.atlassian.crowd.model.user.User)user, attributesToUpdate, false);
            if (requiresPasswordChange) {
                logger.info(user.getName() + ": Attempting to log in with expired password.");
                throw new ExpiredCredentialException("Attempting to log in with expired password.");
            }
        }
        catch (InvalidAuthenticationException e) {
            attributesToUpdate.put("invalidPasswordAttempts", Collections.singleton(Long.toString(++currentInvalidAttempts)));
            this.userDao.storeAttributes((com.atlassian.crowd.model.user.User)user, attributesToUpdate, false);
            throw e;
        }
    }

    private long processPasswordAttempts(UserWithAttributes user) throws InvalidAuthenticationException, UserNotFoundException {
        long maxInvalidAttempts;
        long currentInvalidAttempts = this.currentPrincipalInvalidPasswordAttempts(user);
        String maxAttemptValue = this.getValue(ATTRIBUTE_PASSWORD_MAX_ATTEMPTS);
        if (maxAttemptValue != null && (maxInvalidAttempts = Long.parseLong(maxAttemptValue)) > 0L && currentInvalidAttempts >= maxInvalidAttempts) {
            HashMap<String, Set<String>> attributes = new HashMap<String, Set<String>>();
            attributes.put("requiresPasswordChange", Collections.singleton(Boolean.TRUE.toString()));
            this.userDao.storeAttributes((com.atlassian.crowd.model.user.User)user, attributes, false);
            logger.info(user.getName() + ": Maximum allowed invalid password attempts has been reached.");
            throw new InvalidAuthenticationException("Maximum allowed invalid password attempts has been reached");
        }
        return currentInvalidAttempts;
    }

    protected long currentPrincipalInvalidPasswordAttempts(UserWithAttributes user) {
        String attemptsAsString = user.getValue("invalidPasswordAttempts");
        long longAttempts = 0L;
        if (attemptsAsString != null) {
            try {
                longAttempts = Long.parseLong(attemptsAsString);
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }
        return longAttempts;
    }

    protected boolean requiresPasswordChange(UserWithAttributes user) {
        long maxUnchangedDays;
        boolean requiresPasswordChange = Boolean.parseBoolean(user.getValue("requiresPasswordChange"));
        if (requiresPasswordChange) {
            return true;
        }
        String maxChangeValue = this.getValue(ATTRIBUTE_PASSWORD_MAX_CHANGE_TIME);
        if (maxChangeValue != null && (maxUnchangedDays = Long.parseLong(maxChangeValue)) > 0L) {
            Date lastChanged;
            String time = user.getValue("passwordLastChanged");
            if (time != null) {
                try {
                    lastChanged = new Date(Long.parseLong(time));
                }
                catch (NumberFormatException e) {
                    lastChanged = new Date();
                }
            } else {
                lastChanged = new Date();
            }
            Date now = new Date();
            long maxUnchangedMilli = TimeUnit.DAYS.toMillis(maxUnchangedDays);
            if (now.getTime() - lastChanged.getTime() > maxUnchangedMilli) {
                requiresPasswordChange = true;
            }
        }
        return requiresPasswordChange;
    }

    private void authenticate(com.atlassian.crowd.model.user.User user, PasswordCredential providedCredential, PasswordCredential storedCredential, String encoderAlgorithm) throws InvalidAuthenticationException, UserNotFoundException {
        if (PasswordCredential.NONE.equals((Object)storedCredential)) {
            throw new InvalidAuthenticationException("Failed to authenticate principal, not allowed to login");
        }
        PasswordEncoder encoder = this.passwordEncoderFactory.getInternalEncoder(encoderAlgorithm);
        if (!encoder.isPasswordValid(storedCredential.getCredential(), providedCredential.getCredential(), null)) {
            throw new InvalidAuthenticationException("Failed to authenticate principal, password was invalid");
        }
        this.upgradePasswordIfRequired(user, encoder, storedCredential.getCredential(), providedCredential.getCredential());
    }

    private void upgradePasswordIfRequired(com.atlassian.crowd.model.user.User user, PasswordEncoder encoder, String encPass, String rawPass) throws UserNotFoundException {
        UpgradeablePasswordEncoder upgradeableEncoder;
        if (encoder instanceof UpgradeablePasswordEncoder && (upgradeableEncoder = (UpgradeablePasswordEncoder)encoder).isUpgradeRequired(encPass)) {
            String encPassword = upgradeableEncoder.encodePassword(rawPass, null);
            int maxHistoryCount = NumberUtils.toInt((String)this.getValue(ATTRIBUTE_PASSWORD_HISTORY_COUNT), (int)0);
            this.userDao.updateCredential(user, new PasswordCredential(encPassword, true), maxHistoryCount);
        }
    }

    public abstract UserWithAttributes addUser(UserTemplateWithAttributes var1, PasswordCredential var2) throws InvalidCredentialException, InvalidUserException, UserAlreadyExistsException, OperationFailedException;

    protected PasswordCredential encryptedCredential(PasswordCredential passwordCredential) {
        if (passwordCredential != null && !passwordCredential.isEncryptedCredential()) {
            String encryptedPassword = this.getEncoder().encodePassword(passwordCredential.getCredential(), null);
            return PasswordCredential.encrypted((String)encryptedPassword);
        }
        return passwordCredential;
    }

    protected PasswordEncoder getEncoder() {
        String userEncoder = this.getValue(ATTRIBUTE_USER_ENCRYPTION_METHOD);
        return this.passwordEncoderFactory.getInternalEncoder(userEncoder);
    }

    static String historyMatchDescription(int historyCount) {
        switch (historyCount) {
            case 1: {
                return "the current password";
            }
            case 2: {
                return "either the current password or the previous password";
            }
        }
        return "either the current password or one of the previous " + (historyCount - 1) + " passwords";
    }

    public void updateUserCredential(String name, PasswordCredential newCredential) throws InvalidCredentialException, UserNotFoundException {
        TimestampedUser user = this.userDao.findByName(this.getDirectoryId(), name);
        this.internalDirectoryUtils.validateCredential((User)user, newCredential, this.getPasswordConstraints(), this.getValue(ATTRIBUTE_PASSWORD_COMPLEXITY_MESSAGE));
        int historyCount = 0;
        String historyCountString = this.getValue(ATTRIBUTE_PASSWORD_HISTORY_COUNT);
        if (NumberUtils.isNumber((String)historyCountString)) {
            historyCount = Integer.parseInt(historyCountString);
            if (!newCredential.equals((Object)PasswordCredential.NONE)) {
                PasswordCredential currentCredential = this.userDao.getCredential(this.directoryId, name);
                List credentialHistory = this.userDao.getCredentialHistory(this.directoryId, name);
                if (historyCount != 0 && !this.isUniquePassword(newCredential, currentCredential, credentialHistory, historyCount)) {
                    throw new InvalidCredentialException("Unable to update password since this password matches " + AbstractInternalDirectory.historyMatchDescription(historyCount) + ".");
                }
            }
        }
        PasswordCredential encryptedNewCredential = this.encryptedCredential(newCredential);
        try {
            this.userDao.updateCredential((com.atlassian.crowd.model.user.User)user, encryptedNewCredential, historyCount);
        }
        catch (IllegalArgumentException e) {
            throw new InvalidCredentialException((Throwable)e);
        }
        this.userDao.storeAttributes((com.atlassian.crowd.model.user.User)user, AbstractInternalDirectory.calculatePostPasswordUpdateAttributes(), false);
    }

    protected static Map<String, Set<String>> calculatePostPasswordUpdateAttributes() {
        return ImmutableMap.of((Object)"passwordLastChanged", Collections.singleton(Long.toString(System.currentTimeMillis())), (Object)"requiresPasswordChange", Collections.singleton(Boolean.FALSE.toString()), (Object)"invalidPasswordAttempts", Collections.singleton(Long.toString(0L)));
    }

    private boolean isUniquePassword(PasswordCredential newCredential, PasswordCredential oldCredential, List<PasswordCredential> credentialHistory, int lastPasswordsToCheck) {
        Preconditions.checkArgument((!newCredential.isEncryptedCredential() ? 1 : 0) != 0, (Object)"The credentials should not be encrypted for the unique password check");
        String newPassword = newCredential.getCredential();
        PasswordEncoder encoder = this.getEncoder();
        if (oldCredential != null && encoder.isPasswordValid(oldCredential.getCredential(), newPassword, null)) {
            return false;
        }
        if (lastPasswordsToCheck > credentialHistory.size()) {
            lastPasswordsToCheck = credentialHistory.size();
        }
        for (int i = credentialHistory.size() - lastPasswordsToCheck; i < credentialHistory.size(); ++i) {
            PasswordCredential historicalCredential = credentialHistory.get(i);
            if (!encoder.isPasswordValid(historicalCredential.getCredential(), newPassword, null)) continue;
            return false;
        }
        return true;
    }

    public com.atlassian.crowd.model.user.User renameUser(String oldName, String newName) throws InvalidUserException, UserNotFoundException, UserAlreadyExistsException {
        Validate.notEmpty((CharSequence)oldName, (String)"oldName cannot be null or empty", (Object[])new Object[0]);
        Validate.notEmpty((CharSequence)newName, (String)"newName cannot be null or empty", (Object[])new Object[0]);
        TimestampedUser user = this.findUserByName(oldName);
        this.internalDirectoryUtils.validateUsername(newName);
        if (IdentifierUtils.equalsInLowerCase((String)oldName, (String)newName)) {
            return this.userDao.rename((com.atlassian.crowd.model.user.User)user, newName);
        }
        try {
            this.findUserByName(newName);
            throw new UserAlreadyExistsException(this.getDirectoryId(), newName);
        }
        catch (UserNotFoundException e) {
            return this.userDao.rename((com.atlassian.crowd.model.user.User)user, newName);
        }
    }

    public com.atlassian.crowd.model.user.User forceRenameUser(@Nonnull com.atlassian.crowd.model.user.User oldUser, @Nonnull String newName) throws UserNotFoundException {
        this.internalDirectoryUtils.validateUsername(newName);
        com.atlassian.crowd.model.user.User existingUser = IdentifierUtils.equalsInLowerCase((String)oldUser.getName(), (String)newName) ? null : this.findUserByNameOrNull(newName);
        if (existingUser != null) {
            try {
                this.userDao.rename(existingUser, this.findVacantUsername(newName));
            }
            catch (UserAlreadyExistsException ex) {
                throw new IllegalStateException("Unable to move user " + newName + " out of the way so we can rename " + oldUser.getName(), ex);
            }
            catch (UserNotFoundException ex) {
                // empty catch block
            }
        }
        try {
            return this.userDao.rename(oldUser, newName);
        }
        catch (UserAlreadyExistsException ex) {
            throw new IllegalStateException("Unable to rename user " + oldUser.getName() + " to " + newName, ex);
        }
    }

    protected final Set<PasswordConstraint> getPasswordConstraints() {
        return this.passwordConstraints.getFromDirectoryAttributes(this.directoryId, (Attributes)this.attributes);
    }

    @Nonnull
    public Set<String> getAllUserExternalIds() throws OperationFailedException {
        try {
            return this.userDao.getAllExternalIds(this.getDirectoryId());
        }
        catch (DirectoryNotFoundException e) {
            throw new OperationFailedException((Throwable)e);
        }
    }

    public long getUserCount() throws OperationFailedException {
        try {
            return this.userDao.getUserCount(this.getDirectoryId());
        }
        catch (DirectoryNotFoundException e) {
            throw new OperationFailedException((Throwable)e);
        }
    }

    public long getGroupCount() throws OperationFailedException {
        try {
            return this.groupDao.getGroupCount(this.getDirectoryId());
        }
        catch (DirectoryNotFoundException e) {
            throw new OperationFailedException((Throwable)e);
        }
    }

    private com.atlassian.crowd.model.user.User findUserByNameOrNull(String name) {
        try {
            return this.findUserByName(name);
        }
        catch (UserNotFoundException e) {
            return null;
        }
    }

    private String findVacantUsername(String usernamePrefix) {
        for (int i = 1; i <= 1000; ++i) {
            String username = usernamePrefix + '#' + i;
            try {
                this.findUserByName(username);
                continue;
            }
            catch (UserNotFoundException e) {
                return username;
            }
        }
        throw new IllegalStateException("Unable to find a vacant username for prefix " + usernamePrefix);
    }

    public void storeUserAttributes(String username, Map<String, Set<String>> attributes) throws UserNotFoundException, OperationFailedException {
        try {
            Validate.notNull(attributes, (String)"attributes cannot be null", (Object[])new Object[0]);
            TimestampedUser user = this.findUserByName(username);
            boolean onlyUpdatesAuthenticationRelatedAttributes = Collections.singleton("lastActive").equals(attributes.keySet());
            this.userDao.storeAttributes((com.atlassian.crowd.model.user.User)user, attributes, !onlyUpdatesAuthenticationRelatedAttributes);
        }
        catch (RuntimeException e) {
            throw new OperationFailedException((Throwable)e);
        }
    }

    public void removeUserAttributes(String username, String attributeName) throws UserNotFoundException {
        Validate.notEmpty((CharSequence)username, (String)"username cannot be null or empty", (Object[])new Object[0]);
        Validate.notNull((Object)attributeName, (String)"attributeName cannot be null", (Object[])new Object[0]);
        TimestampedUser user = this.findUserByName(username);
        this.userDao.removeAttribute((com.atlassian.crowd.model.user.User)user, attributeName);
    }

    public void removeUser(String name) throws UserNotFoundException {
        TimestampedUser user = this.findUserByName(name);
        this.userDao.remove((com.atlassian.crowd.model.user.User)user);
        this.tombstoneDao.storeUserTombstones(this.getDirectoryId(), Collections.singleton(name));
    }

    public BatchResult<String> removeAllUsers(Set<String> userNames) {
        BatchResult result = this.userDao.removeAllUsers(this.getDirectoryId(), userNames);
        this.tombstoneDao.storeUserTombstones(this.getDirectoryId(), result.getSuccessfulEntities());
        return result;
    }

    public BatchResult<String> removeAllGroups(Set<String> groupNames) {
        BatchResult result = this.groupDao.removeAllGroups(this.getDirectoryId(), groupNames);
        this.tombstoneDao.storeGroupTombstones(this.getDirectoryId(), result.getSuccessfulEntities());
        return result;
    }

    public <T> List<T> searchUsers(EntityQuery<T> query) {
        Validate.notNull(query, (String)"query cannot be null", (Object[])new Object[0]);
        return this.userDao.search(this.getDirectoryId(), query);
    }

    public InternalDirectoryGroup findGroupByName(String name) throws GroupNotFoundException {
        Validate.notNull((Object)name, (String)"name argument cannot be null", (Object[])new Object[0]);
        return this.groupDao.findByName(this.getDirectoryId(), name);
    }

    public GroupWithAttributes findGroupWithAttributesByName(String name) throws GroupNotFoundException {
        Validate.notNull((Object)name, (String)"name argument cannot be null", (Object[])new Object[0]);
        return this.groupDao.findByNameWithAttributes(this.getDirectoryId(), name);
    }

    public Group addGroup(GroupTemplate group) throws InvalidGroupException, OperationFailedException {
        this.internalDirectoryUtils.validateDirectoryForEntity((DirectoryEntity)group, this.directoryId);
        this.internalDirectoryUtils.validateGroupName((Group)group, group.getName());
        try {
            return group.isLocal() ? this.groupDao.addLocal((Group)group) : this.groupDao.add((Group)group);
        }
        catch (IllegalArgumentException e) {
            throw new InvalidGroupException((Group)group, e.getMessage(), (Throwable)e);
        }
        catch (DirectoryNotFoundException e) {
            throw new OperationFailedException((Throwable)e);
        }
    }

    public abstract Group addLocalGroup(GroupTemplate var1) throws InvalidGroupException, OperationFailedException;

    public Group updateGroup(GroupTemplate group) throws InvalidGroupException, GroupNotFoundException {
        this.internalDirectoryUtils.validateDirectoryForEntity((DirectoryEntity)group, this.directoryId);
        try {
            return this.groupDao.update((Group)group);
        }
        catch (IllegalArgumentException e) {
            throw new InvalidGroupException((Group)group, e.getMessage(), (Throwable)e);
        }
    }

    public Group renameGroup(String oldName, String newName) throws InvalidGroupException, GroupNotFoundException {
        Validate.notEmpty((CharSequence)oldName, (String)"oldName cannot be null or empty", (Object[])new Object[0]);
        Validate.notEmpty((CharSequence)newName, (String)"newName cannot be null or empty", (Object[])new Object[0]);
        InternalDirectoryGroup group = this.findGroupByName(oldName);
        this.internalDirectoryUtils.validateGroupName((Group)group, newName);
        return this.groupDao.rename((Group)group, newName);
    }

    public void storeGroupAttributes(String groupName, Map<String, Set<String>> attributes) throws GroupNotFoundException {
        Validate.notEmpty((CharSequence)groupName, (String)"groupName cannot be null or empty", (Object[])new Object[0]);
        Validate.notNull(attributes, (String)"attributes cannot be null", (Object[])new Object[0]);
        InternalDirectoryGroup group = this.findGroupByName(groupName);
        this.groupDao.storeAttributes((Group)group, attributes);
    }

    public void removeGroupAttributes(String groupName, String attributeName) throws GroupNotFoundException {
        Validate.notEmpty((CharSequence)groupName, (String)"groupName cannot be null or empty", (Object[])new Object[0]);
        Validate.notNull((Object)attributeName, (String)"attributeName cannot be null", (Object[])new Object[0]);
        InternalDirectoryGroup group = this.findGroupByName(groupName);
        this.groupDao.removeAttribute((Group)group, attributeName);
    }

    public void removeGroup(String name) throws GroupNotFoundException {
        InternalDirectoryGroup group = this.findGroupByName(name);
        this.groupDao.remove((Group)group);
        this.tombstoneDao.storeGroupTombstones(this.getDirectoryId(), Collections.singleton(name));
    }

    public <T> List<T> searchGroups(EntityQuery<T> query) {
        Validate.notNull(query, (String)"query cannot be null", (Object[])new Object[0]);
        return this.groupDao.search(this.getDirectoryId(), query);
    }

    public boolean isUserDirectGroupMember(String username, String groupName) {
        Validate.notEmpty((CharSequence)username, (String)"username cannot be null or empty", (Object[])new Object[0]);
        Validate.notEmpty((CharSequence)groupName, (String)"groupName cannot be null or empty", (Object[])new Object[0]);
        return this.membershipDao.isUserDirectMember(this.getDirectoryId(), username, groupName);
    }

    public boolean isGroupDirectGroupMember(String childGroup, String parentGroup) {
        Validate.notEmpty((CharSequence)childGroup, (String)"childGroup cannot be null or empty", (Object[])new Object[0]);
        Validate.notEmpty((CharSequence)parentGroup, (String)"parentGroup cannot be null or empty", (Object[])new Object[0]);
        return this.membershipDao.isGroupDirectMember(this.getDirectoryId(), childGroup, parentGroup);
    }

    public BatchResult<String> addUserToGroups(String username, Set<String> groupNames) throws UserNotFoundException {
        Validate.notNull((Object)username, (String)"username cannot be null", (Object[])new Object[0]);
        Validate.notNull(groupNames, (String)"groups cannot be null", (Object[])new Object[0]);
        return this.membershipDao.addUserToGroups(this.directoryId, username, groupNames);
    }

    public void addUserToGroup(String username, String groupName) throws UserNotFoundException, GroupNotFoundException, MembershipAlreadyExistsException {
        Validate.notEmpty((CharSequence)username, (String)"username cannot be null or empty", (Object[])new Object[0]);
        Validate.notEmpty((CharSequence)groupName, (String)"groupName cannot be null or empty", (Object[])new Object[0]);
        this.membershipDao.addUserToGroup(this.getDirectoryId(), username, groupName);
    }

    public void addGroupToGroup(String childGroup, String parentGroup) throws InvalidMembershipException, GroupNotFoundException, MembershipAlreadyExistsException {
        Validate.notEmpty((CharSequence)childGroup, (String)"childGroup cannot be null or empty", (Object[])new Object[0]);
        Validate.notEmpty((CharSequence)parentGroup, (String)"parentGroup cannot be null or empty", (Object[])new Object[0]);
        InternalDirectoryGroup child = this.findGroupByName(childGroup);
        InternalDirectoryGroup parent = this.findGroupByName(parentGroup);
        if (!child.getType().equals((Object)parent.getType())) {
            throw new InvalidMembershipException("Cannot add group of type " + child.getType().name() + " to group of type " + parent.getType().name());
        }
        this.membershipDao.addGroupToGroup(this.getDirectoryId(), childGroup, parentGroup);
    }

    public BatchResult<String> addAllGroupsToGroup(Collection<String> childGroupNames, String groupName) throws GroupNotFoundException {
        Validate.notNull(childGroupNames, (String)"childGroupNames cannot be null", (Object[])new Object[0]);
        Validate.notEmpty((CharSequence)groupName, (String)"groupName cannot be null or empty", (Object[])new Object[0]);
        return this.membershipDao.addAllGroupsToGroup(this.getDirectoryId(), childGroupNames, groupName);
    }

    public void removeUserFromGroup(String username, String groupName) throws MembershipNotFoundException, GroupNotFoundException, UserNotFoundException {
        Validate.notEmpty((CharSequence)username, (String)"username cannot be null or empty", (Object[])new Object[0]);
        Validate.notEmpty((CharSequence)groupName, (String)"groupName cannot be null or empty", (Object[])new Object[0]);
        this.findUserByName(username);
        this.findGroupByName(groupName);
        if (!this.isUserDirectGroupMember(username, groupName)) {
            throw new MembershipNotFoundException(username, groupName);
        }
        this.membershipDao.removeUserFromGroup(this.getDirectoryId(), username, groupName);
        this.tombstoneDao.storeUserMembershipTombstone(this.getDirectoryId(), username, groupName);
    }

    public BatchResult<String> removeUsersFromGroup(Set<String> usernames, String groupName) throws GroupNotFoundException {
        Validate.notEmpty(usernames, (String)"usernames cannot be null or empty", (Object[])new Object[0]);
        Validate.notEmpty((CharSequence)groupName, (String)"groupName cannot be null or empty", (Object[])new Object[0]);
        BatchResult removalResult = this.membershipDao.removeUsersFromGroup(this.getDirectoryId(), usernames, groupName);
        removalResult.getSuccessfulEntities().forEach(username -> this.tombstoneDao.storeUserMembershipTombstone(this.getDirectoryId(), (String)username, groupName));
        return removalResult;
    }

    public void removeGroupFromGroup(String childGroup, String parentGroup) throws InvalidMembershipException, MembershipNotFoundException, GroupNotFoundException {
        Validate.notEmpty((CharSequence)childGroup, (String)"childGroup cannot be null or empty", (Object[])new Object[0]);
        Validate.notEmpty((CharSequence)parentGroup, (String)"parentGroup cannot be null or empty", (Object[])new Object[0]);
        InternalDirectoryGroup child = this.findGroupByName(childGroup);
        InternalDirectoryGroup parent = this.findGroupByName(parentGroup);
        if (!this.isGroupDirectGroupMember(childGroup, parentGroup)) {
            throw new MembershipNotFoundException(childGroup, parentGroup);
        }
        if (!child.getType().equals((Object)parent.getType())) {
            throw new InvalidMembershipException("Cannot remove group of type " + child.getType().name() + " from group of type " + parent.getType().name());
        }
        this.membershipDao.removeGroupFromGroup(this.getDirectoryId(), childGroup, parentGroup);
        this.tombstoneDao.storeGroupMembershipTombstone(this.getDirectoryId(), childGroup, parentGroup);
    }

    public BatchResult<String> removeGroupsFromGroup(Collection<String> childGroupNames, String groupName) throws GroupNotFoundException {
        Validate.notEmpty(childGroupNames, (String)"childGroupNames cannot be null or empty", (Object[])new Object[0]);
        Validate.notEmpty((CharSequence)groupName, (String)"groupName cannot be null or empty", (Object[])new Object[0]);
        BatchResult removalResult = this.membershipDao.removeGroupsFromGroup(this.getDirectoryId(), childGroupNames, groupName);
        removalResult.getSuccessfulEntities().forEach(childGroupName -> this.tombstoneDao.storeGroupMembershipTombstone(this.getDirectoryId(), (String)childGroupName, groupName));
        return removalResult;
    }

    public BoundedCount countDirectMembersOfGroup(String groupName, int querySizeHint) {
        return this.membershipDao.countDirectMembersOfGroup(this.getDirectoryId(), groupName, querySizeHint);
    }

    public <T> List<T> searchGroupRelationships(MembershipQuery<T> query) {
        Validate.notNull(query, (String)"query cannot be null", (Object[])new Object[0]);
        return this.membershipDao.search(this.getDirectoryId(), query);
    }

    public <T> ListMultimap<String, T> searchGroupRelationshipsGroupedByName(MembershipQuery<T> query) {
        Validate.notNull(query, (String)"query cannot be null", (Object[])new Object[0]);
        return this.membershipDao.searchGroupedByName(this.getDirectoryId(), query);
    }

    public void testConnection() throws OperationFailedException {
    }

    public boolean supportsInactiveAccounts() {
        return true;
    }

    public boolean supportsNestedGroups() {
        return this.attributes.getAttributeAsBoolean("useNestedGroups", false);
    }

    public boolean supportsPasswordExpiration() {
        return true;
    }

    public boolean supportsSettingEncryptedCredential() {
        return true;
    }

    public boolean isRolesDisabled() {
        return true;
    }

    public Iterable<Membership> getMemberships() throws OperationFailedException {
        return new DirectoryMembershipsIterable((RemoteDirectory)this);
    }

    public RemoteDirectory getAuthoritativeDirectory() {
        return this;
    }

    public void expireAllPasswords() {
        this.userDao.setAttributeForAllInDirectory(this.directoryId, "requiresPasswordChange", "true");
    }

    public AvatarReference getUserAvatarByName(String username, int sizeHint) throws OperationFailedException {
        return null;
    }
}

