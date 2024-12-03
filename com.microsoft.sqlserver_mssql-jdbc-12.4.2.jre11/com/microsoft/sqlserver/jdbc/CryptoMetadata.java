/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.CekTableEntry;
import com.microsoft.sqlserver.jdbc.EncryptionKeyInfo;
import com.microsoft.sqlserver.jdbc.SQLServerEncryptionAlgorithm;
import com.microsoft.sqlserver.jdbc.SQLServerEncryptionType;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.TypeInfo;

class CryptoMetadata {
    TypeInfo baseTypeInfo;
    CekTableEntry cekTableEntry;
    byte cipherAlgorithmId;
    String cipherAlgorithmName;
    SQLServerEncryptionType encryptionType;
    byte normalizationRuleVersion;
    SQLServerEncryptionAlgorithm cipherAlgorithm = null;
    EncryptionKeyInfo encryptionKeyInfo;
    short ordinal;

    CekTableEntry getCekTableEntry() {
        return this.cekTableEntry;
    }

    void setCekTableEntry(CekTableEntry cekTableEntryObj) {
        this.cekTableEntry = cekTableEntryObj;
    }

    TypeInfo getBaseTypeInfo() {
        return this.baseTypeInfo;
    }

    void setBaseTypeInfo(TypeInfo baseTypeInfoObj) {
        this.baseTypeInfo = baseTypeInfoObj;
    }

    SQLServerEncryptionAlgorithm getEncryptionAlgorithm() {
        return this.cipherAlgorithm;
    }

    void setEncryptionAlgorithm(SQLServerEncryptionAlgorithm encryptionAlgorithmObj) {
        this.cipherAlgorithm = encryptionAlgorithmObj;
    }

    EncryptionKeyInfo getEncryptionKeyInfo() {
        return this.encryptionKeyInfo;
    }

    void setEncryptionKeyInfo(EncryptionKeyInfo encryptionKeyInfoObj) {
        this.encryptionKeyInfo = encryptionKeyInfoObj;
    }

    byte getEncryptionAlgorithmId() {
        return this.cipherAlgorithmId;
    }

    String getEncryptionAlgorithmName() {
        return this.cipherAlgorithmName;
    }

    SQLServerEncryptionType getEncryptionType() {
        return this.encryptionType;
    }

    byte getNormalizationRuleVersion() {
        return this.normalizationRuleVersion;
    }

    short getOrdinal() {
        return this.ordinal;
    }

    CryptoMetadata(CekTableEntry cekTableEntryObj, short ordinalVal, byte cipherAlgorithmIdVal, String cipherAlgorithmNameVal, byte encryptionTypeVal, byte normalizationRuleVersionVal) throws SQLServerException {
        this.cekTableEntry = cekTableEntryObj;
        this.ordinal = ordinalVal;
        this.cipherAlgorithmId = cipherAlgorithmIdVal;
        this.cipherAlgorithmName = cipherAlgorithmNameVal;
        this.encryptionType = SQLServerEncryptionType.of(encryptionTypeVal);
        this.normalizationRuleVersion = normalizationRuleVersionVal;
        this.encryptionKeyInfo = null;
    }

    boolean isAlgorithmInitialized() {
        return null != this.cipherAlgorithm;
    }
}

