/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.FilterChain
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.http.Parameters$FailReason
 */
package org.apache.catalina.filters;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.filters.FilterBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.http.Parameters;

public class FailedRequestFilter
extends FilterBase {
    private final Log log = LogFactory.getLog(FailedRequestFilter.class);

    @Override
    protected Log getLogger() {
        return this.log;
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (!this.isGoodRequest(request)) {
            int status;
            Parameters.FailReason reason = (Parameters.FailReason)request.getAttribute("org.apache.catalina.parameter_parse_failed_reason");
            switch (reason) {
                case IO_ERROR: {
                    status = 500;
                    break;
                }
                case POST_TOO_LARGE: {
                    status = 413;
                    break;
                }
                default: {
                    status = 400;
                }
            }
            ((HttpServletResponse)response).sendError(status);
            return;
        }
        chain.doFilter(request, response);
    }

    private boolean isGoodRequest(ServletRequest request) {
        request.getParameter("none");
        return request.getAttribute("org.apache.catalina.parameter_parse_failed") == null;
    }

    @Override
    protected boolean isConfigProblemFatal() {
        return true;
    }
}

