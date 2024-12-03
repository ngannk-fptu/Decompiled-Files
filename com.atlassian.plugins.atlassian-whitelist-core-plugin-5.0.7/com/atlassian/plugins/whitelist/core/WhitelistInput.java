/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  javax.annotation.Nullable
 */
package com.atlassian.plugins.whitelist.core;

import com.atlassian.sal.api.user.UserKey;
import java.net.URI;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;

class WhitelistInput {
    private final URI uri;
    private final UserKey userKey;
    private final boolean skipAuthCheck;

    public WhitelistInput(URI uri, @Nullable UserKey userKey, boolean skipAuthCheck) {
        this.uri = Objects.requireNonNull(uri);
        this.userKey = userKey;
        this.skipAuthCheck = skipAuthCheck;
    }

    public URI getUri() {
        return this.uri;
    }

    public Optional<UserKey> getUserKey() {
        return Optional.ofNullable(this.userKey);
    }

    public boolean shouldSkipAuthCheck() {
        return this.skipAuthCheck;
    }
}

