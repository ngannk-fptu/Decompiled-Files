/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.confluence.web.UrlBuilder
 *  com.atlassian.confluence.web.context.HttpContext
 *  com.atlassian.user.User
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.confluence.plugins.macros.dashboard.recentupdates.tabs;

import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.plugins.macros.dashboard.recentupdates.tabs.RecentlyUpdatedMacroTab;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.confluence.web.UrlBuilder;
import com.atlassian.confluence.web.context.HttpContext;
import com.atlassian.user.User;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

public abstract class AbstractTab
implements RecentlyUpdatedMacroTab {
    protected final HttpContext httpContext;
    protected final I18NBeanFactory i18NBeanFactory;
    private final LocaleManager localeManager;
    private static final String TAB_PARAM_KEY = "updatesSelectedTab";

    protected AbstractTab(HttpContext httpContext, I18NBeanFactory i18NBeanFactory, LocaleManager localeManager) {
        this.httpContext = httpContext;
        this.i18NBeanFactory = i18NBeanFactory;
        this.localeManager = localeManager;
    }

    @Override
    public String getDisplayName() {
        return this.i18NBeanFactory.getI18NBean(this.localeManager.getLocale(this.getUser())).getText("updates.tab." + this.getName());
    }

    @Override
    public String getNoContentMessage() {
        return this.i18NBeanFactory.getI18NBean(this.localeManager.getLocale(this.getUser())).getText("confluence.macros.dashboard." + this.getName() + ".no.content");
    }

    @Override
    public String getUrl() {
        HttpServletRequest request = this.httpContext.getRequest();
        String pageUrl = request.getRequestURL().toString();
        UrlBuilder urlBuilder = new UrlBuilder(pageUrl);
        Map<String, String[]> params = AbstractTab.castHttpParams(request.getParameterMap());
        for (Map.Entry<String, String[]> param : params.entrySet()) {
            if (param.getKey().equals(TAB_PARAM_KEY)) continue;
            urlBuilder.add(param.getKey(), param.getValue()[0]);
        }
        urlBuilder.add(TAB_PARAM_KEY, this.getName());
        return urlBuilder.toString();
    }

    private static Map<String, String[]> castHttpParams(Map params) {
        return params;
    }

    protected User getUser() {
        return AuthenticatedUserThreadLocal.get();
    }
}

