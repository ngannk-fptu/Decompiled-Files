/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.oauth.serviceprovider.internal;

import com.atlassian.oauth.serviceprovider.internal.NonceChecker;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InMemoryCacheNonceChecker
implements NonceChecker {
    private static final Duration ONE_MINUTE = Duration.ofMinutes(1L);
    private final Map<String, Long> cache = new ConcurrentHashMap<String, Long>();

    @Override
    public boolean isNonceUnique(String consumerKey, String nonce) {
        this.removeExpiredEntries();
        return !this.cache.containsKey(NonceChecker.generateCacheKeyFrom(consumerKey, nonce));
    }

    @Override
    public void addNonce(String consumerKey, String nonce) {
        this.removeExpiredEntries();
        if (this.cache.putIfAbsent(NonceChecker.generateCacheKeyFrom(consumerKey, nonce), System.currentTimeMillis()) != null) {
            throw new IllegalStateException("{" + consumerKey + ", " + nonce + "} already present in cache.");
        }
    }

    private void removeExpiredEntries() {
        this.cache.entrySet().stream().filter(entry -> this.isExpired((Long)entry.getValue())).map(Map.Entry::getKey).collect(Collectors.toList()).forEach(this.cache::remove);
    }

    private boolean isExpired(Long timestamp) {
        return Duration.of(System.currentTimeMillis() - timestamp, ChronoUnit.MILLIS).compareTo(ONE_MINUTE) > 0;
    }
}

