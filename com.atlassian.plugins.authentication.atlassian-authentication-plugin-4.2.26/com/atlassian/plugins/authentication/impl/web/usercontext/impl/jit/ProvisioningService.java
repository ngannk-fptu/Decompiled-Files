/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.OperationType
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.google.common.collect.ImmutableSet
 *  javax.inject.Inject
 *  javax.inject.Named
 *  javax.servlet.http.HttpServletRequest
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.authentication.impl.web.usercontext.impl.jit;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.OperationType;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.authentication.impl.web.usercontext.PrincipalResolver;
import com.atlassian.plugins.authentication.impl.web.usercontext.impl.jit.GroupProvisioningService;
import com.atlassian.plugins.authentication.impl.web.usercontext.impl.jit.JitCrowdUser;
import com.atlassian.plugins.authentication.impl.web.usercontext.impl.jit.JitDirectoriesFinder;
import com.atlassian.plugins.authentication.impl.web.usercontext.impl.jit.JitException;
import com.atlassian.plugins.authentication.impl.web.usercontext.impl.jit.JitUserFinder;
import com.atlassian.plugins.authentication.impl.web.usercontext.impl.jit.UserProvisionedEvent;
import com.atlassian.plugins.authentication.impl.web.usercontext.impl.jit.UserProvisioningService;
import com.atlassian.plugins.authentication.impl.web.usercontext.impl.jit.mapping.JitUserData;
import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
public class ProvisioningService {
    private static final ImmutableSet<OperationType> REQUIRED_PERMISSIONS = ImmutableSet.of((Object)OperationType.CREATE_USER, (Object)OperationType.CREATE_GROUP);
    private static final Logger log = LoggerFactory.getLogger(ProvisioningService.class);
    private final PrincipalResolver principalResolver;
    private final EventPublisher eventPublisher;
    private final JitDirectoriesFinder jitDirectoriesFinder;
    private final JitUserFinder jitUserFinder;
    private final UserProvisioningService userProvisioningService;
    private final GroupProvisioningService groupProvisioningService;

    @Inject
    public ProvisioningService(PrincipalResolver principalResolver, @ComponentImport EventPublisher eventPublisher, JitDirectoriesFinder jitDirectoriesFinder, JitUserFinder jitUserFinder, UserProvisioningService userProvisioningService, GroupProvisioningService groupProvisioningService) {
        this.principalResolver = principalResolver;
        this.eventPublisher = eventPublisher;
        this.jitDirectoriesFinder = jitDirectoriesFinder;
        this.jitUserFinder = jitUserFinder;
        this.userProvisioningService = userProvisioningService;
        this.groupProvisioningService = groupProvisioningService;
    }

    public void handleJustInTimeProvisioning(JitUserData jitUserData, HttpServletRequest request) {
        List<Directory> activeInternalDirectories = this.jitDirectoriesFinder.findAllActiveInternalDirectories();
        Optional<JitCrowdUser> internalUser = this.jitUserFinder.findUserInternally(jitUserData, activeInternalDirectories);
        Directory jitDirectory = this.findJitDirectory(activeInternalDirectories);
        if (!internalUser.isPresent() && !this.principalResolver.resolvePrincipal(jitUserData.getUsername(), request).isPresent()) {
            log.debug("User {} not found in the application, provisioning the user", (Object)jitUserData.getUsername());
            internalUser = Optional.of(this.userProvisioningService.provisionUser(jitUserData, jitDirectory));
            internalUser.ifPresent(user -> this.eventPublisher.publish((Object)new UserProvisionedEvent()));
        }
        if (internalUser.isPresent()) {
            log.debug("User {} already exists in the application, updating user details", (Object)jitUserData.getUsername());
            JitCrowdUser updatedUser = this.userProvisioningService.updateUser(jitUserData, internalUser.get());
            this.groupProvisioningService.updateUserGroups(updatedUser, jitUserData.getGroups(), jitDirectory);
        }
    }

    private Directory findJitDirectory(List<Directory> activeInternalDirectories) {
        return activeInternalDirectories.stream().filter(d -> d.getAllowedOperations().containsAll((Collection<?>)REQUIRED_PERMISSIONS)).findFirst().orElseThrow(() -> new JitException(String.format("JIT provisioning of group failed as there is no active internal directory with %s permissions", REQUIRED_PERMISSIONS)));
    }

    public static interface DarkFeature {
        public static final String DISABLE_LICENSE_CHECK = "atlassian.authentication.sso.jit.disable.license.check";
    }
}

