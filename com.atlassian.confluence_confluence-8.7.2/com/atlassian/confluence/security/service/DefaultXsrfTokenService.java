/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.xwork.XsrfTokenGenerator
 *  io.atlassian.fugue.Pair
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.confluence.security.service;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.security.service.XsrfTokenService;
import com.atlassian.confluence.util.i18n.Message;
import com.atlassian.xwork.XsrfTokenGenerator;
import io.atlassian.fugue.Pair;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;

public class DefaultXsrfTokenService
implements XsrfTokenService {
    @VisibleForTesting
    static final String REQUEST_PARAM_NAME = "atl_token";
    @VisibleForTesting
    static final String VALIDATION_FAILED_ERROR_KEY = "atlassian.xwork.xsrf.badtoken";
    @VisibleForTesting
    static final String SECURITY_TOKEN_REQUIRED_ERROR_KEY = "atlassian.xwork.xsrf.notoken";
    @VisibleForTesting
    static final String OVERRIDE_HEADER_NAME = "X-Atlassian-Token";
    @VisibleForTesting
    static final String OVERRIDE_HEADER_VALUE = "no-check";
    private final XsrfTokenGenerator tokenGenerator;

    public DefaultXsrfTokenService(XsrfTokenGenerator tokenGenerator) {
        this.tokenGenerator = tokenGenerator;
    }

    @Override
    public Pair<String, String> generateToken(HttpServletRequest request) {
        return Pair.pair((Object)REQUEST_PARAM_NAME, (Object)this.tokenGenerator.generateToken(request));
    }

    @Override
    public Optional<Message> validateToken(HttpServletRequest request) {
        if (OVERRIDE_HEADER_VALUE.equals(request.getHeader(OVERRIDE_HEADER_NAME))) {
            return Optional.empty();
        }
        String token = request.getParameter(REQUEST_PARAM_NAME);
        if (token == null) {
            return Optional.of(Message.getInstance(SECURITY_TOKEN_REQUIRED_ERROR_KEY));
        }
        if (!this.tokenGenerator.validateToken(request, token)) {
            return Optional.of(Message.getInstance(VALIDATION_FAILED_ERROR_KEY));
        }
        return Optional.empty();
    }
}

