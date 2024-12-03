/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugins.whitelist.OutboundWhitelist
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.confluence.plugins.metadata.jira.web;

import com.atlassian.confluence.plugins.metadata.jira.event.JiraItemVisitEvent;
import com.atlassian.confluence.plugins.metadata.jira.model.JiraMetadataGroup;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugins.whitelist.OutboundWhitelist;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JiraRedirectServlet
extends HttpServlet {
    private final EventPublisher eventPublisher;
    private final ApplicationProperties applicationProperties;
    private final OutboundWhitelist outboundWhitelist;

    public JiraRedirectServlet(EventPublisher eventPublisher, ApplicationProperties applicationProperties, OutboundWhitelist outboundWhitelist) {
        this.eventPublisher = eventPublisher;
        this.applicationProperties = applicationProperties;
        this.outboundWhitelist = outboundWhitelist;
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        JiraMetadataGroup.Type type;
        try {
            String typeStr = Optional.ofNullable(req.getParameter("t")).orElse("");
            type = JiraMetadataGroup.Type.valueOf(typeStr);
        }
        catch (IllegalArgumentException e) {
            type = null;
        }
        if (type != null) {
            boolean isViewMore = req.getParameterMap().containsKey("more");
            this.eventPublisher.publish((Object)new JiraItemVisitEvent((Object)this, type, isViewMore));
        }
        String destUrl = Optional.ofNullable(req.getParameter("u")).orElse(this.applicationProperties.getBaseUrl(UrlMode.ABSOLUTE));
        try {
            if (!this.outboundWhitelist.isAllowed(new URI(destUrl))) {
                throw new IllegalArgumentException("The provided url is not included in the whitelist!");
            }
            resp.sendRedirect(destUrl);
        }
        catch (IllegalArgumentException | URISyntaxException e) {
            resp.setContentType("text/plain");
            resp.setStatus(403);
            try (PrintWriter writer = resp.getWriter();){
                writer.write(e.getMessage());
            }
        }
    }
}

