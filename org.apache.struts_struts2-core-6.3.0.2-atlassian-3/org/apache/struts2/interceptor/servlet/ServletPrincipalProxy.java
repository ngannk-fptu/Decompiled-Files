/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package org.apache.struts2.interceptor.servlet;

import java.security.Principal;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.interceptor.PrincipalProxy;

public class ServletPrincipalProxy
implements PrincipalProxy {
    private HttpServletRequest request;

    public ServletPrincipalProxy(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public boolean isUserInRole(String role) {
        return this.request.isUserInRole(role);
    }

    @Override
    public Principal getUserPrincipal() {
        return this.request.getUserPrincipal();
    }

    @Override
    public String getRemoteUser() {
        return this.request.getRemoteUser();
    }

    @Override
    public boolean isRequestSecure() {
        return this.request.isSecure();
    }
}

