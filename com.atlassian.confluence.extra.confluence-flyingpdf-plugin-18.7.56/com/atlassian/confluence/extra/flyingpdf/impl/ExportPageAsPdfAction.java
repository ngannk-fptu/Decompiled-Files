/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.importexport.ImportExportException
 *  com.atlassian.confluence.pages.actions.AbstractPageAwareAction
 *  com.atlassian.confluence.security.GateKeeper
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.setup.settings.ConfluenceDirectories
 *  com.atlassian.core.filters.ServletContextThreadLocal
 *  com.atlassian.user.User
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  javax.servlet.http.HttpServletRequest
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.flyingpdf.impl;

import com.atlassian.confluence.extra.flyingpdf.analytic.PageExportMetrics;
import com.atlassian.confluence.extra.flyingpdf.impl.BigBrotherPdfExporterService;
import com.atlassian.confluence.extra.flyingpdf.impl.PdfExportSemaphore;
import com.atlassian.confluence.importexport.ImportExportException;
import com.atlassian.confluence.pages.actions.AbstractPageAwareAction;
import com.atlassian.confluence.security.GateKeeper;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.setup.settings.ConfluenceDirectories;
import com.atlassian.core.filters.ServletContextThreadLocal;
import com.atlassian.user.User;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.function.Predicate;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExportPageAsPdfAction
extends AbstractPageAwareAction {
    private static final Logger log = LoggerFactory.getLogger(ExportPageAsPdfAction.class);
    private GateKeeper gateKeeper;
    private String downloadPath;
    private BigBrotherPdfExporterService pdfExporterService;
    private PdfExportSemaphore pdfExportSemaphore;
    private ConfluenceDirectories confluenceDirectories;

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() {
        this.pdfExportSemaphore.run(this::doExecute);
        return "download";
    }

    private void doExecute() {
        try {
            String contextPath = this.getServletRequest().getContextPath();
            File exportedDocument = this.pdfExporterService.createPdfForPage(this.getRemoteUser(), this.getPage(), contextPath, new PageExportMetrics());
            String rawPath = this.prepareDownloadPath(exportedDocument);
            this.downloadPath = this.addPdfContentTypeParam(this.replaceBackslashes(this.encodePath(rawPath)));
            String gatekeeperPath = this.replaceBackslashes(rawPath);
            Predicate<User> permissionPredicate = u -> this.permissionManager.hasPermission(u, Permission.VIEW, (Object)this.getPage());
            this.gateKeeper.addKey(gatekeeperPath, this.getRemoteUser(), permissionPredicate);
        }
        catch (ImportExportException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected HttpServletRequest getServletRequest() {
        return ServletContextThreadLocal.getRequest();
    }

    public boolean isPermitted() {
        return this.pdfExporterService.isPermitted(this.getRemoteUser(), this.getPage());
    }

    private String prepareDownloadPath(File file) throws IOException {
        String exportDir = this.confluenceDirectories.getTempDirectory().toString();
        String canonicalPath = file.getCanonicalPath();
        int exportDirIndex = canonicalPath.indexOf(exportDir);
        String urlPath = null;
        if (exportDirIndex != -1) {
            urlPath = canonicalPath.substring(exportDirIndex + exportDir.length());
        } else {
            for (File root : File.listRoots()) {
                String rootPath = root.getCanonicalPath();
                int rootIndex = canonicalPath.indexOf(rootPath);
                if (rootIndex == -1) continue;
                urlPath = canonicalPath.substring(rootIndex + rootPath.length());
                break;
            }
            if (urlPath == null) {
                log.warn("Path to the download [ {} ] has not been stripped of any parent directories, and may be invalid", (Object)file);
                urlPath = file.getPath();
            }
        }
        return "/download/export" + urlPath;
    }

    private String addPdfContentTypeParam(String url) {
        return url + "?contentType=application/pdf";
    }

    private String replaceBackslashes(String relativeFilePath) {
        return relativeFilePath.replaceAll("\\\\", "/");
    }

    private String encodePath(String urlPath) throws UnsupportedEncodingException {
        int lastSlash = ((String)urlPath).lastIndexOf(File.separator);
        String prefix = "";
        if (lastSlash != -1) {
            prefix = ((String)urlPath).substring(0, lastSlash);
        }
        String suffix = ((String)urlPath).substring(lastSlash + 1, ((String)urlPath).length());
        String encodedSuffix = URLEncoder.encode(suffix, "UTF-8");
        urlPath = lastSlash != -1 ? prefix + "/" + encodedSuffix : encodedSuffix;
        return urlPath;
    }

    public void setGateKeeper(GateKeeper gateKeeper) {
        this.gateKeeper = gateKeeper;
    }

    public String getDownloadPath() {
        return this.downloadPath;
    }

    public void setDiagnosticsPdfExporterService(BigBrotherPdfExporterService pdfExporterService) {
        this.pdfExporterService = pdfExporterService;
    }

    public void setPdfExportSemaphore(PdfExportSemaphore pdfExportSemaphore) {
        this.pdfExportSemaphore = pdfExportSemaphore;
    }

    public void setConfluenceDirectories(ConfluenceDirectories confluenceDirectories) {
        this.confluenceDirectories = confluenceDirectories;
    }
}

