/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.ChronoUnitCaseInsensitive
 *  com.atlassian.confluence.api.model.JodaDate
 *  com.atlassian.confluence.api.model.audit.AuditRecord
 *  com.atlassian.confluence.api.model.audit.RetentionPeriod
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessAllowed
 *  com.atlassian.confluence.api.service.audit.AuditService
 *  com.atlassian.confluence.rest.api.model.RestList
 *  com.atlassian.confluence.rest.api.model.RestPageRequest
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.rest.common.security.jersey.AdminOnlyResourceFilter
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Preconditions
 *  com.sun.jersey.core.header.ContentDisposition
 *  com.sun.jersey.spi.container.ResourceFilters
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.CacheControl
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.UriInfo
 */
package com.atlassian.confluence.plugins.restapi.resources;

import com.atlassian.confluence.api.model.ChronoUnitCaseInsensitive;
import com.atlassian.confluence.api.model.JodaDate;
import com.atlassian.confluence.api.model.audit.AuditRecord;
import com.atlassian.confluence.api.model.audit.RetentionPeriod;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessAllowed;
import com.atlassian.confluence.api.service.audit.AuditService;
import com.atlassian.confluence.rest.api.model.RestList;
import com.atlassian.confluence.rest.api.model.RestPageRequest;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.jersey.AdminOnlyResourceFilter;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.sun.jersey.core.header.ContentDisposition;
import com.sun.jersey.spi.container.ResourceFilters;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@ResourceFilters(value={AdminOnlyResourceFilter.class})
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
@Path(value="/audit")
@ReadOnlyAccessAllowed
@Deprecated
public class AuditResource {
    private final AuditService auditService;
    @VisibleForTesting
    Supplier<Instant> nowSupplier;

    public AuditResource(@ComponentImport AuditService auditService) {
        Preconditions.checkNotNull((Object)auditService, (Object)"auditService must not be null");
        this.auditService = auditService;
        this.nowSupplier = Instant::now;
    }

    @GET
    @Path(value="/since")
    public List<AuditRecord> getAuditRecords(@QueryParam(value="number") @DefaultValue(value="3") long number, @QueryParam(value="units") ChronoUnitCaseInsensitive units, @QueryParam(value="start") int start, @QueryParam(value="limit") @DefaultValue(value="1000") int limit, @QueryParam(value="searchString") String searchString, @Context UriInfo uriInfo) {
        ChronoUnit chronoUnit = units == null ? ChronoUnit.MONTHS : units.getChronoUnit();
        Instant endDate = this.nowSupplier.get();
        Instant startDate = endDate.minus(chronoUnit.getDuration().multipliedBy(number));
        return this.fetchRecords(start, limit, startDate, endDate, searchString, uriInfo);
    }

    @GET
    @Path(value="/retention")
    public RetentionPeriod getRetentionPeriod(@Context UriInfo uriInfo) {
        return this.auditService.getRetentionPeriod();
    }

    @PUT
    @Path(value="/retention")
    public RetentionPeriod setRetentionPeriod(RetentionPeriod retentionPeriod, @Context UriInfo uriInfo) {
        return this.auditService.setRetentionPeriod(retentionPeriod);
    }

    @GET
    public List<AuditRecord> getAuditRecords(@QueryParam(value="startDate") JodaDate startDate, @QueryParam(value="endDate") JodaDate endDate, @QueryParam(value="start") int start, @QueryParam(value="limit") @DefaultValue(value="1000") int limit, @QueryParam(value="searchString") String searchString, @Context UriInfo uriInfo) {
        Instant endTime = this.getInstant(endDate, this.nowSupplier.get());
        Instant startTime = this.getInstant(startDate, Instant.EPOCH);
        return this.fetchRecords(start, limit, startTime, endTime, searchString, uriInfo);
    }

    @GET
    @Path(value="/export")
    @Produces(value={"application/zip", "text/csv"})
    public Response export(@QueryParam(value="startDate") JodaDate startDate, @QueryParam(value="endDate") JodaDate endDate, @QueryParam(value="searchString") String searchString, @QueryParam(value="format") @DefaultValue(value="csv") String format, @Context UriInfo uriInfo) {
        Instant now = this.nowSupplier.get();
        Instant endTime = this.getInstant(endDate, now);
        Instant startTime = this.getInstant(startDate, Instant.EPOCH);
        ContentDisposition contentDisposition = ContentDisposition.type((String)"attachment").fileName(String.format("Auditing Export %s.csv", now)).creationDate(new Date(now.toEpochMilli())).build();
        CacheControl cacheControl = new CacheControl();
        cacheControl.setPrivate(true);
        cacheControl.setMustRevalidate(true);
        return Response.ok(output -> this.auditService.exportCSV().withFinder(this.auditService.getRecords(startTime, endTime).withSearchString(searchString)).write(output)).header("Content-Disposition", (Object)contentDisposition).cacheControl(cacheControl).build();
    }

    private List<AuditRecord> fetchRecords(int start, int limit, Instant startDate, Instant endDate, String searchString, UriInfo uriInfo) {
        RestPageRequest request = new RestPageRequest(uriInfo, start, limit);
        PageResponse auditRecords = this.auditService.getRecords(startDate, endDate).withSearchString(searchString).fetchMany((PageRequest)request);
        return RestList.newRestList((PageResponse)auditRecords).pageRequest((PageRequest)request).build();
    }

    @POST
    public AuditRecord storeRecord(AuditRecord record, @Context UriInfo uriInfo) {
        return this.auditService.storeRecord(record);
    }

    private Instant getInstant(JodaDate date, Instant fallback) {
        return date == null ? fallback : Instant.ofEpochMilli(date.getDateTime().getMillis());
    }
}

