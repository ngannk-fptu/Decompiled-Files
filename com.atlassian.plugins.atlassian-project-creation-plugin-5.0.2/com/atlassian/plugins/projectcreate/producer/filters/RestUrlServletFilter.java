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
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.projectcreate.producer.filters;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestUrlServletFilter
implements Filter {
    static final String REST_BASE_INIT_PARAM = "rest-base";
    static final String DEFAULT_VERSION_INIT_PARAM = "rest-default-version";
    private final Logger logger = LoggerFactory.getLogger(RestUrlServletFilter.class);
    private String restBase;
    private String defaultVersion;

    public void init(FilterConfig filterConfig) throws ServletException {
        this.restBase = filterConfig.getInitParameter(REST_BASE_INIT_PARAM);
        this.defaultVersion = filterConfig.getInitParameter(DEFAULT_VERSION_INIT_PARAM);
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest)request;
            String requestUrl = httpRequest.getRequestURI();
            String restUrl = requestUrl.substring(httpRequest.getContextPath().length() + this.restBase.length());
            if (restUrl.matches("^/[\\d.]{1,5}($|/.*)")) {
                if (!restUrl.endsWith("/")) {
                    String rewriteTarget = this.restBase + restUrl + "/";
                    this.logger.debug("Rewriting request to " + rewriteTarget);
                    request.getRequestDispatcher(rewriteTarget).forward(request, response);
                    return;
                }
            } else {
                if (!restUrl.endsWith("/")) {
                    restUrl = restUrl + "/";
                }
                String rewriteTarget = this.restBase + "/" + this.defaultVersion + restUrl;
                this.logger.debug("Rewriting request to " + rewriteTarget);
                request.getRequestDispatcher(rewriteTarget).forward(request, response);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    public void destroy() {
    }
}

