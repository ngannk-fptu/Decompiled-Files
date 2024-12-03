/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.websudo.WebSudoManager
 *  com.atlassian.sal.api.websudo.WebSudoSessionException
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  com.google.common.io.ByteStreams
 *  javax.servlet.ServletContext
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.troubleshooting.stp.servlet;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.websudo.WebSudoManager;
import com.atlassian.sal.api.websudo.WebSudoSessionException;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.atlassian.troubleshooting.stp.events.StpSupportZipDownloadEvent;
import com.atlassian.troubleshooting.stp.salext.SupportApplicationInfo;
import com.atlassian.troubleshooting.stp.servlet.StpServletUtils;
import com.google.common.io.ByteStreams;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SupportZipFileServerServlet
extends HttpServlet {
    private static final String SUPPORT_ZIP_FILENAME_PATTERN = "(JIRA|Confluence|Bamboo|FishEye|Bitbucket|Stash|Crowd)(_[^_\\\\/]+)?_support_([2][0][1-3][0-9])-([0-9]+)-([0-3][0-9])-([0-2][0-9])-([0-5][0-9])-([0-5][0-9]\\.zip)";
    private static final Logger LOGGER = LoggerFactory.getLogger(SupportZipFileServerServlet.class);
    private final SupportApplicationInfo applicationInfo;
    private final EventPublisher eventPublisher;
    private final TemplateRenderer templateRenderer;
    private final UserManager userManager;
    private final WebSudoManager webSudoManager;
    private final StpServletUtils stpServletUtils;

    public SupportZipFileServerServlet(SupportApplicationInfo applicationInfo, StpServletUtils stpServletUtils, EventPublisher eventPublisher, TemplateRenderer templateRenderer, UserManager userManager, WebSudoManager webSudoManager) {
        this.applicationInfo = Objects.requireNonNull(applicationInfo);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
        this.templateRenderer = Objects.requireNonNull(templateRenderer);
        this.userManager = Objects.requireNonNull(userManager);
        this.webSudoManager = Objects.requireNonNull(webSudoManager);
        this.stpServletUtils = Objects.requireNonNull(stpServletUtils);
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        this.stpServletUtils.initializeHeader(res);
        boolean isAdmin = this.isSysAdmin(req, res);
        if (isAdmin) {
            String fileName = req.getParameter("file");
            File downloadFile = new File(this.applicationInfo.getExportDirectory(), fileName);
            boolean isValid = this.isValidSupportZip(fileName);
            if (isValid && downloadFile.exists()) {
                try (FileInputStream inputStream = new FileInputStream(downloadFile);){
                    this.webSudoManager.willExecuteWebSudoRequest(req);
                    ServletContext context = this.getServletContext();
                    String mimeType = context.getMimeType(fileName);
                    if (mimeType == null) {
                        mimeType = "application/octet-stream";
                    }
                    res.setContentType(mimeType);
                    long fileLength = downloadFile.length();
                    if (fileLength <= Integer.MAX_VALUE) {
                        res.setContentLength((int)fileLength);
                    }
                    String headerKey = "Content-Disposition";
                    String headerValue = String.format("attachment; filename=\"%s\"", downloadFile.getName());
                    res.setHeader("Content-Disposition", headerValue);
                    ByteStreams.copy((InputStream)inputStream, (OutputStream)res.getOutputStream());
                    this.triggerAnalyticsEvent(this.applicationInfo.getApplicationName(), downloadFile.length());
                }
                catch (WebSudoSessionException wes) {
                    this.webSudoManager.enforceWebSudoProtection(req, res);
                }
            } else {
                res.setStatus(404);
                this.templateRenderer.render("/templates/html/file-not-found.vm", new HashMap(), (Writer)res.getWriter());
            }
        }
    }

    private boolean isValidSupportZip(String fileName) {
        Pattern fileNamePattern = Pattern.compile(SUPPORT_ZIP_FILENAME_PATTERN);
        Matcher matcher = fileNamePattern.matcher(fileName);
        return matcher.matches();
    }

    private boolean isSysAdmin(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String username = this.userManager.getRemoteUsername(req);
        if (username == null) {
            res.setStatus(403);
            this.templateRenderer.render("/templates/html/ajax-not-logged-in.vm", new HashMap(), (Writer)res.getWriter());
            return false;
        }
        if (this.userManager.isSystemAdmin(username)) {
            return true;
        }
        res.setStatus(403);
        this.templateRenderer.render("/templates/html/ajax-no-permission.vm", new HashMap(), (Writer)res.getWriter());
        return false;
    }

    private void triggerAnalyticsEvent(String applicationName, long fileSize) {
        StpSupportZipDownloadEvent event = new StpSupportZipDownloadEvent(applicationName, fileSize);
        this.eventPublisher.publish((Object)event);
    }
}

