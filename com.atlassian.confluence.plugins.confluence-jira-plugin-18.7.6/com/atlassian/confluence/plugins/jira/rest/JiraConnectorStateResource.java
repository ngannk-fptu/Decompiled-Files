/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.atlassian.user.User
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.confluence.plugins.jira.rest;

import com.atlassian.confluence.extra.jira.api.services.JiraConnectorManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.user.User;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path(value="servers")
@Produces(value={"application/json"})
@AnonymousAllowed
public class JiraConnectorStateResource {
    private final JiraConnectorManager jiraConnectorManager;
    private final PermissionManager permissionManager;

    public JiraConnectorStateResource(JiraConnectorManager jiraConnectorManager, PermissionManager permissionManager) {
        this.jiraConnectorManager = jiraConnectorManager;
        this.permissionManager = permissionManager;
    }

    @GET
    public Response getJiraServers() {
        if (!this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, PermissionManager.TARGET_APPLICATION)) {
            return Response.status((Response.Status)Response.Status.FORBIDDEN).build();
        }
        return Response.ok(this.jiraConnectorManager.getJiraServers()).build();
    }
}

