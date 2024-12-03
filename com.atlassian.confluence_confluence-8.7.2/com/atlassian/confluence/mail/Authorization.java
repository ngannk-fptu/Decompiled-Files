/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.mail;

import java.io.Serializable;
import java.util.Objects;

public interface Authorization
extends Serializable {
    public String getTokenId();

    public String getProviderId();

    public static class OAuth2
    implements Authorization {
        private static final long serialVersionUID = 4186305479288081868L;
        private final String providerId;
        private final String tokenId;

        public OAuth2(String providerId, String tokenId) {
            this.providerId = Objects.requireNonNull(providerId);
            this.tokenId = Objects.requireNonNull(tokenId);
        }

        @Override
        public String getProviderId() {
            return this.providerId;
        }

        @Override
        public String getTokenId() {
            return this.tokenId;
        }
    }
}

