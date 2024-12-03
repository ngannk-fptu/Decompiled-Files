/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.PasswordCredential
 *  com.atlassian.crowd.embedded.api.User
 *  com.atlassian.crowd.embedded.api.UserWithAttributes
 *  com.atlassian.crowd.exception.DirectoryNotFoundException
 *  com.atlassian.crowd.exception.InvalidCredentialException
 *  com.atlassian.crowd.exception.InvalidUserException
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.exception.UserAlreadyExistsException
 *  com.atlassian.crowd.exception.UserNotFoundException
 *  com.atlassian.crowd.manager.directory.DirectoryManager
 *  com.atlassian.crowd.manager.directory.DirectoryPermissionException
 *  com.atlassian.crowd.model.user.UserTemplate
 *  com.atlassian.crowd.model.user.UserTemplateWithAttributes
 *  com.atlassian.crowd.model.user.UserWithAttributes
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.google.common.collect.ImmutableMap
 *  javax.inject.Inject
 *  javax.inject.Named
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.authentication.impl.web.usercontext.impl.jit;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.PasswordCredential;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.crowd.exception.DirectoryNotFoundException;
import com.atlassian.crowd.exception.InvalidCredentialException;
import com.atlassian.crowd.exception.InvalidUserException;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.exception.UserAlreadyExistsException;
import com.atlassian.crowd.exception.UserNotFoundException;
import com.atlassian.crowd.manager.directory.DirectoryManager;
import com.atlassian.crowd.manager.directory.DirectoryPermissionException;
import com.atlassian.crowd.model.user.UserTemplate;
import com.atlassian.crowd.model.user.UserTemplateWithAttributes;
import com.atlassian.crowd.model.user.UserWithAttributes;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.authentication.impl.license.ProductLicenseChecker;
import com.atlassian.plugins.authentication.impl.web.usercontext.impl.jit.JitCrowdUser;
import com.atlassian.plugins.authentication.impl.web.usercontext.impl.jit.JitException;
import com.atlassian.plugins.authentication.impl.web.usercontext.impl.jit.mapping.JitUserData;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
public class UserProvisioningService {
    private static final Logger log = LoggerFactory.getLogger(UserProvisioningService.class);
    private final DarkFeatureManager darkFeatureManager;
    private final ProductLicenseChecker productLicenseChecker;
    private final DirectoryManager directoryManager;

    @Inject
    public UserProvisioningService(@ComponentImport DarkFeatureManager darkFeatureManager, ProductLicenseChecker productLicenseChecker, @ComponentImport DirectoryManager directoryManager) {
        this.darkFeatureManager = darkFeatureManager;
        this.productLicenseChecker = productLicenseChecker;
        this.directoryManager = directoryManager;
    }

    public JitCrowdUser provisionUser(JitUserData userData, Directory jitDirectory) {
        log.debug("Attempting to JIT provision unrecognized user [{}]", (Object)userData.getUsername());
        try {
            if (!this.darkFeatureManager.isEnabledForAllUsers("atlassian.authentication.sso.jit.disable.license.check").orElse(false).booleanValue() && !this.productLicenseChecker.areSlotsAvailable((Set<String>)userData.getGroups())) {
                log.error("Did not provision user [{}] as license limit would have been exceeded", (Object)userData.getUsername());
                throw new JitException("User could not be created due to the license\u2019s limit.");
            }
            UserTemplateWithAttributes user = new UserTemplateWithAttributes((com.atlassian.crowd.embedded.api.UserWithAttributes)new JitCrowdUser(userData, jitDirectory.getId()));
            UserWithAttributes createdUser = this.directoryManager.addUser(jitDirectory.getId().longValue(), user, this.generatePassword());
            return new JitCrowdUser(userData.getIdentityProviderId(), (User)createdUser);
        }
        catch (InvalidCredentialException | InvalidUserException | OperationFailedException | UserAlreadyExistsException | DirectoryPermissionException e) {
            log.error("Provisioning user [{}] by JIT failed", (Object)userData.getUsername(), (Object)e);
            throw new JitException(e);
        }
        catch (DirectoryNotFoundException e) {
            log.error("JIT provisioning of user [{}] failed due to missing directory", (Object)userData.getUsername(), (Object)e);
            throw new ConcurrentModificationException(e);
        }
    }

    public JitCrowdUser updateUser(JitUserData userData, JitCrowdUser existingUser) {
        log.debug("Attempting to update JIT user [{}]", (Object)userData.getUsername());
        try {
            JitCrowdUser user = existingUser;
            if (!userData.getIdentityProviderId().equals(existingUser.getValue("jit_idp_id"))) {
                this.directoryManager.storeUserAttributes(user.getDirectoryId(), user.getName(), (Map)ImmutableMap.of((Object)"jit_idp_id", Collections.singleton(userData.getIdentityProviderId())));
            }
            if (!userData.getUsername().equals(user.getName())) {
                log.debug("Renaming JIT user [{}] to [{}]", (Object)user.getName(), (Object)userData.getUsername());
                user = this.directoryManager.renameUser(user.getDirectoryId(), user.getName(), userData.getUsername());
            }
            if (!userData.getDisplayName().equals(user.getDisplayName()) || !userData.getEmail().equals(user.getEmailAddress())) {
                UserTemplate updatedUserTemplate = new UserTemplate((User)user);
                updatedUserTemplate.setDisplayName(userData.getDisplayName());
                updatedUserTemplate.setEmailAddress(userData.getEmail());
                user = this.directoryManager.updateUser(user.getDirectoryId(), updatedUserTemplate);
            }
            return new JitCrowdUser(userData.getIdentityProviderId(), (User)user);
        }
        catch (InvalidUserException | OperationFailedException | UserAlreadyExistsException | UserNotFoundException | DirectoryPermissionException e) {
            log.error("Updating user [{}] by JIT failed", (Object)userData.getUsername());
            throw new JitException(e);
        }
        catch (DirectoryNotFoundException e) {
            log.error("The directory [{}] was not found when updating user [{}]", new Object[]{existingUser.getDirectoryId(), userData.getUsername(), e});
            throw new ConcurrentModificationException(e);
        }
    }

    private PasswordCredential generatePassword() {
        return new PasswordCredential(UUID.randomUUID() + "ABab23!");
    }
}

