/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Effect
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugins.whitelist.InboundWhitelist
 *  com.google.common.base.Predicate
 *  javax.servlet.Filter
 *  javax.servlet.FilterChain
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.plugins.cors;

import com.atlassian.fugue.Effect;
import com.atlassian.fugue.Option;
import com.atlassian.plugins.whitelist.InboundWhitelist;
import com.google.common.base.Predicate;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;

@Deprecated
public class CorsFilter
implements Filter {
    private static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    private static final String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";
    private static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
    private static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
    private static final String HTTP_METHOD_OPTIONS = "OPTIONS";
    private static final String ORIGIN = "Origin";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String TRUE = String.valueOf(true);
    private final InboundWhitelist inboundWhitelist;

    public CorsFilter(InboundWhitelist inboundWhitelist) {
        this.inboundWhitelist = inboundWhitelist;
    }

    public void init(FilterConfig filterConfig) {
    }

    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)req;
        HttpServletResponse response = (HttpServletResponse)res;
        this.retrieveOrigin(request).filter(this.isAllowed()).foreach(this.addCorsResponseHeaders(response, request));
        if (!HTTP_METHOD_OPTIONS.equals(request.getMethod())) {
            chain.doFilter(req, res);
        }
    }

    private Option<URI> retrieveOrigin(HttpServletRequest request) {
        String origin = request.getHeader(ORIGIN);
        if (StringUtils.isBlank((CharSequence)origin)) {
            return Option.none();
        }
        try {
            return Option.some((Object)new URI(origin));
        }
        catch (URISyntaxException e) {
            return Option.none();
        }
    }

    private Predicate<URI> isAllowed() {
        return new Predicate<URI>(){

            public boolean apply(URI origin) {
                return CorsFilter.this.inboundWhitelist.isAllowed(origin);
            }
        };
    }

    private Effect<URI> addCorsResponseHeaders(final HttpServletResponse response, final HttpServletRequest request) {
        return new Effect<URI>(){

            public void apply(URI input) {
                response.addHeader(CorsFilter.ACCESS_CONTROL_ALLOW_ORIGIN, input.toString());
                response.addHeader(CorsFilter.ACCESS_CONTROL_ALLOW_CREDENTIALS, TRUE);
                response.addHeader(CorsFilter.ACCESS_CONTROL_ALLOW_HEADERS, CorsFilter.CONTENT_TYPE);
                if (CorsFilter.HTTP_METHOD_OPTIONS.equals(request.getMethod())) {
                    response.addHeader(CorsFilter.ACCESS_CONTROL_ALLOW_METHODS, request.getMethod());
                }
            }
        };
    }
}

