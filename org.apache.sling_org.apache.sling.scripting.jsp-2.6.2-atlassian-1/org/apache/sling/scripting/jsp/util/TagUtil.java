/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletRequestWrapper
 *  javax.servlet.ServletResponse
 *  javax.servlet.ServletResponseWrapper
 *  javax.servlet.jsp.PageContext
 *  org.apache.sling.api.SlingHttpServletRequest
 *  org.apache.sling.api.SlingHttpServletResponse
 *  org.slf4j.Logger
 */
package org.apache.sling.scripting.jsp.util;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestWrapper;
import javax.servlet.ServletResponse;
import javax.servlet.ServletResponseWrapper;
import javax.servlet.jsp.PageContext;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.slf4j.Logger;

public final class TagUtil {
    private TagUtil() {
    }

    public static void log(Logger log, PageContext pageContext, String message, Throwable t) {
        if (t instanceof ServletException) {
            t = TagUtil.getRootCause((ServletException)t);
        }
        if (message == null) {
            if (t == null) {
                return;
            }
            message = t.getMessage();
        }
        log.info("Problem on page {}: {}", (Object)new Object[]{pageContext.getPage(), message}, (Object)t);
    }

    public static Throwable getRootCause(ServletException e) {
        ServletException current = e;
        while (current.getRootCause() != null) {
            Throwable t = current.getRootCause();
            if (t instanceof ServletException) {
                current = (ServletException)t;
                continue;
            }
            return t;
        }
        return current;
    }

    public static SlingHttpServletRequest getRequest(PageContext pageContext) {
        ServletRequest req = pageContext.getRequest();
        while (!(req instanceof SlingHttpServletRequest)) {
            if (req instanceof ServletRequestWrapper) {
                req = ((ServletRequestWrapper)req).getRequest();
                continue;
            }
            throw new IllegalStateException("request wrong class");
        }
        return (SlingHttpServletRequest)req;
    }

    public static SlingHttpServletResponse getResponse(PageContext pageContext) {
        ServletResponse req = pageContext.getResponse();
        while (!(req instanceof SlingHttpServletResponse)) {
            if (req instanceof ServletResponseWrapper) {
                req = ((ServletResponseWrapper)req).getResponse();
                continue;
            }
            throw new IllegalStateException("response wrong class");
        }
        return (SlingHttpServletResponse)req;
    }
}

