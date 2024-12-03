/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.hash.Hashing
 */
package com.atlassian.vcache.internal.core;

import com.atlassian.vcache.internal.core.ExternalCacheKeyGenerator;
import com.google.common.hash.Hashing;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Sha1ExternalCacheKeyGenerator
extends ExternalCacheKeyGenerator {
    private static final Charset DIGEST_CHARSET = StandardCharsets.UTF_8;

    public Sha1ExternalCacheKeyGenerator(String productIdentifier) {
        super(productIdentifier);
    }

    @Override
    protected String encode(String plain) {
        byte[] digest = Hashing.sha1().hashString((CharSequence)plain, DIGEST_CHARSET).asBytes();
        return Base64.getEncoder().encodeToString(digest);
    }
}

