/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.util.BootstrapUtils
 *  com.google.common.annotations.VisibleForTesting
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.confluence.servlet;

import com.atlassian.config.util.BootstrapUtils;
import com.atlassian.confluence.impl.health.web.JohnsonPageDataProvider;
import com.atlassian.confluence.json.json.Json;
import com.google.common.annotations.VisibleForTesting;
import java.io.IOException;
import java.util.Objects;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JohnsonDataServlet
extends HttpServlet {
    public static final String URL_SUFFIX = "/johnson/data";
    private JohnsonPageDataProvider johnsonPageDataProvider;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.johnsonPageDataProvider = (JohnsonPageDataProvider)BootstrapUtils.getBootstrapContext().getBean(JohnsonPageDataProvider.class);
    }

    @VisibleForTesting
    void setJohnsonPageDataProvider(JohnsonPageDataProvider johnsonPageDataProvider) {
        this.johnsonPageDataProvider = Objects.requireNonNull(johnsonPageDataProvider);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (request.getRequestURI().endsWith(URL_SUFFIX)) {
            response.setContentType("application/json");
            response.setHeader("Access-Control-Allow-Origin", "*");
            Json johnsonPageData = this.johnsonPageDataProvider.getPageData();
            response.getWriter().write(johnsonPageData.serialize());
        } else {
            response.setStatus(404);
        }
    }
}

