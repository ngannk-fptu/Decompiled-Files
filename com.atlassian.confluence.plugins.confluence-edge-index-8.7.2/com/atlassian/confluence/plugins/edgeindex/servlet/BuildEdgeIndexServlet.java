/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.security.service.XsrfTokenService
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.user.User
 *  com.google.common.base.Strings
 *  io.atlassian.fugue.Pair
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.joda.time.Period
 */
package com.atlassian.confluence.plugins.edgeindex.servlet;

import com.atlassian.confluence.plugins.edgeindex.EdgeIndexBuilder;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.service.XsrfTokenService;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.user.User;
import com.google.common.base.Strings;
import io.atlassian.fugue.Pair;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.joda.time.Period;

public class BuildEdgeIndexServlet
extends HttpServlet {
    private final PermissionManager permissionManager;
    private final EdgeIndexBuilder edgeIndexBuilder;
    private final XsrfTokenService xsrfTokenService;
    private I18NBeanFactory i18NBeanFactory;

    public BuildEdgeIndexServlet(PermissionManager permissionManager, EdgeIndexBuilder edgeIndexBuilder, XsrfTokenService xsrfTokenService, I18NBeanFactory i18NBeanFactory) {
        this.permissionManager = permissionManager;
        this.edgeIndexBuilder = edgeIndexBuilder;
        this.xsrfTokenService = xsrfTokenService;
        this.i18NBeanFactory = i18NBeanFactory;
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.ADMINISTER, PermissionManager.TARGET_APPLICATION)) {
            resp.sendError(403, "Insufficient privileges.");
            return;
        }
        Pair tokenPair = this.xsrfTokenService.generateToken(req);
        Object responseBody = "";
        responseBody = (String)responseBody + "<form action=\"" + req.getContextPath() + "/plugins/servlet/edge-index/build\" method=\"post\">";
        responseBody = (String)responseBody + "<input type=\"hidden\" name=\"" + (String)tokenPair.left() + "\" value=\"" + (String)tokenPair.right() + "\">";
        responseBody = (String)responseBody + "<input type='text' name='weeks' value='2'>";
        responseBody = (String)responseBody + "<input type=\"submit\" value=\"Build edge index\">";
        responseBody = (String)responseBody + "</form>";
        resp.getWriter().append((CharSequence)responseBody);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.ADMINISTER, PermissionManager.TARGET_APPLICATION)) {
            resp.sendError(403, "Insufficient privileges.");
            return;
        }
        try {
            this.edgeIndexBuilder.rebuild(BuildEdgeIndexServlet.getRebuildPeriod(req), EdgeIndexBuilder.RebuildCondition.FORCE);
            resp.getWriter().append("Build index completed successfully");
        }
        catch (RuntimeException e) {
            resp.sendError(500);
            throw e;
        }
    }

    private static Period getRebuildPeriod(HttpServletRequest req) {
        String weeksParam = req.getParameter("weeks");
        if (!Strings.isNullOrEmpty((String)weeksParam)) {
            return Period.weeks((int)Integer.parseInt(weeksParam));
        }
        return EdgeIndexBuilder.EDGE_INDEX_REBUILD_DEFAULT_START_PERIOD;
    }
}

