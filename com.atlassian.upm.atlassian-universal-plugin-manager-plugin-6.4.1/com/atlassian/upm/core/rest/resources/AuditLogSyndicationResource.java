/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.rometools.rome.feed.atom.Feed
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.GET
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.EntityTag
 *  javax.ws.rs.core.Request
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$ResponseBuilder
 *  javax.ws.rs.core.Response$Status
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.upm.core.rest.resources;

import com.atlassian.upm.core.Sys;
import com.atlassian.upm.core.log.AuditLogService;
import com.atlassian.upm.core.permission.Permission;
import com.atlassian.upm.core.rest.representations.BaseRepresentationFactory;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import com.rometools.rome.feed.atom.Feed;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

@Path(value="/log/feed")
public class AuditLogSyndicationResource {
    private final AuditLogService auditLogService;
    private final PermissionEnforcer permissionEnforcer;
    private final BaseRepresentationFactory representationFactory;

    public AuditLogSyndicationResource(BaseRepresentationFactory representationFactory, AuditLogService auditLogService, PermissionEnforcer permissionEnforcer) {
        this.representationFactory = representationFactory;
        this.permissionEnforcer = Objects.requireNonNull(permissionEnforcer, "permissionEnforcer");
        this.auditLogService = auditLogService;
    }

    @GET
    @Produces(value={"application/atom+xml"})
    public Response get(@Context Request request, @DefaultValue(value="25") @QueryParam(value="max-results") Integer maxResults, @DefaultValue(value="0") @QueryParam(value="start-index") Integer startIndex) {
        this.permissionEnforcer.enforcePermission(Permission.GET_AUDIT_LOG);
        Feed feed = this.auditLogService.getFeed(maxResults, startIndex);
        Response.ResponseBuilder builder = request.evaluatePreconditions(feed.getUpdated(), this.computeETag(feed));
        if (builder != null) {
            return builder.build();
        }
        return Response.ok((Object)feed).lastModified(feed.getUpdated()).tag(this.computeETag(feed)).build();
    }

    @PUT
    @Consumes(value={"application/vnd.atl.plugins.audit.log.entries+json"})
    public Response fill(FillEntriesRepresentation fillEntriesRepresentation) {
        this.permissionEnforcer.enforcePermission(Permission.MANAGE_AUDIT_LOG);
        if (!Sys.isUpmDebugModeEnabled()) {
            return Response.status((Response.Status)Response.Status.PRECONDITION_FAILED).build();
        }
        this.auditLogService.purgeLog();
        for (String entry : fillEntriesRepresentation.getEntries()) {
            this.auditLogService.logI18nMessage(entry, new String[0]);
        }
        return Response.ok().build();
    }

    @GET
    @Path(value="purge-after")
    @Produces(value={"application/vnd.atl.plugins.audit.log.purge.after+json"})
    public Response getPurgeAfter() {
        this.permissionEnforcer.enforcePermission(Permission.GET_AUDIT_LOG);
        return Response.ok((Object)new PurgeAfterRepresentation(this.auditLogService.getPurgeAfter())).build();
    }

    @PUT
    @Path(value="purge-after")
    @Consumes(value={"application/vnd.atl.plugins.audit.log.purge.after+json"})
    public Response setPurgeAfter(PurgeAfterRepresentation purgeAfterRepresentation) {
        this.permissionEnforcer.enforcePermission(Permission.MANAGE_AUDIT_LOG);
        if (purgeAfterRepresentation.getPurgeAfter() <= 0 || purgeAfterRepresentation.getPurgeAfter() > 100000) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).type("application/vnd.atl.plugins.error+json").entity((Object)this.representationFactory.createI18nErrorRepresentation("upm.auditLog.error.invalid.purgeAfter")).build();
        }
        this.auditLogService.setPurgeAfter(purgeAfterRepresentation.getPurgeAfter());
        return Response.ok((Object)new PurgeAfterRepresentation(this.auditLogService.getPurgeAfter())).type("application/vnd.atl.plugins.audit.log.purge.after+json").build();
    }

    @GET
    @Path(value="max-entries")
    @Produces(value={"application/vnd.atl.plugins.audit.log.max.entries+json"})
    public Response getMaxEntries() {
        this.permissionEnforcer.enforcePermission(Permission.GET_AUDIT_LOG);
        return Response.ok((Object)new MaxEntriesRepresentation(this.auditLogService.getMaxEntries())).build();
    }

    @PUT
    @Path(value="max-entries")
    @Consumes(value={"application/vnd.atl.plugins.audit.log.max.entries+json"})
    public Response setMaxEntries(MaxEntriesRepresentation maxEntriesRepresentation) {
        this.permissionEnforcer.enforcePermission(Permission.MANAGE_AUDIT_LOG);
        if (maxEntriesRepresentation.getMaxEntries() < 0) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).type("application/vnd.atl.plugins.error+json").entity((Object)this.representationFactory.createI18nErrorRepresentation("upm.auditLog.error.invalid.maxEntries")).build();
        }
        this.auditLogService.setMaxEntries(maxEntriesRepresentation.getMaxEntries());
        return Response.ok((Object)new MaxEntriesRepresentation(this.auditLogService.getMaxEntries())).type("application/vnd.atl.plugins.audit.log.max.entries+json").build();
    }

    private EntityTag computeETag(Feed feed) {
        return this.computeETag(feed.getUpdated());
    }

    private EntityTag computeETag(Date date) {
        return new EntityTag(Long.toString(date.getTime()));
    }

    public static final class FillEntriesRepresentation {
        @JsonProperty
        private List<String> entries;

        @JsonCreator
        public FillEntriesRepresentation(@JsonProperty(value="entries") List<String> entries) {
            this.entries = Objects.requireNonNull(entries, "entries");
        }

        public List<String> getEntries() {
            return this.entries;
        }
    }

    public static final class MaxEntriesRepresentation {
        @JsonProperty
        private int maxEntries;

        @JsonCreator
        public MaxEntriesRepresentation(@JsonProperty(value="maxEntries") int maxEntries) {
            this.maxEntries = maxEntries;
        }

        public int getMaxEntries() {
            return this.maxEntries;
        }
    }

    public static final class PurgeAfterRepresentation {
        @JsonProperty
        private int purgeAfter;

        @JsonCreator
        public PurgeAfterRepresentation(@JsonProperty(value="purgeAfter") int purgeAfter) {
            this.purgeAfter = purgeAfter;
        }

        public int getPurgeAfter() {
            return this.purgeAfter;
        }
    }
}

