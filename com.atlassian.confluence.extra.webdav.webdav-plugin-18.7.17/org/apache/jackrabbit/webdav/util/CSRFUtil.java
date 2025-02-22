/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CSRFUtil {
    public static final String DISABLED = "disabled";
    public static final Set<String> CONTENT_TYPES = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("application/x-www-form-urlencoded", "multipart/form-data", "text/plain")));
    private static final Logger log = LoggerFactory.getLogger(CSRFUtil.class);
    private final boolean disabled;
    private final Set<String> allowedReferrerHosts;

    public CSRFUtil(String config) {
        if (config == null || config.length() == 0) {
            this.disabled = false;
            this.allowedReferrerHosts = Collections.emptySet();
            log.debug("CSRF protection disabled");
        } else {
            if (DISABLED.equalsIgnoreCase(config.trim())) {
                this.disabled = true;
                this.allowedReferrerHosts = Collections.emptySet();
            } else {
                this.disabled = false;
                String[] allowed = config.split(",");
                this.allowedReferrerHosts = new HashSet<String>(allowed.length);
                for (String entry : allowed) {
                    this.allowedReferrerHosts.add(entry.trim());
                }
            }
            log.debug("CSRF protection enabled, allowed referrers: " + this.allowedReferrerHosts);
        }
    }

    public boolean isValidRequest(HttpServletRequest request) {
        if (this.disabled) {
            return true;
        }
        if (!"POST".equals(request.getMethod())) {
            return true;
        }
        Enumeration cts = request.getHeaders("Content-Type");
        String ct = null;
        if (cts != null && cts.hasMoreElements()) {
            String t = (String)cts.nextElement();
            int semicolon = t.indexOf(59);
            if (semicolon >= 0) {
                t = t.substring(0, semicolon);
            }
            ct = t.trim().toLowerCase(Locale.ENGLISH);
        }
        if (cts != null && cts.hasMoreElements()) {
            log.debug("request blocked because there were multiple content-type header fields");
            return false;
        }
        if (ct != null && !CONTENT_TYPES.contains(ct)) {
            return true;
        }
        String refHeader = request.getHeader("Referer");
        if (refHeader == null) {
            log.debug("POST with content type " + ct + " blocked due to missing referer header field");
            return false;
        }
        try {
            boolean ok;
            String host = new URI(refHeader).getHost();
            boolean bl = ok = host == null || host.equals(request.getServerName()) || this.allowedReferrerHosts.contains(host);
            if (!ok) {
                log.debug("POST with content type " + ct + " blocked due to referer header field being: " + refHeader);
            }
            return ok;
        }
        catch (URISyntaxException ex) {
            log.debug("POST with content type " + ct + " blocked due to malformed referer header field: " + refHeader);
            return false;
        }
    }
}

