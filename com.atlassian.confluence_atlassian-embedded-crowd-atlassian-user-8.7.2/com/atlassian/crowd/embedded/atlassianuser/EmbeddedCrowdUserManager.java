/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.CrowdDirectoryService
 *  com.atlassian.crowd.embedded.api.CrowdService
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.OperationType
 *  com.atlassian.crowd.embedded.api.Query
 *  com.atlassian.crowd.embedded.api.User
 *  com.atlassian.crowd.exception.InvalidCredentialException
 *  com.atlassian.crowd.exception.InvalidUserException
 *  com.atlassian.crowd.exception.OperationNotPermittedException
 *  com.atlassian.crowd.exception.runtime.UserNotFoundException
 *  com.atlassian.crowd.model.user.User
 *  com.atlassian.crowd.model.user.UserTemplate
 *  com.atlassian.crowd.search.EntityDescriptor
 *  com.atlassian.crowd.search.builder.QueryBuilder
 *  com.atlassian.crowd.util.SecureRandomStringUtils
 *  com.atlassian.user.Entity
 *  com.atlassian.user.EntityException
 *  com.atlassian.user.User
 *  com.atlassian.user.UserManager
 *  com.atlassian.user.impl.DefaultUser
 *  com.atlassian.user.impl.DuplicateEntityException
 *  com.atlassian.user.impl.EntityMissingException
 *  com.atlassian.user.impl.EntityValidationException
 *  com.atlassian.user.repository.RepositoryIdentifier
 *  com.atlassian.user.search.page.DefaultPager
 *  com.atlassian.user.search.page.Pager
 *  com.atlassian.user.search.page.Pagers
 *  com.atlassian.user.security.authentication.InvalidPasswordException
 *  com.atlassian.user.security.password.Credential
 *  com.atlassian.user.util.Assert
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 */
package com.atlassian.crowd.embedded.atlassianuser;

import com.atlassian.crowd.embedded.api.CrowdDirectoryService;
import com.atlassian.crowd.embedded.api.CrowdService;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.OperationType;
import com.atlassian.crowd.embedded.api.Query;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.crowd.embedded.atlassianuser.Conversions;
import com.atlassian.crowd.exception.InvalidCredentialException;
import com.atlassian.crowd.exception.InvalidUserException;
import com.atlassian.crowd.exception.OperationNotPermittedException;
import com.atlassian.crowd.exception.runtime.UserNotFoundException;
import com.atlassian.crowd.model.user.UserTemplate;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.builder.QueryBuilder;
import com.atlassian.crowd.util.SecureRandomStringUtils;
import com.atlassian.user.Entity;
import com.atlassian.user.EntityException;
import com.atlassian.user.UserManager;
import com.atlassian.user.impl.DefaultUser;
import com.atlassian.user.impl.DuplicateEntityException;
import com.atlassian.user.impl.EntityMissingException;
import com.atlassian.user.impl.EntityValidationException;
import com.atlassian.user.repository.RepositoryIdentifier;
import com.atlassian.user.search.page.DefaultPager;
import com.atlassian.user.search.page.Pager;
import com.atlassian.user.search.page.Pagers;
import com.atlassian.user.security.authentication.InvalidPasswordException;
import com.atlassian.user.security.password.Credential;
import com.atlassian.user.util.Assert;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.Set;

@Deprecated
public final class EmbeddedCrowdUserManager
implements UserManager {
    private static final String DEFAULT_BLANK = "-";
    private static final int RANDOM_PASSWORD_LENGTH = 22;
    private final RepositoryIdentifier repositoryIdentifier;
    private final CrowdService crowdService;
    private final CrowdDirectoryService crowdDirectoryService;

    public EmbeddedCrowdUserManager(RepositoryIdentifier repositoryIdentifier, CrowdService crowdService, CrowdDirectoryService crowdDirectoryService) {
        this.repositoryIdentifier = (RepositoryIdentifier)Preconditions.checkNotNull((Object)repositoryIdentifier);
        this.crowdService = (CrowdService)Preconditions.checkNotNull((Object)crowdService);
        this.crowdDirectoryService = (CrowdDirectoryService)Preconditions.checkNotNull((Object)crowdDirectoryService);
    }

    public RepositoryIdentifier getIdentifier() {
        return this.repositoryIdentifier;
    }

    public RepositoryIdentifier getRepository(Entity entity) {
        if (this.getUser(entity.getName()) != null) {
            return this.repositoryIdentifier;
        }
        return null;
    }

    public boolean isCreative() {
        return true;
    }

    public Pager<com.atlassian.user.User> getUsers() {
        Iterable allUsers = this.crowdService.search((Query)QueryBuilder.queryFor(User.class, (EntityDescriptor)EntityDescriptor.user()).returningAtMost(-1));
        return Pagers.newDefaultPager((Iterable)Iterables.transform((Iterable)allUsers, Conversions.TO_ATLASSIAN_USER));
    }

    public Pager<String> getUserNames() {
        Iterable usernames = this.crowdService.search((Query)QueryBuilder.queryFor(String.class, (EntityDescriptor)EntityDescriptor.user()).returningAtMost(-1));
        return new DefaultPager((Collection)Lists.newArrayList(usernames.iterator()));
    }

    public com.atlassian.user.User getUser(String username) {
        Assert.notNull((Object)username, (String)"username must not be null.");
        return (com.atlassian.user.User)Conversions.TO_ATLASSIAN_USER.apply((Object)this.crowdService.getUser(username));
    }

    public com.atlassian.user.User createUser(String username) throws EntityException {
        DefaultUser userTemplate = new DefaultUser(username, DEFAULT_BLANK, DEFAULT_BLANK);
        return this.createUser((com.atlassian.user.User)userTemplate, Credential.NONE);
    }

    public com.atlassian.user.User createUser(com.atlassian.user.User userTemplate, Credential credential) throws EntityException, IllegalArgumentException {
        User crowdUser;
        com.atlassian.user.User existingUser = this.getUser(userTemplate.getName());
        if (existingUser != null) {
            throw new DuplicateEntityException("User with name [" + userTemplate.getName() + "] already exists in this repository (" + this.getIdentifier().getName() + ")");
        }
        if (Credential.NONE.equals((Object)credential)) {
            credential = this.createRandomCredential();
        }
        if (credential.isEncrypted()) {
            throw new IllegalArgumentException("Cannot create a user with an already encrypted credential");
        }
        try {
            crowdUser = this.crowdService.addUser((User)this.toUserTemplate(userTemplate), credential.getValue());
        }
        catch (InvalidUserException e) {
            throw new EntityValidationException((Throwable)e);
        }
        catch (InvalidCredentialException e) {
            throw new InvalidPasswordException((Throwable)e);
        }
        catch (OperationNotPermittedException e) {
            throw new EntityException((Throwable)e);
        }
        return (com.atlassian.user.User)Conversions.TO_ATLASSIAN_USER.apply((Object)crowdUser);
    }

    private Credential createRandomCredential() {
        String randomPassword = SecureRandomStringUtils.getInstance().randomAlphanumericString(22);
        return Credential.unencrypted((String)randomPassword);
    }

    private UserTemplate toUserTemplate(com.atlassian.user.User atlassianUser) {
        UserTemplate template = new UserTemplate(atlassianUser.getName());
        template.setDisplayName(atlassianUser.getFullName());
        template.setEmailAddress(atlassianUser.getEmail());
        template.setActive(true);
        return template;
    }

    public void alterPassword(com.atlassian.user.User user, String password) throws EntityException {
        try {
            this.crowdService.updateUserCredential(this.getCrowdUser(user), password);
        }
        catch (UserNotFoundException e) {
            throw new EntityMissingException((Throwable)e);
        }
        catch (InvalidCredentialException e) {
            throw new InvalidPasswordException((Throwable)e);
        }
        catch (OperationNotPermittedException e) {
            throw new EntityException((Throwable)e);
        }
    }

    public void saveUser(com.atlassian.user.User user) throws EntityException, IllegalArgumentException {
        try {
            this.crowdService.updateUser((User)this.toUserTemplate(user));
        }
        catch (InvalidUserException e) {
            throw new EntityException((Throwable)e);
        }
        catch (OperationNotPermittedException e) {
            throw new EntityException((Throwable)e);
        }
    }

    public void removeUser(com.atlassian.user.User user) throws EntityException, IllegalArgumentException {
        try {
            User crowdUser = this.getCrowdUser(user);
            if (crowdUser == null) {
                throw new IllegalArgumentException("User [" + user.getName() + "] is not managed by embedded crowd");
            }
            this.crowdService.removeUser(crowdUser);
        }
        catch (OperationNotPermittedException e) {
            throw new EntityException((Throwable)e);
        }
    }

    private User getCrowdUser(com.atlassian.user.User user) throws IllegalArgumentException {
        Assert.notNull((Object)user, (String)"User should not be null");
        if (user instanceof com.atlassian.crowd.model.user.User) {
            return (com.atlassian.crowd.model.user.User)user;
        }
        return this.crowdService.getUser(user.getName());
    }

    public boolean isReadOnly(com.atlassian.user.User user) {
        User crowdUser = this.getCrowdUser(user);
        if (crowdUser == null) {
            return false;
        }
        Directory directory = this.crowdDirectoryService.findDirectoryById(crowdUser.getDirectoryId());
        Set allowedOperations = directory.getAllowedOperations();
        return !allowedOperations.contains(OperationType.CREATE_USER);
    }
}

