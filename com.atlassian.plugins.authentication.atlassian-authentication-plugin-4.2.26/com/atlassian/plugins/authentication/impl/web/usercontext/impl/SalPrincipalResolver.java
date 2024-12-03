/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.component.BambooComponent
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.auth.AuthenticationController
 *  com.atlassian.sal.api.user.UserManager
 *  javax.inject.Inject
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.plugins.authentication.impl.web.usercontext.impl;

import com.atlassian.plugin.spring.scanner.annotation.component.BambooComponent;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.authentication.impl.web.usercontext.PrincipalResolver;
import com.atlassian.sal.api.auth.AuthenticationController;
import com.atlassian.sal.api.user.UserManager;
import java.security.Principal;
import java.util.Optional;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

@BambooComponent
public class SalPrincipalResolver
implements PrincipalResolver {
    protected final UserManager userManager;
    private final AuthenticationController authenticationController;

    @Inject
    public SalPrincipalResolver(@ComponentImport UserManager userManager, @ComponentImport AuthenticationController authenticationController) {
        this.userManager = userManager;
        this.authenticationController = authenticationController;
    }

    @Override
    public Optional<Principal> resolvePrincipal(String username, HttpServletRequest request) {
        Principal resolvedUser = this.userManager.resolve(username);
        return Optional.ofNullable(resolvedUser);
    }

    @Override
    public boolean isAllowedToAuthenticate(Principal principal, HttpServletRequest request) {
        return this.isAllowedToLogin(request, principal);
    }

    protected boolean isAllowedToLogin(HttpServletRequest request, Principal resolvedUser) {
        return this.authenticationController.canLogin(resolvedUser, request);
    }
}

