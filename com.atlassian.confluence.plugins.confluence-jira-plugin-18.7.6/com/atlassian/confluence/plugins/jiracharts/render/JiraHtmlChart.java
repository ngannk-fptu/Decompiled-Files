/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLinkRequest
 *  com.atlassian.applinks.api.ApplicationLinkRequestFactory
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.atlassian.applinks.api.ReadOnlyApplicationLinkService
 *  com.atlassian.applinks.api.auth.Anonymous
 *  com.atlassian.confluence.macro.MacroExecutionException
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.api.net.ResponseException
 *  com.google.gson.Gson
 */
package com.atlassian.confluence.plugins.jiracharts.render;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLinkRequest;
import com.atlassian.applinks.api.ApplicationLinkRequestFactory;
import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.applinks.api.ReadOnlyApplicationLinkService;
import com.atlassian.applinks.api.auth.Anonymous;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.plugins.jiracharts.model.JiraHtmlChartModel;
import com.atlassian.confluence.plugins.jiracharts.render.JiraChart;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.ResponseException;
import com.google.gson.Gson;
import java.net.SocketTimeoutException;

public abstract class JiraHtmlChart
implements JiraChart {
    protected final ReadOnlyApplicationLinkService applicationLinkService;
    protected final I18nResolver i18nResolver;

    protected JiraHtmlChart(ReadOnlyApplicationLinkService applicationLinkService, I18nResolver i18nResolver) {
        this.applicationLinkService = applicationLinkService;
        this.i18nResolver = i18nResolver;
    }

    public abstract Class<? extends JiraHtmlChartModel> getChartModelClass();

    protected Object getChartModel(String appId, String url) throws MacroExecutionException {
        try {
            ReadOnlyApplicationLink applicationLink = this.applicationLinkService.getApplicationLink(new ApplicationId(appId));
            ApplicationLinkRequest request = this.createRequest(applicationLink, Request.MethodType.GET, url);
            return new Gson().fromJson(request.execute(), this.getChartModelClass());
        }
        catch (ResponseException e) {
            if (e.getCause() instanceof SocketTimeoutException) {
                throw new MacroExecutionException(this.i18nResolver.getText("jirachart.error.timeout.connection"), (Throwable)e);
            }
            throw new MacroExecutionException(this.i18nResolver.getText("jirachart.error.execution"), (Throwable)e);
        }
        catch (Exception e) {
            throw new MacroExecutionException(this.i18nResolver.getText("jirachart.error.execution"), (Throwable)e);
        }
    }

    private ApplicationLinkRequest createRequest(ReadOnlyApplicationLink appLink, Request.MethodType methodType, String baseRestUrl) throws CredentialsRequiredException {
        ApplicationLinkRequest request;
        String url = appLink.getRpcUrl() + baseRestUrl;
        ApplicationLinkRequestFactory requestFactory = appLink.createAuthenticatedRequestFactory();
        try {
            request = requestFactory.createRequest(methodType, url);
        }
        catch (CredentialsRequiredException e) {
            requestFactory = appLink.createAuthenticatedRequestFactory(Anonymous.class);
            request = requestFactory.createRequest(methodType, url);
        }
        return request;
    }

    @Override
    public boolean isVerifyChartSupported() {
        return false;
    }
}

