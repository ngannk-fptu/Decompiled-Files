/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.servlet.DownloadException
 *  com.atlassian.plugin.servlet.DownloadableResource
 *  javax.servlet.ServletOutputStream
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.webresource.transformer;

import com.atlassian.plugin.servlet.DownloadException;
import com.atlassian.plugin.servlet.DownloadableResource;
import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractTransformedDownloadableResource
implements DownloadableResource {
    private static final Logger log = LoggerFactory.getLogger(AbstractTransformedDownloadableResource.class);
    private final DownloadableResource originalResource;

    public AbstractTransformedDownloadableResource(DownloadableResource originalResource) {
        this.originalResource = originalResource;
    }

    public boolean isResourceModified(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        return this.originalResource.isResourceModified(httpServletRequest, httpServletResponse);
    }

    public void serveResource(HttpServletRequest httpServletRequest, HttpServletResponse response) throws DownloadException {
        ServletOutputStream out;
        String contentType;
        if (log.isDebugEnabled()) {
            log.debug("Start to serve transformed downloadable resource: {}", (Object)this);
        }
        if (StringUtils.isNotBlank((CharSequence)(contentType = this.getContentType()))) {
            response.setContentType(contentType);
        }
        try {
            out = response.getOutputStream();
        }
        catch (IOException e) {
            throw new DownloadException((Exception)e);
        }
        this.streamResource((OutputStream)out);
    }

    public String getContentType() {
        return this.originalResource.getContentType();
    }

    protected DownloadableResource getOriginalResource() {
        return this.originalResource;
    }

    public String toString() {
        return "Transformed Downloadable Resource: " + this.originalResource.toString();
    }
}

