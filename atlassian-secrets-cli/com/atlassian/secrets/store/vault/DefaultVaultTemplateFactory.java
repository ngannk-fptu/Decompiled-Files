/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.secrets.store.vault;

import com.atlassian.secrets.store.vault.VaultTemplateFactory;
import java.net.URI;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.vault.authentication.ClientAuthentication;
import org.springframework.vault.authentication.SessionManager;
import org.springframework.vault.authentication.SimpleSessionManager;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.core.VaultTemplate;

public class DefaultVaultTemplateFactory
implements VaultTemplateFactory {
    public static final int DEFAULT_CONNECTION_TIMEOUT_MILLIS = 30000;
    public static final int DEFAULT_READ_TIMEOUT_MILLIS = 30000;
    public static final String VAULT_CONNECTION_TIMEOUT_MILLIS_SYSTEM_PROPERTY = "secret.store.vault.connectionTimeoutMs";
    public static final String VAULT_READ_TIMEOUT_MILLIS_SYSTEM_PROPERTY = "secret.store.vault.readTimeoutMs";

    @Override
    public VaultTemplate getTemplate(URI endpoint, ClientAuthentication authentication) {
        VaultEndpoint vaultEndpoint = VaultEndpoint.from(endpoint);
        SimpleSessionManager sessionManager = new SimpleSessionManager(authentication);
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(this.getConnectionTimeout());
        requestFactory.setReadTimeout(this.getReadTimeout());
        return new VaultTemplate(vaultEndpoint, (ClientHttpRequestFactory)requestFactory, (SessionManager)sessionManager);
    }

    private int getConnectionTimeout() {
        return Integer.parseInt(System.getProperty(VAULT_CONNECTION_TIMEOUT_MILLIS_SYSTEM_PROPERTY, String.valueOf(30000)));
    }

    private int getReadTimeout() {
        return Integer.parseInt(System.getProperty(VAULT_READ_TIMEOUT_MILLIS_SYSTEM_PROPERTY, String.valueOf(30000)));
    }
}

