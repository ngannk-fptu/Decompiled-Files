/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.compat.api.service.accessmode.ReadOnlyAccessAllowed
 *  com.atlassian.plugins.rest.common.security.jersey.AdminOnlyResourceFilter
 *  com.sun.jersey.spi.container.ResourceFilters
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 */
package com.atlassian.migration.agent.rest;

import com.atlassian.confluence.compat.api.service.accessmode.ReadOnlyAccessAllowed;
import com.atlassian.migration.agent.service.guardrails.macro.MacroAssessmentService;
import com.atlassian.plugins.rest.common.security.jersey.AdminOnlyResourceFilter;
import com.sun.jersey.spi.container.ResourceFilters;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@ParametersAreNonnullByDefault
@Path(value="instance/assessment/macros")
@ReadOnlyAccessAllowed
@ResourceFilters(value={AdminOnlyResourceFilter.class})
@Produces(value={"application/json"})
@Consumes(value={"application/json"})
public class MacroAssessmentResource {
    private final MacroAssessmentService macroAssessmentService;

    public MacroAssessmentResource(MacroAssessmentService macroAssessmentService) {
        this.macroAssessmentService = macroAssessmentService;
    }

    @GET
    @Path(value="/")
    public Response assessMacros(@Nullable @QueryParam(value="contentSupplier") String contentSupplier) {
        return Response.ok((Object)this.macroAssessmentService.assess(contentSupplier)).type("application/json").build();
    }
}

