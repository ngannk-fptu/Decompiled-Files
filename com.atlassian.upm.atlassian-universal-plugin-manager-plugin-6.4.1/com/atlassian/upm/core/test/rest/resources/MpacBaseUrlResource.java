/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.upm.core.test.rest.resources;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.upm.core.Sys;
import com.atlassian.upm.core.pac.MarketplaceBaseUrlChangedEvent;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import java.util.Objects;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

@Path(value="/test/mpac-base-url")
public class MpacBaseUrlResource {
    private final PermissionEnforcer permissionEnforcer;
    private static volatile MpacBaseUrlRepresentation mpacBaseUrl = null;
    private final EventPublisher eventPublisher;

    public MpacBaseUrlResource(EventPublisher eventPublisher, PermissionEnforcer permissionEnforcer) {
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "eventPublisher");
        this.permissionEnforcer = Objects.requireNonNull(permissionEnforcer, "permissionEnforcer");
    }

    @PUT
    @Consumes(value={"application/vnd.atl.plugins.pac.base.url+json"})
    public Response setMpacBaseUrl(MpacBaseUrlRepresentation mpacBaseUrlRepresentation) {
        this.permissionEnforcer.enforceSystemAdmin();
        if (!Sys.isUpmDebugModeEnabled()) {
            return Response.status((Response.Status)Response.Status.PRECONDITION_FAILED).build();
        }
        mpacBaseUrl = mpacBaseUrlRepresentation;
        this.eventPublisher.publish((Object)new MarketplaceBaseUrlChangedEvent());
        return Response.ok((Object)mpacBaseUrlRepresentation).type("application/vnd.atl.plugins.pac.base.url+json").build();
    }

    @DELETE
    public Response resetMpacBaseUrl() {
        this.permissionEnforcer.enforceSystemAdmin();
        if (!Sys.isUpmDebugModeEnabled()) {
            return Response.status((Response.Status)Response.Status.PRECONDITION_FAILED).build();
        }
        mpacBaseUrl = null;
        this.eventPublisher.publish((Object)new MarketplaceBaseUrlChangedEvent());
        return Response.ok().build();
    }

    public static String getMpacBaseUrl() {
        if (mpacBaseUrl != null) {
            return mpacBaseUrl.getMpacBaseUrl();
        }
        return null;
    }

    public static final class MpacBaseUrlRepresentation {
        @JsonProperty
        private String mpacBaseUrl;

        @JsonCreator
        public MpacBaseUrlRepresentation(@JsonProperty(value="mpac-base-url") String mpacBaseUrl) {
            this.mpacBaseUrl = mpacBaseUrl;
        }

        public String getMpacBaseUrl() {
            return this.mpacBaseUrl;
        }
    }
}

