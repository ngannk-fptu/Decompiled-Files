/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.springframework.lang.Nullable
 */
package org.springframework.web.servlet.handler;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;

public class UserRoleAuthorizationInterceptor
implements HandlerInterceptor {
    @Nullable
    private String[] authorizedRoles;

    public final void setAuthorizedRoles(String ... authorizedRoles) {
        this.authorizedRoles = authorizedRoles;
    }

    @Override
    public final boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServletException, IOException {
        if (this.authorizedRoles != null) {
            for (String role : this.authorizedRoles) {
                if (!request.isUserInRole(role)) continue;
                return true;
            }
        }
        this.handleNotAuthorized(request, response, handler);
        return false;
    }

    protected void handleNotAuthorized(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServletException, IOException {
        response.sendError(403);
    }
}

