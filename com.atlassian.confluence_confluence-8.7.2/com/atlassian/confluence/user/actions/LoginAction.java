/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.security.XsrfProtectionExcluded
 *  com.atlassian.seraph.auth.AuthenticationErrorType
 *  com.atlassian.seraph.config.SecurityConfigFactory
 *  com.atlassian.seraph.filter.LoginFilter
 *  com.atlassian.seraph.filter.LoginFilterRequest
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.validator.routines.UrlValidator
 *  org.apache.struts2.ServletActionContext
 *  org.apache.velocity.app.FieldMethodizer
 */
package com.atlassian.confluence.user.actions;

import com.atlassian.annotations.security.XsrfProtectionExcluded;
import com.atlassian.confluence.security.login.LoginManager;
import com.atlassian.confluence.user.actions.AbstractLoginSignupAction;
import com.atlassian.confluence.util.SeraphUtils;
import com.atlassian.confluence.web.context.HttpContext;
import com.atlassian.seraph.auth.AuthenticationErrorType;
import com.atlassian.seraph.config.SecurityConfigFactory;
import com.atlassian.seraph.filter.LoginFilter;
import com.atlassian.seraph.filter.LoginFilterRequest;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.apache.struts2.ServletActionContext;
import org.apache.velocity.app.FieldMethodizer;

public class LoginAction
extends AbstractLoginSignupAction {
    private static final UrlValidator urlValidator = new UrlValidator(new String[]{"http", "https"}, 8L);
    private LoginManager loginManager;
    private HttpContext httpContext;
    private String os_username;

    @Override
    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
    public String doDefault() throws Exception {
        return super.doDefault();
    }

    @Override
    public void validate() {
        HttpServletRequest req = this.httpContext.getRequest();
        if (req.getRemoteUser() != null) {
            String originalURL = SeraphUtils.getOriginalURL(req);
            if (originalURL != null) {
                if (this.isValidUrl(originalURL)) {
                    this.addActionError("login.not.permitted.description", originalURL);
                } else {
                    this.addActionError(this.getText("login.not.permitted.no-url-description"));
                }
            }
            return;
        }
        if (this.isElevatedSecurityCheckRequired()) {
            this.addActionError(this.getText("login.elevatedsecuritycheck.required"));
            if (null != req.getAttribute("ElevatedSecurityGuard_Failure")) {
                this.addFieldError("captcha", this.getText("login.elevatedsecuritycheck.required.captcharesponse.invalid"));
            }
            return;
        }
        String authStatus = LoginFilterRequest.getAuthenticationStatus((HttpServletRequest)req);
        if ("failed".equals(authStatus)) {
            this.addActionError(this.getText("wrong.password"));
        } else if ("error".equals(authStatus)) {
            AuthenticationErrorType errorType = LoginFilterRequest.getAuthenticationErrorType((HttpServletRequest)req);
            if (errorType == AuthenticationErrorType.CommunicationError) {
                this.addActionError(this.getText("comms.error.occurred"));
            } else {
                this.addActionError(this.getText("error.occurred"));
            }
        }
    }

    private boolean isValidUrl(String originalURL) {
        if (urlValidator.isValid(originalURL)) {
            return true;
        }
        String baseUrl = this.settingsManager.getGlobalSettings().getBaseUrl();
        return urlValidator.isValid(baseUrl + originalURL);
    }

    @XsrfProtectionExcluded
    public String execute() throws Exception {
        String refererURL;
        if (StringUtils.isBlank((CharSequence)this.os_destination) && StringUtils.isNotBlank((CharSequence)(refererURL = this.getRefererURL()))) {
            ServletActionContext.getContext().getSession().put(SecurityConfigFactory.getInstance().getOriginalURLKey(), refererURL);
        }
        return "success";
    }

    private String getRefererURL() {
        HttpServletRequest request = ServletActionContext.getRequest();
        String referer = request.getHeader("Referer");
        if (StringUtils.isBlank((CharSequence)referer)) {
            return null;
        }
        if (referer.contains("logout") || referer.contains("login")) {
            return null;
        }
        Object result = null;
        String baseUrl = this.settingsManager.getGlobalSettings().getBaseUrl();
        if (referer.startsWith(baseUrl) && !((String)(result = referer.substring(baseUrl.length()))).startsWith("/")) {
            result = "/" + (String)result;
        }
        return result;
    }

    @Override
    public boolean isPermitted() {
        return true;
    }

    public FieldMethodizer getLoginFilter() {
        return new FieldMethodizer((Object)new LoginFilter());
    }

    public String getOs_username() {
        return this.os_username;
    }

    public void setOs_username(String os_username) {
        this.os_username = os_username;
    }

    public boolean isShowForgottenPasswordHelp() {
        return this.upgradeManager.isUpgraded() && !this.settingsManager.getGlobalSettings().isExternalUserManagement();
    }

    public void setHttpContext(HttpContext httpContext) {
        this.httpContext = httpContext;
    }

    @Override
    public void setLoginManager(LoginManager loginManager) {
        this.loginManager = loginManager;
    }

    public boolean isElevatedSecurityCheckRequired() {
        return this.loginManager.requiresElevatedSecurityCheck(this.os_username);
    }
}

