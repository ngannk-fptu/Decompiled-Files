/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.cluster.ZduManager
 *  com.atlassian.confluence.cluster.ZduStatus$State
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.status.service.SystemInformationService
 *  com.atlassian.confluence.status.service.systeminfo.ConfluenceInfo
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.core.filters.AbstractHttpFilter
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.atlassian.user.User
 *  com.google.common.annotations.VisibleForTesting
 *  javax.servlet.FilterChain
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.confluence.plugins.pulp;

import com.atlassian.confluence.cluster.ZduManager;
import com.atlassian.confluence.cluster.ZduStatus;
import com.atlassian.confluence.plugins.pulp.VersionManager;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.status.service.SystemInformationService;
import com.atlassian.confluence.status.service.systeminfo.ConfluenceInfo;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.core.filters.AbstractHttpFilter;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.user.User;
import com.google.common.annotations.VisibleForTesting;
import java.io.IOException;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PulpFilter
extends AbstractHttpFilter {
    @VisibleForTesting
    static final String PULP_URL = "/pulp/pulp-render.action";
    @VisibleForTesting
    static final int REDIRECT_LIMIT = 10;
    @VisibleForTesting
    static final long PULP_TIME_LIMIT_IN_DAYS = 14L;
    public static final String REDIRECTION_FLAG = "redirected-to-pulp";
    private final ConfluenceInfo confluenceInfo;
    private final PermissionManager permissionManager;
    private final DarkFeatureManager darkFeatureManager;
    private final VersionManager versionManager;
    private final ZduManager zduManager;

    PulpFilter(@ComponentImport SystemInformationService systemInformationService, @ComponentImport PermissionManager permissionManager, @ComponentImport DarkFeatureManager darkFeatureManager, @ComponentImport ZduManager zduManager, VersionManager versionManager) {
        this.confluenceInfo = systemInformationService.getConfluenceInfo();
        this.permissionManager = permissionManager;
        this.darkFeatureManager = darkFeatureManager;
        this.versionManager = versionManager;
        this.zduManager = zduManager;
    }

    protected void doFilter(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (this.shouldRedirect(httpServletRequest)) {
            this.versionManager.addRedirectForUser();
            httpServletRequest.getSession().setAttribute(REDIRECTION_FLAG, (Object)true);
            httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + PULP_URL);
            return;
        }
        filterChain.doFilter((ServletRequest)httpServletRequest, (ServletResponse)httpServletResponse);
    }

    private boolean shouldRedirect(HttpServletRequest httpServletRequest) {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        return this.isLegitimateRequest(httpServletRequest) && this.isEnabled() && this.isAdministrator(user) && !this.confluenceInfo.isDevMode() && this.lastUpgradeIsWithinTimeLimit() && !this.isRedirectLimitReached() && !this.versionManager.isFreshInstall() && !this.versionManager.hasBeenRedirectedForThisVersionOfConfluence() && !this.clusterIsInUpgradeMode();
    }

    private boolean isLegitimateRequest(HttpServletRequest httpServletRequest) {
        return !httpServletRequest.getServletPath().equals("/rest");
    }

    private boolean isEnabled() {
        return this.darkFeatureManager.isEnabledForCurrentUser("pulp").orElse(false);
    }

    private boolean isAdministrator(ConfluenceUser user) {
        return this.permissionManager.isConfluenceAdministrator((User)user) || this.permissionManager.isSystemAdministrator((User)user);
    }

    private boolean isRedirectLimitReached() {
        return this.versionManager.getTotalRedirects() >= 10;
    }

    private boolean clusterIsInUpgradeMode() {
        return this.zduManager.getUpgradeStatus().getState().equals((Object)ZduStatus.State.ENABLED);
    }

    private boolean lastUpgradeIsWithinTimeLimit() {
        Optional<Date> upgradeDate = this.versionManager.getUpgradeDate();
        if (upgradeDate.isPresent()) {
            long pulpExpiryTime = upgradeDate.get().getTime() + TimeUnit.DAYS.toMillis(14L);
            return System.currentTimeMillis() < pulpExpiryTime;
        }
        return false;
    }
}

