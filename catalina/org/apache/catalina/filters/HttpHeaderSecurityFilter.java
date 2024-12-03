/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.FilterChain
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.catalina.filters;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.filters.FilterBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class HttpHeaderSecurityFilter
extends FilterBase {
    private final Log log = LogFactory.getLog(HttpHeaderSecurityFilter.class);
    private static final String HSTS_HEADER_NAME = "Strict-Transport-Security";
    private boolean hstsEnabled = true;
    private int hstsMaxAgeSeconds = 0;
    private boolean hstsIncludeSubDomains = false;
    private boolean hstsPreload = false;
    private String hstsHeaderValue;
    private static final String ANTI_CLICK_JACKING_HEADER_NAME = "X-Frame-Options";
    private boolean antiClickJackingEnabled = true;
    private XFrameOption antiClickJackingOption = XFrameOption.DENY;
    private URI antiClickJackingUri;
    private String antiClickJackingHeaderValue;
    private static final String BLOCK_CONTENT_TYPE_SNIFFING_HEADER_NAME = "X-Content-Type-Options";
    private static final String BLOCK_CONTENT_TYPE_SNIFFING_HEADER_VALUE = "nosniff";
    private boolean blockContentTypeSniffingEnabled = true;
    @Deprecated
    private static final String XSS_PROTECTION_HEADER_NAME = "X-XSS-Protection";
    @Deprecated
    private static final String XSS_PROTECTION_HEADER_VALUE = "1; mode=block";
    @Deprecated
    private boolean xssProtectionEnabled = false;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
        StringBuilder hstsValue = new StringBuilder("max-age=");
        hstsValue.append(this.hstsMaxAgeSeconds);
        if (this.hstsIncludeSubDomains) {
            hstsValue.append(";includeSubDomains");
        }
        if (this.hstsPreload) {
            hstsValue.append(";preload");
        }
        this.hstsHeaderValue = hstsValue.toString();
        StringBuilder cjValue = new StringBuilder(this.antiClickJackingOption.headerValue);
        if (this.antiClickJackingOption == XFrameOption.ALLOW_FROM) {
            cjValue.append(' ');
            cjValue.append(this.antiClickJackingUri);
        }
        this.antiClickJackingHeaderValue = cjValue.toString();
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (response instanceof HttpServletResponse) {
            HttpServletResponse httpResponse = (HttpServletResponse)response;
            if (response.isCommitted()) {
                throw new ServletException(sm.getString("httpHeaderSecurityFilter.committed"));
            }
            if (this.hstsEnabled && request.isSecure()) {
                httpResponse.setHeader(HSTS_HEADER_NAME, this.hstsHeaderValue);
            }
            if (this.antiClickJackingEnabled) {
                httpResponse.setHeader(ANTI_CLICK_JACKING_HEADER_NAME, this.antiClickJackingHeaderValue);
            }
            if (this.blockContentTypeSniffingEnabled) {
                httpResponse.setHeader(BLOCK_CONTENT_TYPE_SNIFFING_HEADER_NAME, BLOCK_CONTENT_TYPE_SNIFFING_HEADER_VALUE);
            }
            if (this.xssProtectionEnabled) {
                httpResponse.setHeader(XSS_PROTECTION_HEADER_NAME, XSS_PROTECTION_HEADER_VALUE);
            }
        }
        chain.doFilter(request, response);
    }

    @Override
    protected Log getLogger() {
        return this.log;
    }

    @Override
    protected boolean isConfigProblemFatal() {
        return true;
    }

    public boolean isHstsEnabled() {
        return this.hstsEnabled;
    }

    public void setHstsEnabled(boolean hstsEnabled) {
        this.hstsEnabled = hstsEnabled;
    }

    public int getHstsMaxAgeSeconds() {
        return this.hstsMaxAgeSeconds;
    }

    public void setHstsMaxAgeSeconds(int hstsMaxAgeSeconds) {
        this.hstsMaxAgeSeconds = hstsMaxAgeSeconds < 0 ? 0 : hstsMaxAgeSeconds;
    }

    public boolean isHstsIncludeSubDomains() {
        return this.hstsIncludeSubDomains;
    }

    public void setHstsIncludeSubDomains(boolean hstsIncludeSubDomains) {
        this.hstsIncludeSubDomains = hstsIncludeSubDomains;
    }

    public boolean isHstsPreload() {
        return this.hstsPreload;
    }

    public void setHstsPreload(boolean hstsPreload) {
        this.hstsPreload = hstsPreload;
    }

    public boolean isAntiClickJackingEnabled() {
        return this.antiClickJackingEnabled;
    }

    public void setAntiClickJackingEnabled(boolean antiClickJackingEnabled) {
        this.antiClickJackingEnabled = antiClickJackingEnabled;
    }

    public String getAntiClickJackingOption() {
        return this.antiClickJackingOption.toString();
    }

    public void setAntiClickJackingOption(String antiClickJackingOption) {
        for (XFrameOption option : XFrameOption.values()) {
            if (!option.getHeaderValue().equalsIgnoreCase(antiClickJackingOption)) continue;
            this.antiClickJackingOption = option;
            return;
        }
        throw new IllegalArgumentException(sm.getString("httpHeaderSecurityFilter.clickjack.invalid", new Object[]{antiClickJackingOption}));
    }

    public String getAntiClickJackingUri() {
        return this.antiClickJackingUri.toString();
    }

    public boolean isBlockContentTypeSniffingEnabled() {
        return this.blockContentTypeSniffingEnabled;
    }

    public void setBlockContentTypeSniffingEnabled(boolean blockContentTypeSniffingEnabled) {
        this.blockContentTypeSniffingEnabled = blockContentTypeSniffingEnabled;
    }

    public void setAntiClickJackingUri(String antiClickJackingUri) {
        URI uri;
        try {
            uri = new URI(antiClickJackingUri);
        }
        catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
        this.antiClickJackingUri = uri;
    }

    @Deprecated
    public boolean isXssProtectionEnabled() {
        return this.xssProtectionEnabled;
    }

    @Deprecated
    public void setXssProtectionEnabled(boolean xssProtectionEnabled) {
        this.xssProtectionEnabled = xssProtectionEnabled;
    }

    private static enum XFrameOption {
        DENY("DENY"),
        SAME_ORIGIN("SAMEORIGIN"),
        ALLOW_FROM("ALLOW-FROM");

        private final String headerValue;

        private XFrameOption(String headerValue) {
            this.headerValue = headerValue;
        }

        public String getHeaderValue() {
            return this.headerValue;
        }
    }
}

