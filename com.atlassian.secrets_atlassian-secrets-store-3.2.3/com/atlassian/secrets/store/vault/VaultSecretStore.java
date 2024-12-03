/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.secrets.api.SecretStore
 *  com.atlassian.secrets.api.SecretStoreException
 *  com.fasterxml.jackson.core.JacksonException
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.vault.core.VaultTemplate
 *  org.springframework.vault.support.Versioned
 */
package com.atlassian.secrets.store.vault;

import com.atlassian.secrets.api.SecretStore;
import com.atlassian.secrets.api.SecretStoreException;
import com.atlassian.secrets.store.vault.DefaultVaultTemplateFactory;
import com.atlassian.secrets.store.vault.VaultParams;
import com.atlassian.secrets.store.vault.VaultTemplateFactory;
import com.atlassian.secrets.store.vault.auth.DefaultVaultAuthenticationProvider;
import com.atlassian.secrets.store.vault.auth.VaultAuthenticationProvider;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.Versioned;

public class VaultSecretStore
implements SecretStore {
    private static final Logger log = LoggerFactory.getLogger(VaultSecretStore.class);
    private final VaultTemplateFactory templateFactory;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final VaultAuthenticationProvider vaultAuthenticationProvider;

    public VaultSecretStore(VaultTemplateFactory templateFactory, VaultAuthenticationProvider vaultAuthenticationProvider) {
        this.templateFactory = templateFactory;
        this.vaultAuthenticationProvider = vaultAuthenticationProvider;
    }

    public VaultSecretStore() {
        this(new DefaultVaultTemplateFactory(), new DefaultVaultAuthenticationProvider());
    }

    public String get(String jsonParams) {
        try {
            VaultParams vaultParams = (VaultParams)this.objectMapper.readValue(jsonParams, VaultParams.class);
            VaultTemplate vaultTemplate = this.templateFactory.getTemplate(URI.create(vaultParams.getEndpoint()), this.vaultAuthenticationProvider.getAuthentication(vaultParams));
            Versioned readResponse = vaultTemplate.opsForVersionedKeyValue(vaultParams.getMount()).get(vaultParams.getPath());
            log.debug("Retrieved Vault secret: {}", (Object)vaultParams);
            return this.getSecretValueFromReadResponse((Versioned<Map<String, Object>>)readResponse, vaultParams.getKey());
        }
        catch (JacksonException e) {
            log.error("Problem when reading secret store configuration. Please review the JSON configuration string.");
            throw new SecretStoreException("Problem when reading secret store configuration. Please review the JSON configuration string.");
        }
        catch (Exception e) {
            log.error("Problem when getting the secret value: {}", (Object)e.getMessage());
            throw new SecretStoreException("Problem when getting the secret value", (Throwable)e);
        }
    }

    public String store(String plainTextData) {
        throw new UnsupportedOperationException("Encryption is currently not supported for HashiCorp Vault");
    }

    private String getSecretValueFromReadResponse(Versioned<Map<String, Object>> readResponse, String key) {
        if (readResponse == null) {
            throw new SecretStoreException("Path to Secret value does not exist.");
        }
        Map data = (Map)readResponse.getData();
        if (data == null) {
            throw new SecretStoreException("Secret value not available. It may be deleted/destroyed.");
        }
        if (data.containsKey(key)) {
            return (String)data.get(key);
        }
        throw new SecretStoreException(String.format("Cannot find secret value for key: %s", key));
    }
}

