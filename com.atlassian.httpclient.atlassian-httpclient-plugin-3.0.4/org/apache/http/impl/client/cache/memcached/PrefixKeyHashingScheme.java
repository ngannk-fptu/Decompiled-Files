/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.client.cache.memcached;

import org.apache.http.impl.client.cache.memcached.KeyHashingScheme;

public class PrefixKeyHashingScheme
implements KeyHashingScheme {
    private final String prefix;
    private final KeyHashingScheme backingScheme;

    public PrefixKeyHashingScheme(String prefix, KeyHashingScheme backingScheme) {
        this.prefix = prefix;
        this.backingScheme = backingScheme;
    }

    @Override
    public String hash(String storageKey) {
        return this.prefix + this.backingScheme.hash(storageKey);
    }
}

