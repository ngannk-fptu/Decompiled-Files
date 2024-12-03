/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.ParametersAreNonnullByDefault
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.troubleshooting.preupgrade.rest;

import com.atlassian.troubleshooting.preupgrade.PreUpgradePlanningManager;
import com.atlassian.troubleshooting.stp.security.PermissionValidationService;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path(value="pre-upgrade")
@Produces(value={"application/json"})
@ParametersAreNonnullByDefault
public class PreUpgradePlanningResource {
    private final PreUpgradePlanningManager preUpgradePlanningManager;
    private final PermissionValidationService permissionValidationService;

    public PreUpgradePlanningResource(PreUpgradePlanningManager preUpgradePlanningManager, PermissionValidationService permissionValidationService) {
        this.preUpgradePlanningManager = Objects.requireNonNull(preUpgradePlanningManager);
        this.permissionValidationService = Objects.requireNonNull(permissionValidationService);
    }

    @GET
    @Path(value="info")
    @Nonnull
    public Response getSupportedPlatformInfo(@QueryParam(value="zdu") boolean zduRecommendation) {
        this.permissionValidationService.validateIsSysadmin();
        return this.preUpgradePlanningManager.getPreUpgradeInfo(zduRecommendation).map(info -> Response.ok((Object)info).build()).orElse(Response.status((Response.Status)Response.Status.INTERNAL_SERVER_ERROR).entity((Object)"No data available").build());
    }
}

