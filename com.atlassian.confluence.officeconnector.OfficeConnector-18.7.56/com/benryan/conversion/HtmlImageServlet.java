/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.RenderedContentCleaner
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.ContextPathHolder
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.plugin.services.VelocityHelperService
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.util.sandbox.Sandbox
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.conversion.convert.html.HtmlConversionResult
 *  com.atlassian.user.User
 *  com.google.common.collect.Maps
 *  javax.servlet.ServletException
 *  javax.servlet.ServletOutputStream
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.benryan.conversion;

import com.atlassian.confluence.content.render.xhtml.RenderedContentCleaner;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.ContextPathHolder;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.sandbox.Sandbox;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.conversion.convert.html.HtmlConversionResult;
import com.atlassian.user.User;
import com.benryan.components.AttachmentCacheKey;
import com.benryan.components.HtmlCacheManager;
import com.benryan.conversion.Converter;
import com.benryan.conversion.DocConverter;
import com.benryan.conversion.SandboxConversionFeature;
import com.benryan.conversion.XlsConverter;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

public class HtmlImageServlet
extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(HtmlImageServlet.class);
    private final PageManager pageManager;
    private final AttachmentManager fileManager;
    private final PermissionManager permissionManager;
    private final ContextPathHolder contextPathHolder;
    private final HtmlCacheManager htmlCacheManager;
    private final RenderedContentCleaner renderedContentCleaner;
    private VelocityHelperService velocityHelperService;
    private SandboxConversionFeature sandboxConversionFeature;
    private final Sandbox sandbox;

    public HtmlImageServlet(@ComponentImport PageManager pageManager, @ComponentImport AttachmentManager fileManager, @ComponentImport PermissionManager permissionManager, @ComponentImport ContextPathHolder contextPathHolder, HtmlCacheManager htmlCacheManager, @ComponentImport RenderedContentCleaner renderedContentCleaner, @ComponentImport VelocityHelperService velocityHelperService, SandboxConversionFeature sandboxConversionFeature, @Qualifier(value="officeConnectorConversionSandbox") Sandbox sandbox) {
        this.pageManager = pageManager;
        this.fileManager = fileManager;
        this.permissionManager = permissionManager;
        this.contextPathHolder = contextPathHolder;
        this.htmlCacheManager = htmlCacheManager;
        this.renderedContentCleaner = renderedContentCleaner;
        this.velocityHelperService = velocityHelperService;
        this.sandboxConversionFeature = sandboxConversionFeature;
        this.sandbox = sandbox;
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ContentEntityObject contentObj;
        Attachment file;
        String space = req.getParameter("space");
        String page = req.getParameter("page");
        String attachment = req.getParameter("attachment");
        String sheetName = req.getParameter("sheetName");
        String pageId = req.getParameter("pageId");
        String imgName = HtmlImageServlet.extractImageName(req);
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        if (!this.permissionManager.hasPermission((User)user, Permission.VIEW, (Object)(file = this.getAttachment(attachment, contentObj = this.getContentEntity(space, page, pageId))))) {
            resp.sendError(403);
            return;
        }
        AttachmentCacheKey dataKey = new AttachmentCacheKey(file, sheetName);
        HtmlConversionResult data = this.getHtmlConvertionData(attachment, sheetName, contentObj, file, dataKey);
        if (data != null) {
            HtmlImageServlet.sendResponse(resp, imgName, data);
        } else {
            resp.sendError(404);
        }
    }

    private HtmlConversionResult getHtmlConvertionData(String attachment, String sheetName, ContentEntityObject contentObj, Attachment file, AttachmentCacheKey dataKey) {
        HtmlConversionResult cachedData = this.htmlCacheManager.getHtmlConversionData(dataKey);
        if (cachedData == null) {
            this.convertAttachment(attachment, sheetName, contentObj, file, dataKey);
        }
        return this.htmlCacheManager.getHtmlConversionData(dataKey);
    }

    private static void sendResponse(HttpServletResponse resp, String imgName, HtmlConversionResult data) throws IOException {
        resp.setContentType(HtmlImageServlet.determineContentType(imgName));
        ServletOutputStream out = resp.getOutputStream();
        data.getImage(imgName).streamTo((OutputStream)out);
        out.flush();
    }

    private void convertAttachment(String attachment, String sheetName, ContentEntityObject contentObj, Attachment file, AttachmentCacheKey dataKey) {
        Converter converter = this.getConverter(attachment);
        Map<String, Object> argMap = this.buildConverterArguments(attachment, sheetName, contentObj, file, dataKey);
        try {
            converter.execute(argMap);
        }
        catch (Exception e) {
            log.debug("Failed to convert attachment", (Throwable)e);
        }
    }

    private static String determineContentType(String imgName) {
        String contentType = imgName.endsWith(".jpg") ? "image/jpeg" : (imgName.endsWith(".gif") ? "image/gif" : (imgName.endsWith(".png") ? "image/png" : "application/octet-stream"));
        return contentType;
    }

    private Map<String, Object> buildConverterArguments(String attachment, String sheetName, ContentEntityObject contentObj, Attachment file, AttachmentCacheKey dataKey) {
        HashMap argMap = Maps.newHashMap();
        argMap.put("pageID", String.valueOf(contentObj.getId()));
        argMap.put("attachment", attachment);
        argMap.put("fullname", dataKey);
        argMap.put("attachmentObj", file);
        argMap.put("context", this.contextPathHolder.getContextPath());
        if (sheetName != null) {
            argMap.put("sheet", sheetName);
        }
        return argMap;
    }

    private Converter getConverter(String attachment) {
        DocConverter converter = null;
        if (attachment.endsWith(".doc")) {
            converter = new DocConverter(this.htmlCacheManager, this.velocityHelperService, this.sandboxConversionFeature, this.sandbox);
        } else if (attachment.endsWith(".xls")) {
            converter = new XlsConverter(this.htmlCacheManager, this.velocityHelperService, this.sandboxConversionFeature, this.sandbox, this.renderedContentCleaner);
        }
        return converter;
    }

    private Attachment getAttachment(String attachment, ContentEntityObject contentObj) throws ServletException {
        Attachment file = this.fileManager.getAttachment(contentObj, attachment);
        if (file == null) {
            throw new ServletException("Unable to locate the attachment");
        }
        return file;
    }

    private static String extractImageName(HttpServletRequest req) {
        String imgName = req.getParameter("val");
        if (imgName.startsWith("/")) {
            imgName = imgName.substring(1);
        }
        return imgName;
    }

    private ContentEntityObject getContentEntity(String space, String page, String pageId) throws ServletException {
        Page contentObj = null;
        if (pageId != null) {
            try {
                long l = Long.parseLong(pageId);
                contentObj = this.pageManager.getById(l);
            }
            catch (NumberFormatException e) {
                log.debug("Failed to parse Page ID", (Throwable)e);
            }
        } else {
            contentObj = this.pageManager.getPage(space, page);
        }
        if (contentObj == null) {
            throw new ServletException("Unable to locate page containing the attachment");
        }
        return contentObj;
    }
}

