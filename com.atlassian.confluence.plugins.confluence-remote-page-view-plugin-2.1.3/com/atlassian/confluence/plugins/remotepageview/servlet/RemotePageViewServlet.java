/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang.StringUtils
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.confluence.plugins.remotepageview.servlet;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.plugins.remotepageview.api.service.RemotePageViewService;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

public class RemotePageViewServlet
extends HttpServlet {
    private final RemotePageViewService remotePageViewService;

    @Autowired
    public RemotePageViewServlet(RemotePageViewService remotePageViewService) {
        this.remotePageViewService = remotePageViewService;
    }

    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        this.handleRequest(httpServletRequest, httpServletResponse);
    }

    protected void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        this.handleRequest(httpServletRequest, httpServletResponse);
    }

    @VisibleForTesting
    void handleRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
        long pageId;
        String pageIdRequestParam = httpServletRequest.getParameter("pageId");
        if (StringUtils.isBlank((String)pageIdRequestParam)) {
            httpServletResponse.sendError(400);
            return;
        }
        try {
            pageId = Long.valueOf(pageIdRequestParam);
        }
        catch (NumberFormatException e) {
            httpServletResponse.sendError(400);
            return;
        }
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        String htmlPage = this.remotePageViewService.renderPage(pageId, user);
        httpServletResponse.setContentType("text/html");
        httpServletResponse.getWriter().write(htmlPage);
    }
}

