/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.AttachmentUpload
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentStatus
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.service.content.AttachmentService
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.confluence.api.service.exceptions.PermissionException
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.confluence.api.service.settings.SettingsService
 *  com.atlassian.confluence.rest.api.model.ExpansionsParser
 *  com.atlassian.confluence.rest.api.model.RestList
 *  com.atlassian.confluence.rest.api.model.RestPageRequest
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.rest.common.multipart.FilePart
 *  com.atlassian.plugins.rest.common.multipart.MultipartConfig
 *  com.atlassian.plugins.rest.common.multipart.MultipartConfigClass
 *  com.atlassian.plugins.rest.common.multipart.MultipartFormParam
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.atlassian.plugins.rest.common.security.RequiresXsrfCheck
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.UriInfo
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.restapi.resources;

import com.atlassian.annotations.PublicApi;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.AttachmentUpload;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentStatus;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.service.content.AttachmentService;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.api.service.exceptions.PermissionException;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.api.service.settings.SettingsService;
import com.atlassian.confluence.rest.api.model.ExpansionsParser;
import com.atlassian.confluence.rest.api.model.RestList;
import com.atlassian.confluence.rest.api.model.RestPageRequest;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.multipart.FilePart;
import com.atlassian.plugins.rest.common.multipart.MultipartConfig;
import com.atlassian.plugins.rest.common.multipart.MultipartConfigClass;
import com.atlassian.plugins.rest.common.multipart.MultipartFormParam;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.plugins.rest.common.security.RequiresXsrfCheck;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@AnonymousAllowed
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
@Path(value="/content/{id}/child/attachment")
public class AttachmentResource {
    private static final Logger log = LoggerFactory.getLogger(AttachmentResource.class);
    private static final String DEFAULT_LIMIT = "50";
    private final AttachmentService attachmentService;

    public AttachmentResource(@ComponentImport AttachmentService attachmentService, @ComponentImport SettingsService settingsService) {
        this.attachmentService = attachmentService;
        ConfluenceAttachmentMultipartConfig.settingsService = settingsService;
    }

    @GET
    @PublicApi
    public RestList<Content> getAttachments(@PathParam(value="id") ContentId contentId, @QueryParam(value="expand") @DefaultValue(value="") String expand, @QueryParam(value="start") int start, @QueryParam(value="limit") @DefaultValue(value="50") int limit, @QueryParam(value="filename") String filename, @QueryParam(value="mediaType") String mediaType, @Context UriInfo uriInfo) throws ServiceException {
        Expansion[] expansions = ExpansionsParser.parse((String)expand);
        RestPageRequest request = new RestPageRequest(uriInfo, start, limit);
        PageResponse content = this.attachmentService.find(expansions).withContainerId(contentId).withFilename(filename).withMediaType(mediaType).fetchMany((PageRequest)request);
        return RestList.createRestList((PageRequest)request, (PageResponse)content);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @POST
    @Consumes(value={"multipart/form-data"})
    @MultipartConfigClass(value=ConfluenceAttachmentMultipartConfig.class)
    @RequiresXsrfCheck
    @PublicApi
    public RestList<Content> createAttachments(@PathParam(value="id") ContentId containerId, @QueryParam(value="status") @DefaultValue(value="current") ContentStatus containerStatus, @QueryParam(value="allowDuplicated") @DefaultValue(value="false") boolean allowDuplicated, @QueryParam(value="expand") @DefaultValue(value="") String expand, @MultipartFormParam(value="file") List<FilePart> fileParts, @MultipartFormParam(value="comment") @Nullable List<FilePart> comments, @MultipartFormParam(value="minorEdit") @Nullable List<FilePart> minorEdits, @MultipartFormParam(value="hidden") @Nullable List<FilePart> hiddens) throws ServiceException {
        int numhiddens;
        int numMinorEdits;
        if (!this.attachmentService.validator().canCreateAttachments(containerId, containerStatus)) {
            throw new PermissionException("User not permitted to create attachments for content: " + containerId);
        }
        if (fileParts.isEmpty()) {
            throw new BadRequestException("At least one attachment file must be included.");
        }
        int numComments = comments == null ? 0 : comments.size();
        int numAttachments = fileParts.size();
        if (numComments > 0 && numComments != numAttachments) {
            throw new BadRequestException("Must be same number of attachment files and comments.");
        }
        int n = numMinorEdits = minorEdits == null ? 0 : minorEdits.size();
        if (numMinorEdits > 0 && numMinorEdits != numAttachments) {
            throw new BadRequestException("Must have the same number of attachment files and minorEdits flag, or not have any minorEdits flags at all");
        }
        int n2 = numhiddens = hiddens == null ? 0 : hiddens.size();
        if (numhiddens > 0 && numhiddens != numAttachments) {
            throw new BadRequestException("Must have the same number of attachment files and hidden flags, or not have any hidden flags at all");
        }
        Expansions expansions = ExpansionsParser.parseAsExpansions((String)expand);
        List<AttachmentUpload> uploads = this.convertFilePartsToAttachmentUploads(fileParts, comments, minorEdits, hiddens);
        try {
            PageResponse contents = this.attachmentService.addAttachments(containerId, containerStatus, uploads, allowDuplicated, expansions);
            RestList restList = RestList.createRestList((PageResponse)contents);
            return restList;
        }
        finally {
            uploads.stream().map(AttachmentUpload::getFile).forEach(File::delete);
        }
    }

    @PUT
    @Path(value="/{attachmentId}")
    @PublicApi
    public Content update(@PathParam(value="attachmentId") String attachmentId, Content attachment) throws ServiceException {
        return this.attachmentService.update(attachment);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @POST
    @Path(value="/{attachmentId}/data")
    @Consumes(value={"multipart/form-data"})
    @MultipartConfigClass(value=ConfluenceAttachmentMultipartConfig.class)
    @RequiresXsrfCheck
    @PublicApi
    public Content updateData(@PathParam(value="attachmentId") ContentId attachmentId, @MultipartFormParam(value="file") FilePart filePart, @MultipartFormParam(value="comment") FilePart comment, @MultipartFormParam(value="minorEdit") FilePart minorEdit, @MultipartFormParam(value="hidden") FilePart hidden) throws ServiceException {
        boolean isMinorEdit = false;
        if (minorEdit != null && Boolean.valueOf(minorEdit.getValue()).booleanValue()) {
            isMinorEdit = true;
        }
        boolean isHidden = false;
        if (hidden != null && Boolean.valueOf(hidden.getValue()).booleanValue()) {
            isHidden = true;
        }
        String commentText = null;
        if (comment != null) {
            commentText = comment.getValue();
        }
        AttachmentUpload upload = this.makeAttachmentUpload(filePart, commentText, isMinorEdit, isHidden);
        try {
            Content content = this.attachmentService.updateData(attachmentId, upload);
            return content;
        }
        finally {
            upload.getFile().delete();
        }
    }

    private List<AttachmentUpload> convertFilePartsToAttachmentUploads(List<FilePart> fileParts, List<FilePart> comments, List<FilePart> minorEdits, List<FilePart> hiddens) throws ServiceException {
        ArrayList<AttachmentUpload> uploads = new ArrayList<AttachmentUpload>();
        int numFiles = fileParts.size();
        for (int i = 0; i < numFiles; ++i) {
            FilePart filePart = fileParts.get(i);
            String comment = comments.isEmpty() ? null : comments.get(i).getValue();
            Boolean minorEdit = minorEdits.isEmpty() ? false : Boolean.parseBoolean(minorEdits.get(i).getValue());
            Boolean hidden = hiddens.isEmpty() ? false : Boolean.parseBoolean(hiddens.get(i).getValue());
            uploads.add(this.makeAttachmentUpload(filePart, comment, minorEdit, hidden));
        }
        return uploads;
    }

    private AttachmentUpload makeAttachmentUpload(FilePart filePart, String comment, boolean minorEdit, boolean hidden) throws ServiceException {
        return new AttachmentUpload(this.getFileFromFilePart(filePart), filePart.getName(), filePart.getContentType(), comment, minorEdit, hidden);
    }

    private File getFileFromFilePart(FilePart filePart) throws ServiceException {
        try {
            File file = File.createTempFile("attachment-", ".tmp");
            if (!file.delete()) {
                log.debug("Could not remove newly created temp file. Continuing");
            }
            file.deleteOnExit();
            filePart.write(file);
            return file;
        }
        catch (IOException e) {
            throw new ServiceException("Error processing FilePart: " + filePart.getName(), (Throwable)e);
        }
    }

    public static class ConfluenceAttachmentMultipartConfig
    implements MultipartConfig {
        private static SettingsService settingsService;

        public long getMaxFileSize() {
            return settingsService.getGlobalSettings().getAttachmentMaxSizeBytes();
        }

        public long getMaxSize() {
            return this.getMaxFileSize() * 10L;
        }
    }
}

