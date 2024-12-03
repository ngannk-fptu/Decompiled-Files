/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.CrowdService
 *  com.atlassian.crowd.exception.InactiveAccountException
 *  com.atlassian.crowd.exception.runtime.OperationFailedException
 *  com.atlassian.crowd.exception.runtime.UserNotFoundException
 *  com.atlassian.plugin.spring.scanner.annotation.component.BitbucketComponent
 *  com.atlassian.plugin.spring.scanner.annotation.component.ConfluenceComponent
 *  com.atlassian.plugin.spring.scanner.annotation.component.FecruComponent
 *  com.atlassian.plugin.spring.scanner.annotation.component.JiraComponent
 *  com.atlassian.plugin.spring.scanner.annotation.component.StashComponent
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.auth.AuthenticationController
 *  com.atlassian.sal.api.user.UserManager
 *  javax.inject.Inject
 *  javax.servlet.http.HttpServletRequest
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.authentication.impl.web.usercontext.impl.embeddedcrowd;

import com.atlassian.crowd.embedded.api.CrowdService;
import com.atlassian.crowd.exception.InactiveAccountException;
import com.atlassian.crowd.exception.runtime.OperationFailedException;
import com.atlassian.crowd.exception.runtime.UserNotFoundException;
import com.atlassian.plugin.spring.scanner.annotation.component.BitbucketComponent;
import com.atlassian.plugin.spring.scanner.annotation.component.ConfluenceComponent;
import com.atlassian.plugin.spring.scanner.annotation.component.FecruComponent;
import com.atlassian.plugin.spring.scanner.annotation.component.JiraComponent;
import com.atlassian.plugin.spring.scanner.annotation.component.StashComponent;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.authentication.impl.web.usercontext.AuthenticationFailedException;
import com.atlassian.plugins.authentication.impl.web.usercontext.impl.SalPrincipalResolver;
import com.atlassian.sal.api.auth.AuthenticationController;
import com.atlassian.sal.api.user.UserManager;
import java.security.Principal;
import java.util.Optional;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@JiraComponent
@BitbucketComponent
@StashComponent
@ConfluenceComponent
@FecruComponent
public class EmbeddedCrowdPrincipalResolver
extends SalPrincipalResolver {
    private static final Logger log = LoggerFactory.getLogger(EmbeddedCrowdPrincipalResolver.class);
    private final CrowdService crowdService;

    @Inject
    public EmbeddedCrowdPrincipalResolver(@ComponentImport UserManager userManager, @ComponentImport AuthenticationController authenticationController, @ComponentImport CrowdService crowdService) {
        super(userManager, authenticationController);
        this.crowdService = crowdService;
    }

    @Override
    public Optional<Principal> resolvePrincipal(String username, HttpServletRequest request) {
        try {
            this.crowdService.userAuthenticated(username);
            return Optional.ofNullable(this.userManager.resolve(username));
        }
        catch (OperationFailedException e) {
            throw new AuthenticationFailedException("Error authenticating user", e);
        }
        catch (InactiveAccountException | UserNotFoundException e) {
            log.debug("Exception caught when looking for user, treating user as not found", e);
            return Optional.empty();
        }
    }
}

