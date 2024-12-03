/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ReadOnlyApplicationLinkService
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.user.User
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.confluence.plugins.jira;

import com.atlassian.applinks.api.ReadOnlyApplicationLinkService;
import com.atlassian.confluence.plugins.jira.AbstractProxyServlet;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.net.Request;
import com.atlassian.user.User;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AppLinksProxyRequestServlet
extends AbstractProxyServlet {
    private static final String APP_TYPE = "appType";
    private static final String APP_ID = "appId";
    private static final String JSON_STRING = "jsonString";
    private static final String FORMAT_ERRORS = "formatErrors";
    private static final String PATH = "path";
    private static Set<String> reservedParameters = new HashSet<String>(Arrays.asList("path", "jsonString", "appId", "appType", "formatErrors"));
    private final I18nResolver i18nResolver;
    private final PermissionManager permissionManager;

    public AppLinksProxyRequestServlet(ReadOnlyApplicationLinkService appLinkService, I18nResolver i18nResolver, PermissionManager permissionManager) {
        super(appLinkService);
        this.i18nResolver = i18nResolver;
        this.permissionManager = permissionManager;
    }

    @Override
    void doProxy(HttpServletRequest req, HttpServletResponse resp, Request.MethodType methodType) throws IOException, ServletException {
        if (!this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, PermissionManager.TARGET_APPLICATION)) {
            resp.sendError(403, this.i18nResolver.getText("jiraissues.error.notpermitted"));
            return;
        }
        Object url = req.getParameter(PATH);
        if (url == null) {
            url = req.getHeader("X-AppPath");
        }
        Map parameters = req.getParameterMap();
        StringBuilder queryString = new StringBuilder();
        for (Object name : parameters.keySet()) {
            if (reservedParameters.contains(name)) continue;
            Object val = parameters.get(name);
            if (val instanceof String[]) {
                String[] params;
                for (String param : params = (String[])val) {
                    queryString.append(queryString.length() > 0 ? "&" : "").append(URLEncoder.encode(name.toString(), StandardCharsets.UTF_8)).append("=").append(URLEncoder.encode(param, StandardCharsets.UTF_8));
                }
                continue;
            }
            queryString.append(queryString.length() > 0 ? "&" : "").append(URLEncoder.encode(name.toString(), StandardCharsets.UTF_8)).append("=").append(URLEncoder.encode(req.getParameter(name.toString()), StandardCharsets.UTF_8));
        }
        if (methodType == Request.MethodType.GET && queryString.length() > 0) {
            url = (String)url + (((String)url).contains("?") ? (char)'&' : '?') + queryString;
        }
        super.doProxy(resp, req, methodType, (String)url);
    }
}

