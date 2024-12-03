/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.auth.AuthenticationController
 *  com.atlassian.seraph.auth.RoleMapper
 *  com.atlassian.seraph.config.SecurityConfigFactory
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.sal.core.auth;

import com.atlassian.sal.api.auth.AuthenticationController;
import com.atlassian.sal.core.util.Assert;
import com.atlassian.seraph.auth.RoleMapper;
import com.atlassian.seraph.config.SecurityConfigFactory;
import java.security.Principal;
import javax.servlet.http.HttpServletRequest;

public class SeraphAuthenticationController
implements AuthenticationController {
    private final RoleMapper roleMapper;

    public SeraphAuthenticationController() {
        RoleMapper roleMapper = SecurityConfigFactory.getInstance().getRoleMapper();
        this.roleMapper = Assert.notNull(roleMapper, "roleMapper");
    }

    public boolean canLogin(Principal principal, HttpServletRequest request) {
        return this.roleMapper.canLogin(principal, request);
    }

    public boolean shouldAttemptAuthentication(HttpServletRequest request) {
        return request.getAttribute("os_authstatus") == null;
    }
}

