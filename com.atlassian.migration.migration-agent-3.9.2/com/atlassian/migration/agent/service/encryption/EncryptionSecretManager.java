/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.service.encryption;

import com.atlassian.migration.agent.config.MigrationAgentConfiguration;
import com.atlassian.migration.agent.service.encryption.EncryptionConfigHandler;
import com.atlassian.migration.agent.service.encryption.SecretStoreLocationType;
import com.atlassian.migration.agent.service.encryption.exception.EncryptionException;
import java.io.IOException;
import java.util.Base64;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionSecretManager {
    private final MigrationAgentConfiguration configuration;
    private final EncryptionConfigHandler encryptionConfigHandler;
    private final String encryptionAlgorithm = "AES";

    public EncryptionSecretManager(MigrationAgentConfiguration migrationAgentConfiguration, EncryptionConfigHandler configSecretGeneratorRetriever) {
        this.configuration = migrationAgentConfiguration;
        this.encryptionConfigHandler = configSecretGeneratorRetriever;
    }

    public SecretKey getEncryptionSecret() throws EncryptionException {
        byte[] secret;
        byte[] byArray = secret = this.encryptionConfigHandler.doesStorageTypeExist() ? this.getEncryptionSecretWhenLocationTypeIsConfigured() : this.getEncryptionSecretWhenLocationTypeIsNotConfigured();
        if (secret.length == 0) {
            throw new EncryptionException("Secret Length is Zero", EncryptionException.EncryptionErrorCode.SECRET_KEY_NOT_FOUND);
        }
        return new SecretKeySpec(secret, "AES");
    }

    private byte[] getEncryptionSecretWhenLocationTypeIsConfigured() throws EncryptionException {
        return this.getEncryptionSecretByStorage(this.encryptionConfigHandler.getKeyLocationFromConfig());
    }

    private byte[] getEncryptionSecretWhenLocationTypeIsNotConfigured() throws EncryptionException {
        this.encryptionConfigHandler.setKeyLocationInConfig(SecretStoreLocationType.CONFIG);
        return this.getEncryptionSecretByStorage(SecretStoreLocationType.CONFIG);
    }

    private byte[] getEncryptionSecretByStorage(SecretStoreLocationType secretStoreLocationType) throws EncryptionException {
        try {
            switch (secretStoreLocationType) {
                case CONFIG: {
                    return this.encryptionConfigHandler.getKeyFromConfig();
                }
                case ENV: {
                    if (this.isEncryptionKeyFromEnvEmpty()) {
                        throw new EncryptionException("Encryption Key is Missing in Environment Variable", EncryptionException.EncryptionErrorCode.SECRET_KEY_NOT_FOUND);
                    }
                    return Base64.getDecoder().decode(this.configuration.getEncryptionKeyFromEnv());
                }
            }
            throw new EncryptionException("Unsupported Storage Type");
        }
        catch (IllegalArgumentException e) {
            throw new EncryptionException(e.getMessage(), EncryptionException.EncryptionErrorCode.INVALID_SECRET_KEY);
        }
        catch (IOException e) {
            throw new EncryptionException(e.getMessage());
        }
    }

    private boolean isEncryptionKeyFromEnvEmpty() {
        return this.configuration.getEncryptionKeyFromEnv() == null || this.configuration.getEncryptionKeyFromEnv().trim().isEmpty();
    }
}

