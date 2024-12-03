/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.security.CaptchaManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  javax.servlet.http.HttpServletRequest
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.inlinecomments.resources;

import com.atlassian.confluence.plugins.inlinecomments.entities.HistoryPageInlineComment;
import com.atlassian.confluence.plugins.inlinecomments.entities.InlineCommentCreationBean;
import com.atlassian.confluence.plugins.inlinecomments.entities.InlineCommentCreationResultBean;
import com.atlassian.confluence.plugins.inlinecomments.entities.InlineCommentResult;
import com.atlassian.confluence.plugins.inlinecomments.entities.InlineCommentUpdateBean;
import com.atlassian.confluence.plugins.inlinecomments.entities.Reply;
import com.atlassian.confluence.plugins.inlinecomments.entities.TopLevelInlineComment;
import com.atlassian.confluence.plugins.inlinecomments.service.InlineCommentService;
import com.atlassian.confluence.plugins.inlinecomments.service.ReplyCommentService;
import com.atlassian.confluence.security.CaptchaManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@AnonymousAllowed
@Path(value="comments")
public class InlineCommentResource {
    private static final Logger log = LoggerFactory.getLogger(InlineCommentResource.class);
    private final InlineCommentService inlineCommentService;
    private final ReplyCommentService replyCommentService;
    private final CaptchaManager captchaManager;

    public InlineCommentResource(InlineCommentService inlineCommentService, ReplyCommentService replyCommentService, CaptchaManager captchaManager) {
        this.inlineCommentService = inlineCommentService;
        this.replyCommentService = replyCommentService;
        this.captchaManager = captchaManager;
    }

    @POST
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    public Response createInlineComment(InlineCommentCreationBean creationBean, @Context HttpServletRequest req) {
        Response captchaCheckResponse = this.checkCaptcha(req);
        if (captchaCheckResponse.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
            return captchaCheckResponse;
        }
        return this.createComment(creationBean, true);
    }

    @GET
    @Produces(value={"application/json"})
    public Response getInlineComments(@QueryParam(value="containerId") long containerId) {
        return this.inlineCommentService.getCommentThreads(containerId).buildResponse();
    }

    @PUT
    @Path(value="/{commentId}")
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    public Response updateInlineComment(InlineCommentUpdateBean updateInlineComment, @Context HttpServletRequest req) {
        Response captchaCheckResponse = this.checkCaptcha(req);
        if (captchaCheckResponse.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
            return captchaCheckResponse;
        }
        return this.inlineCommentService.updateComment(updateInlineComment).buildResponse();
    }

    @GET
    @Path(value="/{commentId}")
    @Produces(value={"application/json"})
    public Response getInlineComment(@PathParam(value="commentId") long commentId) {
        return this.inlineCommentService.getComment(commentId).buildResponse();
    }

    @DELETE
    @Path(value="/{commentId}")
    @Produces(value={"application/json"})
    public Response deleteInlineComment(@PathParam(value="commentId") long commentId) {
        return this.inlineCommentService.deleteInlineComment(commentId).buildResponse();
    }

    private Response createComment(InlineCommentCreationBean creationBean, boolean inlineAttempt) {
        Response response;
        if (creationBean == null) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).build();
        }
        if (StringUtils.isBlank((CharSequence)creationBean.getBody())) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).build();
        }
        InlineCommentService.Result result = inlineAttempt ? this.inlineCommentService.create(creationBean) : this.inlineCommentService.createAsPageLevelComment(creationBean);
        switch (result.getStatus()) {
            case SUCCESS: {
                response = this.buildCommentCreationResponse(result, inlineAttempt);
                break;
            }
            case NOT_PERMITTED: {
                response = Response.status((Response.Status)Response.Status.UNAUTHORIZED).build();
                break;
            }
            case CANNOT_MODIFY_STORAGE_FORMAT: {
                response = Response.status((Response.Status)Response.Status.PRECONDITION_FAILED).build();
                break;
            }
            case STALE_STORAGE_FORMAT: {
                response = Response.status((Response.Status)Response.Status.CONFLICT).build();
                break;
            }
            case BAD_REQUEST_UTF8_MYSQL_ERROR: {
                response = Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)result.getErrorMessage()).build();
                break;
            }
            default: {
                response = Response.status((Response.Status)Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        }
        return response;
    }

    private Response buildCommentCreationResponse(InlineCommentService.Result result, boolean isInline) {
        if (isInline) {
            InlineCommentResult<TopLevelInlineComment> inlineCommentResult = this.inlineCommentService.getComment(result.getCommentId());
            if (inlineCommentResult.getStatus() == InlineCommentResult.Status.SUCCESS) {
                return inlineCommentResult.buildResponse();
            }
            log.error("Inline comment was created successfully but could not be retrieved, id is " + result.getCommentId());
            return Response.status((Response.Status)Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.ok((Object)new InlineCommentCreationResultBean(result.getCommentId())).build();
    }

    @GET
    @Path(value="/{commentId}/replies")
    @Produces(value={"application/json"})
    public Response getReplies(@PathParam(value="commentId") long commentId) {
        return this.replyCommentService.getReplies(commentId).buildResponse();
    }

    @POST
    @Path(value="/{commentId}/replies")
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    public Response createReply(Reply reply, @QueryParam(value="containerId") long containerId, @Context HttpServletRequest req) {
        Response captchaCheckResponse = this.checkCaptcha(req);
        if (captchaCheckResponse.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
            return captchaCheckResponse;
        }
        return this.replyCommentService.createReply(reply, containerId).buildResponse();
    }

    @PUT
    @Path(value="/{commentId}/replies/{replyId}")
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    public Response updateReply(Reply reply, @Context HttpServletRequest req) {
        Response captchaCheckResponse = this.checkCaptcha(req);
        if (captchaCheckResponse.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
            return captchaCheckResponse;
        }
        return this.replyCommentService.updateReply(reply).buildResponse();
    }

    @DELETE
    @Path(value="/{commentId}/replies/{replyId}")
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    public Response deleteReply(@PathParam(value="replyId") Long replyId) {
        return this.replyCommentService.deleteReply(replyId).buildResponse();
    }

    @PUT
    @Path(value="/{commentId}/resolve/{resolved}/dangling/{dangling}")
    @Produces(value={"application/json"})
    @Consumes(value={"application/json"})
    public Response resolveInlineComment(@PathParam(value="commentId") long commentId, @PathParam(value="resolved") boolean resolved, @PathParam(value="dangling") boolean isDangling) {
        if (isDangling) {
            HistoryPageInlineComment historyComment = this.inlineCommentService.getHistoryPageComment(commentId);
            if (historyComment.getDiffVersion() == 0) {
                return Response.status((Response.Status)Response.Status.CONFLICT).entity((Object)"This comment is not dangling").build();
            }
            boolean publishEvent = historyComment.getDiffVersion() <= 1;
            return this.inlineCommentService.updateResolveProperty(historyComment.getComment(), resolved, historyComment.getAbstractPage().getLastModificationDate(), historyComment.getAbstractPage().getLastModifier(), true, publishEvent).buildResponse();
        }
        return this.inlineCommentService.updateResolveProperty(commentId, resolved, new Date(), AuthenticatedUserThreadLocal.get(), false, true).buildResponse();
    }

    @GET
    @Path(value="/replies/{replyId}/commentId")
    @Produces(value={"application/json"})
    public Response getInlineCommentId(@PathParam(value="replyId") long replyId) {
        return this.inlineCommentService.getInlineCommentId(replyId).buildResponse();
    }

    private Response checkCaptcha(HttpServletRequest request) {
        if (this.captchaManager.isCaptchaEnabled() && !this.captchaManager.validateCaptcha(request.getHeader("X-Atlassian-Captcha-Id"), request.getHeader("X-Atlassian-Captcha-Response"))) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).entity((Object)"Captcha check failed").build();
        }
        return Response.status((Response.Status)Response.Status.OK).entity((Object)"Captcha check successful").build();
    }
}

