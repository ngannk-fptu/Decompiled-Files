/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.importexport.resource.DownloadResourceManager
 *  com.atlassian.confluence.importexport.resource.DownloadResourceNotFoundException
 *  com.atlassian.confluence.importexport.resource.DownloadResourceReader
 *  com.atlassian.confluence.importexport.resource.UnauthorizedDownloadResourceException
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.renderer.util.UrlUtil
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.flyingpdf.impl;

import com.atlassian.confluence.extra.flyingpdf.impl.AbstractExportUserAgent;
import com.atlassian.confluence.importexport.resource.DownloadResourceManager;
import com.atlassian.confluence.importexport.resource.DownloadResourceNotFoundException;
import com.atlassian.confluence.importexport.resource.DownloadResourceReader;
import com.atlassian.confluence.importexport.resource.UnauthorizedDownloadResourceException;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.renderer.util.UrlUtil;
import java.io.InputStream;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xhtmlrenderer.pdf.ITextOutputDevice;

public class ConfluenceExportUserAgent
extends AbstractExportUserAgent {
    private static final Logger LOG = LoggerFactory.getLogger(ConfluenceExportUserAgent.class);
    private DownloadResourceManager resourceManager;

    public ConfluenceExportUserAgent(ITextOutputDevice device, String baseUrl, String cdnUrl, DownloadResourceManager resourceManager) {
        super(device, baseUrl, cdnUrl);
        this.resourceManager = resourceManager;
    }

    @Override
    protected InputStream fetchResourceFromConfluence(String relativeUri, String decodedUri) {
        if (this.resourceManager.matches(decodedUri)) {
            String strippedUri;
            String userName = AuthenticatedUserThreadLocal.getUsername();
            DownloadResourceReader downloadResourceReader = this.getResourceReader(decodedUri, userName, strippedUri = this.stripQueryString(decodedUri));
            if (downloadResourceReader == null) {
                strippedUri = this.stripQueryString(relativeUri);
                downloadResourceReader = this.getResourceReader(relativeUri, userName, strippedUri);
            }
            if (downloadResourceReader != null) {
                try {
                    return downloadResourceReader.getStreamForReading();
                }
                catch (Exception e) {
                    this.log(Level.SEVERE, "Couldn't retrieve image resource " + decodedUri + " during Confluence export");
                }
            }
        }
        return null;
    }

    @Override
    protected void log(Level level, String message) {
        if (level.equals(Level.SEVERE)) {
            LOG.error(message);
        } else {
            LOG.debug(message);
        }
    }

    private DownloadResourceReader getResourceReader(String uri, String userName, String strippedUri) {
        DownloadResourceReader downloadResourceReader = null;
        try {
            downloadResourceReader = this.resourceManager.getResourceReader(userName, strippedUri, UrlUtil.getQueryParameters((String)uri));
        }
        catch (UnauthorizedDownloadResourceException ex) {
            this.log(Level.WARNING, "Not authorized to download resource " + uri + ", error: " + ex.getMessage());
        }
        catch (DownloadResourceNotFoundException ex) {
            this.log(Level.WARNING, "No resource found for resource " + uri + ", error: " + ex.getMessage());
        }
        return downloadResourceReader;
    }

    private String stripQueryString(String uri) {
        int queryIndex = uri.indexOf(63);
        if (queryIndex > 0) {
            uri = uri.substring(0, queryIndex);
        }
        return uri;
    }
}

