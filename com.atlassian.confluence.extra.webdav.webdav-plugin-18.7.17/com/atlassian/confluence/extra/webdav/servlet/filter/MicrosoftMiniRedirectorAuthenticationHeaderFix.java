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
 *  org.apache.commons.codec.binary.Base64
 *  org.apache.commons.lang.StringUtils
 */
package com.atlassian.confluence.extra.webdav.servlet.filter;

import com.atlassian.confluence.extra.webdav.servlet.filter.AbstractHttpFilter;
import com.atlassian.confluence.extra.webdav.util.UserAgentUtil;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

public class MicrosoftMiniRedirectorAuthenticationHeaderFix
extends AbstractHttpFilter {
    private HttpServletRequest rewriteDestinationHeader(HttpServletRequest httpServletRequest) throws IOException, ServletException {
        String authorizationHeader = httpServletRequest.getHeader("Authorization");
        if (StringUtils.isNotBlank((String)authorizationHeader)) {
            int indexOfBackSlash;
            Object[] authTokens = StringUtils.split((String)StringUtils.trim((String)authorizationHeader), (char)' ');
            if (authTokens.length != 2) {
                throw new ServletException("Malformed Authorization header: " + StringUtils.join((Object[])authTokens));
            }
            String credentials = new String(Base64.decodeBase64((byte[])((String)authTokens[1]).getBytes("UTF-8")), "UTF-8");
            Object[] credentialTokens = StringUtils.split((String)credentials, (char)':');
            if (credentialTokens.length == 2 && (indexOfBackSlash = credentialTokens[0].indexOf(92)) >= 0 && indexOfBackSlash < credentialTokens[0].length() - 1) {
                credentialTokens[0] = credentialTokens[0].substring(indexOfBackSlash + 1);
                credentials = new String(Base64.encodeBase64((byte[])StringUtils.join((Object[])credentialTokens, (char)':').getBytes("UTF-8")), "UTF-8");
                return new HttpServletRequestWrapperWithModifiedAuthorizationHeader(httpServletRequest, StringUtils.join((Object[])new String[]{authTokens[0], credentials}, (char)' '));
            }
        }
        return httpServletRequest;
    }

    @Override
    protected boolean handles(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        return UserAgentUtil.isMicrosoftMiniRedirector(request.getHeader("User-Agent"));
    }

    @Override
    public void doFilter(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws IOException, ServletException {
        filterChain.doFilter((ServletRequest)this.rewriteDestinationHeader(httpServletRequest), (ServletResponse)httpServletResponse);
    }

    private static class HttpServletRequestWrapperWithModifiedAuthorizationHeader
    extends HttpServletRequestWrapper {
        private final String authorizationHeaderValue;

        public HttpServletRequestWrapperWithModifiedAuthorizationHeader(HttpServletRequest httpServletRequest, String authorizationHeaderValue) {
            super(httpServletRequest);
            this.authorizationHeaderValue = authorizationHeaderValue;
        }

        public String getHeader(String name) {
            if (StringUtils.equals((String)"Authorization", (String)name)) {
                return this.authorizationHeaderValue;
            }
            return super.getHeader(name);
        }
    }
}

