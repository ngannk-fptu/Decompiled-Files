/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model.inventory;

public enum InventoryOptionalField {
    Size("Size"),
    LastModifiedDate("LastModifiedDate"),
    StorageClass("StorageClass"),
    ETag("ETag"),
    IsMultipartUploaded("IsMultipartUploaded"),
    ReplicationStatus("ReplicationStatus"),
    InventoryOptionalField("InventoryOptionalField"),
    EncryptionStatus("EncryptionStatus"),
    ObjectLockRetainUntilDate("ObjectLockRetainUntilDate"),
    ObjectLockMode("ObjectLockMode"),
    ObjectLockLegalHoldStatus("ObjectLockLegalHoldStatus"),
    IntelligentTieringAccessTier("IntelligentTieringAccessTier"),
    BucketKeyStatus("BucketKeyStatus");

    private final String field;

    private InventoryOptionalField(String field) {
        this.field = field;
    }

    public String toString() {
        return this.field;
    }

    public static InventoryOptionalField fromValue(String value) {
        if (value == null || "".equals(value)) {
            throw new IllegalArgumentException("Value cannot be null or empty!");
        }
        for (InventoryOptionalField enumEntry : InventoryOptionalField.values()) {
            if (!enumEntry.toString().equals(value)) continue;
            return enumEntry;
        }
        throw new IllegalArgumentException("Cannot create enum from " + value + " value!");
    }
}

