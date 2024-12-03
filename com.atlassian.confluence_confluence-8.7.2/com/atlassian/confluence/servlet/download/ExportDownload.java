/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.util.BootstrapUtils
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.servlet.download;

import com.atlassian.config.util.BootstrapUtils;
import com.atlassian.confluence.importexport.resource.DownloadResourceManager;
import com.atlassian.confluence.importexport.resource.DownloadResourceNotFoundException;
import com.atlassian.confluence.importexport.resource.DownloadResourceReader;
import com.atlassian.confluence.importexport.resource.UnauthorizedDownloadResourceException;
import com.atlassian.confluence.servlet.download.ServeAfterTransactionDownload;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.util.HtmlUtil;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExportDownload
extends ServeAfterTransactionDownload {
    private static final Logger log = LoggerFactory.getLogger(ExportDownload.class);
    private static final String CONTENTTYPE_PARAM_NAME = "contentType";
    private DownloadResourceManager downloadResourceManager;

    public boolean matches(String urlPath) {
        return urlPath.startsWith(ExportDownload.getExportRoot().toLowerCase());
    }

    @Override
    protected InputStream getStreamForDownload(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
        DownloadResourceReader resourceReader;
        String remoteUser = httpServletRequest.getRemoteUser();
        try {
            resourceReader = this.downloadResourceManager.getResourceReader(remoteUser, HtmlUtil.urlDecode(httpServletRequest.getRequestURI()), httpServletRequest.getParameterMap());
        }
        catch (UnauthorizedDownloadResourceException e) {
            log.error("Unauthorized attempt to access resource by {}.  For more detail turn on INFO level logging for package : com.atlassian.confluence.servlet.download ", (Object)remoteUser);
            log.info("More detail for unauthorized attempt to access resource: ", (Throwable)e);
            httpServletResponse.sendError(403);
            return null;
        }
        catch (DownloadResourceNotFoundException e) {
            log.error(e.toString(), (Throwable)e);
            httpServletResponse.sendError(404);
            return null;
        }
        String contentType = httpServletRequest.getParameter(CONTENTTYPE_PARAM_NAME);
        if (contentType == null) {
            contentType = "application/x-download";
        }
        httpServletResponse.setContentType(contentType);
        httpServletResponse.setHeader("Content-Disposition", "attachment; filename=" + ExportDownload.quoteString(resourceReader.getName()));
        httpServletResponse.setHeader("Content-Length", Long.toString(resourceReader.getContentLength()));
        return resourceReader.getStreamForReading();
    }

    public void setDownloadResourceManager(DownloadResourceManager downloadResourceManager) {
        this.downloadResourceManager = downloadResourceManager;
    }

    @Deprecated
    public static String getExportRoot() {
        BootstrapManager bootstrapManager = (BootstrapManager)BootstrapUtils.getBootstrapManager();
        return bootstrapManager.getWebAppContextPath() + "/download/export/";
    }

    private static String quoteString(String str) {
        return "\"" + str.replace("\"", "\\\"") + "\"";
    }
}

