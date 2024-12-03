/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.upm.servlet;

import com.atlassian.upm.core.permission.Permission;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import com.atlassian.upm.core.servlet.UpmServletHandler;
import com.atlassian.upm.servlet.PluginManagerHandler;
import java.io.IOException;
import java.util.Objects;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public final class PluginFinderServlet
extends HttpServlet {
    private final PluginManagerHandler handler;
    private final PermissionEnforcer permissionEnforcer;

    public PluginFinderServlet(PluginManagerHandler handler, PermissionEnforcer permissionEnforcer) {
        this.handler = Objects.requireNonNull(handler, "handler");
        this.permissionEnforcer = Objects.requireNonNull(permissionEnforcer, "permissionEnforcer");
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (!this.permissionEnforcer.hasPermission(Permission.MANAGE_IN_PROCESS_PLUGIN_INSTALL_FROM_URI)) {
            if (!this.permissionEnforcer.isLoggedIn()) {
                this.handler.redirectToLogin(request, response, UpmServletHandler.PermissionLevel.ANY);
            } else {
                this.handler.handle(request, response, "request-plugins.vm", this.permissionEnforcer.isAdmin());
            }
        } else {
            this.handler.handle(request, response, "find-plugins.vm", true);
        }
    }
}

