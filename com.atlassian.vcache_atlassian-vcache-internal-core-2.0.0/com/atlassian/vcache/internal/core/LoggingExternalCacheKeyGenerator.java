/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.vcache.internal.core;

import com.atlassian.vcache.internal.core.ExternalCacheKeyGenerator;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingExternalCacheKeyGenerator
extends ExternalCacheKeyGenerator {
    private static final Logger log = LoggerFactory.getLogger(LoggingExternalCacheKeyGenerator.class);
    private final ExternalCacheKeyGenerator delegate;

    public LoggingExternalCacheKeyGenerator(String productIdentifier, ExternalCacheKeyGenerator delegate) {
        super(productIdentifier);
        this.delegate = Objects.requireNonNull(delegate);
    }

    @Override
    protected String encode(String plain) {
        String result = this.delegate.encode(plain);
        log.debug("Generated key {} from {}", (Object)result, (Object)plain);
        return result;
    }
}

