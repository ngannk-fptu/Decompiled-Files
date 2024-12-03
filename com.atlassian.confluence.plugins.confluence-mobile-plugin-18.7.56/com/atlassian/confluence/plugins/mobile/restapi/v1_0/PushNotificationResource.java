/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.mobile.restapi.v1_0;

import com.atlassian.confluence.plugins.mobile.dto.notification.RegistrationDto;
import com.atlassian.confluence.plugins.mobile.notification.PushNotificationSetting;
import com.atlassian.confluence.plugins.mobile.notification.PushNotificationStatus;
import com.atlassian.confluence.plugins.mobile.service.PushNotificationService;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Collections;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Tag(name="Push Notification API", description="Contains all operations for push notifications")
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
@Path(value="/push-notification")
@Component
public class PushNotificationResource {
    private PushNotificationService pushNotificationService;

    @Autowired
    public PushNotificationResource(PushNotificationService pushNotificationService) {
        this.pushNotificationService = pushNotificationService;
    }

    @Operation(summary="Get the global push notification status", description="Gets the push notification status of the plugin", responses={@ApiResponse(responseCode="200", description="Push notification status")})
    @GET
    @Path(value="/status")
    public Map<String, PushNotificationStatus> getStatus() {
        return Collections.singletonMap("status", this.pushNotificationService.getStatus());
    }

    @Operation(summary="Update the global push notification status", description="Updates the push notification status of the plugin", responses={@ApiResponse(responseCode="204", description="Successfully updated push notification status"), @ApiResponse(responseCode="400", description="Bad request"), @ApiResponse(responseCode="403", description="User unauthorized")})
    @POST
    @Path(value="/status/{status}")
    public Response updateStatus(@PathParam(value="status") PushNotificationStatus status) {
        this.pushNotificationService.updateStatus(status);
        return Response.noContent().build();
    }

    @Operation(summary="Register a device", description="Registers a device to receive push notifications", responses={@ApiResponse(responseCode="200", description="Device registered", content={@Content(schema=@Schema(implementation=RegistrationDto.class))}), @ApiResponse(responseCode="400", description="Bad request")})
    @POST
    @Path(value="/registration")
    public RegistrationDto register(@Parameter(description="must contain os, build and token") RegistrationDto registrationDto) {
        return this.pushNotificationService.register(registrationDto);
    }

    @Operation(summary="Unregister a device", description="Unregisters a device to no longer receive push notification", responses={@ApiResponse(responseCode="204", description="Device unregistered"), @ApiResponse(responseCode="404", description="Device is not registered")})
    @DELETE
    @Path(value="/registration/{registrationId}")
    @AnonymousAllowed
    public Response unregister(@PathParam(value="registrationId") String registrationId) {
        this.pushNotificationService.unregister(registrationId);
        return Response.noContent().build();
    }

    @Operation(summary="Get device push notification settings", description="Gets the push notification settings for a device", responses={@ApiResponse(responseCode="200", description="Push notification settings", content={@Content(schema=@Schema(implementation=PushNotificationSetting.class))}), @ApiResponse(responseCode="501", description="Push notifications are disabled for this device")})
    @GET
    @Path(value="/setting/device/{deviceId}/app/{appName}")
    public PushNotificationSetting getSetting(@PathParam(value="deviceId") String deviceId, @PathParam(value="appName") String appName) {
        return this.pushNotificationService.getSetting(deviceId, appName);
    }

    @Operation(summary="Update device push notification settings", description="Updates the push notification settings for a device", responses={@ApiResponse(responseCode="200", description="Push notification settings", content={@Content(schema=@Schema(implementation=PushNotificationSetting.class))}), @ApiResponse(responseCode="501", description="Push notifications are disabled for this device")})
    @POST
    @Path(value="/setting/device/{deviceId}/app/{appName}")
    public PushNotificationSetting updateSetting(@PathParam(value="deviceId") String deviceId, @PathParam(value="appName") String appName, PushNotificationSetting notificationSetting) {
        return this.pushNotificationService.updateSetting(deviceId, appName, notificationSetting);
    }
}

