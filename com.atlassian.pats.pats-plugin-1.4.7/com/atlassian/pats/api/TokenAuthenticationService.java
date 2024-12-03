/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.pats.api;

import com.atlassian.pats.db.TokenDTO;
import javax.annotation.Nonnull;

public interface TokenAuthenticationService {
    @Nonnull
    public TokenDTO authenticate(@Nonnull String var1);

    public static class AuthenticationResult {
        private final String hashedToken;
        private final boolean authenticated;

        public AuthenticationResult(String hashedToken, boolean authenticated) {
            this.hashedToken = hashedToken;
            this.authenticated = authenticated;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof AuthenticationResult)) {
                return false;
            }
            AuthenticationResult other = (AuthenticationResult)o;
            if (!other.canEqual(this)) {
                return false;
            }
            if (this.isAuthenticated() != other.isAuthenticated()) {
                return false;
            }
            String this$hashedToken = this.getHashedToken();
            String other$hashedToken = other.getHashedToken();
            return !(this$hashedToken == null ? other$hashedToken != null : !this$hashedToken.equals(other$hashedToken));
        }

        protected boolean canEqual(Object other) {
            return other instanceof AuthenticationResult;
        }

        public int hashCode() {
            int PRIME = 59;
            int result = 1;
            result = result * 59 + (this.isAuthenticated() ? 79 : 97);
            String $hashedToken = this.getHashedToken();
            result = result * 59 + ($hashedToken == null ? 43 : $hashedToken.hashCode());
            return result;
        }

        public String toString() {
            return "TokenAuthenticationService.AuthenticationResult(hashedToken=" + this.getHashedToken() + ", authenticated=" + this.isAuthenticated() + ")";
        }

        public String getHashedToken() {
            return this.hashedToken;
        }

        public boolean isAuthenticated() {
            return this.authenticated;
        }
    }
}

