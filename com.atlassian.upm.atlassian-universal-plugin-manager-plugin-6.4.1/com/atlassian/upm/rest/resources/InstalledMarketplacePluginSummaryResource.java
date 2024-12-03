/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.PathSegment
 *  javax.ws.rs.core.Response
 */
package com.atlassian.upm.rest.resources;

import com.atlassian.upm.UpmHostApplicationInformation;
import com.atlassian.upm.core.PluginRetriever;
import com.atlassian.upm.core.rest.UpmUriEscaper;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import com.atlassian.upm.pac.PacClient;
import com.atlassian.upm.rest.representations.UpmRepresentationFactory;
import com.atlassian.upm.rest.resources.AbstractInstalledMarketplacePluginResource;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;

@Path(value="/{pluginKey}/marketplace/summary")
public class InstalledMarketplacePluginSummaryResource
extends AbstractInstalledMarketplacePluginResource {
    public InstalledMarketplacePluginSummaryResource(UpmRepresentationFactory representationFactory, PluginRetriever pluginRetriever, PermissionEnforcer permissionEnforcer, PacClient pacClient, UpmHostApplicationInformation appInfo) {
        super(representationFactory, pluginRetriever, permissionEnforcer, pacClient, appInfo);
    }

    @GET
    @Produces(value={"application/vnd.atl.plugins+json"})
    public Response get(@PathParam(value="pluginKey") PathSegment pluginKeyPath, @QueryParam(value="update") @DefaultValue(value="false") boolean withUpdate) {
        return this.getInternal(UpmUriEscaper.unescape(pluginKeyPath.getPath()), withUpdate);
    }
}

