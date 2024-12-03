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

import com.atlassian.ratelimiting.requesthandler.PreAuthRequestDecoder;
import com.atlassian.ratelimiting.requesthandler.PreAuthRequestSingleMethodDecoder;
import com.atlassian.sal.api.user.UserKey;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultPreAuthRequestDecoder
implements PreAuthRequestDecoder {
    private static final Logger logger = LoggerFactory.getLogger(DefaultPreAuthRequestDecoder.class);
    private final List<PreAuthRequestSingleMethodDecoder> decoders;

    public DefaultPreAuthRequestDecoder(List<PreAuthRequestSingleMethodDecoder> decoders) {
        this.decoders = decoders;
    }

    @Override
    public Optional<UserKey> getUserKey(HttpServletRequest httpServletRequest) {
        for (PreAuthRequestSingleMethodDecoder preAuthRequestDecoder : this.decoders) {
            Optional<UserKey> userKey = preAuthRequestDecoder.getUserKey(httpServletRequest);
            if (!userKey.isPresent()) continue;
            return userKey;
        }
        logger.trace("Username could not be decoded before authentication");
        return Optional.empty();
    }
}

