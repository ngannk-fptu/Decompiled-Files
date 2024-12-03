/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.security.auth.trustedapps;

import com.atlassian.security.auth.trustedapps.IPMatcher;
import com.atlassian.security.auth.trustedapps.InvalidIPAddressException;
import com.atlassian.security.auth.trustedapps.InvalidRemoteAddressException;
import com.atlassian.security.auth.trustedapps.InvalidRequestException;
import com.atlassian.security.auth.trustedapps.InvalidRequestUrlException;
import com.atlassian.security.auth.trustedapps.InvalidXForwardedForAddressException;
import com.atlassian.security.auth.trustedapps.Null;
import com.atlassian.security.auth.trustedapps.RequestValidator;
import com.atlassian.security.auth.trustedapps.URLMatcher;
import java.util.StringTokenizer;
import javax.servlet.http.HttpServletRequest;

public class DefaultRequestValidator
implements RequestValidator {
    private final IPMatcher ipMatcher;
    private final URLMatcher urlMatcher;

    public DefaultRequestValidator(IPMatcher ipMatcher, URLMatcher urlMatcher) {
        Null.not("ipMatcher", ipMatcher);
        Null.not("urlMatcher", urlMatcher);
        this.ipMatcher = ipMatcher;
        this.urlMatcher = urlMatcher;
    }

    @Override
    public void validate(HttpServletRequest request) throws InvalidRequestException {
        this.validateRemoteRequestIP(request);
        this.validateXForwardedFor(request);
        this.validateRequestURL(request);
    }

    private void validateRemoteRequestIP(HttpServletRequest request) throws InvalidIPAddressException {
        String remoteAddr = request.getRemoteAddr();
        if (!this.ipMatcher.match(remoteAddr)) {
            throw new InvalidRemoteAddressException(remoteAddr);
        }
    }

    private void validateXForwardedFor(HttpServletRequest request) throws InvalidXForwardedForAddressException {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null) {
            StringTokenizer tokenizer = new StringTokenizer(forwardedFor, ",");
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                if (token.trim().length() <= 0 || this.ipMatcher.match(token.trim())) continue;
                throw new InvalidXForwardedForAddressException(token);
            }
        }
    }

    private void validateRequestURL(HttpServletRequest request) throws InvalidRequestUrlException {
        String pathInfo = this.getPathInfo(request);
        if (!this.urlMatcher.match(pathInfo)) {
            throw new InvalidRequestUrlException(pathInfo);
        }
    }

    private String getPathInfo(HttpServletRequest request) {
        String context = request.getContextPath();
        String uri = request.getRequestURI();
        if (context != null && context.length() > 0) {
            return uri.substring(context.length());
        }
        return uri;
    }
}

