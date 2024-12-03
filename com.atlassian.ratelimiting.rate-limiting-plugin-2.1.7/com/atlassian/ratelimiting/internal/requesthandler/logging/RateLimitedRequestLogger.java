/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  javax.servlet.http.HttpServletRequest
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.ratelimiting.internal.requesthandler.logging;

import com.atlassian.sal.api.user.UserKey;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RateLimitedRequestLogger {
    private static final Logger logger = LoggerFactory.getLogger(RateLimitedRequestLogger.class);

    public void logRateLimitedRequest(UserKey userKey, HttpServletRequest request) {
        logger.debug("User [{}] has been rate limited for URL [{}]", (Object)userKey, (Object)request.getRequestURL());
    }

    public void logRateLimitedRequestPreAuth(Optional<UserKey> userKey, HttpServletRequest request) {
        if (userKey.isPresent()) {
            logger.debug("User [{}] has been rate limited for URL [{}], pre-auth", (Object)userKey.get(), (Object)request.getRequestURL());
        } else {
            logger.error("User key not present in pre-auth request");
        }
    }
}

