/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.xsrf.XsrfHeaderValidator
 *  com.atlassian.sal.api.xsrf.XsrfTokenValidator
 *  com.atlassian.streams.api.common.Either
 *  com.atlassian.streams.spi.StreamsCommentHandler$PostReplyError
 *  com.atlassian.streams.spi.StreamsCommentHandler$PostReplyError$Type
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Joiner
 *  com.google.common.base.Preconditions
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.streams.internal.servlet;

import com.atlassian.sal.api.xsrf.XsrfHeaderValidator;
import com.atlassian.sal.api.xsrf.XsrfTokenValidator;
import com.atlassian.streams.api.common.Either;
import com.atlassian.streams.internal.MissingModuleKeyException;
import com.atlassian.streams.internal.NoSuchModuleException;
import com.atlassian.streams.internal.PostReplyHandler;
import com.atlassian.streams.internal.RemotePostReplyException;
import com.atlassian.streams.internal.RemotePostValidationException;
import com.atlassian.streams.internal.servlet.XsrfAwareRequest;
import com.atlassian.streams.spi.StreamsCommentHandler;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StreamsCommentsServlet
extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(StreamsCommentsServlet.class);
    private final PostReplyHandler handler;
    private final XsrfTokenValidator xsrfTokenValidator;
    private final XsrfHeaderValidator xsrfHeaderValidator;

    public StreamsCommentsServlet(PostReplyHandler handler, XsrfTokenValidator xsrfTokenValidator, XsrfHeaderValidator xsrfHeaderValidator) {
        this.handler = (PostReplyHandler)Preconditions.checkNotNull((Object)handler, (Object)"handler");
        this.xsrfTokenValidator = (XsrfTokenValidator)Preconditions.checkNotNull((Object)xsrfTokenValidator, (Object)"XSRF token validator");
        this.xsrfHeaderValidator = (XsrfHeaderValidator)Preconditions.checkNotNull((Object)xsrfHeaderValidator, (Object)"XSRF header validator");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        if (!this.isXsrfSafe(req)) {
            resp.setContentType("application/json");
            StreamsCommentHandler.PostReplyError.Type errorType = StreamsCommentHandler.PostReplyError.Type.CONFLICT;
            resp.setStatus(errorType.getStatusCode());
            out.print(errorType.asJsonString());
            out.close();
            return;
        }
        String pathInfo = req.getPathInfo();
        String comment = req.getParameter("comment");
        if (comment == null) {
            resp.sendError(400, "No comment specified");
            return;
        }
        String replyTo = req.getParameter("replyTo");
        try {
            Either<StreamsCommentHandler.PostReplyError, URI> locationOrError = this.handler.postReply(pathInfo, comment, replyTo);
            if (locationOrError.isLeft()) {
                StreamsCommentHandler.PostReplyError error = (StreamsCommentHandler.PostReplyError)locationOrError.left().get();
                if (error.getType().equals((Object)StreamsCommentHandler.PostReplyError.Type.UNKNOWN_ERROR)) {
                    if (error.getCause().isDefined()) {
                        log.error("Unknown error while posting comment", (Throwable)error.getCause().get());
                    } else {
                        log.error("Unknown error while posting comment. No exception details available");
                    }
                } else {
                    log.warn("Logged an error while posting comment: " + error.getType().toString());
                }
                resp.setContentType("application/json");
                resp.setStatus(error.getType().getStatusCode());
                out.print(error.asJsonString());
                out.flush();
            } else {
                resp.setStatus(201);
                resp.setHeader("Location", ((URI)locationOrError.right().get()).toASCIIString());
            }
        }
        catch (MissingModuleKeyException e) {
            resp.sendError(404, "No module key in URI");
        }
        catch (NoSuchModuleException e) {
            resp.sendError(404, "No module with key " + e.getKey() + " installed");
        }
        catch (RemotePostReplyException e) {
            resp.sendError(502, e.getMessage());
        }
        catch (RemotePostValidationException e) {
            resp.sendError(400, Joiner.on((char)'\n').join(e.getErrors()));
        }
        finally {
            out.close();
        }
    }

    @VisibleForTesting
    boolean isXsrfSafe(HttpServletRequest req) {
        if (this.xsrfHeaderValidator.requestHasValidXsrfHeader(req)) {
            return true;
        }
        XsrfAwareRequest xsrfAwareReq = new XsrfAwareRequest(req, this.xsrfTokenValidator.getXsrfParameterName());
        return this.xsrfTokenValidator.validateFormEncodedToken((HttpServletRequest)xsrfAwareReq);
    }
}

