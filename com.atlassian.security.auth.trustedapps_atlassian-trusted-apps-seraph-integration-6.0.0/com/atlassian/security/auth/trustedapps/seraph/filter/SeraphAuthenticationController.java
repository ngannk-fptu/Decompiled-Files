/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.security.auth.trustedapps.filter.AuthenticationController
 *  com.atlassian.seraph.auth.RoleMapper
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.security.auth.trustedapps.seraph.filter;

import com.atlassian.security.auth.trustedapps.filter.AuthenticationController;
import com.atlassian.seraph.auth.RoleMapper;
import java.security.Principal;
import javax.servlet.http.HttpServletRequest;

public class SeraphAuthenticationController
implements AuthenticationController {
    private final RoleMapper roleMapper;

    public SeraphAuthenticationController(RoleMapper roleMapper) {
        if (roleMapper == null) {
            throw new IllegalArgumentException("roleMapper must not be null!");
        }
        this.roleMapper = roleMapper;
    }

    public boolean canLogin(Principal principal, HttpServletRequest request) {
        return this.roleMapper.canLogin(principal, request);
    }

    public boolean shouldAttemptAuthentication(HttpServletRequest request) {
        return request.getAttribute("os_authstatus") == null;
    }
}

