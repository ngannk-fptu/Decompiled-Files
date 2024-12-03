/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.upm.request.rest.resources;

import com.atlassian.upm.SysPersisted;
import com.atlassian.upm.UpmSettings;
import java.util.Objects;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

@Path(value="/requests-status")
public class PluginRequestStatusResource {
    private final SysPersisted sysPersisted;

    public PluginRequestStatusResource(SysPersisted sysPersisted) {
        this.sysPersisted = Objects.requireNonNull(sysPersisted, "sysPersisted");
    }

    @GET
    @Produces(value={"application/vnd.atl.plugins+json"})
    public Response getPluginRequestDisablement() {
        return Response.ok((Object)new PluginRequestDisabledRepresentation(this.sysPersisted.is(UpmSettings.REQUESTS_DISABLED))).build();
    }

    public static final class PluginRequestDisabledRepresentation {
        @JsonProperty
        private boolean disabled;

        @JsonCreator
        public PluginRequestDisabledRepresentation(@JsonProperty(value="disabled") boolean disabled) {
            this.disabled = disabled;
        }

        public boolean isDisabled() {
            return this.disabled;
        }
    }
}

