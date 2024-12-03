/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

class EncryptionKeyInfo {
    byte[] encryptedKey;
    int databaseId;
    int cekId;
    int cekVersion;
    byte[] cekMdVersion;
    String keyPath;
    String keyStoreName;
    String algorithmName;
    byte normalizationRuleVersion;

    EncryptionKeyInfo(byte[] encryptedKeyVal, int dbId, int keyId, int keyVersion, byte[] mdVersion, String keyPathVal, String keyStoreNameVal, String algorithmNameVal) {
        this.encryptedKey = encryptedKeyVal;
        this.databaseId = dbId;
        this.cekId = keyId;
        this.cekVersion = keyVersion;
        this.cekMdVersion = mdVersion;
        this.keyPath = keyPathVal;
        this.keyStoreName = keyStoreNameVal;
        this.algorithmName = algorithmNameVal;
    }
}

