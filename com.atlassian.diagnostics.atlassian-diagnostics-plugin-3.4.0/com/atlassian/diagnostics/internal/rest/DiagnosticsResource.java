/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.AlertCriteria
 *  com.atlassian.diagnostics.MonitoringService
 *  com.atlassian.diagnostics.PageCallback
 *  com.atlassian.diagnostics.PageRequest
 *  com.atlassian.diagnostics.Severity
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.permission.PermissionEnforcer
 *  com.sun.jersey.spi.resource.Singleton
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.WebApplicationException
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.UriBuilder
 *  javax.ws.rs.core.UriInfo
 *  org.apache.commons.lang3.StringUtils
 *  org.codehaus.jackson.JsonGenerator
 */
package com.atlassian.diagnostics.internal.rest;

import com.atlassian.diagnostics.AlertCriteria;
import com.atlassian.diagnostics.MonitoringService;
import com.atlassian.diagnostics.PageCallback;
import com.atlassian.diagnostics.PageRequest;
import com.atlassian.diagnostics.Severity;
import com.atlassian.diagnostics.internal.rest.AlertCountPageWritingCallback;
import com.atlassian.diagnostics.internal.rest.AlertPageWritingCallback;
import com.atlassian.diagnostics.internal.rest.AlertWithEllisionsPageWritingCallback;
import com.atlassian.diagnostics.internal.rest.StreamingJsonOutput;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.permission.PermissionEnforcer;
import com.sun.jersey.spi.resource.Singleton;
import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonGenerator;

@AnonymousAllowed
@Consumes(value={"application/json"})
@Path(value="/")
@Produces(value={"application/json;charset=UTF-8"})
@Singleton
public class DiagnosticsResource {
    private final I18nResolver i18nResolver;
    private final MonitoringService monitoringService;
    private final PermissionEnforcer permissionEnforcer;
    private final PluginAccessor pluginAccessor;

    @Nonnull
    private static Set<String> flatmapQueryParamValues(@Nullable Set<String> queryParamValues) {
        if (Objects.isNull(queryParamValues)) {
            return Collections.emptySet();
        }
        return queryParamValues.stream().map(value -> value.split("&")).flatMap(Arrays::stream).collect(Collectors.toSet());
    }

    @GET
    @Path(value="alerts")
    public Response getAlerts(@QueryParam(value="since") String sinceParam, @QueryParam(value="until") String untilParam, @QueryParam(value="componentId") Set<String> componentIds, @QueryParam(value="issueId") Set<String> issueIds, @QueryParam(value="node") Set<String> nodes, @QueryParam(value="pluginKey") Set<String> pluginKeys, @QueryParam(value="severity") Set<String> severities, final @QueryParam(value="start") @DefaultValue(value="0") int start, final @QueryParam(value="limit") @DefaultValue(value="50") int limit, final @Context UriInfo uriInfo) {
        this.permissionEnforcer.enforceSystemAdmin();
        final Instant stableUntil = DiagnosticsResource.getStableUntil(untilParam);
        final AlertCriteria criteria = DiagnosticsResource.toCriteria(sinceParam, stableUntil, DiagnosticsResource.flatmapQueryParamValues(componentIds), DiagnosticsResource.flatmapQueryParamValues(issueIds), DiagnosticsResource.flatmapQueryParamValues(nodes), DiagnosticsResource.flatmapQueryParamValues(pluginKeys), DiagnosticsResource.flatmapQueryParamValues(severities));
        return Response.ok((Object)new StreamingJsonOutput(){

            @Override
            protected void write(@Nonnull JsonGenerator generator) throws IOException, WebApplicationException {
                DiagnosticsResource.this.monitoringService.streamAlerts(criteria, (PageCallback)new AlertPageWritingCallback(generator, DiagnosticsResource.uriBuilderSupplier(uriInfo, stableUntil), new String[0]), DiagnosticsResource.toPageRequest(start, limit));
            }
        }).build();
    }

    @GET
    @Path(value="alerts/details/{issueId}/{pluginKey}")
    public Response getAlertDetails(@QueryParam(value="since") String sinceParam, @QueryParam(value="until") String untilParam, @PathParam(value="issueId") String issueId, @QueryParam(value="node") Set<String> nodes, @PathParam(value="pluginKey") String pluginKey, @QueryParam(value="severity") Set<String> severities, final @QueryParam(value="start") @DefaultValue(value="0") int start, final @QueryParam(value="limit") @DefaultValue(value="50") int limit, final @Context UriInfo uriInfo) {
        this.permissionEnforcer.enforceSystemAdmin();
        final Instant stableUntil = DiagnosticsResource.getStableUntil(untilParam);
        final AlertCriteria criteria = DiagnosticsResource.toCriteria(sinceParam, stableUntil, Collections.emptySet(), Collections.singleton(issueId), DiagnosticsResource.flatmapQueryParamValues(nodes), Collections.singleton(pluginKey), DiagnosticsResource.flatmapQueryParamValues(severities));
        return Response.ok((Object)new StreamingJsonOutput(){

            @Override
            protected void write(@Nonnull JsonGenerator generator) throws IOException, WebApplicationException {
                DiagnosticsResource.this.monitoringService.streamAlertsWithElisions(criteria, (PageCallback)new AlertWithEllisionsPageWritingCallback(generator, (Supplier<UriBuilder>)DiagnosticsResource.uriBuilderSupplier(uriInfo, stableUntil), DiagnosticsResource.this.i18nResolver, DiagnosticsResource.this.pluginAccessor), DiagnosticsResource.toPageRequest(start, limit));
            }
        }).build();
    }

    private static Instant getStableUntil(String paramValue) {
        Instant now = Instant.now();
        if (StringUtils.isBlank((CharSequence)paramValue)) {
            return now;
        }
        Instant instant = DiagnosticsResource.toInstantOrThrow(paramValue);
        return instant.isBefore(now) ? instant : now;
    }

    private static AlertCriteria toCriteria(String sinceParam, Instant until, Set<String> componentIds, Set<String> issueIds, Set<String> nodes, Set<String> pluginKeys, Set<String> severityNames) {
        Instant since = StringUtils.isEmpty((CharSequence)sinceParam) ? null : DiagnosticsResource.toInstantOrThrow(sinceParam);
        Set severities = severityNames.stream().filter(StringUtils::isNotBlank).map(value -> value.toUpperCase(Locale.ROOT)).map(Severity::valueOf).collect(Collectors.toSet());
        return AlertCriteria.builder().since(since).until(until).componentIds(componentIds).issueIds(issueIds).nodeNames(nodes).pluginKeys(pluginKeys).severities(severities).build();
    }

    private static Instant toInstantOrThrow(String value) {
        if (StringUtils.isNotBlank((CharSequence)value)) {
            try {
                long timestamp = Long.parseLong(value);
                if (timestamp >= 0L) {
                    return Instant.ofEpochMilli(timestamp);
                }
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }
        throw new IllegalArgumentException(value + " is not a valid timestamp");
    }

    private static PageRequest toPageRequest(int start, int limit) {
        return PageRequest.of((int)Math.max(0, start), (int)Math.max(1, limit));
    }

    private static Supplier<UriBuilder> uriBuilderSupplier(UriInfo uriInfo, Instant stableUntil) {
        return () -> uriInfo.getRequestUriBuilder().replaceQueryParam("until", new Object[]{stableUntil.toEpochMilli()});
    }

    @GET
    @Path(value="reports/alerts")
    public Response getAlertReport(@QueryParam(value="since") String sinceParam, @QueryParam(value="until") String untilParam, @QueryParam(value="componentId") Set<String> componentIds, @QueryParam(value="issueId") Set<String> issueIds, @QueryParam(value="node") Set<String> nodes, @QueryParam(value="pluginKey") Set<String> pluginKeys, @QueryParam(value="severity") Set<String> severities, final @QueryParam(value="start") @DefaultValue(value="0") int start, final @QueryParam(value="limit") @DefaultValue(value="50") int limit, final @Context UriInfo uriInfo) {
        this.permissionEnforcer.enforceSystemAdmin();
        final Instant stableUntil = DiagnosticsResource.getStableUntil(untilParam);
        final AlertCriteria criteria = DiagnosticsResource.toCriteria(sinceParam, stableUntil, DiagnosticsResource.flatmapQueryParamValues(componentIds), DiagnosticsResource.flatmapQueryParamValues(issueIds), DiagnosticsResource.flatmapQueryParamValues(nodes), DiagnosticsResource.flatmapQueryParamValues(pluginKeys), DiagnosticsResource.flatmapQueryParamValues(severities));
        return Response.ok((Object)new StreamingJsonOutput(){

            @Override
            protected void write(@Nonnull JsonGenerator generator) throws IOException, WebApplicationException {
                DiagnosticsResource.this.monitoringService.streamAlertCounts(criteria, (PageCallback)new AlertCountPageWritingCallback(generator, DiagnosticsResource.uriBuilderSupplier(uriInfo, stableUntil)), DiagnosticsResource.toPageRequest(start, limit));
            }
        }).build();
    }

    public DiagnosticsResource(I18nResolver i18nResolver, MonitoringService monitoringService, PermissionEnforcer permissionEnforcer, PluginAccessor pluginAccessor) {
        this.i18nResolver = i18nResolver;
        this.monitoringService = monitoringService;
        this.permissionEnforcer = permissionEnforcer;
        this.pluginAccessor = pluginAccessor;
    }
}

