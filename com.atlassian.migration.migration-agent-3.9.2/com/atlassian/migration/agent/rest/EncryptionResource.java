/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.compat.api.service.accessmode.ReadOnlyAccessAllowed
 *  com.atlassian.plugins.rest.common.security.jersey.AdminOnlyResourceFilter
 *  com.sun.jersey.spi.container.ResourceFilters
 *  javax.annotation.ParametersAreNonnullByDefault
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.migration.agent.rest;

import com.atlassian.confluence.compat.api.service.accessmode.ReadOnlyAccessAllowed;
import com.atlassian.migration.MigrationDarkFeaturesManager;
import com.atlassian.migration.agent.rest.MessageDto;
import com.atlassian.migration.agent.service.encryption.EncryptionService;
import com.atlassian.migration.agent.service.encryption.exception.EncryptionException;
import com.atlassian.plugins.rest.common.security.jersey.AdminOnlyResourceFilter;
import com.sun.jersey.spi.container.ResourceFilters;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@ParametersAreNonnullByDefault
@Path(value="encryption")
@ReadOnlyAccessAllowed
@ResourceFilters(value={AdminOnlyResourceFilter.class})
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
public class EncryptionResource {
    private final EncryptionService encryptionService;
    private final MigrationDarkFeaturesManager migrationDarkFeaturesManager;

    EncryptionResource(EncryptionService encryptionService, MigrationDarkFeaturesManager migrationDarkFeaturesManager) {
        this.encryptionService = encryptionService;
        this.migrationDarkFeaturesManager = migrationDarkFeaturesManager;
    }

    @GET
    @Path(value="/validate")
    public Response validateEncryption() {
        if (!this.migrationDarkFeaturesManager.isTokenEncryptionEnabled()) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)new MessageDto("FedRAMP is not enabled")).build();
        }
        try {
            this.encryptionService.validateEncryption();
        }
        catch (EncryptionException e) {
            return Response.ok((Object)new MessageDto(e.getEncryptionErrorCode().toString())).build();
        }
        catch (Exception e) {
            return Response.status((Response.Status)Response.Status.INTERNAL_SERVER_ERROR).entity((Object)new MessageDto(e.getMessage())).build();
        }
        return Response.ok((Object)new MessageDto("VALID_SECRET_KEY")).build();
    }
}

