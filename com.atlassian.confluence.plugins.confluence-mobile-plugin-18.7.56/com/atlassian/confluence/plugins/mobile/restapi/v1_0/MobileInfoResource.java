/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.mobile.restapi.v1_0;

import com.atlassian.confluence.plugins.mobile.dto.LoginInfoDto;
import com.atlassian.confluence.plugins.mobile.dto.ServerInfoDto;
import com.atlassian.confluence.plugins.mobile.service.MobileInfoService;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Tag(name="Server Info API", description="Contains all operations for server info")
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
@Path(value="/info")
@Component
public class MobileInfoResource {
    private final MobileInfoService mobileInfoService;

    @Autowired
    public MobileInfoResource(MobileInfoService mobileInfoService) {
        this.mobileInfoService = mobileInfoService;
    }

    @Operation(summary="Get login info", description="Get the metadata of the Confluence instance required for mobile app login", responses={@ApiResponse(responseCode="200", description="Login Info", content={@Content(schema=@Schema(implementation=LoginInfoDto.class))})})
    @GET
    @Path(value="login")
    @AnonymousAllowed
    public LoginInfoDto getLoginInfo() {
        return this.mobileInfoService.getLoginInfo();
    }

    @Operation(summary="Get server info", description="Get mobile-related metadata of the Confluence instance", responses={@ApiResponse(responseCode="200", description="Server Info", content={@Content(schema=@Schema(implementation=ServerInfoDto.class))})})
    @GET
    @Path(value="server")
    public ServerInfoDto getServerInfo() {
        return this.mobileInfoService.getServerInfo();
    }
}

