/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.interceptor.csp;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.interceptor.csp.CspSettings;

public class DefaultCspSettings
implements CspSettings {
    private static final Logger LOG = LogManager.getLogger(DefaultCspSettings.class);
    private final SecureRandom sRand = new SecureRandom();
    private String reportUri;
    private String cspHeader = "Content-Security-Policy-Report-Only";

    @Override
    public void addCspHeaders(HttpServletResponse response) {
        throw new UnsupportedOperationException("Unsupported implementation, use #addCspHeaders(HttpServletRequest request, HttpServletResponse response)");
    }

    @Override
    public void addCspHeaders(HttpServletRequest request, HttpServletResponse response) {
        if (this.isSessionActive(request)) {
            LOG.trace("Session is active, applying CSP settings");
            this.associateNonceWithSession(request);
            response.setHeader(this.cspHeader, this.cratePolicyFormat(request));
        } else {
            LOG.trace("Session is not active, ignoring CSP settings");
        }
    }

    private boolean isSessionActive(HttpServletRequest request) {
        return request.getSession(false) != null;
    }

    private void associateNonceWithSession(HttpServletRequest request) {
        String nonceValue = Base64.getUrlEncoder().encodeToString(this.getRandomBytes());
        request.getSession().setAttribute("nonce", (Object)nonceValue);
    }

    private String cratePolicyFormat(HttpServletRequest request) {
        StringBuilder policyFormatBuilder = new StringBuilder().append("object-src").append(String.format(" '%s'; ", "none")).append("script-src").append(" 'nonce-%s' ").append(String.format("'%s' ", "strict-dynamic")).append(String.format("%s %s; ", "http:", "https:")).append("base-uri").append(String.format(" '%s'; ", "none"));
        if (this.reportUri != null) {
            policyFormatBuilder.append("report-uri").append(String.format(" %s", this.reportUri));
        }
        return String.format(policyFormatBuilder.toString(), this.getNonceString(request));
    }

    private String getNonceString(HttpServletRequest request) {
        Object nonce = request.getSession().getAttribute("nonce");
        return Objects.toString(nonce);
    }

    private byte[] getRandomBytes() {
        byte[] ret = new byte[18];
        this.sRand.nextBytes(ret);
        return ret;
    }

    @Override
    public void setEnforcingMode(boolean enforcingMode) {
        if (enforcingMode) {
            this.cspHeader = "Content-Security-Policy";
        }
    }

    @Override
    public void setReportUri(String reportUri) {
        this.reportUri = reportUri;
    }

    public String toString() {
        return "DefaultCspSettings{reportUri='" + this.reportUri + '\'' + ", cspHeader='" + this.cspHeader + '\'' + '}';
    }
}

