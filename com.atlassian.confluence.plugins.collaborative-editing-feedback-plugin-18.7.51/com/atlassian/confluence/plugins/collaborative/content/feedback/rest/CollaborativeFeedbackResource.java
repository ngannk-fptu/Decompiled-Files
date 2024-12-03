/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.user.User
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.GET
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.confluence.plugins.collaborative.content.feedback.rest;

import com.atlassian.confluence.plugins.collaborative.content.feedback.rest.Utils;
import com.atlassian.confluence.plugins.collaborative.content.feedback.rest.model.CollectMetadata;
import com.atlassian.confluence.plugins.collaborative.content.feedback.service.AuditingService;
import com.atlassian.confluence.plugins.collaborative.content.feedback.service.DataExportService;
import com.atlassian.confluence.plugins.collaborative.content.feedback.service.FileNameUtils;
import com.atlassian.confluence.plugins.collaborative.content.feedback.service.PermissionService;
import com.atlassian.confluence.plugins.collaborative.content.feedback.service.SettingsManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.user.User;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path(value="/")
@Produces(value={"application/json"})
public class CollaborativeFeedbackResource {
    private static final String COLLECTED_DATA_ACTION_KEY = "audit.logging.collaborative.feedback.data.collected";
    private static final String DOWNLOADED_DATA_ACTION_KEY = "audit.logging.collaborative.feedback.data.downloaded";
    private static final String DELETED_DATA_ACTION_KEY = "audit.logging.collaborative.feedback.data.deleted";
    private final DataExportService dataExportService;
    private final PermissionService permissionService;
    private final SettingsManager settingsManager;
    private final AuditingService auditingService;

    public CollaborativeFeedbackResource(DataExportService dataExportService, PermissionService permissionService, SettingsManager settingsManager, AuditingService auditingService) {
        this.dataExportService = dataExportService;
        this.permissionService = permissionService;
        this.settingsManager = settingsManager;
        this.auditingService = auditingService;
    }

    @PUT
    @Path(value="collect/{contentId}")
    public Response collectData(@PathParam(value="contentId") long contentId, CollectMetadata meta) {
        return Utils.executeAndRespond(() -> {
            this.auditingService.audit(contentId, COLLECTED_DATA_ACTION_KEY);
            return Collections.singletonMap("result-file", this.dataExportService.exportDataFor(contentId, meta));
        });
    }

    @GET
    @Path(value="file/list")
    public Response listFiles() {
        return Utils.executeAndRespond(() -> {
            this.permissionService.enforceSysAdmin((User)AuthenticatedUserThreadLocal.get());
            return Arrays.stream(Objects.requireNonNull(this.settingsManager.getDestinationFolder().listFiles())).map(File::getName).filter(FileNameUtils::isValidFileName).collect(Collectors.toList());
        });
    }

    @GET
    @Path(value="file")
    @Produces(value={"application/octet-stream"})
    public Response downloadFile(@QueryParam(value="fileName") String fileName) {
        return Utils.executeAndWrapExceptions(() -> this.invalidContext(fileName).orElseGet(() -> {
            this.auditingService.audit(FileNameUtils.getContentId(fileName), DOWNLOADED_DATA_ACTION_KEY);
            File fileToDownload = new File(this.settingsManager.getDestinationFolder(), Objects.requireNonNull(fileName));
            if (!fileToDownload.exists()) {
                return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
            }
            return Response.ok((Object)fileToDownload).header("content-disposition", (Object)("attachment; filename = " + fileName)).build();
        }));
    }

    @DELETE
    @Path(value="file")
    public Response deleteFile(@QueryParam(value="fileName") String fileName) {
        return Utils.executeAndWrapExceptions(() -> this.invalidContext(fileName).orElseGet(() -> {
            this.auditingService.audit(FileNameUtils.getContentId(fileName), DELETED_DATA_ACTION_KEY);
            File fileToDelete = new File(this.settingsManager.getDestinationFolder(), Objects.requireNonNull(fileName));
            if (!fileToDelete.exists()) {
                return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
            }
            return Response.ok().entity((Object)("{\"success\": " + fileToDelete.delete() + "}")).build();
        }));
    }

    private Optional<Response> invalidContext(String fileName) {
        if (!this.permissionService.isSysAdmin((User)AuthenticatedUserThreadLocal.get())) {
            return Optional.of(Response.status((int)403).build());
        }
        if (!FileNameUtils.isValidFileName(fileName)) {
            return Optional.of(Response.status((Response.Status)Response.Status.BAD_REQUEST).build());
        }
        return Optional.empty();
    }
}

