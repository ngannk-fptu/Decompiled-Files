/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.version.report;

import org.apache.jackrabbit.webdav.WebdavRequest;
import org.apache.jackrabbit.webdav.WebdavRequestContext;
import org.apache.jackrabbit.webdav.server.WebdavRequestContextHolder;
import org.apache.jackrabbit.webdav.version.report.Report;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractReport
implements Report {
    private static Logger log = LoggerFactory.getLogger(AbstractReport.class);

    protected String normalizeResourceHref(String href) {
        WebdavRequest request;
        if (href == null) {
            return href;
        }
        WebdavRequestContext requestContext = WebdavRequestContextHolder.getContext();
        WebdavRequest webdavRequest = request = requestContext != null ? requestContext.getRequest() : null;
        if (request == null) {
            log.error("WebdavRequest is unavailable in the current execution context.");
            return href;
        }
        String contextPath = request.getContextPath();
        if (!contextPath.isEmpty() && href.startsWith(contextPath)) {
            return href.substring(contextPath.length());
        }
        return href;
    }
}

