/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessAllowed
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.confluence.rest.api.model.RestList
 *  com.atlassian.confluence.rest.api.model.RestPageRequest
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.FormParam
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.UriInfo
 */
package com.atlassian.confluence.plugins.files.rest;

import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessAllowed;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.plugins.files.api.services.ConfluenceFileService;
import com.atlassian.confluence.plugins.files.entities.ConfluenceFileEntity;
import com.atlassian.confluence.plugins.files.entities.FileVersionSummaryEntity;
import com.atlassian.confluence.rest.api.model.RestList;
import com.atlassian.confluence.rest.api.model.RestPageRequest;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

@AnonymousAllowed
@ReadOnlyAccessAllowed
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
@Path(value="/files")
public class ConfluenceFileResource {
    private final ConfluenceFileService fileService;

    public ConfluenceFileResource(ConfluenceFileService fileService) {
        this.fileService = fileService;
    }

    @GET
    @Path(value="/content/{contentId}/byAttachmentId")
    public ConfluenceFileEntity getConfluenceFile(@PathParam(value="contentId") long contentId, @QueryParam(value="attachmentId") long attachmentId, @QueryParam(value="attachmentVersion") @DefaultValue(value="0") int attachmentVersion) throws ServiceException {
        return this.fileService.getFileById(attachmentId, attachmentVersion);
    }

    @GET
    @Path(value="/content/{contentId}")
    public PageResponse<ConfluenceFileEntity> getConfluenceFiles(@PathParam(value="contentId") long contentId, @QueryParam(value="start") @DefaultValue(value="0") int start, @QueryParam(value="limit") @DefaultValue(value="100") int limit, @Context UriInfo uriInfo) throws ServiceException {
        RestPageRequest pageRequest = new RestPageRequest(uriInfo, start, limit);
        return RestList.newRestList(this.fileService.getFiles(contentId, (PageRequest)pageRequest)).pageRequest((PageRequest)pageRequest).build();
    }

    @POST
    @Path(value="/content/{contentId}/byAttachmentIds")
    public PageResponse<ConfluenceFileEntity> getConfluenceFiles(@PathParam(value="contentId") long contentId, @FormParam(value="attachmentIds") List<Long> attachmentIds) throws ServiceException {
        return RestList.newRestList(this.fileService.getFilesByIds(attachmentIds)).build();
    }

    @POST
    @Path(value="/content/{contentId}/minusAttachmentIds")
    public PageResponse<ConfluenceFileEntity> getConfluenceFilesMinusAttachmentIds(@PathParam(value="contentId") long contentId, @FormParam(value="attachmentIds") List<Long> attachmentIds, @QueryParam(value="start") @DefaultValue(value="0") int start, @QueryParam(value="limit") @DefaultValue(value="100") int limit, @Context UriInfo uriInfo) throws ServiceException {
        RestPageRequest restPageRequest = new RestPageRequest(uriInfo, start, limit);
        return RestList.newRestList(this.fileService.getFilesMinusAttachmentId(contentId, attachmentIds, (PageRequest)restPageRequest)).build();
    }

    @GET
    @Path(value="/content/{contentId}/commentCount")
    public int getUnresolvedCommentCountPathParam(@PathParam(value="contentId") long contentId, @QueryParam(value="attachmentId") long attachmentId, @QueryParam(value="attachmentVersion") @DefaultValue(value="0") int attachmentVersion) throws ServiceException {
        return this.fileService.getUnresolvedCommentCountByAttachmentId(attachmentId, attachmentVersion);
    }

    @GET
    @Path(value="/{fileId}/versions")
    public RestList<FileVersionSummaryEntity> getVersionSummaries(@PathParam(value="fileId") long attachmentId, @QueryParam(value="start") @DefaultValue(value="0") int start, @QueryParam(value="limit") @DefaultValue(value="100") int limit, @Context UriInfo uriInfo) throws ServiceException {
        RestPageRequest pageRequest = new RestPageRequest(uriInfo, start, limit);
        return RestList.newRestList(this.fileService.getVersionSummaries(attachmentId, (PageRequest)pageRequest)).pageRequest((PageRequest)pageRequest).build();
    }
}

