/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.FilterChain
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.gzipfilter;

import com.atlassian.gzipfilter.AbstractFilter;
import com.atlassian.gzipfilter.SelectingResponseWrapper;
import com.atlassian.gzipfilter.integration.GzipFilterIntegration;
import com.atlassian.gzipfilter.selector.GzipCompatibilitySelector;
import com.atlassian.gzipfilter.selector.GzipCompatibilitySelectorFactory;
import com.atlassian.gzipfilter.selector.NoGzipCompatibilitySelector;
import com.atlassian.gzipfilter.selector.UserAgentBasedGzipSelectorFactory;
import java.io.File;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GzipFilter
extends AbstractFilter {
    private static final Logger log = LoggerFactory.getLogger(GzipFilter.class);
    private static final String ALREADY_FILTERED = GzipFilter.class.getName() + "_already_filtered";
    private static final GzipCompatibilitySelector NO_GZIP_SELECTOR = new NoGzipCompatibilitySelector();
    private GzipCompatibilitySelectorFactory factory;
    private final GzipFilterIntegration integration;
    private FilterConfig filterConfig;
    protected static final String LEGACY_INIT_PARAM = "urlrewrite.configfile";
    protected static final String LEGACY_CONFIG_FILE = "urlrewrite-gzip.xml";

    public GzipFilter(GzipFilterIntegration integration) {
        this.integration = integration;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String path;
        if (filterConfig.getInitParameter(LEGACY_INIT_PARAM) != null) {
            throw new IllegalArgumentException("Url rewrite is no longer used in gzip filter, you provided urlrewrite.configfile as init param");
        }
        ServletContext servletContext = filterConfig.getServletContext();
        if (servletContext != null && (path = servletContext.getRealPath("/WEB-INF/urlrewrite-gzip.xml")) != null && new File(path).exists()) {
            throw new IllegalArgumentException("Url rewrite is no longer used in gzip filter, but you have urlrewrite-gzip.xml in web-inf directory");
        }
        this.filterConfig = filterConfig;
        this.factory = new UserAgentBasedGzipSelectorFactory(filterConfig);
        super.init(filterConfig);
    }

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        if (req.getAttribute(ALREADY_FILTERED) == null) {
            this.doFilterInternal(req, res, chain);
        } else {
            chain.doFilter(req, res);
        }
    }

    private void doFilterInternal(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        req.setAttribute(ALREADY_FILTERED, (Object)Boolean.TRUE);
        if (req instanceof HttpServletRequest && this.integration.useGzip() && this.isTopLevelRequest(req)) {
            HttpServletRequest request = (HttpServletRequest)req;
            HttpServletResponse response = (HttpServletResponse)res;
            GzipCompatibilitySelector selector = this.getCompatibilitySelector(request);
            if (selector.shouldGzip()) {
                log.debug("GZIP supported, compressing.");
                SelectingResponseWrapper wrappedResponse = new SelectingResponseWrapper(response, selector, this.integration.getResponseEncoding(request));
                chain.doFilter(req, (ServletResponse)wrappedResponse);
                wrappedResponse.finishResponse();
                return;
            }
        }
        chain.doFilter(req, res);
    }

    private GzipCompatibilitySelector getCompatibilitySelector(HttpServletRequest request) {
        String acceptEncodingHeader = request.getHeader("accept-encoding");
        if (acceptEncodingHeader == null || acceptEncodingHeader.indexOf("gzip") == -1) {
            return NO_GZIP_SELECTOR;
        }
        return this.getFactory().getSelector(this.filterConfig, request);
    }

    protected GzipCompatibilitySelectorFactory getFactory() {
        return this.factory;
    }

    private boolean isTopLevelRequest(ServletRequest request) {
        return request.getAttribute("javax.servlet.include.servlet_path") == null;
    }
}

