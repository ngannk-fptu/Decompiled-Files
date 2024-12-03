/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.FilesystemUtils
 *  com.atlassian.core.util.thumbnail.ThumbnailDimension
 *  com.atlassian.favicon.core.Constants
 *  com.atlassian.favicon.core.FaviconManager
 *  com.atlassian.favicon.core.ImageType
 *  com.atlassian.favicon.core.StoredFavicon
 *  com.atlassian.plugin.servlet.ResourceDownloadUtils
 *  javax.servlet.Filter
 *  javax.servlet.FilterChain
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletOutputStream
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.io.IOUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.favicon.filter;

import com.atlassian.confluence.util.FilesystemUtils;
import com.atlassian.core.util.thumbnail.ThumbnailDimension;
import com.atlassian.favicon.core.Constants;
import com.atlassian.favicon.core.FaviconManager;
import com.atlassian.favicon.core.ImageType;
import com.atlassian.favicon.core.StoredFavicon;
import com.atlassian.favicon.filter.RequestUtils;
import com.atlassian.plugin.servlet.ResourceDownloadUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FaviconInterceptor
implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(FaviconInterceptor.class);
    private FilterConfig filterConfig;
    private FaviconManager faviconManager;

    public FaviconInterceptor(FaviconManager aFaviconManager) {
        this.faviconManager = aFaviconManager;
    }

    public void init(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest) {
            Optional<StoredFavicon> favicon;
            HttpServletResponse res = (HttpServletResponse)response;
            HttpServletRequest req = (HttpServletRequest)request;
            String requestURL = req.getRequestURI().substring(req.getContextPath().length());
            Optional<ImageType> requestedImageType = RequestUtils.getImageTypeFromRequestURL(requestURL);
            if (requestedImageType.isPresent()) {
                ThumbnailDimension requestedDimension = RequestUtils.getDesiredSizeFromRequestURL(requestURL).orElse(Constants.DEFAULT_DIMENSION);
                favicon = this.faviconManager.getFavicon(requestedImageType.get(), requestedDimension);
                if (!favicon.isPresent()) {
                    favicon = this.getDefaultIconFromFile(requestURL, requestedImageType.get());
                }
            } else {
                favicon = this.getDefaultIconFromFile(requestURL, ImageType.ICO);
            }
            if (favicon.isPresent()) {
                this.writeFaviconToResponse(favicon.get(), req, res);
            } else {
                res.setStatus(404);
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }

    public void destroy() {
    }

    private Optional<StoredFavicon> getDefaultIconFromFile(String aPath, ImageType anImageType) {
        try {
            if (FilesystemUtils.containsEncodedPathTraversal((String)aPath)) {
                logger.warn("Detected path traversal with a request to a path {}", (Object)aPath);
                return Optional.empty();
            }
            File file = new File(this.filterConfig.getServletContext().getRealPath(RequestUtils.getFilePathWithoutStaticResourcePrefix(aPath)));
            return Optional.of(new StoredFavicon((InputStream)new FileInputStream(file), anImageType.toString(), file.length()));
        }
        catch (IOException e) {
            return Optional.empty();
        }
    }

    private void writeFaviconToResponse(StoredFavicon aFavicon, HttpServletRequest aRequest, HttpServletResponse aResponse) throws IOException {
        if (aRequest.getServletPath().startsWith("/s/")) {
            ResourceDownloadUtils.addPublicCachingHeaders((HttpServletRequest)aRequest, (HttpServletResponse)aResponse);
        }
        aResponse.setContentLength((int)aFavicon.getContentLength());
        aResponse.setContentType(aFavicon.getContentType());
        try (InputStream imageDataStream = aFavicon.getImageDataStream();
             ServletOutputStream outputStream = aResponse.getOutputStream();){
            IOUtils.copy((InputStream)imageDataStream, (OutputStream)outputStream);
            outputStream.flush();
        }
    }
}

