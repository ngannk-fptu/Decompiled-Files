/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.secrets.store.vault.auth.token;

import com.atlassian.secrets.store.vault.auth.AuthenticationConfigUtils;
import org.springframework.vault.authentication.TokenAuthentication;

public class TokenAuthenticationFactory {
    public static final String VAULT_TOKEN_ENV_KEY = "SECRET_STORE_VAULT_TOKEN";
    public static final String VAULT_TOKEN_SYSTEM_PROP_KEY = "secret.store.vault.token";

    public TokenAuthentication getAuthentication() {
        return new TokenAuthentication(AuthenticationConfigUtils.parseRequiredValueFromEnv(VAULT_TOKEN_ENV_KEY, VAULT_TOKEN_SYSTEM_PROP_KEY));
    }
}

