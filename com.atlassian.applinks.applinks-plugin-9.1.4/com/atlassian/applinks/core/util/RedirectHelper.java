/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.net.Response
 *  javax.ws.rs.core.UriBuilder
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.applinks.core.util;

import com.atlassian.sal.api.net.Response;
import java.net.URI;
import javax.ws.rs.core.UriBuilder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedirectHelper {
    private static final Logger log = LoggerFactory.getLogger(RedirectHelper.class);
    private static final int MAX_REDIRECTS = 3;
    private int redirects = 0;
    private String url;

    public RedirectHelper(String url) {
        this.url = url;
    }

    public String getNextRedirectLocation(Response response) {
        String location = response.getHeader("location");
        if (UriBuilder.fromUri((String)location).build(new Object[0]).isAbsolute()) {
            this.url = location;
        } else {
            URI uri = UriBuilder.fromUri((String)this.url).build(new Object[0]);
            StringBuilder builder = new StringBuilder(uri.getScheme()).append("://").append(uri.getHost());
            if (this.isCustomPort(uri.getScheme(), uri.getPort())) {
                builder.append(":").append(uri.getPort());
            }
            this.url = builder.append(StringUtils.prependIfMissing((String)location, (CharSequence)"/", (CharSequence[])new CharSequence[0])).toString();
        }
        ++this.redirects;
        return this.url;
    }

    public boolean responseShouldRedirect(Response response) {
        return this.isRedirectStatusCode(response) && this.hasLocation(response) && this.notExceededMaximumRedirects();
    }

    private boolean hasLocation(Response response) {
        String location = response.getHeader("location");
        if (StringUtils.isBlank((CharSequence)location)) {
            log.warn("HTTP response returned redirect code {} but did not provide a location header", (Object)response.getStatusCode());
        }
        return StringUtils.isNotBlank((CharSequence)location);
    }

    private boolean isCustomPort(String scheme, int port) {
        return port != -1 && (port != 80 || !"http".equalsIgnoreCase(scheme)) && (port != 443 || !"https".equalsIgnoreCase(scheme));
    }

    private boolean isRedirectStatusCode(Response response) {
        return response.getStatusCode() >= 300 && response.getStatusCode() < 400;
    }

    private boolean notExceededMaximumRedirects() {
        if (this.redirects >= 3) {
            log.warn("Maximum of {} redirects reached", (Object)3);
            return false;
        }
        return true;
    }
}

