/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.mobile.restapi.v1_0;

import com.atlassian.confluence.plugins.mobile.dto.MobileAnalyticEventDto;
import com.atlassian.confluence.plugins.mobile.service.MobileAnalyticService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Tag(name="Analytics API", description="Contains all operations related to analytics")
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
@Path(value="/analytic")
@Component
public class AnalyticResource {
    private final MobileAnalyticService analyticService;

    @Autowired
    public AnalyticResource(MobileAnalyticService analyticService) {
        this.analyticService = analyticService;
    }

    @Operation(summary="Publish analytics", description="Publishes a list of analytics events", responses={@ApiResponse(responseCode="204", description="successfully published"), @ApiResponse(responseCode="401", description="User unauthorized")})
    @POST
    public Response publish(List<MobileAnalyticEventDto> events) {
        this.analyticService.publish(events);
        return Response.noContent().build();
    }
}

