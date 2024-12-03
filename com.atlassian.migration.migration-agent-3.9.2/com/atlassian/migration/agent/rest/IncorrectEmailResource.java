/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.security.jersey.AdminOnlyResourceFilter
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Strings
 *  com.sun.jersey.spi.container.ResourceFilters
 *  javax.annotation.ParametersAreNonnullByDefault
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.WebApplicationException
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.migration.agent.rest;

import com.atlassian.migration.MigrationDarkFeaturesManager;
import com.atlassian.migration.agent.dto.DuplicateEmailsConfigDto;
import com.atlassian.migration.agent.dto.InvalidEmailsConfigDto;
import com.atlassian.migration.agent.entity.SortOrder;
import com.atlassian.migration.agent.entity.UserBaseScanSortKey;
import com.atlassian.migration.agent.rest.ContainerTokenValidator;
import com.atlassian.migration.agent.service.email.GlobalEmailFixesConfigService;
import com.atlassian.migration.agent.service.email.IncorrectEmailResponse;
import com.atlassian.migration.agent.service.email.IncorrectEmailService;
import com.atlassian.plugins.rest.common.security.jersey.AdminOnlyResourceFilter;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.sun.jersey.spi.container.ResourceFilters;
import java.util.Optional;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@ParametersAreNonnullByDefault
@Path(value="incorrect-email")
@ResourceFilters(value={AdminOnlyResourceFilter.class})
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
public class IncorrectEmailResource {
    private final GlobalEmailFixesConfigService globalEmailFixesConfigService;
    private final IncorrectEmailService incorrectEmailService;
    private final MigrationDarkFeaturesManager migrationDarkFeaturesManager;
    private final ContainerTokenValidator containerTokenValidator;

    public IncorrectEmailResource(GlobalEmailFixesConfigService globalEmailFixesConfigService, IncorrectEmailService incorrectEmailService, MigrationDarkFeaturesManager migrationDarkFeaturesManager, ContainerTokenValidator containerTokenValidator) {
        this.globalEmailFixesConfigService = globalEmailFixesConfigService;
        this.incorrectEmailService = incorrectEmailService;
        this.migrationDarkFeaturesManager = migrationDarkFeaturesManager;
        this.containerTokenValidator = containerTokenValidator;
    }

    @GET
    @Path(value="/duplicated/config")
    public Response getDuplicateEmailConfig() {
        if (this.isGlobalEmailFixesDisabled()) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
        }
        return Response.ok((Object)this.globalEmailFixesConfigService.getDuplicateEmailsConfig()).build();
    }

    @POST
    @Path(value="/duplicated/config")
    public Response saveDuplicateEmailConfig(DuplicateEmailsConfigDto duplicateEmailsConfigDto) {
        if (this.isGlobalEmailFixesDisabled()) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
        }
        this.globalEmailFixesConfigService.saveDuplicateEmailsConfig(duplicateEmailsConfigDto);
        return Response.ok().build();
    }

    @GET
    @Path(value="/invalid/config")
    public Response getInvalidEmailConfig() {
        if (this.isGlobalEmailFixesDisabled()) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
        }
        return Response.ok((Object)this.globalEmailFixesConfigService.getInvalidEmailsConfig()).build();
    }

    @POST
    @Path(value="/invalid/config")
    public Response saveInvalidEmailConfig(InvalidEmailsConfigDto invalidEmailsConfigDto) {
        if (this.isGlobalEmailFixesDisabled()) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
        }
        this.globalEmailFixesConfigService.saveInvalidEmailsConfig(invalidEmailsConfigDto);
        return Response.ok().build();
    }

    @GET
    @Path(value="/invalid/{cloudId}")
    public Response getInvalidEmails(@PathParam(value="cloudId") String cloudId, @QueryParam(value="userBaseScanId") String userBaseScanId, @QueryParam(value="page") @DefaultValue(value="1") Integer page, @QueryParam(value="limit") @DefaultValue(value="10") Integer limit, @QueryParam(value="sortKey") @DefaultValue(value="USERNAME") UserBaseScanSortKey sortKey, @QueryParam(value="sortOrder") @DefaultValue(value="ASC") SortOrder sortOrder) {
        if (this.isGlobalEmailFixesDisabled()) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
        }
        this.validateParameters(cloudId, userBaseScanId, page, limit);
        Optional<IncorrectEmailResponse> emails = this.incorrectEmailService.getInvalidEmails(userBaseScanId, cloudId, page, limit, sortKey, sortOrder);
        return emails.isPresent() ? Response.ok((Object)emails.get()).build() : Response.status((Response.Status)Response.Status.NOT_FOUND).build();
    }

    @GET
    @Path(value="/duplicated/{cloudId}")
    public Response getDuplicateEmails(@PathParam(value="cloudId") String cloudId, @QueryParam(value="userBaseScanId") String userBaseScanId, @QueryParam(value="page") @DefaultValue(value="1") Integer page, @QueryParam(value="limit") @DefaultValue(value="10") Integer limit, @QueryParam(value="sortKey") @DefaultValue(value="USERNAME") UserBaseScanSortKey sortKey, @QueryParam(value="sortOrder") @DefaultValue(value="ASC") SortOrder sortOrder) {
        if (this.isGlobalEmailFixesDisabled()) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
        }
        this.validateParameters(cloudId, userBaseScanId, page, limit);
        Optional<IncorrectEmailResponse> emails = this.incorrectEmailService.getDuplicateEmails(userBaseScanId, cloudId, page, limit, sortKey, sortOrder);
        return emails.isPresent() ? Response.ok((Object)emails.get()).build() : Response.status((Response.Status)Response.Status.NOT_FOUND).build();
    }

    private boolean isGlobalEmailFixesDisabled() {
        return !this.migrationDarkFeaturesManager.shouldHandleGlobalEmailFixes();
    }

    private void validateParameters(String cloudId, String userBaseScanId, Integer page, Integer limit) {
        this.containerTokenValidator.validateContainerToken(cloudId).toResponseWhenNotValid().ifPresent(e -> {
            throw new WebApplicationException(e);
        });
        try {
            Preconditions.checkArgument((!Strings.isNullOrEmpty((String)userBaseScanId) ? 1 : 0) != 0, (Object)"userBaseScanId must be set");
            Preconditions.checkArgument((page > 0 ? 1 : 0) != 0, (Object)"page must be a positive integer");
            Preconditions.checkArgument((limit > 0 && limit <= 100 ? 1 : 0) != 0, (Object)"limit must be a number between 1 and 100");
        }
        catch (IllegalArgumentException e2) {
            throw new WebApplicationException((Throwable)e2, Response.Status.BAD_REQUEST);
        }
    }
}

