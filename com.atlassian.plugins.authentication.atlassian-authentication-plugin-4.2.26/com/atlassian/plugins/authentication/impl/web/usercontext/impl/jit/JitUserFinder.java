/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.SearchRestriction
 *  com.atlassian.crowd.embedded.api.User
 *  com.atlassian.crowd.exception.DirectoryNotFoundException
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.exception.UserNotFoundException
 *  com.atlassian.crowd.manager.directory.DirectoryManager
 *  com.atlassian.crowd.model.user.User
 *  com.atlassian.crowd.search.EntityDescriptor
 *  com.atlassian.crowd.search.builder.QueryBuilder
 *  com.atlassian.crowd.search.builder.Restriction
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 *  com.atlassian.crowd.search.query.entity.restriction.Property
 *  com.atlassian.crowd.search.query.entity.restriction.PropertyUtils
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.google.common.base.Preconditions
 *  javax.inject.Inject
 *  javax.inject.Named
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.authentication.impl.web.usercontext.impl.jit;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.SearchRestriction;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.crowd.exception.DirectoryNotFoundException;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.exception.UserNotFoundException;
import com.atlassian.crowd.manager.directory.DirectoryManager;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.builder.QueryBuilder;
import com.atlassian.crowd.search.builder.Restriction;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import com.atlassian.crowd.search.query.entity.restriction.Property;
import com.atlassian.crowd.search.query.entity.restriction.PropertyUtils;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.authentication.impl.web.usercontext.impl.jit.JitCrowdUser;
import com.atlassian.plugins.authentication.impl.web.usercontext.impl.jit.mapping.JitUserData;
import com.google.common.base.Preconditions;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
public class JitUserFinder {
    private static final Logger log = LoggerFactory.getLogger(JitUserFinder.class);
    private final DirectoryManager directoryManager;

    @Inject
    public JitUserFinder(@ComponentImport DirectoryManager directoryManager) {
        this.directoryManager = directoryManager;
    }

    @VisibleForTesting
    public Optional<JitCrowdUser> findUserInternally(JitUserData jitUserData, List<Directory> activeInternalDirectories) {
        Preconditions.checkArgument((!activeInternalDirectories.isEmpty() ? 1 : 0) != 0, (Object)"There must be at least one active internal directory");
        Optional<JitCrowdUser> userByIdpId = this.getUserByIdentityProviderId(jitUserData.getIdentityProviderId(), activeInternalDirectories);
        if (userByIdpId.isPresent()) {
            return userByIdpId;
        }
        return this.findUserInInternalDirectoryByName(jitUserData, activeInternalDirectories);
    }

    private Optional<JitCrowdUser> findUserInInternalDirectoryByName(JitUserData jitUserData, List<Directory> activeInternalDirectories) {
        log.debug("Will search for user [{}] in directories [{}]", (Object)jitUserData.getUsername(), activeInternalDirectories);
        for (Directory directory : activeInternalDirectories) {
            try {
                com.atlassian.crowd.model.user.User result = this.directoryManager.findUserByName(directory.getId().longValue(), jitUserData.getUsername());
                return Optional.of(new JitCrowdUser(null, (User)result));
            }
            catch (DirectoryNotFoundException | OperationFailedException e) {
                log.error("Searching for user [{}] in directory [{}] failed", new Object[]{jitUserData.getUsername(), directory.getId(), e});
            }
            catch (UserNotFoundException e) {
                log.debug("User [{}] not found in directory [{}]", (Object)jitUserData.getUsername(), (Object)directory.getId());
            }
        }
        log.debug("User [{}] was not found in any active internal directory", (Object)jitUserData.getUsername());
        return Optional.empty();
    }

    private Optional<JitCrowdUser> getUserByIdentityProviderId(String identityProviderId, List<Directory> internalActiveDirectories) {
        EntityQuery<User> query = this.createQueryForUserByIdpId(identityProviderId);
        log.debug("Will search for user with IdP id [{}] in directories [{}]", (Object)identityProviderId, internalActiveDirectories);
        for (Directory directory : internalActiveDirectories) {
            try {
                List result = this.directoryManager.searchUsers(directory.getId().longValue(), query);
                if (!result.iterator().hasNext()) continue;
                return Optional.of(new JitCrowdUser(null, (User)result.iterator().next()));
            }
            catch (DirectoryNotFoundException | OperationFailedException e) {
                log.error("Searching for user by IdP id [{}] in directory [{}] failed", new Object[]{identityProviderId, directory.getId(), e});
            }
        }
        log.debug("User with IdP id [{}] was not found in any active internal directory", (Object)identityProviderId);
        return Optional.empty();
    }

    private EntityQuery<User> createQueryForUserByIdpId(String identityProviderId) {
        return QueryBuilder.queryFor(User.class, (EntityDescriptor)EntityDescriptor.user()).with((SearchRestriction)Restriction.on((Property)PropertyUtils.ofTypeString((String)"jit_idp_id")).exactlyMatching((Object)identityProviderId)).returningAtMost(1);
    }
}

