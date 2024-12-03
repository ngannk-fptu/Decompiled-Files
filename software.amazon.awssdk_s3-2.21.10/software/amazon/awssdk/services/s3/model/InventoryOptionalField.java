/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.utils.internal.EnumUtils
 */
package software.amazon.awssdk.services.s3.model;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import software.amazon.awssdk.utils.internal.EnumUtils;

public enum InventoryOptionalField {
    SIZE("Size"),
    LAST_MODIFIED_DATE("LastModifiedDate"),
    STORAGE_CLASS("StorageClass"),
    E_TAG("ETag"),
    IS_MULTIPART_UPLOADED("IsMultipartUploaded"),
    REPLICATION_STATUS("ReplicationStatus"),
    ENCRYPTION_STATUS("EncryptionStatus"),
    OBJECT_LOCK_RETAIN_UNTIL_DATE("ObjectLockRetainUntilDate"),
    OBJECT_LOCK_MODE("ObjectLockMode"),
    OBJECT_LOCK_LEGAL_HOLD_STATUS("ObjectLockLegalHoldStatus"),
    INTELLIGENT_TIERING_ACCESS_TIER("IntelligentTieringAccessTier"),
    BUCKET_KEY_STATUS("BucketKeyStatus"),
    CHECKSUM_ALGORITHM("ChecksumAlgorithm"),
    OBJECT_ACCESS_CONTROL_LIST("ObjectAccessControlList"),
    OBJECT_OWNER("ObjectOwner"),
    UNKNOWN_TO_SDK_VERSION(null);

    private static final Map<String, InventoryOptionalField> VALUE_MAP;
    private final String value;

    private InventoryOptionalField(String value) {
        this.value = value;
    }

    public String toString() {
        return String.valueOf(this.value);
    }

    public static InventoryOptionalField fromValue(String value) {
        if (value == null) {
            return null;
        }
        return VALUE_MAP.getOrDefault(value, UNKNOWN_TO_SDK_VERSION);
    }

    public static Set<InventoryOptionalField> knownValues() {
        EnumSet<InventoryOptionalField> knownValues = EnumSet.allOf(InventoryOptionalField.class);
        knownValues.remove((Object)UNKNOWN_TO_SDK_VERSION);
        return knownValues;
    }

    static {
        VALUE_MAP = EnumUtils.uniqueIndex(InventoryOptionalField.class, InventoryOptionalField::toString);
    }
}

