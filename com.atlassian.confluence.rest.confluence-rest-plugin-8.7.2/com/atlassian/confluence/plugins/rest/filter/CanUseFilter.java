/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.core.filters.AbstractHttpFilter
 *  com.atlassian.user.User
 *  javax.servlet.FilterChain
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.confluence.plugins.rest.filter;

import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.core.filters.AbstractHttpFilter;
import com.atlassian.user.User;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CanUseFilter
extends AbstractHttpFilter {
    private final PermissionManager permissionManager;

    public CanUseFilter(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        ConfluenceUser remoteUser = AuthenticatedUserThreadLocal.get();
        if (!this.canUseConfluenceCheck((User)remoteUser)) {
            if (remoteUser == null) {
                response.sendError(401);
            } else {
                response.sendError(403);
            }
        } else {
            filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
        }
    }

    protected boolean canUseConfluenceCheck(User remoteUser) {
        return this.permissionManager.hasPermission(remoteUser, Permission.VIEW, PermissionManager.TARGET_APPLICATION);
    }
}

