/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.security.jersey.AdminOnlyResourceFilter
 *  com.sun.jersey.spi.container.ResourceFilters
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.slf4j.Logger
 *  org.springframework.web.bind.annotation.RequestBody
 */
package com.atlassian.migration.agent.rest;

import com.atlassian.migration.agent.dto.MigrationSettingsDto;
import com.atlassian.migration.agent.json.JsonSerializingException;
import com.atlassian.migration.agent.logging.ContextLoggerFactory;
import com.atlassian.migration.agent.rest.MessageDto;
import com.atlassian.migration.agent.service.impl.CloudSettingsException;
import com.atlassian.migration.agent.service.impl.MigrationSettingsService;
import com.atlassian.plugins.rest.common.security.jersey.AdminOnlyResourceFilter;
import com.sun.jersey.spi.container.ResourceFilters;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.RequestBody;

@ParametersAreNonnullByDefault
@ResourceFilters(value={AdminOnlyResourceFilter.class})
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
@Path(value="settings")
public class MigrationSettingsResource {
    private final MigrationSettingsService migrationSettingsService;
    private static final Logger log = ContextLoggerFactory.getLogger(MigrationSettingsResource.class);

    public MigrationSettingsResource(MigrationSettingsService migrationSettingsService) {
        this.migrationSettingsService = migrationSettingsService;
    }

    @GET
    public Response getSettings(@QueryParam(value="type") @Nullable String type) {
        try {
            MigrationSettingsDto migrationSettingsDto = this.migrationSettingsService.getSettings(type);
            return Response.ok((Object)migrationSettingsDto).build();
        }
        catch (JsonSerializingException | IllegalArgumentException e) {
            log.warn("Failed to get settings", (Throwable)e);
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)new MessageDto(e.getMessage())).build();
        }
    }

    @PUT
    public Response updateSettings(@RequestBody MigrationSettingsDto migrationSettingsDto) {
        try {
            this.migrationSettingsService.updateSettings(migrationSettingsDto);
            return Response.ok().build();
        }
        catch (JsonSerializingException | CloudSettingsException | IllegalArgumentException e) {
            log.warn("Failed to update settings", (Throwable)e);
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)new MessageDto(e.getMessage())).build();
        }
    }
}

