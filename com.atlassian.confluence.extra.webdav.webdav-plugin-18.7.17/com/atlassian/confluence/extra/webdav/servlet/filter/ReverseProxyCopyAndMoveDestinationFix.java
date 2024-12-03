/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.FilterChain
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletRequestWrapper
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang.StringUtils
 */
package com.atlassian.confluence.extra.webdav.servlet.filter;

import com.atlassian.confluence.extra.webdav.servlet.filter.AbstractPrefixAwareFilter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;

public class ReverseProxyCopyAndMoveDestinationFix
extends AbstractPrefixAwareFilter {
    private static final Pattern GLOBAL_RESOURCE_PATTERN = Pattern.compile("/(\\bGlobal\\b|\\bPersonal\\b)");

    private boolean isCopyOrMoveMethod(HttpServletRequest httpServletRequest) {
        return StringUtils.equals((String)httpServletRequest.getMethod(), (String)"MOVE") || StringUtils.equals((String)httpServletRequest.getMethod(), (String)"COPY");
    }

    private HttpServletRequest rewriteDestinationHeader(HttpServletRequest httpServletRequest) {
        Matcher globalResourcePatternMatcher;
        String destination = httpServletRequest.getHeader("Destination");
        if (StringUtils.isNotBlank((String)destination) && (globalResourcePatternMatcher = GLOBAL_RESOURCE_PATTERN.matcher(destination)).find()) {
            String rewrittenDestination = new StringBuffer().append(httpServletRequest.getScheme()).append("://").append(httpServletRequest.getHeader("Host")).append(httpServletRequest.getContextPath()).append(this.getPrefix()).append(destination.substring(globalResourcePatternMatcher.start())).toString();
            return new HttpServletRequestWrapperWithModifiedDestinationHeader(httpServletRequest, rewrittenDestination);
        }
        return httpServletRequest;
    }

    @Override
    public void doFilter(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws IOException, ServletException {
        filterChain.doFilter((ServletRequest)(this.isCopyOrMoveMethod(httpServletRequest) ? this.rewriteDestinationHeader(httpServletRequest) : httpServletRequest), (ServletResponse)httpServletResponse);
    }

    private static class HttpServletRequestWrapperWithModifiedDestinationHeader
    extends HttpServletRequestWrapper {
        private final String destinationHeaderValue;

        public HttpServletRequestWrapperWithModifiedDestinationHeader(HttpServletRequest httpServletRequest, String destinationHeaderValue) {
            super(httpServletRequest);
            this.destinationHeaderValue = destinationHeaderValue;
        }

        public String getHeader(String name) {
            if (StringUtils.equals((String)"Destination", (String)name)) {
                return this.destinationHeaderValue;
            }
            return super.getHeader(name);
        }
    }
}

