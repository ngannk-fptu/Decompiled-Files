/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.authentication;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.vault.authentication.UnwrappingEndpoints;
import org.springframework.vault.support.VaultToken;

public class CubbyholeAuthenticationOptions {
    private final VaultToken initialToken;
    private final String path;
    private final UnwrappingEndpoints unwrappingEndpoints;
    private final boolean wrappedToken;
    private final boolean selfLookup;

    private CubbyholeAuthenticationOptions(VaultToken initialToken, String path, UnwrappingEndpoints unwrappingEndpoints, boolean wrappedToken, boolean selfLookup) {
        this.initialToken = initialToken;
        this.path = path;
        this.wrappedToken = wrappedToken;
        this.selfLookup = selfLookup;
        this.unwrappingEndpoints = unwrappingEndpoints;
    }

    public static CubbyholeAuthenticationOptionsBuilder builder() {
        return new CubbyholeAuthenticationOptionsBuilder();
    }

    public VaultToken getInitialToken() {
        return this.initialToken;
    }

    public String getPath() {
        return this.path;
    }

    public UnwrappingEndpoints getUnwrappingEndpoints() {
        return this.unwrappingEndpoints;
    }

    public boolean isWrappedToken() {
        return this.wrappedToken;
    }

    public boolean isSelfLookup() {
        return this.selfLookup;
    }

    public static class CubbyholeAuthenticationOptionsBuilder {
        @Nullable
        private VaultToken initialToken;
        @Nullable
        private String path;
        private UnwrappingEndpoints endpoints = UnwrappingEndpoints.SysWrapping;
        private boolean wrappedToken;
        private boolean selfLookup = true;

        CubbyholeAuthenticationOptionsBuilder() {
        }

        public CubbyholeAuthenticationOptionsBuilder initialToken(VaultToken initialToken) {
            Assert.notNull((Object)initialToken, "Initial Vault Token must not be null");
            this.initialToken = initialToken;
            return this;
        }

        public CubbyholeAuthenticationOptionsBuilder path(String path) {
            Assert.hasText(path, "Path must not be empty");
            this.path = path;
            return this;
        }

        public CubbyholeAuthenticationOptionsBuilder unwrappingEndpoints(UnwrappingEndpoints endpoints) {
            Assert.notNull((Object)endpoints, "UnwrappingEndpoints must not be empty");
            this.endpoints = endpoints;
            return this;
        }

        public CubbyholeAuthenticationOptionsBuilder wrapped() {
            this.path = "";
            this.wrappedToken = true;
            return this;
        }

        public CubbyholeAuthenticationOptionsBuilder selfLookup(boolean selfLookup) {
            this.selfLookup = selfLookup;
            return this;
        }

        public CubbyholeAuthenticationOptions build() {
            Assert.notNull((Object)this.initialToken, "Initial Vault Token must not be null");
            Assert.notNull((Object)this.path, "Path must not be null");
            return new CubbyholeAuthenticationOptions(this.initialToken, this.path, this.endpoints, this.wrappedToken, this.selfLookup);
        }
    }
}

