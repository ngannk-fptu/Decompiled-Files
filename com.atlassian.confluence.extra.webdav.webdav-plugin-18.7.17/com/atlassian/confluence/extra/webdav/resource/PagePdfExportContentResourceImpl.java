/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.extra.flyingpdf.PdfExporterService
 *  com.atlassian.confluence.importexport.ImportExportException
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  org.apache.struts2.ServletActionContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.webdav.resource;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.extra.flyingpdf.PdfExporterService;
import com.atlassian.confluence.extra.webdav.ConfluenceDavSession;
import com.atlassian.confluence.extra.webdav.resource.AbstractPageExportContentResource;
import com.atlassian.confluence.importexport.ImportExportException;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.lock.LockManager;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PagePdfExportContentResourceImpl
extends AbstractPageExportContentResource {
    public static final String CONTENT_TYPE = "application/pdf";
    public static final String DISPLAY_NAME_SUFFIX = ".pdf";
    private static final Logger LOGGER = LoggerFactory.getLogger(PagePdfExportContentResourceImpl.class);
    private final PdfExporterService pdfExporterService;

    public PagePdfExportContentResourceImpl(DavResourceLocator davResourceLocator, DavResourceFactory davResourceFactory, LockManager lockManager, ConfluenceDavSession davSession, @ComponentImport PageManager pageManager, @ComponentImport PdfExporterService pdfExporterService, String spaceKey, String pageTitle) {
        super(davResourceLocator, davResourceFactory, lockManager, davSession, pageManager, spaceKey, pageTitle);
        this.pdfExporterService = pdfExporterService;
    }

    @Override
    public boolean exists() {
        return super.exists() && !((ConfluenceDavSession)this.getSession()).getResourceStates().isContentPdfExportHidden((ContentEntityObject)this.getPage());
    }

    @Override
    protected String getExportSuffix() {
        return DISPLAY_NAME_SUFFIX;
    }

    private File generatePdfExportFromPdfExporterService() throws ImportExportException {
        return this.pdfExporterService.createPdfForPage((User)AuthenticatedUserThreadLocal.get(), (AbstractPage)this.getPage(), ServletActionContext.getRequest().getContextPath());
    }

    @Override
    protected InputStream getContentInternal() {
        try {
            return new BufferedInputStream(Files.newInputStream(this.generatePdfExportFromPdfExporterService().toPath(), new OpenOption[0]));
        }
        catch (Exception e) {
            LOGGER.error("Error exporting " + this.getPage() + " as PDF. Returning InputStream with one byte", (Throwable)e);
            return new ByteArrayInputStream(new byte[1]);
        }
    }

    @Override
    protected String getContentType() {
        return CONTENT_TYPE;
    }

    @Override
    public String getDisplayName() {
        return this.getPage().getTitle() + DISPLAY_NAME_SUFFIX;
    }
}

