/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.filters.AbstractHttpFilter
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 *  javax.servlet.FilterChain
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpServletResponseWrapper
 */
package com.atlassian.confluence.extra.calendar3.caldav.filter;

import com.atlassian.core.filters.AbstractHttpFilter;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class CalDavAuthenticationFilter
extends AbstractHttpFilter {
    private final ApplicationProperties applicationProperties;

    public CalDavAuthenticationFilter(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        filterChain.doFilter((ServletRequest)request, (ServletResponse)new BasicWWWWAuthenticateAddingResponse(response, this.applicationProperties));
    }

    private static final class BasicWWWWAuthenticateAddingResponse
    extends HttpServletResponseWrapper {
        private final ApplicationProperties applicationProperties;

        BasicWWWWAuthenticateAddingResponse(HttpServletResponse response, ApplicationProperties applicationProperties) {
            super(response);
            this.applicationProperties = applicationProperties;
        }

        public void sendError(int error) throws IOException {
            if (error == 401) {
                this.addBasicAuthenticateHeader();
            }
            super.sendError(error);
        }

        public void sendError(int error, String msg) throws IOException {
            if (error == 401) {
                this.addBasicAuthenticateHeader();
            }
            super.sendError(error, msg);
        }

        private void addBasicAuthenticateHeader() throws UnsupportedEncodingException {
            String realm = URLEncoder.encode(this.applicationProperties.getBaseUrl(UrlMode.ABSOLUTE), "UTF-8").replace("+", "%20").replace("*", "%2A").replace("%7E", "~");
            super.setHeader("WWW-Authenticate", String.format("Basic realm=\"%s\"", realm));
        }
    }
}

