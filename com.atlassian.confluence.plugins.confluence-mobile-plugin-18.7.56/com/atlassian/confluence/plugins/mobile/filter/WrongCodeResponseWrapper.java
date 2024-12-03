/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpServletResponseWrapper
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.mobile.filter;

import com.atlassian.confluence.plugins.mobile.util.MobileUtil;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WrongCodeResponseWrapper
extends HttpServletResponseWrapper {
    private static final Logger log = LoggerFactory.getLogger(WrongCodeResponseWrapper.class);
    private static final String[] WRONG_CODE_CONTENT_URLS = new String[]{"/rest/experimental/content/", "/rest/api/content/"};
    private final HttpServletRequest request;

    public WrongCodeResponseWrapper(HttpServletResponse response, HttpServletRequest request) {
        super(response);
        this.request = request;
    }

    public void setStatus(int sc) {
        super.setStatus(this.resolveCorrectStatus(sc));
    }

    public void setStatus(int sc, String sm) {
        super.setStatus(this.resolveCorrectStatus(sc), sm);
    }

    public void sendError(int sc, String msg) throws IOException {
        super.sendError(this.resolveCorrectStatus(sc), msg);
    }

    public void sendError(int sc) throws IOException {
        super.sendError(this.resolveCorrectStatus(sc));
    }

    private int resolveCorrectStatus(int originalCode) {
        if (originalCode == 401 && !AuthenticatedUserThreadLocal.isAnonymousUser()) {
            log.debug("Found an incorrect 401 status code mapping for URI {}. Rewriting response status code from 401 to 403.", (Object)this.request.getRequestURI());
            return 403;
        }
        if (originalCode == 403 && AuthenticatedUserThreadLocal.isAnonymousUser()) {
            log.debug("Found an incorrect 403 status code mapping for URI {}. Session {} is invalid. Returning 401 status code.", (Object)this.request.getRequestURI(), (Object)this.request.getSession().getId());
            return 401;
        }
        if (this.contentHasWrongStatus(originalCode) && AuthenticatedUserThreadLocal.isAnonymousUser()) {
            log.debug("Found an incorrect 404 status code mapping for URI {}. Session {} is invalid. Returning 401 status code.", (Object)this.request.getRequestURI(), (Object)this.request.getSession().getId());
            return 401;
        }
        return originalCode;
    }

    private boolean contentHasWrongStatus(int originalCode) {
        return originalCode == 404 && StringUtils.startsWithAny((CharSequence)MobileUtil.extractURL(this.request), (CharSequence[])WRONG_CODE_CONTENT_URLS);
    }
}

