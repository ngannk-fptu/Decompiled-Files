/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.secrets.store.vault.auth;

import com.atlassian.secrets.store.vault.VaultParams;
import com.atlassian.secrets.store.vault.auth.VaultAuthenticationProvider;
import com.atlassian.secrets.store.vault.auth.kubernetes.KubernetesAuthenticationFactory;
import com.atlassian.secrets.store.vault.auth.token.TokenAuthenticationFactory;
import org.springframework.vault.authentication.ClientAuthentication;

public class DefaultVaultAuthenticationProvider
implements VaultAuthenticationProvider {
    private final KubernetesAuthenticationFactory kubernetesAuthenticationFactory;
    private final TokenAuthenticationFactory tokenAuthenticationFactory;

    public DefaultVaultAuthenticationProvider(KubernetesAuthenticationFactory kubernetesAuthenticationFactory, TokenAuthenticationFactory tokenAuthenticationFactory) {
        this.kubernetesAuthenticationFactory = kubernetesAuthenticationFactory;
        this.tokenAuthenticationFactory = tokenAuthenticationFactory;
    }

    public DefaultVaultAuthenticationProvider() {
        this(new KubernetesAuthenticationFactory(), new TokenAuthenticationFactory());
    }

    @Override
    public ClientAuthentication getAuthentication(VaultParams params) {
        switch (params.getAuthenticationType()) {
            case KUBERNETES: {
                return this.kubernetesAuthenticationFactory.getAuthentication(params);
            }
        }
        return this.tokenAuthenticationFactory.getAuthentication();
    }
}

