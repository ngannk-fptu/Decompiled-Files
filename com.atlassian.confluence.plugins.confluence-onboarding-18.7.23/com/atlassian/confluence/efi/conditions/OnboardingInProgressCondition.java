/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.web.context.HttpContext
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugin.webresource.QueryParams
 *  com.atlassian.plugin.webresource.condition.UrlReadingCondition
 *  com.atlassian.plugin.webresource.url.UrlBuilder
 *  javax.servlet.http.Cookie
 */
package com.atlassian.confluence.efi.conditions;

import com.atlassian.confluence.efi.OnboardingUtils;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.web.context.HttpContext;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.webresource.QueryParams;
import com.atlassian.plugin.webresource.condition.UrlReadingCondition;
import com.atlassian.plugin.webresource.url.UrlBuilder;
import java.util.Map;
import javax.servlet.http.Cookie;

public class OnboardingInProgressCondition
implements UrlReadingCondition {
    public static final String ONBOARDING_COOKIE_KEY = "load-tutorial-flow";
    public static final String ONBOARDING_COOKIE_LOAD_VALUE = "true";
    public static final String ONBOARDING_IN_PROGRESS_QUERY_PARAM = "onboarding-in-progress";
    private HttpContext httpContext;

    public OnboardingInProgressCondition(@ComponentImport HttpContext httpContext) {
        this.httpContext = httpContext;
    }

    public void init(Map<String, String> map) throws PluginParseException {
    }

    public void addToUrl(UrlBuilder urlBuilder) {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        if (user != null && this.isLoadTutorialCookieSet()) {
            urlBuilder.addToQueryString(ONBOARDING_IN_PROGRESS_QUERY_PARAM, String.valueOf(true));
        }
    }

    public boolean shouldDisplay(QueryParams params) {
        return Boolean.valueOf(params.get(ONBOARDING_IN_PROGRESS_QUERY_PARAM));
    }

    private boolean isLoadTutorialCookieSet() {
        Cookie[] cookies = this.httpContext.getRequest().getCookies();
        return OnboardingUtils.isCookieContains(cookies, ONBOARDING_COOKIE_KEY, ONBOARDING_COOKIE_LOAD_VALUE);
    }
}

