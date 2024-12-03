/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.google.common.base.Preconditions
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.CacheControl
 *  javax.ws.rs.core.Response
 */
package com.atlassian.streams.internal.rest.resources;

import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.streams.internal.ConfigRepresentationBuilder;
import com.google.common.base.Preconditions;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;

@Path(value="preferences")
@AnonymousAllowed
public class ConfigurationPreferencesResource {
    private static final CacheControl NO_CACHE = new CacheControl();
    private final ConfigRepresentationBuilder representationBuilder;

    public ConfigurationPreferencesResource(ConfigRepresentationBuilder representationBuilder) {
        this.representationBuilder = (ConfigRepresentationBuilder)Preconditions.checkNotNull((Object)representationBuilder, (Object)"representationBuilder");
    }

    @GET
    @Produces(value={"application/vnd.atl.streams+json"})
    public Response getPreferences() {
        return Response.ok((Object)this.representationBuilder.getConfigPreferencesRepresentation()).type("application/vnd.atl.streams+json").cacheControl(NO_CACHE).build();
    }

    static {
        NO_CACHE.setNoStore(true);
        NO_CACHE.setNoCache(true);
    }
}

