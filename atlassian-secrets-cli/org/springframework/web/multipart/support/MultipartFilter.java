/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.FilterChain
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.springframework.web.multipart.support;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

public class MultipartFilter
extends OncePerRequestFilter {
    public static final String DEFAULT_MULTIPART_RESOLVER_BEAN_NAME = "filterMultipartResolver";
    private final MultipartResolver defaultMultipartResolver = new StandardServletMultipartResolver();
    private String multipartResolverBeanName = "filterMultipartResolver";

    public void setMultipartResolverBeanName(String multipartResolverBeanName) {
        this.multipartResolverBeanName = multipartResolverBeanName;
    }

    protected String getMultipartResolverBeanName() {
        return this.multipartResolverBeanName;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        HttpServletRequest processedRequest;
        MultipartResolver multipartResolver = this.lookupMultipartResolver(request);
        if (multipartResolver.isMultipart(processedRequest = request)) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Resolving multipart request [" + processedRequest.getRequestURI() + "] with MultipartFilter");
            }
            processedRequest = multipartResolver.resolveMultipart(processedRequest);
        } else if (this.logger.isDebugEnabled()) {
            this.logger.debug("Request [" + processedRequest.getRequestURI() + "] is not a multipart request");
        }
        try {
            filterChain.doFilter((ServletRequest)processedRequest, (ServletResponse)response);
        }
        finally {
            if (processedRequest instanceof MultipartHttpServletRequest) {
                multipartResolver.cleanupMultipart((MultipartHttpServletRequest)processedRequest);
            }
        }
    }

    protected MultipartResolver lookupMultipartResolver(HttpServletRequest request) {
        return this.lookupMultipartResolver();
    }

    protected MultipartResolver lookupMultipartResolver() {
        WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(this.getServletContext());
        String beanName = this.getMultipartResolverBeanName();
        if (wac != null && wac.containsBean(beanName)) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Using MultipartResolver '" + beanName + "' for MultipartFilter");
            }
            return wac.getBean(beanName, MultipartResolver.class);
        }
        return this.defaultMultipartResolver;
    }
}

