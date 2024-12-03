/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.rest.api.model.RestPageRequest
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.UriInfo
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.mobile.restapi.v1_0;

import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.plugins.mobile.dto.NotificationDto;
import com.atlassian.confluence.plugins.mobile.helper.NotificationHelper;
import com.atlassian.confluence.plugins.mobile.restapi.docs.NotificationsResponse;
import com.atlassian.confluence.plugins.mobile.service.MobileNotificationService;
import com.atlassian.confluence.rest.api.model.RestPageRequest;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Tag(name="Notification API", description="Contains all operations for notifications")
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
@Path(value="/notification")
@Component
public class NotificationResource {
    private static final String APP_ID_PARAM = "appId=";
    private final MobileNotificationService notificationService;
    private final InternalHostApplication internalHostApplication;

    @Autowired
    public NotificationResource(MobileNotificationService notificationService, @ComponentImport InternalHostApplication internalHostApplication) {
        this.notificationService = notificationService;
        this.internalHostApplication = internalHostApplication;
    }

    @Operation(summary="Get a notification", description="Get a notification by Id", responses={@ApiResponse(responseCode="200", description="Retrieved Notification", content={@Content(schema=@Schema(implementation=NotificationDto.class))}), @ApiResponse(responseCode="404", description="Notification doesn't exist"), @ApiResponse(responseCode="403", description="User unauthorized")})
    @GET
    @Path(value="/{id}")
    public NotificationDto getNotification(@PathParam(value="id") long id) {
        return this.notificationService.getNotification(id);
    }

    @Operation(summary="Get notifications", description="Get a pageable list of notifications. Params are additive in that they will match the intersection. i.e specifying time and pageId will match only for that time and pageId", responses={@ApiResponse(responseCode="200", description="List of notifications", content={@Content(schema=@Schema(implementation=NotificationsResponse.class))})})
    @GET
    public PageResponse<NotificationDto> getNotifications(@Parameter(description="value of 0 denotes no lower time range constraint") @QueryParam(value="from") @DefaultValue(value="0") long from, @Parameter(description="value of 0 denotes no upper time range constraint") @QueryParam(value="to") @DefaultValue(value="0") long to, @QueryParam(value="notificationId") List<Long> notificationIds, @QueryParam(value="pageId") List<Long> pageIds, @QueryParam(value="action") List<String> actions, @QueryParam(value="start") @DefaultValue(value="0") int start, @QueryParam(value="limit") @DefaultValue(value="50") int limit, @Context UriInfo uriInfo) {
        String appId = APP_ID_PARAM + this.internalHostApplication.getId().get();
        if (actions == null || actions.isEmpty()) {
            actions = NotificationHelper.WORKBOX_ACTION_LIST;
        }
        return this.notificationService.getNotifications(NotificationHelper.build(from, to, notificationIds, pageIds, actions, appId), (PageRequest)new RestPageRequest(uriInfo, start, limit));
    }

    @Operation(summary="Mark notifications as read", description="Mark a set of notifications as read. Params are additive in that they will match the intersection. i.e specifying time and pageId will match only for that time and pageId", responses={@ApiResponse(responseCode="204", description="Successfully marked notifications as read")})
    @POST
    @Path(value="/read")
    public Response readNotifications(@Parameter(description="value of 0 denotes no lower time range constraint") @QueryParam(value="from") @DefaultValue(value="0") long from, @Parameter(description="value of 0 denotes no upper time range constraint") @QueryParam(value="to") @DefaultValue(value="0") long to, @QueryParam(value="notificationId") List<Long> notificationIds, @QueryParam(value="pageId") List<Long> pageIds, @QueryParam(value="action") List<String> actions) {
        this.notificationService.readNotifications(NotificationHelper.build(from, to, notificationIds, pageIds, actions, null));
        return Response.noContent().build();
    }

    @Operation(summary="Delete notifications", description="Delete a set of notification. Params are additive in that they will match the intersection. i.e specifying time and pageId will match only for that time and pageId", responses={@ApiResponse(responseCode="204", description="Successfully deleted notifications")})
    @DELETE
    public Response deleteNotifications(@Parameter(description="value of 0 denotes no lower time range constraint") @QueryParam(value="from") @DefaultValue(value="0") long from, @Parameter(description="value of 0 denotes no upper time range constraint") @QueryParam(value="to") @DefaultValue(value="0") long to, @QueryParam(value="notificationId") List<Long> notificationIds, @QueryParam(value="pageId") List<Long> pageIds, @QueryParam(value="action") List<String> actions) {
        this.notificationService.deleteNotification(NotificationHelper.build(from, to, notificationIds, pageIds, actions, null));
        return Response.noContent().build();
    }
}

