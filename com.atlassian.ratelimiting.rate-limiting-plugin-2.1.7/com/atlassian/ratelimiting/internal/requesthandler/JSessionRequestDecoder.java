/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  javax.servlet.http.HttpServletRequest
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.ratelimiting.internal.requesthandler;

import com.atlassian.ratelimiting.requesthandler.PreAuthRequestSingleMethodDecoder;
import com.atlassian.ratelimiting.user.keyprovider.UserKeyProvider;
import com.atlassian.sal.api.user.UserKey;
import java.security.Principal;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JSessionRequestDecoder
implements PreAuthRequestSingleMethodDecoder {
    private static final Logger logger = LoggerFactory.getLogger(JSessionRequestDecoder.class);
    private final UserKeyProvider userKeyProvider;

    public JSessionRequestDecoder(UserKeyProvider userKeyProvider) {
        this.userKeyProvider = userKeyProvider;
    }

    @Override
    public Optional<UserKey> getUserKey(HttpServletRequest httpServletRequest) {
        Optional<UserKey> basicAuthUser = this.getJSessionUserKey(httpServletRequest);
        basicAuthUser.ifPresent(userKey -> logger.trace("Pre-auth user detected from basic auth: {}", userKey));
        return basicAuthUser;
    }

    private Optional<UserKey> getJSessionUserKey(HttpServletRequest httpServletRequest) {
        return Optional.ofNullable(httpServletRequest.getSession(false)).map(session -> session.getAttribute("seraph_defaultauthenticator_user")).filter(p -> Principal.class.isAssignableFrom(p.getClass())).map(Principal.class::cast).map(Principal::getName).flatMap(this.userKeyProvider);
    }
}

