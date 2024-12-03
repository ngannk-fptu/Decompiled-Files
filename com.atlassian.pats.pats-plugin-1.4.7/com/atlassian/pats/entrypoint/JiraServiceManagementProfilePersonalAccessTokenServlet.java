/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.jira.component.ComponentAccessor
 *  com.atlassian.servicedesk.api.customer.CustomerContextService
 *  com.atlassian.soy.renderer.SoyTemplateRenderer
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.pats.entrypoint;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.servicedesk.api.customer.CustomerContextService;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JiraServiceManagementProfilePersonalAccessTokenServlet
extends HttpServlet {
    private final SoyTemplateRenderer renderer;

    public JiraServiceManagementProfilePersonalAccessTokenServlet(SoyTemplateRenderer renderer) {
        this.renderer = renderer;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (this.isInCustomerContext()) {
            response.setContentType("text/html");
            this.renderer.render((Appendable)response.getWriter(), "com.atlassian.pats.pats-plugin:jsm-personal-access-tokens-plugin-frontend-templates", "Personal.Access.Tokens.Display", Collections.emptyMap());
        } else {
            response.sendError(403);
        }
    }

    private boolean isInCustomerContext() {
        return Optional.ofNullable(ComponentAccessor.getOSGiComponentInstanceOfType(CustomerContextService.class)).map(CustomerContextService::isInCustomerContext).orElse(false);
    }
}

