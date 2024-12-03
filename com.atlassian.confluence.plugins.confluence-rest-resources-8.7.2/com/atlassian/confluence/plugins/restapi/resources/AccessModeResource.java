/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  com.atlassian.confluence.api.service.accessmode.AccessModeService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 */
package com.atlassian.confluence.plugins.restapi.resources;

import com.atlassian.annotations.PublicApi;
import com.atlassian.confluence.api.service.accessmode.AccessModeService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@AnonymousAllowed
@Produces(value={"application/json"})
@Path(value="/accessmode")
public class AccessModeResource {
    private final AccessModeService accessModeService;

    public AccessModeResource(@ComponentImport AccessModeService accessModeService) {
        this.accessModeService = accessModeService;
    }

    @GET
    @PublicApi
    public Response getAccessModeStatus() {
        return Response.ok((Object)this.accessModeService.getAccessMode()).build();
    }
}

