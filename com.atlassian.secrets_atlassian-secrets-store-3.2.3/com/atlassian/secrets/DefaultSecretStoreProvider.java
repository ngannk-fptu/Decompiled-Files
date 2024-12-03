/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.db.config.password.Cipher
 *  com.atlassian.secrets.api.SecretStore
 *  com.atlassian.secrets.api.SecretStoreException
 *  com.atlassian.secrets.api.SecretStoreProvider
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.secrets;

import com.atlassian.db.config.password.Cipher;
import com.atlassian.secrets.DelegateCipherSecretStore;
import com.atlassian.secrets.api.SecretStore;
import com.atlassian.secrets.api.SecretStoreException;
import com.atlassian.secrets.api.SecretStoreProvider;
import com.atlassian.secrets.store.algorithm.AesOnlyAlgorithmSecretStore;
import com.atlassian.secrets.store.algorithm.AlgorithmSecretStore;
import com.atlassian.secrets.store.base64.Base64SecretStore;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultSecretStoreProvider
implements SecretStoreProvider {
    private static final Logger log = LoggerFactory.getLogger(DefaultSecretStoreProvider.class);

    public String getDefaultSecretStoreClassName() {
        return Base64SecretStore.class.getCanonicalName();
    }

    public Optional<SecretStore> getInstance(String className) {
        if (className == null || className.isEmpty()) {
            return Optional.empty();
        }
        String secretStoreClassName = DefaultSecretStoreProvider.convertOldCiphersToSecretStore(className);
        log.info("Initiating secret store class: {}", (Object)secretStoreClassName);
        try {
            Class<?> secretStoreClass = Class.forName(secretStoreClassName);
            SecretStore provider = Cipher.class.isAssignableFrom(secretStoreClass) ? new DelegateCipherSecretStore((Cipher)secretStoreClass.getDeclaredConstructor(new Class[0]).newInstance(new Object[0])) : (SecretStore)secretStoreClass.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
            log.info("Initiated secret store class: {}", (Object)provider.getClass().getSimpleName());
            return Optional.of(provider);
        }
        catch (Exception classInitException) {
            log.error("Couldn't initiate class: " + secretStoreClassName, (Throwable)classInitException);
            throw new SecretStoreException((Throwable)classInitException);
        }
    }

    private static String convertOldCiphersToSecretStore(String cipherClassName) {
        switch (cipherClassName) {
            case "com.atlassian.db.config.password.ciphers.base64.Base64Cipher": {
                return Base64SecretStore.class.getCanonicalName();
            }
            case "com.atlassian.db.config.password.ciphers.algorithm.AlgorithmCipher": {
                return AlgorithmSecretStore.class.getCanonicalName();
            }
            case "com.atlassian.db.config.password.ciphers.algorithm.AesOnlyAlgorithmCipher": {
                return AesOnlyAlgorithmSecretStore.class.getCanonicalName();
            }
        }
        return cipherClassName;
    }
}

