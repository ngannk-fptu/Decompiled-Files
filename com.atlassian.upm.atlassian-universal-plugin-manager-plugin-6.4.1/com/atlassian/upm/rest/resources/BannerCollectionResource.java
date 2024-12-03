/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.websudo.WebSudoNotRequired
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.upm.rest.resources;

import com.atlassian.marketplace.client.MpacException;
import com.atlassian.marketplace.client.api.AddonQuery;
import com.atlassian.marketplace.client.api.Page;
import com.atlassian.marketplace.client.api.QueryBounds;
import com.atlassian.marketplace.client.model.AddonReference;
import com.atlassian.sal.api.websudo.WebSudoNotRequired;
import com.atlassian.upm.core.permission.Permission;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import com.atlassian.upm.pac.PacClient;
import com.atlassian.upm.rest.representations.UpmRepresentationFactory;
import java.util.Objects;
import java.util.Optional;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path(value="/banners")
@WebSudoNotRequired
public class BannerCollectionResource {
    private static final String UPM_CAROUSEL_LABEL = "upm-carousel";
    private final UpmRepresentationFactory factory;
    private final PacClient client;
    private final PermissionEnforcer permissionEnforcer;
    private static final Logger log = LoggerFactory.getLogger(BannerCollectionResource.class);

    public BannerCollectionResource(UpmRepresentationFactory factory, PacClient client, PermissionEnforcer permissionEnforcer) {
        this.factory = Objects.requireNonNull(factory, "representationFactory");
        this.permissionEnforcer = Objects.requireNonNull(permissionEnforcer, "permissionEnforcer");
        this.client = Objects.requireNonNull(client, "client");
    }

    @GET
    @Produces(value={"application/vnd.atl.plugins.banners+json"})
    public Response get(@QueryParam(value="offset") @DefaultValue(value="0") int offset) {
        this.permissionEnforcer.enforcePermission(Permission.GET_AVAILABLE_PLUGINS);
        AddonQuery query = AddonQuery.builder().label(Optional.of(UPM_CAROUSEL_LABEL)).bounds(QueryBounds.offset(offset)).build();
        try {
            Page<AddonReference> banners = this.client.findBanners(query);
            return Response.ok((Object)this.factory.createBannerCollectionRepresentation(banners)).build();
        }
        catch (MpacException e) {
            log.warn("Failed to get banners: " + e.getMessage());
            log.debug(e.getMessage(), (Throwable)e);
            return Response.serverError().build();
        }
    }
}

