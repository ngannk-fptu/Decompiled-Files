/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.retention.SpaceRetentionPolicy
 *  com.atlassian.confluence.api.service.exceptions.NotFoundException
 *  com.atlassian.confluence.api.service.exceptions.PermissionException
 *  com.atlassian.confluence.api.service.retention.RetentionFeatureChecker
 *  com.atlassian.confluence.retention.SpaceRetentionPolicyService
 *  com.atlassian.confluence.search.v2.SearchResults
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.GET
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.retentionrules.rest;

import com.atlassian.confluence.api.model.retention.SpaceRetentionPolicy;
import com.atlassian.confluence.api.service.exceptions.NotFoundException;
import com.atlassian.confluence.api.service.exceptions.PermissionException;
import com.atlassian.confluence.api.service.retention.RetentionFeatureChecker;
import com.atlassian.confluence.plugins.retentionrules.impl.service.SearchService;
import com.atlassian.confluence.plugins.retentionrules.rest.model.SpaceSearchResult;
import com.atlassian.confluence.retention.SpaceRetentionPolicyService;
import com.atlassian.confluence.search.v2.SearchResults;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Supplier;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path(value="/space")
public class SpaceRetentionPolicyResource {
    private final Logger logger = LoggerFactory.getLogger(SpaceRetentionPolicyResource.class);
    private final SpaceRetentionPolicyService spaceRetentionPolicyService;
    private final RetentionFeatureChecker featureChecker;
    private final SearchService searchService;

    public SpaceRetentionPolicyResource(@ComponentImport SpaceRetentionPolicyService spaceRetentionPolicyService, @ComponentImport RetentionFeatureChecker featureChecker, SearchService searchService) {
        this.spaceRetentionPolicyService = Objects.requireNonNull(spaceRetentionPolicyService);
        this.featureChecker = Objects.requireNonNull(featureChecker);
        this.searchService = searchService;
    }

    @PUT
    @Path(value="/{spaceKey}")
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    public Response setSpaceRetentionPolicy(@PathParam(value="spaceKey") String spaceKey, SpaceRetentionPolicy policy) {
        return this.withFeatureChecking(() -> this.withExceptionHandling("Failed to save Space Retention rules.", () -> {
            List validations = policy.validate();
            if (!validations.isEmpty()) {
                return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)validations).build();
            }
            this.spaceRetentionPolicyService.savePolicy(spaceKey, policy);
            Optional newPolicy = this.spaceRetentionPolicyService.getPolicy(spaceKey);
            if (newPolicy.isPresent()) {
                return Response.ok(newPolicy.get()).build();
            }
            return Response.serverError().build();
        }));
    }

    @DELETE
    @Path(value="/{spaceKey}")
    public Response deleteSpaceRetentionPolicy(@PathParam(value="spaceKey") String spaceKey) {
        return this.withFeatureChecking(() -> this.withExceptionHandling("Failed to delete Space Retention rules.", () -> {
            this.spaceRetentionPolicyService.deletePolicy(spaceKey);
            return Response.noContent().build();
        }));
    }

    @GET
    @Path(value="/search/policy")
    @Produces(value={"application/json"})
    public Response searchSpacesWithRetentionPolicies(@QueryParam(value="space") String titleOrKey) {
        return this.withFeatureChecking(() -> this.withExceptionHandling("Failed to search spaces with retention rules.", () -> Response.ok((Object)this.toResponse(() -> this.searchService.spaces(titleOrKey, true))).build()));
    }

    @GET
    @Path(value="/search/all")
    @Produces(value={"application/json"})
    public Response searchSpaces(@QueryParam(value="space") String titleOrKey) {
        return this.withFeatureChecking(() -> this.withExceptionHandling("Failed to search spaces.", () -> Response.ok((Object)this.enrich(this.toResponse(() -> this.searchService.spaces(titleOrKey, false)))).build()));
    }

    private SpaceSearchResult toResponse(Callable<SearchResults> searchResultsCallable) throws Exception {
        return SpaceSearchResult.from(searchResultsCallable);
    }

    private SpaceSearchResult enrich(SpaceSearchResult result) {
        result.getResults().forEach(spaceDescriptor -> spaceDescriptor.appendSpaceRetentionPolicy(this.spaceRetentionPolicyService.getPolicy(spaceDescriptor.getSpace().get("key").toString()).orElse(null)));
        return result;
    }

    private Response handleModifyNotAuthorizedException(PermissionException exception) {
        this.logger.debug("Failed to modify Space Retention rules.", (Throwable)exception);
        return Response.status((int)401).build();
    }

    private Response handleNotFoundException(@Nullable NotFoundException exception) {
        if (exception != null) {
            this.logger.debug("Failed to retrieve Space Retention rules.", (Throwable)exception);
        }
        return Response.status((int)404).build();
    }

    private Response withFeatureChecking(Supplier<Response> responseSupplier) {
        if (!this.featureChecker.isFeatureAvailable()) {
            return this.handleNotFoundException(null);
        }
        return responseSupplier.get();
    }

    private Response withExceptionHandling(String generalExceptionMessage, Callable<Response> action) {
        try {
            return action.call();
        }
        catch (PermissionException e) {
            return this.handleModifyNotAuthorizedException(e);
        }
        catch (NotFoundException e) {
            return this.handleNotFoundException(e);
        }
        catch (Exception e) {
            this.logger.error(generalExceptionMessage, (Throwable)e);
            return Response.serverError().build();
        }
    }
}

