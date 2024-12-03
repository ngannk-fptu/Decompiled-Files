/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.servlet;

import com.atlassian.plugin.servlet.DownloadException;
import com.atlassian.plugin.servlet.DownloadStrategy;
import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractFileServerServlet
extends HttpServlet {
    public static final String PATH_SEPARATOR = "/";
    public static final String RESOURCE_URL_PREFIX = "resources";
    public static final String SERVLET_PATH = "download";
    private static final Logger log = LoggerFactory.getLogger(AbstractFileServerServlet.class);

    protected final void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
        block3: {
            DownloadStrategy downloadStrategy = this.getDownloadStrategy(httpServletRequest);
            if (downloadStrategy == null) {
                httpServletResponse.sendError(404, "The file you were looking for was not found");
                return;
            }
            try {
                downloadStrategy.serveFile(httpServletRequest, httpServletResponse);
            }
            catch (DownloadException e) {
                log.debug("Error while serving file for request:" + httpServletRequest.getRequestURI(), (Throwable)e);
                if (httpServletResponse.isCommitted()) break block3;
                httpServletResponse.sendError(500, "Error while serving file");
            }
        }
    }

    protected abstract List<DownloadStrategy> getDownloadStrategies();

    private DownloadStrategy getDownloadStrategy(HttpServletRequest httpServletRequest) {
        String url = httpServletRequest.getRequestURI();
        DownloadStrategy strategy = this.findStrategy(url);
        if (strategy == null) {
            strategy = this.findStrategy(url.toLowerCase());
        }
        return strategy;
    }

    private DownloadStrategy findStrategy(String url) {
        for (DownloadStrategy downloadStrategy : this.getDownloadStrategies()) {
            if (!downloadStrategy.matches(url)) continue;
            return downloadStrategy;
        }
        return null;
    }
}

