/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.retention.RetentionFeatureChecker
 *  com.atlassian.confluence.retention.RetentionPolicyPermissionService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 */
package com.atlassian.confluence.plugins.retentionrules.rest;

import com.atlassian.confluence.api.service.retention.RetentionFeatureChecker;
import com.atlassian.confluence.retention.RetentionPolicyPermissionService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path(value="/space/{spaceKey}/edit-permission")
public class SpaceRetentionPolicyPermissionResource {
    private final RetentionFeatureChecker featureChecker;
    private final RetentionPolicyPermissionService retentionPolicyPermissionService;

    public SpaceRetentionPolicyPermissionResource(@ComponentImport RetentionFeatureChecker featureChecker, @ComponentImport RetentionPolicyPermissionService retentionPolicyPermissionService) {
        this.featureChecker = featureChecker;
        this.retentionPolicyPermissionService = retentionPolicyPermissionService;
    }

    @GET
    @Produces(value={"application/json"})
    public Response getUserPermissionForSpace(@PathParam(value="spaceKey") String spaceKey) {
        if (!this.featureChecker.isFeatureAvailable()) {
            return Response.status((int)404).build();
        }
        Boolean canEdit = this.retentionPolicyPermissionService.getUserRetentionPolicyPermissionForSpace(spaceKey);
        return Response.ok((Object)canEdit).build();
    }
}

