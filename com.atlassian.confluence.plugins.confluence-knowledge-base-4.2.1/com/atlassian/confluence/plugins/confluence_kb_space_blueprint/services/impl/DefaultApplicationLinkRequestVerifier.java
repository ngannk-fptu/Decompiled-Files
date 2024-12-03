/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.auth.OAuthRequestVerifier
 *  com.atlassian.sal.api.auth.OAuthRequestVerifierFactory
 *  javax.servlet.ServletRequest
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.confluence_kb_space_blueprint.services.impl;

import com.atlassian.confluence.plugins.confluence_kb_space_blueprint.services.ApplicationLinkRequestVerifier;
import com.atlassian.sal.api.auth.OAuthRequestVerifier;
import com.atlassian.sal.api.auth.OAuthRequestVerifierFactory;
import java.util.Objects;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultApplicationLinkRequestVerifier
implements ApplicationLinkRequestVerifier {
    private static final String SERAPH_TRUSTED_APP_STATUS_HEADER = "X-Seraph-Trusted-App-Status";
    private static final String SERAPH_TRUSTED_APP_ERROR_HEADER = "X-Seraph-Trusted-App-Error";
    private static final String SERAPH_OS_AUTHSTATUS_ATTRIBUTE_KEY = "os_authstatus";
    private static final String LOGIN_SUCCESS = "success";
    private static final Logger log = LoggerFactory.getLogger(DefaultApplicationLinkRequestVerifier.class);
    private final OAuthRequestVerifierFactory oAuthRequestVerifierFactory;

    public DefaultApplicationLinkRequestVerifier(OAuthRequestVerifierFactory oAuthRequestVerifierFactory) {
        this.oAuthRequestVerifierFactory = oAuthRequestVerifierFactory;
    }

    @Override
    public boolean isApplicationLinkRequest(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        OAuthRequestVerifier requestVerifier = this.oAuthRequestVerifierFactory.getInstance((ServletRequest)servletRequest);
        if (requestVerifier.isVerified()) {
            log.debug("Verified OAuth request");
            return true;
        }
        if (this.isAuthenticatedTrustedAppsCall(servletRequest, servletResponse)) {
            log.debug("Verified Trusted Apps request");
            return true;
        }
        return false;
    }

    private boolean isAuthenticatedTrustedAppsCall(HttpServletRequest request, HttpServletResponse response) {
        return Objects.equals(request.getAttribute(SERAPH_OS_AUTHSTATUS_ATTRIBUTE_KEY), LOGIN_SUCCESS) && response.containsHeader(SERAPH_TRUSTED_APP_STATUS_HEADER) && !response.containsHeader(SERAPH_TRUSTED_APP_ERROR_HEADER);
    }
}

