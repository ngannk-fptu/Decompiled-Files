/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.compat.api.service.accessmode.ReadOnlyAccessAllowed
 *  com.atlassian.plugins.rest.common.multipart.FilePart
 *  com.atlassian.plugins.rest.common.multipart.MultipartFormParam
 *  com.atlassian.plugins.rest.common.security.jersey.AdminOnlyResourceFilter
 *  com.sun.jersey.spi.container.ResourceFilters
 *  javax.annotation.ParametersAreNonnullByDefault
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.WebApplicationException
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.migration.agent.rest;

import com.atlassian.confluence.compat.api.service.accessmode.ReadOnlyAccessAllowed;
import com.atlassian.migration.agent.dto.UserDomainRuleDto;
import com.atlassian.migration.agent.dto.UserDomainRulesetDto;
import com.atlassian.migration.agent.dto.util.UserDomainsDto;
import com.atlassian.migration.agent.rest.ContainerTokenValidator;
import com.atlassian.migration.agent.service.email.CsvDomainUploadStatus;
import com.atlassian.migration.agent.service.impl.BlockedDomainService;
import com.atlassian.migration.agent.service.impl.TrustedDomainCsvReaderService;
import com.atlassian.migration.agent.service.impl.TrustedDomainCsvWriterService;
import com.atlassian.migration.agent.service.impl.TrustedDomainsCsvZipException;
import com.atlassian.migration.agent.service.impl.UserDomainService;
import com.atlassian.plugins.rest.common.multipart.FilePart;
import com.atlassian.plugins.rest.common.multipart.MultipartFormParam;
import com.atlassian.plugins.rest.common.security.jersey.AdminOnlyResourceFilter;
import com.sun.jersey.spi.container.ResourceFilters;
import java.io.ByteArrayOutputStream;
import java.time.Instant;
import java.util.regex.Pattern;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@ParametersAreNonnullByDefault
@Path(value="email")
@ReadOnlyAccessAllowed
@ResourceFilters(value={AdminOnlyResourceFilter.class})
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
public class EmailResource {
    private final UserDomainService userDomainService;
    private final TrustedDomainCsvReaderService trustedDomainCsvReaderService;
    private final TrustedDomainCsvWriterService trustedDomainCsvWriterService;
    private final BlockedDomainService blockedDomainService;
    private final ContainerTokenValidator containerTokenValidator;

    public EmailResource(UserDomainService userDomainService, TrustedDomainCsvReaderService trustedDomainCsvReaderService, TrustedDomainCsvWriterService trustedDomainCsvWriterService, BlockedDomainService blockedDomainService, ContainerTokenValidator containerTokenValidator) {
        this.userDomainService = userDomainService;
        this.trustedDomainCsvReaderService = trustedDomainCsvReaderService;
        this.trustedDomainCsvWriterService = trustedDomainCsvWriterService;
        this.blockedDomainService = blockedDomainService;
        this.containerTokenValidator = containerTokenValidator;
    }

    @GET
    @Path(value="/domains")
    public Response getUserDomains() {
        return Response.ok((Object)new UserDomainsDto(this.userDomainService.getUserDomainCounts())).build();
    }

    @GET
    @Produces(value={"application/zip"})
    @Path(value="/download/domains/{cloudId}")
    public Response getUserDomainsCSV(@PathParam(value="cloudId") String cloudId) {
        Pattern pattern = Pattern.compile("[a-zA-Z0-9-]+");
        if (!pattern.matcher(cloudId).matches()) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).build();
        }
        long filenameTimestamp = Instant.now().toEpochMilli();
        ByteArrayOutputStream inMemoryOutputStream = new ByteArrayOutputStream();
        try {
            this.trustedDomainCsvWriterService.writeDomainsCsvZip(inMemoryOutputStream, cloudId, filenameTimestamp);
        }
        catch (TrustedDomainsCsvZipException e) {
            switch (e.type) {
                case INVALID_CLOUD_ID: {
                    return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)TrustedDomainsCsvZipException.Type.INVALID_CLOUD_ID.message).build();
                }
                case COULD_NOT_CREATE_CSV: {
                    return Response.status((Response.Status)Response.Status.INTERNAL_SERVER_ERROR).entity((Object)TrustedDomainsCsvZipException.Type.COULD_NOT_CREATE_CSV.message).build();
                }
                case COULD_NOT_WRITE_CSV: {
                    return Response.status((Response.Status)Response.Status.INTERNAL_SERVER_ERROR).entity((Object)TrustedDomainsCsvZipException.Type.COULD_NOT_WRITE_CSV.message).build();
                }
                case COULD_NOT_WRITE_ZIP: {
                    return Response.status((Response.Status)Response.Status.INTERNAL_SERVER_ERROR).entity((Object)TrustedDomainsCsvZipException.Type.COULD_NOT_WRITE_ZIP.message).build();
                }
            }
            throw new IllegalStateException("Unexpected value: " + (Object)((Object)e.type));
        }
        return Response.ok(inMemoryOutputStream::writeTo).type("application/zip").header("Content-disposition", (Object)("attachment; filename=email-domains-" + filenameTimestamp + ".zip")).build();
    }

    @GET
    @Path(value="/domain-rules")
    public Response getDomainRuleset() {
        UserDomainRulesetDto ruleset = this.userDomainService.getDomainRules();
        return Response.ok((Object)ruleset).build();
    }

    @PUT
    @Path(value="/domain-rules")
    public Response updateDomainRule(UserDomainRuleDto rule) {
        this.userDomainService.upsertDomainRule(rule);
        return Response.ok().build();
    }

    @DELETE
    @Path(value="/domain-rules/{domain}")
    public Response deleteDomainRule(@PathParam(value="domain") String domain) {
        this.userDomainService.deleteDomainRule(domain);
        return Response.ok().build();
    }

    @DELETE
    @Path(value="/rules")
    public Response deleteAllUserModifiedDomainRules() {
        this.userDomainService.deleteAllUserModifiedDomainRules();
        return Response.ok().build();
    }

    @POST
    @Path(value="/upload/domains")
    @Consumes(value={"multipart/form-data"})
    public Response uploadDomainsFile(@MultipartFormParam(value="file") FilePart filePart) {
        CsvDomainUploadStatus status = this.trustedDomainCsvReaderService.processDomainsCsv(filePart);
        switch (status.getResult()) {
            case SUCCESS: {
                return Response.ok((Object)"File upload successful. All records are valid.").build();
            }
            case RECORD_FAILED: 
            case RECORD_VALIDATION_FAILED: {
                return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)status.getErrorMessage()).build();
            }
        }
        return Response.status((Response.Status)Response.Status.INTERNAL_SERVER_ERROR).entity((Object)status.getErrorMessage()).build();
    }

    @GET
    @Path(value="/blocked-domains/{cloudId}")
    public Response getBlockedDomains(@PathParam(value="cloudId") String cloudId) {
        this.containerTokenValidator.validateContainerToken(cloudId).toResponseWhenNotValid().ifPresent(e -> {
            throw new WebApplicationException(e);
        });
        return Response.ok(this.blockedDomainService.getBlockedDomains(cloudId)).build();
    }
}

