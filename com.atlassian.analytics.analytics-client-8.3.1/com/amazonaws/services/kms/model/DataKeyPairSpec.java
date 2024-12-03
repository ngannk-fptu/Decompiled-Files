/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

public enum DataKeyPairSpec {
    RSA_2048("RSA_2048"),
    RSA_3072("RSA_3072"),
    RSA_4096("RSA_4096"),
    ECC_NIST_P256("ECC_NIST_P256"),
    ECC_NIST_P384("ECC_NIST_P384"),
    ECC_NIST_P521("ECC_NIST_P521"),
    ECC_SECG_P256K1("ECC_SECG_P256K1"),
    SM2("SM2");

    private String value;

    private DataKeyPairSpec(String value) {
        this.value = value;
    }

    public String toString() {
        return this.value;
    }

    public static DataKeyPairSpec fromValue(String value) {
        if (value == null || "".equals(value)) {
            throw new IllegalArgumentException("Value cannot be null or empty!");
        }
        for (DataKeyPairSpec enumEntry : DataKeyPairSpec.values()) {
            if (!enumEntry.toString().equals(value)) continue;
            return enumEntry;
        }
        throw new IllegalArgumentException("Cannot create enum from " + value + " value!");
    }
}

