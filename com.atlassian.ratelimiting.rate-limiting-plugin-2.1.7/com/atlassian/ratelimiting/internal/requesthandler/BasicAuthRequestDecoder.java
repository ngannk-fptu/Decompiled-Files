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
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasicAuthRequestDecoder
implements PreAuthRequestSingleMethodDecoder {
    private static final Logger logger = LoggerFactory.getLogger(BasicAuthRequestDecoder.class);
    private static final String BASIC_AUTHZ_TYPE_PREFIX = "Basic ";
    private final UserKeyProvider userKeyProvider;

    public BasicAuthRequestDecoder(UserKeyProvider userKeyProvider) {
        this.userKeyProvider = userKeyProvider;
    }

    @Override
    public Optional<UserKey> getUserKey(HttpServletRequest httpServletRequest) {
        Optional<UserKey> basicAuthUser = this.getBasicAuthUserKey(httpServletRequest);
        basicAuthUser.ifPresent(userKey -> logger.trace("Pre-auth user detected from basic auth: {}", userKey));
        return basicAuthUser;
    }

    private Optional<UserKey> getBasicAuthUserKey(HttpServletRequest httpServletRequest) {
        String authorizationHeader = httpServletRequest.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith(BASIC_AUTHZ_TYPE_PREFIX)) {
            String base64Token = authorizationHeader.substring(BASIC_AUTHZ_TYPE_PREFIX.length());
            byte[] bytes = Base64.getDecoder().decode(base64Token);
            String token = new String(bytes, StandardCharsets.ISO_8859_1);
            int delim = token.indexOf(":");
            if (delim != -1) {
                return Optional.of(token.substring(0, delim)).flatMap(this.userKeyProvider);
            }
        }
        return Optional.empty();
    }
}

