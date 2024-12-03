/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  javax.validation.constraints.NotNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.pats.web.filter;

import com.google.common.collect.ImmutableMap;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LastAccessedTimeBatcher {
    private TokenBatch batch = new TokenBatch();

    public void onAuthSuccessEvent(@NotNull Long tokenId, @NotNull Instant utcAuthenticatedTime) {
        this.batch.logTokenAuthenticationTime(tokenId, utcAuthenticatedTime);
    }

    public Map<Long, Instant> collect() {
        TokenBatch batchRun = this.swap(this.batch);
        return batchRun.completed();
    }

    private TokenBatch swap(TokenBatch original) {
        this.batch = new TokenBatch();
        return original;
    }

    private static class TokenBatch {
        private static final Logger logger = LoggerFactory.getLogger(TokenBatch.class);
        private final Map<Long, Instant> authTokenMap = new ConcurrentHashMap<Long, Instant>();

        public void logTokenAuthenticationTime(@NotNull Long tokenId, @NotNull Instant utcAuthenticatedTime) {
            logger.trace("Storing auth time: [{}] for tokenId: [{}]", (Object)utcAuthenticatedTime, (Object)tokenId);
            this.authTokenMap.merge(tokenId, utcAuthenticatedTime, (oldValue, newValue) -> newValue.isAfter((Instant)oldValue) ? newValue : oldValue);
        }

        public Map<Long, Instant> completed() {
            return ImmutableMap.copyOf(this.authTokenMap);
        }
    }
}

