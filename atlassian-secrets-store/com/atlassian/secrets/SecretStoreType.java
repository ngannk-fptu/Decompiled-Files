/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.secrets;

public enum SecretStoreType {
    BASE64,
    ALGORITHM,
    AES_ONLY_ALGORITHM,
    AWS,
    VAULT,
    NONE,
    CUSTOM;


    public static SecretStoreType of(String className) {
        if (className == null || className.isEmpty()) {
            return NONE;
        }
        switch (className) {
            case "com.atlassian.secrets.store.base64.Base64SecretStore": 
            case "com.atlassian.db.config.password.ciphers.base64.Base64Cipher": {
                return BASE64;
            }
            case "com.atlassian.secrets.store.algorithm.AesOnlyAlgorithmSecretStore": 
            case "com.atlassian.db.config.password.ciphers.algorithm.AesOnlyAlgorithmCipher": {
                return AES_ONLY_ALGORITHM;
            }
            case "com.atlassian.secrets.store.algorithm.AlgorithmSecretStore": 
            case "com.atlassian.db.config.password.ciphers.algorithm.AlgorithmCipher": {
                return ALGORITHM;
            }
            case "com.atlassian.secrets.store.aws.AwsSecretsManagerStore": {
                return AWS;
            }
            case "com.atlassian.secrets.store.vault.VaultSecretStore": {
                return VAULT;
            }
        }
        return CUSTOM;
    }
}

