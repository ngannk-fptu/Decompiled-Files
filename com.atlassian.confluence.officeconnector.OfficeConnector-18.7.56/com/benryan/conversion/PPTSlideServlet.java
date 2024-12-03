/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.spring.container.LazyComponentReference
 *  com.atlassian.user.User
 *  com.google.common.annotations.VisibleForTesting
 *  javax.servlet.ServletException
 *  javax.servlet.ServletOutputStream
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.math.NumberUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.benryan.conversion;

import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.spring.container.LazyComponentReference;
import com.atlassian.user.User;
import com.benryan.components.SlideCacheManager;
import com.benryan.conversion.SlidePageConversionData;
import com.google.common.annotations.VisibleForTesting;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PPTSlideServlet
extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(PPTSlideServlet.class);
    private final int TASK_WAIT_TIME_MILLISEC = Integer.getInteger("officeconnector.slide.conversion.waittime", 300);
    private final AttachmentManager fileManager;
    private final PermissionManager permissionManager;
    private final SlideCacheManager slideManager;
    private final LazyComponentReference<ContentEntityManager> contentEntityManagerRef;
    private ContentEntityManager contentEntityManager;

    public PPTSlideServlet(@ComponentImport AttachmentManager fileManager, @ComponentImport PermissionManager permissionManager, SlideCacheManager slideManager) {
        this.fileManager = fileManager;
        this.permissionManager = permissionManager;
        this.slideManager = slideManager;
        this.contentEntityManagerRef = new LazyComponentReference("contentEntityManager");
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        block18: {
            String attachment = req.getParameter("attachment");
            int attachmentVer = NumberUtils.toInt((String)req.getParameter("attachmentVer"), (int)0);
            String strSlideNum = req.getParameter("slide");
            String strReady = req.getParameter("ready");
            String contentId = req.getParameter("pageId");
            if (this.contentEntityManager == null) {
                throw new ServletException("Cannot get ContentEntityManager");
            }
            ContentEntityObject contentObj = null;
            if (contentId != null) {
                try {
                    long l = Long.parseLong(contentId);
                    contentObj = this.contentEntityManager.getById(l);
                }
                catch (NumberFormatException l) {
                    // empty catch block
                }
            }
            if (contentObj == null) {
                throw new ServletException("Unable to locate content containing the attachment");
            }
            Attachment attObj = this.fileManager.getAttachment(contentObj, attachment, attachmentVer);
            if (attObj == null) {
                throw new ServletException("Unable to locate attachment");
            }
            User user = AuthenticatedUserThreadLocal.getUser();
            if (!this.permissionManager.hasPermission(user, Permission.VIEW, (Object)attObj)) {
                resp.sendError(403);
                return;
            }
            int slideNum = !StringUtils.isEmpty((CharSequence)strSlideNum) ? Integer.parseInt(strSlideNum) : 0;
            boolean readyQuery = !StringUtils.isEmpty((CharSequence)strReady);
            try {
                Future<SlidePageConversionData> task = this.slideManager.getSlideConversionData(attObj, slideNum);
                if (task == null) break block18;
                try {
                    SlidePageConversionData slide = task.get(this.TASK_WAIT_TIME_MILLISEC, TimeUnit.MILLISECONDS);
                    if (slide != null) {
                        if (readyQuery) {
                            int numSlides = slide.getParent().getNumSlides();
                            if (numSlides > 0) {
                                resp.setContentType("text/json");
                                resp.setHeader("Cache-Control", "max-age=7200");
                                PrintWriter writer = resp.getWriter();
                                writer.write("{\"numSlides\": " + numSlides + "}");
                            } else {
                                this.writeErrorResponse(resp);
                            }
                        } else {
                            resp.setContentType("image/jpeg");
                            resp.setHeader("Cache-Control", "max-age=7200");
                            ServletOutputStream out = resp.getOutputStream();
                            this.slideManager.writeSlideToStream(slide, (OutputStream)out);
                            out.flush();
                        }
                        break block18;
                    }
                    this.writeErrorResponse(resp);
                }
                catch (TimeoutException te) {
                    log.info("Conversion task timed out for attachment : " + attachment + " with page number " + slideNum + ", waited : " + this.TASK_WAIT_TIME_MILLISEC + ", now executing in background.");
                    if (slideNum == 0) {
                        resp.setStatus(404);
                        this.sendErrorMessage(resp, "converting");
                        break block18;
                    }
                    resp.sendError(404);
                }
            }
            catch (ExecutionException e) {
                this.writeErrorResponse(resp);
                log.error("Could not convert slide for attachment " + attachment + ", page num: " + slideNum, (Throwable)e);
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                this.writeErrorResponse(resp);
            }
        }
    }

    private void writeErrorResponse(HttpServletResponse resp) throws IOException {
        resp.setStatus(500);
        this.sendErrorMessage(resp, "There was a problem converting this attachment.");
    }

    private void sendErrorMessage(HttpServletResponse resp, String message) throws IOException {
        resp.setContentType("text/json");
        PrintWriter writer = resp.getWriter();
        writer.write("{\"error\": \"" + message + "\"}");
    }

    public void init() throws ServletException {
        super.init();
        this.contentEntityManager = (ContentEntityManager)this.contentEntityManagerRef.get();
    }

    @VisibleForTesting
    public void setContentEntityManager(@ComponentImport(value="contentEntityManager") ContentEntityManager contentEntityManager) {
        this.contentEntityManager = contentEntityManager;
    }
}

