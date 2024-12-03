/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.admin.criteria.WritableDirectoryExistsCriteria
 *  com.atlassian.confluence.security.CaptchaManager
 *  com.atlassian.confluence.security.login.LoginManager
 *  com.atlassian.confluence.user.SignupManager
 *  com.atlassian.confluence.util.UserChecker
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Iterables
 *  javax.servlet.FilterChain
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.plugins.authentication.impl.web.filter.authentication;

import com.atlassian.confluence.admin.criteria.WritableDirectoryExistsCriteria;
import com.atlassian.confluence.security.CaptchaManager;
import com.atlassian.confluence.security.login.LoginManager;
import com.atlassian.confluence.user.SignupManager;
import com.atlassian.confluence.util.UserChecker;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.authentication.api.config.IdpConfigService;
import com.atlassian.plugins.authentication.api.config.LoginOption;
import com.atlassian.plugins.authentication.api.config.LoginOptionsService;
import com.atlassian.plugins.authentication.api.config.SsoConfigService;
import com.atlassian.plugins.authentication.impl.johnson.JohnsonChecker;
import com.atlassian.plugins.authentication.impl.web.AuthenticationHandlerProvider;
import com.atlassian.plugins.authentication.impl.web.exception.UnsupportedHttpMethodException;
import com.atlassian.plugins.authentication.impl.web.filter.authentication.SeraphAuthenticationFilter;
import com.atlassian.plugins.authentication.impl.web.filter.authentication.confluence.ConfluenceActionResolver;
import com.atlassian.plugins.authentication.impl.web.filter.authentication.confluence.ConfluenceActionResolverFactory;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class ConfluenceAuthenticationFilter
extends SeraphAuthenticationFilter {
    private final SignupManager signupManager;
    private final UserChecker userChecker;
    private final WritableDirectoryExistsCriteria writableDirectoryExistsCriteria;
    private final ConfluenceActionResolver defaultActionResolver;
    private final ConfluenceActionResolver darkFeatureActionResolver;
    private final DarkFeatureManager darkFeatureManager;
    private final CaptchaManager captchaManager;
    private final LoginManager loginManager;
    private final SsoConfigService ssoConfigService;
    private static final String LOGIN_ACTION_CLASSNAME = "com.atlassian.confluence.user.actions.LoginAction";
    private static final String SIGNUP_ACTION_CLASSNAME = "com.atlassian.confluence.user.actions.SignUpAction";
    private static final Set<String> PUBLIC_AUTHENTICATION_ACTIONS = ImmutableSet.builder().add((Object)"com.atlassian.confluence.user.actions.LoginAction").add((Object)"com.atlassian.confluence.user.actions.SignUpAction").build();

    public ConfluenceAuthenticationFilter(AuthenticationHandlerProvider authenticationHandlerProvider, IdpConfigService idpConfigService, LoginOptionsService loginOptionsService, JohnsonChecker johnsonChecker, ConfluenceActionResolverFactory actionResolverFactory, SsoConfigService ssoConfigService, @ComponentImport SignupManager signupManager, @ComponentImport UserChecker userChecker, @ComponentImport WritableDirectoryExistsCriteria writableDirectoryExistsCriteria, @ComponentImport DarkFeatureManager darkFeatureManager, @ComponentImport CaptchaManager captchaManager, @ComponentImport LoginManager loginManager) {
        super(authenticationHandlerProvider, idpConfigService, loginOptionsService, johnsonChecker);
        this.defaultActionResolver = actionResolverFactory.createActionResolver();
        this.darkFeatureActionResolver = actionResolverFactory.createStaticActionResolver(LOGIN_ACTION_CLASSNAME, SIGNUP_ACTION_CLASSNAME);
        this.ssoConfigService = ssoConfigService;
        this.signupManager = signupManager;
        this.userChecker = userChecker;
        this.writableDirectoryExistsCriteria = writableDirectoryExistsCriteria;
        this.darkFeatureManager = darkFeatureManager;
        this.captchaManager = captchaManager;
        this.loginManager = loginManager;
    }

    @Override
    protected void doFilterInternal(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest)request;
        if (this.isRequestForPublicAuthenticationPage(httpRequest)) {
            super.doFilterInternal(request, response, chain);
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    protected boolean isProductSpecificSkip(List<LoginOption> loginOptions, HttpServletRequest request) {
        return this.isSignUpHidden(request) || this.isPostToNativeLoginPage(loginOptions, request) || this.isCaptchaSpecificSkip(loginOptions, request);
    }

    @Override
    protected boolean isSupportedHttpMethod(HttpServletRequest httpRequest) {
        String httpMethod = httpRequest.getMethod();
        if (httpMethod.equals("GET") || httpMethod.equals("HEAD")) {
            return true;
        }
        throw new UnsupportedHttpMethodException(httpMethod);
    }

    private boolean isSignUpHidden(HttpServletRequest request) {
        return this.isOnPublicSignupPage(request) && this.signupManager.isPublicSignupPermitted() && this.userChecker.isLicensedToAddMoreUsers() && this.writableDirectoryExistsCriteria.isMet();
    }

    private boolean isRequestForPublicAuthenticationPage(HttpServletRequest httpRequest) {
        return this.getActionResolver().getActionConfigClassName(httpRequest).filter(PUBLIC_AUTHENTICATION_ACTIONS::contains).isPresent();
    }

    private ConfluenceActionResolver getActionResolver() {
        boolean isDarkFeatureEnabled = this.darkFeatureManager.isEnabledForAllUsers(DarkFeature.FILTER_REQUEST_WITH_ACTION_CONFIG_DISABLED.getKey()).filter(Boolean.TRUE::equals).orElse(false);
        return isDarkFeatureEnabled ? this.darkFeatureActionResolver : this.defaultActionResolver;
    }

    private boolean isOnPublicSignupPage(HttpServletRequest request) {
        return request.getServletPath() != null && ("/signup.action".equals(request.getServletPath()) || "/dosignup.action".equals(request.getServletPath()));
    }

    private boolean isPostToNativeLoginPage(List<LoginOption> loginOptions, HttpServletRequest request) {
        return request.getMethod().equals("POST") && this.isNativeLoginTheOnlyAvailable(loginOptions);
    }

    private boolean isCaptchaSpecificSkip(List<LoginOption> loginOptions, HttpServletRequest request) {
        return request.getMethod().equals("POST") && (this.isNativeLoginPageAvailable(loginOptions) || this.isGlobalAuthenticationFallbackEnabled()) && this.isCaptchaRequired(request);
    }

    private boolean isCaptchaRequired(HttpServletRequest request) {
        return this.captchaManager.isCaptchaAvailable() && this.loginManager.requiresElevatedSecurityCheck(request.getParameter("os_username"));
    }

    private boolean isGlobalAuthenticationFallbackEnabled() {
        return this.ssoConfigService.getSsoConfig().enableAuthenticationFallback();
    }

    private boolean isNativeLoginTheOnlyAvailable(List<LoginOption> loginOptions) {
        return loginOptions.size() == 1 && ((LoginOption)Iterables.getOnlyElement(loginOptions)).getType().equals((Object)LoginOption.Type.LOGIN_FORM);
    }

    private boolean isNativeLoginPageAvailable(List<LoginOption> loginOptions) {
        return loginOptions.stream().anyMatch(loginOption -> loginOption.getType().equals((Object)LoginOption.Type.LOGIN_FORM));
    }

    public static enum DarkFeature {
        FILTER_REQUEST_WITH_ACTION_CONFIG_DISABLED("atlassian.authentication.sso.filter.request.action.configuration.disabled");

        private final String key;

        private DarkFeature(String key) {
            this.key = key;
        }

        public String getKey() {
            return this.key;
        }
    }
}

