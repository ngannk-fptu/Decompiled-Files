/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.security.jersey.AdminOnlyResourceFilter
 *  com.google.common.collect.ImmutableMap
 *  com.sun.jersey.spi.container.ResourceFilters
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 */
package com.atlassian.migration.agent.rest;

import com.atlassian.migration.agent.logging.ContextLoggerFactory;
import com.atlassian.migration.agent.newexport.SpaceCSVExportTaskContext;
import com.atlassian.migration.agent.newexport.SpaceRapidExporter;
import com.atlassian.migration.agent.newexport.store.JdbcConfluenceStore;
import com.atlassian.migration.agent.service.stepexecutor.export.SpaceExportCacheService;
import com.atlassian.plugins.rest.common.security.jersey.AdminOnlyResourceFilter;
import com.google.common.collect.ImmutableMap;
import com.sun.jersey.spi.container.ResourceFilters;
import java.nio.file.AccessDeniedException;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

@Path(value="export")
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
@ResourceFilters(value={AdminOnlyResourceFilter.class})
public class ExportDebugResource {
    private static final Logger log = ContextLoggerFactory.getLogger(ExportDebugResource.class);
    private static final String VALID_SPACE_PATTERN = "^~?\\w+";
    private final SpaceRapidExporter rapidExporter;
    private final JdbcConfluenceStore confluenceStore;
    private final SpaceExportCacheService spaceExportCacheService;

    public ExportDebugResource(SpaceRapidExporter rapidExporter, JdbcConfluenceStore confluenceStore, SpaceExportCacheService spaceExportCacheService) {
        this.rapidExporter = rapidExporter;
        this.confluenceStore = confluenceStore;
        this.spaceExportCacheService = spaceExportCacheService;
    }

    @POST
    @Path(value="/new/{spaceKey}")
    public Response newExport(@PathParam(value="spaceKey") String spaceKey) throws AccessDeniedException {
        this.validateSpaceKey(spaceKey);
        log.info("Running new export for space {}", (Object)spaceKey);
        SpaceCSVExportTaskContext config = new SpaceCSVExportTaskContext(this.confluenceStore.getSpaceId(spaceKey), spaceKey, "cloudId", "debug-plan", "debug-task", "/tmp", false);
        String outputFile = this.rapidExporter.export(config);
        return Response.ok(this.responsePayloadMap(String.format("New export finished for space: %s", spaceKey), outputFile)).build();
    }

    @GET
    @Path(value="/cache/stale/{spaceKey}/{cloudId}")
    public Response getCacheEntry(@PathParam(value="spaceKey") String spaceKey, @PathParam(value="cloudId") String cloudId) {
        boolean result = this.spaceExportCacheService.debugCacheEntry(spaceKey, cloudId);
        return Response.ok((Object)result).build();
    }

    private Map<String, String> responsePayloadMap(String message, String file) {
        return ImmutableMap.of((Object)"message", (Object)message, (Object)"file", (Object)file);
    }

    private void validateSpaceKey(String spaceKey) {
        if (StringUtils.isEmpty((CharSequence)spaceKey) || !spaceKey.matches(VALID_SPACE_PATTERN)) {
            throw new IllegalArgumentException("Invalid space key");
        }
    }
}

