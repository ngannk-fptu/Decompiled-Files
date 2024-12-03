/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jersey.spi.resource.Singleton
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 */
package com.atlassian.troubleshooting.thready.rest;

import com.atlassian.troubleshooting.stp.security.PermissionValidationService;
import com.atlassian.troubleshooting.thready.manager.ThreadDiagnosticsConfigurationManager;
import com.sun.jersey.spi.resource.Singleton;
import java.util.Objects;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path(value="threadDiagnostics")
@Produces(value={"application/json"})
@Singleton
public class ThreadDiagnosticsResource {
    private final ThreadDiagnosticsConfigurationManager threadDiagnosticsConfigurationManager;
    private final PermissionValidationService permissionValidationService;

    public ThreadDiagnosticsResource(ThreadDiagnosticsConfigurationManager threadDiagnosticsConfigurationManager, PermissionValidationService permissionValidationService) {
        this.threadDiagnosticsConfigurationManager = Objects.requireNonNull(threadDiagnosticsConfigurationManager);
        this.permissionValidationService = Objects.requireNonNull(permissionValidationService);
    }

    @GET
    @Path(value="config")
    public ThreadDiagnosticsConfigurationManager.Configuration configuration() {
        this.permissionValidationService.validateIsSysadmin();
        return this.threadDiagnosticsConfigurationManager.getConfiguration();
    }

    @POST
    @Path(value="config")
    public void setConfig(ThreadDiagnosticsConfigurationManager.Configuration configuration) {
        this.permissionValidationService.validateIsSysadmin();
        this.threadDiagnosticsConfigurationManager.setConfiguration(configuration);
    }
}

