/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.support;

import java.time.Duration;
import java.time.Instant;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.vault.support.VaultToken;

public class WrappedMetadata {
    private final VaultToken token;
    private final Instant creationTime;
    private final String path;
    private final Duration ttl;

    public WrappedMetadata(VaultToken token, Duration ttl, Instant creationTime, @Nullable String path) {
        Assert.notNull((Object)token, "VaultToken must not be null");
        Assert.notNull((Object)ttl, "TTL duration must not be null");
        Assert.notNull((Object)creationTime, "Creation time must not be null");
        this.token = token;
        this.ttl = ttl;
        this.creationTime = creationTime;
        this.path = path;
    }

    public VaultToken getToken() {
        return this.token;
    }

    public Duration getTtl() {
        return this.ttl;
    }

    public Instant getCreationTime() {
        return this.creationTime;
    }

    @Nullable
    public String getPath() {
        return this.path;
    }
}

