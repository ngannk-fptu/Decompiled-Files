/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  com.google.common.collect.ImmutableMap
 *  javax.annotation.ParametersAreNonnullByDefault
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.stp.servlet;

import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.atlassian.troubleshooting.api.WebResourcesService;
import com.atlassian.troubleshooting.stp.servlet.StpServletUtils;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Map;
import java.util.Objects;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@ParametersAreNonnullByDefault
public class PreUpgradeServlet
extends HttpServlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(PreUpgradeServlet.class);
    private static final String PRE_UPGRADE_TEMPLATE = "/templates/html/pre-upgrade.vm";
    private final TemplateRenderer renderer;
    private final UserManager userManager;
    private final WebResourcesService webResourcesService;
    private final StpServletUtils stpServletUtils;
    private final ApplicationProperties applicationProperties;

    @Autowired
    public PreUpgradeServlet(UserManager userManager, TemplateRenderer renderer, WebResourcesService webResourcesService, StpServletUtils stpServletUtils, ApplicationProperties applicationProperties) {
        this.userManager = Objects.requireNonNull(userManager);
        this.renderer = Objects.requireNonNull(renderer);
        this.webResourcesService = Objects.requireNonNull(webResourcesService);
        this.stpServletUtils = Objects.requireNonNull(stpServletUtils);
        this.applicationProperties = Objects.requireNonNull(applicationProperties);
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        this.stpServletUtils.initializeHeader(res);
        if (this.applicationProperties.getPlatformId().equals("jira") || this.applicationProperties.getPlatformId().equals("conf") || this.applicationProperties.getPlatformId().equals("bitbucket")) {
            if (this.userManager.isSystemAdmin(this.userManager.getRemoteUserKey(req))) {
                try (PrintWriter writer = res.getWriter();){
                    this.renderer.render(PRE_UPGRADE_TEMPLATE, (Map)ImmutableMap.of((Object)"webResource", (Object)this.webResourcesService), (Writer)writer);
                }
            } else {
                this.stpServletUtils.redirectToLogin(req, res);
            }
        } else {
            LOGGER.warn("Not running in JIRA, Confluence, or Bitbucket");
            res.sendError(404);
        }
    }
}

