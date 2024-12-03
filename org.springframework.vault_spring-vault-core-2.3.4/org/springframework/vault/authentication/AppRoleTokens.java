/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.authentication;

import org.springframework.vault.authentication.AppRoleAuthenticationOptions;
import org.springframework.vault.support.VaultToken;

class AppRoleTokens {
    AppRoleTokens() {
    }

    static class Provided
    implements AppRoleAuthenticationOptions.RoleId,
    AppRoleAuthenticationOptions.SecretId {
        final String value;

        Provided(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }

    static class Pull
    implements AppRoleAuthenticationOptions.RoleId,
    AppRoleAuthenticationOptions.SecretId {
        final VaultToken initialToken;

        Pull(VaultToken initialToken) {
            this.initialToken = initialToken;
        }

        public VaultToken getInitialToken() {
            return this.initialToken;
        }
    }

    static class Wrapped
    implements AppRoleAuthenticationOptions.RoleId,
    AppRoleAuthenticationOptions.SecretId {
        final VaultToken initialToken;

        Wrapped(VaultToken initialToken) {
            this.initialToken = initialToken;
        }

        public VaultToken getInitialToken() {
            return this.initialToken;
        }
    }

    static enum AbsentSecretId implements AppRoleAuthenticationOptions.SecretId
    {
        ABSENT_SECRET_ID;

    }
}

