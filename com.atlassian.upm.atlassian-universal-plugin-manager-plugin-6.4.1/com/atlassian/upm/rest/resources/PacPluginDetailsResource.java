/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.PathSegment
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.upm.rest.resources;

import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.Plugin;
import com.atlassian.upm.core.PluginRetriever;
import com.atlassian.upm.pac.PacClient;
import com.atlassian.upm.pac.PluginVersionPair;
import com.atlassian.upm.rest.representations.UpmRepresentationFactory;
import java.util.Iterator;
import java.util.Objects;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;

@Path(value="/pac-details/{pluginKey}/{pluginVersion}")
public class PacPluginDetailsResource {
    private final PacClient client;
    private final UpmRepresentationFactory representationFactory;
    private final PluginRetriever pluginRetriever;

    public PacPluginDetailsResource(PacClient client, UpmRepresentationFactory representationFactory, PluginRetriever pluginRetriever) {
        this.client = Objects.requireNonNull(client, "client");
        this.representationFactory = Objects.requireNonNull(representationFactory, "representationFactory");
        this.pluginRetriever = Objects.requireNonNull(pluginRetriever, "pluginRetriever");
    }

    @GET
    @Produces(value={"application/vnd.atl.plugins.pac.details+json"})
    public Response get(@PathParam(value="pluginKey") PathSegment pluginKeyPath, @PathParam(value="pluginVersion") String version) {
        String key = pluginKeyPath.getPath();
        Iterator<Plugin> iterator = this.pluginRetriever.getPlugin(key).iterator();
        if (iterator.hasNext()) {
            Plugin plugin = iterator.next();
            Option<PluginVersionPair> pluginVersionPair = this.client.getSpecificAndLatestAvailablePluginVersions(plugin, version);
            return Response.ok((Object)this.representationFactory.createPacDetailsRepresentation(plugin, pluginVersionPair)).build();
        }
        return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
    }
}

