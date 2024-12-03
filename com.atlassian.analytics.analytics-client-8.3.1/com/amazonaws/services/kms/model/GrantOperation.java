/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

public enum GrantOperation {
    Decrypt("Decrypt"),
    Encrypt("Encrypt"),
    GenerateDataKey("GenerateDataKey"),
    GenerateDataKeyWithoutPlaintext("GenerateDataKeyWithoutPlaintext"),
    ReEncryptFrom("ReEncryptFrom"),
    ReEncryptTo("ReEncryptTo"),
    Sign("Sign"),
    Verify("Verify"),
    GetPublicKey("GetPublicKey"),
    CreateGrant("CreateGrant"),
    RetireGrant("RetireGrant"),
    DescribeKey("DescribeKey"),
    GenerateDataKeyPair("GenerateDataKeyPair"),
    GenerateDataKeyPairWithoutPlaintext("GenerateDataKeyPairWithoutPlaintext"),
    GenerateMac("GenerateMac"),
    VerifyMac("VerifyMac");

    private String value;

    private GrantOperation(String value) {
        this.value = value;
    }

    public String toString() {
        return this.value;
    }

    public static GrantOperation fromValue(String value) {
        if (value == null || "".equals(value)) {
            throw new IllegalArgumentException("Value cannot be null or empty!");
        }
        for (GrantOperation enumEntry : GrantOperation.values()) {
            if (!enumEntry.toString().equals(value)) continue;
            return enumEntry;
        }
        throw new IllegalArgumentException("Cannot create enum from " + value + " value!");
    }
}

