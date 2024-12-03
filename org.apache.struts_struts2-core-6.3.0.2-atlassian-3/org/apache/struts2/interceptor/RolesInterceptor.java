/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ServletActionContext;

public class RolesInterceptor
extends AbstractInterceptor {
    private static final Logger LOG = LogManager.getLogger(RolesInterceptor.class);
    private boolean isProperlyConfigured = true;
    protected List<String> allowedRoles = Collections.emptyList();
    protected List<String> disallowedRoles = Collections.emptyList();

    public void setAllowedRoles(String roles) {
        this.allowedRoles = this.stringToList(roles);
        this.checkRoles(this.allowedRoles);
    }

    public void setDisallowedRoles(String roles) {
        this.disallowedRoles = this.stringToList(roles);
        this.checkRoles(this.disallowedRoles);
    }

    private void checkRoles(List<String> roles) {
        if (!this.areRolesValid(roles)) {
            LOG.fatal("An unknown Role was configured: {}", roles);
            this.isProperlyConfigured = false;
            throw new IllegalArgumentException("An unknown role was configured: " + roles);
        }
    }

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        if (!this.isProperlyConfigured) {
            throw new IllegalArgumentException("RolesInterceptor is misconfigured, check logs for erroneous configuration!");
        }
        if (!this.isAllowed(request, invocation.getAction())) {
            LOG.debug("Request is NOT allowed. Rejecting.");
            return this.handleRejection(invocation, response);
        }
        LOG.debug("Request is allowed. Invoking.");
        return invocation.invoke();
    }

    protected List<String> stringToList(String val) {
        if (val != null) {
            String[] list = val.split("[ ]*,[ ]*");
            return Arrays.asList(list);
        }
        return Collections.emptyList();
    }

    protected boolean isAllowed(HttpServletRequest request, Object action) {
        for (String role : this.disallowedRoles) {
            if (!request.isUserInRole(role)) continue;
            LOG.debug("User role '{}' is in the disallowedRoles list.", (Object)role);
            return false;
        }
        if (this.allowedRoles.isEmpty()) {
            LOG.debug("The allowedRoles list is empty.");
            return true;
        }
        for (String role : this.allowedRoles) {
            if (!request.isUserInRole(role)) continue;
            LOG.debug("User role '{}' is in the allowedRoles list.", (Object)role);
            return true;
        }
        return false;
    }

    protected String handleRejection(ActionInvocation invocation, HttpServletResponse response) throws Exception {
        response.sendError(403);
        return null;
    }

    protected boolean areRolesValid(List<String> roles) {
        return true;
    }
}

