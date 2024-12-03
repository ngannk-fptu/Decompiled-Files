/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.SeraphUtils
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.permission.AuthorisationException
 *  com.atlassian.sal.api.permission.PermissionEnforcer
 *  com.atlassian.soy.renderer.SoyTemplateRenderer
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.confluence.internal.webhooks;

import com.atlassian.confluence.util.SeraphUtils;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.permission.AuthorisationException;
import com.atlassian.sal.api.permission.PermissionEnforcer;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import java.io.IOException;
import java.util.HashMap;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;

public class ApplicationWebhooksPermissionHelperServlet
extends HttpServlet {
    private static final String RESOURCE_KEY = "com.atlassian.confluence.plugins.confluence-webhooks:admin-webhooks-soy-resources";
    private static final String TEMPLATE_KEY = "confluence.webhooks.root";
    private final SoyTemplateRenderer soyTemplateRenderer;
    private final PermissionEnforcer permissionEnforcer;

    @Autowired
    public ApplicationWebhooksPermissionHelperServlet(@ComponentImport SoyTemplateRenderer soyTemplateRenderer, @ComponentImport PermissionEnforcer permissionEnforcer) {
        this.soyTemplateRenderer = soyTemplateRenderer;
        this.permissionEnforcer = permissionEnforcer;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (this.permissionEnforcer.isAdmin()) {
            this.soyTemplateRenderer.render((Appendable)response.getWriter(), RESOURCE_KEY, TEMPLATE_KEY, new HashMap());
        } else {
            if (this.permissionEnforcer.isAuthenticated()) {
                throw new AuthorisationException();
            }
            response.sendRedirect(SeraphUtils.getLoginURL((HttpServletRequest)request));
        }
    }
}

