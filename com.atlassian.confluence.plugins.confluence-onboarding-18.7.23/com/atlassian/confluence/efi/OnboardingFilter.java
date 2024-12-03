/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.accessmode.AccessModeService
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.security.access.AccessStatus
 *  com.atlassian.confluence.security.access.ConfluenceAccessManager
 *  com.atlassian.confluence.setup.settings.CoreFeaturesManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.PersonalInformation
 *  com.atlassian.confluence.user.PersonalInformationManager
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.atlassian.core.filters.AbstractHttpFilter
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.atlassian.user.User
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 *  javax.servlet.FilterChain
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.Cookie
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringUtils
 *  org.joda.time.DateTime
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.efi;

import com.atlassian.confluence.api.service.accessmode.AccessModeService;
import com.atlassian.confluence.efi.OnboardingManager;
import com.atlassian.confluence.efi.OnboardingUtils;
import com.atlassian.confluence.efi.store.UserStorageService;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.access.AccessStatus;
import com.atlassian.confluence.security.access.ConfluenceAccessManager;
import com.atlassian.confluence.setup.settings.CoreFeaturesManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.PersonalInformation;
import com.atlassian.confluence.user.PersonalInformationManager;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.core.filters.AbstractHttpFilter;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.user.User;
import java.io.IOException;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public class OnboardingFilter
extends AbstractHttpFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(OnboardingFilter.class);
    private static final String ONBOARDING_STATE = "onboarding-state:";
    private static final String INTRO_WORKFLOW_KEY = "introWorkflow";
    private static final String INTRO_WORKFLOW_VALUE_COMPLETE = "__complete__";
    private static final String COOKIE_NAME_IMPERSONATED_USERNAME = "um.user.impersonated.username";
    private static final String FILTER_BYPASS_FEATURE = "confluence.onboarding.bypass";
    private UserStorageService userStorageService;
    private OnboardingManager onboardingManager;
    private PermissionManager permissionManager;
    private CoreFeaturesManager coreFeaturesManager;
    private PersonalInformationManager personalInformationManager;
    private DarkFeatureManager darkFeatureManager;
    private ConfluenceAccessManager confluenceAccessManager;
    private AccessModeService accessModeService;

    public OnboardingFilter(UserStorageService userStorageService, OnboardingManager onboardingManager, @ComponentImport PermissionManager permissionManager, @ComponentImport CoreFeaturesManager coreFeaturesManager, @ComponentImport PersonalInformationManager personalInformationManager, @ComponentImport DarkFeatureManager darkFeatureManager, @ComponentImport ConfluenceAccessManager confluenceAccessManager, @ComponentImport AccessModeService accessModeService) {
        this.userStorageService = userStorageService;
        this.onboardingManager = onboardingManager;
        this.permissionManager = permissionManager;
        this.coreFeaturesManager = coreFeaturesManager;
        this.personalInformationManager = personalInformationManager;
        this.darkFeatureManager = darkFeatureManager;
        this.confluenceAccessManager = confluenceAccessManager;
        this.accessModeService = accessModeService;
    }

    protected void doFilter(HttpServletRequest servletRequest, HttpServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String contextPath = servletRequest.getContextPath();
        if (!this.isRootOrIndexPath(servletRequest, contextPath) && !this.isWelcomePath(servletRequest) && !this.isDashboardPath(servletRequest) || this.bypassFilter()) {
            filterChain.doFilter((ServletRequest)servletRequest, (ServletResponse)servletResponse);
            return;
        }
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        if (this.isWelcomePath(servletRequest)) {
            if (!this.isLicensedForOnboarding(user) || this.hasUserCompletedOnboarding(user) || this.accessModeService.isReadOnlyAccessModeEnabled()) {
                servletResponse.sendRedirect(servletRequest.getContextPath() + "/dashboard.action");
                return;
            }
        } else if (this.isDashboardPath(servletRequest) || this.isRootOrIndexPath(servletRequest, contextPath)) {
            String onboardingParam = servletRequest.getParameter("onboarding");
            if (user != null && StringUtils.equalsIgnoreCase((CharSequence)"clear-state", (CharSequence)onboardingParam)) {
                LOGGER.debug("[Confluence Onboarding] [User = {}] [Request Parameter 'onboarding=clear-state'] Removing data & redirecting to dashboard", (Object)AuthenticatedUserThreadLocal.getUsername());
                this.userStorageService.remove("onboarding-state:introWorkflow", user);
                this.userStorageService.remove("onboarding-state:tutorialFlow", user);
                servletResponse.sendRedirect(servletRequest.getContextPath() + "/dashboard.action");
                return;
            }
            if (!this.accessModeService.isReadOnlyAccessModeEnabled() && !OnboardingFilter.isRunningWebDriver(servletRequest.getCookies()) && !OnboardingFilter.isImpersonatingUser(servletRequest.getCookies()) && this.checkIfNeedToReredirect(servletRequest)) {
                if (StringUtils.equalsIgnoreCase((CharSequence)"evaluator", (CharSequence)onboardingParam)) {
                    servletResponse.sendRedirect(servletRequest.getContextPath() + "/welcome.action?evaluator=true");
                } else {
                    servletResponse.sendRedirect(servletRequest.getContextPath() + "/welcome.action");
                }
                return;
            }
        }
        filterChain.doFilter((ServletRequest)servletRequest, (ServletResponse)servletResponse);
    }

    protected boolean checkIfNeedToReredirect(HttpServletRequest req) {
        boolean isRedirect;
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        if (!this.isLicensedForOnboarding(user)) {
            return false;
        }
        if (!this.permissionManager.isConfluenceAdministrator((User)user)) {
            return false;
        }
        String onboarding = req.getParameter("onboarding");
        if (onboarding != null && !onboarding.equals("false")) {
            if (!onboarding.equals("evaluator")) {
                this.userStorageService.remove("onboarding-state:introWorkflow", user);
            }
            LOGGER.debug("[Confluence Onboarding] [User = {}] [Request Parameter 'onboarding={}']", (Object)AuthenticatedUserThreadLocal.getUsername(), (Object)onboarding);
            return true;
        }
        if (this.isCloudSysAdmin(user)) {
            return false;
        }
        long userCreatedDateInMillis = this.getUserCreatedDateInMillis();
        long pluginInstalledDateInMillis = this.onboardingManager.getPluginInstalledDateInMillis();
        String sequenceKey = this.userStorageService.get("onboarding-state:introWorkflow", user);
        boolean bl = isRedirect = userCreatedDateInMillis > pluginInstalledDateInMillis && !this.hasUserCompletedOnboarding(user);
        if (isRedirect) {
            LOGGER.debug("[Confluence Onboarding] [User = {}] [userCreatedDateInMillis = {}] [pluginInstalledDateInMillis = {}] [sequenceKey = {}] ", new Object[]{AuthenticatedUserThreadLocal.getUsername(), userCreatedDateInMillis, pluginInstalledDateInMillis, sequenceKey});
        }
        return isRedirect;
    }

    private boolean hasUserCompletedOnboarding(@Nullable ConfluenceUser user) {
        if (user == null) {
            return true;
        }
        String hasUserCompletedOnboarding = this.userStorageService.get("onboarding-state:introWorkflow", user);
        return StringUtils.equalsIgnoreCase((CharSequence)INTRO_WORKFLOW_VALUE_COMPLETE, (CharSequence)hasUserCompletedOnboarding);
    }

    private boolean isLicensedForOnboarding(@Nullable ConfluenceUser user) {
        if (user == null || GeneralUtil.isLicenseExpired()) {
            return false;
        }
        AccessStatus accessStatus = this.confluenceAccessManager.getUserAccessStatus((User)user);
        return accessStatus.hasLicensedAccess();
    }

    private long getUserCreatedDateInMillis() {
        long DEFAULT_IN_MILLIS = Long.MIN_VALUE;
        String username = AuthenticatedUserThreadLocal.getUsername();
        if (username == null) {
            return Long.MIN_VALUE;
        }
        PersonalInformation personalInformation = this.personalInformationManager.getOrCreatePersonalInformation((User)AuthenticatedUserThreadLocal.get());
        return new DateTime((Object)personalInformation.getCreationDate()).plusHours(1).getMillis();
    }

    private boolean isCloudSysAdmin(ConfluenceUser user) {
        return this.coreFeaturesManager.isOnDemand() && this.permissionManager.isSystemAdministrator((User)user);
    }

    private static boolean isImpersonatingUser(Cookie[] cookies) {
        return OnboardingUtils.isCookieContains(cookies, COOKIE_NAME_IMPERSONATED_USERNAME, null);
    }

    private static boolean isRunningWebDriver(@Nullable Cookie[] cookies) {
        return OnboardingUtils.isCookieContains(cookies, "webdriver", "true");
    }

    private boolean isWelcomePath(HttpServletRequest servletRequest) {
        return this.isPathContain(servletRequest, "/welcome.action");
    }

    private boolean isDashboardPath(HttpServletRequest servletRequest) {
        return this.isPathContain(servletRequest, "/dashboard.action");
    }

    private boolean isPathContain(HttpServletRequest servletRequest, String s) {
        return servletRequest.getRequestURI().contains(s);
    }

    private boolean isRootOrIndexPath(HttpServletRequest servletRequest, String contextPath) {
        return servletRequest.getRequestURI().equals(contextPath) || servletRequest.getRequestURI().equals(contextPath + "/") || servletRequest.getRequestURI().equals(contextPath + "/index.action");
    }

    private boolean bypassFilter() {
        return this.darkFeatureManager.isFeatureEnabledForCurrentUser(FILTER_BYPASS_FEATURE);
    }
}

