/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.seraph.auth.Authenticator
 *  com.atlassian.seraph.config.SecurityConfigFactory
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.struts2.interceptor.ServletRequestAware
 *  org.apache.struts2.interceptor.ServletResponseAware
 */
package com.atlassian.confluence.user.actions;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.user.actions.AuthenticationHelper;
import com.atlassian.seraph.auth.Authenticator;
import com.atlassian.seraph.config.SecurityConfigFactory;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.security.Principal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

public class LogoutAction
extends ConfluenceActionSupport
implements ServletRequestAware,
ServletResponseAware {
    private static final String DEFAULT_LOGOUT_URL = "/login.action?logout=true";
    private HttpServletRequest request;
    private HttpServletResponse response;
    private String logoutUrl;

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() throws Exception {
        Authenticator authenticator = SecurityConfigFactory.getInstance().getAuthenticator();
        Principal user = authenticator.getUser(this.request);
        boolean isLoggedOut = AuthenticationHelper.userLogout(user, this.request, this.response, this.eventManager, this);
        if (!isLoggedOut) {
            this.addActionError("unsuccessful.logout.message");
            return "error";
        }
        this.logoutUrl = this.getRedirectLogoutURL();
        return "success";
    }

    @Override
    public boolean isPermitted() {
        return true;
    }

    public void setServletRequest(HttpServletRequest request) {
        this.request = request;
    }

    public void setServletResponse(HttpServletResponse response) {
        this.response = response;
    }

    public String getLogoutUrl() {
        return this.logoutUrl;
    }

    private String getRedirectLogoutURL() {
        String logoutUrl = SecurityConfigFactory.getInstance().getLogoutURL();
        return StringUtils.isBlank((CharSequence)logoutUrl) ? DEFAULT_LOGOUT_URL : logoutUrl;
    }
}

