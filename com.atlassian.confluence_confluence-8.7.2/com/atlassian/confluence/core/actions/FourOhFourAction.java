/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.security.XsrfProtectionExcluded
 *  com.atlassian.johnson.Johnson
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.struts2.ServletActionContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.core.actions;

import com.atlassian.annotations.security.XsrfProtectionExcluded;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.core.actions.LoginExemptionHelper;
import com.atlassian.confluence.themes.Theme;
import com.atlassian.confluence.themes.ThemeManager;
import com.atlassian.confluence.user.actions.AuthenticationHelper;
import com.atlassian.confluence.util.SeraphUtils;
import com.atlassian.johnson.Johnson;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FourOhFourAction
extends ConfluenceActionSupport {
    private static final Logger log = LoggerFactory.getLogger(FourOhFourAction.class);
    private ThemeManager themeManager;
    private LoginExemptionHelper loginExemptionHelper;

    @PermittedMethods(value={HttpMethod.ANY_METHOD})
    @XsrfProtectionExcluded
    public String execute() throws Exception {
        ServletActionContext.getResponse().setStatus(404);
        if (!this.isSetupComplete()) {
            return "setup-success";
        }
        String originalUrlPathFromErrorUri = this.getOriginalUrlPathFromErrorUri();
        if (!(!Objects.isNull(this.getAuthenticatedUser()) || this.canAnonymousUseConfluence() || this.isStaticResource(originalUrlPathFromErrorUri) || Johnson.getConfig().isIgnoredPath(originalUrlPathFromErrorUri) || this.loginExemptionHelper.isUrlPathExempted(originalUrlPathFromErrorUri))) {
            return "login";
        }
        return super.execute();
    }

    @Override
    public boolean isPermitted() {
        return true;
    }

    public Theme getTheme() {
        return this.themeManager.getGlobalTheme();
    }

    public String getLoginUrl() {
        String originalUrl = StringUtils.defaultString((String)((String)ServletActionContext.getRequest().getAttribute("atlassian.core.seraph.original.url")));
        if (originalUrl.isEmpty() || originalUrl.equals("/fourohfour.action")) {
            this.configureSeraphWithErrorRequestUrlAsOriginalUrl();
        }
        return AuthenticationHelper.getLoginUrl();
    }

    public void setThemeManager(ThemeManager themeManager) {
        this.themeManager = themeManager;
    }

    public void setLoginExemptionHelper(LoginExemptionHelper loginExemptionHelper) {
        this.loginExemptionHelper = loginExemptionHelper;
    }

    private boolean isSetupComplete() {
        return ContainerManager.isContainerSetup() && this.getBootstrapStatusProvider().isSetupComplete();
    }

    private void configureSeraphWithErrorRequestUrlAsOriginalUrl() {
        String pathFromOriginalUrl = this.getOriginalUrlPathFromErrorUri();
        ServletActionContext.getRequest().setAttribute("atlassian.core.seraph.original.url", (Object)pathFromOriginalUrl);
        log.debug("No {} was found in the request. Storing path: {}", (Object)"atlassian.core.seraph.original.url", (Object)pathFromOriginalUrl);
    }

    private String getOriginalUrlPathFromErrorUri() {
        String originalURL = (String)ServletActionContext.getRequest().getAttribute("javax.servlet.error.request_uri");
        return SeraphUtils.stripContextPathFromRequestURL(ServletActionContext.getRequest(), StringUtils.defaultString((String)originalURL));
    }

    private boolean isStaticResource(String pathFromOriginalUrl) {
        return pathFromOriginalUrl.startsWith("/s/");
    }

    private boolean canAnonymousUseConfluence() {
        return this.getConfluenceAccessManager().getUserAccessStatusNoExemptions(null).canUseConfluence();
    }
}

