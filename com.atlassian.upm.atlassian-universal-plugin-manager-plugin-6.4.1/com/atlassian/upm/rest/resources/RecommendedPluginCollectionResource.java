/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.websudo.WebSudoNotRequired
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.PathSegment
 *  javax.ws.rs.core.Response
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.upm.rest.resources;

import com.atlassian.marketplace.client.MpacException;
import com.atlassian.marketplace.client.model.AddonReference;
import com.atlassian.sal.api.websudo.WebSudoNotRequired;
import com.atlassian.upm.core.permission.Permission;
import com.atlassian.upm.core.rest.UpmUriEscaper;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import com.atlassian.upm.pac.PacClient;
import com.atlassian.upm.rest.representations.UpmRepresentationFactory;
import java.util.Collection;
import java.util.Objects;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path(value="/{pluginKey}/recommendations")
@WebSudoNotRequired
public class RecommendedPluginCollectionResource {
    private final UpmRepresentationFactory factory;
    private final PacClient client;
    private final PermissionEnforcer permissionEnforcer;
    private static final int MAX_RECOMMENDATIONS_TO_SHOW = 4;
    private static final Logger log = LoggerFactory.getLogger(RecommendedPluginCollectionResource.class);

    public RecommendedPluginCollectionResource(UpmRepresentationFactory factory, PacClient client, PermissionEnforcer permissionEnforcer) {
        this.permissionEnforcer = Objects.requireNonNull(permissionEnforcer, "permissionEnforcer");
        this.factory = Objects.requireNonNull(factory, "factory");
        this.client = Objects.requireNonNull(client, "client");
    }

    @GET
    @Produces(value={"application/vnd.atl.plugins+json"})
    public Response get(@PathParam(value="pluginKey") PathSegment pluginKeyPath) {
        String pluginKey = UpmUriEscaper.unescape(pluginKeyPath.getPath());
        this.permissionEnforcer.enforcePermission(Permission.GET_AVAILABLE_PLUGINS);
        try {
            Collection<AddonReference> recommendations = this.client.getPluginRecommendations(pluginKey, 4);
            return Response.ok((Object)this.factory.createRecommendedPluginCollectionRepresentation(recommendations, pluginKey)).build();
        }
        catch (MpacException.ServerError e) {
            log.warn("Failed to get recommendations for " + pluginKey + ": MPAC returned error " + e.getStatus());
            return Response.status((int)502).build();
        }
        catch (MpacException e) {
            log.warn("Failed to get recommendations for " + pluginKey + ": " + e.getMessage());
            log.debug(e.getMessage(), (Throwable)e);
            return Response.status((int)502).build();
        }
    }
}

