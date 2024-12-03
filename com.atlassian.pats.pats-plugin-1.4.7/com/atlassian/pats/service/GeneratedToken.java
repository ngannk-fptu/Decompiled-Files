/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.pats.service;

public class GeneratedToken {
    final String tokenId;
    final String hashedToken;
    final String rawToken;

    GeneratedToken(String tokenId, String hashedToken, String rawToken) {
        this.tokenId = tokenId;
        this.hashedToken = hashedToken;
        this.rawToken = rawToken;
    }

    public static GeneratedTokenBuilder builder() {
        return new GeneratedTokenBuilder();
    }

    public String getTokenId() {
        return this.tokenId;
    }

    public String getHashedToken() {
        return this.hashedToken;
    }

    public String getRawToken() {
        return this.rawToken;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof GeneratedToken)) {
            return false;
        }
        GeneratedToken other = (GeneratedToken)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$tokenId = this.getTokenId();
        String other$tokenId = other.getTokenId();
        if (this$tokenId == null ? other$tokenId != null : !this$tokenId.equals(other$tokenId)) {
            return false;
        }
        String this$hashedToken = this.getHashedToken();
        String other$hashedToken = other.getHashedToken();
        if (this$hashedToken == null ? other$hashedToken != null : !this$hashedToken.equals(other$hashedToken)) {
            return false;
        }
        String this$rawToken = this.getRawToken();
        String other$rawToken = other.getRawToken();
        return !(this$rawToken == null ? other$rawToken != null : !this$rawToken.equals(other$rawToken));
    }

    protected boolean canEqual(Object other) {
        return other instanceof GeneratedToken;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $tokenId = this.getTokenId();
        result = result * 59 + ($tokenId == null ? 43 : $tokenId.hashCode());
        String $hashedToken = this.getHashedToken();
        result = result * 59 + ($hashedToken == null ? 43 : $hashedToken.hashCode());
        String $rawToken = this.getRawToken();
        result = result * 59 + ($rawToken == null ? 43 : $rawToken.hashCode());
        return result;
    }

    public String toString() {
        return "GeneratedToken(tokenId=" + this.getTokenId() + ", hashedToken=" + this.getHashedToken() + ", rawToken=" + this.getRawToken() + ")";
    }

    public static class GeneratedTokenBuilder {
        private String tokenId;
        private String hashedToken;
        private String rawToken;

        GeneratedTokenBuilder() {
        }

        public GeneratedTokenBuilder tokenId(String tokenId) {
            this.tokenId = tokenId;
            return this;
        }

        public GeneratedTokenBuilder hashedToken(String hashedToken) {
            this.hashedToken = hashedToken;
            return this;
        }

        public GeneratedTokenBuilder rawToken(String rawToken) {
            this.rawToken = rawToken;
            return this;
        }

        public GeneratedToken build() {
            return new GeneratedToken(this.tokenId, this.hashedToken, this.rawToken);
        }

        public String toString() {
            return "GeneratedToken.GeneratedTokenBuilder(tokenId=" + this.tokenId + ", hashedToken=" + this.hashedToken + ", rawToken=" + this.rawToken + ")";
        }
    }
}

