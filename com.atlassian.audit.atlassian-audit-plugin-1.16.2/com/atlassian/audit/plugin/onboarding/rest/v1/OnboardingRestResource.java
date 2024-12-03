/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.SecurityContext
 */
package com.atlassian.audit.plugin.onboarding.rest.v1;

import com.atlassian.audit.plugin.onboarding.OnboardingSeenService;
import com.atlassian.audit.rest.model.ResponseErrorJson;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@OpenAPIDefinition(info=@Info(title="Audit Onboarding", version="1.0.0", description="This is the onboarding API. The root path is /rest/auditing/1.0"))
@Path(value="/onboarding")
@Produces(value={"application/json"})
@Consumes(value={"application/json"})
public class OnboardingRestResource {
    private final OnboardingSeenService onboardingSeenService;

    public OnboardingRestResource(OnboardingSeenService onboardingSeenService) {
        this.onboardingSeenService = onboardingSeenService;
    }

    @PUT
    @Path(value="/dismiss")
    @Operation(summary="Dismiss the onboarding so it will not show again in any version in the future.", tags={"audit", "onboarding"})
    @ApiResponses(value={@ApiResponse(responseCode="200", description="Successful operation"), @ApiResponse(responseCode="400", description="Bad request", content={@Content(array=@ArraySchema(schema=@Schema(implementation=ResponseErrorJson.class)))}), @ApiResponse(responseCode="401", description="Unauthorized")})
    public Response dismissedOnboarding(@Context SecurityContext securityContext) {
        this.onboardingSeenService.seenAndDismissed();
        return Response.ok().build();
    }
}

