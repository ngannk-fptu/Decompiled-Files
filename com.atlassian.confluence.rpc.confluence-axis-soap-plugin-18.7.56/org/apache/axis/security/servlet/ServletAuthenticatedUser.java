/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package org.apache.axis.security.servlet;

import java.security.Principal;
import javax.servlet.http.HttpServletRequest;
import org.apache.axis.security.AuthenticatedUser;

public class ServletAuthenticatedUser
implements AuthenticatedUser {
    private String name;
    private HttpServletRequest req;

    public ServletAuthenticatedUser(HttpServletRequest req) {
        this.req = req;
        Principal principal = req.getUserPrincipal();
        this.name = principal == null ? null : principal.getName();
    }

    public String getName() {
        return this.name;
    }

    public HttpServletRequest getRequest() {
        return this.req;
    }
}

