/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.websudo.WebSudoNotRequired
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.upm.rest.resources;

import com.atlassian.sal.api.websudo.WebSudoNotRequired;
import com.atlassian.upm.SysPersisted;
import com.atlassian.upm.UpmSettings;
import com.atlassian.upm.pac.PacClient;
import com.atlassian.upm.rest.UpmUriBuilder;
import com.atlassian.upm.rest.representations.UpmLinkBuilder;
import com.atlassian.upm.rest.representations.UpmRepresentationFactory;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

@Path(value="/pac-status")
@WebSudoNotRequired
public class PacStatusResource {
    private final PacClient client;
    private final UpmRepresentationFactory factory;
    private final SysPersisted sysPersisted;

    public PacStatusResource(PacClient client, UpmRepresentationFactory factory, SysPersisted sysPersisted) {
        this.client = Objects.requireNonNull(client, "client");
        this.factory = Objects.requireNonNull(factory, "factory");
        this.sysPersisted = Objects.requireNonNull(sysPersisted, "sysPersisted");
    }

    @GET
    @Produces(value={"application/vnd.atl.plugins.pac.status+json"})
    public Response getPacStatus() {
        this.client.forgetPacReachableState(false);
        return Response.ok((Object)this.factory.createPacStatusRepresentation(this.sysPersisted.is(UpmSettings.PAC_DISABLED), this.client.isPacReachable())).build();
    }

    public static final class PacStatusRepresentation {
        @JsonProperty
        private boolean disabled;
        @JsonProperty
        private boolean reached;
        @JsonProperty
        private final Map<String, URI> links;

        @JsonCreator
        public PacStatusRepresentation(@JsonProperty(value="disabled") boolean disabled, @JsonProperty(value="reached") boolean reached, @JsonProperty(value="links") Map<String, URI> links) {
            this.disabled = disabled;
            this.reached = reached;
            this.links = Collections.unmodifiableMap(new HashMap<String, URI>(links));
        }

        public PacStatusRepresentation(boolean disabled, boolean reached, UpmUriBuilder uriBuilder, UpmLinkBuilder linkBuilder) {
            this(disabled, reached, linkBuilder.buildLinksFor(uriBuilder.buildPacStatusUri(), false).build());
        }

        public boolean isDisabled() {
            return this.disabled;
        }

        public boolean isReached() {
            return this.reached;
        }

        public URI getSelf() {
            return this.links.get("self");
        }
    }
}

