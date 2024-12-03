/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.retention.GlobalRetentionPolicy
 *  com.atlassian.confluence.api.service.exceptions.NotFoundException
 *  com.atlassian.confluence.api.service.exceptions.PermissionException
 *  com.atlassian.confluence.api.service.retention.RetentionFeatureChecker
 *  com.atlassian.confluence.retention.GlobalRetentionPolicyService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.retentionrules.rest;

import com.atlassian.confluence.api.model.retention.GlobalRetentionPolicy;
import com.atlassian.confluence.api.service.exceptions.NotFoundException;
import com.atlassian.confluence.api.service.exceptions.PermissionException;
import com.atlassian.confluence.api.service.retention.RetentionFeatureChecker;
import com.atlassian.confluence.retention.GlobalRetentionPolicyService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.List;
import java.util.Objects;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path(value="/")
public class GlobalRetentionPolicyResource {
    private final Logger logger = LoggerFactory.getLogger(GlobalRetentionPolicyResource.class);
    private final GlobalRetentionPolicyService retentionRulesSettingsService;
    private final RetentionFeatureChecker featureChecker;

    public GlobalRetentionPolicyResource(@ComponentImport GlobalRetentionPolicyService retentionRulesSettingsService, @ComponentImport RetentionFeatureChecker featureChecker) {
        this.retentionRulesSettingsService = Objects.requireNonNull(retentionRulesSettingsService);
        this.featureChecker = Objects.requireNonNull(featureChecker);
    }

    @GET
    @Produces(value={"application/json"})
    public Response getGlobalRetentionPolicy() {
        if (!this.featureChecker.isFeatureAvailable()) {
            return this.notFound();
        }
        try {
            return Response.ok((Object)this.retentionRulesSettingsService.getPolicy()).build();
        }
        catch (PermissionException ex) {
            this.logger.debug("Permission error", (Throwable)ex);
            return Response.status((Response.Status)Response.Status.FORBIDDEN).build();
        }
        catch (NotFoundException nfe) {
            return this.notFound();
        }
        catch (Exception e) {
            this.logger.error("Failed to retrieve Global Retention rules.", (Throwable)e);
            return Response.serverError().build();
        }
    }

    @PUT
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    public Response setGlobalRetentionPolicy(GlobalRetentionPolicy policy) {
        if (!this.featureChecker.isFeatureAvailable()) {
            return this.notFound();
        }
        try {
            List validations = policy.validate();
            if (validations.size() > 0) {
                return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)validations).build();
            }
            this.retentionRulesSettingsService.savePolicy(policy);
            return Response.ok((Object)policy).build();
        }
        catch (PermissionException ex) {
            this.logger.debug("Permission error", (Throwable)ex);
            return Response.status((Response.Status)Response.Status.FORBIDDEN).build();
        }
        catch (Exception e) {
            this.logger.error("Failed to save Global Retention rules.", (Throwable)e);
            return Response.serverError().build();
        }
    }

    private Response notFound() {
        return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
    }
}

