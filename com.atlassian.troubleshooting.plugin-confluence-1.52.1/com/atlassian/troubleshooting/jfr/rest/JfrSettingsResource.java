/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.sun.jersey.spi.resource.Singleton
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.apache.commons.lang3.StringUtils
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.jfr.rest;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.atlassian.troubleshooting.jfr.domain.JfrSettings;
import com.atlassian.troubleshooting.jfr.exception.JfrException;
import com.atlassian.troubleshooting.jfr.exception.JfrWriteException;
import com.atlassian.troubleshooting.jfr.manager.JfrRecordingManager;
import com.atlassian.troubleshooting.jfr.rest.dto.ErrorReasonDto;
import com.atlassian.troubleshooting.stp.security.PermissionValidationService;
import com.sun.jersey.spi.resource.Singleton;
import java.io.IOException;
import java.util.Objects;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;

@Path(value="/jfr/settings")
@Produces(value={"application/json"})
@Singleton
@WebSudoRequired
public class JfrSettingsResource {
    private static final int JFR_CONFIGURATION_INVALID_CODE = 506;
    private final PermissionValidationService permissionValidationService;
    private final JfrRecordingManager jfrRecordingManager;
    private final I18nResolver i18nResolver;

    @Autowired
    public JfrSettingsResource(PermissionValidationService permissionValidationService, JfrRecordingManager jfrRecordingManager, I18nResolver i18nResolver) {
        this.permissionValidationService = Objects.requireNonNull(permissionValidationService);
        this.jfrRecordingManager = Objects.requireNonNull(jfrRecordingManager);
        this.i18nResolver = Objects.requireNonNull(i18nResolver);
    }

    @ExperimentalApi
    @GET
    public JfrSettings getSettings() {
        this.permissionValidationService.validateIsSysadmin();
        return this.jfrRecordingManager.getSettings();
    }

    @ExperimentalApi
    @POST
    public Response storeSettings(String jsonBody) {
        this.permissionValidationService.validateIsSysadmin();
        try {
            JfrSettings jfrSettings = this.parseJfrSettingsRequest(jsonBody);
            return Response.ok((Object)this.jfrRecordingManager.storeSettings(jfrSettings)).build();
        }
        catch (InvalidJfrRequestException exc) {
            ErrorReasonDto reason = new ErrorReasonDto(exc.getMessage());
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)reason).build();
        }
        catch (IllegalStateException exc) {
            ErrorReasonDto reason = new ErrorReasonDto(this.i18nResolver.getText("stp.jfr.configuration.communication.failed"));
            return Response.status((Response.Status)Response.Status.CONFLICT).entity((Object)reason).build();
        }
        catch (JfrException exc) {
            ErrorReasonDto reason = new ErrorReasonDto(exc.getMessage());
            return Response.status((int)506).entity((Object)reason).build();
        }
        catch (JfrWriteException exc) {
            ErrorReasonDto reason = new ErrorReasonDto(exc.getMessage());
            return Response.status((Response.Status)Response.Status.INTERNAL_SERVER_ERROR).entity((Object)reason).build();
        }
    }

    private JfrSettings parseJfrSettingsRequest(String jsonBody) {
        if (StringUtils.isEmpty((CharSequence)jsonBody)) {
            String reason = this.i18nResolver.getText("stp.jfr.error.request.empty");
            throw new InvalidJfrRequestException(reason);
        }
        try {
            return (JfrSettings)new ObjectMapper().readValue(jsonBody, JfrSettings.class);
        }
        catch (IOException exc) {
            String reason = this.i18nResolver.getText("stp.jfr.error.request.malformed");
            throw new InvalidJfrRequestException(reason, exc);
        }
    }

    private static class InvalidJfrRequestException
    extends RuntimeException {
        InvalidJfrRequestException(String message) {
            super(message);
        }

        InvalidJfrRequestException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}

