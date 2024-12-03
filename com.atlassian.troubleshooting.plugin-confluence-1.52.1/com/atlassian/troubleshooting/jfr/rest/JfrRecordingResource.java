/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.sun.jersey.spi.resource.Singleton
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.jfr.rest;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.atlassian.troubleshooting.jfr.manager.JfrRecordingManager;
import com.atlassian.troubleshooting.stp.security.PermissionValidationService;
import com.sun.jersey.spi.resource.Singleton;
import java.util.Objects;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;

@Path(value="/jfr/recordings")
@Produces(value={"application/json"})
@Singleton
@WebSudoRequired
public class JfrRecordingResource {
    private final PermissionValidationService permissionValidationService;
    private final JfrRecordingManager jfrRecordingManager;

    @Autowired
    public JfrRecordingResource(PermissionValidationService permissionValidationService, JfrRecordingManager jfrRecordingManager) {
        this.permissionValidationService = Objects.requireNonNull(permissionValidationService);
        this.jfrRecordingManager = Objects.requireNonNull(jfrRecordingManager);
    }

    @ExperimentalApi
    @GET
    public Response getRecordings() {
        this.permissionValidationService.validateIsSysadmin();
        return Response.ok(this.jfrRecordingManager.getRecordingDetails()).build();
    }
}

