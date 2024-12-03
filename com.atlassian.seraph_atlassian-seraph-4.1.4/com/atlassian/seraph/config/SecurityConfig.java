/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.seraph.config;

import com.atlassian.seraph.SecurityService;
import com.atlassian.seraph.auth.AuthenticationContext;
import com.atlassian.seraph.auth.Authenticator;
import com.atlassian.seraph.auth.RoleMapper;
import com.atlassian.seraph.config.RedirectPolicy;
import com.atlassian.seraph.controller.SecurityController;
import com.atlassian.seraph.elevatedsecurity.ElevatedSecurityGuard;
import com.atlassian.seraph.interceptor.Interceptor;
import com.atlassian.seraph.service.rememberme.RememberMeService;
import java.util.List;

public interface SecurityConfig {
    public static final String STORAGE_KEY = "seraph_config";
    public static final String BASIC_AUTH = "basic";

    public List<SecurityService> getServices();

    public String getLoginURL();

    public String getLoginURL(boolean var1, boolean var2);

    public String getLoginForwardPath();

    public String getLinkLoginURL();

    public String getLogoutURL();

    public String getOriginalURLKey();

    public List<String> getLoginSubmitURL();

    public Authenticator getAuthenticator();

    public AuthenticationContext getAuthenticationContext();

    public SecurityController getController();

    public RoleMapper getRoleMapper();

    public ElevatedSecurityGuard getElevatedSecurityGuard();

    public RememberMeService getRememberMeService();

    public RedirectPolicy getRedirectPolicy();

    public <T extends Interceptor> List<T> getInterceptors(Class<T> var1);

    public void destroy();

    public String getLoginCookiePath();

    public String getLoginCookieKey();

    public String getWebsudoRequestKey();

    public boolean isInsecureCookie();

    public int getAutoLoginCookieAge();

    public String getAuthType();

    public boolean isInvalidateSessionOnLogin();

    public boolean isInvalidateSessionOnWebsudo();

    public List<String> getInvalidateSessionExcludeList();

    public List<String> getInvalidateWebsudoSessionExcludeList();
}

