/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.vcache.internal.NameValidator
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.vcache.internal.core;

import com.atlassian.vcache.internal.NameValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ExternalCacheKeyGenerator {
    private static final Logger log = LoggerFactory.getLogger(ExternalCacheKeyGenerator.class);
    private static final String SEPARATOR = "::";
    private static final String CACHE_VERSION_KEY = "cache-version";
    private final String productIdentifier;

    public ExternalCacheKeyGenerator(String productIdentifier) {
        this.productIdentifier = NameValidator.requireValidProductIdentifier((String)productIdentifier);
    }

    public String entryKey(String partition, String cacheName, long cacheVersion, String entryKey) {
        String plain = this.productIdentifier + SEPARATOR + partition + SEPARATOR + cacheName + SEPARATOR + cacheVersion + SEPARATOR + entryKey;
        String encoded = this.encode(plain);
        log.trace("Encoded cacheKey {} to {}", (Object)plain, (Object)encoded);
        return encoded;
    }

    public String cacheVersionKey(String partition, String cacheName) {
        String plain = this.productIdentifier + SEPARATOR + partition + SEPARATOR + cacheName + SEPARATOR + CACHE_VERSION_KEY;
        String encoded = this.encode(plain);
        log.trace("Encoded cacheVersionKey {} to {}", (Object)plain, (Object)encoded);
        return encoded;
    }

    protected abstract String encode(String var1);
}

