/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.jwt.core.http;

import com.atlassian.jwt.CanonicalHttpRequest;
import com.atlassian.jwt.core.http.HttpRequestWrapper;
import com.atlassian.jwt.core.http.JwtRequestExtractor;
import org.apache.commons.lang3.StringUtils;

public abstract class AbstractJwtRequestExtractor<REQ>
implements JwtRequestExtractor<REQ> {
    @Override
    public String extractJwt(REQ request) {
        return new JwtDefaultRequestHelper(this.wrapRequest(request)).extractJwt();
    }

    @Override
    public CanonicalHttpRequest getCanonicalHttpRequest(REQ request) {
        return this.wrapRequest(request).getCanonicalHttpRequest();
    }

    protected abstract HttpRequestWrapper wrapRequest(REQ var1);

    private static class JwtDefaultRequestHelper {
        private final HttpRequestWrapper requestWrapper;

        public JwtDefaultRequestHelper(HttpRequestWrapper requestWrapper) {
            this.requestWrapper = requestWrapper;
        }

        public String extractJwt() {
            String jwt = this.getJwtParameter();
            if (jwt == null) {
                jwt = this.getJwtHeaderValue();
            }
            return jwt;
        }

        private String getJwtParameter() {
            String jwtParam = this.requestWrapper.getParameter("jwt");
            return StringUtils.isEmpty((CharSequence)jwtParam) ? null : jwtParam;
        }

        private String getJwtHeaderValue() {
            Iterable<String> headers = this.requestWrapper.getHeaderValues("Authorization");
            for (String header : headers) {
                String authzHeader = header.trim();
                String first4Chars = authzHeader.substring(0, Math.min(4, authzHeader.length()));
                if (!"JWT ".equalsIgnoreCase(first4Chars)) continue;
                return authzHeader.substring(4);
            }
            return null;
        }
    }
}

