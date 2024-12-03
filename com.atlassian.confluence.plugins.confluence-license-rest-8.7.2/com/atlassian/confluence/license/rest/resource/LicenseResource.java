/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessAllowed
 *  com.atlassian.confluence.cluster.ClusterManager
 *  com.atlassian.confluence.license.LicenseService
 *  com.atlassian.confluence.license.LicenseWebFacade
 *  com.atlassian.confluence.license.exception.LicenseException
 *  com.atlassian.confluence.license.util.ConfluenceLicenseUtils
 *  com.atlassian.confluence.setup.BootstrapManager
 *  com.atlassian.confluence.util.UserChecker
 *  com.atlassian.confluence.util.db.DatabaseUtils
 *  com.atlassian.extras.api.confluence.ConfluenceLicense
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.rest.common.security.jersey.AdminOnlyResourceFilter
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.upm.api.license.DataCenterCrossgradeablePlugins
 *  com.sun.jersey.spi.container.ResourceFilters
 *  io.atlassian.fugue.Either
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.FormParam
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.license.rest.resource;

import com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessAllowed;
import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.license.LicenseService;
import com.atlassian.confluence.license.LicenseWebFacade;
import com.atlassian.confluence.license.exception.LicenseException;
import com.atlassian.confluence.license.rest.model.LicenseDetailsModel;
import com.atlassian.confluence.license.rest.model.UserCountResourceModel;
import com.atlassian.confluence.license.util.ConfluenceLicenseUtils;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.util.UserChecker;
import com.atlassian.confluence.util.db.DatabaseUtils;
import com.atlassian.extras.api.confluence.ConfluenceLicense;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.jersey.AdminOnlyResourceFilter;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.upm.api.license.DataCenterCrossgradeablePlugins;
import com.sun.jersey.spi.container.ResourceFilters;
import io.atlassian.fugue.Either;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.codehaus.jackson.annotate.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

@Consumes(value={"application/json"})
@Produces(value={"application/json"})
@Path(value="/license")
@ReadOnlyAccessAllowed
@ResourceFilters(value={AdminOnlyResourceFilter.class})
public class LicenseResource {
    private static final Logger log = LoggerFactory.getLogger(LicenseResource.class);
    private final LicenseWebFacade licenseWebFacade;
    private final BootstrapManager bootstrapManager;
    private final ClusterManager clusterManager;
    private final I18nResolver i18nResolver;
    private final LicenseService licenseService;
    private final UserChecker userChecker;
    private final DataCenterCrossgradeablePlugins dataCenterCrossgradeablePlugins;

    public LicenseResource(@ComponentImport @Qualifier(value="licenseWebFacade") LicenseWebFacade licenseWebFacade, @ComponentImport BootstrapManager bootstrapManager, @ComponentImport ClusterManager clusterManager, @ComponentImport I18nResolver i18nResolver, @ComponentImport LicenseService licenseService, @ComponentImport UserChecker userChecker, @ComponentImport DataCenterCrossgradeablePlugins dataCenterCrossgradeablePlugins) {
        this.bootstrapManager = bootstrapManager;
        this.licenseWebFacade = licenseWebFacade;
        this.clusterManager = clusterManager;
        this.i18nResolver = i18nResolver;
        this.licenseService = licenseService;
        this.userChecker = userChecker;
        this.dataCenterCrossgradeablePlugins = dataCenterCrossgradeablePlugins;
    }

    @GET
    @Path(value="/details")
    public Response getDetails() {
        try {
            ConfluenceLicense license = this.licenseService.retrieve();
            LicenseDetailsModel licenseDetails = LicenseDetailsModel.builder().licenseType(license.getLicenseType().name()).dataCenter(license.isClusteringEnabled()).subscription(license.isSubscription()).evaluation(license.isEvaluation()).expired(license.isExpired()).creationDate(license.getCreationDate()).purchaseDate(license.getPurchaseDate()).expiryDate(license.getExpiryDate()).maintenanceExpiryDate(license.getMaintenanceExpiryDate()).build();
            return Response.ok((Object)licenseDetails).build();
        }
        catch (LicenseException e) {
            log.error("License not found or cannot be decrypted.", (Throwable)e);
            return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
        }
    }

    @GET
    @Path(value="/organisation")
    public Response getOrganisation() {
        try {
            String org = this.licenseService.retrieve().getOrganisation().getName();
            return Response.ok((Object)org).build();
        }
        catch (LicenseException e) {
            log.error("License not found or cannot be decrypted.", (Throwable)e);
            return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
        }
    }

    @GET
    @Path(value="/userCount")
    public Response getUserCount() {
        Integer userCount = this.userChecker.getNumberOfRegisteredUsers();
        log.info("There are " + userCount + " users on this instance.");
        return Response.ok((Object)new UserCountResourceModel(userCount)).build();
    }

    @GET
    @Path(value="/remainingSeats")
    public Response getRemainingUserCount() {
        int userCount = this.userChecker.getNumberOfRegisteredUsers();
        try {
            ConfluenceLicense license = this.licenseService.retrieve();
            Integer remainingFreeSlots = license.isUnlimitedNumberOfUsers() ? Integer.MAX_VALUE : license.getMaximumNumberOfUsers() - userCount;
            log.info("There are " + remainingFreeSlots + " remaining free slots on this instance.");
            return Response.ok((Object)new UserCountResourceModel(remainingFreeSlots)).build();
        }
        catch (LicenseException e) {
            log.error("License not found or cannot be decrypted.", (Throwable)e);
            return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
        }
    }

    @GET
    @Path(value="/maxUsers")
    public Response getMaxUsers() {
        try {
            ConfluenceLicense license = this.licenseService.retrieve();
            Integer maxUsers = license.isUnlimitedNumberOfUsers() ? Integer.MAX_VALUE : license.getMaximumNumberOfUsers();
            log.info("This license allows for " + maxUsers + " users");
            return Response.ok((Object)new UserCountResourceModel(maxUsers)).build();
        }
        catch (LicenseException e) {
            log.error("License not found or cannot be decrypted.", (Throwable)e);
            return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
        }
    }

    @POST
    @Path(value="/validate")
    @Consumes(value={"application/x-www-form-urlencoded"})
    public Response validate(@FormParam(value="licenseKey") String licenseKey) {
        return (Response)this.licenseWebFacade.validateLicense(licenseKey).flatMap(this::validateDcEvaluationDb).fold(errorMessage -> Response.status((Response.Status)Response.Status.BAD_REQUEST).entity(errorMessage).build(), license -> Response.ok((Object)new ValidationResponse((ConfluenceLicense)license)).build());
    }

    private Either<String, ConfluenceLicense> validateDcEvaluationDb(ConfluenceLicense license) {
        if (ConfluenceLicenseUtils.isDataCenter((ConfluenceLicense)license) && DatabaseUtils.evaluationDatabaseName((BootstrapManager)this.bootstrapManager).isPresent()) {
            return Either.left((Object)this.i18nResolver.getText("setup.start.cluster.upgrade.cannot.start.embedded.db", new Serializable[]{(Serializable)DatabaseUtils.evaluationDatabaseName().get()}));
        }
        return Either.right((Object)license);
    }

    class ValidationResponse {
        private final List<String> crossgradeableApps = new ArrayList<String>();
        private final boolean evaluation;
        private final boolean clusteringEnabled;

        ValidationResponse(ConfluenceLicense license) {
            this.evaluation = license.isEvaluation();
            this.clusteringEnabled = license.isClusteringEnabled();
            if (this.clusteringEnabled && !LicenseResource.this.licenseService.isLicensedForDataCenter()) {
                LicenseResource.this.dataCenterCrossgradeablePlugins.getDataCenterLicenseCrossgradeablePlugins().forEach(a -> this.crossgradeableApps.add(a.getName()));
            }
        }

        @JsonProperty
        public List<String> getCrossgradeableApps() {
            return this.crossgradeableApps;
        }

        @JsonProperty
        public Boolean isEvaluation() {
            return this.evaluation;
        }

        @JsonProperty
        public boolean isClusteringEnabled() {
            return this.clusteringEnabled;
        }
    }
}

