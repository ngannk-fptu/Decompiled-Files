/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLinkRequest
 *  com.atlassian.applinks.api.ApplicationLinkRequestFactory
 *  com.atlassian.applinks.api.ApplicationLinkResponseHandler
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.atlassian.applinks.api.ReadOnlyApplicationLinkService
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.api.net.Response
 *  com.atlassian.sal.api.net.ResponseException
 *  com.atlassian.user.User
 *  com.google.gson.Gson
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.io.IOUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.jira;

import com.atlassian.applinks.api.ApplicationLinkRequest;
import com.atlassian.applinks.api.ApplicationLinkRequestFactory;
import com.atlassian.applinks.api.ApplicationLinkResponseHandler;
import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.applinks.api.ReadOnlyApplicationLinkService;
import com.atlassian.confluence.plugins.jira.AbstractProxyServlet;
import com.atlassian.confluence.plugins.jiracharts.helper.JiraChartHelper;
import com.atlassian.confluence.plugins.jiracharts.model.JiraImageChartModel;
import com.atlassian.confluence.plugins.jiracharts.render.JiraChartFactory;
import com.atlassian.confluence.plugins.jiracharts.render.JiraImageChart;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.user.User;
import com.google.gson.Gson;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChartProxyServlet
extends AbstractProxyServlet {
    private static final Logger log = LoggerFactory.getLogger(ChartProxyServlet.class);
    private final JiraChartFactory jiraChartFactory;
    private final I18nResolver i18nResolver;
    private final PermissionManager permissionManager;

    public ChartProxyServlet(ReadOnlyApplicationLinkService appLinkService, JiraChartFactory jiraChartFactory, I18nResolver i18nResolver, PermissionManager permissionManager) {
        super(appLinkService);
        this.jiraChartFactory = jiraChartFactory;
        this.i18nResolver = i18nResolver;
        this.permissionManager = permissionManager;
    }

    protected I18nResolver getI18nResolver() {
        return this.i18nResolver;
    }

    protected PermissionManager getPermissionManager() {
        return this.permissionManager;
    }

    @Override
    void doProxy(HttpServletRequest req, HttpServletResponse resp, Request.MethodType methodType) throws IOException, ServletException {
        if (!this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, PermissionManager.TARGET_APPLICATION)) {
            resp.sendError(403, this.i18nResolver.getText("jiraissues.error.notpermitted"));
            return;
        }
        if (JiraChartHelper.isRequiredParamValid(req)) {
            String chartType = req.getParameter("chartType");
            JiraImageChart jiraChart = (JiraImageChart)this.jiraChartFactory.getJiraChartRenderer(chartType);
            super.doProxy(resp, req, methodType, jiraChart.getJiraGadgetUrl(req));
        } else {
            resp.sendError(400, "Either jql, chartType or appId parameters is empty");
        }
    }

    @Override
    protected void handleResponse(ApplicationLinkRequestFactory requestFactory, HttpServletRequest req, HttpServletResponse resp, ApplicationLinkRequest request, ReadOnlyApplicationLink appLink) throws ResponseException {
        String redirectLink = this.getRedirectImgLink(request, req, requestFactory, resp, appLink);
        if (redirectLink != null) {
            try {
                resp.sendRedirect(redirectLink);
            }
            catch (IOException e) {
                log.error("unable to send redirect to " + redirectLink, (Throwable)e);
            }
        }
    }

    String getRedirectImgLink(ApplicationLinkRequest request, HttpServletRequest req, ApplicationLinkRequestFactory requestFactory, HttpServletResponse resp, ReadOnlyApplicationLink appLink) throws ResponseException {
        ChartProxyResponseHandler responseHandler = new ChartProxyResponseHandler(req, requestFactory, resp);
        Object ret = request.execute((ApplicationLinkResponseHandler)responseHandler);
        if (ret instanceof ByteArrayOutputStream) {
            ByteArrayInputStream in = new ByteArrayInputStream(((ByteArrayOutputStream)ret).toByteArray());
            JiraImageChartModel chartModel = null;
            try {
                chartModel = (JiraImageChartModel)GsonHolder.gson.fromJson((Reader)new InputStreamReader((InputStream)in, StandardCharsets.UTF_8), JiraImageChartModel.class);
            }
            catch (Exception e) {
                log.error("Unable to parse jira chart macro json to object", (Throwable)e);
            }
            if (chartModel != null && chartModel.getLocation() != null) {
                return this.getApplinkURL(appLink) + "/charts?filename=" + chartModel.getLocation();
            }
        }
        return null;
    }

    protected URI getApplinkURL(ReadOnlyApplicationLink applicationLink) {
        return applicationLink.getDisplayUrl();
    }

    protected static class ChartProxyResponseHandler
    extends AbstractProxyServlet.ProxyApplicationLinkResponseHandler {
        private ChartProxyResponseHandler(HttpServletRequest req, ApplicationLinkRequestFactory requestFactory, HttpServletResponse resp) {
            super(req, requestFactory, resp);
        }

        @Override
        protected Object processSuccess(Response response) throws ResponseException {
            InputStream responseStream = response.getResponseBodyAsStream();
            Map headers = response.getHeaders();
            headers.keySet().stream().filter(key -> AbstractProxyServlet.headerWhitelist.contains(key)).forEach(key -> this.resp.setHeader(key, (String)headers.get(key)));
            try {
                if (responseStream != null) {
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    IOUtils.copy((InputStream)responseStream, (OutputStream)outputStream);
                    outputStream.flush();
                    outputStream.close();
                    return outputStream;
                }
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        }
    }

    private static final class GsonHolder {
        static final Gson gson = new Gson();

        private GsonHolder() {
        }
    }
}

