/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.Draft
 *  com.atlassian.confluence.pages.DraftManager
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringUtils
 */
package com.benryan.servlet;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.pages.DraftManager;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.benryan.components.ContentResolver;
import java.io.IOException;
import java.text.ParseException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;

public class DownloadRedirectServlet
extends HttpServlet {
    private static final String TITLE_PARAM = "title";
    private static final String DATE_PARAM = "date";
    private static final String SPACEKEY_PARAM = "space";
    private static final String FILENAME_PARAM = "filename";
    private static final String CONTEXT_ID_PARAM = "contextid";
    private static final String CONTEXT_CONTENT_TYPE_PARAM = "contexttype";
    private final ContentResolver contentResolver;
    private final PageManager pageManager;
    private final DraftManager draftManager;

    public DownloadRedirectServlet(ContentResolver contentResolver, @ComponentImport PageManager pageManager, @ComponentImport DraftManager draftManager) {
        this.contentResolver = contentResolver;
        this.pageManager = pageManager;
        this.draftManager = draftManager;
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ContentEntityObject ceo;
        long contextId;
        String title = req.getParameter(TITLE_PARAM);
        String dateStr = req.getParameter(DATE_PARAM);
        String spaceKey = req.getParameter(SPACEKEY_PARAM);
        String filename = req.getParameter(FILENAME_PARAM);
        String contextIdStr = req.getParameter(CONTEXT_ID_PARAM);
        String contextType = req.getParameter(CONTEXT_CONTENT_TYPE_PARAM);
        if (StringUtils.isBlank((CharSequence)spaceKey) || StringUtils.isBlank((CharSequence)contextIdStr) || StringUtils.isBlank((CharSequence)filename) || StringUtils.isBlank((CharSequence)contextType)) {
            resp.sendError(400, "Request missing required parameters.");
            return;
        }
        try {
            contextId = Long.valueOf(contextIdStr);
        }
        catch (NumberFormatException ex) {
            resp.sendError(400, "The contextid must be numeric.");
            return;
        }
        ContentEntityObject context = this.getContextEntity(contextId, contextIdStr, spaceKey, contextType);
        try {
            ceo = this.contentResolver.getContent(title, spaceKey, dateStr, context);
        }
        catch (ParseException ex) {
            resp.sendError(400, "The date parameter was not of the format mm/dd/yyyy");
            return;
        }
        if (ceo == null) {
            resp.sendError(404, "The requested attachment could not be found.");
        } else {
            resp.sendRedirect(req.getContextPath() + "/download/attachments/" + ceo.getId() + "/" + filename);
        }
    }

    private ContentEntityObject getContextEntity(long contextId, String contextIdStr, String spaceKey, String contentType) {
        if (Draft.NEW.equals(contextIdStr)) {
            return this.draftManager.findDraft(StringUtils.isBlank((CharSequence)contextIdStr) ? null : Long.valueOf(contextIdStr), AuthenticatedUserThreadLocal.getUsername(), contentType, spaceKey);
        }
        return this.pageManager.getById(contextId);
    }
}

