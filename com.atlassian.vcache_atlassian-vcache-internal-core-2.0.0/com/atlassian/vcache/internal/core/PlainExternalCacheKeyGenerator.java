/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.vcache.internal.core;

import com.atlassian.vcache.internal.core.ExternalCacheKeyGenerator;

public class PlainExternalCacheKeyGenerator
extends ExternalCacheKeyGenerator {
    public PlainExternalCacheKeyGenerator(String productIdentifier) {
        super(productIdentifier);
    }

    @Override
    protected String encode(String plain) {
        return plain;
    }
}

