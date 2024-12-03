/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.servlet;

import com.atlassian.confluence.util.GeneralUtil;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TinyUrlServlet
extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(TinyUrlServlet.class);

    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        if (!GeneralUtil.isSetupComplete()) {
            httpServletResponse.sendError(503);
            return;
        }
        String identifier = httpServletRequest.getPathInfo();
        if (identifier == null) {
            identifier = "";
        } else if (identifier.startsWith("/")) {
            identifier = identifier.substring(1);
        }
        String baseUrl = GeneralUtil.getGlobalSettings().getBaseUrl();
        if (baseUrl == null) {
            log.warn("GeneralUtil.getGlobalSettings().getBaseUrl() returned null. Server Base Url should never be null.");
            baseUrl = GeneralUtil.lookupDomainName(httpServletRequest);
        }
        httpServletResponse.sendRedirect(baseUrl + "/pages/tinyurl.action?urlIdentifier=" + identifier);
    }
}

