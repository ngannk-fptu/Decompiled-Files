/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.Filter
 *  javax.servlet.FilterChain
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  org.slf4j.MDC
 */
package ch.qos.logback.classic.helpers;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.MDC;

public class MDCInsertingServletFilter
implements Filter {
    public void destroy() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        this.insertIntoMDC(request);
        try {
            chain.doFilter(request, response);
        }
        finally {
            this.clearMDC();
        }
    }

    void insertIntoMDC(ServletRequest request) {
        MDC.put((String)"req.remoteHost", (String)request.getRemoteHost());
        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpServletRequest = (HttpServletRequest)request;
            MDC.put((String)"req.requestURI", (String)httpServletRequest.getRequestURI());
            StringBuffer requestURL = httpServletRequest.getRequestURL();
            if (requestURL != null) {
                MDC.put((String)"req.requestURL", (String)requestURL.toString());
            }
            MDC.put((String)"req.method", (String)httpServletRequest.getMethod());
            MDC.put((String)"req.queryString", (String)httpServletRequest.getQueryString());
            MDC.put((String)"req.userAgent", (String)httpServletRequest.getHeader("User-Agent"));
            MDC.put((String)"req.xForwardedFor", (String)httpServletRequest.getHeader("X-Forwarded-For"));
        }
    }

    void clearMDC() {
        MDC.remove((String)"req.remoteHost");
        MDC.remove((String)"req.requestURI");
        MDC.remove((String)"req.queryString");
        MDC.remove((String)"req.requestURL");
        MDC.remove((String)"req.method");
        MDC.remove((String)"req.userAgent");
        MDC.remove((String)"req.xForwardedFor");
    }

    public void init(FilterConfig arg0) throws ServletException {
    }
}

