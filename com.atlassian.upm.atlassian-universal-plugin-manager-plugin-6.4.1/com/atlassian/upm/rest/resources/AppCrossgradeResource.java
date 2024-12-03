/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.PathSegment
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.upm.rest.resources;

import com.atlassian.upm.core.rest.UpmUriEscaper;
import com.atlassian.upm.mac.HamletClient;
import com.atlassian.upm.rest.representations.UpmRepresentationFactory;
import java.net.URI;
import java.util.Objects;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path(value="/{pluginKey}/license/crossgrade")
public class AppCrossgradeResource {
    private static final Logger log = LoggerFactory.getLogger(AppCrossgradeResource.class);
    private final HamletClient hamletClient;
    private final UpmRepresentationFactory representationFactory;

    public AppCrossgradeResource(HamletClient hamletClient, UpmRepresentationFactory representationFactory) {
        this.hamletClient = Objects.requireNonNull(hamletClient, "hamletClient");
        this.representationFactory = Objects.requireNonNull(representationFactory, "representationFactory");
    }

    @POST
    @Produces(value={"application/json"})
    public Response getLicense(@PathParam(value="pluginKey") PathSegment pluginKeyPath) {
        String pluginKey = UpmUriEscaper.unescape(pluginKeyPath.getPath());
        try {
            return (Response)this.hamletClient.crossgradeAppLicense(pluginKey).map(uri -> Response.ok((Object)new AppCrossgradeRepresentation((URI)uri)).build()).getOrElse(Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)this.representationFactory.createI18nErrorRepresentation("upm.plugin.license.error.crossgrade")).type("application/vnd.atl.plugins.error+json").build());
        }
        catch (Exception e) {
            log.error("Error crossgrading license for " + pluginKey, (Throwable)e);
            return Response.status((Response.Status)Response.Status.INTERNAL_SERVER_ERROR).entity((Object)this.representationFactory.createI18nErrorRepresentation("upm.plugin.license.error.crossgrade")).type("application/vnd.atl.plugins.error+json").build();
        }
    }

    class AppCrossgradeRepresentation {
        @JsonProperty
        private final URI hamletCrossgradeUri;

        @JsonCreator
        public AppCrossgradeRepresentation(URI hamletCrossgradeUri) {
            this.hamletCrossgradeUri = hamletCrossgradeUri;
        }
    }
}

