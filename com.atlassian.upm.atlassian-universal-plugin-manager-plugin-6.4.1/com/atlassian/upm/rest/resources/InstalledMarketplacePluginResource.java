/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 */
package com.atlassian.upm.rest.resources;

import com.atlassian.upm.UpmHostApplicationInformation;
import com.atlassian.upm.core.PluginRetriever;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import com.atlassian.upm.pac.PacClient;
import com.atlassian.upm.rest.representations.InstalledMarketplacePluginRepresentation;
import com.atlassian.upm.rest.representations.UpmRepresentationFactory;
import com.atlassian.upm.rest.resources.AbstractInstalledMarketplacePluginResource;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@OpenAPIDefinition(info=@Info(title="UPM Plugin License", version="6.1.0", description="REST endpoint for retrieving plugin license file"))
@Path(value="/{pluginKey}/marketplace")
public class InstalledMarketplacePluginResource
extends AbstractInstalledMarketplacePluginResource {
    public static final String INSTALLED_MARKETPLACE_PLUGIN_RESOURCE_PATH = "/{pluginKey}/marketplace";

    public InstalledMarketplacePluginResource(UpmRepresentationFactory representationFactory, PluginRetriever pluginRetriever, PermissionEnforcer permissionEnforcer, PacClient pacClient, UpmHostApplicationInformation appInfo) {
        super(representationFactory, pluginRetriever, permissionEnforcer, pacClient, appInfo);
    }

    @GET
    @Produces(value={"application/vnd.atl.plugins+json"})
    @Operation(summary="Get a plugin license for the plugin key")
    @ApiResponses(value={@ApiResponse(responseCode="200", description="Successful operation", content={@Content(schema=@Schema(implementation=InstalledMarketplacePluginRepresentation.class))}), @ApiResponse(responseCode="404", description="Not found")})
    public Response get(@PathParam(value="pluginKey") String pluginKey) {
        return this.getInternal(pluginKey, true);
    }
}

