/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.confluence.rest.api.model.RestList
 *  com.atlassian.confluence.rest.api.model.RestPageRequest
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
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
 */
package com.atlassian.confluence.plugins.files.rest;

import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.plugins.files.api.FileComment;
import com.atlassian.confluence.plugins.files.api.services.FileCommentService;
import com.atlassian.confluence.plugins.files.entities.FileCommentInput;
import com.atlassian.confluence.rest.api.model.RestList;
import com.atlassian.confluence.rest.api.model.RestPageRequest;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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

@AnonymousAllowed
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
@Path(value="/files/{fileId}/comments")
public class FileCommentResource {
    private final FileCommentService commentService;

    public FileCommentResource(FileCommentService commentService) throws ServiceException {
        this.commentService = commentService;
    }

    @POST
    public FileComment createFileComment(@PathParam(value="fileId") long attachmentId, @QueryParam(value="attachmentVersion") @DefaultValue(value="0") int attachmentVersion, FileCommentInput commentInput) throws ServiceException {
        return this.commentService.createComment(attachmentId, attachmentVersion, commentInput);
    }

    @GET
    public PageResponse<FileComment> getFileComments(@PathParam(value="fileId") long attachmentId, @QueryParam(value="attachmentVersion") @DefaultValue(value="0") int attachmentVersion, @QueryParam(value="start") @DefaultValue(value="0") int start, @QueryParam(value="limit") @DefaultValue(value="100") int limit, @Context UriInfo uriInfo) throws ServiceException {
        RestPageRequest pageRequest = new RestPageRequest(uriInfo, start, limit);
        PageResponse<FileComment> response = this.commentService.getComments(attachmentId, attachmentVersion, (PageRequest)pageRequest);
        return RestList.newRestList(response).pageRequest((PageRequest)pageRequest).build();
    }

    @GET
    @Path(value="/{commentId}")
    public FileComment getFileCommentById(@PathParam(value="fileId") long attachmentId, @QueryParam(value="attachmentVersion") @DefaultValue(value="0") int attachmentVersion, @PathParam(value="commentId") long commentId) throws ServiceException {
        return this.commentService.getCommentById(attachmentId, attachmentVersion, commentId);
    }

    @PUT
    @Path(value="/{commentId}")
    public FileComment updateFileComment(@PathParam(value="fileId") long attachmentId, @QueryParam(value="attachmentVersion") @DefaultValue(value="0") int attachmentVersion, @PathParam(value="commentId") long commentId, FileCommentInput commentInput) throws ServiceException {
        return this.commentService.updateComment(attachmentId, attachmentVersion, commentId, commentInput);
    }

    @DELETE
    @Path(value="/{commentId}")
    public void deleteFileComment(@PathParam(value="fileId") long attachmentId, @QueryParam(value="attachmentVersion") @DefaultValue(value="0") int attachmentVersion, @PathParam(value="commentId") long commentId) throws ServiceException {
        this.commentService.deleteComment(attachmentId, attachmentVersion, commentId);
    }
}

